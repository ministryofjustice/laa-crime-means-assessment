package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.meansassessment.CrimeMeansAssessmentApplication;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentResponseBuilder;
import uk.gov.justice.laa.crime.meansassessment.client.MaatCourtDataClient;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.service.CrownCourtEligibilityService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getAuthorizationResponseDTO;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.getOutstandingAssessmentResultDTO;


@SpringBootTest(classes = {
        CrimeMeansAssessmentApplication.class, MeansAssessmentResponseBuilder.class}, webEnvironment = DEFINED_PORT)
@RunWith(SpringRunner.class)
@DirtiesContext
public class MeansAssessmentIntegrationTest {

    private static final boolean IS_VALID = true;
    private static final String CLIENT_SECRET = "secret";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String CLIENT_ID = "test-client";
    private static final String SCOPE_READ_WRITE = "READ_WRITE";
    private static final String MEANS_ASSESSMENT_ENDPOINT_URL = "/api/internal/v1/assessment/means";

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    @MockBean
    private WebClient webClient;

    @MockBean
    private MaatCourtDataClient maatCourtDataClient;

    @MockBean
    private CrownCourtEligibilityService crownCourtEligibilityService;

    private final WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
    private final WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
    private final WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    private final WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);

    private final WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    private MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content) throws Exception {
        return buildRequestGivenContent(method, content, true);
    }

    private MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content, boolean withAuth) throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(method, MEANS_ASSESSMENT_ENDPOINT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);
        if (withAuth) {
            final String accessToken = obtainAccessToken();
            requestBuilder.header("Authorization", "Bearer " + accessToken);
        }
        return requestBuilder;
    }

    private String obtainAccessToken() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", CLIENT_CREDENTIALS);
        params.add("scope", SCOPE_READ_WRITE);

        ResultActions result = mvc.perform(post("/oauth2/token")
                        .params(params)
                        .with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
                .andExpect(status().isOk());
        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private void setUpWebClientMock() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class), any(HashMap.class));
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class), any(HashMap.class));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(maatCourtDataClient.getApiResponseViaGET(
                eq(AuthorizationResponseDTO.class), anyString(), anyMap(), any())
        ).thenReturn(getAuthorizationResponseDTO(true));

        when(maatCourtDataClient.getApiResponseViaGET(
                eq(OutstandingAssessmentResultDTO.class), anyString(), anyMap(), any())
        ).thenReturn(getOutstandingAssessmentResultDTO(false));
    }

    @Test
    public void givenAInvalidContent_whenCreateAssessmentInvoked_ShouldFailsBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNoOAuthToken_whenCreateAssessmentInvoked_shouldFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAInvalidRepId_whenCreateAssessmentInvoked_ShouldFailsValidation() throws Exception {
        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        initialMeansAssessmentRequest.setRepId(-1000);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);

        MvcResult result = mvc.perform(buildRequestGivenContent(HttpMethod.POST, initialMeansAssessmentRequestJson))
                .andExpect(status().is4xxClientError())
                .andReturn();
        assertErrorScenario("Rep Id is missing from request and is required", result);

    }

    @Test
    public void givenAValidCreateMeansAssessmentRequest_whenMaatCourtApiCallFails_ShouldFailsCreateMeanAssessment() throws Exception {
        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);

        doThrow(new RuntimeException()).when(maatCourtDataClient).getApiResponseViaPOST(any(), any(), any(), any());
        setUpWebClientMock();

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, initialMeansAssessmentRequestJson))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenAValidCreateMeansAssessmentRequest_WhenCreateAssessmentInvoked_success() throws Exception {
        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);

        when(maatCourtDataClient.getApiResponseViaPOST(any(), any(), any(), any()))
                .thenReturn(TestModelDataBuilder.getMaatApiAssessmentResponse());
        setUpWebClientMock();

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, initialMeansAssessmentRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(maatCourtDataClient, timeout(1)).getApiResponseViaPOST(any(), any(), any(), any());
    }

    @Test
    public void givenAInvalidContent_whenUpdateAssessmentInvoked_ShouldFailsBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNoOAuthToken_whenUpdateAssessmentInvoked_shouldFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAValidUpdateMeansAssessmentRequest_whenUpdateAssessmentInvoked_ShouldSuccess() throws Exception {

        var updateAssessmentRequest =
                TestModelDataBuilder.getUpdateMeansAssessmentRequest(IS_VALID);
        updateAssessmentRequest.setAssessmentType(AssessmentType.FULL);
        var updateAssessmentRequestJson = objectMapper.writeValueAsString(updateAssessmentRequest);
        setUpWebClientMock();
        when(maatCourtDataClient.getApiResponseViaPUT(any(), any(), any(), any()))
                .thenReturn(TestModelDataBuilder.getMaatApiAssessmentResponse());
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, updateAssessmentRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(maatCourtDataClient, timeout(1)).getApiResponseViaPUT(any(), any(), any(), any());

    }

    private void assertErrorScenario(String expectedErrorMessage, MvcResult result) throws Exception {
        ErrorDTO expectedError = ErrorDTO.builder().code(HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.name()).message(expectedErrorMessage).build();
        assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(expectedError));
    }
}