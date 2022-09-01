package uk.gov.justice.laa.crime.meansassessment.builder;


import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentDetailDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentSectionSummaryDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MeansAssessmentSectionSummaryBuilderTest {

    private MeansAssessmentSectionSummaryBuilder  meansAssessmentSectionSummaryBuilder
            = new MeansAssessmentSectionSummaryBuilder();


    @Test
    public void givenInvalidASectionAssessment_whenBuildInvoked_shouldReturnEmpty() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_SECTION, TEST_SEQ));
        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryDTOS =  meansAssessmentSectionSummaryBuilder.build(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS.isEmpty()).isTrue();
    }

    @Test
    public void givenInitASectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ+TEST_SEQ));

        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryDTOS =  meansAssessmentSectionSummaryBuilder.build(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(1).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.INIT);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_INITA);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(BigDecimal.valueOf(280.00));

    }

    @Test
    public void givenInitBSectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITB, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITB, TEST_SEQ+TEST_SEQ));

        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryDTOS =  meansAssessmentSectionSummaryBuilder.build(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(1).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.INIT);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_INITB);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(BigDecimal.valueOf(280.00));

    }

    @Test
    public void givenBothInitAAndIntBSectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ+TEST_SEQ));

        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITB, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITB, TEST_SEQ+TEST_SEQ));


        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryDTOS =  meansAssessmentSectionSummaryBuilder.build(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.INIT);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_INITA);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(BigDecimal.valueOf(280.00));

        assertThat(assessmentSectionSummaryDTOS.get(1).getAssessmentType()).isEqualTo(AssessmentType.INIT);
        assertThat(assessmentSectionSummaryDTOS.get(1).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_INITB);
        assertThat(assessmentSectionSummaryDTOS.get(1).getAnnualTotal()).isEqualTo(BigDecimal.valueOf(280.00));

    }

    @Test
    public void givenFullASectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLA, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLA, TEST_SEQ+TEST_SEQ));

        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryDTOS =  meansAssessmentSectionSummaryBuilder.build(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(1).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.FULL);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_FULLA);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(BigDecimal.valueOf(280.00));

    }

    @Test
    public void givenBothFullAAndFullBSectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLA, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLA, TEST_SEQ+TEST_SEQ));

        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLB, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLB, TEST_SEQ+TEST_SEQ));


        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryDTOS =  meansAssessmentSectionSummaryBuilder.build(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(assessmentSectionSummaryDTOS.size()).isEqualTo(2);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.FULL);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size()).isEqualTo(2);
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_FULLA);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(BigDecimal.valueOf(280.00));

        assertThat(assessmentSectionSummaryDTOS.get(1).getAssessmentType()).isEqualTo(AssessmentType.FULL);
        assertThat(assessmentSectionSummaryDTOS.get(1).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_FULLB);
        assertThat(assessmentSectionSummaryDTOS.get(1).getAnnualTotal()).isEqualTo(BigDecimal.valueOf(280.00));

    }

    @Test
    public void givenFullBSectionAssessment_whenBuildInvoked_shouldReturnInitialAssessmentSummary() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLB, TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_FULLB, TEST_SEQ+TEST_SEQ));

        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryDTOS =  meansAssessmentSectionSummaryBuilder.build(assessmentDTOList);
        assertThat(assessmentSectionSummaryDTOS).isNotNull();
        assertThat(1).isEqualTo(assessmentSectionSummaryDTOS.size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getAssessmentType()).isEqualTo(AssessmentType.FULL);
        assertThat(2).isEqualTo(assessmentSectionSummaryDTOS.get(0).getAssessmentDetails().size());
        assertThat(assessmentSectionSummaryDTOS.get(0).getSection()).isEqualTo(TEST_ASSESSMENT_SECTION_FULLB);
        assertThat(assessmentSectionSummaryDTOS.get(0).getAnnualTotal()).isEqualTo(BigDecimal.valueOf(280.00));

    }


    @Test
    public void givenValidAssessment_whenGetAssessmentDetailInvoked_shouldReturnAssessmentDetails() {

        AssessmentDetailDTO assessmentDetailDTO = meansAssessmentSectionSummaryBuilder.
                getAssessmentDetail(TestModelDataBuilder.getAssessmentDTO(TEST_ASSESSMENT_SECTION_INITA, TEST_SEQ));
        assertThat(assessmentDetailDTO.getApplicantFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(assessmentDetailDTO.getCriteriaDetailId()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_DETAILS_ID);
        assertThat(assessmentDetailDTO.getAssessmentDescription()).isEqualTo(TestModelDataBuilder.TEST_DESCRIPTION);
        assertThat(assessmentDetailDTO.getPartnerAmount()).isEqualTo(BigDecimal.valueOf(20.00));
        assertThat(assessmentDetailDTO.getPartnerFrequency()).isEqualTo(Frequency.ANNUALLY);
        assertThat(assessmentDetailDTO.getApplicantAmount()).isEqualTo(BigDecimal.valueOf(10.00));

    }

    @Test
    public void givenValidAssessmentCriteriaDetail_whenBuildAssessmentDTOInvoked_shouldReturnAssessment() {

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
    public void givenValidAssessmentAmt_whenGetAssessmentSectionSummaryTotalInvoked_shouldReturnAssessmentAmt() {

        BigDecimal assessmentAmt = meansAssessmentSectionSummaryBuilder
                .getAssessmentSectionSummaryTotal(TestModelDataBuilder.TEST_APPLICANT_VALUE, TEST_FREQUENCY);
        assertThat(assessmentAmt).isEqualTo(BigDecimal.valueOf(120.00));
    }

    @Test
    public void givenInvalidFrequence_whenGetAssessmentSectionSummaryTotalInvoked_shouldReturnZero() {

        BigDecimal assessmentAmt = meansAssessmentSectionSummaryBuilder
                .getAssessmentSectionSummaryTotal(TestModelDataBuilder.TEST_APPLICANT_VALUE, null);
        assertThat(assessmentAmt).isEqualTo(BigDecimal.ZERO);
    }

}