package uk.gov.justice.laa.crime.meansassessment.initial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.initial.service.InitialMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.initial.validator.InitialMeansAssessmentValidationProcessor;
import uk.gov.justice.laa.crime.meansassessment.model.initial.ApiCreateMeansAssessmentResponse;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.MEANS_ASSESSMENT_ID;

@RunWith(SpringRunner.class)
@WebMvcTest(InitialMeansAssessmentController.class)
public class InitialMeansAssessmentControllerTest {

    private static final boolean IS_VALID = true;
    @Autowired
    private MockMvc mvc;

    @MockBean
    private InitialMeansAssessmentService initialMeansAssessmentService;

    @MockBean
    private InitialMeansAssessmentValidationProcessor initialMeansAssessmentValidationProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createAssessment_success() throws Exception {
        //given
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        // and given
        var initialMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(IS_VALID);
        when(initialMeansAssessmentService.createAssessment(initialMeansAssessmentRequest))
                .thenReturn(initialMeansAssessmentResponse);

        when(initialMeansAssessmentValidationProcessor.validate(any(ApiCreateMeansAssessmentResponse.class))).thenReturn(Optional.empty());


        mvc.perform(MockMvcRequestBuilders.post("/api/internal/v1/assessment/means/initial").content(initialMeansAssessmentRequestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value(MEANS_ASSESSMENT_ID));
    }


    @Test
    public void createAssessment_RequestObjectFailsValidation() throws Exception {
        //given
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(!IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        // and given
        var initialMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(IS_VALID);
        when(initialMeansAssessmentService.createAssessment(initialMeansAssessmentRequest))
                .thenReturn(initialMeansAssessmentResponse);

        mvc.perform(MockMvcRequestBuilders.post("/api/internal/v1/assessment/means/initial").content(initialMeansAssessmentRequestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //TODO: test invalid RESPONSE object
    @Test
    public void createAssessment_ResponseObjectFailsValidation() throws Exception {
        //given
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        // and given
        var initialMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(!IS_VALID);
        when(initialMeansAssessmentService.createAssessment(initialMeansAssessmentRequest))
                .thenReturn(initialMeansAssessmentResponse);
        when(initialMeansAssessmentValidationProcessor.validate(any(ApiCreateMeansAssessmentResponse.class))).thenThrow(new ValidationException("exception"));

        mvc.perform(MockMvcRequestBuilders.post("/api/internal/v1/assessment/means/initial").content(initialMeansAssessmentRequestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}