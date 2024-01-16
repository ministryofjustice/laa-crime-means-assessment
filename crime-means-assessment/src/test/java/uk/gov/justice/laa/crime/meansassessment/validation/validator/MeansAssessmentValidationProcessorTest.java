package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uk.gov.justice.laa.crime.meansassessment.validation.validator.MeansAssessmentValidationProcessor.*;

@ExtendWith(MockitoExtension.class)
class MeansAssessmentValidationProcessorTest {

    MeansAssessmentRequestDTO createMeansAssessmentRequest;
    MeansAssessmentRequestDTO fullAssessment;
    @Mock
    private MeansAssessmentValidationService meansAssessmentValidationService;
    @Mock
    private InitAssessmentValidator initAssessmentValidator;
    @Mock
    private FullAssessmentValidator fullAssessmentValidator;
    @InjectMocks
    private MeansAssessmentValidationProcessor meansAssessmentValidationProcessor;

    @BeforeEach
    void setup() {
        createMeansAssessmentRequest = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        fullAssessment = MeansAssessmentRequestDTO.builder().assessmentType(AssessmentType.FULL).repId(1000).build();
    }

    @Test
    void givenCreateInitAssessmentRequest_whenAllValidationsPass_thenValidatorDoesNotThrowException() {
        when(meansAssessmentValidationService.isNewWorkReasonValid(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        buildMockForRoleActionValidAndRepOrderReserved();

        when(initAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        Optional<Void> result =
                meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, RequestType.CREATE);

        verify(meansAssessmentValidationService).isRoleActionValid(eq(createMeansAssessmentRequest), anyString());
        verify(meansAssessmentValidationService).isRepOrderReserved(createMeansAssessmentRequest);
        verify(meansAssessmentValidationService).isOutstandingAssessment(createMeansAssessmentRequest);
        verify(meansAssessmentValidationService).isNewWorkReasonValid(createMeansAssessmentRequest);
        verify(initAssessmentValidator).validate(createMeansAssessmentRequest);

        verify(fullAssessmentValidator, never()).validate(fullAssessment);

        assertThat(result).isEmpty();
    }

    @Test
    void givenUpdateFullAssessmentRequest_whenAllValidationsPass_thenValidatorDoesNotThrowException() {
        buildMockForRoleActionValidAndRepOrderReserved();

        when(fullAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class)
        )).thenReturn(Boolean.TRUE);

        fullAssessment.setFullAssessmentDate(LocalDateTime.now());
        Optional<Void> result = meansAssessmentValidationProcessor.validate(fullAssessment, RequestType.UPDATE);

        verify(meansAssessmentValidationService).isRoleActionValid(eq(fullAssessment), anyString());
        verify(meansAssessmentValidationService).isRepOrderReserved(fullAssessment);
        verify(fullAssessmentValidator).validate(fullAssessment);

        verify(initAssessmentValidator, never()).validate(createMeansAssessmentRequest);
        verify(meansAssessmentValidationService, never()).isOutstandingAssessment(createMeansAssessmentRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void givenInitAssessmentValidationFailure_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isNewWorkReasonValid(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        buildMockForRoleActionValidAndRepOrderReserved();

        assertThatThrownBy(
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, RequestType.UPDATE)
        ).isInstanceOf(ValidationException.class).hasMessage(MSG_INCORRECT_REVIEW_TYPE);
    }

    @Test
    void givenFullAssessmentValidationFailure_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(fullAssessmentValidator.validate(
                any(MeansAssessmentRequestDTO.class)
        )).thenReturn(Boolean.FALSE);

        buildMockForRoleActionValidAndRepOrderReserved();

        assertThatThrownBy(
                () -> meansAssessmentValidationProcessor.validate(fullAssessment, RequestType.UPDATE)
        ).isInstanceOf(ValidationException.class).hasMessage(MSG_FULL_ASSESSMENT_DATE_REQUIRED);
    }

    @Test
    void givenInvalidNewWorkReason_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isNewWorkReasonValid(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        buildMockForRoleActionValidAndRepOrderReserved();

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, RequestType.CREATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_NEW_WORK_REASON_IS_NOT_VALID);
    }

    @Test
    void givenNullRepId_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        createMeansAssessmentRequest = TestModelDataBuilder.getMeansAssessmentRequestDTO(false);
        ValidationException validationException = assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, RequestType.UPDATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_REP_ID_REQUIRED);
    }

    @Test
    void givenNegativeRepId_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        createMeansAssessmentRequest.setRepId(-1000);
        ValidationException validationException = assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, RequestType.UPDATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_REP_ID_REQUIRED);
    }

    @Test
    void givenInvalidRoleReservation_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isRoleActionValid(
                any(MeansAssessmentRequestDTO.class), any(String.class))
        ).thenReturn(Boolean.TRUE);

        when(meansAssessmentValidationService.isRepOrderReserved(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, RequestType.UPDATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_RECORD_NOT_RESERVED_BY_CURRENT_USER);
    }

    @Test
    void givenInvalidRoleAction_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        when(meansAssessmentValidationService.isRoleActionValid(
                any(MeansAssessmentRequestDTO.class), any(String.class))
        ).thenReturn(Boolean.FALSE);

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, RequestType.UPDATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_ROLE_ACTION_IS_NOT_VALID);
    }

    @Test
    void givenOutstandingAssessment_whenValidateIsInvoked_thenCorrectExceptionIsThrown() {
        buildMockForRoleActionValidAndRepOrderReserved();

        when(meansAssessmentValidationService.isOutstandingAssessment(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> meansAssessmentValidationProcessor.validate(createMeansAssessmentRequest, RequestType.CREATE));
        assertThat(validationException.getMessage()).isEqualTo(MSG_INCOMPLETE_ASSESSMENT_FOUND);
    }

    private void buildMockForRoleActionValidAndRepOrderReserved() {
        when(meansAssessmentValidationService.isRoleActionValid(
                any(MeansAssessmentRequestDTO.class), any(String.class))
        ).thenReturn(Boolean.TRUE);

        when(meansAssessmentValidationService.isRepOrderReserved(
                any(MeansAssessmentRequestDTO.class))
        ).thenReturn(Boolean.TRUE);
    }

}