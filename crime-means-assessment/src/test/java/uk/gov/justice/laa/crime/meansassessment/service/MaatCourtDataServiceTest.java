package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.util.MockMaatApiConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MaatCourtDataServiceTest {

    private static final String LAA_TRANSACTION_ID = "laaTransactionId";

    @Mock
    MaatCourtDataClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Spy
    private MaatApiConfiguration maatApiConfiguration = MockMaatApiConfiguration.getConfiguration(1000);

    @Test
    public void givenCreateRequest_whenPersistMeansAssessmentIsInvoked_thenPostRequestIsSentToCourtDataApi() {
        MaatApiAssessmentResponse expected = new MaatApiAssessmentResponse().withId(1234);

        when(maatCourtDataClient.getApiResponseViaPOST(
                any(MaatApiAssessmentRequest.class), any(), anyString(), anyMap()
        )).thenReturn(expected);

        MaatApiAssessmentResponse response = maatCourtDataService.persistMeansAssessment(
                new MaatApiAssessmentRequest(), LAA_TRANSACTION_ID, AssessmentRequestType.CREATE
        );
        assertThat(response).isEqualTo(expected);
    }

    @Test
    public void givenUpdateRequest_whenPersistMeansAssessmentIsInvoked_thenPutRequestIsSentToCourtDataApi() {
        MaatApiAssessmentResponse expected = new MaatApiAssessmentResponse().withId(5678);
        when(maatCourtDataClient.getApiResponseViaPUT(
                any(MaatApiAssessmentRequest.class), any(), anyString(), anyMap()
        )).thenReturn(expected);

        MaatApiAssessmentResponse response = maatCourtDataService.persistMeansAssessment(
                new MaatApiAssessmentRequest(), LAA_TRANSACTION_ID, AssessmentRequestType.UPDATE
        );
        assertThat(response).isEqualTo(expected);
    }

    @Test
    public void givenRepId_whenGetPassportAssessmentFromRepIdIsInvoked_thenResponseIsReturned() {
        PassportAssessmentDTO expected = new PassportAssessmentDTO();
        when(maatCourtDataClient.getApiResponseViaGET(any(), anyString(), anyMap(), any()))
                .thenReturn(expected);

        PassportAssessmentDTO response =
                maatCourtDataService.getPassportAssessmentFromRepId(TestModelDataBuilder.TEST_REP_ID, LAA_TRANSACTION_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    public void givenRepId_whenGetHardshipReviewFromRepIdIsInvoked_thenResponseIsReturned() {
        HardshipReviewDTO expected = new HardshipReviewDTO();
        when(maatCourtDataClient.getApiResponseViaGET(any(), anyString(), anyMap(), any()))
                .thenReturn(expected);

        HardshipReviewDTO response =
                maatCourtDataService.getHardshipReviewFromRepId(TestModelDataBuilder.TEST_REP_ID, LAA_TRANSACTION_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    public void givenRepId_whenGetIojAppealFromRepIdIsInvoked_thenResponseIsReturned() {
        IOJAppealDTO expected = new IOJAppealDTO();
        when(maatCourtDataClient.getApiResponseViaGET(any(), anyString(), anyMap(), any()))
                .thenReturn(expected);

        IOJAppealDTO response =
                maatCourtDataService.getIOJAppealFromRepId(TestModelDataBuilder.TEST_REP_ID, LAA_TRANSACTION_ID);

        assertThat(response).isEqualTo(expected);
    }
}