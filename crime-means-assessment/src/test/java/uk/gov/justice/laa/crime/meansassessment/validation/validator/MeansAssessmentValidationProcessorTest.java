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
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uk.gov.justice.laa.crime.meansassessment.validation.validator.MeansAssessmentValidationProcessor.*;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentValidationProcessorTest {

    @Mock
    private MeansAssessmentValidationService meansAssessmentValidationService;

    @Mock
    private InitAssessmentValidator initAssessmentValidator;

    @Mock
    private FullAssessmentValidator fullAssessmentValidator;

    @InjectMocks
    private MeansAssessmentValidationProcessor meansAssessmentValidationProcessor;

    MeansAssessmentRequestDTO createMeansAssessmentRequest;

    MeansAssessmentRequestDTO fullAssessment;

    @Before
    public void setup() {

        when(meansAssessmentValidationService.isNewWorkReasonValid(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        when(meansAssessmentValidationService.isRoleActionValid(
                any(MeansAssessmentRequestDTO.class), any(String.class))
        ).thenReturn(Boolean.TRUE);

        when(meansAssessmentValidationService.isRepOrderReserved(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        when(initAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        when(fullAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class)
        )).thenReturn(Boolean.TRUE);

        createMeansAssessmentRequest = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        fullAssessment = MeansAssessmentRequestDTO.builder().assessmentType(AssessmentType.FULL).repId(1000).build();
    }

    @Test
    public void givenCreateInitAssessmentRequest_whenAllValidationsPass_thenValidatorDoesNotThrowException() {
        Optional<Void> result =
                meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.CREATE);

        verify(meansAssessmentValidationService).isRoleActionValid(eq(createMeansAssessmentRequest), anyString());
        verify(meansAssessmentValidationService).isRepOrderReserved(createMeansAssessmentRequest);
        verify(meansAssessmentValidationService).isOutstandingAssessment(createMeansAssessmentRequest);
        verify(meansAssessmentValidationService).isNewWorkReasonValid(createMeansAssessmentRequest);
        verify(initAssessmentValidator).validate(createMeansAssessmentRequest);

        verify(fullAssessmentValidator, never()).validate(fullAssessment);
        verify(meansAssessmentValidationService, never()).isAssessmentModifiedByAnotherUser(createMeansAssessmentRequest);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenUpdateFullAssessmentRequest_whenAllValidationsPass_thenValidatorDoesNotThrowException() {
        fullAssessment.setFullAssessmentDate(LocalDateTime.now());
        Optional<Void> result = meansAssessmentValidationProcessor.validate(fullAssessment, AssessmentRequestType.UPDATE);

        verify(meansAssessmentValidationService).isRoleActionValid(eq(fullAssessment), anyString());
        verify(meansAssessmentValidationService).isRepOrderReserved(fullAssessment);
        verify(meansAssessmentValidationService).isAssessmentModifiedByAnotherUser(fullAssessment);
        verify(fullAssessmentValidator).validate(fullAssessment);

        verify(initAssessmentValidator, never()).validate(createMeansAssessmentRequest);
        verify(meansAssessmentValidationService, never()).isOutstandingAssessment(createMeansAssessmentRequest);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenInitAssessmentValidationFailure_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(initAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        assertThatThrownBy(
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.UPDATE)
        ).isInstanceOf(ValidationException.class).hasMessage(MSG_INCORRECT_REVIEW_TYPE);
    }

    @Test
    public void givenFullAssessmentValidationFailure_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(fullAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class)
        )).thenReturn(Boolean.FALSE);

        assertThatThrownBy(
                () -> meansAssessmentValidationProcessor.validate(fullAssessment, AssessmentRequestType.UPDATE)
        ).isInstanceOf(ValidationException.class).hasMessage(MSG_FULL_ASSESSMENT_DATE_REQUIRED);
    }

    @Test
    public void givenInvalidNewWorkReason_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isNewWorkReasonValid(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.CREATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_NEW_WORK_REASON_IS_NOT_VALID);
    }

    @Test
    public void givenNullRepId_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        createMeansAssessmentRequest = TestModelDataBuilder.getMeansAssessmentRequestDTO(false);
        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.UPDATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_REP_ID_REQUIRED);
    }

    @Test
    public void givenNegativeRepId_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        createMeansAssessmentRequest.setRepId(-1000);
        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.UPDATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_REP_ID_REQUIRED);
    }

    @Test
    public void givenInvalidRoleReservation_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isRepOrderReserved(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.UPDATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_RECORD_NOT_RESERVED_BY_CURRENT_USER);
    }

    @Test
    public void givenInvalidRoleAction_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isRoleActionValid(
                any(MeansAssessmentRequestDTO.class), any(String.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.UPDATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_ROLE_ACTION_IS_NOT_VALID);
    }

    @Test
    public void givenOutstandingAssessment_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isOutstandingAssessment(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        ValidationException validationException = Assert.assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.CREATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_INCOMPLETE_ASSESSMENT_FOUND);
    }

    @Test
    public void givenInvalidFinancialAssessmentTimeStamp_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isAssessmentModifiedByAnotherUser(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        assertThatThrownBy(
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, AssessmentRequestType.UPDATE)
        ).isInstanceOf(ValidationException.class).hasMessageContaining(ASSESSMENT_MODIFIED_BY_ANOTHER_USER);
    }
}