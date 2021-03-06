package uk.gov.justice.laa.crime.meansassessment.service;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.meansassessment.common.Constants;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.PostProcessing;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaatCourtDataService {

    public static final String RESPONSE_STRING = "Response from Court Data API: %s";
    @Qualifier("maatAPIOAuth2WebClient")
    private final WebClient webClient;
    private final MaatApiConfiguration configuration;

    public MaatApiAssessmentResponse postMeansAssessment(MaatApiAssessmentRequest assessment, String laaTransactionId, AssessmentRequestType requestType) {
        MaatApiAssessmentResponse response = getApiResponseViaPOST(
                assessment,
                MaatApiAssessmentResponse.class,
                configuration.getFinancialAssessmentEndpoints().getByRequestType(requestType),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public PassportAssessmentDTO getPassportAssessmentFromRepId(Integer repId, String laaTransactionId) {
        PassportAssessmentDTO response = getApiResponseViaGET(
                PassportAssessmentDTO.class,
                configuration.getPassportAssessmentEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public HardshipReviewDTO getHardshipReviewFromRepId(Integer repId, String laaTransactionId) {
        HardshipReviewDTO response = getApiResponseViaGET(
                HardshipReviewDTO.class,
                configuration.getHardshipReviewEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public IOJAppealDTO getIOJAppealFromRepId(Integer repId, String laaTransactionId) {
        IOJAppealDTO response = getApiResponseViaGET(
                IOJAppealDTO.class,
                configuration.getIojAppealEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    private <T> T getApiResponseViaGET(Class<T> responseClass, String url, Map<String, String> headers, Object... urlVariables) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(url)
                        .build(urlVariables))
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .retrieve()
                .bodyToMono(responseClass)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .onErrorMap(this::handleError)
                .doOnError(Sentry::captureException)
                .block();
    }

    private <T, R> R getApiResponseViaPOST(T postBody, Class<R> responseClass, String url, Map<String, String> headers) {
        return webClient
                .post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(postBody))
                .retrieve()
                .bodyToMono(responseClass)
                .onErrorMap(this::handleError)
                .doOnError(Sentry::captureException)
                .block();
    }

    private Throwable handleError(Throwable error) {
        if (error instanceof APIClientException) {
            return error;
        }
        return new APIClientException("Call to Court Data API failed, invalid response.", error);
    }

    public Mono<Void> createFinancialAssessmentHistory(final Integer finAssessmentId,
                                                       final Boolean fullAssessmentAvailable,
                                                       final String laaTransactionId) {
        String errorMessage = "Error calling Court Data API. Failed to create financial " +
                "assessment history for financialAssessmentId: " + finAssessmentId;
        return getApiResponseViaPOSTAsync(
                Void.class,
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                errorMessage,
                configuration.getFinancialAssessmentEndpoints().getCreateHistoryUrl(),
                finAssessmentId, fullAssessmentAvailable);
    }


    private <T> Mono<T> getApiResponseViaPOSTAsync(Class<T> responseClass, Map<String, String> headers, String errorMessage, String url, Object... urlVariables) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(url).build(urlVariables))
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseClass)
                .onErrorMap(throwable -> new APIClientException(errorMessage, throwable))
                .doOnError(Sentry::captureException);
    }

    public void performAssessmentPostProcessing(PostProcessing postProcessing) {


        Map<String, String> headers = Map.of(Constants.LAA_TRANSACTION_ID, postProcessing.getLaaTransactionId());
        getApiResponseViaPOST(
                postProcessing,
                Void.class,
                configuration.getPostProcessingUrl(),
                headers);

        log.info(String.format("Post processing is completed for : %s", postProcessing.getRepId()));
    }

}
