package uk.gov.justice.laa.crime.meansassessment.validation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason;
import uk.gov.justice.laa.crime.meansassessment.util.MockMaatApiConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentValidationServiceTest {

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
    @Mock
    private MaatCourtDataService maatCourtDataService;
    @InjectMocks
    private MeansAssessmentValidationService meansAssessmentValidationService;
    @Spy
    private MaatApiConfiguration maatApiConfiguration = MockMaatApiConfiguration.getConfiguration(1000);

    @Before
    public void setup() {
        requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
    }

    @Test
    public void whenGetUserIdFromRequestIsCalled_thenUserIdIsReturned() {
        assertThat(meansAssessmentValidationService.getUserIdFromRequest(requestDTO))
                .isEqualTo(TestModelDataBuilder.TEST_USER);
    }

    @Test
    public void givenInvalidNewWorkReason_whenIsNewWorkReasonValidIsInvoked_thenFalseIsReturned() {
        when(maatAPIClient.get(
                eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {}), anyString(), anyMap(), any()))
                .thenReturn(FALSE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isFalse();
    }

    @Test
    public void givenValidNewWorkReason_whenIsNewWorkReasonValidIsInvoked_thenTrueIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {}), anyString(), anyMap(), any()))
                .thenReturn(TRUE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isTrue();
    }

    @Test
    public void givenNullNewWorkReason_whenIsNewWorkReasonValidIsInvoked_thenFalseIsReturned() {
        requestDTO.setNewWorkReason(NewWorkReason.getFrom(""));
        assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isFalse();
    }

    @Test
    public void givenBlankUserId_whenIsRoleActionValidIsInvoked_thenFalseIsReturned() {
        requestDTO.getUserSession().setUserName("");
        assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, "FMA")).isFalse();
    }

    @Test
    public void givenBlankAction_whenIsRoleActionValidIsInvoked_thenFalseIsReturned() {
        assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, "")).isFalse();
    }

    @Test
    public void givenInvalidRoleAction_whenIsRoleActionValidIsInvoked_thenFalseIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {}), anyString(), anyMap(), any()))
                .thenReturn(FALSE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, "FMA")).isFalse();
    }

    @Test
    public void givenValidRoleAction_whenIsRoleActionValidIsInvoked_thenTrueIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {}), anyString(), anyMap(), any()))
                .thenReturn(TRUE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, "FMA")).isTrue();
    }

    @Test
    public void givenValidReservation_whenIsRepOrderReserved_thenTrueIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {}), anyString(), anyMap(), any()))
                .thenReturn(TRUE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isTrue();
    }

    @Test
    public void givenInvalidReservation_whenIsRepOrderReserved_thenFalseIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<AuthorizationResponseDTO>() {}), anyString(), anyMap(), any()))
                .thenReturn(FALSE_AUTH_RESPONSE);
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
    }

    @Test
    public void givenBlankUserIdInRequest_whenIsRepOrderReserved_thenFalseIsReturned() {
        requestDTO.getUserSession().setUserName("");
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
    }

    @Test
    public void givenBlankSessionIDInRequest_whenIsRepOrderReserved_thenFalseIsReturned() {
        requestDTO.getUserSession().setSessionId("");
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
    }

    @Test
    public void givenNullRepIdInRequest_whenIsRepOrderReserved_thenFalseIsReturned() {
        requestDTO.setRepId(null);
        assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
    }

    @Test
    public void givenAnOutstandingAssessment_whenIsOutstandingAssessmentIsInvoked_thenTrueIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<OutstandingAssessmentResultDTO>() {}), anyString(), anyMap(), any()))
                .thenReturn(IS_OUTSTANDING_ASSESSMENT);
        assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isTrue();
    }

    @Test
    public void givenNoOutstandingAssessments_whenIsOutstandingAssessmentIsInvoked_thenFalseIsReturned() {
        when(maatAPIClient.get(eq(new ParameterizedTypeReference<OutstandingAssessmentResultDTO>() {}), anyString(), anyMap(), any()))
                .thenReturn(NO_OUTSTANDING_ASSESSMENT);
        assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isFalse();
    }

    @Test
    public void givenNoRepId_whenIsOutstandingAssessmentIsInvoked_thenFalseIsReturned() {
        requestDTO.setRepId(null);
        assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isFalse();
    }

    @Test
    public void givenNoTimeStamp_whenIsFinAssessmentModifiedByOtherUserInvoked_thenTrueIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        when(maatCourtDataService.getFinancialAssessment(any(), any())).thenReturn(TestModelDataBuilder.getFinancialAssessmentDTO());
        assertThat(meansAssessmentValidationService.isAssessmentModifiedByAnotherUser(requestDTO)).isTrue();
    }

    @Test
    public void givenMisMatchedTimeStamp_whenIsAssessmentModifiedByAnotherUserIsInvoked_thenTrueIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setTimeStamp(TestModelDataBuilder.TEST_DATE_CREATED.plusDays(1));
        when(maatCourtDataService.getFinancialAssessment(any(), any())).thenReturn(TestModelDataBuilder.getFinancialAssessmentDTO());
        assertThat(meansAssessmentValidationService.isAssessmentModifiedByAnotherUser(requestDTO)).isTrue();
    }

    @Test
    public void givenAValidTimeStampInRequest_whenSameDateCreatedInFinancialAssessmentDTO_thenFalseIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setTimeStamp(TestModelDataBuilder.TEST_DATE_CREATED);
        when(maatCourtDataService.getFinancialAssessment(any(), any())).thenReturn(TestModelDataBuilder.getFinancialAssessmentDTO());
        assertThat(meansAssessmentValidationService.isAssessmentModifiedByAnotherUser(requestDTO)).isFalse();
    }

    @Test
    public void givenAValidTimeStampInRequest_whenSameTimeStampInFinancialAssessmentDTO_thenFalseIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setTimeStamp(TestModelDataBuilder.TEST_DATE_CREATED);
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO();
        financialAssessmentDTO.setUpdated(TestModelDataBuilder.TEST_DATE_CREATED);
        when(maatCourtDataService.getFinancialAssessment(any(), any())).thenReturn(financialAssessmentDTO);
        assertThat(meansAssessmentValidationService.isAssessmentModifiedByAnotherUser(requestDTO)).isFalse();
    }

    @Test
    public void givenAValidTimeStampInRequest_whenEmptyFinancialAssessmentDTO_theFalseIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setTimeStamp(TestModelDataBuilder.TEST_DATE_CREATED);
        when(maatCourtDataService.getFinancialAssessment(any(), any())).thenReturn(null);
        assertThat(meansAssessmentValidationService.isAssessmentModifiedByAnotherUser(requestDTO)).isFalse();
    }
}
