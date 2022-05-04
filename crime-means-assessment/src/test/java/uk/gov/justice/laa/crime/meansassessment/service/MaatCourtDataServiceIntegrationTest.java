package uk.gov.justice.laa.crime.meansassessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiOAuth2Client;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MaatCourtDataServiceIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static MockWebServer mockMaatCourtDataApi;
    private final Integer maxRetries = 2;
    private final Integer repId = 1234;
    private final String laaTransactionId = "laa-transaction-id";

    private MaatCourtDataService maatCourtDataService;

    @Rule
    public OutputCaptureRule output = new OutputCaptureRule();

    @Mock
    private MaatApiConfiguration configuration;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private OAuth2AuthorizedClientRepository authorizedClients;

    @Before
    public void initialize() throws IOException {
        mockMaatCourtDataApi = new MockWebServer();
        mockMaatCourtDataApi.start();
        String baseUrl = String.format("http://localhost:%s", mockMaatCourtDataApi.getPort());
        configuration = new MaatApiConfiguration();
        configuration.setBaseUrl(baseUrl);
        configuration.setOAuthEnabled(false);
        configuration.setPostProcessingUrl("/post-processing/{repId}");

        MaatApiConfiguration.PassportAssessmentEndpoints passportEndpoints = new MaatApiConfiguration.PassportAssessmentEndpoints(
                "/passport-assessments/{repId}"
        );
        MaatApiConfiguration.HardshipReviewEndpoints hardshipEndpoints = new MaatApiConfiguration.HardshipReviewEndpoints(
                "/hardship/{repId}"
        );
        MaatApiConfiguration.IOJAppealEndpoints iojEndpoints = new MaatApiConfiguration.IOJAppealEndpoints(
                "/ioj-appeal/{repId}"
        );
        MaatApiConfiguration.FinancialAssessmentEndpoints financialAssessmentEndpoints = new MaatApiConfiguration.FinancialAssessmentEndpoints(
                "/financial-assessments/{financialAssessmentId}",
                "/financial-assessments/",
                "/financial-assessments/{financialAssessmentId}",
                "/financial-assessments/history/{financialAssessmentId}/fullAvailable/{fullAvailable}"
        );

        configuration.setPassportAssessmentEndpoints(passportEndpoints);
        configuration.setHardshipReviewEndpoints(hardshipEndpoints);
        configuration.setIojAppealEndpoints(iojEndpoints);
        configuration.setFinancialAssessmentEndpoints(financialAssessmentEndpoints);

        RetryConfiguration retryConfiguration = new RetryConfiguration();
        retryConfiguration.setMaxRetries(maxRetries);
        retryConfiguration.setMinBackOffPeriod(1);
        retryConfiguration.setJitterValue(0.5);

        WebClient maatWebClient = buildWebClient(configuration, retryConfiguration, clientRegistrationRepository, authorizedClients);
        maatCourtDataService = new MaatCourtDataService(maatWebClient, configuration);
    }

    @After
    public void tearDown() throws IOException {
        mockMaatCourtDataApi.shutdown();
    }

    @Test
    public void whenPostMeansAssessmentAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new MaatApiAssessmentRequest(), 0);
        MaatApiAssessmentResponse apiResponse = runPostMeansAssessment();
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenPostMeansAssessmentAPICalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();
        APIClientException error = assertThrows(APIClientException.class, this::runPostMeansAssessment);
        validateInvalidResponseError(error);
    }

    @Test
    public void whenPostMeansAssessmentAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new MaatApiAssessmentRequest(), maxRetries - 1);
        MaatApiAssessmentResponse apiResponse = runPostMeansAssessment();
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenPostMeansAssessmentAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new MaatApiAssessmentResponse(), maxRetries + 1);
        APIClientException error = assertThrows(APIClientException.class, this::runPostMeansAssessment);
        validateRetryErrorResponse(error);
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new PassportAssessmentDTO(), 0);
        PassportAssessmentDTO apiResponse =
                maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();
        APIClientException error = assertThrows(APIClientException.class, () -> maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId));
        validateInvalidResponseError(error);
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new PassportAssessmentDTO(), maxRetries - 1);
        PassportAssessmentDTO apiResponse =
                maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallReturnsNotFoundResponse_thenMethodReturnsNull() {
        setupMockNotFoundResponse();
        PassportAssessmentDTO apiResponse = maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);
        assertNull(apiResponse);
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new PassportAssessmentDTO(), maxRetries + 1);
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId)
        );
        validateRetryErrorResponse(error);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPIAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new HardshipReviewDTO(), 0);
        HardshipReviewDTO apiResponse =
                maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();
        APIClientException error = assertThrows(APIClientException.class, () -> maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId));
        validateInvalidResponseError(error);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new HardshipReviewDTO(), maxRetries - 1);
        HardshipReviewDTO apiResponse = maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new HardshipReviewDTO(), maxRetries + 1);
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId)
        );
        validateRetryErrorResponse(error);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallReturnsNotFoundResponse_thenMethodReturnsNull() {
        setupMockNotFoundResponse();
        HardshipReviewDTO apiResponse = maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);
        assertNull(apiResponse);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPIAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new IOJAppealDTO(), maxRetries - 1);
        IOJAppealDTO apiResponse =
                maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();
        APIClientException error = assertThrows(APIClientException.class, () -> maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId));
        validateInvalidResponseError(error);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new IOJAppealDTO(), maxRetries - 1);
        IOJAppealDTO apiResponse =
                maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new IOJAppealDTO(), maxRetries + 1);
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId)
        );
        validateRetryErrorResponse(error);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallReturnsNotFoundResponse_thenMethodReturnsNull() {
        setupMockNotFoundResponse();
        IOJAppealDTO apiResponse = maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);
        assertNull(apiResponse);
    }

    @Test
    public void whenPerformAssessmentPostProcessingAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        setupMockApiResponses(Void.class, 0);
        StepVerifier
                .create(maatCourtDataService.performAssessmentPostProcessing(repId, laaTransactionId))
                .expectComplete()
                .verify();
        assertTrue(output.getOut().contains(getPostProcessingSuccessMessage()));
    }

    @Test
    public void whenPerformAssessmentPostProcessingAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        setupMockApiResponses(Void.class, maxRetries - 1);
        StepVerifier
                .create(maatCourtDataService.performAssessmentPostProcessing(repId, laaTransactionId))
                .expectComplete()
                .verify();
        assertTrue(output.getOut().contains(getPostProcessingSuccessMessage()));
    }

    @Test
    public void whenPerformAssessmentPostProcessingCallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        String errorMessage = String.format("An error occurred whilst performing assessment post-processing for RepID: %d", repId);

        setupMockApiResponses(Void.class, maxRetries + 1);
        StepVerifier
                .create(maatCourtDataService.performAssessmentPostProcessing(repId, laaTransactionId))
                .expectErrorMessage(errorMessage)
                .verify();
        assertTrue(output.getOut().contains(errorMessage));
    }

    private <T> String setupMockApiResponses(T responseObject, Integer attemptsBeforeSuccess) throws JsonProcessingException {
        String expectedResponse = OBJECT_MAPPER.writeValueAsString(responseObject);
        for (int i = 0; i < attemptsBeforeSuccess; i++) {
            mockMaatCourtDataApi.enqueue(new MockResponse()
                    .setResponseCode(SERVICE_UNAVAILABLE.code()));
        }

        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setBody(expectedResponse).addHeader("Content-Type", "application/json"));

        return expectedResponse;
    }

    private void setupMockInvalidResponse() {
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setBody("Invalid response.")
                .addHeader("Content-Type", "application/json")
        );
    }

    private void setupMockNotFoundResponse() {
        mockMaatCourtDataApi.enqueue(new MockResponse().setResponseCode(NOT_FOUND.code()));
    }

    private void validateInvalidResponseError(APIClientException error) {
        assertEquals("Call to Court Data API failed, invalid response.", error.getMessage());
    }

    private void validateRetryErrorResponse(APIClientException error) {
        assertEquals(String.format("Call to Court Data API failed. Retries exhausted: %d/%d.", maxRetries, maxRetries), error.getMessage());
        assertEquals("503 Received error 503 due to Service Unavailable", error.getCause().getLocalizedMessage());
    }

    private WebClient buildWebClient(
            MaatApiConfiguration maatConfig,
            RetryConfiguration retryConfiguration,
            ClientRegistrationRepository clientRegistrations,
            OAuth2AuthorizedClientRepository authorizedClients
    ) {
        MaatApiOAuth2Client client = new MaatApiOAuth2Client(maatConfig, retryConfiguration);

        return client.webClient(clientRegistrations, authorizedClients);
    }

    private MaatApiAssessmentResponse runPostMeansAssessment() {
        return maatCourtDataService.postMeansAssessment(
                new MaatApiAssessmentRequest(),
                laaTransactionId,
                AssessmentRequestType.CREATE
        );
    }

    private String getPostProcessingSuccessMessage() {
        return String.format("Assessment post-processing successfully submitted for RepID: %d", repId);
    }
}
