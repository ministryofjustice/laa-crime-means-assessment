package uk.gov.justice.laa.crime.meansassessment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.meansassessment.model.common.CfeCrimeSpikeRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.CfeCrimeSpikeResponse;
import uk.gov.justice.laa.crime.meansassessment.service.LepCrimeSpike;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("api/internal/v1/lep-crime")
public class SpikeLepCrimeAPIController {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "LEP Crime Spike")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CfeCrimeSpikeResponse.class)
            )
    )
    public ResponseEntity<CfeCrimeSpikeResponse> invoke(@Parameter(description = "LEP Crime spike",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CfeCrimeSpikeRequest.class)
            )
    ) @Valid @RequestBody CfeCrimeSpikeRequest request) {
//        Can't work out how to pass dates yet (spike) - getSubmissionDate() always returns null
//        Date submissionDate = new Date(Date.parse(request.getSubmissionDate()));
        Date submissionDate = null;
        var result = LepCrimeSpike.execute(submissionDate, request.getTotalIncome().intValue());
        var response = new CfeCrimeSpikeResponse();
        response.setResult(CfeCrimeSpikeResponse.Result.fromValue(result));
        return ResponseEntity.ok(response);
    }
}
