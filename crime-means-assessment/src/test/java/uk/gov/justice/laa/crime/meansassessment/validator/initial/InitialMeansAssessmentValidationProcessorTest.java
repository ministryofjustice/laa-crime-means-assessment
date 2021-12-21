package uk.gov.justice.laa.crime.meansassessment.validator.initial;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InitialMeansAssessmentValidationProcessorTest {
    private static final boolean IS_VALID = true;

    @InjectMocks
    private InitialMeansAssessmentValidationProcessor initialMeansAssessmentValidationProcessor;

    @Mock
    private CreateMeansAssessmentResponseValidator createMeansAssessmentResponseValidator;

    @Test
    public void givenValidCreateMeansAssessmentResponse_thenValidationProcessorReturnsEmptyOptional(){
        //given
        var validCreateMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(IS_VALID);
        when(createMeansAssessmentResponseValidator.validate(validCreateMeansAssessmentResponse))
                .thenReturn(Optional.empty());

        var emptyOptional = initialMeansAssessmentValidationProcessor.validate(validCreateMeansAssessmentResponse);
        assertTrue(emptyOptional.isEmpty());
    }

    @Test(expected= ValidationException.class)
    public void givenInvalidCreateMeansAssessmentResponse_thenValidationProcessorThrowsException(){
        //given
        var validCreateMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(!IS_VALID);
        when(createMeansAssessmentResponseValidator.validate(validCreateMeansAssessmentResponse))
                .thenThrow(new ValidationException());

        var emptyOptional = initialMeansAssessmentValidationProcessor.validate(validCreateMeansAssessmentResponse);
    }
}