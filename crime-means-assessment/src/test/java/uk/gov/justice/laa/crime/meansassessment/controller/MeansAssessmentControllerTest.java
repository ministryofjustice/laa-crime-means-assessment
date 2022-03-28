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
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.meansassessment.CrimeMeansAssessmentApplication;
import uk.gov.justice.laa.crime.meansassessment.config.CrimeMeansAssessmentTestConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
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
@SpringBootTest(classes = CrimeMeansAssessmentApplication.class, webEnvironment = DEFINED_PORT)
public class MeansAssessmentControllerTest {

    private static final boolean IS_VALID = true;
    private static final String CLIENT_ID = "test-client";
    private static final String CLIENT_SECRET = "secret";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String SCOPE_READ_WRITE = "READ_WRITE";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private Environment env;

    private MockMvc mvc;

    @MockBean
    private MeansAssessmentService meansAssessmentService;

    @MockBean
    private MeansAssessmentValidationProcessor assessmentValidator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).addFilter(springSecurityFilterChain).build();
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
    public void createInitialAssessment_success() throws Exception {
        //given
        final String accessToken = obtainAccessToken();
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        // and given
        var initialMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(IS_VALID);

        when(meansAssessmentService.doAssessment(initialMeansAssessmentRequest, AssessmentRequestType.CREATE))
                .thenReturn(initialMeansAssessmentResponse);

        when(assessmentValidator.validate(any(ApiCreateMeansAssessmentRequest.class))).thenReturn(Optional.empty());

        mvc.perform(post("/api/internal/v1/assessment/means")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(initialMeansAssessmentRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value(MEANS_ASSESSMENT_ID));
    }

    @Test
    public void createInitialAssessment_RequestObjectFailsValidation() throws Exception {
        //given
        final String accessToken = obtainAccessToken();
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(!IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        // and given
        var initialMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(IS_VALID);
        when(meansAssessmentService.doAssessment(initialMeansAssessmentRequest, AssessmentRequestType.CREATE))
                .thenReturn(initialMeansAssessmentResponse);

        mvc.perform(post("/api/internal/v1/assessment/means")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(initialMeansAssessmentRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createInitialAssessment_ServerError_RequestBodyIsMissing() throws Exception {
        final String accessToken = obtainAccessToken();
        mvc.perform(post("/api/internal/v1/assessment/means")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(new String())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createInitialAssessment_BadRequest_RequestEmtpyBody() throws Exception {
        final String accessToken = obtainAccessToken();
        mvc.perform(post("/api/internal/v1/assessment/means")
                        .header("Authorization", "Bearer " + accessToken)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createInitialAssessment_Unauthorized_NoAccessToken() throws Exception {

        mvc.perform(post("/api/internal/v1/assessment/means")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}