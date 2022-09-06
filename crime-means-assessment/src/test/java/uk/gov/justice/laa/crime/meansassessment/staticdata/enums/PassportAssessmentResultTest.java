package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PassportAssessmentResultTest {

    @Test
    public void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(PassportAssessmentResult.getFrom("PASS")).isEqualTo(PassportAssessmentResult.PASS);
    }

    @Test
    public void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(PassportAssessmentResult.getFrom(null)).isNull();
    }

    @Test
    public void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(
                () -> PassportAssessmentResult.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
