package uk.gov.justice.laa.crime.meansassessment.defendant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.service.DefendantAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;

@RestController
@RequestMapping("/defendantmeansassessment")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Defendant Means Assessment", description = "Rest APIs for Means Assessment.")
public class DefendantAssessmentController {

    private final DefendantAssessmentService defendantAssessmentService;

    @GetMapping(value="/{defendantAssessmentId}")
    @Operation(description = "Defendant Means Assessment API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<DefendantAssessmentEntity> getDefendantAssessment(@PathVariable("defendantAssessmentId") String defendantAssessmentId) {
        log.info("Retrieving DefendantAssessmentEntity with id {}",defendantAssessmentId);
        DefendantAssessmentEntity defendantAssessmentDTO = defendantAssessmentService.findById(defendantAssessmentId);
        return ResponseEntity.ok(defendantAssessmentDTO);
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<DefendantAssessmentEntity> createDefendantAssessmentEntity(@RequestBody DefendantAssessmentEntity defendantAssessmentEntity){
        if (StringUtils.hasLength(defendantAssessmentEntity.getId())) {
            return ResponseEntity.badRequest().body(defendantAssessmentEntity);
        }
        return ResponseEntity.ok(defendantAssessmentService.save(defendantAssessmentEntity));
    }

    @PutMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<DefendantAssessmentEntity> updateDefendantAssessmentEntity(@RequestBody DefendantAssessmentEntity defendantAssessmentEntity){
       return ResponseEntity.ok(defendantAssessmentService.update(defendantAssessmentEntity));
    }

    @DeleteMapping(value="/{defendantAssessmentId}")
    @Operation(description = "Defendant Means Assessment API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<String> deleteDefendantAssessment(@PathVariable("defendantAssessmentId") String defendantAssessmentId) {
        log.info("deleting DefendantAssessmentEntity with id {}",defendantAssessmentId);

        return ResponseEntity.ok(defendantAssessmentService.deleteById(defendantAssessmentId));
    }
}
