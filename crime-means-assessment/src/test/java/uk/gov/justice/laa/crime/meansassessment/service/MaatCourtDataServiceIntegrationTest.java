package uk.gov.justice.laa.crime.meansassessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.util.MaatWebClientIntegrationTestUtil;
import uk.gov.justice.laa.crime.meansassessment.util.MockMaatApiConfiguration;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class MaatCourtDataServiceIntegrationTest extends MaatWebClientIntegrationTestUtil {

    private final Integer maxRetries = 2;
    private final Integer repId = 1234;
    private final String laaTransactionId = "laa-transaction-id";
    RetryConfiguration retryConfiguration = generateRetryConfiguration(maxRetries, 1, 0.5);

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
        configuration = MockMaatApiConfiguration.getConfiguration(mockMaatCourtDataApi.getPort());

        WebClient maatWebClient =
                buildWebClient(configuration, retryConfiguration, clientRegistrationRepository, authorizedClients);

        MaatCourtDataClient maatCourtDataClient = Mockito.spy(new MaatCourtDataClient(maatWebClient));
        maatCourtDataService = new MaatCourtDataService(configuration, maatCourtDataClient);
    }

    @After
    public void tearDown() throws IOException {
        shutdownMockWebServer();
    }

    @Test
    public void whenPersistMeansAssessmentAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new MaatApiAssessmentRequest(), 0);
        MaatApiAssessmentResponse apiResponse = runPersistMeansAssessment();
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenPersistMeansAssessmentAPICalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();

        assertThatThrownBy(
                this::runPersistMeansAssessment
        ).isInstanceOf(APIClientException.class)
                .hasMessage(INVALID_RESPONSE_ERROR_MSG);
    }

    @Test
    public void whenPersistMeansAssessmentAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new MaatApiAssessmentRequest(), maxRetries - 1);
        MaatApiAssessmentResponse apiResponse = runPersistMeansAssessment();
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenPersistMeansAssessmentAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new MaatApiAssessmentResponse(), maxRetries + 1);
        assertThatThrownBy(
                this::runPersistMeansAssessment
        ).isInstanceOf(APIClientException.class)
                .hasMessage(getRetryErrorResponse(maxRetries))
                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new PassportAssessmentDTO(), 0);
        PassportAssessmentDTO apiResponse =
                maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();

        assertThatThrownBy(
                () -> maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(INVALID_RESPONSE_ERROR_MSG);
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new PassportAssessmentDTO(), maxRetries - 1);
        PassportAssessmentDTO apiResponse =
                maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallReturnsNotFoundResponse_thenMethodReturnsNull() {
        setupMockNotFoundResponse();
        PassportAssessmentDTO apiResponse = maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);
        assertThat(apiResponse).isNull();
    }

    @Test
    public void whenGetPassportAssessmentFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new PassportAssessmentDTO(), maxRetries + 1);

        assertThatThrownBy(
                () -> maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(getRetryErrorResponse(maxRetries))
                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPIAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new HardshipReviewDTO(), 0);
        HardshipReviewDTO apiResponse =
                maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();

        assertThatThrownBy(
                () -> maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(INVALID_RESPONSE_ERROR_MSG);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new HardshipReviewDTO(), maxRetries - 1);
        HardshipReviewDTO apiResponse = maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new HardshipReviewDTO(), maxRetries + 1);

        assertThatThrownBy(
                () -> maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(getRetryErrorResponse(maxRetries))
                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
    }

    @Test
    public void whenGetHardshipReviewFromRepIdAPICallReturnsNotFoundResponse_thenMethodReturnsNull() {
        setupMockNotFoundResponse();
        HardshipReviewDTO apiResponse = maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);
        assertThat(apiResponse).isNull();
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPIAPICalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new IOJAppealDTO(), maxRetries - 1);
        IOJAppealDTO apiResponse =
                maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();

        assertThatThrownBy(
                () -> maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(INVALID_RESPONSE_ERROR_MSG);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new IOJAppealDTO(), maxRetries - 1);
        IOJAppealDTO apiResponse =
                maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new IOJAppealDTO(), maxRetries + 1);

        assertThatThrownBy(
                () -> maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(getRetryErrorResponse(maxRetries))
                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
    }

    @Test
    public void whenGetIOJAppealFromRepIdAPICallReturnsNotFoundResponse_thenMethodReturnsNull() {
        setupMockNotFoundResponse();
        IOJAppealDTO apiResponse = maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);
        assertThat(apiResponse).isNull();
    }

    @Test
    public void whenGetFinancialAssessmentIsCalled_thenTheCorrectResponseIsReturnedWhenItSucceedsFirstTime() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new FinancialAssessmentDTO(), 0);
        FinancialAssessmentDTO apiResponse =
                maatCourtDataService.getFinancialAssessment(repId, laaTransactionId);
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenGetFinancialAssessmentIsCalled_thenInvalidResponseErrorsAreHandledCorrectly() {
        setupMockInvalidResponse();

        assertThatThrownBy(
                () -> maatCourtDataService.getFinancialAssessment(repId, laaTransactionId)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(INVALID_RESPONSE_ERROR_MSG);
    }

    @Test
    public void whenGetFinancialAssessmentCallFails_thenTheCallIsRetriedAndSucceeds() throws JsonProcessingException {
        String mockResponse = setupMockApiResponses(new FinancialAssessmentDTO(), maxRetries - 1);
        FinancialAssessmentDTO apiResponse =
                maatCourtDataService.getFinancialAssessment(repId, laaTransactionId);
        assertThat(OBJECT_MAPPER.writeValueAsString(apiResponse)).isEqualTo(mockResponse);
    }

    @Test
    public void whenGetFinancialAssessmentCallReturnsNotFoundResponse_thenMethodReturnsNull() {
        setupMockNotFoundResponse();
        FinancialAssessmentDTO apiResponse = maatCourtDataService.getFinancialAssessment(repId, laaTransactionId);
        assertThat(apiResponse).isNull();
    }

    @Test
    public void whenGetFinancialAssessmentCallFails_thenTheCallIsRetriedAndFails() throws JsonProcessingException {
        setupMockApiResponses(new FinancialAssessmentDTO(), maxRetries + 1);

        assertThatThrownBy(
                () -> maatCourtDataService.getFinancialAssessment(repId, laaTransactionId)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(getRetryErrorResponse(maxRetries))
                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
    }

    private MaatApiAssessmentResponse runPersistMeansAssessment() {
        return maatCourtDataService.persistMeansAssessment(
                new MaatApiAssessmentRequest(),
                laaTransactionId,
                AssessmentRequestType.CREATE
        );
    }
}
