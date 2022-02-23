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
import uk.gov.justice.laa.crime.meansassessment.exception.MAATCourtDataException;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

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
    private final MaatApiConfiguration config;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final CreateInitialAssessmentBuilder createInitialAssessmentBuilder;
    private final AssessmentCriteriaChildWeightingService assessmentCriteriaChildWeightingService;

    public ApiCreateMeansAssessmentResponse createInitialAssessment(ApiCreateMeansAssessmentRequest meansAssessment) {
        log.info("Create initial means assessment - Start");
        List<ApiAssessmentSectionSummary> sectionSummaries = meansAssessment.getSectionSummaries();

        AssessmentCriteriaEntity assessmentCriteria =
                assessmentCriteriaService.getAssessmentCriteria(meansAssessment.getAssessmentDate(), meansAssessment.getHasPartner(), meansAssessment.getPartnerContraryInterest());

        BigDecimal applicantAnnualTotal, partnerAnnualTotal, annualTotal;
        applicantAnnualTotal = partnerAnnualTotal = BigDecimal.ZERO;

        for (ApiAssessmentSectionSummary sectionSummary : sectionSummaries) {
            List<ApiAssessmentDetail> assessmentDetails = sectionSummary.getAssessmentDetails();
            for (ApiAssessmentDetail assessmentDetail : assessmentDetails) {
                assessmentCriteriaService.checkAssessmentDetail(meansAssessment.getCaseType(), sectionSummary.getSection(), assessmentCriteria, assessmentDetail);
                int applicantFrequencyWeighting = 0;
                if (!BigDecimal.ZERO.equals(assessmentDetail.getApplicantAmount())) {
                    applicantFrequencyWeighting = (assessmentDetail.getApplicantFrequency()).getWeighting();
                }

                int partnerFrequencyWeighting = 0;
                if (BigDecimal.ZERO.equals(assessmentDetail.getPartnerAmount()) || !BigDecimal.ZERO.equals(assessmentCriteria.getPartnerWeightingFactor())) {
                    partnerFrequencyWeighting = assessmentDetail.getPartnerFrequency().getWeighting();
                }

                BigDecimal applicantAmount =
                        (assessmentDetail.getApplicantAmount() != null) ? assessmentDetail.getApplicantAmount() : BigDecimal.ZERO;
                BigDecimal detailApplicantTotal =
                        (BigDecimal.ZERO.compareTo(applicantAmount) < 0) ? applicantAmount : BigDecimal.ZERO;
                applicantAnnualTotal = applicantAnnualTotal.add(detailApplicantTotal.multiply(BigDecimal.valueOf(applicantFrequencyWeighting)));

                BigDecimal partnerAmount =
                        (assessmentDetail.getPartnerAmount() != null) ? assessmentDetail.getPartnerAmount() : BigDecimal.ZERO;
                BigDecimal detailPartnerTotal =
                        (BigDecimal.ZERO.compareTo(partnerAmount) < 0) ? partnerAmount : BigDecimal.ZERO;
                partnerAnnualTotal = partnerAnnualTotal.add(detailPartnerTotal.multiply(BigDecimal.valueOf(partnerFrequencyWeighting)));

            }
        }
        annualTotal = applicantAnnualTotal.add(partnerAnnualTotal);
        BigDecimal totalChildWeighting =
                assessmentCriteriaChildWeightingService.getTotalChildWeighting(meansAssessment.getChildWeightings(), assessmentCriteria);

        BigDecimal adjustedIncomeValue = annualTotal
                .divide(assessmentCriteria.getApplicantWeightingFactor()
                                .add(assessmentCriteria.getPartnerWeightingFactor())
                                .add(totalChildWeighting),
                        RoundingMode.DOWN);

        BigDecimal initialLowerThreshold = assessmentCriteria.getInitialLowerThreshold();
        BigDecimal initialUpperThreshold = assessmentCriteria.getInitialUpperThreshold();

        String resultReason = null;
        String result = "IN PROGRESS";
        String status = meansAssessment.getAssessmentStatus().getStatus();
        if (status.equals("COMPLETE")) {
            if (adjustedIncomeValue.compareTo(initialLowerThreshold) <= 0) {
                result = "PASS";
                resultReason = "Gross income below the lower threshold";
            } else if (adjustedIncomeValue.compareTo(initialUpperThreshold) >= 0) {
                // TODO: Comment in PL/SQL suggests this should also apply to crown court cases
                if (meansAssessment.getNewWorkReason().getCode().equals("HR")) {
                    result = "HARDSHIP APPLICATION";
                } else {
                    result = "FAIL";
                    resultReason = "Gross income above the upper threshold";
                }
            } else {
                result = "FULL";
                resultReason = "Gross income in between the upper and lower thresholds";
            }
        }
        log.info("Initial means assessment calculation complete for Rep ID: {}", meansAssessment.getRepId());

        ApiCreateAssessment assessment = createInitialAssessmentBuilder.build(
                new InitialMeansAssessmentDTO(result, status, resultReason, annualTotal, adjustedIncomeValue, assessmentCriteria, meansAssessment, sectionSummaries));
        return persistAssessment(assessment, meansAssessment.getLaaTransactionId());
    }



    public ApiCreateMeansAssessmentResponse persistAssessment(ApiCreateAssessment createAssessment, String laaTransactionId) {
        ApiCreateMeansAssessmentResponse response = webClient.post()
                .uri(config.getFinancialAssessmentUrl())
                .headers(httpHeaders -> httpHeaders.setAll(Map.of(
                        "Laa-Transaction-Id", laaTransactionId
                )))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createAssessment))
                .retrieve()
                .bodyToMono(ApiCreateMeansAssessmentResponse.class)
                .onErrorMap(throwable -> new MAATCourtDataException("Call to Court Data API failed, invalid response."))
                .doOnError(Sentry::captureException)
                .block();

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }
}
