package uk.gov.justice.laa.crime.meansassessment.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.model.AuthorizationResponse;

@Slf4j
@Component
public class AuthorisationMeansAssessmentClient {

    @Value("${maatApi.baseUrl}")
    public String baseURL;

    @Value("${maatApi.validation.endpoints.newWorkReasonEndpoint}")
    public String workReasonPath;

    private final WebClient webClient;

    public AuthorisationMeansAssessmentClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseURL).build();
    }

    public AuthorizationResponse checkWorkReasonStatus(String username, String newWorkerCode) {
        log.info("Calling Rest API - /users/{username}/work-reasons/{nworCode}");

       return this.webClient
                    .get()
                    .uri("/authorization/users/{username}/work-reasons/{newWorkerCode}",username,newWorkerCode)
                    .retrieve()
                    .bodyToMono(AuthorizationResponse.class)
                    .block();
    }
}