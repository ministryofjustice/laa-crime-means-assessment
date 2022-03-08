package uk.gov.justice.laa.crime.meansassessment.service;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.builder.maatapi.ApiCreateAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.maatapi.ApiUpdateAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

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
    private final FullMeansAssessmentService fullMeansAssessmentService;
    private final InitMeansAssessmentService initMeansAssessmentService;
    private final AssessmentCriteriaService assessmentCriteriaService;

    public ApiCreateMeansAssessmentResponse doAssessment(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentRequestType requestType) {
        AssessmentCriteriaEntity assessmentCriteria =
                assessmentCriteriaService.getAssessmentCriteria(
                        meansAssessment.getAssessmentDate(), meansAssessment.getHasPartner(), meansAssessment.getPartnerContraryInterest()
                );
        String targetEndpoint;
        MeansAssessmentDTO assessment;
        if (meansAssessment.getAssessmentType().equals(AssessmentType.INIT)) {
            log.info("Create initial means assessment - Start");
            assessment = doInitAssessment(meansAssessment, assessmentCriteria);
            log.info("Init means assessment calculation complete for Rep ID: {}", meansAssessment.getRepId());
        } else {
            log.info("Create full means assessment - Start");
            assessment = doFullAssessment(meansAssessment, assessmentCriteria);
            log.info("Full means assessment calculation complete for Rep ID: {}", meansAssessment.getRepId());
        }

        MaatApiAssessmentRequest assessmentPayload;
        if (requestType.equals(AssessmentRequestType.CREATE)) {
            targetEndpoint = configuration.getFinancialAssessmentEndpoints().getCreateUrl();
            assessmentPayload = ApiCreateAssessmentBuilder.build(assessment);
        } else {
            targetEndpoint = configuration.getFinancialAssessmentEndpoints().getUpdateUrl();
            assessmentPayload = ApiUpdateAssessmentBuilder.build(assessment);
        }

        MaatApiAssessmentResponse response =
                persistAssessment(assessmentPayload, meansAssessment.getLaaTransactionId(), targetEndpoint);

        return new ApiCreateMeansAssessmentResponse()
                .withAssessmentId(response.getId().toString())
                .withCriteriaId(assessmentCriteria.getId())
                .withLowerThreshold(assessmentCriteria.getInitialLowerThreshold())
                .withUpperThreshold(assessmentCriteria.getInitialUpperThreshold())
                .withTotalAggregatedIncome(assessment.getTotalAggregatedIncome())
                .withResult(assessment.getInitialAssessmentResult().getResult())
                .withResultReason(assessment.getInitialAssessmentResult().getReason())
                .withAdjustedIncomeValue(assessment.getAdjustedIncomeValue())
                .withAssessmentStatus(new ApiAssessmentStatus()
                        .withStatus(assessment.getCurrentStatus().getStatus())
                        .withDescription(assessment.getCurrentStatus().getDescription())
                        .withComplete(assessment.getCurrentStatus().equals(CurrentStatus.COMPLETE))
                )
                .withAssessmentSummary(assessment.getMeansAssessment().getSectionSummaries());

    }

    public MeansAssessmentDTO doInitAssessment(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria) {
        BigDecimal annualTotal = calculateSummariesTotal(meansAssessment, assessmentCriteria);
        BigDecimal adjustedIncomeValue = initMeansAssessmentService.getAdjustedIncome(
                meansAssessment, assessmentCriteria, annualTotal);

        InitialAssessmentResult result;
        CurrentStatus status = meansAssessment.getAssessmentStatus();
        String newWorkReasonCode = meansAssessment.getNewWorkReason().getCode();
        if (status.equals(CurrentStatus.COMPLETE)) {
            result = initMeansAssessmentService.getAssessmentResult(adjustedIncomeValue, assessmentCriteria, newWorkReasonCode);
        } else {
            result = InitialAssessmentResult.NONE;
        }
        return MeansAssessmentDTO
                .builder()
                .currentStatus(status)
                .initialAssessmentResult(result)
                .meansAssessment(meansAssessment)
                .assessmentCriteria(assessmentCriteria)
                .adjustedIncomeValue(adjustedIncomeValue)
                .totalAggregatedIncome(annualTotal)
                .build();

    }

    public MeansAssessmentDTO doFullAssessment(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria) {
        BigDecimal expenditureTotal = calculateSummariesTotal(meansAssessment, assessmentCriteria);
        BigDecimal adjustedLivingAllowance =
                fullMeansAssessmentService.getAdjustedLivingAllowance(meansAssessment, assessmentCriteria);
        BigDecimal totalDisposableIncome = fullMeansAssessmentService.getDisposableIncome(
                meansAssessment, expenditureTotal, adjustedLivingAllowance
        );
        FullAssessmentResult result;
        CurrentStatus status = meansAssessment.getAssessmentStatus();
        if (status.equals(CurrentStatus.COMPLETE)) {
            result = fullMeansAssessmentService.getAssessmentResult(totalDisposableIncome, assessmentCriteria);
        } else {
            result = FullAssessmentResult.NONE;
        }
        return MeansAssessmentDTO
                .builder()
                .currentStatus(status)
                .fullAssessmentResult(result)
                .adjustedLivingAllowance(adjustedLivingAllowance)
                .totalAggregatedExpense(expenditureTotal)
                .totalAnnualDisposableIncome(totalDisposableIncome)
                .meansAssessment(meansAssessment)
                .assessmentCriteria(assessmentCriteria)
                .build();
    }

    protected BigDecimal calculateSummariesTotal(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria) {
        List<ApiAssessmentSectionSummary> sectionSummaries = meansAssessment.getSectionSummaries();
        BigDecimal annualTotal = BigDecimal.ZERO;
        for (ApiAssessmentSectionSummary sectionSummary : sectionSummaries) {
            BigDecimal partnerTotal = BigDecimal.ZERO;
            BigDecimal applicantTotal = BigDecimal.ZERO;
            for (ApiAssessmentDetail assessmentDetail : sectionSummary.getAssessmentDetails()) {
                assessmentCriteriaService.checkAssessmentDetail(meansAssessment.getCaseType(), sectionSummary.getSection(), assessmentCriteria, assessmentDetail);

                applicantTotal = applicantTotal.add(
                        getDetailTotal(assessmentDetail, false));

                partnerTotal = partnerTotal.add(
                        getDetailTotal(assessmentDetail, true));

            }
            annualTotal = annualTotal.add(
                    applicantTotal.add(partnerTotal)
            );
            sectionSummary.setAnnualTotal(annualTotal);
            sectionSummary.setApplicantAnnualTotal(applicantTotal);
            sectionSummary.setPartnerAnnualTotal(partnerTotal);
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
