package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.crime.commons.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentRequestDTOBuilder;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiRollbackMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaService;
import uk.gov.justice.laa.crime.meansassessment.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.validation.validator.MeansAssessmentValidationProcessor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.*;
import static uk.gov.justice.laa.crime.meansassessment.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MeansAssessmentController.class)
class MeansAssessmentControllerTest {

    private static final boolean IS_VALID = true;
    private static final String ENDPOINT_URL = "/api/internal/v1/assessment/means";
    private static final String FA_THRESHOLD_ENDPOINT_URL = "/api/internal/v1/assessment/means/fullAssessmentThreshold/";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeansAssessmentRequestDTOBuilder assessmentRequestDTOBuilder;

    @MockBean
    private MeansAssessmentService meansAssessmentService;

    @MockBean
    private AssessmentCriteriaService assessmentCriteriaService;

    @MockBean
    private MeansAssessmentValidationProcessor assessmentValidator;

    @MockBean
    private TraceIdHandler traceIdHandler;

    @Test
    void createAssessment_success() throws Exception {
        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        var initialMeansAssessmentResponse =
                TestModelDataBuilder.getInitMeansAssessmentResponse(IS_VALID);

        when(assessmentRequestDTOBuilder.buildRequestDTO(any(ApiCreateMeansAssessmentRequest.class)))
                .thenReturn(TestModelDataBuilder.getMeansAssessmentRequestDTO(true));

        when(meansAssessmentService.doAssessment(any(MeansAssessmentRequestDTO.class), any(RequestType.class)))
                .thenReturn(initialMeansAssessmentResponse);

        when(assessmentValidator.validate(any(MeansAssessmentRequestDTO.class), any(RequestType.class)))
                .thenReturn(Optional.empty());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, initialMeansAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value(MEANS_ASSESSMENT_ID));
    }

    @Test
    void updateAssessment_success() throws Exception {
        var updateAssessmentRequest =
                TestModelDataBuilder.getApiUpdateMeansAssessmentRequest(IS_VALID);
        var updateAssessmentRequestJson = objectMapper.writeValueAsString(updateAssessmentRequest);
        var updateAssessmentResponse =
                TestModelDataBuilder.getFullMeansAssessmentResponse(IS_VALID);

        when(assessmentRequestDTOBuilder.buildRequestDTO(any(ApiUpdateMeansAssessmentRequest.class)))
                .thenReturn(TestModelDataBuilder.getMeansAssessmentRequestDTO(true));

        when(meansAssessmentService.doAssessment(any(MeansAssessmentRequestDTO.class), any(RequestType.class)))
                .thenReturn(updateAssessmentResponse);

        when(assessmentValidator.validate(any(MeansAssessmentRequestDTO.class), any(RequestType.class)))
                .thenReturn(Optional.empty());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, updateAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value(MEANS_ASSESSMENT_ID));
    }

    @Test
    void createAssessment_RequestObjectFailsValidation() throws Exception {
        var createAssessmentRequest =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(!IS_VALID);
        var createAssessmentRequestJson = objectMapper.writeValueAsString(createAssessmentRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, createAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateAssessment_RequestObjectFailsValidation() throws Exception {
        var updateAssessmentRequest =
                TestModelDataBuilder.getApiUpdateMeansAssessmentRequest(!IS_VALID);
        var updateAssessmentRequestJson = objectMapper.writeValueAsString(updateAssessmentRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, updateAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createAssessment_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createAssessment_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAssessment_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "", ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateAssessment_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidParam_whenGetOldAssessmentInvoked_shouldFailBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", ENDPOINT_URL, true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenValidParam_whenGetOldAssessmentInvoked_shouldSuccess() throws Exception {
        when(meansAssessmentService.getOldAssessment(any())).thenReturn(new ApiGetMeansAssessmentResponse());
        mvc.perform(buildRequestGivenContent(
                HttpMethod.GET,
                "",
                ENDPOINT_URL + "/" + MEANS_ASSESSMENT_ID,
                true)
        ).andExpect(status().isOk());
    }

    @Test
    void givenInvalidParam_whenFullAssessmentThresholdInvoked_shouldFailBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", FA_THRESHOLD_ENDPOINT_URL, true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenValidParam_whenFullAssessmentThresholdInvoked_shouldSuccess() throws Exception {
        when(assessmentCriteriaService.getFullAssessmentThreshold(any())).thenReturn(new BigDecimal("1000"));
        mvc.perform(buildRequestGivenContent(
                        HttpMethod.GET,
                        "",
                        FA_THRESHOLD_ENDPOINT_URL + "/" + ASSESSMENT_DATE,
                        true)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$").value(FULL_THRESHOLD));

    }

    @Test
    void givenInvalidParam_whenRollbackInvoked_shouldFailBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "", ENDPOINT_URL + "/rollback", true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenValidParam_whenRollbackInvoked_shouldSuccess() throws Exception {
        when(meansAssessmentService.updateFinancialAssessment(any())).thenReturn(
                new ApiRollbackMeansAssessmentResponse()
                        .withAssessmentId(MEANS_ASSESSMENT_ID)
                        .withFassFullStatus(CurrentStatus.IN_PROGRESS)
                        .withFullResult(FullAssessmentResult.PASS.getResult())
                        .withFassInitStatus(CurrentStatus.IN_PROGRESS)
                        .withInitResult(InitAssessmentResult.FULL.name())
        );
        var rollbackAssessmentRequestJson = objectMapper.writeValueAsString(TestModelDataBuilder.getMaatApiRollbackAssessment());
        mvc.perform(buildRequestGivenContent(
                HttpMethod.PUT,
                rollbackAssessmentRequestJson,
                ENDPOINT_URL + "/rollback",
                true)
        ).andExpect(status().isOk());
    }
}