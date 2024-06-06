package uk.gov.justice.laa.crime.meansassessment.builder;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.meansassessment.CrimeMeansAssessmentApplication;
import uk.gov.justice.laa.crime.meansassessment.config.CrimeMeansAssessmentTestConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.common.model.meansassessment.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.*;

@ExtendWith(SpringExtension.class)
@Import(CrimeMeansAssessmentTestConfiguration.class)
@SpringBootTest(classes = {CrimeMeansAssessmentApplication.class})
@AutoConfigureObservability
class MeansAssessmentSectionSummaryBuilderTest {

    static final BigDecimal EXPECTED_AMOUNT = new BigDecimal("280.00");
    private final MeansAssessmentSectionSummaryBuilder meansAssessmentSectionSummaryBuilder
            = new MeansAssessmentSectionSummaryBuilder();

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenInvalidASectionAssessment_whenBuildInvoked_shouldReturnEmpty() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_SECTION, TEST_SEQ));
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryDTOS = meansAssessmentSectionSummaryBuilder.buildAssessmentSectionSummary(assessmentDTOList);
        assertThat(0).isEqualTo(assessmentSectionSummaryDTOS.size());
    }

    @Test
    void givenInitASectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ + TEST_SEQ));

        List<ApiAssessmentSectionSummary> assessmentSectionSummaryDTOS = meansAssessmentSectionSummaryBuilder.buildAssessmentSectionSummary(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(1).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.INIT);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_INITA);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(EXPECTED_AMOUNT);

    }

    @Test
    void givenInitBSectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITB, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITB, TEST_SEQ + TEST_SEQ));

        List<ApiAssessmentSectionSummary> assessmentSectionSummaryDTOS = meansAssessmentSectionSummaryBuilder.buildAssessmentSectionSummary(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(1).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.INIT);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_INITB);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(EXPECTED_AMOUNT);

    }

    @Test
    void givenBothInitAAndIntBSectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ + TEST_SEQ));

        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITB, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITB, TEST_SEQ + TEST_SEQ));


        List<ApiAssessmentSectionSummary> assessmentSectionSummaryDTOS = meansAssessmentSectionSummaryBuilder.buildAssessmentSectionSummary(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.INIT);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_INITA);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(EXPECTED_AMOUNT);

        assertThat(assessmentSectionSummaryDTOS.get(1).getAssessmentType()).isEqualTo(AssessmentType.INIT);
        assertThat(assessmentSectionSummaryDTOS.get(1).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_INITB);
        assertThat(assessmentSectionSummaryDTOS.get(1).getAnnualTotal()).isEqualTo(EXPECTED_AMOUNT);

    }

    @Test
    void givenFullASectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLA, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLA, TEST_SEQ + TEST_SEQ));

        List<ApiAssessmentSectionSummary> assessmentSectionSummaryDTOS = meansAssessmentSectionSummaryBuilder.buildAssessmentSectionSummary(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(1).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.FULL);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_FULLA);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(EXPECTED_AMOUNT);

    }

    @Test
    void givenBothFullAAndFullBSectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLA, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLA, TEST_SEQ + TEST_SEQ));

        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLB, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLB, TEST_SEQ + TEST_SEQ));


        List<ApiAssessmentSectionSummary> assessmentSectionSummaryDTOS = meansAssessmentSectionSummaryBuilder.buildAssessmentSectionSummary(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.FULL);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_FULLA);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(EXPECTED_AMOUNT);

        assertThat(assessmentSectionSummaryDTOS.get(1).getAssessmentType()).isEqualTo(AssessmentType.FULL);
        assertThat(assessmentSectionSummaryDTOS.get(1).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_FULLB);
        assertThat(assessmentSectionSummaryDTOS.get(1).getAnnualTotal()).isEqualTo(EXPECTED_AMOUNT);

    }

    @Test
    void givenFullBSectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLB, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLB, TEST_SEQ + TEST_SEQ));

        List<ApiAssessmentSectionSummary> assessmentSectionSummaryDTOS = meansAssessmentSectionSummaryBuilder.buildAssessmentSectionSummary(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(1).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.FULL);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_FULLB);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(EXPECTED_AMOUNT);

    }

    @Test
    void givenValidAssessment_whenGetAssessmentDetailInvoked_shouldReturnAssessmentDetails() {

        ApiAssessmentDetail assessmentDetail = meansAssessmentSectionSummaryBuilder.
                getAssessmentDetail(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ));
        assertThat(assessmentDetail.getApplicantFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(assessmentDetail.getCriteriaDetailId()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_DETAILS_ID);
        assertThat(assessmentDetail.getAssessmentDescription()).isEqualTo(TestModelDataBuilder.TEST_DESCRIPTION);
        assertThat(assessmentDetail.getPartnerAmount()).isEqualTo(BigDecimal.valueOf(20.00));
        assertThat(assessmentDetail.getPartnerFrequency()).isEqualTo(Frequency.ANNUALLY);
        assertThat(assessmentDetail.getApplicantAmount()).isEqualTo(BigDecimal.valueOf(10.00));

    }

    @Test
    void givenValidAssessmentCriteriaDetail_whenBuildAssessmentDTOInvoked_shouldReturnAssessment() {

        AssessmentDTO assessmentDTO = meansAssessmentSectionSummaryBuilder.
                buildAssessmentDTO(TestModelDataBuilder.getAssessmentCriteriaDetailEntity(TestModelDataBuilder.TEST_SECTION),
                        TestModelDataBuilder.getAssessmentDetailsWithoutList());

        assertThat(assessmentDTO.getCriteriaDetailId()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_DETAILS_ID);
        assertThat(assessmentDTO.getSection()).isEqualTo(TestModelDataBuilder.TEST_SECTION);
        assertThat(assessmentDTO.getAssessmentDetailCode()).isEqualTo(TestModelDataBuilder.TEST_DETAIL_CODE);
        assertThat(assessmentDTO.getCriteriaDetailDescription()).isEqualTo(TestModelDataBuilder.TEST_DESCRIPTION);
        assertThat(assessmentDTO.getFinancialDetailId()).isEqualTo(TestModelDataBuilder.TEST_DETAIL_ID);
        assertThat(assessmentDTO.getApplicantAmount()).isEqualTo(BigDecimal.valueOf(1650.00));
        assertThat(assessmentDTO.getApplicantFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(assessmentDTO.getPartnerAmount()).isEqualTo(BigDecimal.valueOf(1650.00));
        assertThat(assessmentDTO.getPartnerFrequency()).isEqualTo(Frequency.TWO_WEEKLY);
        assertThat(assessmentDTO.getSequence()).isEqualTo(TEST_SEQ);

    }

    @Test
    void givenValidAssessmentAmt_whenGetAssessmentSectionSummaryTotalInvoked_shouldReturnAssessmentAmt() {

        BigDecimal assessmentAmt = meansAssessmentSectionSummaryBuilder
                .getAssessmentSectionSummaryTotal(TestModelDataBuilder.TEST_APPLICANT_VALUE, TEST_FREQUENCY);
        assertThat(assessmentAmt).isEqualTo(new BigDecimal("120.00"));
    }

    @Test
    void givenInvalidFrequency_whenGetAssessmentSectionSummaryTotalInvoked_shouldReturnZero() {

        BigDecimal assessmentAmt = meansAssessmentSectionSummaryBuilder
                .getAssessmentSectionSummaryTotal(TestModelDataBuilder.TEST_APPLICANT_VALUE, null);
        assertThat(assessmentAmt).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenAValidFinancialAssessment_whenBuildInitialAssessmentInvoked_shouldBuildInitialAssessment() throws Exception {

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        response.setInitialAssessment(new ApiInitialMeansAssessment());

        var expectedInitAssessment = TestModelDataBuilder.getApiInitialMeansAssessment(CurrentStatus.IN_PROGRESS, NewWorkReason.HR, ReviewType.NAFI);
        expectedInitAssessment.setAssessmentSectionSummary(new ArrayList<>(
                List.of(
                        TestModelDataBuilder.getAssessmentSectionSummary(Section.INITA.name(), AssessmentType.INIT))
        ));
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO(CurrentStatus.IN_PROGRESS.getStatus(), NewWorkReason.HR.getCode(),
                ReviewType.NAFI.getCode());
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        assessmentSectionSummaryList.add(TestModelDataBuilder.getAssessmentSectionSummary(Section.INITA.name(), AssessmentType.INIT));
        assessmentSectionSummaryList.add(TestModelDataBuilder.getAssessmentSectionSummary(Section.FULLA.name(), AssessmentType.FULL));
        Optional<AssessmentCriteriaEntity> criteriaEntity = Optional.of(TestModelDataBuilder.getAssessmentCriteriaEntity());
        meansAssessmentSectionSummaryBuilder.buildInitialAssessment(response, financialAssessmentDTO, assessmentSectionSummaryList, criteriaEntity);
        assertThat(objectMapper.writeValueAsString(response.getInitialAssessment())).isEqualTo(objectMapper.writeValueAsString(expectedInitAssessment));
    }

    @Test
    void givenEmptyCurrentStatus_whenBuildInitialAssessmentInvoked_shouldNotReturnAssessmentStatus() {

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        response.setInitialAssessment(new ApiInitialMeansAssessment());
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO(null, NewWorkReason.HR.getCode(),
                ReviewType.NAFI.getCode());
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        Optional<AssessmentCriteriaEntity> criteriaEntity = Optional.of(TestModelDataBuilder.getAssessmentCriteriaEntity());
        meansAssessmentSectionSummaryBuilder.buildInitialAssessment(response, financialAssessmentDTO, assessmentSectionSummaryList, criteriaEntity);
        assertThat(response.getInitialAssessment().getAssessmentStatus()).isNull();
    }

    @Test
    void givenEmptyWorkReason_whenBuildInitialAssessmentInvoked_shouldNotReturnNewWorkReason() {

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        response.setInitialAssessment(new ApiInitialMeansAssessment());
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO(CurrentStatus.IN_PROGRESS.getStatus(), null,
                ReviewType.NAFI.getCode());
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        Optional<AssessmentCriteriaEntity> criteriaEntity = Optional.of(TestModelDataBuilder.getAssessmentCriteriaEntity());
        meansAssessmentSectionSummaryBuilder.buildInitialAssessment(response, financialAssessmentDTO, assessmentSectionSummaryList, criteriaEntity);
        assertThat(response.getInitialAssessment().getNewWorkReason()).isNull();
    }

    @Test
    void givenEmptyReview_whenBuildInitialAssessmentInvoked_shouldNotReturnReviewType() {

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        response.setInitialAssessment(new ApiInitialMeansAssessment());
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO(CurrentStatus.IN_PROGRESS.getStatus(), NewWorkReason.HR.getCode(),
                null);
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        Optional<AssessmentCriteriaEntity> criteriaEntity = Optional.of(TestModelDataBuilder.getAssessmentCriteriaEntity());
        meansAssessmentSectionSummaryBuilder.buildInitialAssessment(response, financialAssessmentDTO, assessmentSectionSummaryList, criteriaEntity);
        assertThat(response.getInitialAssessment().getReviewType()).isNull();
    }

    @Test
    void givenEmptyAssessmentCriteriaEntity_whenBuildInitialAssessmentInvoked_shouldNotReturnThreshold() {

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        response.setInitialAssessment(new ApiInitialMeansAssessment());
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO(CurrentStatus.IN_PROGRESS.getStatus(), NewWorkReason.HR.getCode(),
                ReviewType.NAFI.getCode());
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        Optional<AssessmentCriteriaEntity> criteriaEntity = Optional.empty();
        meansAssessmentSectionSummaryBuilder.buildInitialAssessment(response, financialAssessmentDTO, assessmentSectionSummaryList, criteriaEntity);
        assertThat(response.getInitialAssessment().getLowerThreshold()).isNull();
        assertThat(response.getInitialAssessment().getUpperThreshold()).isNull();
    }

    @Test
    void givenAValidFinancialAssessment_whenBuildFullAssessmentInvoked_shouldBuildFullAssessment() throws Exception {

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        response.setFullAssessment(new ApiFullMeansAssessment());

        var expectedFullAssessment = TestModelDataBuilder.getApiFullAssessment(CurrentStatus.IN_PROGRESS);
        expectedFullAssessment.setAssessmentSectionSummary(new ArrayList<>(
                List.of(
                        TestModelDataBuilder.getAssessmentSectionSummary(Section.FULLA.name(), AssessmentType.FULL))
        ));
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO(CurrentStatus.IN_PROGRESS.getStatus(), NewWorkReason.HR.getCode(),
                ReviewType.NAFI.getCode());
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        assessmentSectionSummaryList.add(TestModelDataBuilder.getAssessmentSectionSummary(Section.INITA.name(), AssessmentType.INIT));
        assessmentSectionSummaryList.add(TestModelDataBuilder.getAssessmentSectionSummary(Section.FULLA.name(), AssessmentType.FULL));
        Optional<AssessmentCriteriaEntity> criteriaEntity = Optional.of(TestModelDataBuilder.getAssessmentCriteriaEntity());
        meansAssessmentSectionSummaryBuilder.buildFullAssessment(response, financialAssessmentDTO, assessmentSectionSummaryList, criteriaEntity);
        assertThat(objectMapper.writeValueAsString(response.getFullAssessment())).isEqualTo(objectMapper.writeValueAsString(expectedFullAssessment));
    }

    @Test
    void givenEmptyCurrentStatus_whenBuildFullAssessmentInvoked_shouldNotReturnCurrentStatus() throws Exception {

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        response.setFullAssessment(new ApiFullMeansAssessment());

        var expectedFullAssessment = TestModelDataBuilder.getApiFullAssessment(null);
        expectedFullAssessment.setAssessmentSectionSummary(new ArrayList<>(
                List.of(
                        TestModelDataBuilder.getAssessmentSectionSummary(Section.FULLA.name(), AssessmentType.FULL))
        ));
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO(null, NewWorkReason.HR.getCode(),
                ReviewType.NAFI.getCode());
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        assessmentSectionSummaryList.add(TestModelDataBuilder.getAssessmentSectionSummary(Section.INITA.name(), AssessmentType.INIT));
        assessmentSectionSummaryList.add(TestModelDataBuilder.getAssessmentSectionSummary(Section.FULLA.name(), AssessmentType.FULL));
        Optional<AssessmentCriteriaEntity> criteriaEntity = Optional.of(TestModelDataBuilder.getAssessmentCriteriaEntity());
        meansAssessmentSectionSummaryBuilder.buildFullAssessment(response, financialAssessmentDTO, assessmentSectionSummaryList, criteriaEntity);
        assertThat(objectMapper.writeValueAsString(response.getFullAssessment())).isEqualTo(objectMapper.writeValueAsString(expectedFullAssessment));
    }

    @Test
    void givenEmptyAssessmentCriteriaEntity_whenBuildFullAssessmentInvoked_shouldNotReturnThreshold() throws Exception {

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        response.setFullAssessment(new ApiFullMeansAssessment());

        var expectedFullAssessment = TestModelDataBuilder.getApiFullAssessment(CurrentStatus.IN_PROGRESS);
        expectedFullAssessment.setAssessmentSectionSummary(new ArrayList<>(
                List.of(
                        TestModelDataBuilder.getAssessmentSectionSummary(Section.FULLA.name(), AssessmentType.FULL))
        ));
        expectedFullAssessment.setThreshold(null);
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO(CurrentStatus.IN_PROGRESS.getStatus(), NewWorkReason.HR.getCode(),
                ReviewType.NAFI.getCode());
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        assessmentSectionSummaryList.add(TestModelDataBuilder.getAssessmentSectionSummary(Section.INITA.name(), AssessmentType.INIT));
        assessmentSectionSummaryList.add(TestModelDataBuilder.getAssessmentSectionSummary(Section.FULLA.name(), AssessmentType.FULL));
        Optional<AssessmentCriteriaEntity> criteriaEntity = Optional.empty();
        meansAssessmentSectionSummaryBuilder.buildFullAssessment(response, financialAssessmentDTO, assessmentSectionSummaryList, criteriaEntity);
        assertThat(objectMapper.writeValueAsString(response.getFullAssessment())).isEqualTo(objectMapper.writeValueAsString(expectedFullAssessment));
    }

}