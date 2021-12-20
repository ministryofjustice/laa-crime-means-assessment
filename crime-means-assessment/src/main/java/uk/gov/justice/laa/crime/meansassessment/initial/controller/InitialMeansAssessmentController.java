package uk.gov.justice.laa.crime.meansassessment.initial.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;
import uk.gov.justice.laa.crime.meansassessment.initial.service.InitialMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.model.initial.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.initial.ApiCreateMeansAssessmentResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("api/internal/v1/assessment/means/initial")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Means Assessment", description = "Intial Means Assessment API.")
public class InitialMeansAssessmentController {

    private final InitialMeansAssessmentService initialMeansAssessmentService;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = " This API does the buisness processing of intial means assessment.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiCreateMeansAssessmentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<Object> createAssessment(@Parameter(description = "Initial means assessment data", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiCreateMeansAssessmentRequest.class))) @Valid @RequestBody ApiCreateMeansAssessmentRequest meansAssessment) {
        log.info("Create Initial Means Assessment Request Received");
        //TODO: call validator on request object

       //TODO: call validator on response object

//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        Validator validator = factory.getValidator();
        var createMeansAssessmentResponse = initialMeansAssessmentService.createAssessment(meansAssessment);

        return ResponseEntity.ok(createMeansAssessmentResponse);


//        Set<ConstraintViolation<ApiCreateMeansAssessmentResponse>> constraintViolations = validator.validate(response);
//
//        return ResponseEntity.ok(response);
    }

}
