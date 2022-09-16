package uk.gov.justice.laa.crime.meansassessment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiOAuth2Client;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;

public abstract class MaatWebClientIntegrationTestUtil {

    public static MockWebServer mockMaatCourtDataApi;

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String INVALID_RESPONSE_ERROR_MSG = "Call to Court Data API failed, invalid response.";
    public static final String SERVICE_UNAVAILABLE_ERROR_MSG = "503 Received error 503 due to Service Unavailable";
    public static final String RETRIES_EXHAUSTED_ERROR_MSG = "Call to Court Data API failed. Retries exhausted: %d/%d.";

    protected void startMockWebServer() throws IOException {
        mockMaatCourtDataApi = new MockWebServer();
        mockMaatCourtDataApi.start();
    }

    protected void shutdownMockWebServer() throws IOException {
        mockMaatCourtDataApi.shutdown();
    }

    protected WebClient buildWebClient(
            MaatApiConfiguration maatConfig,
            RetryConfiguration retryConfiguration,
            ClientRegistrationRepository clientRegistrations,
            OAuth2AuthorizedClientRepository authorizedClients
    ) {
        MaatApiOAuth2Client client = new MaatApiOAuth2Client(maatConfig, retryConfiguration);
        return client.webClient(clientRegistrations, authorizedClients);
    }

    protected RetryConfiguration generateRetryConfiguration(Integer maxRetries, Integer minBackOff, Double jitterValue) {
        RetryConfiguration retryConfiguration = new RetryConfiguration();
        retryConfiguration.setMaxRetries(maxRetries);
        retryConfiguration.setMinBackOffPeriod(minBackOff);
        retryConfiguration.setJitterValue(jitterValue);
        return retryConfiguration;
    }

    protected <T> String setupMockApiResponses(T responseObject, Integer attemptsBeforeSuccess) throws JsonProcessingException {
        String expectedResponse = OBJECT_MAPPER.writeValueAsString(responseObject);
        for (int i = 0; i < attemptsBeforeSuccess; i++) {
            mockMaatCourtDataApi.enqueue(new MockResponse()
                    .setResponseCode(SERVICE_UNAVAILABLE.code()));
        }

        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setBody(expectedResponse).addHeader("Content-Type", "application/json"));

        return expectedResponse;
    }

    protected void setupMockInvalidResponse() {
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setBody("Invalid response.")
                .addHeader("Content-Type", "application/json")
        );
    }

    protected void setupMockNotFoundResponse() {
        mockMaatCourtDataApi.enqueue(new MockResponse().setResponseCode(NOT_FOUND.code()));
    }

    protected String getRetryErrorResponse(int maxRetries) {
        return String.format(RETRIES_EXHAUSTED_ERROR_MSG, maxRetries, maxRetries);
    }
}
