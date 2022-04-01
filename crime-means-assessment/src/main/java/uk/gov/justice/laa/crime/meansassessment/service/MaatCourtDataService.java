package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;

import java.util.Map;

@Service
@Slf4j
public class MaatCourtDataService extends RetryableWebClientService {

    @Qualifier("maatAPIOAuth2WebClient")
    private final WebClient webClient;
    private final MaatApiConfiguration configuration;
    private final String baseErrorMessage = "Call to Court Data API failed";

    @Autowired
    public MaatCourtDataService(RetryConfiguration retryConfiguration, WebClient webClient, MaatApiConfiguration configuration) {
        super(retryConfiguration);
        this.webClient = webClient;
        this.configuration = configuration;
    }

    public MaatApiAssessmentResponse postMeansAssessment(MaatApiAssessmentRequest assessment, String laaTransactionId, String endpointUrl) {

        WebClient.ResponseSpec baseResponseSpec =
                buildPostResponseSpec(
                        endpointUrl,
                        Map.of("Laa-Transaction-Id", laaTransactionId),
                        assessment
                );

        MaatApiAssessmentResponse response = callWithRetry(MaatApiAssessmentResponse.class, baseResponseSpec, baseErrorMessage);

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    public PassportAssessmentDTO getPassportAssessmentFromRepId(Integer repId, String laaTransactionId) {
        WebClient.ResponseSpec baseResponseSpec =
                buildGetResponseSpec(
                    configuration.getPassportAssessmentEndpoints().getFindUrl(),
                    Map.of("Laa-Transaction-Id", laaTransactionId),
                    repId
                );

        PassportAssessmentDTO response = callWithRetry(PassportAssessmentDTO.class, baseResponseSpec, baseErrorMessage);

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    public HardshipReviewDTO getHardshipReviewFromRepId(Integer repId, String laaTransactionId) {
        WebClient.ResponseSpec baseResponseSpec =
                buildGetResponseSpec(
                    configuration.getHardshipReviewEndpoints().getFindUrl(),
                    Map.of("Laa-Transaction-Id", laaTransactionId),
                    repId
                );

        HardshipReviewDTO response = callWithRetry(HardshipReviewDTO.class, baseResponseSpec, baseErrorMessage);

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    public IOJAppealDTO getIOJAppealFromRepId(Integer repId, String laaTransactionId) {
        WebClient.ResponseSpec baseResponseSpec =
                buildGetResponseSpec(
                    configuration.getIojAppealEndpoints().getFindUrl(),
                    Map.of("Laa-Transaction-Id", laaTransactionId),
                    repId
                );

        IOJAppealDTO response = callWithRetry(IOJAppealDTO.class, baseResponseSpec, baseErrorMessage);

        log.info(String.format("Response from Court Data API: %s", response));
        return response;
    }

    private WebClient.ResponseSpec buildGetResponseSpec(String url, Map<String, String> headers, Object... urlVariables) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(url)
                        .build(urlVariables))
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .retrieve();
    }

    private <T> WebClient.ResponseSpec buildPostResponseSpec(String url, Map<String, String> headers, T postBody) {
        return webClient
                .post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(postBody))
                .retrieve();
    }

}
