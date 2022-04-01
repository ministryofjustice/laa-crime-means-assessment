package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.meansassessment.validation.validator.MeansAssessmentValidationProcessor.*;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentValidationProcessorTest {

    @Mock
    private MeansAssessmentValidationService meansAssessmentValidationService;

    @Mock
    private InitAssessmentValidator initAssessmentValidator;

    @InjectMocks
    private MeansAssessmentValidationProcessor meansAssessmentValidationProcessor;

    MeansAssessmentRequestDTO createMeansAssessmentRequest;

    @Before
    public void setup() {

        when(meansAssessmentValidationService.validateOutstandingAssessments(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        when(meansAssessmentValidationService.validateNewWorkReason(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        when(meansAssessmentValidationService.validateRoleAction(
                any(MeansAssessmentRequestDTO.class), any(String.class))
        ).thenReturn(Boolean.TRUE);

        when(meansAssessmentValidationService.validateRoleReservation(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        when(meansAssessmentValidationService.isRepIdPresentForCreateAssessment(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        when(initAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        createMeansAssessmentRequest = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
    }

    @Test
    public void givenValidRequest_whenAllValidationsPass_thenValidatorDoesNotThrowException() {

        Optional<Void> result = meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void givenInitAssessmentRequest_whenInitAssessmentValidatorFails_thenValidatorThrowsException() {
        when(initAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        assertThatThrownBy(
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest)
        ).isInstanceOf(ValidationException.class).hasMessage(MSG_INCORRECT_REVIEW_TYPE);
    }

    @Test
    public void givenValidRequest_whenNworValidationFails_thenValidatorThrowsException() {

        when(meansAssessmentValidationService.validateNewWorkReason(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest));
        assertThat(validationException.getMessage()).isEqualTo(MSG_NEW_WORK_REASON_IS_NOT_VALID);
    }

    @Test
    public void givenValidRequest_whenRepIdValidationFails_thenValidatorThrowsException() {

        when(meansAssessmentValidationService.isRepIdPresentForCreateAssessment(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest));
        assertThat(validationException.getMessage()).isEqualTo(MSG_REP_ID_REQUIRED);
    }

    @Test
    public void givenValidRequest_whenRoleReservationValidationFails_thenValidatorThrowsException() {

        when(meansAssessmentValidationService.validateRoleReservation(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest));
        assertThat(validationException.getMessage()).isEqualTo(MSG_RECORD_NOT_RESERVED_BY_CURRENT_USER);
    }

    @Test
    public void givenValidRequest_whenRoleActionValidationFails_thenValidatorThrowsException() {

        when(meansAssessmentValidationService.validateRoleAction(
                any(MeansAssessmentRequestDTO.class), any(String.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest));
        assertThat(validationException.getMessage()).isEqualTo(MSG_ROLE_ACTION_IS_NOT_VALID);
    }

    @Test
    public void givenValidRequest_whenOutstandingAssessmentsValidationFails_thenValidatorThrowsException() {

        when(meansAssessmentValidationService.validateOutstandingAssessments(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest));
        assertThat(validationException.getMessage()).isEqualTo(MSG_INCOMPLETE_ASSESSMENT_FOUND);
    }
}