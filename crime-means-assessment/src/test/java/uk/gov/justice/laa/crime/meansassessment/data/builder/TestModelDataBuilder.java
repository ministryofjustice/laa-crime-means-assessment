package uk.gov.justice.laa.crime.meansassessment.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;

@Component
public class TestModelDataBuilder {
    public static final String DEFENDANT_ASSESSMENT_ID = "484cf7b4-b910-4f28-82bd-b60c69467053";
    public static final String DEFENDANT_ASSESSMENT_UPDATED_INFO = "updated info test";

    public static DefendantAssessmentEntity getDefendantAssessmentDTO(){
        return DefendantAssessmentEntity.builder()
                .id(DEFENDANT_ASSESSMENT_ID)
                .updatedInfo(DEFENDANT_ASSESSMENT_UPDATED_INFO)
                .build();
    }
}
