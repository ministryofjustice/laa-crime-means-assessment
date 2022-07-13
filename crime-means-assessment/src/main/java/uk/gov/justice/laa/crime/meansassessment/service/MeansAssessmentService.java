package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.builder.maatapi.MaatCourtDataAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentProcessingException;
import uk.gov.justice.laa.crime.meansassessment.model.PostProcessing;
import uk.gov.justice.laa.crime.meansassessment.model.UserSession;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final MaatCourtDataService maatCourtDataService;
    private final FullMeansAssessmentService fullMeansAssessmentService;
    private final AssessmentSummaryService assessmentSummaryService;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final MaatCourtDataAssessmentBuilder assessmentBuilder;
    private final InitMeansAssessmentService initMeansAssessmentService;
    private final FullAssessmentAvailabilityService fullAssessmentAvailabilityService;


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
            AssessmentCriteriaEntity assessmentCriteria = assessmentCriteriaService.getAssessmentCriteria(
                    assessmentDate, requestDTO.getHasPartner(), requestDTO.getPartnerContraryInterest());

            BigDecimal summariesTotal = calculateSummariesTotal(requestDTO, assessmentCriteria);

            MeansAssessmentDTO completedAssessment = assessmentService.execute(summariesTotal, requestDTO, assessmentCriteria);
            completedAssessment.setMeansAssessment(requestDTO);
            completedAssessment.setAssessmentCriteria(assessmentCriteria);

            MaatApiAssessmentResponse maatApiAssessmentResponse = maatCourtDataService.postMeansAssessment(
                    assessmentBuilder.buildAssessmentRequest(completedAssessment, requestType),
                    requestDTO.getLaaTransactionId(), requestType);
            log.info("Posting completed means assessment to Court Data API");

            ApiCreateMeansAssessmentResponse assessmentResponse = buildApiCreateMeansAssessmentResponse(
                    maatApiAssessmentResponse, assessmentCriteria, completedAssessment);

            fullAssessmentAvailabilityService.processFullAssessmentAvailable(requestDTO, assessmentResponse);
            assessmentSummaryService.addAssessmentSummaryToMeansResponse(assessmentResponse, requestDTO.getLaaTransactionId());
            maatCourtDataService.createFinancialAssessmentHistory(assessmentResponse.getAssessmentId(), assessmentResponse.getFullAssessmentAvailable(), requestDTO.getLaaTransactionId()).subscribe();

            doPostProcessing(requestDTO);


            return assessmentResponse;
        } catch (RuntimeException exception) {
            throw new AssessmentProcessingException(
                    String.format("An error occurred whilst processing the assessment request with RepID: %d",
                            requestDTO.getRepId()), exception);
        }
    }

    private void doPostProcessing(MeansAssessmentRequestDTO requestDTO) {
        log.info("Sending assessment post processing request for MAAT ID: {}", requestDTO.getRepId());
        ApiUserSession userSession = requestDTO.getUserSession();
        PostProcessing postprocessingRequest = PostProcessing
                .builder()
                .repId(requestDTO.getRepId())
                .laaTransactionId(requestDTO.getLaaTransactionId())
                .user(UserSession
                        .builder()
                        .username(userSession.getUserName())
                        .id(userSession.getSessionId())
                        .build())
                .build();
        maatCourtDataService.performAssessmentPostProcessing(postprocessingRequest);
    }

    BigDecimal calculateSummariesTotal(final MeansAssessmentRequestDTO requestDTO, final AssessmentCriteriaEntity assessmentCriteria) {
        List<ApiAssessmentSectionSummary> sectionSummaries = requestDTO.getSectionSummaries();
        BigDecimal annualTotal = BigDecimal.ZERO;
        for (ApiAssessmentSectionSummary sectionSummary : sectionSummaries) {
            BigDecimal summaryTotal;
            BigDecimal applicantTotal;
            BigDecimal partnerTotal;

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

    BigDecimal getDetailTotal(final ApiAssessmentDetail assessmentDetail, final boolean usePartner) {
        BigDecimal detailTotal = BigDecimal.ZERO;

        if (usePartner) {
            BigDecimal partnerAmount = assessmentDetail.getPartnerAmount();
            if (partnerAmount != null && !BigDecimal.ZERO.equals(partnerAmount)) {
                Frequency partnerFrequency = assessmentDetail.getPartnerFrequency();
                if (partnerFrequency != null) {
                    detailTotal = detailTotal.add(partnerAmount.multiply(BigDecimal.valueOf(partnerFrequency.getWeighting())));
                }
            }
        } else {
            BigDecimal applicationAmount = assessmentDetail.getApplicantAmount();
            if (applicationAmount != null && !BigDecimal.ZERO.equals(applicationAmount)) {
                Frequency applicantFrequency = assessmentDetail.getApplicantFrequency();
                if (applicantFrequency != null) {
                    detailTotal = detailTotal.add(applicationAmount.multiply(BigDecimal.valueOf(applicantFrequency.getWeighting())));
                }
            }
        }
        return detailTotal;
    }

    private ApiCreateMeansAssessmentResponse buildApiCreateMeansAssessmentResponse(final MaatApiAssessmentResponse maatApiAssessmentResponse,
                                                                                   final AssessmentCriteriaEntity assessmentCriteria,
                                                                                   final MeansAssessmentDTO completedAssessment) {
        return new ApiCreateMeansAssessmentResponse()
                .withAssessmentId(maatApiAssessmentResponse.getId())
                .withRepId(maatApiAssessmentResponse.getRepId())
                .withCriteriaId(assessmentCriteria.getId())
                .withLowerThreshold(assessmentCriteria.getInitialLowerThreshold())
                .withUpperThreshold(assessmentCriteria.getInitialUpperThreshold())
                .withTotalAggregatedIncome(maatApiAssessmentResponse.getInitTotAggregatedIncome())
                .withInitResult(maatApiAssessmentResponse.getInitResult())
                .withInitResultReason(maatApiAssessmentResponse.getInitResultReason())
                .withAdjustedIncomeValue(maatApiAssessmentResponse.getInitAdjustedIncomeValue())
                .withFassInitStatus(CurrentStatus.getFrom(maatApiAssessmentResponse.getFassInitStatus()))
                .withAssessmentSectionSummary(completedAssessment.getMeansAssessment().getSectionSummaries());
    }
}
