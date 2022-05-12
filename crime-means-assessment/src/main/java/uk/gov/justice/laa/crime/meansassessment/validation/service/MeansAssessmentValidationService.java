package uk.gov.justice.laa.crime.meansassessment.validation.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.justice.laa.crime.meansassessment.common.Constants.*;

/**
 * This service provides methods for validation of means assessment requests
 */
@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class MeansAssessmentValidationService {

    @Qualifier("maatAPIOAuth2WebClient")
    private final WebClient webClient;
    private final MaatApiConfiguration configuration;

    public boolean validateRoleAction(final MeansAssessmentRequestDTO meansAssessmentRequest, String action) {
        boolean result = false;
        if (StringUtils.isNotBlank(meansAssessmentRequest.getUserId()) && StringUtils.isNotBlank(action)) {
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, meansAssessmentRequest.getUserId());
            uriVariables.put(URIVAR_ACTION, action);
            Optional<AuthorizationResponseDTO> apiResponse = getApiResponseViaGET(
                    configuration.getValidationEndpoints().getRoleActionUrl(), uriVariables, AuthorizationResponseDTO.class
            );
            if (apiResponse.isPresent()) {
                result = apiResponse.get().isResult();
            }
        }
        return result;
    }

    public boolean validateNewWorkReason(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        boolean result = false;
        if (meansAssessmentRequest.getNewWorkReason() != null && StringUtils.isNotBlank(meansAssessmentRequest.getNewWorkReason().getCode())) {
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, meansAssessmentRequest.getUserId());
            uriVariables.put(URIVAR_NWOR_CODE, meansAssessmentRequest.getNewWorkReason().getCode());
            Optional<AuthorizationResponseDTO> apiResponse = getApiResponseViaGET(
                    configuration.getValidationEndpoints().getNewWorkReasonUrl(), uriVariables, AuthorizationResponseDTO.class
            );
            if (apiResponse.isPresent()) {
                result = apiResponse.get().isResult();
            }
        }
        return result;
    }

    public boolean isRepIdPresentForCreateAssessment(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        return (meansAssessmentRequest.getRepId() != null && Integer.signum(meansAssessmentRequest.getRepId()) >= 0);
    }

    public boolean validateOutstandingAssessments(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        boolean result = false;
        if (meansAssessmentRequest.getRepId() != null) {
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_REP_ID, meansAssessmentRequest.getRepId());
            Optional<OutstandingAssessmentResultDTO> apiResponse = getApiResponseViaGET(
                    configuration.getValidationEndpoints().getOutstandingAssessmentsUrl(), uriVariables, OutstandingAssessmentResultDTO.class
            );
            if (apiResponse.isPresent()) {
                result = !(apiResponse.get().isOutstandingAssessments());
            }
        }
        return result;
    }

    public boolean validateRoleReservation(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        boolean result = false;
        if (StringUtils.isNotBlank(meansAssessmentRequest.getUserId()) && StringUtils.isNotBlank(meansAssessmentRequest.getUserSession().getSessionId()) && meansAssessmentRequest.getRepId() != null) {
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, meansAssessmentRequest.getUserId());
            uriVariables.put(URIVAR_RESERVATION_ID, meansAssessmentRequest.getRepId());
            uriVariables.put(URIVAR_SESSION_ID, meansAssessmentRequest.getUserSession().getSessionId());
            Optional<AuthorizationResponseDTO> apiResponse = getApiResponseViaGET(
                    configuration.getValidationEndpoints().getReservationsUrl(), uriVariables, AuthorizationResponseDTO.class
            );
            if (apiResponse.isPresent()) {
                result = apiResponse.get().isResult();
            }
        }
        return result;
    }

    private <R> Optional<R> getApiResponseViaGET(final String endpoint, final Map<String, Object> uriVariables, final Class<R> responseClass) {
        Mono<R> response;
        response = webClient
                .get()
                .uri(endpoint, uriVariables)
                .retrieve()
                .bodyToMono(responseClass);
        R responseBody = response.block();
        if (responseClass.equals(Void.class)) {
            return Optional.empty();
        }
        return Optional.ofNullable(responseBody);
    }
}
