package uk.gov.justice.laa.crime.meansassessment.validation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason;
import uk.gov.justice.laa.crime.meansassessment.util.MockMaatApiConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeansAssessmentValidationServiceTest {

    private static final AuthorizationResponseDTO TRUE_AUTH_RESPONSE =
            AuthorizationResponseDTO.builder().result(true).build();
    private static final AuthorizationResponseDTO FALSE_AUTH_RESPONSE =
            AuthorizationResponseDTO.builder().result(false).build();
    private static final OutstandingAssessmentResultDTO NO_OUTSTANDING_ASSESSMENT =
            OutstandingAssessmentResultDTO.builder().build();
    private static final OutstandingAssessmentResultDTO IS_OUTSTANDING_ASSESSMENT =
            OutstandingAssessmentResultDTO.builder().outstandingAssessments(true).build();
    @Mock
    RestAPIClient maatAPIClient;
    private MeansAssessmentRequestDTO requestDTO;

    @InjectMocks
    private MeansAssessmentValidationService meansAssessmentValidationService;
    @Spy
    private MaatApiConfiguration maatApiConfiguration = MockMaatApiConfiguration.getConfiguration(1000);

    @BeforeEach
    void setup() {
        requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
    }

    @Test
    void whenGetUserIdFromRequestIsCalled_thenUserIdIsReturned() {
        assertThat(meansAssessmentValidationService.getUserIdFromRequest(requestDTO))
                .isEqualTo(TestModelDataBuilder.TEST_USER);
    }

    @Test
    void givenInvalidNewWorkReason_whenIsNewWorkReasonValidIsInvoked_thenFalseIsReturned() {
        when(maatAPIClient.get(
                eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {
                }), anyString(), anyString(), anyString()))
                .thenReturn(FALSE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isFalse();
    }

    @Test
    void givenValidNewWorkReason_whenIsNewWorkReasonValidIsInvoked_thenTrueIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {
        }), anyString(), anyString(), anyString()))
                .thenReturn(TRUE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isTrue();
    }

    @Test
    void givenNullNewWorkReason_whenIsNewWorkReasonValidIsInvoked_thenFalseIsReturned() {
        requestDTO.setNewWorkReason(NewWorkReason.getFrom(""));
        assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isFalse();
    }

    @Test
    void givenBlankUserId_whenIsRoleActionValidIsInvoked_thenFalseIsReturned() {
        requestDTO.getUserSession().setUserName("");
        assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, "FMA")).isFalse();
    }

    @Test
    void givenBlankAction_whenIsRoleActionValidIsInvoked_thenFalseIsReturned() {
        assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, "")).isFalse();
    }

    @Test
    void givenInvalidRoleAction_whenIsRoleActionValidIsInvoked_thenFalseIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {
        }), anyString(), anyString(), anyString()))
                .thenReturn(FALSE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, "FMA")).isFalse();
    }

    @Test
    void givenValidRoleAction_whenIsRoleActionValidIsInvoked_thenTrueIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {
        }), anyString(), anyString(), anyString()))
                .thenReturn(TRUE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, "FMA")).isTrue();
    }

    @Test
    void givenValidReservation_whenIsRepOrderReserved_thenTrueIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {
        }), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(TRUE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isTrue();
    }

    @Test
    void givenInvalidReservation_whenIsRepOrderReserved_thenFalseIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {
        }), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(FALSE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
    }

    @Test
    void givenBlankUserIdInRequest_whenIsRepOrderReserved_thenFalseIsReturned() {
        requestDTO.getUserSession().setUserName("");
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
    }

    @Test
    void givenBlankSessionIDInRequest_whenIsRepOrderReserved_thenFalseIsReturned() {
        requestDTO.getUserSession().setSessionId("");
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
    }

    @Test
    void givenNullRepIdInRequest_whenIsRepOrderReserved_thenFalseIsReturned() {
        requestDTO.setRepId(null);
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
    }

    @Test
    void givenAnOutstandingAssessment_whenIsOutstandingAssessmentIsInvoked_thenTrueIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<OutstandingAssessmentResultDTO>() {
        }), anyString(), any()))
                .thenReturn(IS_OUTSTANDING_ASSESSMENT);
        assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isTrue();
    }

    @Test
    void givenNoOutstandingAssessments_whenIsOutstandingAssessmentIsInvoked_thenFalseIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<OutstandingAssessmentResultDTO>() {
        }), anyString(), any()))
                .thenReturn(NO_OUTSTANDING_ASSESSMENT);
        assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isFalse();
    }

    @Test
    void givenNoRepId_whenIsOutstandingAssessmentIsInvoked_thenFalseIsReturned() {
        requestDTO.setRepId(null);
        assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isFalse();
    }
}
