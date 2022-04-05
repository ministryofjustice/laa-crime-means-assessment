package uk.gov.justice.laa.crime.meansassessment.service;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;

import java.util.Map;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaatCourtDataService {

    @Qualifier("maatAPIOAuth2WebClient")
    private final WebClient webClient;
    private final MaatApiConfiguration configuration;

    public MaatApiAssessmentResponse postMeansAssessment(MaatApiAssessmentRequest assessment, String laaTransactionId, String endpointUrl) {
        MaatApiAssessmentResponse response = performPostRequest(
                assessment,
                MaatApiAssessmentResponse.class,
                endpointUrl,
                Map.of("Laa-Transaction-Id", laaTransactionId)
        );

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    public PassportAssessmentDTO getPassportAssessmentFromRepId(Integer repId, String laaTransactionId) {
        PassportAssessmentDTO response = performGetRequest(
                PassportAssessmentDTO.class,
                configuration.getPassportAssessmentEndpoints().getFindUrl(),
                Map.of("Laa-Transaction-Id", laaTransactionId),
                repId
        );

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    public HardshipReviewDTO getHardshipReviewFromRepId(Integer repId, String laaTransactionId) {
        HardshipReviewDTO response = performGetRequest(
                HardshipReviewDTO.class,
                configuration.getHardshipReviewEndpoints().getFindUrl(),
                Map.of("Laa-Transaction-Id", laaTransactionId),
                repId
        );

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    public IOJAppealDTO getIOJAppealFromRepId(Integer repId, String laaTransactionId) {
        IOJAppealDTO response = performGetRequest(
                IOJAppealDTO.class,
                configuration.getIojAppealEndpoints().getFindUrl(),
                Map.of("Laa-Transaction-Id", laaTransactionId),
                repId
        );

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    private <T> T performGetRequest(Class<T> responseClass, String url, Map<String, String> headers, Object... urlVariables) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(url)
                        .build(urlVariables))
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .retrieve()
                .bodyToMono(responseClass)
                .onErrorMap(Predicate.not(APIClientException.class::isInstance), throwable ->
                        new APIClientException("Call to Court Data API failed, invalid response.", throwable)
                )
                .doOnError(Sentry::captureException)
                .block();
    }

    private <T, V> V performPostRequest(T postBody, Class<V> responseClass, String url, Map<String, String> headers) {
        return webClient
                .post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(postBody))
                .retrieve()
                .bodyToMono(responseClass)
                .onErrorMap(Predicate.not(APIClientException.class::isInstance), throwable ->
                        new APIClientException("Call to Court Data API failed, invalid response.", throwable)
                )
                .doOnError(Sentry::captureException)
                .block();
    }
}
