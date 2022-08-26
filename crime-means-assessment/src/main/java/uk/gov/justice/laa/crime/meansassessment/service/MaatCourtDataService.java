package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataClient;
import uk.gov.justice.laa.crime.meansassessment.common.Constants;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.*;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaatCourtDataService {

    private final MaatApiConfiguration configuration;
    private final MaatCourtDataClient maatCourtDataClient;
    public static final String RESPONSE_STRING = "Response from Court Data API: %s";

    public MaatApiAssessmentResponse persistMeansAssessment(MaatApiAssessmentRequest assessment,
                                                            String laaTransactionId,
                                                            AssessmentRequestType requestType) {
        MaatApiAssessmentResponse response;
        String endpoint = configuration.getFinancialAssessmentEndpoints().getByRequestType(requestType);
        if (AssessmentRequestType.CREATE.equals(requestType)) {
            response =
                    maatCourtDataClient.getApiResponseViaPOST(
                            assessment,
                            MaatApiAssessmentResponse.class,
                            endpoint,
                            Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
                    );
        } else {
            response =
                    maatCourtDataClient.getApiResponseViaPUT(
                            assessment,
                            MaatApiAssessmentResponse.class,
                            endpoint,
                            Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
                    );
        }
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public void updateCompletionDate(DateCompletionRequestDTO dateCompletionRequestDTO, String laaTransactionId) {
        maatCourtDataClient.getApiResponseViaPOST(
                dateCompletionRequestDTO,
                Void.class,
                configuration.getRepOrderEndpoints().getDateCompletionUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
    }

    public PassportAssessmentDTO getPassportAssessmentFromRepId(Integer repId, String laaTransactionId) {
        PassportAssessmentDTO response = maatCourtDataClient.getApiResponseViaGET(
                PassportAssessmentDTO.class,
                configuration.getPassportAssessmentEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public List<PassportAssessmentDTO> getPassportAssessmentsFromRepId(Integer repId, String laaTransactionId) {

        ParameterizedTypeReference<List<PassportAssessmentDTO>> responseClass =
                new ParameterizedTypeReference<>() {
                };

        PassportAssessmentDTO[] response = maatCourtDataClient.getApiResponseViaGET(
                PassportAssessmentDTO[].class,
                configuration.getPassportAssessmentEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        return Arrays.asList(response);
    }

    public HardshipReviewDTO getHardshipReviewFromRepId(Integer repId, String laaTransactionId) {
        HardshipReviewDTO response = maatCourtDataClient.getApiResponseViaGET(
                HardshipReviewDTO.class,
                configuration.getHardshipReviewEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public IOJAppealDTO getIOJAppealFromRepId(Integer repId, String laaTransactionId) {
        IOJAppealDTO response = maatCourtDataClient.getApiResponseViaGET(
                IOJAppealDTO.class,
                configuration.getIojAppealEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public FinancialAssessmentDTO getFinancialAssessment(Integer financialAssessmentId, String laaTransactionId) {
        FinancialAssessmentDTO response = maatCourtDataClient.getApiResponseViaGET(
                FinancialAssessmentDTO.class,
                configuration.getFinancialAssessmentEndpoints().getSearchUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                financialAssessmentId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public RepOrderDTO getRepOrder(Integer repId, String laaTransactionId) {
        RepOrderDTO response = maatCourtDataClient.getApiResponseViaGET(
                RepOrderDTO.class,
                configuration.getRepOrderEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}
