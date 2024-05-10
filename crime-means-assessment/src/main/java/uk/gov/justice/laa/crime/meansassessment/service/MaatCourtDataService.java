package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaatCourtDataService {

    private static final String RESPONSE_STRING = "Response from Court Data API: %s";
    private final MaatApiConfiguration configuration;
    @Qualifier("maatApiClient")
    private final RestAPIClient maatAPIClient;

    public MaatApiAssessmentResponse persistMeansAssessment(MaatApiAssessmentRequest assessment,
                                                            RequestType requestType) {
        MaatApiAssessmentResponse response;
        String endpoint = configuration.getFinancialAssessmentEndpoints().getByRequestType(requestType);
        if (RequestType.CREATE.equals(requestType)) {
            response =
                    maatAPIClient.post(assessment, new ParameterizedTypeReference<>() {
                            },
                            endpoint,
                            Map.of());
        } else {
            response =
                    maatAPIClient.put(assessment, new ParameterizedTypeReference<>() {
                            },
                            endpoint,
                            Map.of());
        }
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public RepOrderDTO updateCompletionDate(DateCompletionRequestDTO dateCompletionRequestDTO) {
        RepOrderDTO response = maatAPIClient.post(dateCompletionRequestDTO, new ParameterizedTypeReference<>() {
                },
                configuration.getRepOrderEndpoints().getDateCompletionUrl(),
                Map.of());
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public PassportAssessmentDTO getPassportAssessmentFromRepId(Integer repId) {
        PassportAssessmentDTO response = maatAPIClient.get(new ParameterizedTypeReference<>() {
                                                           },
                configuration.getPassportAssessmentEndpoints().getFindUrl(),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public HardshipReviewDTO getHardshipReviewFromRepId(Integer repId) {
        HardshipReviewDTO response = maatAPIClient.get(new ParameterizedTypeReference<>() {
                                                       },
                configuration.getHardshipReviewEndpoints().getFindUrl(),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public IOJAppealDTO getIOJAppealFromRepId(Integer repId) {
        IOJAppealDTO response = maatAPIClient.get(new ParameterizedTypeReference<>() {
                                                  },
                configuration.getIojAppealEndpoints().getFindUrl(),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public FinancialAssessmentDTO getFinancialAssessment(Integer financialAssessmentId) {
        FinancialAssessmentDTO response = maatAPIClient.get(new ParameterizedTypeReference<>() {
                                                            },
                configuration.getFinancialAssessmentEndpoints().getSearchUrl(),
                financialAssessmentId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public RepOrderDTO getRepOrder(Integer repId) {
        RepOrderDTO response = maatAPIClient.get(new ParameterizedTypeReference<>() {
                                                 },
                configuration.getRepOrderEndpoints().getFindUrl(),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public void rollbackFinancialAssessment(Integer financialAssessmentId, Map<String, Object> updateFields) {
        maatAPIClient.patch(updateFields,
                new ParameterizedTypeReference<>() {
                },
                configuration.getFinancialAssessmentEndpoints().getRollbackUrl(),
                Map.of(),
                financialAssessmentId
        );
    }
}
