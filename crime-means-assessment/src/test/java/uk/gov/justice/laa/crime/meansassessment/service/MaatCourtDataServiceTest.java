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
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.DateCompletionRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.util.MockMaatApiConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
    public void givenRepId_whenGetFinancialAssessmentIsInvoked_thenResponseIsReturned() {
        FinancialAssessmentDTO expected = new FinancialAssessmentDTO();
        when(maatCourtDataClient.getApiResponseViaGET(any(), anyString(), anyMap(), any()))
                .thenReturn(expected);

        FinancialAssessmentDTO response =
                maatCourtDataService.getFinancialAssessment(TestModelDataBuilder.TEST_REP_ID, LAA_TRANSACTION_ID);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    public void givenDateCompletionRequest_whenUpdateCompletionDateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.updateCompletionDate(DateCompletionRequestDTO.builder().build(), LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).getApiResponseViaPOST(
                any(DateCompletionRequestDTO.class),
                any(),
                anyString(),
                anyMap()
        );
    }
}