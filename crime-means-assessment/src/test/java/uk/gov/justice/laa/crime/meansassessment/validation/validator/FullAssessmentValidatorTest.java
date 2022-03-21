package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FullAssessmentValidatorTest {

    private final FullAssessmentValidator fullAssessmentValidator = new FullAssessmentValidator();

    private final ApiCreateMeansAssessmentRequest meansAssessment =
            TestModelDataBuilder.getCreateMeansAssessmentRequest(true);

    @Test
    public void givenFullAssessmentDateIsPresent_whenValidateIsInvoked_thenValidationPasses() {
        assertThat(fullAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void givenFullAssessmentDateNotPresent_whenValidateIsInvoked_thenValidationFails() {
        meansAssessment.setFullAssessmentDate(null);
        assertThat(fullAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.FALSE);
    }
}
