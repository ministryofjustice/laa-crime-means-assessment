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
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.service.MaatCourtDataService;

import java.time.LocalDateTime;
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
            AuthorizationResponseDTO apiResponse = maatAPIClient.get(new ParameterizedTypeReference<AuthorizationResponseDTO>() {},
                    configuration.getValidationEndpoints().getRoleActionUrl(),
                    Map.of(LAA_TRANSACTION_ID, meansAssessmentRequest.getLaaTransactionId()),
                    getUserIdFromRequest(meansAssessmentRequest),
                    action
            );
            return apiResponse.isResult();
        }
        return false;
    }

    public boolean isNewWorkReasonValid(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        if (meansAssessmentRequest.getNewWorkReason() != null) {
            AuthorizationResponseDTO apiResponse = maatAPIClient.get(new ParameterizedTypeReference<AuthorizationResponseDTO>() {},
                    configuration.getValidationEndpoints().getNewWorkReasonUrl(),
                    Map.of(LAA_TRANSACTION_ID, meansAssessmentRequest.getLaaTransactionId()),
                    getUserIdFromRequest(meansAssessmentRequest),
                    meansAssessmentRequest.getNewWorkReason().getCode()
            );
            return apiResponse.isResult();
        }
        return false;
    }

    public boolean isOutstandingAssessment(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        if (meansAssessmentRequest.getRepId() != null) {
            OutstandingAssessmentResultDTO apiResponse = maatAPIClient.get(new ParameterizedTypeReference<OutstandingAssessmentResultDTO>() {},
                    configuration.getValidationEndpoints().getOutstandingAssessmentsUrl(),
                    Map.of(LAA_TRANSACTION_ID, meansAssessmentRequest.getLaaTransactionId()),
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
            AuthorizationResponseDTO apiResponse = maatAPIClient.get(new ParameterizedTypeReference<AuthorizationResponseDTO>() {},
                    configuration.getValidationEndpoints().getReservationsUrl(),
                    Map.of(LAA_TRANSACTION_ID, meansAssessmentRequest.getLaaTransactionId()),
                    getUserIdFromRequest(meansAssessmentRequest),
                    meansAssessmentRequest.getRepId(),
                    meansAssessmentRequest.getUserSession().getSessionId()
            );
            return apiResponse.isResult();
        }
        return false;
    }

    public boolean isAssessmentModifiedByAnotherUser(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        FinancialAssessmentDTO financialAssessmentDTO =
                maatCourtDataService.getFinancialAssessment(
                        meansAssessmentRequest.getFinancialAssessmentId(), meansAssessmentRequest.getLaaTransactionId()
                );
        if (null != financialAssessmentDTO) {
            LocalDateTime updateFinAssessmentTimeStamp = null != financialAssessmentDTO.getUpdated() ? financialAssessmentDTO.getUpdated()
                    : financialAssessmentDTO.getDateCreated();
            return !updateFinAssessmentTimeStamp.equals(meansAssessmentRequest.getTimeStamp());
        }
        return false;
    }
}
