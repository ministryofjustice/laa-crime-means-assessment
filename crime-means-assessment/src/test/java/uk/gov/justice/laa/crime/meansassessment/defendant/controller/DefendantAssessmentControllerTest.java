package uk.gov.justice.laa.crime.meansassessment.defendant.controller;

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
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.service.DefendantAssessmentService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.DEFENDANT_ASSESSMENT_ID;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.DEFENDANT_ASSESSMENT_UPDATED_INFO;

@RunWith(SpringRunner.class)
@WebMvcTest(DefendantAssessmentController.class)
public class DefendantAssessmentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DefendantAssessmentService defendantAssessmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getDefendantAssessmentByID_Success() throws Exception {
        //given
        var defendantAssessment = TestModelDataBuilder.getDefendantAssessmentDTO();
        //when
        when(defendantAssessmentService.findById(DEFENDANT_ASSESSMENT_ID))
                        .thenReturn(defendantAssessment);

        mvc.perform(MockMvcRequestBuilders.get("/defendantmeansassessment/"+DEFENDANT_ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.updatedInfo").value(DEFENDANT_ASSESSMENT_UPDATED_INFO));
    }

    @Test
    public void getDefendantAssessmentByIDNotFound() throws Exception {
        //given
        String idNotInDB = "484cf7b4-b910-4f28-82bd-b60c69467054";
        //when
        when(defendantAssessmentService.findById(idNotInDB))
                .thenReturn(null);

        mvc.perform(MockMvcRequestBuilders.get("/defendantmeansassessment/"+idNotInDB))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getDefendantAssessmentByID_BadRequestError_invalidID() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/defendantmeansassessment/expectBadRequestError"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createNewDefendantAssessment_Success() throws Exception {
        //given
        var defendantAssessmentToSave = DefendantAssessmentEntity.builder().updatedInfo(DEFENDANT_ASSESSMENT_UPDATED_INFO).build();
        // and given
        var returnedDefendantAssessment = TestModelDataBuilder.getDefendantAssessmentDTO();
        //when
        when(defendantAssessmentService.save(defendantAssessmentToSave))
                .thenReturn(returnedDefendantAssessment);

        String json = objectMapper.writeValueAsString(DefendantAssessmentEntity.builder()
                .updatedInfo(DEFENDANT_ASSESSMENT_UPDATED_INFO)
                .build());

        mvc.perform(MockMvcRequestBuilders.post("/defendantmeansassessment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returnedDefendantAssessment.getId()));
    }

    @Test
    public void createNewDefendantAssessment_ServerError_RequestBodyIsMissing() throws Exception {

       mvc.perform(MockMvcRequestBuilders.post("/defendantmeansassessment").content(new String()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void createNewDefendantAssessment_BadRequest_IdIsPresent() throws Exception {
        //given
        var defendantAssessmentWithID =TestModelDataBuilder.getDefendantAssessmentDTO();

        String json = objectMapper.writeValueAsString(TestModelDataBuilder.getDefendantAssessmentDTO());

        mvc.perform(MockMvcRequestBuilders.post("/defendantmeansassessment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateDefendantAssessment_Success() throws Exception {
        //given
        var defendantAssessmentToUpdate = TestModelDataBuilder.getDefendantAssessmentDTO();
        // and given
        var updatedDefendantAssessment = TestModelDataBuilder.getDefendantAssessmentDTO();
        //when

        when(defendantAssessmentService.findById(defendantAssessmentToUpdate.getId()))
                .thenReturn(defendantAssessmentToUpdate);

        //when
        when(defendantAssessmentService.update(defendantAssessmentToUpdate))
                .thenReturn(updatedDefendantAssessment);

        String json = objectMapper.writeValueAsString(defendantAssessmentToUpdate);

        mvc.perform(MockMvcRequestBuilders.put("/defendantmeansassessment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.updatedInfo").value(updatedDefendantAssessment.getUpdatedInfo()));
    }

    @Test
    public void updateDefendantAssessment_NotFoundError_notInDB() throws Exception {
        //given
        var defendantAssessmentNotInDB = TestModelDataBuilder.getDefendantAssessmentDTO();

        String json = objectMapper.writeValueAsString(defendantAssessmentNotInDB);

        mvc.perform(MockMvcRequestBuilders.put("/defendantmeansassessment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateDefendantAssessment_BadRequestError_invalidID() throws Exception {
        //given
        var defendantAssessmentNotInDB = DefendantAssessmentEntity.builder()
                .id("484cf7b4")
                .updatedInfo(DEFENDANT_ASSESSMENT_UPDATED_INFO)
                .build();

        String json = objectMapper.writeValueAsString(defendantAssessmentNotInDB);

        mvc.perform(MockMvcRequestBuilders.put("/defendantmeansassessment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
    @Test
    public void updateDefendantAssessment_BadRequestError_missingID() throws Exception {
        //given
        var defendantAssessmentNotInDB = DefendantAssessmentEntity.builder()
                .updatedInfo(DEFENDANT_ASSESSMENT_UPDATED_INFO)
                .build();

        String json = objectMapper.writeValueAsString(defendantAssessmentNotInDB);

        mvc.perform(MockMvcRequestBuilders.put("/defendantmeansassessment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void updateDefendantAssessment_ServerError_RequestBodyIsMissing() throws Exception {

        mvc.perform(MockMvcRequestBuilders.put("/defendantmeansassessment").content(new String()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void deleteDefendantAssessmentByID_Success() throws Exception {
        //given
        var defendantAssessmentToDelete = TestModelDataBuilder.getDefendantAssessmentDTO();
        //when
        when(defendantAssessmentService.findById(DEFENDANT_ASSESSMENT_ID))
                .thenReturn(defendantAssessmentToDelete);

        when(defendantAssessmentService.deleteById(DEFENDANT_ASSESSMENT_ID))
                .thenReturn("successful delete");

        mvc.perform(MockMvcRequestBuilders.delete("/defendantmeansassessment/"+DEFENDANT_ASSESSMENT_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteDefendantAssessment_BadRequestError_missingID() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/defendantmeansassessment/"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteDefendantAssessment_NotFoundError_notInDB() throws Exception {
        //given
        var defendantAssessmentNotInDB = TestModelDataBuilder.getDefendantAssessmentDTO();

        mvc.perform(MockMvcRequestBuilders.delete("/defendantmeansassessment/"+DEFENDANT_ASSESSMENT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteDefendantAssessment_BadRequestError_invalidID() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/defendantmeansassessment/expectBadRequestError"))
                .andExpect(status().is4xxClientError());
    }

}