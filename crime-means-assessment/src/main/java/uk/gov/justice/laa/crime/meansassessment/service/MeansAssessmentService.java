package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.meansassessment.*;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.meansassessment.builder.MaatCourtDataAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentResponseBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentSectionSummaryBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinAssIncomeEvidenceDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDetails;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentProcessingException;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.IncomeEvidenceEntity;
import uk.gov.justice.laa.crime.meansassessment.util.SortUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MeansAssessmentService extends BaseMeansAssessmentService {

    private final MaatCourtDataService maatCourtDataService;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final MeansAssessmentResponseBuilder responseBuilder;
    private final MaatCourtDataAssessmentBuilder assessmentBuilder;
    private final FullAssessmentAvailabilityService fullAssessmentAvailabilityService;
    private final MeansAssessmentServiceFactory meansAssessmentServiceFactory;
    private final AssessmentCompletionService assessmentCompletionService;
    private final MeansAssessmentSectionSummaryBuilder meansAssessmentBuilder;
    private final AssessmentCriteriaDetailService assessmentCriteriaDetailService;
    private final EligibilityChecker crownCourtEligibilityService;

    public MeansAssessmentService(MaatCourtDataService maatCourtDataService,
                                  AssessmentCriteriaService assessmentCriteriaService,
                                  MeansAssessmentResponseBuilder responseBuilder,
                                  MaatCourtDataAssessmentBuilder assessmentBuilder,
                                  FullAssessmentAvailabilityService fullAssessmentAvailabilityService,
                                  MeansAssessmentServiceFactory meansAssessmentServiceFactory,
                                  AssessmentCompletionService assessmentCompletionService,
                                  MeansAssessmentSectionSummaryBuilder meansAssessmentBuilder,
                                  AssessmentCriteriaDetailService assessmentCriteriaDetailService,
                                  EligibilityChecker crownCourtEligibilityService) {

        super(assessmentCriteriaService);
        this.maatCourtDataService = maatCourtDataService;
        this.assessmentCriteriaService = assessmentCriteriaService;
        this.responseBuilder = responseBuilder;
        this.assessmentBuilder = assessmentBuilder;
        this.fullAssessmentAvailabilityService = fullAssessmentAvailabilityService;
        this.meansAssessmentServiceFactory = meansAssessmentServiceFactory;
        this.assessmentCompletionService = assessmentCompletionService;
        this.meansAssessmentBuilder = meansAssessmentBuilder;
        this.assessmentCriteriaDetailService = assessmentCriteriaDetailService;
        this.crownCourtEligibilityService = crownCourtEligibilityService;
    }

    public ApiMeansAssessmentResponse doAssessment(MeansAssessmentRequestDTO requestDTO, RequestType requestType) {
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

            if (AssessmentType.FULL == assessmentType) {
                requestDTO.setEligibilityCheckRequired(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO));
            }
            MeansAssessmentDTO completedAssessment =
                    assessmentService.execute(summariesTotal, requestDTO, assessmentCriteria);
            completedAssessment.setMeansAssessment(requestDTO);
            completedAssessment.setAssessmentCriteria(assessmentCriteria);

            assessmentCompletionService.execute(completedAssessment);

            MaatApiAssessmentResponse maatApiAssessmentResponse =
                    maatCourtDataService.persistMeansAssessment(
                            assessmentBuilder.build(completedAssessment, requestType),
                            requestType
                    );
            log.info("Posting completed means assessment to Court Data API");

            updateDetailIds(completedAssessment, maatApiAssessmentResponse);

            ApiMeansAssessmentResponse assessmentResponse =
                    responseBuilder.build(maatApiAssessmentResponse, assessmentCriteria, completedAssessment);

            if (AssessmentType.INIT.equals(assessmentType)) {
                boolean fullAssessmentAvailable = fullAssessmentAvailabilityService
                        .isFullAssessmentAvailable(requestDTO.getCaseType(),
                                requestDTO.getMagCourtOutcome(),
                                requestDTO.getNewWorkReason(),
                                completedAssessment.getInitAssessmentResult());
                assessmentResponse.setFullAssessmentAvailable(fullAssessmentAvailable);
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

    public ApiGetMeansAssessmentResponse getOldAssessment(Integer financialAssessmentId) {
        log.info("Processing get old assessment request - Start");
        ApiGetMeansAssessmentResponse assessmentResponse = null;
        FinancialAssessmentDTO financialAssessmentDTO =
                maatCourtDataService.getFinancialAssessment(financialAssessmentId);
        if (null != financialAssessmentDTO) {
            assessmentResponse = new ApiGetMeansAssessmentResponse();
            buildMeansAssessmentResponse(assessmentResponse, financialAssessmentDTO);
        }
        log.info("Processing get old assessment request - End");
        return assessmentResponse;
    }

    protected void mapIncomeEvidence(ApiGetMeansAssessmentResponse apiGetMeansAssessmentResponse,
                                     FinancialAssessmentDTO financialAssessmentDTO) {

        List<FinAssIncomeEvidenceDTO> finAssIncomeEvidenceDTOList = financialAssessmentDTO.getFinAssIncomeEvidences();

        ApiIncomeEvidenceSummary apiIncomeEvidenceSummary = getApiIncomeEvidenceSummary(financialAssessmentDTO);
        if (!finAssIncomeEvidenceDTOList.isEmpty()) {
            sortFinAssIncomeEvidenceSummary(finAssIncomeEvidenceDTOList);
            finAssIncomeEvidenceDTOList.forEach(finAssIncomeEvidenceDTO -> {
                ApiIncomeEvidence apiIncomeEvidence = new ApiIncomeEvidence()
                        .withId(finAssIncomeEvidenceDTO.getId())
                        .withApplicantId(finAssIncomeEvidenceDTO.getApplicant().getId())
                        .withAdhoc(finAssIncomeEvidenceDTO.getAdhoc())
                        .withMandatory(finAssIncomeEvidenceDTO.getMandatory())
                        .withOtherText(finAssIncomeEvidenceDTO.getOtherText())
                        .withDateModified(finAssIncomeEvidenceDTO.getDateModified())
                        .withDateReceived(finAssIncomeEvidenceDTO.getDateReceived())
                        .withIncomeEvidence(finAssIncomeEvidenceDTO.getIncomeEvidence());
                apiIncomeEvidenceSummary.getIncomeEvidence().add(apiIncomeEvidence);
            });
        }
        apiGetMeansAssessmentResponse.setIncomeEvidenceSummary(apiIncomeEvidenceSummary);
    }

    @NotNull
    private static ApiIncomeEvidenceSummary getApiIncomeEvidenceSummary(FinancialAssessmentDTO financialAssessmentDTO) {
        return new ApiIncomeEvidenceSummary()
                .withEvidenceDueDate(financialAssessmentDTO.getIncomeEvidenceDueDate())
                .withEvidenceReceivedDate(financialAssessmentDTO.getEvidenceReceivedDate())
                .withIncomeEvidenceNotes(financialAssessmentDTO.getIncomeEvidenceNotes())
                .withUpliftAppliedDate(financialAssessmentDTO.getIncomeUpliftApplyDate())
                .withUpliftRemovedDate(financialAssessmentDTO.getIncomeUpliftRemoveDate())
                .withFirstReminderDate(financialAssessmentDTO.getFirstReminderDate())
                .withSecondReminderDate(financialAssessmentDTO.getSecondReminderDate());
    }

    protected void sortFinAssIncomeEvidenceSummary(List<FinAssIncomeEvidenceDTO> finAssIncomeEvidenceDTOList) {
        SortUtils.sortListWithComparing(finAssIncomeEvidenceDTOList, FinAssIncomeEvidenceDTO::getMandatory,
                FinAssIncomeEvidenceDTO::getIncomeEvidence, SortUtils.getReverseComparator()
        );
    }

    protected void mapChildWeightings(ApiInitialMeansAssessment assessment, FinancialAssessmentDTO financialAssessmentDTO) {
        List<ApiAssessmentChildWeighting> apiAssessmentChildWeightings = new ArrayList<>();
        financialAssessmentDTO.getChildWeightings().forEach(childWeightings -> {
            Optional<AssessmentCriteriaChildWeightingEntity> criteriaChildWeighting =
                    assessmentCriteriaService.getAssessmentCriteriaChildWeightingsById(
                            childWeightings.getChildWeightingId()
                    );
            if (criteriaChildWeighting.isPresent()) {
                AssessmentCriteriaChildWeightingEntity assessmentCriteriaChildWeightingEntity =
                        criteriaChildWeighting.get();
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
        assessment.setChildWeighting(apiAssessmentChildWeightings);
    }

    public void buildMeansAssessmentResponse(ApiGetMeansAssessmentResponse assessmentResponse,
                                             FinancialAssessmentDTO financialAssessmentDTO) {

        assessmentResponse.setId(financialAssessmentDTO.getId());
        assessmentResponse.setUsn(financialAssessmentDTO.getUsn());
        assessmentResponse.setInitialAssessment(new ApiInitialMeansAssessment());
        assessmentResponse.setFullAssessment(new ApiFullMeansAssessment());
        assessmentResponse.setIncomeEvidenceSummary(new ApiIncomeEvidenceSummary());

        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = getAssessmentSectionSummary(financialAssessmentDTO);
        Optional<AssessmentCriteriaEntity> initAssessmentCriteria =
                assessmentCriteriaService.getAssessmentCriteriaById(financialAssessmentDTO.getInitialAscrId());
        meansAssessmentBuilder.buildInitialAssessment(
                assessmentResponse, financialAssessmentDTO, assessmentSectionSummaryList, initAssessmentCriteria
        );

        if (AssessmentType.FULL.equals(AssessmentType.getFrom(financialAssessmentDTO.getAssessmentType()))) {
            Optional<AssessmentCriteriaEntity> fullAssessmentCriteria =
                    assessmentCriteriaService.getAssessmentCriteriaById(financialAssessmentDTO.getFullAscrId());

            meansAssessmentBuilder.buildFullAssessment(
                    assessmentResponse, financialAssessmentDTO, assessmentSectionSummaryList, fullAssessmentCriteria
            );
        }

        assessmentResponse.setFullAvailable(Boolean.FALSE);
        if (null != financialAssessmentDTO.getFullAssessmentDate()) {
            assessmentResponse.setFullAvailable(Boolean.TRUE);
        }

        mapChildWeightings(assessmentResponse.getInitialAssessment(), financialAssessmentDTO);
        mapIncomeEvidence(assessmentResponse, financialAssessmentDTO);
    }

    protected List<ApiAssessmentSectionSummary> getAssessmentSectionSummary(FinancialAssessmentDTO financialAssessmentDTO) {

        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
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
            Optional<AssessmentCriteriaDetailEntity> assessmentCriteriaDetailEntity =
                    assessmentCriteriaDetailService.getAssessmentCriteriaDetailById(e.getCriteriaDetailId());
            assessmentCriteriaDetailEntity.ifPresent(criteriaDetailEntity ->
                    assessmentDTOList.add(meansAssessmentBuilder.buildAssessmentDTO(criteriaDetailEntity, e))
            );
        });
        return assessmentDTOList;
    }

    protected void sortAssessmentDetail(List<AssessmentDTO> assessmentDTOList) {
        SortUtils.sortListWithComparing(
                assessmentDTOList, AssessmentDTO::getSection, AssessmentDTO::getSequence, SortUtils.getComparator()
        );
    }

    public ApiRollbackMeansAssessmentResponse rollbackAssessment(int financialAssessmentId) {
        FinancialAssessmentDTO financialAssessmentDTO =
                maatCourtDataService.getFinancialAssessment(financialAssessmentId);
        ApiRollbackMeansAssessmentResponse apiRollbackMeansAssessmentResponse =
                new ApiRollbackMeansAssessmentResponse();
        if (financialAssessmentDTO != null) {
            String assessmentType = financialAssessmentDTO.getAssessmentType();
            apiRollbackMeansAssessmentResponse.setAssessmentType(assessmentType);
            Map<String, Object> updateFields = new HashMap<>();
            if (AssessmentType.INIT.getType().equals(assessmentType)) {
                updateFields.put("fassInitStatus", "IN PROGRESS");
                updateFields.put("initResult", null);
                apiRollbackMeansAssessmentResponse.setFassInitStatus(CurrentStatus.IN_PROGRESS);
                apiRollbackMeansAssessmentResponse.setInitResult(null);
            } else if (AssessmentType.FULL.getType().equals(assessmentType)) {
                updateFields.put("fassFullStatus", "IN PROGRESS");
                updateFields.put("fullResult", null);
                apiRollbackMeansAssessmentResponse.setFassFullStatus(CurrentStatus.IN_PROGRESS);
                apiRollbackMeansAssessmentResponse.setFullResult(null);
            }
            maatCourtDataService.rollbackFinancialAssessment(financialAssessmentId, updateFields);
        }
        return apiRollbackMeansAssessmentResponse;
    }
}
