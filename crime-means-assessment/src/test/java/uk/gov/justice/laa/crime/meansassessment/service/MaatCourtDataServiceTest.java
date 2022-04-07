package uk.gov.justice.laa.crime.meansassessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(MockitoJUnitRunner.class)
public class MaatCourtDataServiceTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String postAssessmentUrl = "post-assessment-url";
    private final Integer repId = 1234;
    private final String laaTransactionId = "laaTransactionId";

    @Mock
    private ExchangeFunction shortCircuitExchangeFunction;
    @Mock
    private MaatApiConfiguration maatApiConfiguration;
    @Mock
    private MaatApiConfiguration.PassportAssessmentEndpoints passportAssessmentEndpoints;
    @Mock
    private MaatApiConfiguration.IOJAppealEndpoints iojAppealEndpoints;
    @Mock
    private MaatApiConfiguration.HardshipReviewEndpoints hardshipReviewEndpoints;

    private MaatCourtDataService maatCourtDataService;

    @Before
    public void setup() {
        WebClient testWebClient = WebClient
                .builder()
                .baseUrl("http://localhost:1234")
                .filter(
                        ExchangeFilterFunctions.statusError(
                                HttpStatus::is4xxClientError, r ->
                                        WebClientResponseException.create(
                                                r.rawStatusCode(),
                                                r.statusCode().getReasonPhrase(),
                                                null,
                                                null,
                                                null
                                        )
                        )
                )
                .exchangeFunction(shortCircuitExchangeFunction)
                .build();

        String passportUrl = "passport-url";
        when(passportAssessmentEndpoints.getFindUrl()).thenReturn(passportUrl);
        String hardshipUrl = "hardship-url";
        when(hardshipReviewEndpoints.getFindUrl()).thenReturn(hardshipUrl);
        String iojUrl = "ioj-url";
        when(iojAppealEndpoints.getFindUrl()).thenReturn(iojUrl);

        when(maatApiConfiguration.getPassportAssessmentEndpoints()).thenReturn(passportAssessmentEndpoints);
        when(maatApiConfiguration.getHardshipReviewEndpoints()).thenReturn(hardshipReviewEndpoints);
        when(maatApiConfiguration.getIojAppealEndpoints()).thenReturn(iojAppealEndpoints);

        maatCourtDataService = new MaatCourtDataService(testWebClient, maatApiConfiguration);
    }

    @Test
    public void givenANotFoundException_whenPostMeansAssessmentIsInvoked_thenTheMethodShouldReturnNull() {
        setupNotFoundTest();
        assertNull(maatCourtDataService.postMeansAssessment(new MaatApiAssessmentRequest(), laaTransactionId, postAssessmentUrl));
    }

    @Test
    public void givenAnInvalidResponse_whenPostMeansAssessmentIsInvoked_thenAnAppropriateErrorShouldBeThrown() {
        setupInvalidResponseTest();
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.postMeansAssessment(new MaatApiAssessmentRequest(), laaTransactionId, postAssessmentUrl)
        );
        validateInvalidResponseError(error);
    }

    @Test
    public void givenAValidResponse_whenPostMeansAssessmentIsInvoked_thenTheCorrectResponseShouldBeReturned() throws JsonProcessingException {
        Integer testId = 42;
        MaatApiAssessmentResponse expectedResponse = new MaatApiAssessmentResponse();
        expectedResponse.setId(testId);
        setupValidResponseTest(expectedResponse);
        MaatApiAssessmentResponse actualResponse =
                maatCourtDataService.postMeansAssessment(new MaatApiAssessmentRequest(), laaTransactionId, postAssessmentUrl);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
    }

    @Test
    public void givenANotFoundException_whenGetPassportAssessmentFromRepIdIsInvoked_thenTheMethodShouldReturnNull() {
        setupNotFoundTest();
        assertNull(maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId));
    }

    @Test
    public void givenAnInvalidResponse_whenGetPassportAssessmentFromRepIdIsInvoked_thenAnAppropriateErrorShouldBeThrown() {
        setupInvalidResponseTest();
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId)
        );
        validateInvalidResponseError(error);
    }

    @Test
    public void givenAValidResponse_whenGetPassportAssessmentFromRepIdIsInvoked_thenTheCorrectResponseShouldBeReturned() throws JsonProcessingException {
        Integer testId = 42;
        PassportAssessmentDTO expectedResponse = new PassportAssessmentDTO();
        expectedResponse.setId(testId);
        setupValidResponseTest(expectedResponse);
        PassportAssessmentDTO actualResponse = maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
    }

    @Test
    public void givenANotFoundException_whenGetHardshipReviewFromRepIdIsInvoked_thenTheMethodShouldReturnNull() {
        setupNotFoundTest();
        assertNull(maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId));
    }

    @Test
    public void givenAnInvalidResponse_whenGetHardshipReviewFromRepIdIsInvoked_thenAnAppropriateErrorShouldBeThrown() {
        setupInvalidResponseTest();
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId)
        );
        validateInvalidResponseError(error);
    }

    @Test
    public void givenAValidResponse_whenGetHardshipReviewFromRepIdIsInvoked_thenTheCorrectResponseShouldBeReturned() throws JsonProcessingException {
        Integer testId = 42;
        HardshipReviewDTO expectedResponse = new HardshipReviewDTO();
        expectedResponse.setId(testId);
        setupValidResponseTest(expectedResponse);
        HardshipReviewDTO actualResponse = maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
    }

    @Test
    public void givenANotFoundException_whenGetIOJAppealFromRepIdIsInvoked_thenTheMethodShouldReturnNull() {
        setupNotFoundTest();
        assertNull(maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId));
    }

    @Test
    public void givenAnInvalidResponse_whenGetIOJAppealFromRepIdIsInvoked_thenAnAppropriateErrorShouldBeThrown() {
        setupInvalidResponseTest();
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId)
        );
        validateInvalidResponseError(error);
    }

    @Test
    public void givenAValidResponse_whenGetIOJAppealFromRepIdIsInvoked_thenTheCorrectResponseShouldBeReturned() throws JsonProcessingException {
        Integer testId = 42;
        IOJAppealDTO expectedResponse = new IOJAppealDTO();
        expectedResponse.setId(testId);
        setupValidResponseTest(expectedResponse);
        IOJAppealDTO actualResponse = maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
    }


    private void setupNotFoundTest() {
        when(shortCircuitExchangeFunction.exchange(any()))
                .thenReturn(
                        Mono.just(
                                ClientResponse
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
                        Mono.just(
                                ClientResponse
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
                        Mono.just(
                                ClientResponse
                                        .create(HttpStatus.OK)
                                        .body(body)
                                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .build()
                        )
                );
    }

    private void validateInvalidResponseError(APIClientException error) {
        assertTrue(error.getCause() instanceof WebClientResponseException);
    }
}