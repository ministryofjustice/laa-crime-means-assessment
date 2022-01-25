package uk.gov.justice.laa.crime.meansassessment.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.model.AuthorizationResponse;

@Slf4j
@Component
//might change to authorisation
public class WorkReasonsClient {

    @Value("${maat-api-client.base-url}")
    public String baseURL = "";

    @Value("${maat-api-client.base-url}")
    public String workReasonPath = "";

    private final WebClient webClient;

    public WorkReasonsClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseURL).build();
    }

    public AuthorizationResponse checkWorkReasonStatus(String newWorkerCode) {
        log.info("Calling Rest API - /users/{username}/work-reasons/{nworCode}");

        AuthorizationResponse responseMono = this.webClient
                    .get()
                    .uri("/authorization/users/{username}/work-reasons/{newWorkerCode}",newWorkerCode)
                    .retrieve()
                    .bodyToMono(AuthorizationResponse.class)
                    .block();
            return responseMono;
    }
}