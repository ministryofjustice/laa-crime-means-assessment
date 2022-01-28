package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.justice.laa.crime.meansassessment.client.AuthorisationMeansAssessmentClient;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.AuthorizationResponse;
import uk.gov.justice.laa.crime.meansassessment.service.MeansAssessmentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.MEANS_ASSESSMENT_ID;

@RunWith(SpringRunner.class)
@WebMvcTest(MeansAssessmentController.class)
public class MeansAssessmentControllerTest {

    private static final boolean IS_VALID = true;
    @Autowired
    private MockMvc mvc;



    @MockBean
    private MeansAssessmentService meansAssessmentService;

    @Mock
    private AuthorisationMeansAssessmentClient workReasonsClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createInitialAssessment_success() throws Exception {
        //given
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        // and given
        var initialMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(IS_VALID);
        //when(meansAssessmentService.createInitialAssessment(initialMeansAssessmentRequest)).thenReturn(initialMeansAssessmentResponse);


        mvc.perform(MockMvcRequestBuilders.post("/api/internal/v1/assessment/means").content(initialMeansAssessmentRequestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value(MEANS_ASSESSMENT_ID));
    }


    @Test
    public void createInitialAssessment_RequestObjectFailsValidation() throws Exception {
        //given
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(!IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);
        // and given
        var initialMeansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(IS_VALID);
        //when(meansAssessmentService.createInitialAssessment(initialMeansAssessmentRequest)).thenReturn(initialMeansAssessmentResponse);

        when(workReasonsClient.checkWorkReasonStatus(any())).thenReturn(AuthorizationResponse.builder().result(true).build());

        mvc.perform(MockMvcRequestBuilders.post("/api/internal/v1/assessment/means").content(initialMeansAssessmentRequestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createInitialAssessment_ServerError_RequestBodyIsMissing() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/api/internal/v1/assessment/means").content(new String()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
    @Test
    public void createInitialAssessment_BadRequest_RequestEmtpyBody() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/api/internal/v1/assessment/means").content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}