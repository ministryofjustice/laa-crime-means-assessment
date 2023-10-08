package uk.gov.justice.laa.crime.meansassessment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentRequestDTOBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentCriteriaDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaService;
import uk.gov.justice.laa.crime.meansassessment.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.validation.validator.MeansAssessmentValidationProcessor;

import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType.CREATE;
import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType.UPDATE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/assessment/means")
@Tag(name = "Means Assessment", description = "Rest API for Means Assessment.")
public class MeansAssessmentController {

    private final MeansAssessmentService meansAssessmentService;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final MeansAssessmentRequestDTOBuilder meansAssessmentRequestDTOBuilder;
    private final MeansAssessmentValidationProcessor meansAssessmentValidationProcessor;

    private MeansAssessmentRequestDTO preProcessRequest(ApiMeansAssessmentRequest meansAssessment, AssessmentRequestType requestType) {
        MeansAssessmentRequestDTO requestDTO =
                meansAssessmentRequestDTOBuilder.buildRequestDTO(meansAssessment);
        meansAssessmentValidationProcessor.validate(requestDTO, requestType);
        return requestDTO;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create an initial means assessment")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiMeansAssessmentResponse.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Bad Request.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Server Error.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ResponseEntity<ApiMeansAssessmentResponse> createAssessment(@Parameter(description = "Init means assessment data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiCreateMeansAssessmentRequest.class)
            )
    ) @Valid @RequestBody ApiCreateMeansAssessmentRequest meansAssessment) {

        MeansAssessmentRequestDTO requestDTO = preProcessRequest(meansAssessment, CREATE);
        return ResponseEntity.ok(
                meansAssessmentService.doAssessment(requestDTO, CREATE)
        );
    }


    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update a means assessment")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiMeansAssessmentResponse.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Bad Request.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Server Error.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ResponseEntity<ApiMeansAssessmentResponse> updateAssessment(@Parameter(description = "Init/Full means assessment data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiUpdateMeansAssessmentRequest.class)
            )
    ) @Valid @RequestBody ApiUpdateMeansAssessmentRequest meansAssessment) {

        MeansAssessmentRequestDTO requestDTO = preProcessRequest(meansAssessment, UPDATE);
        return ResponseEntity.ok(
                meansAssessmentService.doAssessment(requestDTO, UPDATE)
        );
    }

    @GetMapping(value = "/{financialAssessmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Retrieve an old means assessment")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiMeansAssessmentResponse.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Bad Request.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Server Error.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ResponseEntity<ApiGetMeansAssessmentResponse> getOldAssessment(@PathVariable int financialAssessmentId,
                                                                          @Parameter(description = "Used for tracing calls") @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {
        log.info("Get old means assessment request received");
        return ResponseEntity.ok(meansAssessmentService.getOldAssessment(financialAssessmentId, laaTransactionId));
    }


    @GetMapping(value = "/fullAssessmentThreshold/{assessmentDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Retrieve full assessment threshold")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<AssessmentCriteriaDTO> fullAssessmentThreshold(@PathVariable("assessmentDate") String assessmentDate) {
        log.info("Retrieve full assessment threshold");
        return ResponseEntity.ok(assessmentCriteriaService.getFullAssessmentThreshold(assessmentDate));
    }


}