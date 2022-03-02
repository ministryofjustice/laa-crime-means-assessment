package uk.gov.justice.laa.crime.meansassessment.validation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static uk.gov.justice.laa.crime.meansassessment.common.Constants.ACTION_CREATE_ASSESSMENT;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getAuthorizationResponseDTO;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getOutstandingAssessmentResultDTO;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentValidationServiceTest {

    private static WebClient webClient;
    public static final String MAAT_API_BASE_URL = "http://localhost:8090/";

    private MaatApiConfiguration configuration;
    private MeansAssessmentValidationService meansAssessmentValidationService;

    private WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
    private WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
    private WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    private WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);


    @Before
    public void setup() {
        webClient = mock(WebClient.class);
        configuration = new MaatApiConfiguration();
        configuration.setBaseUrl(MAAT_API_BASE_URL);
        MaatApiConfiguration.ValidationEndpoints validationEndpoints = new MaatApiConfiguration.ValidationEndpoints(
                "/authorization/users/{username}/actions/{action}",
                "/authorization/users/{username}/work-reasons/{nworCode}",
                "/authorization/users/{username}/reservations/{reservationId}/sessions/{sessionId}",
                "/financial-assessments/check-outstanding/{repId}"
        );
        configuration.setValidationEndpoints(validationEndpoints);
        meansAssessmentValidationService = new MeansAssessmentValidationService(configuration);
        ReflectionTestUtils.setField(meansAssessmentValidationService, "webClient", webClient);
    }

    @Test
    public void whenRepIdIsNull_thenFalseResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        request.setRepId(null);
        assertFalse(meansAssessmentValidationService.isRepIdPresentForCreateAssessment(request));
    }

    @Test
    public void whenRepIdIsNegative_thenFalseResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        request.setRepId(-1);
        assertFalse(meansAssessmentValidationService.isRepIdPresentForCreateAssessment(request));
    }

    @Test
    public void whenRepIdIsValid_thenTrueResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        assertTrue(meansAssessmentValidationService.isRepIdPresentForCreateAssessment(request));
    }

    @Test
    public void whenNworCodeIsInvalid_thenFalseResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getNewWorkReasonUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateNewWorkReason(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }

        StepVerifier.create(Mono.just(response))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void whenNworCodeIsValid_thenTrueResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getNewWorkReasonUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateNewWorkReason(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }

        StepVerifier.create(Mono.just(response))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void whenRoleActionIsInvalid_thenFalseResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getRoleActionUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateRoleAction(request, ACTION_CREATE_ASSESSMENT);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }

        StepVerifier.create(Mono.just(response))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void whenRoleActionIsValid_thenTrueResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getRoleActionUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateRoleAction(request, ACTION_CREATE_ASSESSMENT);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }

        StepVerifier.create(Mono.just(response))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void whenRoleReservationIsInvalid_thenFalseResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getReservationsUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateRoleReservation(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }

        StepVerifier.create(Mono.just(response))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void whenRoleReservationIsValid_thenTrueResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getReservationsUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateRoleReservation(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }

        StepVerifier.create(Mono.just(response))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void whenOutstandingAssessmentsAreFound_thenFalseResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getOutstandingAssessmentsUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OutstandingAssessmentResultDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateOutstandingAssessments(request);
            assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }

        StepVerifier.create(Mono.just(response))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void whenOutstandingAssessmentsAreNotFound_thenTrueResultIsReturned() {
        ApiCreateMeansAssessmentRequest request = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getOutstandingAssessmentsUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OutstandingAssessmentResultDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateOutstandingAssessments(request);
            assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("UnexpectedException : " + e.getMessage());
        }

        StepVerifier.create(Mono.just(response))
                .expectNext(response)
                .verifyComplete();
    }
}
