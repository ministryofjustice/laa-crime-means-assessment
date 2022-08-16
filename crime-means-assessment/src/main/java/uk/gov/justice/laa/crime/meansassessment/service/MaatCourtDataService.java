package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataClient;
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
                configuration.getFinancialAssessmentEndpoints().getDateCompletionUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
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
}
