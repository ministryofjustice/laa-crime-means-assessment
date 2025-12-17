package uk.gov.justice.laa.crime.meansassessment.client;

import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.DateCompletionRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.RepOrderDTO;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange()
public interface MaatCourtDataApiClient {

    @PostExchange("/financial-assessments")
    MaatApiAssessmentResponse createFinancialAssessment(@RequestBody MaatApiAssessmentRequest assessment);

    @PutExchange("/financial-assessments")
    MaatApiAssessmentResponse updateFinancialAssessment(@RequestBody MaatApiAssessmentRequest assessment);

    @GetExchange("/financial-assessments/{financialAssessmentId}")
    FinancialAssessmentDTO getFinancialAssessment(@PathVariable Integer financialAssessmentId);

    @PatchExchange("/financial-assessments/rollback/{financialAssessmentId}")
    void patchFinancialAssessment(
            @RequestBody Map<String, Object> updateFields, @PathVariable Integer financialAssessmentId);

    @PostExchange("/rep-orders/update-date-completed")
    RepOrderDTO updateCompletionDate(@RequestBody DateCompletionRequestDTO dateCompletionRequestDTO);

    @GetExchange("/passport-assessments/repId/{repId}")
    PassportAssessmentDTO getPassportAssessmentFromRepId(@PathVariable Integer repId);

    @GetExchange("/hardship/repId/{repId}")
    HardshipReviewDTO getHardshipReviewFromRepId(@PathVariable Integer repId);

    @GetExchange("/ioj-appeal/repId/{repId}")
    IOJAppealDTO getIOJAppealFromRepId(@PathVariable Integer repId);

    @GetExchange("/rep-orders/{repId}")
    RepOrderDTO getRepOrder(@PathVariable Integer repId);

    @GetExchange("/authorization/users/{username}/actions/{action}")
    AuthorizationResponseDTO getUserRoleAction(@PathVariable String username, @PathVariable String action);

    @GetExchange("/authorization/users/{username}/work-reasons/{nworCode}")
    AuthorizationResponseDTO getNewWorkReason(@PathVariable String username, @PathVariable String nworCode);

    @GetExchange("/financial-assessments/check-outstanding/{repId}")
    OutstandingAssessmentResultDTO getOutstandingAssessment(@PathVariable Integer repId);

    @GetExchange("/authorization/users/{username}/reservations/{reservationId}/sessions/{sessionId}")
    AuthorizationResponseDTO getReservationDetail(
            @PathVariable String username, @PathVariable Integer reservationId, @PathVariable String sessionId);
}
