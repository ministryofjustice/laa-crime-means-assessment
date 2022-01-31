package uk.gov.justice.laa.crime.meansassessment.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.validation.model.AssessmentValidationRequest;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class CreateAssessmentValidator {


    @Qualifier("maatAPIOAuth2WebClient")
    private final WebClient webClient;

   // @Value("${maatApi.validationUrl}") //TODO - set the app.ymal with ref data
    private String maatAPIValidateUrl;

    public void validate(AssessmentValidationRequest assessmentValidationRequest) {

        validateRoleAction(assessmentValidationRequest);
        validateRoleReservation(assessmentValidationRequest);
        validateRoleReservation(assessmentValidationRequest);
        validateRoleReservation(assessmentValidationRequest);


    }

    private boolean validateRoleReservation(AssessmentValidationRequest assessmentValidationRequest) {

        WebClient.ResponseSpec clientResponse =
                webClient
                        .post()
                        .uri(maatAPIValidateUrl)
                        .headers(httpHeaders -> httpHeaders.setAll(new HashMap<>()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("JSON"))
                         .retrieve();
        return true;
    }

    private boolean validateRoleAction(AssessmentValidationRequest assessmentValidationRequest) {

        //TODO - validate role action by calling MAAT API
        return true;
    }


}
