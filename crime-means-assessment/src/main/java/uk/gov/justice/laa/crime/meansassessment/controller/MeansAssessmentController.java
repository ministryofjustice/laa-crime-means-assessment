package uk.gov.justice.laa.crime.meansassessment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.HelloDTO;

@RestController

@RequestMapping("/meansassessment")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Means Assessment", description = "Rest APIs for Means Assessment.")
public class MeansAssessmentController {

    @GetMapping ("/hello")
    @Operation(description = "Hello API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server Error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<HelloDTO> hello(@RequestParam String name) {
        HelloDTO result  = new HelloDTO();
        result.setMessage("Hello "+name);
        return new ResponseEntity<HelloDTO>(result, HttpStatus.OK);
    }
}