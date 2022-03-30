package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.builder.maatapi.MaatCourtDataAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentProcessingException;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final MaatCourtDataService maatCourtDataService;
    private final MaatApiConfiguration configuration;
    private final FullMeansAssessmentService fullMeansAssessmentService;
    private final AssessmentSummaryService assessmentSummaryService;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final MaatCourtDataAssessmentBuilder assessmentBuilder;
    private final InitMeansAssessmentService initMeansAssessmentService;

    public ApiCreateMeansAssessmentResponse doAssessment(MeansAssessmentRequestDTO requestDTO, AssessmentRequestType requestType) {
        log.info("Processing assessment request - Start");
        try {
            LocalDateTime assessmentDate;
            AssessmentService assessmentService;
            if (AssessmentType.FULL.equals(requestDTO.getAssessmentType())) {
                assessmentService = fullMeansAssessmentService;
                assessmentDate = requestDTO.getFullAssessmentDate();
            } else {
                assessmentService = initMeansAssessmentService;
                assessmentDate = requestDTO.getInitialAssessmentDate();
            }
            log.info("Retrieving assessment criteria for date: {}", assessmentDate);
            AssessmentCriteriaEntity assessmentCriteria =
                    assessmentCriteriaService.getAssessmentCriteria(
                            assessmentDate, requestDTO.getHasPartner(), requestDTO.getPartnerContraryInterest()
                    );
            BigDecimal summariesTotal = calculateSummariesTotal(requestDTO, assessmentCriteria);

            MeansAssessmentDTO completedAssessment =
                    assessmentService.execute(summariesTotal, requestDTO, assessmentCriteria);

            completedAssessment.setMeansAssessment(requestDTO);
            completedAssessment.setAssessmentCriteria(assessmentCriteria);

            String targetEndpoint = configuration.getFinancialAssessmentEndpoints()
                    .getByRequestType(requestType);

            log.info("Posting completed means assessment to Court Data API");
            MaatApiAssessmentResponse response =
                    maatCourtDataService.postMeansAssessment(
                            assessmentBuilder.buildAssessmentRequest(completedAssessment, requestType),
                            requestDTO.getLaaTransactionId(), targetEndpoint
                    );

            ApiCreateMeansAssessmentResponse assessmentResponse =
                    new ApiCreateMeansAssessmentResponse()
                            .withAssessmentId(response.getId())
                            .withCriteriaId(assessmentCriteria.getId())
                            .withLowerThreshold(assessmentCriteria.getInitialLowerThreshold())
                            .withUpperThreshold(assessmentCriteria.getInitialUpperThreshold())
                            .withTotalAggregatedIncome(response.getInitTotAggregatedIncome())
                            .withInitResult(response.getInitResult())
                            .withInitResultReason(response.getInitResultReason())
                            .withAdjustedIncomeValue(response.getInitAdjustedIncomeValue())
                            .withFassInitStatus(CurrentStatus.getFrom(response.getFassInitStatus()))
                            .withAssessmentSectionSummary(completedAssessment.getMeansAssessment().getSectionSummaries());

            assessmentSummaryService.addAssessmentSummaryToMeansResponse(
                    assessmentResponse, requestDTO.getLaaTransactionId());

            return assessmentResponse;

        } catch (RuntimeException exception) {
            throw new AssessmentProcessingException(
                    String.format("An error occurred whilst processing the assessment request with RepID: %d",
                            requestDTO.getRepId()), exception);
        }
    }

    BigDecimal calculateSummariesTotal(MeansAssessmentRequestDTO requestDTO, AssessmentCriteriaEntity assessmentCriteria) {
        List<ApiAssessmentSectionSummary> sectionSummaries = requestDTO.getSectionSummaries();
        BigDecimal annualTotal = BigDecimal.ZERO;
        for (ApiAssessmentSectionSummary sectionSummary : sectionSummaries) {
            BigDecimal summaryTotal, applicantTotal, partnerTotal;
            applicantTotal = partnerTotal = BigDecimal.ZERO;
            for (ApiAssessmentDetail assessmentDetail : sectionSummary.getAssessmentDetails()) {
                assessmentCriteriaService.checkAssessmentDetail(
                        requestDTO.getCaseType(), sectionSummary.getSection(), assessmentCriteria, assessmentDetail
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

    BigDecimal getDetailTotal(ApiAssessmentDetail assessmentDetail, boolean usePartner) {
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
}
