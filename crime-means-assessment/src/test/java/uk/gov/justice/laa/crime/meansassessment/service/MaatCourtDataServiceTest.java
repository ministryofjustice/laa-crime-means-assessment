package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.*;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    @Mock
    MaatCourtDataApiClient maatAPIClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenCreateRequest_whenPersistMeansAssessmentIsInvoked_thenPostRequestIsSentToCourtDataApi() {
        MaatApiAssessmentResponse expected = new MaatApiAssessmentResponse().withId(1234);

        when(maatAPIClient.createFinancialAssessment(any(MaatApiAssessmentRequest.class)))
                .thenReturn(expected);

        MaatApiAssessmentResponse response = maatCourtDataService.persistMeansAssessment(
                new MaatApiAssessmentRequest(), RequestType.CREATE);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenUpdateRequest_whenPersistMeansAssessmentIsInvoked_thenPutRequestIsSentToCourtDataApi() {
        MaatApiAssessmentResponse expected = new MaatApiAssessmentResponse().withId(5678);
        when(maatAPIClient.updateFinancialAssessment(any(MaatApiAssessmentRequest.class)))
                .thenReturn(expected);

        MaatApiAssessmentResponse response = maatCourtDataService.persistMeansAssessment(
                new MaatApiAssessmentRequest(), RequestType.UPDATE);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetPassportAssessmentFromRepIdIsInvoked_thenResponseIsReturned() {
        PassportAssessmentDTO expected = new PassportAssessmentDTO();
        when(maatAPIClient.getPassportAssessmentFromRepId(anyInt()))
                .thenReturn(expected);

        PassportAssessmentDTO response =
                maatCourtDataService.getPassportAssessmentFromRepId(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetHardshipReviewFromRepIdIsInvoked_thenResponseIsReturned() {
        HardshipReviewDTO expected = new HardshipReviewDTO();
        when(maatAPIClient.getHardshipReviewFromRepId(anyInt()))
                .thenReturn(expected);

        HardshipReviewDTO response =
                maatCourtDataService.getHardshipReviewFromRepId(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetIojAppealFromRepIdIsInvoked_thenResponseIsReturned() {
        IOJAppealDTO expected = new IOJAppealDTO();
        when(maatAPIClient.getIOJAppealFromRepId(anyInt()))
                .thenReturn(expected);

        IOJAppealDTO response =
                maatCourtDataService.getIOJAppealFromRepId(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetFinancialAssessmentIsInvoked_thenResponseIsReturned() {
        FinancialAssessmentDTO expected = new FinancialAssessmentDTO();
        when(maatAPIClient.getFinancialAssessment(anyInt()))
                .thenReturn(expected);

        FinancialAssessmentDTO response =
                maatCourtDataService.getFinancialAssessment(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetRepOrderIsInvoked_thenResponseIsReturned() {
        RepOrderDTO expected = new RepOrderDTO();
        when(maatAPIClient.getRepOrder(anyInt()))
                .thenReturn(expected);

        RepOrderDTO response =
                maatCourtDataService.getRepOrder(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenDateCompletionRequest_whenUpdateCompletionDateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.updateCompletionDate(DateCompletionRequestDTO.builder().build());
        verify(maatAPIClient).updateCompletionDate(any(DateCompletionRequestDTO.class));
    }

    @Test
    void givenFinancialAssessmentId_whenUpdateFinancialAssessmentIsInvoked_thenPatchRequestIsSent() {
        maatCourtDataService.rollbackFinancialAssessment(TestModelDataBuilder.TEST_REP_ID, Map.of());
        verify(maatAPIClient).patchFinancialAssessment(anyMap(), anyInt());
    }

}