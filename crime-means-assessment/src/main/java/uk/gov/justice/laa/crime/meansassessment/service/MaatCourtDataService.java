package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.meansassessment.common.Constants;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.*;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaatCourtDataService {

    private static final String RESPONSE_STRING = "Response from Court Data API: %s";

    @Qualifier("maatApiClient")
    private final RestAPIClient maatAPIClient;

    private final MaatApiConfiguration configuration;

    public MaatApiAssessmentResponse persistMeansAssessment(MaatApiAssessmentRequest assessment,
                                                            String laaTransactionId,
                                                            AssessmentRequestType requestType) {
        MaatApiAssessmentResponse response;
        String endpoint = configuration.getFinancialAssessmentEndpoints().getByRequestType(requestType);
        if (AssessmentRequestType.CREATE.equals(requestType)) {
            response =
                    maatAPIClient.post(assessment, new ParameterizedTypeReference<MaatApiAssessmentResponse>() {},
                            endpoint,
                            Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId));
        } else {
            response =
                    maatAPIClient.put(assessment, new ParameterizedTypeReference<MaatApiAssessmentResponse>() {},
                            endpoint,
                            Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId));
        }
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public RepOrderDTO updateCompletionDate(DateCompletionRequestDTO dateCompletionRequestDTO, String laaTransactionId) {
        RepOrderDTO response = maatAPIClient.post(dateCompletionRequestDTO, new ParameterizedTypeReference<RepOrderDTO>() {
                },
                configuration.getRepOrderEndpoints().getDateCompletionUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId));
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public PassportAssessmentDTO getPassportAssessmentFromRepId(Integer repId, String laaTransactionId) {
        PassportAssessmentDTO response = maatAPIClient.get(new ParameterizedTypeReference<PassportAssessmentDTO>() {
                                                           },
                configuration.getPassportAssessmentEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public HardshipReviewDTO getHardshipReviewFromRepId(Integer repId, String laaTransactionId) {
        HardshipReviewDTO response = maatAPIClient.get(new ParameterizedTypeReference<HardshipReviewDTO>() {
                                                       },
                configuration.getHardshipReviewEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public IOJAppealDTO getIOJAppealFromRepId(Integer repId, String laaTransactionId) {
        IOJAppealDTO response = maatAPIClient.get(new ParameterizedTypeReference<IOJAppealDTO>() {
                                                  },
                configuration.getIojAppealEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public FinancialAssessmentDTO getFinancialAssessment(Integer financialAssessmentId, String laaTransactionId) {
        FinancialAssessmentDTO response = maatAPIClient.get(new ParameterizedTypeReference<FinancialAssessmentDTO>() {
                                                            },
                configuration.getFinancialAssessmentEndpoints().getSearchUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                financialAssessmentId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public RepOrderDTO getRepOrder(Integer repId, String laaTransactionId) {
        RepOrderDTO response = maatAPIClient.get(new ParameterizedTypeReference<RepOrderDTO>() {
                                                 },
                configuration.getRepOrderEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}
