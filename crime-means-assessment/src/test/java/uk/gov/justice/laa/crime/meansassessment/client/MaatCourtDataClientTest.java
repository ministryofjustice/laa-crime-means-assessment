package uk.gov.justice.laa.crime.meansassessment.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(MockitoJUnitRunner.class)
public class MaatCourtDataClientTest {

    public static final Integer TEST_ID = 42;
    private MaatCourtDataClient maatCourtDataClient;

    private final Integer REP_ID = 1234;
    public static final String MOCK_URL = "mock-url";
    private final String LAA_TRANSACTION_ID = "laaTransactionId";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mock
    private ExchangeFunction shortCircuitExchangeFunction;

    @Before
    public void setup() {
        WebClient testWebClient = WebClient
                .builder()
                .baseUrl("http://localhost:1234")
                .filter(ExchangeFilterFunctions.statusError(
                                HttpStatus::is4xxClientError,
                                r -> WebClientResponseException.create(
                                        r.rawStatusCode(), r.statusCode().getReasonPhrase(), null, null, null
                                )
                        )
                )
                .exchangeFunction(shortCircuitExchangeFunction)
                .build();

        maatCourtDataClient = Mockito.spy(new MaatCourtDataClient(testWebClient));
    }

    @Test
    public void givenAnInvalidResponse_whenGetApiResponseIsInvoked_thenAnAppropriateErrorShouldBeThrown() {
        setupInvalidResponseTest();
        assertThatThrownBy(
                () -> maatCourtDataClient.getApiResponse(
                        new MaatApiAssessmentRequest(),
                        MaatApiAssessmentResponse.class,
                        MOCK_URL,
                        Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                        HttpMethod.POST
                )
        ).isInstanceOf(APIClientException.class).getCause().isInstanceOf(WebClientResponseException.class);
    }

    @Test
    public void givenAValidResponse_whenGetApiResponseIsInvoked_thenTheCorrectResponseShouldBeReturned() throws JsonProcessingException {
        Integer testId = 42;
        MaatApiAssessmentResponse expectedResponse = new MaatApiAssessmentResponse();
        expectedResponse.setId(testId);
        setupValidResponseTest(expectedResponse);
        MaatApiAssessmentResponse actualResponse = maatCourtDataClient.getApiResponse(
                new MaatApiAssessmentRequest(),
                MaatApiAssessmentResponse.class,
                MOCK_URL,
                Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                HttpMethod.POST
        );
        assertThat(expectedResponse.getId()).isEqualTo(actualResponse.getId());
    }

    @Test
    public void givenANotFoundException_whenGetApiResponseIsInvoked_thenTheMethodShouldReturnNull() {
        setupNotFoundTest();
        MaatApiAssessmentResponse response = maatCourtDataClient.getApiResponse(
                new MaatApiAssessmentRequest(),
                MaatApiAssessmentResponse.class,
                MOCK_URL,
                Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                HttpMethod.POST
        );
        assertThat(response).isNull();
    }

    @Test
    public void givenCorrectParams_whenGetApiResponseViaPOSTIsInvoked_thenGetApiResponseIsCalledWithCorrectMethod() throws JsonProcessingException {
        MaatApiAssessmentRequest requestBody = new MaatApiAssessmentRequest();
        setupValidResponseTest(new MaatApiAssessmentResponse());
        maatCourtDataClient.getApiResponseViaPOST(
                requestBody,
                MaatApiAssessmentResponse.class,
                MOCK_URL,
                Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID)
        );
        verify(maatCourtDataClient)
                .getApiResponse(
                        requestBody,
                        MaatApiAssessmentResponse.class,
                        MOCK_URL,
                        Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                        HttpMethod.POST
                );
    }

    @Test
    public void givenCorrectParams_whenGetApiResponseViaPUTIsInvoked_thenGetApiResponseIsCalledWithCorrectMethod() throws JsonProcessingException {
        MaatApiAssessmentRequest requestBody = new MaatApiAssessmentRequest();
        setupValidResponseTest(new MaatApiAssessmentResponse());
        maatCourtDataClient.getApiResponseViaPUT(
                requestBody,
                MaatApiAssessmentResponse.class,
                MOCK_URL,
                Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID)
        );
        verify(maatCourtDataClient)
                .getApiResponse(
                        requestBody,
                        MaatApiAssessmentResponse.class,
                        MOCK_URL,
                        Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                        HttpMethod.PUT
                );
    }

    @Test
    public void givenANotFoundException_whenGetApiResponseViaGetIsInvoked_thenTheMethodShouldReturnNull() {
        setupNotFoundTest();
        MaatApiAssessmentResponse response = maatCourtDataClient.getApiResponseViaGET(
                MaatApiAssessmentResponse.class,
                MOCK_URL,
                Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                REP_ID
        );
        assertThat(response).isNull();
    }

    @Test
    public void givenAnInvalidResponse_whenGetApiResponseViaGetIsInvoked_thenAnAppropriateErrorShouldBeThrown() {
        setupInvalidResponseTest();
        assertThatThrownBy(
                () -> maatCourtDataClient.getApiResponseViaGET(
                        MaatApiAssessmentResponse.class,
                        MOCK_URL,
                        Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                        REP_ID
                )
        ).isInstanceOf(APIClientException.class).getCause().isInstanceOf(WebClientResponseException.class);
    }

    @Test
    public void givenAValidResponse_whenGetApiResponseViaGetIsInvoked_thenTheCorrectResponseShouldBeReturned() throws JsonProcessingException {
        MaatApiAssessmentResponse expectedResponse = new MaatApiAssessmentResponse();
        expectedResponse.setId(TEST_ID);
        setupValidResponseTest(expectedResponse);
        MaatApiAssessmentResponse actualResponse = maatCourtDataClient.getApiResponseViaGET(
                MaatApiAssessmentResponse.class,
                MOCK_URL,
                Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                REP_ID
        );
        assertEquals(expectedResponse.getId(), actualResponse.getId());
    }

    private void setupNotFoundTest() {
        when(shortCircuitExchangeFunction.exchange(any()))
                .thenReturn(
                        Mono.just(ClientResponse
                                .create(HttpStatus.NOT_FOUND)
                                .body("Error")
                                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .build()
                        )
                );
    }

    private void setupInvalidResponseTest() {
        when(shortCircuitExchangeFunction.exchange(any()))
                .thenReturn(
                        Mono.just(ClientResponse
                                .create(HttpStatus.OK)
                                .body("Invalid response")
                                .build()
                        )
                );
    }

    private <T> void setupValidResponseTest(T returnBody) throws JsonProcessingException {
        String body = OBJECT_MAPPER.writeValueAsString(returnBody);
        when(shortCircuitExchangeFunction.exchange(any()))
                .thenReturn(
                        Mono.just(ClientResponse
                                .create(HttpStatus.OK)
                                .body(body)
                                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .build()
                        )
                );
    }
}
