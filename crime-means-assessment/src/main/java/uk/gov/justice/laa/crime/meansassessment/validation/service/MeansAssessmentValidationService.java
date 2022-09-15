package uk.gov.justice.laa.crime.meansassessment.validation.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;

import java.util.HashMap;

import static uk.gov.justice.laa.crime.meansassessment.common.Constants.*;

/**
 * This service provides methods for validation of means assessment requests
 */
@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class MeansAssessmentValidationService {

    private final MaatApiConfiguration configuration;
    private final MaatCourtDataClient maatCourtDataClient;

    String getUserIdFromRequest(MeansAssessmentRequestDTO meansAssessmentRequest) {
        return meansAssessmentRequest.getUserSession().getUserName();
    }

    public boolean isRoleActionValid(final MeansAssessmentRequestDTO meansAssessmentRequest, String action) {
        if (StringUtils.isNotBlank(getUserIdFromRequest(meansAssessmentRequest)) && StringUtils.isNotBlank(action)) {
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, getUserIdFromRequest(meansAssessmentRequest));
            uriVariables.put(URIVAR_ACTION, action);
            AuthorizationResponseDTO apiResponse = maatCourtDataClient.getApiResponseViaGET(
                    AuthorizationResponseDTO.class, configuration.getValidationEndpoints().getRoleActionUrl(), null, uriVariables
            );
            return apiResponse.isResult();
        }
        return false;
    }

    public boolean isNewWorkReasonValid(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        if (meansAssessmentRequest.getNewWorkReason() != null) {
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, getUserIdFromRequest(meansAssessmentRequest));
            uriVariables.put(URIVAR_NWOR_CODE, meansAssessmentRequest.getNewWorkReason().getCode());
            AuthorizationResponseDTO apiResponse = maatCourtDataClient.getApiResponseViaGET(
                    AuthorizationResponseDTO.class, configuration.getValidationEndpoints().getNewWorkReasonUrl(), null, uriVariables
            );
            return apiResponse.isResult();
        }
        return false;
    }

    public boolean isOutstandingAssessment(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        if (meansAssessmentRequest.getRepId() != null) {
            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_REP_ID, meansAssessmentRequest.getRepId());
            OutstandingAssessmentResultDTO apiResponse = maatCourtDataClient.getApiResponseViaGET(
                    OutstandingAssessmentResultDTO.class, configuration.getValidationEndpoints().getOutstandingAssessmentsUrl(), null, uriVariables
            );
            return apiResponse.isOutstandingAssessments();
        }
        return false;
    }

    public boolean isRepOrderReserved(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        if (StringUtils.isNotBlank(getUserIdFromRequest(meansAssessmentRequest))
                && StringUtils.isNotBlank(meansAssessmentRequest.getUserSession().getSessionId())
                && meansAssessmentRequest.getRepId() != null) {

            HashMap<String, Object> uriVariables = new HashMap<>();
            uriVariables.put(URIVAR_USERNAME, getUserIdFromRequest(meansAssessmentRequest));
            uriVariables.put(URIVAR_RESERVATION_ID, meansAssessmentRequest.getRepId());
            uriVariables.put(URIVAR_SESSION_ID, meansAssessmentRequest.getUserSession().getSessionId());
            AuthorizationResponseDTO apiResponse = maatCourtDataClient.getApiResponseViaGET(
                    AuthorizationResponseDTO.class, configuration.getValidationEndpoints().getReservationsUrl(), null, uriVariables
            );
            return apiResponse.isResult();
        }
        return false;
    }
}
