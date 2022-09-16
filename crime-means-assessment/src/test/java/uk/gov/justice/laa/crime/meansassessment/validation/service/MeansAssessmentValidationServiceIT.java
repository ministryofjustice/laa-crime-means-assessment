package uk.gov.justice.laa.crime.meansassessment.validation.service;

import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.util.MaatWebClientIntegrationTestUtil;
import uk.gov.justice.laa.crime.meansassessment.util.MockMaatApiConfiguration;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.fail;
import static uk.gov.justice.laa.crime.meansassessment.common.Constants.ACTION_CREATE_ASSESSMENT;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getAuthorizationResponseDTO;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getOutstandingAssessmentResultDTO;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentValidationServiceIT extends MaatWebClientIntegrationTestUtil {

    private final Integer maxRetries = 2;

    @InjectMocks
    private MeansAssessmentValidationService meansAssessmentValidationService;

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
        RetryConfiguration retryConfiguration = generateRetryConfiguration(maxRetries, 1, 0.5);
        WebClient maatWebClient = buildWebClient(configuration, retryConfiguration, clientRegistrationRepository, authorizedClients);
        MaatCourtDataClient courtDataClient = new MaatCourtDataClient(maatWebClient);
        meansAssessmentValidationService = new MeansAssessmentValidationService(configuration, courtDataClient);
    }

    @After
    public void tearDown() throws IOException {
        shutdownMockWebServer();
    }

    @Test
    public void whenNworCodeIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setNewWorkReason(null);
        try {
            assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(0);
    }

    @Test
    public void whenNworCodeIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        setupMockApiResponses(response, 0);

        try {
            assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(1);

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        String expectedPath = String.format("/authorization/users/%s/work-reasons/%s",
                requestDTO.getUserSession().getUserName(), requestDTO.getNewWorkReason().getCode()
        );
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .isEqualTo(expectedPath);
    }

    @Test
    public void whenNworCodeIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, 0);

        try {
            assertThat(meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)).isTrue();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(1);

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        String expectedPath = String.format("/authorization/users/%s/work-reasons/%s",
                requestDTO.getUserSession().getUserName(), requestDTO.getNewWorkReason().getCode()
        );
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .isEqualTo(expectedPath);
    }

    @Test
    public void whenValidateNewWorkReasonIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, maxRetries - 1);

        try {
            assertThat(meansAssessmentValidationService.isNewWorkReasonValid(request)).isTrue();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(maxRetries.longValue());
    }

    @Test
    public void whenValidateNewWorkReasonIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, maxRetries + 1);

        assertThatThrownBy(
                () -> meansAssessmentValidationService.isNewWorkReasonValid(request)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(getRetryErrorResponse(maxRetries))
                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
    }

    @Test
    public void whenRoleActionIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.getUserSession().setUserName(null);

        try {
            assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, ACTION_CREATE_ASSESSMENT))
                    .isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(0);
    }

    @Test
    public void whenRoleActionIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        setupMockApiResponses(response, 0);

        try {
            assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, ACTION_CREATE_ASSESSMENT))
                    .isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(1);

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        String expectedPath = String.format("/authorization/users/%s/actions/%s",
                requestDTO.getUserSession().getUserName(), ACTION_CREATE_ASSESSMENT
        );
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .isEqualTo(expectedPath);
    }

    @Test
    public void whenRoleActionIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, 0);

        try {
            assertThat(meansAssessmentValidationService.isRoleActionValid(requestDTO, ACTION_CREATE_ASSESSMENT))
                    .isTrue();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(1);

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        String expectedPath = String.format("/authorization/users/%s/actions/%s",
                requestDTO.getUserSession().getUserName(), ACTION_CREATE_ASSESSMENT
        );
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .isEqualTo(expectedPath);
    }

    @Test
    public void whenRoleReservationIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setRepId(null);

        try {
            assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(0);
    }

    @Test
    public void whenRoleReservationIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        setupMockApiResponses(response, 0);

        try {
            assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(1);

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        String expectedPath = String.format("/authorization/users/%s/reservations/%s/sessions/%s",
                requestDTO.getUserSession().getUserName(),
                requestDTO.getRepId(),
                requestDTO.getUserSession().getSessionId()
        );
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .isEqualTo(expectedPath);
    }

    @Test
    public void whenRoleReservationIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, 0);

        try {
            assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isTrue();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(1);

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        String expectedPath = String.format("/authorization/users/%s/reservations/%s/sessions/%s",
                requestDTO.getUserSession().getUserName(),
                requestDTO.getRepId(),
                requestDTO.getUserSession().getSessionId()
        );
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .isEqualTo(expectedPath);
    }

    @Test
    public void whenValidateRoleReservationIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, maxRetries - 1);

        try {
            assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isTrue();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(maxRetries.longValue());
    }

    @Test
    public void whenValidateRoleReservationIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, maxRetries + 1);

        assertThatThrownBy(
                () -> meansAssessmentValidationService.isRepOrderReserved(requestDTO)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(getRetryErrorResponse(maxRetries))
                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
    }

    @Test
    public void whenOutstandingAssessmentRepIdIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setRepId(null);

        try {
            assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(0);
    }

    @Test
    public void whenOutstandingAssessmentsAreFound_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(true);
        setupMockApiResponses(response, 0);

        try {
            assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isTrue();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(1);

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        String expectedPath = String.format("/financial-assessments/check-outstanding/%d", requestDTO.getRepId());
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .isEqualTo(expectedPath);
    }

    @Test
    public void whenOutstandingAssessmentsAreNotFound_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
        setupMockApiResponses(response, 0);

        try {
            assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(1);

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        String expectedPath = String.format("/financial-assessments/check-outstanding/%d", requestDTO.getRepId());
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath())
                .isEqualTo(expectedPath);
    }

    @Test
    public void whenValidateOutstandingAssessmentsIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
        setupMockApiResponses(response, maxRetries - 1);

        try {
            assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(maxRetries.longValue());
    }

    @Test
    public void whenValidateOutstandingAssessmentsIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
        setupMockApiResponses(response, maxRetries + 1);

        assertThatThrownBy(
                () -> meansAssessmentValidationService.isOutstandingAssessment(requestDTO)
        ).isInstanceOf(APIClientException.class)
                .hasMessage(getRetryErrorResponse(maxRetries))
                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
    }
}
