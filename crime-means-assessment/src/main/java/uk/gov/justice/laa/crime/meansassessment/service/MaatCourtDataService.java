package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaatCourtDataService {

    private static final String RESPONSE_STRING = "Response from Court Data API: %s";

    private final MaatCourtDataApiClient maatCourtDataApiClient;

    public MaatApiAssessmentResponse persistMeansAssessment(MaatApiAssessmentRequest assessment,
                                                            RequestType requestType) {
        log.debug("Request to persist means assessment detail : {}", assessment);
        MaatApiAssessmentResponse response;
        if (RequestType.CREATE.equals(requestType)) {
            response = maatCourtDataApiClient.create(assessment);
        } else {
            response = maatCourtDataApiClient.update(assessment);
        }
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }

    public RepOrderDTO updateCompletionDate(DateCompletionRequestDTO dateCompletionRequestDTO) {
        log.debug("Request to update completion date detail : {}", dateCompletionRequestDTO);
        RepOrderDTO response = maatCourtDataApiClient.updateCompletionDate(dateCompletionRequestDTO);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }

    public PassportAssessmentDTO getPassportAssessmentFromRepId(Integer repId) {
        PassportAssessmentDTO response = maatCourtDataApiClient.getPassportAssessmentFromRepId(repId);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }

    public HardshipReviewDTO getHardshipReviewFromRepId(Integer repId) {
        HardshipReviewDTO response = maatCourtDataApiClient.getHardshipReviewFromRepId(repId);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }

    public IOJAppealDTO getIOJAppealFromRepId(Integer repId) {
        IOJAppealDTO response = maatCourtDataApiClient.getIOJAppealFromRepId(repId);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }

    public FinancialAssessmentDTO getFinancialAssessment(Integer financialAssessmentId) {
        FinancialAssessmentDTO response = maatCourtDataApiClient.getFinancialAssessment(financialAssessmentId);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }

    public RepOrderDTO getRepOrder(Integer repId) {
        RepOrderDTO response = maatCourtDataApiClient.getRepOrder(repId);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }

    public void rollbackFinancialAssessment(Integer financialAssessmentId, Map<String, Object> updateFields) {
        maatCourtDataApiClient.rollbackFinancialAssessment(updateFields, financialAssessmentId);
    }
}
