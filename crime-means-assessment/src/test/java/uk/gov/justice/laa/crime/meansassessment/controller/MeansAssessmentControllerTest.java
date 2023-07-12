package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentRequestDTOBuilder;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiUpdateMeansAssessmentRequest;
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
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MeansAssessmentController.class)
public class MeansAssessmentControllerTest {

    private static final boolean IS_VALID = true;
    private static final String ENDPOINT_URL = "/api/internal/v1/assessment/means";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeansAssessmentRequestDTOBuilder assessmentRequestDTOBuilder;

    @MockBean
    private MeansAssessmentService meansAssessmentService;

    @MockBean
    private MeansAssessmentValidationProcessor assessmentValidator;

    @Test
    public void createAssessment_success() throws Exception {
        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        var initialMeansAssessmentResponse =
                TestModelDataBuilder.getInitMeansAssessmentResponse(IS_VALID);

        when(assessmentRequestDTOBuilder.buildRequestDTO(any(ApiCreateMeansAssessmentRequest.class)))
                .thenReturn(TestModelDataBuilder.getMeansAssessmentRequestDTO(true));

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

        when(assessmentRequestDTOBuilder.buildRequestDTO(any(ApiUpdateMeansAssessmentRequest.class)))
                .thenReturn(TestModelDataBuilder.getMeansAssessmentRequestDTO(true));

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