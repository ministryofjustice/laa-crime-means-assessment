package uk.gov.justice.laa.crime.meansassessment.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.*;

import java.util.Map;

@HttpExchange()
public interface MaatCourtDataApiClient {

    @PostExchange("/financial-assessments")
    MaatApiAssessmentResponse create(@RequestBody MaatApiAssessmentRequest assessment);

    @PutExchange("/financial-assessments")
    MaatApiAssessmentResponse update(@RequestBody MaatApiAssessmentRequest assessment);

    @PostExchange("/rep-orders/update-date-completed")
    RepOrderDTO updateCompletionDate(@RequestBody DateCompletionRequestDTO dateCompletionRequestDTO);

    @GetExchange("/passport-assessments/repId/{repId}")
    PassportAssessmentDTO getPassportAssessmentFromRepId(@PathVariable Integer repId);

    @GetExchange("/hardship/repId/{repId}")
    HardshipReviewDTO getHardshipReviewFromRepId(@PathVariable Integer repId);

    @GetExchange("/ioj-appeal/repId/{repId}")
    IOJAppealDTO getIOJAppealFromRepId(@PathVariable Integer repId);

    @GetExchange("/financial-assessments/{financialAssessmentId}")
    FinancialAssessmentDTO getFinancialAssessment(@PathVariable Integer financialAssessmentId);

    @GetExchange("/rep-orders/{repId}")
    RepOrderDTO getRepOrder(@PathVariable Integer repId);

    @PatchExchange("/financial-assessments/rollback/{financialAssessmentId}")
    PassportAssessmentDTO rollbackFinancialAssessment(@RequestBody Map<String, Object> updateFields,
                                                      @PathVariable Integer financialAssessmentId);
    @GetExchange("/authorization/users/{username}/actions/{action}")
    AuthorizationResponseDTO getUserRoleAction(@PathVariable String username, @PathVariable String action);

    @GetExchange("/authorization/users/{username}/work-reasons/{nworCode}")
    AuthorizationResponseDTO getNewWorkReason(@PathVariable String username, @PathVariable String nworCode);

    @GetExchange("/financial-assessments/check-outstanding/{repId}")
    OutstandingAssessmentResultDTO getOutstandingAssessment(@PathVariable Integer repId);

    @GetExchange("/authorization/users/{username}/reservations/{reservationId}/sessions/{sessionId}")
    AuthorizationResponseDTO getReservationDetail(@PathVariable String username,  @PathVariable Integer reservationId,
                                                  @PathVariable String sessionId);

}
