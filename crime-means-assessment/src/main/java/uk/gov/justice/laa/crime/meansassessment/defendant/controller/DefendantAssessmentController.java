package uk.gov.justice.laa.crime.meansassessment.defendant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.service.DefendantAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;

import java.util.UUID;

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
    public ResponseEntity<Object> getDefendantAssessment(@PathVariable("defendantAssessmentId") String defendantAssessmentId) {
        log.info("Retrieving DefendantAssessmentEntity with id {}",defendantAssessmentId);
        try {
            validate(defendantAssessmentId);
            var defendantAssessment = defendantAssessmentService.findById(defendantAssessmentId);
            if (defendantAssessment != null) {
                return ResponseEntity.ok(defendantAssessment);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e){
            log.error("Unable to retrieve defendant Assessment with Id: {}",defendantAssessmentId);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(ErrorDTO.builder().code("BAD_REQUEST").message(new StringBuilder("Unable to process request for id: ").append(defendantAssessmentId).append(". ").append(e.getMessage()).toString()).build());
        }
    }

    /**
     * check that the defendant id is of type UUID
     * @param defendantAssessmentId
     * @throws IllegalArgumentException
     */
    private void validate(String defendantAssessmentId) throws IllegalArgumentException{
        UUID.fromString(defendantAssessmentId);
    }


    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<Object> createDefendantAssessmentEntity(@RequestBody DefendantAssessmentEntity defendantAssessmentEntity){
        try {
            validateNoIDIsPassedFor(defendantAssessmentEntity);

            return new ResponseEntity<>(defendantAssessmentService.save(defendantAssessmentEntity), HttpStatus.CREATED);
        } catch (RuntimeException e){
            log.error("Unable to create defendant Assessment: {}",defendantAssessmentEntity);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(ErrorDTO.builder().code("CREATE_BAD_REQUEST").message(new StringBuilder("Unable to create defendant assessment: ").append(defendantAssessmentEntity).append(e.getMessage()).toString()).build());
        }
    }
    private void validateNoIDIsPassedFor(DefendantAssessmentEntity defendantAssessmentEntity){
        if (StringUtils.hasLength(defendantAssessmentEntity.getId())) {
            throw new RuntimeException(" No id should be present when creating defendantAssessmentEntity");
        }
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
