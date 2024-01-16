package uk.gov.justice.laa.crime.meansassessment.validation.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.service.MaatCourtDataService;

import java.util.Map;

import static uk.gov.justice.laa.crime.meansassessment.common.Constants.LAA_TRANSACTION_ID;

/**
 * This service provides methods for validation of means assessment requests
 */
@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class MeansAssessmentValidationService {

    private final MaatApiConfiguration configuration;
    private final MaatCourtDataService maatCourtDataService;
    @Qualifier("maatApiClient")
    private final RestAPIClient maatAPIClient;

    String getUserIdFromRequest(MeansAssessmentRequestDTO meansAssessmentRequest) {
        return meansAssessmentRequest.getUserSession().getUserName();
    }

    public boolean isRoleActionValid(final MeansAssessmentRequestDTO meansAssessmentRequest, String action) {
        if (StringUtils.isNotBlank(getUserIdFromRequest(meansAssessmentRequest)) && StringUtils.isNotBlank(action)) {
            AuthorizationResponseDTO apiResponse = maatAPIClient.get(new ParameterizedTypeReference<>() {},
                    configuration.getValidationEndpoints().getRoleActionUrl(),
                    getUserIdFromRequest(meansAssessmentRequest),
                    action
            );
            return apiResponse.isResult();
        }
        return false;
    }

    public boolean isNewWorkReasonValid(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        if (meansAssessmentRequest.getNewWorkReason() != null) {
            AuthorizationResponseDTO apiResponse = maatAPIClient.get(new ParameterizedTypeReference<>() {},
                    configuration.getValidationEndpoints().getNewWorkReasonUrl(),
                    getUserIdFromRequest(meansAssessmentRequest),
                    meansAssessmentRequest.getNewWorkReason().getCode()
            );
            return apiResponse.isResult();
        }
        return false;
    }

    public boolean isOutstandingAssessment(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        if (meansAssessmentRequest.getRepId() != null) {
            OutstandingAssessmentResultDTO apiResponse = maatAPIClient.get(new ParameterizedTypeReference<>() {},
                    configuration.getValidationEndpoints().getOutstandingAssessmentsUrl(),
                    meansAssessmentRequest.getRepId()
            );
            return apiResponse.isOutstandingAssessments();
        }
        return false;
    }

    public boolean isRepOrderReserved(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        if (StringUtils.isNotBlank(getUserIdFromRequest(meansAssessmentRequest))
                && StringUtils.isNotBlank(meansAssessmentRequest.getUserSession().getSessionId())
                && meansAssessmentRequest.getRepId() != null) {
            AuthorizationResponseDTO apiResponse = maatAPIClient.get(new ParameterizedTypeReference<>() {},
                    configuration.getValidationEndpoints().getReservationsUrl(),
                    getUserIdFromRequest(meansAssessmentRequest),
                    meansAssessmentRequest.getRepId(),
                    meansAssessmentRequest.getUserSession().getSessionId()
            );
            return apiResponse.isResult();
        }
        return false;
    }
}
