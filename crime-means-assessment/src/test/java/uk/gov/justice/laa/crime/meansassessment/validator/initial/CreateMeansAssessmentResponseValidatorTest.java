package uk.gov.justice.laa.crime.meansassessment.validator.initial;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CreateMeansAssessmentResponseValidatorTest {
    private static final boolean IS_VALID = true;

    @InjectMocks
    private CreateMeansAssessmentResponseValidator createMeansAssessmentResponseValidator;

    @Test
    public void givenThatValidCreateMeansAssessmentResponse_thenValidatorReturnsEmptyOptional(){
        var validCreateMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(IS_VALID);
       var optionalReturned= createMeansAssessmentResponseValidator.validate(validCreateMeansAssessmentResponse);

        assertTrue(optionalReturned.isEmpty());
    }
    @Test(expected= ValidationException.class)
    public void givenInvalidCreateMeansAssessmentResponse_thenValidatorThrowsException(){
        var invalidCreateMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(!IS_VALID);
        var optionalReturned= createMeansAssessmentResponseValidator.validate(invalidCreateMeansAssessmentResponse);
    }

}