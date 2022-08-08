package uk.gov.justice.laa.crime.meansassessment.validation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;

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
    private MeansAssessmentRequestDTO requestDTO;
    private MeansAssessmentValidationService meansAssessmentValidationService;

    private final WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
    private final WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
    private final WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    private final WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    private final WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);


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
        requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        meansAssessmentValidationService = new MeansAssessmentValidationService(webClient, configuration);
    }

    @Test
    public void whenGetUserIdFromRequestIsCalled_thenUserIdIsReturned() {
        assertEquals(meansAssessmentValidationService.getUserIdFromRequest(requestDTO), TestModelDataBuilder.TEST_USER);
    }

    @Test
    public void whenGetWebClientIsCalled_thenTheClientIsReturned() {
        assertEquals(webClient, meansAssessmentValidationService.getWebClient());
    }

    @Test
    public void whenRepIdIsNull_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        request.setRepId(null);
        assertFalse(meansAssessmentValidationService.isRepIdPresentForCreateAssessment(request));
    }

    @Test
    public void whenRepIdIsNegative_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        requestDTO.setRepId(-1);
        assertFalse(meansAssessmentValidationService.isRepIdPresentForCreateAssessment(requestDTO));
    }

    @Test
    public void whenRepIdIsValid_thenTrueResultIsReturned() {
        MeansAssessmentRequestDTO request = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        assertTrue(meansAssessmentValidationService.isRepIdPresentForCreateAssessment(request));
    }

    @Test
    public void whenNworCodeIsInvalid_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getNewWorkReasonUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateNewWorkReason(requestDTO);
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
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getNewWorkReasonUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateNewWorkReason(requestDTO);
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
    public void whenValidateRoleActionIsCalledWithBlankUserId_thenValidationFails() {
        requestDTO.getUserSession().setUserName("");
        assertEquals(meansAssessmentValidationService
                .validateRoleAction(requestDTO, ACTION_CREATE_ASSESSMENT), Boolean.FALSE
        );
    }

    @Test
    public void whenValidateRoleActionIsCalledWithBlankAction_thenValidationFails() {
        assertEquals(meansAssessmentValidationService
                .validateRoleAction(requestDTO, ""), Boolean.FALSE
        );
    }

    @Test
    public void whenRoleActionIsInvalid_thenFalseResultIsReturned() {
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getRoleActionUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateRoleAction(requestDTO, ACTION_CREATE_ASSESSMENT);
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
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getRoleActionUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateRoleAction(requestDTO, ACTION_CREATE_ASSESSMENT);
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
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getReservationsUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateRoleReservation(requestDTO);
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
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        AuthorizationResponseDTO response = getAuthorizationResponseDTO(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getReservationsUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthorizationResponseDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateRoleReservation(requestDTO);
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
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getOutstandingAssessmentsUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OutstandingAssessmentResultDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateOutstandingAssessments(requestDTO);
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
        MeansAssessmentRequestDTO requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        OutstandingAssessmentResultDTO response = getOutstandingAssessmentResultDTO(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(configuration.getValidationEndpoints().getOutstandingAssessmentsUrl()), any(HashMap.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OutstandingAssessmentResultDTO.class)).thenReturn(Mono.just(response));

        boolean result;
        try {
            result = meansAssessmentValidationService.validateOutstandingAssessments(requestDTO);
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
