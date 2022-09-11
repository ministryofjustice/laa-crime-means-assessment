package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class NewWorkReasonTest {

    @Test
    public void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(NewWorkReason.getFrom("FMA")).isEqualTo(NewWorkReason.FMA);
    }

    @Test
    public void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(NewWorkReason.getFrom(null)).isNull();
    }

    @Test
    public void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(
                () -> NewWorkReason.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
