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
import uk.gov.justice.laa.crime.meansassessment.dto.courtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateAssessment;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourtDataService {

    @Qualifier("maatAPIOAuth2WebClient")
    private final WebClient webClient;
    private final MaatApiConfiguration configuration;

    public ApiCreateMeansAssessmentResponse postMeansAssessment(ApiCreateAssessment createAssessment, String laaTransactionId) {
        ApiCreateMeansAssessmentResponse response = webClient.post()
                .uri(configuration.getFinancialAssessmentEndpoints().getCreateUrl())
                .headers(httpHeaders -> httpHeaders.setAll(Map.of(
                        "Laa-Transaction-Id", laaTransactionId
                )))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createAssessment))
                .retrieve()
                .bodyToMono(ApiCreateMeansAssessmentResponse.class)
                .onErrorMap(throwable -> new APIClientException("Call to Court Data API failed, invalid response."))
                .doOnError(Sentry::captureException)
                .block();

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    public PassportAssessmentDTO getPassportAssessmentFromRepId(Integer repId, String laaTransactionId) {
        PassportAssessmentDTO response = webClient.get()
                .uri(configuration.getPassportAssessmentEndpoints().getFindUrl())
                .headers(httpHeaders -> httpHeaders.setAll(Map.of(
                        "Laa-Transaction-Id", laaTransactionId)))
                .retrieve()
                .bodyToMono(PassportAssessmentDTO.class)
                .onErrorMap(throwable -> new APIClientException("Call to Court Data API failed, invalid response."))
                .doOnError(Sentry::captureException)
                .block();

        log.info(String.format("PassportAssessmentDTO response from Court Data API: %s", response));
        return response;
    }

}
