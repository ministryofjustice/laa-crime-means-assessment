package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.builder.MaatCourtDataAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentResponseBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentSectionSummaryBuilder;
import uk.gov.justice.laa.crime.meansassessment.config.FeaturesConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentSectionSummaryDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinAssIncomeEvidenceDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDetails;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentProcessingException;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.IncomeEvidenceEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.util.SortUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentService {

    private final MaatCourtDataService maatCourtDataService;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final MeansAssessmentResponseBuilder responseBuilder;
    private final FeaturesConfiguration featuresConfiguration;
    private final MaatCourtDataAssessmentBuilder assessmentBuilder;
    private final FullAssessmentAvailabilityService fullAssessmentAvailabilityService;
    private final MeansAssessmentServiceFactory meansAssessmentServiceFactory;
    private final AssessmentCompletionService assessmentCompletionService;
    private final MeansAssessmentSectionSummaryBuilder meansAssessmentBuilder;
    private final AssessmentCriteriaDetailService assessmentCriteriaDetailService;
    private final IncomeEvidenceService incomeEvidenceService;

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

            if (featuresConfiguration.isDateCompletionEnabled()) {
                assessmentCompletionService.execute(completedAssessment, requestDTO.getLaaTransactionId());
            }

            MaatApiAssessmentResponse maatApiAssessmentResponse =
                    maatCourtDataService.persistMeansAssessment(
                            assessmentBuilder.build(completedAssessment, requestType),
                            requestDTO.getLaaTransactionId(),
                            requestType
                    );
            log.info("Posting completed means assessment to Court Data API");

            updateDetailIds(completedAssessment, maatApiAssessmentResponse);

            ApiMeansAssessmentResponse assessmentResponse =
                    responseBuilder.build(maatApiAssessmentResponse, assessmentCriteria, completedAssessment);

            if (AssessmentType.INIT.equals(assessmentType)) {
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

    public ApiMeansAssessmentResponse getOldAssessment(Integer financialAssessmentId, String laaTransactionId) {
        log.info("Processing get old assessment request - Start");
        ApiMeansAssessmentResponse assessmentResponse = null;
        FinancialAssessmentDTO financialAssessmentDTO = maatCourtDataService.getFinancialAssessment(financialAssessmentId, laaTransactionId);
        if (null != financialAssessmentDTO) {
            assessmentResponse = new ApiMeansAssessmentResponse();
            getAssessmentSectionSummary(financialAssessmentDTO);
            mapChildWeightings(assessmentResponse, financialAssessmentDTO);
            mapIncomeEvidence(assessmentResponse, financialAssessmentDTO);
        }
        log.info("Processing get old assessment request - End");
        return assessmentResponse;
    }

    protected void mapIncomeEvidence(ApiMeansAssessmentResponse assessmentResponse, FinancialAssessmentDTO financialAssessmentDTO) {
        List<FinAssIncomeEvidenceDTO> finAssIncomeEvidenceDTOList = financialAssessmentDTO.getFinAssIncomeEvidences();

        if (!finAssIncomeEvidenceDTOList.isEmpty()) {
            sortFinAssIncomeEvidenceSummary(finAssIncomeEvidenceDTOList);
            List<ApiIncomeEvidence> apiIncomeEvidenceList = new ArrayList<>();
            finAssIncomeEvidenceDTOList.forEach(finAssIncomeEvidenceDTO -> {
                ApiIncomeEvidence apiIncomeEvidence = new ApiIncomeEvidence()
                        .withId(finAssIncomeEvidenceDTO.getId())
                        .withApplicantId(finAssIncomeEvidenceDTO.getApplicant().getId())
                        .withAdhoc(finAssIncomeEvidenceDTO.getAdhoc())
                        .withMandatory(finAssIncomeEvidenceDTO.getMandatory())
                        .withOtherText(finAssIncomeEvidenceDTO.getOtherText())
                        .withDateModified(finAssIncomeEvidenceDTO.getDateModified())
                        .withDateReceived(finAssIncomeEvidenceDTO.getDateReceived())
                        .withApiEvidenceType(getEvidenceType(finAssIncomeEvidenceDTO.getIncomeEvidence()));
                apiIncomeEvidenceList.add(apiIncomeEvidence);
            });
            assessmentResponse.getIncomeEvidenceSummary().setIncomeEvidence(apiIncomeEvidenceList);
        }
    }

    protected ApiEvidenceType getEvidenceType(String evidence) {
        ApiEvidenceType apiEvidenceType = new ApiEvidenceType().withCode(evidence);
        Optional<IncomeEvidenceEntity> incomeEvidenceEntityOptional = incomeEvidenceService
                .getIncomeEvidenceById(evidence);
        incomeEvidenceEntityOptional.ifPresent(incomeEvidenceEntity ->
                apiEvidenceType.setDescription(incomeEvidenceEntity.getDescription())
        );
        return apiEvidenceType;
    }

    protected void sortFinAssIncomeEvidenceSummary(List<FinAssIncomeEvidenceDTO> finAssIncomeEvidenceDTOList) {
        SortUtils.sortListWithComparing(finAssIncomeEvidenceDTOList,
                FinAssIncomeEvidenceDTO::getMandatory, FinAssIncomeEvidenceDTO::getIncomeEvidence, SortUtils.getReverseComparator());
    }

    protected void mapChildWeightings(ApiMeansAssessmentResponse assessmentResponse, FinancialAssessmentDTO financialAssessmentDTO) {
        List<ApiAssessmentChildWeighting> apiAssessmentChildWeightings = new ArrayList<>();
        financialAssessmentDTO.getChildWeightings().forEach(childWeightings -> {
            Optional<AssessmentCriteriaChildWeightingEntity> assessmentCriteriaChildWeightingEntityO =
                    assessmentCriteriaService.getAssessmentCriteriaChildWeightingsById(childWeightings.getChildWeightingId());
            if (assessmentCriteriaChildWeightingEntityO.isPresent()) {
                AssessmentCriteriaChildWeightingEntity assessmentCriteriaChildWeightingEntity = assessmentCriteriaChildWeightingEntityO.get();
                ApiAssessmentChildWeighting apiAssessmentChildWeighting = new ApiAssessmentChildWeighting()
                        .withId(childWeightings.getId())
                        .withChildWeightingId(childWeightings.getChildWeightingId())
                        .withNoOfChildren(childWeightings.getNoOfChildren())
                        .withWeightingFactor(assessmentCriteriaChildWeightingEntity.getWeightingFactor())
                        .withLowerAgeRange(assessmentCriteriaChildWeightingEntity.getLowerAgeRange())
                        .withUpperAgeRange(assessmentCriteriaChildWeightingEntity.getUpperAgeRange());
                apiAssessmentChildWeightings.add(apiAssessmentChildWeighting);
            }
        });
        assessmentResponse.withChildWeightings(apiAssessmentChildWeightings);
    }

    protected List<AssessmentSectionSummaryDTO> getAssessmentSectionSummary(FinancialAssessmentDTO financialAssessmentDTO) {

        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryList = new ArrayList<>();
        if (!financialAssessmentDTO.getAssessmentDetails().isEmpty()) {
            List<AssessmentDTO> assessmentList = getAssessmentDTO(financialAssessmentDTO.getAssessmentDetails());
            if (!assessmentList.isEmpty()) {
                sortAssessmentDetail(assessmentList);
                assessmentSectionSummaryList = meansAssessmentBuilder.buildAssessmentSectionSummary(assessmentList);
            }
        }
        return assessmentSectionSummaryList;
    }

    protected List<AssessmentDTO> getAssessmentDTO(List<FinancialAssessmentDetails> financialAssessmentDetailsList) {
        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        financialAssessmentDetailsList.forEach(e -> {
            Optional<AssessmentCriteriaDetailEntity> assessmentCriteriaDetailEntity = assessmentCriteriaDetailService.getAssessmentCriteriaDetailById(e.getCriteriaDetailId());
            assessmentCriteriaDetailEntity.ifPresent(criteriaDetailEntity -> assessmentDTOList.add(meansAssessmentBuilder.buildAssessmentDTO(criteriaDetailEntity, e)));
        });
        return assessmentDTOList;
    }

    protected void sortAssessmentDetail(List<AssessmentDTO> assessmentDTOList) {
        SortUtils.sortListWithComparing(assessmentDTOList, AssessmentDTO::getSection, AssessmentDTO::getSequence, SortUtils.getComparator());
    }
}
