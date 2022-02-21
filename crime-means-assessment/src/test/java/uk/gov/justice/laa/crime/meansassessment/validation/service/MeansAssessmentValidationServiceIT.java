package uk.gov.justice.laa.crime.meansassessment.validation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

import java.io.IOException;

import static org.junit.Assert.*;
import static uk.gov.justice.laa.crime.meansassessment.common.Constants.ACTION_CREATE_ASSESSMENT;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getAuthorizationResponseDTO;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getOutstandingAssessmentResultDTO;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentValidationServiceIT {

    public static MockWebServer mockBackEnd;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private MeansAssessmentValidationService meansAssessmentValidationService;

    @Before
    public void initialize() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        meansAssessmentValidationService = new MeansAssessmentValidationService();
        ReflectionTestUtils.setField(meansAssessmentValidationService, "maatAPIBaseUrl", baseUrl);
        ReflectionTestUtils.setField(meansAssessmentValidationService, "validateRoleActionEndpoint", "/authorization/users/{username}/actions/{action}");
        ReflectionTestUtils.setField(meansAssessmentValidationService, "validateNewWorkReasonEndpoint", "/authorization/users/{username}/work-reasons/{nworCode}");
        ReflectionTestUtils.setField(meansAssessmentValidationService, "validateReservationEndpoint", "/authorization/users/{username}/reservations/{reservationId}/sessions/{sessionId}");
        ReflectionTestUtils.setField(meansAssessmentValidationService, "checkOutstandingAssessmentsEndpoint", "/financial-assessments/check-outstanding/{repId}");
        meansAssessmentValidationService.initializeWebClient();
    }

    @After
    public void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    public void whenNworCodeIsNull_thenFalseResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        request.setNewWorkReason(null);
        try {
            boolean result = meansAssessmentValidationService.validateNewWorkReason(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(0, mockBackEnd.getRequestCount());
    }

    @Test
    public void whenNworCodeIsInvalid_thenFalseResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateNewWorkReason(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/"+request.getUserId()+"/work-reasons/"+request.getNewWorkReason().getCode(), recordedRequest.getPath());
    }

    @Test
    public void whenNworCodeIsValid_thenTrueResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateNewWorkReason(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/"+request.getUserId()+"/work-reasons/"+request.getNewWorkReason().getCode(), recordedRequest.getPath());
    }

    @Test
    public void whenRoleActionIsNull_thenFalseResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        request.setUserId(null);

        try {
            boolean result = meansAssessmentValidationService.validateRoleAction(request, ACTION_CREATE_ASSESSMENT);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(0, mockBackEnd.getRequestCount());
    }

    @Test
    public void whenRoleActionIsInvalid_thenFalseResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);

        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateRoleAction(request, ACTION_CREATE_ASSESSMENT);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/"+request.getUserId()+"/actions/"+ACTION_CREATE_ASSESSMENT, recordedRequest.getPath());
    }

    @Test
    public void whenRoleActionIsValid_thenTrueResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateRoleAction(request, ACTION_CREATE_ASSESSMENT);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/"+request.getUserId()+"/actions/"+ACTION_CREATE_ASSESSMENT, recordedRequest.getPath());
    }

    @Test
    public void whenRoleReservationIsNull_thenFalseResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        request.setReservationId(null);

        try {
            boolean result = meansAssessmentValidationService.validateRoleReservation(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(0, mockBackEnd.getRequestCount());
    }

    @Test
    public void whenRoleReservationIsInvalid_thenFalseResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);

        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateRoleReservation(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/"+request.getUserId()+"/reservations/"+request.getReservationId()+"/sessions/"+request.getSessionId(), recordedRequest.getPath());
    }

    @Test
    public void whenRoleReservationIsValid_thenTrueResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateRoleReservation(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/authorization/users/"+request.getUserId()+"/reservations/"+request.getReservationId()+"/sessions/"+request.getSessionId(), recordedRequest.getPath());
    }


    @Test
    public void whenOutstandingAssessmentRepIdIsNull_thenFalseResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        request.setRepId(null);

        try {
            boolean result = meansAssessmentValidationService.validateOutstandingAssessments(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(0, mockBackEnd.getRequestCount());
    }

    @Test
    public void whenOutstandingAssessmentsAreFound_thenFalseResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);

        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(true);
        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateOutstandingAssessments(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/financial-assessments/check-outstanding/"+request.getRepId(), recordedRequest.getPath());
    }

    @Test
    public void whenOutstandingAssessmentsAreNotFound_thenTrueResultIsReturned() throws Exception{
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);

        mockBackEnd.enqueue(new MockResponse().setBody(OBJECT_MAPPER.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
        try {
            boolean result = meansAssessmentValidationService.validateOutstandingAssessments(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : "+e.getMessage());
        }
        assertEquals(1, mockBackEnd.getRequestCount());

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/financial-assessments/check-outstanding/"+request.getRepId(), recordedRequest.getPath());
    }
}
