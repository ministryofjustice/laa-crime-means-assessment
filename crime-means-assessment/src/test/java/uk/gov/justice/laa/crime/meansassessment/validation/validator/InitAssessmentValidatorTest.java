package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import org.junit.Before;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.ReviewType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InitAssessmentValidatorTest {

    private ApiCreateMeansAssessmentRequest meansAssessment;
    private final InitAssessmentValidator initAssessmentValidator = new InitAssessmentValidator();

    String REFUSED_REP_ORDER_DECISION = "Refused - Ineligible";

    @Before
    public void setup() {
        meansAssessment = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        meansAssessment.setReviewType(null);
    }

    @Test
    public void givenReviewTypeIsNullAndRepOrderRefused_whenValidateIsInvoked_thenReturnsFalse() {
        meansAssessment.getCrownCourtOverview()
                .getCrownCourtSummary()
                .setRepOrderDecision(REFUSED_REP_ORDER_DECISION);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void givenReviewTypeIsNullAndCrownCourtOverviewIsNull_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.setCrownCourtOverview(null);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void givenReviewTypeIsNullAndCrownCourtSummaryIsNull_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.getCrownCourtOverview()
                .setCrownCourtSummary(null);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void givenReviewTypeIsNullAndRepOrderDecisionIsNull_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.getCrownCourtOverview()
                .getCrownCourtSummary()
                .setRepOrderDecision(null);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void givenReviewTypeIsNullAndEligibleRepOrderDecision_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.getCrownCourtOverview()
                .getCrownCourtSummary()
                .setRepOrderDecision("ELIGIBLE");
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void givenReviewTypeIsNotNull_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.setReviewType(ReviewType.NAFI);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }
}
