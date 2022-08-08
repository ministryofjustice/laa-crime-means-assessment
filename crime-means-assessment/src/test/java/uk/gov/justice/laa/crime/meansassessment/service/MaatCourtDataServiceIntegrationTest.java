package uk.gov.justice.laa.crime.meansassessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.util.MaatWebClientIntegrationTestUtil;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MaatCourtDataServiceIntegrationTest extends MaatWebClientIntegrationTestUtil {

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
        startMockWebServer();
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

        RetryConfiguration retryConfiguration = generateRetryConfiguration(maxRetries, 1, 0.5);

        WebClient maatWebClient = buildWebClient(configuration, retryConfiguration, clientRegistrationRepository, authorizedClients);
        maatCourtDataService = new MaatCourtDataService(maatWebClient, configuration);
    }

    @After
    public void tearDown() throws IOException {
        shutdownMockWebServer();
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
        validateRetryErrorResponse(error, maxRetries);
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
        validateRetryErrorResponse(error, maxRetries);
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
        validateRetryErrorResponse(error, maxRetries);
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
        validateRetryErrorResponse(error, maxRetries);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallReturnsNotFoundResponse_thenMethodReturnsNull() {
        setupMockNotFoundResponse();
        IOJAppealDTO apiResponse = maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);
        assertNull(apiResponse);
    }

    private MaatApiAssessmentResponse runPostMeansAssessment() {
        return maatCourtDataService.persistMeansAssessment(
                new MaatApiAssessmentRequest(),
                laaTransactionId,
                AssessmentRequestType.CREATE
        );
    }

    private String getPostProcessingSuccessMessage() {
        return String.format("Assessment post-processing successfully submitted for RepID: %d", repId);
    }
}
