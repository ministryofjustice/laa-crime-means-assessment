package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.meansassessment.CrimeMeansAssessmentApplication;
import uk.gov.justice.laa.crime.meansassessment.config.CrimeMeansAssessmentTestConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.validation.validator.MeansAssessmentValidationProcessor;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.MEANS_ASSESSMENT_ID;

@RunWith(SpringRunner.class)
@Import(CrimeMeansAssessmentTestConfiguration.class)
@SpringBootTest(classes = {CrimeMeansAssessmentApplication.class}, webEnvironment = DEFINED_PORT)
@DirtiesContext
public class MeansAssessmentControllerTest {

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
    private MeansAssessmentService meansAssessmentService;

    @MockBean
    private MeansAssessmentValidationProcessor assessmentValidator;

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

    private MockHttpServletRequestBuilder buildRequestForGet(HttpMethod method, String url, boolean withAuth) throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(method, url)
                        .contentType(MediaType.APPLICATION_JSON);
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

    @Test
    public void createAssessment_success() throws Exception {
        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        var initialMeansAssessmentResponse =
                TestModelDataBuilder.getInitMeansAssessmentResponse(IS_VALID);

        when(meansAssessmentService.doAssessment(any(MeansAssessmentRequestDTO.class), any(AssessmentRequestType.class)))
                .thenReturn(initialMeansAssessmentResponse);

        when(assessmentValidator.validate(any(MeansAssessmentRequestDTO.class))).thenReturn(Optional.empty());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, initialMeansAssessmentRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value(MEANS_ASSESSMENT_ID));
    }

    @Test
    public void updateAssessment_success() throws Exception {
        var updateAssessmentRequest =
                TestModelDataBuilder.getApiUpdateMeansAssessmentRequest(IS_VALID);
        var updateAssessmentRequestJson = objectMapper.writeValueAsString(updateAssessmentRequest);
        var updateAssessmentResponse =
                TestModelDataBuilder.getFullMeansAssessmentResponse(IS_VALID);

        when(meansAssessmentService.doAssessment(any(MeansAssessmentRequestDTO.class), any(AssessmentRequestType.class)))
                .thenReturn(updateAssessmentResponse);

        when(assessmentValidator.validate(any(MeansAssessmentRequestDTO.class))).thenReturn(Optional.empty());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, updateAssessmentRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value(MEANS_ASSESSMENT_ID));
    }

    @Test
    public void createAssessment_RequestObjectFailsValidation() throws Exception {
        var createAssessmentRequest =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(!IS_VALID);
        var createAssessmentRequestJson = objectMapper.writeValueAsString(createAssessmentRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, createAssessmentRequestJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateAssessment_RequestObjectFailsValidation() throws Exception {
        var updateAssessmentRequest =
                TestModelDataBuilder.getApiUpdateMeansAssessmentRequest(!IS_VALID);
        var updateAssessmentRequestJson = objectMapper.writeValueAsString(updateAssessmentRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, updateAssessmentRequestJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createAssessment_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, ""))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createAssessment_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAssessment_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", false))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void updateAssessment_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, ""))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateAssessment_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateAssessment_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getOldAssessment_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(buildRequestForGet(HttpMethod.GET, MEANS_ASSESSMENT_ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenInvalidPram_whenGetOldAssessmentInvoked_shouldFailBadRequest() throws Exception {
        mvc.perform(buildRequestForGet(HttpMethod.GET, MEANS_ASSESSMENT_ENDPOINT_URL, true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenValidPram_whenGetOldAssessmentInvoked_shouldSuccess() throws Exception {
        when(meansAssessmentService.getOldAssessment(any(), any())).thenReturn(new ApiGetMeansAssessmentResponse());
        mvc.perform(buildRequestForGet(HttpMethod.GET, MEANS_ASSESSMENT_ENDPOINT_URL + "/" + MEANS_ASSESSMENT_ID + "/" + TestModelDataBuilder.MEANS_ASSESSMENT_TRANSACTION_ID, true))
                .andExpect(status().isOk());
    }
}