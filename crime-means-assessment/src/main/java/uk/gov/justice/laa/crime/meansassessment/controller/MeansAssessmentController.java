package uk.gov.justice.laa.crime.meansassessment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentRequestDTOBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.validation.validator.MeansAssessmentValidationProcessor;

import javax.validation.Valid;

import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType.CREATE;
import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType.UPDATE;

@RestController
@RequestMapping("api/internal/v1/assessment/means")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Means Assessment", description = "Rest API for Means Assessment.")
public class MeansAssessmentController {

    private final MeansAssessmentService meansAssessmentService;
    private final MeansAssessmentRequestDTOBuilder meansAssessmentRequestDTOBuilder;
    private final MeansAssessmentValidationProcessor meansAssessmentValidationProcessor;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This endpoint creates an initial means assessment")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiMeansAssessmentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<ApiMeansAssessmentResponse> createAssessment(@Parameter(description = "Init means assessment data", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiCreateMeansAssessmentRequest.class))) @Valid @RequestBody ApiCreateMeansAssessmentRequest meansAssessment) {

        log.info("Create Assessment Request Received for MAAT ID:  {}", meansAssessment.getRepId());
        MeansAssessmentRequestDTO requestDTO = meansAssessmentRequestDTOBuilder.buildRequestDTO(meansAssessment);
        meansAssessmentValidationProcessor.validate(requestDTO);
        var MeansAssessmentResponse = meansAssessmentService.doAssessment(requestDTO, CREATE);
        log.info("Create Assessment Request completed for MAAT ID: {}", requestDTO.getRepId());
        return ResponseEntity.ok(MeansAssessmentResponse);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This endpoint updates an initial means assessment or converts it to a full one")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiMeansAssessmentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<ApiMeansAssessmentResponse> updateAssessment(@Parameter(description = "Init/Full means assessment data", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiUpdateMeansAssessmentRequest.class))) @Valid @RequestBody ApiUpdateMeansAssessmentRequest meansAssessment) {

        log.info("Update Assessment Request Received for MAAT ID:  {}", meansAssessment.getRepId());
        MeansAssessmentRequestDTO requestDTO = meansAssessmentRequestDTOBuilder.buildRequestDTO(meansAssessment);
        meansAssessmentValidationProcessor.validate(requestDTO);
        var meansAssessmentResponse = meansAssessmentService.doAssessment(requestDTO, UPDATE);
        log.info("Update Assessment Request completed for MAAT ID: {}", requestDTO.getRepId());
        return ResponseEntity.ok(meansAssessmentResponse);
    }
}