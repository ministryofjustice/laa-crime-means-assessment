package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class AssessmentCriteriaRepositoryTest {

    private AssessmentCriteriaEntity assessmentCriteriaEntity;

    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;

    @BeforeEach
    void setup() {
        Integer latestCriteriaId = 34;
        assessmentCriteriaEntity =
                assessmentCriteriaRepository.findById(latestCriteriaId).orElse(null);
        if (assessmentCriteriaEntity == null) {
            throw new RuntimeException("Assessment Criteria not found.");
        }
    }

    @Test
    void givenAssessmentCriteriaIsPopulated_WhenValidDateIsProvided_ThenAssessmentCriteriaShouldBeReturned() {
        // when valid Date is given
        AssessmentCriteriaEntity result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(
                TestModelDataBuilder.TEST_DATE_FROM.plusHours(1));
        // then at least one result is returned
        assertThat(result).isEqualTo(assessmentCriteriaEntity);
    }

    @Test
    void
            givenAssessmentCriteriaIsPopulated_WhenValidDateIsProvidedAndDateToIsNull_ThenAssessmentCriteriaShouldBeReturned() {
        // when valid Date is given
        AssessmentCriteriaEntity result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(
                TestModelDataBuilder.TEST_DATE_FROM.plusHours(1));
        // then at least one result is returned
        assertThat(result).isEqualTo(assessmentCriteriaEntity);
        assertThat(result.getDateTo()).isNull();
    }

    @Test
    void givenAssessmentCriteriaIsPopulated_WhenInvalidDateIsProvided_ThenAssessmentCriteriaShouldNotBeReturned() {
        // when invalid Date is given
        AssessmentCriteriaEntity result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(
                TestModelDataBuilder.TEST_DATE_FROM.minusYears(100));
        // then no results are returned
        assertThat(result).isNull();
    }

    @Test
    void givenAssessmentCriteriaIsPopulated_WhenOldValidDateIsProvided_ThenAssessmentCriteriaShouldBeReturned() {
        AssessmentCriteriaEntity result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(
                assessmentCriteriaEntity.getDateFrom().minusDays(1));
        assertThat(result).isNotEqualTo(assessmentCriteriaEntity);
    }

    @AfterEach
    void tearDown() {
        if (assessmentCriteriaEntity != null) {
            assessmentCriteriaRepository.delete(assessmentCriteriaEntity);
            assessmentCriteriaEntity = null;
        }
    }
}
