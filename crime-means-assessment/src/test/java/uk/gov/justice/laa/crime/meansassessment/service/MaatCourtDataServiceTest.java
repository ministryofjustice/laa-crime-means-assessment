package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MaatCourtDataServiceTest {

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


    @Mock
    private RetryConfiguration retryConfiguration;

    private MaatCourtDataService maatCourtDataService;
    private final Integer maxRetries = 2;
    private final String postAssessmentUrl = "post-assessment-url";
    private final Integer repId = 1234;
    private final String laaTransactionId = "laaTransactionId";
    private final String retriesExhaustedErrorMessage = String.format("Retries exhausted: %d/%d", maxRetries, maxRetries);

    @Before
    public void setup() {
        WebClient testWebClient = WebClient
                .builder()
                .baseUrl("http://localhost:1234")
                .filter(
                        ExchangeFilterFunctions.statusError(
                                HttpStatus::is5xxServerError, r ->
                                        new HttpServerErrorException(
                                                r.statusCode(),
                                                "Simulated 5xx error."
                                        )
                        )
                )
                .exchangeFunction(shortCircuitExchangeFunction)
                .build();

        when(retryConfiguration.getMaxRetries()).thenReturn(maxRetries);
        Double jitterValue = 0.1;
        when(retryConfiguration.getJitterValue()).thenReturn(jitterValue);
        Integer minBackOffPeriod = 0;
        when(retryConfiguration.getMinBackOffPeriod()).thenReturn(minBackOffPeriod);

        String passportUrl = "passport-url";
        when(passportAssessmentEndpoints.getFindUrl()).thenReturn(passportUrl);
        String hardshipUrl = "hardship-url";
        when(hardshipReviewEndpoints.getFindUrl()).thenReturn(hardshipUrl);
        String iojUrl = "ioj-url";
        when(iojAppealEndpoints.getFindUrl()).thenReturn(iojUrl);

        when(maatApiConfiguration.getPassportAssessmentEndpoints()).thenReturn(passportAssessmentEndpoints);
        when(maatApiConfiguration.getHardshipReviewEndpoints()).thenReturn(hardshipReviewEndpoints);
        when(maatApiConfiguration.getIojAppealEndpoints()).thenReturn(iojAppealEndpoints);

        maatCourtDataService = new MaatCourtDataService(retryConfiguration, testWebClient, maatApiConfiguration);
    }

    @Test
    public void givenAHttpServerErrorException_whenPostMeansAssessmentIsInvoked_thenTheApiCallShouldBeRetried() {
        setupRetryTest();
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.postMeansAssessment(new MaatApiAssessmentRequest(), laaTransactionId, postAssessmentUrl)
        );
        validateRetryTestError(error);
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
    public void givenAHttpServerErrorException_whenGetPassportAssessmentFromRepIdIsInvoked_thenTheApiCallShouldBeRetried() {
        setupRetryTest();
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId)
        );
        validateRetryTestError(error);
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
    public void givenAHttpServerErrorException_whenGetHardshipReviewFromRepIdIsInvoked_thenTheApiCallShouldBeRetried() {
        setupRetryTest();
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId)
        );
        validateRetryTestError(error);
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
    public void givenAHttpServerErrorException_whenGetIOJAppealFromRepIdIsInvoked_thenTheApiCallShouldBeRetried() {
        setupRetryTest();
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId)
        );
        validateRetryTestError(error);
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

    private void setupRetryTest() {
        when(shortCircuitExchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).body("Error").build()));
    }

    private void validateRetryTestError(APIClientException error) {
        verify(shortCircuitExchangeFunction, times(maxRetries + 1)).exchange(any());
        assertEquals(retriesExhaustedErrorMessage, error.getCause().getMessage());
        String serverErrorMessage = "500 Simulated 5xx error.";
        assertEquals(serverErrorMessage, error.getCause().getCause().getMessage());
    }

    private void setupInvalidResponseTest() {
        when(shortCircuitExchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK).body("{message: \"Invalid response\"}").build()));
    }

    private void validateInvalidResponseError(APIClientException error) {
        verify(shortCircuitExchangeFunction, times(1)).exchange(any());
        assertTrue(error.getCause() instanceof WebClientResponseException);
    }
}