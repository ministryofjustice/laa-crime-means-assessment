package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.ReviewType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class InitAssessmentValidatorTest {

    private MeansAssessmentRequestDTO meansAssessment;
    private final InitAssessmentValidator initAssessmentValidator = new InitAssessmentValidator();

    String REFUSED_REP_ORDER_DECISION = "Refused - Ineligible";

    @BeforeEach
    void setup() {
        meansAssessment = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        meansAssessment.setReviewType(null);
    }

    @Test
    void givenReviewTypeIsNullAndRepOrderRefused_whenValidateIsInvoked_thenReturnsFalse() {
        meansAssessment.getCrownCourtOverview()
                .getCrownCourtSummary()
                .setRepOrderDecision(REFUSED_REP_ORDER_DECISION);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.FALSE);
    }

    @Test
    void givenReviewTypeIsNullAndCrownCourtOverviewIsNull_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.setCrownCourtOverview(null);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void givenReviewTypeIsNullAndCrownCourtSummaryIsNull_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.getCrownCourtOverview()
                .setCrownCourtSummary(null);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void givenReviewTypeIsNullAndRepOrderDecisionIsNull_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.getCrownCourtOverview()
                .getCrownCourtSummary()
                .setRepOrderDecision(null);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void givenReviewTypeIsNullAndEligibleRepOrderDecision_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.getCrownCourtOverview()
                .getCrownCourtSummary()
                .setRepOrderDecision("ELIGIBLE");
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void givenReviewTypeIsNotNull_whenValidateIsInvoked_thenReturnsTrue() {
        meansAssessment.setReviewType(ReviewType.NAFI);
        assertThat(initAssessmentValidator.validate(meansAssessment)).isEqualTo(Boolean.TRUE);
    }
}
