package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.builder.MaatCourtDataAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentResponseBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentProcessingException;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final MaatCourtDataService maatCourtDataService;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final MeansAssessmentResponseBuilder responseBuilder;
    private final MaatCourtDataAssessmentBuilder assessmentBuilder;
    private final FullAssessmentAvailabilityService fullAssessmentAvailabilityService;
    private final MeansAssessmentServiceFactory meansAssessmentServiceFactory;
    private final MaatAssessmentCompletionService maatAssessmentCompletionService;

    public ApiMeansAssessmentResponse doAssessment(MeansAssessmentRequestDTO requestDTO, AssessmentRequestType requestType) {
        log.info("Processing assessment request - Start");
        try {
            AssessmentType assessmentType = requestDTO.getAssessmentType();
            LocalDateTime assessmentDate =
                    (AssessmentType.FULL.equals(assessmentType)) ? requestDTO.getFullAssessmentDate()
                            : requestDTO.getInitialAssessmentDate();

            AssessmentService assessmentService =
                    meansAssessmentServiceFactory.getService(assessmentType);

            AssessmentCriteriaEntity assessmentCriteria = assessmentCriteriaService.getAssessmentCriteria(
                    assessmentDate, requestDTO.getHasPartner(), requestDTO.getPartnerContraryInterest());

            BigDecimal summariesTotal = calculateSummariesTotal(requestDTO, assessmentCriteria);

            MeansAssessmentDTO completedAssessment =
                    assessmentService.execute(summariesTotal, requestDTO, assessmentCriteria);
            completedAssessment.setMeansAssessment(requestDTO);
            completedAssessment.setAssessmentCriteria(assessmentCriteria);

            maatAssessmentCompletionService.execute(completedAssessment, assessmentType, requestDTO.getLaaTransactionId());

            MaatApiAssessmentResponse maatApiAssessmentResponse =
                    maatCourtDataService.persistMeansAssessment(
                            assessmentBuilder.build(completedAssessment, requestType),
                            requestDTO.getLaaTransactionId(),
                            requestType
                    );
            log.info("Posting completed means assessment to Court Data API");

            updateDetailIds(completedAssessment, maatApiAssessmentResponse);

            ApiMeansAssessmentResponse assessmentResponse =
                    responseBuilder.build(maatApiAssessmentResponse.getId(), assessmentCriteria, completedAssessment);

            if (AssessmentRequestType.UPDATE.equals(requestType)) {
                fullAssessmentAvailabilityService.processFullAssessmentAvailable(requestDTO, assessmentResponse);
            }

            return assessmentResponse;
        } catch (Exception exception) {
            throw new AssessmentProcessingException(
                    String.format("An error occurred whilst processing the assessment request with RepID: %d",
                            requestDTO.getRepId()), exception
            );
        }
    }

    void updateDetailIds(MeansAssessmentDTO completedAssessment, MaatApiAssessmentResponse maatApiAssessmentResponse) {
        for (ApiAssessmentSectionSummary assessmentSectionSummary : completedAssessment.getMeansAssessment().getSectionSummaries()) {
            for (ApiAssessmentDetail assessmentDetail : assessmentSectionSummary.getAssessmentDetails()) {
                for (ApiAssessmentDetail responseAssessmentDetail : maatApiAssessmentResponse.getAssessmentDetails()) {
                    if (assessmentDetail.getCriteriaDetailId().equals(responseAssessmentDetail.getCriteriaDetailId())) {
                        assessmentDetail.setId(responseAssessmentDetail.getId());
                    }
                }
            }
        }
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
}
