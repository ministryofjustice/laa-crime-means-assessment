package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.*;
import uk.gov.justice.laa.crime.meansassessment.model.common.maatapi.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.util.MockMaatApiConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    @Mock
    RestAPIClient maatAPIClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Spy
    private MaatApiConfiguration maatApiConfiguration = MockMaatApiConfiguration.getConfiguration(1000);

    @Test
    void givenCreateRequest_whenPersistMeansAssessmentIsInvoked_thenPostRequestIsSentToCourtDataApi() {
        MaatApiAssessmentResponse expected = new MaatApiAssessmentResponse().withId(1234);

        when(maatAPIClient.post(any(MaatApiAssessmentRequest.class), any(), anyString(), anyMap()))
                .thenReturn(expected);

        MaatApiAssessmentResponse response = maatCourtDataService.persistMeansAssessment(
                new MaatApiAssessmentRequest(), RequestType.CREATE);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenUpdateRequest_whenPersistMeansAssessmentIsInvoked_thenPutRequestIsSentToCourtDataApi() {
        MaatApiAssessmentResponse expected = new MaatApiAssessmentResponse().withId(5678);
        when(maatAPIClient.put(any(MaatApiAssessmentRequest.class), any(), anyString(), anyMap()))
                .thenReturn(expected);

        MaatApiAssessmentResponse response = maatCourtDataService.persistMeansAssessment(
                new MaatApiAssessmentRequest(), RequestType.UPDATE);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetPassportAssessmentFromRepIdIsInvoked_thenResponseIsReturned() {
        PassportAssessmentDTO expected = new PassportAssessmentDTO();
        when(maatAPIClient.get(any(), anyString(), any()))
                .thenReturn(expected);

        PassportAssessmentDTO response =
                maatCourtDataService.getPassportAssessmentFromRepId(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetHardshipReviewFromRepIdIsInvoked_thenResponseIsReturned() {
        HardshipReviewDTO expected = new HardshipReviewDTO();
        when(maatAPIClient.get(any(), anyString(), any()))
                .thenReturn(expected);

        HardshipReviewDTO response =
                maatCourtDataService.getHardshipReviewFromRepId(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetIojAppealFromRepIdIsInvoked_thenResponseIsReturned() {
        IOJAppealDTO expected = new IOJAppealDTO();
        when(maatAPIClient.get(any(), anyString(), any()))
                .thenReturn(expected);

        IOJAppealDTO response =
                maatCourtDataService.getIOJAppealFromRepId(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetFinancialAssessmentIsInvoked_thenResponseIsReturned() {
        FinancialAssessmentDTO expected = new FinancialAssessmentDTO();
        when(maatAPIClient.get(any(), anyString(), any()))
                .thenReturn(expected);

        FinancialAssessmentDTO response =
                maatCourtDataService.getFinancialAssessment(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenRepId_whenGetRepOrderIsInvoked_thenResponseIsReturned() {
        RepOrderDTO expected = new RepOrderDTO();
        when(maatAPIClient.get(any(), anyString(), any()))
                .thenReturn(expected);

        RepOrderDTO response =
                maatCourtDataService.getRepOrder(TestModelDataBuilder.TEST_REP_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void givenDateCompletionRequest_whenUpdateCompletionDateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.updateCompletionDate(DateCompletionRequestDTO.builder().build());
        verify(maatAPIClient).post(
                any(DateCompletionRequestDTO.class),
                any(),
                anyString(),
                anyMap()
        );
    }
}