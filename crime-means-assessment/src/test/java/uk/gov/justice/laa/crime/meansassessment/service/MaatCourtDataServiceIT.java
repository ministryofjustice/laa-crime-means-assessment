package uk.gov.justice.laa.crime.meansassessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiOAuth2Client;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class MaatCourtDataServiceIT {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static MockWebServer mockMaatCourtDataApi;
    private final Integer maxRetries = 2;

    private MaatCourtDataService maatCourtDataService;

    @Mock
    private MaatApiConfiguration configuration;

    @Mock
    private OAuth2AuthorizedClientManager authClientManager;

    @Before
    public void initialize() throws IOException {
        mockMaatCourtDataApi = new MockWebServer();
        mockMaatCourtDataApi.start();
        String baseUrl = String.format("http://localhost:%s", mockMaatCourtDataApi.getPort());
        configuration = new MaatApiConfiguration();
        configuration.setBaseUrl(baseUrl);
        configuration.setOAuthEnabled(false);


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
                "/financial-assessments/{financialAssessmentId}"
        );

        configuration.setPassportAssessmentEndpoints(passportEndpoints);
        configuration.setHardshipReviewEndpoints(hardshipEndpoints);
        configuration.setIojAppealEndpoints(iojEndpoints);
        configuration.setFinancialAssessmentEndpoints(financialAssessmentEndpoints);

        RetryConfiguration retryConfiguration = new RetryConfiguration();
        retryConfiguration.setMaxRetries(maxRetries);
        retryConfiguration.setMinBackOffPeriod(1);
        retryConfiguration.setJitterValue(0.5);

        WebClient maatWebClient = buildWebClient(configuration, retryConfiguration, authClientManager);
        maatCourtDataService = new MaatCourtDataService(maatWebClient, configuration);
    }

    @After
    public void tearDown() throws IOException {
        mockMaatCourtDataApi.shutdown();
    }

    @Test
    public void whenPostMeansAssessmentAPICallSucceedsFirstTime_thenTheCorrectResponseIsReturned() throws JsonProcessingException {
        MaatApiAssessmentRequest request = new MaatApiAssessmentRequest();

        String mockResponse = setupMockApiResponses(request, 0);
        MaatApiAssessmentResponse apiResponse =
                maatCourtDataService.postMeansAssessment(
                        new MaatApiAssessmentRequest(),
                        "laa-transaction-id",
                        configuration.getFinancialAssessmentEndpoints().getCreateUrl()
                );
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenPostMeansAssessmentAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        MaatApiAssessmentRequest request = new MaatApiAssessmentRequest();

        String mockResponse = setupMockApiResponses(request, maxRetries - 1);
        MaatApiAssessmentResponse apiResponse =
                maatCourtDataService.postMeansAssessment(
                        new MaatApiAssessmentRequest(),
                        "laa-transaction-id",
                        configuration.getFinancialAssessmentEndpoints().getCreateUrl()
                );
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenPostMeansAssessmentAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new MaatApiAssessmentResponse(), maxRetries + 1);
        APIClientException error = assertThrows(APIClientException.class, () -> maatCourtDataService.postMeansAssessment(
                        new MaatApiAssessmentRequest(),
                        "laa-transaction-id",
                        configuration.getFinancialAssessmentEndpoints().getCreateUrl()
                )
        );
        validateErrorResponse(error);
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallSucceedsFirstTime_thenTheCorrectResponseIsReturned() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new PassportAssessmentDTO(), 0);
        PassportAssessmentDTO apiResponse =
                maatCourtDataService.getPassportAssessmentFromRepId(001, "laa-transaction-id");
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new PassportAssessmentDTO(), maxRetries - 1);
        PassportAssessmentDTO apiResponse =
                maatCourtDataService.getPassportAssessmentFromRepId(001, "laa-transaction-id");
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new PassportAssessmentDTO(), maxRetries + 1);
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getPassportAssessmentFromRepId(001, "laa-transaction-id")
        );
        validateErrorResponse(error);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallSucceedsFirstTime_thenTheCorrectResponseIsReturned() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new HardshipReviewDTO(), 0);
        HardshipReviewDTO apiResponse =
                maatCourtDataService.getHardshipReviewFromRepId(001, "laa-transaction-id");
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new HardshipReviewDTO(), maxRetries - 1);
        HardshipReviewDTO apiResponse =
                maatCourtDataService.getHardshipReviewFromRepId(001, "laa-transaction-id");
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new HardshipReviewDTO(), maxRetries + 1);
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getHardshipReviewFromRepId(001, "laa-transaction-id")
        );
        validateErrorResponse(error);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallSucceedsFirstTime_thenTheCorrectResponseIsReturned() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new IOJAppealDTO(), maxRetries - 1);
        IOJAppealDTO apiResponse =
                maatCourtDataService.getIOJAppealFromRepId(001, "laa-transaction-id");
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new IOJAppealDTO(), maxRetries - 1);
        IOJAppealDTO apiResponse =
                maatCourtDataService.getIOJAppealFromRepId(001, "laa-transaction-id");
        assertEquals(mockResponse, OBJECT_MAPPER.writeValueAsString(apiResponse));
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new IOJAppealDTO(), maxRetries + 1);
        APIClientException error = assertThrows(
                APIClientException.class,
                () -> maatCourtDataService.getIOJAppealFromRepId(001, "laa-transaction-id")
        );
        validateErrorResponse(error);
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

    private void validateErrorResponse(APIClientException error) {
        assertEquals(String.format("Call to Court Data API failed. Retries exhausted: %d/%d.", maxRetries, maxRetries), error.getMessage());
        assertEquals("503 Received error 503 due to Service Unavailable", error.getCause().getLocalizedMessage());
    }

    private WebClient buildWebClient(
            MaatApiConfiguration maatConfig,
            RetryConfiguration retryConfiguration,
            OAuth2AuthorizedClientManager authClientManager
    ) {
        MaatApiOAuth2Client client = new MaatApiOAuth2Client(maatConfig, retryConfiguration);

        return client.webClient(authClientManager);
    }
}
