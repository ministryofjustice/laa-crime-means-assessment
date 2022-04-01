package uk.gov.justice.laa.crime.meansassessment.validation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;

import java.io.IOException;

import static org.junit.Assert.*;
import static uk.gov.justice.laa.crime.meansassessment.common.Constants.ACTION_CREATE_ASSESSMENT;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getAuthorizationResponseDTO;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getOutstandingAssessmentResultDTO;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentValidationServiceIT {

    public static MockWebServer mockBackEnd;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @InjectMocks
    private MeansAssessmentValidationService meansAssessmentValidationService;

    @Mock
    private MaatApiConfiguration configuration;

    @Before
    public void initialize() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        configuration = new MaatApiConfiguration();
        configuration.setBaseUrl(String.format("http://localhost:%s", mockBackEnd.getPort()));
        MaatApiConfiguration.ValidationEndpoints validationEndpoints = new MaatApiConfiguration.ValidationEndpoints(
                "/authorization/users/{username}/actions/{action}",
                "/authorization/users/{username}/work-reasons/{nworCode}",
                "/authorization/users/{username}/reservations/{reservationId}/sessions/{sessionId}",
                "/financial-assessments/check-outstanding/{repId}"
        );
        configuration.setValidationEndpoints(validationEndpoints);
        meansAssessmentValidationService = new MeansAssessmentValidationService(configuration);
        meansAssessmentValidationService.initializeWebClient();
    }

    @After
    public void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    public void whenNworCodeIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setNewWorkReason(null);
        try {
            boolean result = meansAssessmentValidationService.validateNewWorkReason(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(0, mockBackEnd.getRequestCount());
    }

    @Test
    public void whenNworCodeIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateNewWorkReason(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserId() + "/work-reasons/" + requestDTO.getNewWorkReason().getCode(), recordedRequest.getPath());
    }

    @Test
    public void whenNworCodeIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateNewWorkReason(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + request.getUserId() + "/work-reasons/" + request.getNewWorkReason().getCode(), recordedRequest.getPath());
    }

    @Test
    public void whenRoleActionIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setUserId(null);

        try {
            boolean result = meansAssessmentValidationService.validateRoleAction(requestDTO, ACTION_CREATE_ASSESSMENT);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(0, mockBackEnd.getRequestCount());
    }

    @Test
    public void whenRoleActionIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);

        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateRoleAction(requestDTO, ACTION_CREATE_ASSESSMENT);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserId() + "/actions/" + ACTION_CREATE_ASSESSMENT, recordedRequest.getPath());
    }

    @Test
    public void whenRoleActionIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateRoleAction(requestDTO, ACTION_CREATE_ASSESSMENT);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserId() + "/actions/" + ACTION_CREATE_ASSESSMENT, recordedRequest.getPath());
    }

    @Test
    public void whenRoleReservationIsNull_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setRepId(null);

        try {
            boolean result = meansAssessmentValidationService.validateRoleReservation(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(0, mockBackEnd.getRequestCount());
    }

    @Test
    public void whenRoleReservationIsInvalid_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);

        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateRoleReservation(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserId() + "/reservations/" + requestDTO.getRepId() + "/sessions/" + requestDTO.getUserSession().getSessionId(), recordedRequest.getPath());
    }

    @Test
    public void whenRoleReservationIsValid_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateRoleReservation(requestDTO);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/" + requestDTO.getUserId() + "/reservations/" + requestDTO.getRepId() + "/sessions/" + requestDTO.getUserSession().getSessionId(), recordedRequest.getPath());
    }


    @Test
    public void whenOutstandingAssessmentRepIdIsNull_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setRepId(null);

        try {
            boolean result = meansAssessmentValidationService.validateOutstandingAssessments(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(0, mockBackEnd.getRequestCount());
    }

    @Test
    public void whenOutstandingAssessmentsAreFound_thenFalseResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);

        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(true);
        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateOutstandingAssessments(requestDTO);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/financial-assessments/check-outstanding/" + requestDTO.getRepId(), recordedRequest.getPath());
    }

    @Test
    public void whenOutstandingAssessmentsAreNotFound_thenTrueResultIsReturned() throws Exception {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateOutstandingAssessments(requestDTO);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/financial-assessments/check-outstanding/" + requestDTO.getRepId(), recordedRequest.getPath());
    }
}
