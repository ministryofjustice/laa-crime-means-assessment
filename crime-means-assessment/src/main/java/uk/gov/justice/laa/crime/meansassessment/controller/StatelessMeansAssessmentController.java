package uk.gov.justice.laa.crime.meansassessment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.StatelessApiRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.StatelessApiResponse;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessFullResult;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessInitialResult;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.FullAssessmentResult;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/internal/v2/assessment/means")
public class StatelessMeansAssessmentController {
    @Operation(description = "Stateless Means Assessment")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = StatelessApiResponse.class)
            )
    )

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatelessApiResponse> invoke(@Parameter(description = "Stateless Means Assessment Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = StatelessApiRequest.class)
            )
    ) @Valid @RequestBody StatelessApiRequest meansAssessment) {
        StatelessInitialResult initialMeansAssessment = new StatelessInitialResult(
                InitAssessmentResult.PASS,
                BigDecimal.valueOf(0.0),
                BigDecimal.valueOf(0.0),
                false
                );
        StatelessFullResult fullResult = new StatelessFullResult(
                FullAssessmentResult.PASS,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
        var response = new StatelessApiResponse()
                .withInitialMeansAssessment(initialMeansAssessment)
                .withFullMeansAssessment(fullResult);

        return ResponseEntity.ok(response);
    }
}
