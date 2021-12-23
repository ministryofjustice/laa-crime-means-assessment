package uk.gov.justice.laa.crime.meansassessment.defendant.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.exceptions.DefendantAssessmentIdPresentException;
import uk.gov.justice.laa.crime.meansassessment.defendant.exceptions.DefendantAssessmentInvalidIdException;
import uk.gov.justice.laa.crime.meansassessment.defendant.exceptions.DefendantAssessmentMissingException;
import uk.gov.justice.laa.crime.meansassessment.defendant.exceptions.DefendantAssessmentNotFoundException;
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
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "Not Found.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<Object> getDefendantAssessment(@PathVariable("defendantAssessmentId") String defendantAssessmentId) {
        log.info("Retrieving DefendantAssessmentEntity with id {}",defendantAssessmentId);

        validateDefendantAssessmentId(defendantAssessmentId);

        var defendantAssessment = defendantAssessmentService.findById(defendantAssessmentId);
        if (defendantAssessment == null) {
         throw new DefendantAssessmentNotFoundException(defendantAssessmentId);
        }
        return ResponseEntity.ok(defendantAssessment);
    }

    private boolean isValid(String defendantAssessmentId){
        try {
            UUID.fromString(defendantAssessmentId);
            return true;
        } catch (RuntimeException e){
            return false;
        }
    }

    /**
     * if defendantAssessmentId is not falid, throw DefendantAssessmentInvalidIdException
     * @param defendantAssessmentId
     * @throws DefendantAssessmentInvalidIdException
     */
    private void validateDefendantAssessmentId(String defendantAssessmentId) throws DefendantAssessmentInvalidIdException{
        if (!isValid(defendantAssessmentId)) {
            throw new DefendantAssessmentInvalidIdException(defendantAssessmentId);
        }
    }



    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<Object> createDefendantAssessmentEntity(@RequestBody DefendantAssessmentEntity defendantAssessmentEntity){
        log.info("creating DefendantAssessmentEntity {}",defendantAssessmentEntity);

        validateDefendantAssessmentIsPresentFor(defendantAssessmentEntity);

        if( isIDPresentFor(defendantAssessmentEntity)) {
            throw new DefendantAssessmentIdPresentException(defendantAssessmentEntity.getId());
        }
        return new ResponseEntity<>(defendantAssessmentService.save(defendantAssessmentEntity), HttpStatus.CREATED);
    }

    private void validateDefendantAssessmentIsPresentFor(DefendantAssessmentEntity defendantAssessmentEntity) throws DefendantAssessmentMissingException{
        if (defendantAssessmentEntity == null) {
            throw new DefendantAssessmentMissingException();
        }
    }

    private boolean isIDPresentFor(DefendantAssessmentEntity defendantAssessmentEntity){
        return StringUtils.hasLength(defendantAssessmentEntity.getId());
    }

    @PutMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<DefendantAssessmentEntity> updateDefendantAssessmentEntity(@RequestBody DefendantAssessmentEntity defendantAssessmentEntity){
        log.info("log attempting to update {}", defendantAssessmentEntity);

        validateDefendantAssessmentIsPresentFor(defendantAssessmentEntity);

        validateDefendantAssessmentId(defendantAssessmentEntity.getId());

        var defendantAssessment = defendantAssessmentService.findById(defendantAssessmentEntity.getId());
        if (defendantAssessment == null) {
            throw new DefendantAssessmentNotFoundException(defendantAssessmentEntity.getId());
        }

       return ResponseEntity.ok(defendantAssessmentService.update(defendantAssessmentEntity));
    }


    @DeleteMapping(value="/{defendantAssessmentId}")
    @Operation(description = "Defendant Means Assessment API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<Object> deleteDefendantAssessment(@PathVariable("defendantAssessmentId") String defendantAssessmentId) {
        log.info("deleting DefendantAssessmentEntity with id {}",defendantAssessmentId);

        validateDefendantAssessmentId(defendantAssessmentId);

        var defendantAssessment = defendantAssessmentService.findById(defendantAssessmentId);
        if (defendantAssessment == null) {
            throw new DefendantAssessmentNotFoundException(defendantAssessmentId);
        }
        final var gson = new Gson();
        return ResponseEntity.ok(gson.toJson(defendantAssessmentService.deleteById(defendantAssessmentId)));
    }
}
