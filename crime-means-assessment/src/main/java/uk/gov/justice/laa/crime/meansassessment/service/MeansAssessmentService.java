package uk.gov.justice.laa.crime.meansassessment.service;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.builder.CreateInitialAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.InitialMeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    @Qualifier("maatAPIOAuth2WebClient")
    private final WebClient webClient;
    private final MaatApiConfiguration configuration;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final CreateInitialAssessmentBuilder createInitialAssessmentBuilder;
    private final AssessmentCriteriaChildWeightingService childWeightingService;

    public ApiCreateMeansAssessmentResponse createInitialAssessment(ApiCreateMeansAssessmentRequest meansAssessment) {
        log.info("Create initial means assessment - Start");
        List<ApiAssessmentSectionSummary> sectionSummaries = meansAssessment.getSectionSummaries();
        AssessmentCriteriaEntity assessmentCriteria =
                assessmentCriteriaService.getAssessmentCriteria(
                        meansAssessment.getAssessmentDate(), meansAssessment.getHasPartner(), meansAssessment.getPartnerContraryInterest()
                );

        BigDecimal annualTotal = getAnnualTotal(meansAssessment.getCaseType(), assessmentCriteria, sectionSummaries);
        BigDecimal adjustedIncomeValue = getAdjustedIncome(meansAssessment, assessmentCriteria, annualTotal);

        BigDecimal initialLowerThreshold = assessmentCriteria.getInitialLowerThreshold();
        BigDecimal initialUpperThreshold = assessmentCriteria.getInitialUpperThreshold();

        CurrentStatus status = meansAssessment.getAssessmentStatus();
        String newWorkReasonCode = meansAssessment.getNewWorkReason().getCode();
        MeansAssessmentResultDTO result = getAssessmentResult(
                status, adjustedIncomeValue, initialUpperThreshold, initialLowerThreshold, newWorkReasonCode
        );

        log.info("Initial means assessment calculation complete for Rep ID: {}", meansAssessment.getRepId());

        ApiCreateAssessment assessment = createInitialAssessmentBuilder.build(
                new InitialMeansAssessmentDTO(annualTotal, status, adjustedIncomeValue, result, assessmentCriteria, meansAssessment, sectionSummaries));
        return persistAssessment(assessment, meansAssessment.getLaaTransactionId());
    }

    protected BigDecimal getAdjustedIncome(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria, BigDecimal annualTotal) {
        BigDecimal totalChildWeighting =
                childWeightingService.getTotalChildWeighting(meansAssessment.getChildWeightings(), assessmentCriteria);

        if (BigDecimal.ZERO.compareTo(annualTotal) <= 0) {
            return annualTotal
                    .divide(assessmentCriteria.getApplicantWeightingFactor()
                                    .add(assessmentCriteria.getPartnerWeightingFactor())
                                    .add(totalChildWeighting),
                            RoundingMode.UP);
        }
        return BigDecimal.ZERO;
    }

    protected MeansAssessmentResultDTO getAssessmentResult(CurrentStatus status, BigDecimal adjustedIncomeValue, BigDecimal upperThreshold, BigDecimal lowerThreshold, String newWorkReasonCode) {
        MeansAssessmentResultDTO resultDTO = new MeansAssessmentResultDTO();
        if (status.equals(CurrentStatus.COMPLETE)) {
            if (adjustedIncomeValue.compareTo(lowerThreshold) <= 0) {
                resultDTO.setResult("PASS");
                resultDTO.setReason("Gross income below the lower threshold");
            } else if (adjustedIncomeValue.compareTo(upperThreshold) >= 0) {
                // TODO: Comment in PL/SQL suggests this should also apply to crown court cases
                if (newWorkReasonCode.equals("HR")) {
                    resultDTO.setResult("HARDSHIP APPLICATION");
                } else {
                    resultDTO.setResult("FAIL");
                    resultDTO.setReason("Gross income above the upper threshold");
                }
            } else {
                resultDTO.setResult("FULL");
                resultDTO.setReason("Gross income in between the upper and lower thresholds");
            }
        }
        return resultDTO;
    }

    protected BigDecimal getAnnualTotal(CaseType caseType, AssessmentCriteriaEntity assessmentCriteria, List<ApiAssessmentSectionSummary> sectionSummaries) {
        BigDecimal applicantAnnualTotal, partnerAnnualTotal;
        applicantAnnualTotal = partnerAnnualTotal = BigDecimal.ZERO;
        for (ApiAssessmentSectionSummary sectionSummary : sectionSummaries) {
            for (ApiAssessmentDetail assessmentDetail : sectionSummary.getAssessmentDetails()) {
                assessmentCriteriaService.checkAssessmentDetail(caseType, sectionSummary.getSection(), assessmentCriteria, assessmentDetail);

                applicantAnnualTotal = applicantAnnualTotal.add(
                        getDetailTotal(assessmentDetail)
                );

                partnerAnnualTotal = partnerAnnualTotal.add(
                        getDetailTotal(assessmentDetail, true)
                );
            }
        }
        return applicantAnnualTotal.add(partnerAnnualTotal);
    }

    protected BigDecimal getDetailTotal(ApiAssessmentDetail assessmentDetail) {
        return getDetailTotal(assessmentDetail, false);
    }

    protected BigDecimal getDetailTotal(ApiAssessmentDetail assessmentDetail, boolean usePartner) {
        BigDecimal detailAmount;
        BigDecimal detailTotal = BigDecimal.ZERO;
        if (usePartner) {
            if (assessmentDetail.getPartnerAmount() != null) {
                detailAmount = assessmentDetail.getPartnerAmount();
                detailTotal = detailAmount.multiply(
                        BigDecimal.valueOf(assessmentDetail.getPartnerFrequency().getWeighting())
                );
            }
        } else {
            if (assessmentDetail.getApplicantAmount() != null) {
                detailAmount = assessmentDetail.getApplicantAmount();
                detailTotal = detailAmount.multiply(
                        BigDecimal.valueOf(assessmentDetail.getApplicantFrequency().getWeighting())
                );
            }
        }
        return detailTotal;
    }

    public ApiCreateMeansAssessmentResponse persistAssessment(ApiCreateAssessment createAssessment, String laaTransactionId) {
        ApiCreateMeansAssessmentResponse response = webClient.post()
                .uri(configuration.getFinancialAssessmentEndpoints().getCreateUrl())
                .headers(httpHeaders -> httpHeaders.setAll(Map.of(
                        "Laa-Transaction-Id", laaTransactionId
                )))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createAssessment))
                .retrieve()
                .bodyToMono(ApiCreateMeansAssessmentResponse.class)
                .onErrorMap(throwable -> new APIClientException("Call to Court Data API failed, invalid response."))
                .doOnError(Sentry::captureException)
                .block();

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }
}
