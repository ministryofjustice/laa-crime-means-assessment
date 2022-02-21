package uk.gov.justice.laa.crime.meansassessment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.service.MeansAssessmentService;

import javax.validation.Valid;

@RestController
@RequestMapping("api/internal/v1/assessment/means")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Means Assessment", description = "Rest APIs for Means Assessment.")
public class MeansAssessmentController {

    private final MeansAssessmentService meansAssessmentService;

    @SneakyThrows
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = " This API does the business processing of initial means assessment.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiCreateMeansAssessmentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<ApiCreateMeansAssessmentResponse> createAssessment(@Parameter(description = "Initial means assessment data", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiCreateMeansAssessmentRequest.class))) @Valid @RequestBody ApiCreateMeansAssessmentRequest meansAssessment) {
        log.info("Means Assessment Request Received for MAAT ID:  {}", meansAssessment.getRepId());
        log.info("Create Initial Means Assessment Request Received");
        meansAssessmentService.checkInitialAssessment(meansAssessment);
        log.info("Finished processing Means Assessment Request for MAAT ID:  {}", meansAssessment.getRepId());
        return ResponseEntity.ok(null);
    }
}