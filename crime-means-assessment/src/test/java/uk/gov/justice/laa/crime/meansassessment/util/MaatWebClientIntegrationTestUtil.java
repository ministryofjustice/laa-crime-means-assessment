package uk.gov.justice.laa.crime.meansassessment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

public abstract class MaatWebClientIntegrationTestUtil {

    public static MockWebServer mockMaatCourtDataApi = new MockWebServer();
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String INVALID_RESPONSE_ERROR_MSG = "Call to Court Data API failed, invalid response.";
    public static final String SERVICE_UNAVAILABLE_ERROR_MSG = "503 Received error 503 due to Service Unavailable";
    public static final String RETRIES_EXHAUSTED_ERROR_MSG = "Call to Court Data API failed. Retries exhausted: %d/%d.";

    protected void startMockWebServer() throws IOException {
        mockMaatCourtDataApi = new MockWebServer();
        mockMaatCourtDataApi.start(9999);
    }

    protected void shutdownMockWebServer() throws IOException {
        mockMaatCourtDataApi.shutdown();
    }

    protected void setOAuthDispatcher() {
        final Dispatcher dispatcher = new QueueDispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

                if ("/oauth2/token".equals(request.getPath())) {
                    return getOauthResponse();
                }

                var requestLine = request.getRequestLine();
                if ("GET /favicon.ico HTTP/1.1".equals(requestLine)) {
                    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
                }

                return getResponseQueue().take();
            }
        };
        mockMaatCourtDataApi.setDispatcher(dispatcher);
    }

    protected MockResponse getOauthResponse() {
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );
        String responseBody;
        MockResponse response = new MockResponse();
        response.setResponseCode(OK.code());
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON);

        try {
            responseBody = OBJECT_MAPPER.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return response.setBody(responseBody);
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
