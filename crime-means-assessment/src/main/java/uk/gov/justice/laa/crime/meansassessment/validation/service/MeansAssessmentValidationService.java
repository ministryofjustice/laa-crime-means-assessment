package uk.gov.justice.laa.crime.meansassessment.validation.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.justice.laa.crime.meansassessment.common.Constants.*;

/**
 * This service provides methods for validation of means assessment requests
 */
@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class MeansAssessmentValidationService {

    private WebClient webClient;

    @Value("${maatApi.baseUrl}")
    private String maatAPIBaseUrl;

    @Value("${maatApi.validation.endpoints.roleActionEndpoint}")
    private String validateRoleActionEndpoint;

    @Value("${maatApi.validation.endpoints.newWorkReasonEndpoint}")
    private String validateNewWorkReasonEndpoint;

    @Value("${maatApi.validation.endpoints.reservationEndpoint}")
    private String validateReservationEndpoint;

    @Value("${maatApi.validation.endpoints.outstandingAssessmentsEndpoint}")
    private String checkOutstandingAssessmentsEndpoint;


    public boolean validateRoleAction(final ApiCreateMeansAssessmentRequest meansAssessmentRequest, String action) {
        boolean result = false;
        if(StringUtils.isNotBlank(meansAssessmentRequest.getUserId()) && StringUtils.isNotBlank(action) ){
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, meansAssessmentRequest.getUserId());
            uriVariables.put(URIVAR_ACTION, action);
            Optional<AuthorizationResponseDTO> apiResponse = getApiResponseViaGET(validateRoleActionEndpoint, uriVariables, AuthorizationResponseDTO.class, meansAssessmentRequest.getLaaTransactionId());
            if(apiResponse.isPresent()){
                result = apiResponse.get().isResult();
            }
        }
        return result;
    }

    public boolean validateNewWorkReason(final ApiCreateMeansAssessmentRequest meansAssessmentRequest) {
        boolean result = false;
        if(meansAssessmentRequest.getNewWorkReason() != null && StringUtils.isNotBlank(meansAssessmentRequest.getNewWorkReason().getCode()) ){
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, meansAssessmentRequest.getUserId());
            uriVariables.put(URIVAR_NWOR_CODE, meansAssessmentRequest.getNewWorkReason().getCode());
            Optional<AuthorizationResponseDTO> apiResponse = getApiResponseViaGET(validateNewWorkReasonEndpoint, uriVariables, AuthorizationResponseDTO.class, meansAssessmentRequest.getLaaTransactionId());
            if(apiResponse.isPresent()){
                result = apiResponse.get().isResult();
            }
        }
        return result;
    }

    public boolean isRepIdPresentForCreateAssessment(final ApiCreateMeansAssessmentRequest meansAssessmentRequest) {
        return (meansAssessmentRequest.getRepId() != null && Integer.signum(meansAssessmentRequest.getRepId()) >= 0);
    }

    public boolean validateOutstandingAssessments(final ApiCreateMeansAssessmentRequest meansAssessmentRequest) {
        boolean result = false;
        if(meansAssessmentRequest.getRepId() != null){
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_REP_ID, meansAssessmentRequest.getRepId());
            Optional<OutstandingAssessmentResultDTO> apiResponse = getApiResponseViaGET(checkOutstandingAssessmentsEndpoint, uriVariables, OutstandingAssessmentResultDTO.class, meansAssessmentRequest.getLaaTransactionId());
            if(apiResponse.isPresent()){
                result = !(apiResponse.get().isOutstandingAssessments());
            }
        }
        return result;
    }

    public boolean validateRoleReservation(final ApiCreateMeansAssessmentRequest meansAssessmentRequest) {
        boolean result = false;
        if(StringUtils.isNotBlank(meansAssessmentRequest.getUserId()) && StringUtils.isNotBlank(meansAssessmentRequest.getSessionId()) &&  meansAssessmentRequest.getReservationId() != null){
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, meansAssessmentRequest.getUserId());
            uriVariables.put(URIVAR_RESERVATION_ID, meansAssessmentRequest.getReservationId());
            uriVariables.put(URIVAR_SESSION_ID, meansAssessmentRequest.getSessionId());
            Optional<AuthorizationResponseDTO> apiResponse = getApiResponseViaGET(validateReservationEndpoint, uriVariables, AuthorizationResponseDTO.class, meansAssessmentRequest.getLaaTransactionId());
            if(apiResponse.isPresent()){
                result = apiResponse.get().isResult();
            }
        }
        return result;
    }

    private <T, R> Optional<R> getApiResponseViaGET(final String endpoint, final Map<String, Object> uriVariables, final Class<R> responseClass, final String laaTransactionId){
        Mono<R> response;
        response = webClient
                .get()
                .uri(endpoint, uriVariables)
                .retrieve()
                .bodyToMono(responseClass);
        R responseBody = response.block();
        if(responseClass.equals(Void.class)){
            return Optional.empty();
        }
        return Optional.of(responseBody);
    }

    private <T, R> Optional<R> getApiResponseViaPOST(final String endpoint, final Map<String, Object> uriVariables, final Optional<T> requestBody, final Class<R> responseClass, final String laaTransactionId){
        Mono<R> response;
        if(requestBody.isPresent()){
            response = webClient
                    .post()
                    .uri(uriBuilder -> {
                        return uriBuilder.build();
                    })
                    .bodyValue(requestBody.get())
                    .retrieve()
                    .bodyToMono(responseClass);
        } else {
            response = webClient
                    .post()
                    .uri(uriBuilder -> {
                        return uriBuilder.build();
                    })
                    .body(BodyInserters.empty())
                    .retrieve()
                    .bodyToMono(responseClass);
        }

        R responseBody = response.block();
        if(responseClass.equals(Void.class)){
            return Optional.empty();
        }
        return Optional.of(responseBody);
    }

    @PostConstruct
    public void initializeWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(maatAPIBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        webClient = WebClient
                .builder()
                .uriBuilderFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+clientToken)
                .filter(ExchangeFilterFunctions.statusError(HttpStatus::isError, r -> new APIClientException(String.format("Received error %s due to %s", r.statusCode().value(), r.statusCode().getReasonPhrase()))))
                .build();
    }

}
