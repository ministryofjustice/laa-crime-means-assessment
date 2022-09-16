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

import static org.junit.Assert.*;
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
            boolean result = meansAssessmentValidationService.isNewWorkReasonValid(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(0, mockMaatCourtDataApi.getRequestCount());
    }

    @Test
    public void whenNworCodeIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        setupMockApiResponses(response, 0);

        try {
            boolean result = meansAssessmentValidationService.isNewWorkReasonValid(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockMaatCourtDataApi.getRequestCount());

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserSession().getUserName() + "/work-reasons/" + requestDTO.getNewWorkReason().getCode(), recordedRequest.getPath());
    }

    @Test
    public void whenNworCodeIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, 0);

        try {
            boolean result = meansAssessmentValidationService.isNewWorkReasonValid(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockMaatCourtDataApi.getRequestCount());

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + request.getUserSession().getUserName() + "/work-reasons/" + request.getNewWorkReason().getCode(), recordedRequest.getPath());
    }

    @Test
    public void whenValidateNewWorkReasonIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, maxRetries - 1);

        try {
            boolean result = meansAssessmentValidationService.isNewWorkReasonValid(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(maxRetries.longValue(), mockMaatCourtDataApi.getRequestCount());
    }

    @Test
    public void whenValidateNewWorkReasonIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, maxRetries + 1);

        APIClientException error = assertThrows(
                APIClientException.class,
                () -> meansAssessmentValidationService.isNewWorkReasonValid(request)
        );
        validateRetryErrorResponse(error, maxRetries);
    }

    @Test
    public void whenRoleActionIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.getUserSession().setUserName(null);

        try {
            boolean result = meansAssessmentValidationService.isRoleActionValid(requestDTO, ACTION_CREATE_ASSESSMENT);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(0, mockMaatCourtDataApi.getRequestCount());
    }

    @Test
    public void whenRoleActionIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        setupMockApiResponses(response, 0);

        try {
            boolean result = meansAssessmentValidationService.isRoleActionValid(requestDTO, ACTION_CREATE_ASSESSMENT);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockMaatCourtDataApi.getRequestCount());

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserSession().getUserName() + "/actions/" + ACTION_CREATE_ASSESSMENT, recordedRequest.getPath());
    }

    @Test
    public void whenRoleActionIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, 0);

        try {
            boolean result = meansAssessmentValidationService.isRoleActionValid(requestDTO, ACTION_CREATE_ASSESSMENT);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockMaatCourtDataApi.getRequestCount());

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserSession().getUserName() + "/actions/" + ACTION_CREATE_ASSESSMENT, recordedRequest.getPath());
    }

    @Test
    public void whenRoleReservationIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setRepId(null);

        try {
            boolean result = meansAssessmentValidationService.isRepOrderReserved(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(0, mockMaatCourtDataApi.getRequestCount());
    }

    @Test
    public void whenRoleReservationIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        setupMockApiResponses(response, 0);

        try {
            boolean result = meansAssessmentValidationService.isRepOrderReserved(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockMaatCourtDataApi.getRequestCount());

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserSession().getUserName() + "/reservations/" + requestDTO.getRepId() + "/sessions/" + requestDTO.getUserSession().getSessionId(), recordedRequest.getPath());
    }

    @Test
    public void whenRoleReservationIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, 0);

        try {
            boolean result = meansAssessmentValidationService.isRepOrderReserved(requestDTO);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockMaatCourtDataApi.getRequestCount());

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserSession().getUserName() + "/reservations/" + requestDTO.getRepId() + "/sessions/" + requestDTO.getUserSession().getSessionId(), recordedRequest.getPath());
    }

    @Test
    public void whenValidateRoleReservationIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, maxRetries - 1);

        try {
            boolean result = meansAssessmentValidationService.isRepOrderReserved(requestDTO);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(maxRetries.longValue(), mockMaatCourtDataApi.getRequestCount());
    }

    @Test
    public void whenValidateRoleReservationIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        setupMockApiResponses(response, maxRetries + 1);

        APIClientException error = assertThrows(
                APIClientException.class,
                () -> meansAssessmentValidationService.isRepOrderReserved(requestDTO)
        );
        validateRetryErrorResponse(error, maxRetries);
    }

    @Test
    public void whenOutstandingAssessmentRepIdIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setRepId(null);

        try {
            boolean result = meansAssessmentValidationService.isOutstandingAssessment(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(0, mockMaatCourtDataApi.getRequestCount());
    }

    @Test
    public void whenOutstandingAssessmentsAreFound_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(true);
        setupMockApiResponses(response, 0);

        try {
            boolean result = meansAssessmentValidationService.isOutstandingAssessment(requestDTO);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockMaatCourtDataApi.getRequestCount());

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/financial-assessments/check-outstanding/" + requestDTO.getRepId(), recordedRequest.getPath());
    }

    @Test
    public void whenOutstandingAssessmentsAreNotFound_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
        setupMockApiResponses(response, 0);

        try {
            boolean result = meansAssessmentValidationService.isOutstandingAssessment(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockMaatCourtDataApi.getRequestCount());

        RecordedRequest recordedRequest = mockMaatCourtDataApi.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/financial-assessments/check-outstanding/" + requestDTO.getRepId(), recordedRequest.getPath());
    }

    @Test
    public void whenValidateOutstandingAssessmentsIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
        setupMockApiResponses(response, maxRetries - 1);

        try {
            boolean result = meansAssessmentValidationService.isOutstandingAssessment(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(maxRetries.longValue(), mockMaatCourtDataApi.getRequestCount());
    }

    @Test
    public void whenValidateOutstandingAssessmentsIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
        setupMockApiResponses(response, maxRetries + 1);

        APIClientException error = assertThrows(
                APIClientException.class,
                () -> meansAssessmentValidationService.isOutstandingAssessment(requestDTO)
        );
        validateRetryErrorResponse(error, maxRetries);
    }
}
