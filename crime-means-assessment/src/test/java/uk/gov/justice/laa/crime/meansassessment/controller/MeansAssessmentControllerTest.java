package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.meansassessment.CrimeMeansAssessmentApplication;
import uk.gov.justice.laa.crime.meansassessment.config.CrimeMeansAssessmentTestConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.validation.validator.MeansAssessmentValidationProcessor;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.MEANS_ASSESSMENT_ID;
import static uk.gov.justice.laa.crime.meansassessment.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@RunWith(SpringRunner.class)
@Import(CrimeMeansAssessmentTestConfiguration.class)
@SpringBootTest(classes = {CrimeMeansAssessmentApplication.class})
public class MeansAssessmentControllerTest {

    private static final boolean IS_VALID = true;
    private static final String ENDPOINT_URL = "/api/internal/v1/assessment/means";

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeansAssessmentService meansAssessmentService;

    @MockBean
    private MeansAssessmentValidationProcessor assessmentValidator;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
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

        when(assessmentValidator.validate(any(MeansAssessmentRequestDTO.class), any(AssessmentRequestType.class)))
                .thenReturn(Optional.empty());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, initialMeansAssessmentRequestJson, ENDPOINT_URL))
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

        when(assessmentValidator.validate(any(MeansAssessmentRequestDTO.class), any(AssessmentRequestType.class)))
                .thenReturn(Optional.empty());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, updateAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value(MEANS_ASSESSMENT_ID));
    }

    @Test
    public void createAssessment_RequestObjectFailsValidation() throws Exception {
        var createAssessmentRequest =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(!IS_VALID);
        var createAssessmentRequestJson = objectMapper.writeValueAsString(createAssessmentRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, createAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateAssessment_RequestObjectFailsValidation() throws Exception {
        var updateAssessmentRequest =
                TestModelDataBuilder.getApiUpdateMeansAssessmentRequest(!IS_VALID);
        var updateAssessmentRequestJson = objectMapper.writeValueAsString(updateAssessmentRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, updateAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createAssessment_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createAssessment_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAssessment_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void updateAssessment_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "", ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateAssessment_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateAssessment_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getOldAssessment_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenInvalidPram_whenGetOldAssessmentInvoked_shouldFailBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", ENDPOINT_URL, true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenValidPram_whenGetOldAssessmentInvoked_shouldSuccess() throws Exception {
        when(meansAssessmentService.getOldAssessment(any(), any())).thenReturn(new ApiGetMeansAssessmentResponse());
        mvc.perform(buildRequestGivenContent(
                HttpMethod.GET,
                "",
                ENDPOINT_URL + "/" + MEANS_ASSESSMENT_ID,
                true)
        ).andExpect(status().isOk());
    }
}