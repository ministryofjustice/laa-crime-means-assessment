package uk.gov.justice.laa.crime.meansassessment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.common.model.meansassessment.stateless.StatelessApiRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.stateless.StatelessApiResponse;
import uk.gov.justice.laa.crime.enums.meansassessment.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.DependantChild;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaService;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessAssessmentService;


import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/internal/v2/assessment/means")
public class StatelessMeansAssessmentController {

    private final StatelessAssessmentService statelessAssessmentService;
    private final AssessmentCriteriaService assessmentCriteriaService;

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
        var apiAssessment = meansAssessment.getAssessment();
        Map<AgeRange, Integer> childGroupings = getChildGroupings(apiAssessment.getDependantChildren());

        var result = statelessAssessmentService.execute(apiAssessment, childGroupings,
                meansAssessment.getIncome(), meansAssessment.getOutgoings());
        var initialResult = result.getInitialResult();
        var fullResult = result.getFullResult();
        var response = new StatelessApiResponse()
                .withInitialMeansAssessment(initialResult);

        if (fullResult != null) {
            response = response.withFullMeansAssessment(fullResult);
        }

        return ResponseEntity.ok(response);
    }

    private static Map<AgeRange, Integer> getChildGroupings(List<DependantChild> dependantChildren) {
        var groupings = new EnumMap<AgeRange, Integer>(AgeRange.class);
        for (var child : dependantChildren) {
            groupings.put(child.getAgeRange(), child.getCount());
        }
        return groupings;
    }
}
