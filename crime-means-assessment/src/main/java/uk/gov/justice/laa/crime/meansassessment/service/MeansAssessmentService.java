package uk.gov.justice.laa.crime.meansassessment.service;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.builder.maatapi.MaatAPIAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    @Qualifier("maatAPIOAuth2WebClient")
    private final WebClient webClient;
    private final MaatApiConfiguration configuration;
    private final MaatAPIAssessmentBuilder assessmentBuilder;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final FullMeansAssessmentService fullMeansAssessmentService;
    private final InitMeansAssessmentService initMeansAssessmentService;

    public ApiCreateMeansAssessmentResponse doAssessment(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentRequestType requestType) {
        AssessmentCriteriaEntity assessmentCriteria =
                assessmentCriteriaService.getAssessmentCriteria(
                        meansAssessment.getAssessmentDate(), meansAssessment.getHasPartner(), meansAssessment.getPartnerContraryInterest()
                );
        BigDecimal summariesTotal = calculateSummariesTotal(meansAssessment, assessmentCriteria);
        AssessmentService assessmentService =
                meansAssessment.getAssessmentType().equals(AssessmentType.INIT) ? initMeansAssessmentService : fullMeansAssessmentService;

        MeansAssessmentDTO assessment = assessmentService.execute(summariesTotal, meansAssessment, assessmentCriteria);

        assessment.setMeansAssessment(meansAssessment);
        assessment.setAssessmentCriteria(assessmentCriteria);

        String targetEndpoint = configuration.getFinancialAssessmentEndpoints()
                .getByRequestType(requestType);

        MaatApiAssessmentResponse response =
                persistAssessment(assessmentBuilder.build(assessment, requestType), meansAssessment.getLaaTransactionId(), targetEndpoint);

        return new ApiCreateMeansAssessmentResponse()
                .withAssessmentId(response.getId())
                .withCriteriaId(assessmentCriteria.getId())
                .withLowerThreshold(assessmentCriteria.getInitialLowerThreshold())
                .withUpperThreshold(assessmentCriteria.getInitialUpperThreshold())
                .withTotalAggregatedIncome(response.getInitTotAggregatedIncome())
                .withResult(response.getInitResult())
                .withResultReason(response.getInitResultReason())
                .withAdjustedIncomeValue(response.getInitAdjustedIncomeValue())
                .withAssessmentStatus(new ApiAssessmentStatus()
                        .withStatus(response.getFassInitStatus())
                )
                .withAssessmentSummary(assessment.getMeansAssessment().getSectionSummaries());
    }

    protected BigDecimal calculateSummariesTotal(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria) {
        List<ApiAssessmentSectionSummary> sectionSummaries = meansAssessment.getSectionSummaries();
        BigDecimal annualTotal = BigDecimal.ZERO;
        for (ApiAssessmentSectionSummary sectionSummary : sectionSummaries) {
            BigDecimal summaryTotal, applicantTotal, partnerTotal;
            applicantTotal = partnerTotal = BigDecimal.ZERO;
            for (ApiAssessmentDetail assessmentDetail : sectionSummary.getAssessmentDetails()) {
                assessmentCriteriaService.checkAssessmentDetail(
                        meansAssessment.getCaseType(), sectionSummary.getSection(), assessmentCriteria, assessmentDetail
                );

                applicantTotal = applicantTotal.add(
                        getDetailTotal(assessmentDetail, false));

                partnerTotal = partnerTotal.add(
                        getDetailTotal(assessmentDetail, true));

            }
            summaryTotal = applicantTotal.add(partnerTotal);
            sectionSummary.setApplicantAnnualTotal(applicantTotal);
            sectionSummary.setAnnualTotal(summaryTotal);
            sectionSummary.setPartnerAnnualTotal(partnerTotal);

            annualTotal = annualTotal.add(summaryTotal);
        }
        return annualTotal;
    }

    protected BigDecimal getDetailTotal(ApiAssessmentDetail assessmentDetail, boolean usePartner) {
        BigDecimal detailTotal = BigDecimal.ZERO;

        if (usePartner) {
            BigDecimal partnerAmount = assessmentDetail.getPartnerAmount();
            if (partnerAmount != null && !BigDecimal.ZERO.equals(partnerAmount)) {
                detailTotal = detailTotal.add(
                        partnerAmount.multiply(
                                BigDecimal.valueOf(assessmentDetail.getPartnerFrequency().getWeighting())
                        )
                );
            }
        } else {
            BigDecimal applicationAmount = assessmentDetail.getApplicantAmount();
            if (applicationAmount != null && !BigDecimal.ZERO.equals(applicationAmount)) {
                detailTotal = detailTotal.add(
                        applicationAmount.multiply(
                                BigDecimal.valueOf(assessmentDetail.getApplicantFrequency().getWeighting())
                        )
                );
            }
        }
        return detailTotal;
    }

    public MaatApiAssessmentResponse persistAssessment(MaatApiAssessmentRequest assessment, String laaTransactionId, String endpointUrl) {
        MaatApiAssessmentResponse response = webClient.post()
                .uri(endpointUrl)
                .headers(httpHeaders -> httpHeaders.setAll(Map.of(
                        "Laa-Transaction-Id", laaTransactionId
                )))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(assessment))
                .retrieve()
                .bodyToMono(MaatApiAssessmentResponse.class)
                .onErrorMap(throwable -> new APIClientException("Call to Court Data API failed, invalid response."))
                .doOnError(Sentry::captureException)
                .block();

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }
}
