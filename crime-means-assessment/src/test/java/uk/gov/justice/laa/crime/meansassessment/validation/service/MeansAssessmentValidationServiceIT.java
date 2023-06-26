package uk.gov.justice.laa.crime.meansassessment.validation.service;

import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.meansassessment.CrimeMeansAssessmentApplication;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.meansassessment.util.MaatWebClientIntegrationTestUtil;
import uk.gov.justice.laa.crime.meansassessment.util.MockMaatApiConfiguration;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.fail;
import static uk.gov.justice.laa.crime.meansassessment.common.Constants.ACTION_CREATE_ASSESSMENT;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getAuthorizationResponseDTO;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getOutstandingAssessmentResultDTO;

@DirtiesContext
@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CrimeMeansAssessmentApplication.class)
public class MeansAssessmentValidationServiceIT extends MaatWebClientIntegrationTestUtil {

    private static final String OAUTH_PATH = "/oauth2/token";
    private MeansAssessmentValidationService meansAssessmentValidationService;
    @Autowired
    private MaatCourtDataService maatCourtDataService;
    @Autowired
    private RestAPIClient maatAPIClient;

    @Before
    public void initialize() throws IOException {
        startMockWebServer();
        setOAuthDispatcher();
        MaatApiConfiguration configuration = MockMaatApiConfiguration.getConfiguration(mockMaatCourtDataApi.getPort());
        meansAssessmentValidationService = new MeansAssessmentValidationService(configuration, maatCourtDataService, maatAPIClient);
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isZero();
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(2);
        String expectedPath = String.format("/authorization/users/%s/work-reasons/%s",
                requestDTO.getUserSession().getUserName(), requestDTO.getNewWorkReason().getCode()
        );
        assertRecordedRequest(expectedPath);
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(2);

        String expectedPath = String.format("/authorization/users/%s/work-reasons/%s",
                requestDTO.getUserSession().getUserName(), requestDTO.getNewWorkReason().getCode()
        );
        assertRecordedRequest(expectedPath);
    }

//    @Test
//    public void whenValidateNewWorkReasonIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
//        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
//        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
//        setupMockApiResponses(response, maxRetries - 1);
//
//        try {
//            assertThat(meansAssessmentValidationService.isNewWorkReasonValid(request)).isTrue();
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("UnexpectedException : " + e.getMessage());
//        }
//        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(maxRetries.longValue());
//    }

//    @Test
//    public void whenValidateNewWorkReasonIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
//        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
//        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
//        setupMockApiResponses(response, maxRetries + 1);
//
//        assertThatThrownBy(
//                () -> meansAssessmentValidationService.isNewWorkReasonValid(request)
//        ).isInstanceOf(APIClientException.class)
//                .hasMessage(getRetryErrorResponse(maxRetries))
//                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
//    }

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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isZero();
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(2);
        String expectedPath = String.format("/authorization/users/%s/actions/%s",
                requestDTO.getUserSession().getUserName(), ACTION_CREATE_ASSESSMENT
        );
        assertRecordedRequest(expectedPath);
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(2);
        String expectedPath = String.format("/authorization/users/%s/actions/%s",
                requestDTO.getUserSession().getUserName(), ACTION_CREATE_ASSESSMENT
        );
        assertRecordedRequest(expectedPath);
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isZero();
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(2);
        String expectedPath = String.format("/authorization/users/%s/reservations/%s/sessions/%s",
                requestDTO.getUserSession().getUserName(),
                requestDTO.getRepId(),
                requestDTO.getUserSession().getSessionId()
        );
        assertRecordedRequest(expectedPath);
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(2);
        String expectedPath = String.format("/authorization/users/%s/reservations/%s/sessions/%s",
                requestDTO.getUserSession().getUserName(),
                requestDTO.getRepId(),
                requestDTO.getUserSession().getSessionId()
        );
        assertRecordedRequest(expectedPath);
    }

//    @Test
//    public void whenValidateRoleReservationIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
//        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
//        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
//        setupMockApiResponses(response, maxRetries - 1);
//
//        try {
//            assertThat(meansAssessmentValidationService.isRepOrderReserved(requestDTO)).isTrue();
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("UnexpectedException : " + e.getMessage());
//        }
//        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(maxRetries.longValue());
//    }

//    @Test
//    public void whenValidateRoleReservationIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
//        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
//        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
//        setupMockApiResponses(response, maxRetries + 1);
//
//        assertThatThrownBy(
//                () -> meansAssessmentValidationService.isRepOrderReserved(requestDTO)
//        ).isInstanceOf(APIClientException.class)
//                .hasMessage(getRetryErrorResponse(maxRetries))
//                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
//    }

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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isZero();
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(2);
        String expectedPath = String.format("/financial-assessments/check-outstanding/%d", requestDTO.getRepId());
        assertRecordedRequest(expectedPath);
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
        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(2);
        String expectedPath = String.format("/financial-assessments/check-outstanding/%d", requestDTO.getRepId());
        assertRecordedRequest(expectedPath);
    }

//    @Test
//    public void whenValidateOutstandingAssessmentsIsCalledAndTheFirstRequestFail_thenTheRequestIsRetried() throws Exception {
//        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
//        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
//        setupMockApiResponses(response, maxRetries - 1);
//
//        try {
//            assertThat(meansAssessmentValidationService.isOutstandingAssessment(requestDTO)).isFalse();
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("UnexpectedException : " + e.getMessage());
//        }
//        assertThat(mockMaatCourtDataApi.getRequestCount()).isEqualTo(maxRetries.longValue());
//    }

//    @Test
//    public void whenValidateOutstandingAssessmentsIsCalledAndTheAllRetriesFail_thenTheCorrectErrorIsReported() throws Exception {
//        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
//        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
//        setupMockApiResponses(response, maxRetries + 1);
//
//        assertThatThrownBy(
//                () -> meansAssessmentValidationService.isOutstandingAssessment(requestDTO)
//        ).isInstanceOf(APIClientException.class)
//                .hasMessage(getRetryErrorResponse(maxRetries))
//                .hasRootCauseMessage(SERVICE_UNAVAILABLE_ERROR_MSG);
//    }

    private void assertRecordedRequest(String expectedPath) throws InterruptedException {
        RecordedRequest recordedRequest1 = mockMaatCourtDataApi.takeRequest();
        RecordedRequest recordedRequest2 = mockMaatCourtDataApi.takeRequest();

        assertThat(recordedRequest1.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest1.getPath()).isEqualTo(OAUTH_PATH);
        assertThat(recordedRequest2.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest2.getPath()).isEqualTo(expectedPath);
    }
}
