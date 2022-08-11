package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FullAssessmentValidatorTest {

    private final FullAssessmentValidator fullAssessmentValidator = new FullAssessmentValidator();

    @Test
    public void givenFullAssessmentDate_whenValidateIsInvoked_thenValidationPasses() {
        assertThat(fullAssessmentValidator.validate(TestModelDataBuilder.getMeansAssessmentRequestDTO(true)))
                .isEqualTo(Boolean.TRUE);
    }

    @Test
    public void givenNullFullAssessmentDate_whenValidateIsInvoked_thenValidationFails() {
        MeansAssessmentRequestDTO meansAssessment = MeansAssessmentRequestDTO.builder()
                .fullAssessmentDate(null)
                .build();
        assertThat(fullAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.FALSE);
    }
}
