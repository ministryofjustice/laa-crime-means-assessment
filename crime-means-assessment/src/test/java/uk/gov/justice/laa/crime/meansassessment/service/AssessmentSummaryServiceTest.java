package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewStatus;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.ReviewType.ER;
import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.WorkType.*;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentSummaryServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private AssessmentSummaryService assessmentSummaryService;

    private ApiCreateMeansAssessmentResponse meansAssessmentResponse;

    @Before
    public void setup() {
        meansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(true);
        meansAssessmentResponse.setRepId(1234);
        meansAssessmentResponse.setAssessmentType(AssessmentType.INIT);
        meansAssessmentResponse.setReviewType(ER);
        meansAssessmentResponse.setInitialAssessmentDate(LocalDateTime.of(2021, 12, 20, 10, 0));
        meansAssessmentResponse.setInitResult("PASS");
    }

    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvoked_ThenAssessmentSummaryShouldBeReturnedWithInitFinAssSummary() {
        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        Assertions.assertThat(meansAssessmentResponse.getAssessmentSummary()).extracting("type", "status", "result", "assessmentDate")
                .containsExactly(tuple(Initial_Assessment, "Complete", "PASS", LocalDateTime.of(2021, 12, 20, 10, 0)));
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
        verify(maatCourtDataService).getHardshipReviewFromRepId(1234, null);
        verify(maatCourtDataService).getIOJAppealFromRepId(1234, null);
    }

    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvoked_ThenAssessmentSummaryShouldBeReturnedWithFullFinAssSummary() {
        meansAssessmentResponse.setAssessmentType(AssessmentType.FULL);
        meansAssessmentResponse.setFassFullStatus(CurrentStatus.IN_PROGRESS);

        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        Assertions.assertThat(meansAssessmentResponse.getAssessmentSummary()).extracting("type", "status", "result", "assessmentDate")
                .containsExactly(tuple(Full_Means_Test, "Incomplete", null, LocalDateTime.of(2021, 12, 20, 10, 0)));
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
        verify(maatCourtDataService).getHardshipReviewFromRepId(1234, null);
        verify(maatCourtDataService).getIOJAppealFromRepId(1234, null);
    }

    @Test
    public void givenMeansAssessmentResponseWithFullAssDate_WhenGetAssessmentSummaryIsInvoked_ThenAssessmentSummaryShouldBeReturnedWithFullFinAssSummary() {
        meansAssessmentResponse.setAssessmentType(AssessmentType.FULL);
        meansAssessmentResponse.setFullAssessmentDate(LocalDateTime.of(2022, 1, 20, 10, 0));

        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        Assertions.assertThat(meansAssessmentResponse.getAssessmentSummary()).extracting("type", "status", "result", "assessmentDate")
                .containsExactly(tuple(Full_Means_Test, null, null, LocalDateTime.of(2022, 1, 20, 10, 0)));
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
        verify(maatCourtDataService).getHardshipReviewFromRepId(1234, null);
        verify(maatCourtDataService).getIOJAppealFromRepId(1234, null);
    }

    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvoked_ThenAssessmentSummaryShouldBeReturnedWithFinAssAndPassAssSummary() {
        when(maatCourtDataService.getPassportAssessmentFromRepId(1234, null)).thenReturn(buildPassportAssessmentDTO());

        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        Assertions.assertThat(meansAssessmentResponse.getAssessmentSummary()).extracting("type")
                .containsExactlyInAnyOrder(Initial_Assessment, Passported);
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
        verify(maatCourtDataService).getHardshipReviewFromRepId(1234, null);
        verify(maatCourtDataService).getIOJAppealFromRepId(1234, null);
    }

    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvoked_ThenAssessmentSummaryShouldBeReturnedWithFinAssAndHardshipReviewSummary() {
        when(maatCourtDataService.getHardshipReviewFromRepId(1234, null)).thenReturn(buildHardshipReviewDTO());

        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        Assertions.assertThat(meansAssessmentResponse.getAssessmentSummary()).extracting("type")
                .containsExactlyInAnyOrder(Initial_Assessment, Hardship_Review_CrownCourt);
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
        verify(maatCourtDataService).getHardshipReviewFromRepId(1234, null);
        verify(maatCourtDataService).getIOJAppealFromRepId(1234, null);
    }

    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvoked_ThenAssessmentSummaryShouldBeReturnedWithFinAssAndHardshipReviewSummaryWithCourtTypeMagistrate() {
        HardshipReviewDTO hardshipReviewDTO = buildHardshipReviewDTO();
        hardshipReviewDTO.setCourtType("MAGISTRATE");
        when(maatCourtDataService.getHardshipReviewFromRepId(1234, null)).thenReturn(hardshipReviewDTO);

        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        Assertions.assertThat(meansAssessmentResponse.getAssessmentSummary()).extracting("type")
                .containsExactlyInAnyOrder(Initial_Assessment, Hardship_Review_Magistrate);
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
        verify(maatCourtDataService).getHardshipReviewFromRepId(1234, null);
        verify(maatCourtDataService).getIOJAppealFromRepId(1234, null);
    }

    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvoked_ThenAssessmentSummaryShouldBeReturnedWithFinAssAndIOJSummary() {
        when(maatCourtDataService.getIOJAppealFromRepId(1234, null)).thenReturn(buildIOJAppealDTO());

        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        Assertions.assertThat(meansAssessmentResponse.getAssessmentSummary()).extracting("type")
                .containsExactlyInAnyOrder(Initial_Assessment, IoJ_Appeal);
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
        verify(maatCourtDataService).getHardshipReviewFromRepId(1234, null);
        verify(maatCourtDataService).getIOJAppealFromRepId(1234, null);
    }

    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvoked_ThenAllAssessmentSummaryShouldBeReturned() {
        when(maatCourtDataService.getPassportAssessmentFromRepId(1234, null)).thenReturn(buildPassportAssessmentDTO());
        when(maatCourtDataService.getHardshipReviewFromRepId(1234, null)).thenReturn(buildHardshipReviewDTO());
        when(maatCourtDataService.getIOJAppealFromRepId(1234, null)).thenReturn(buildIOJAppealDTO());

        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        assertThat(meansAssessmentResponse.getAssessmentSummary().size()).isEqualTo(4);
        Assertions.assertThat(meansAssessmentResponse.getAssessmentSummary()).extracting("type")
                .containsExactlyInAnyOrder(Initial_Assessment, IoJ_Appeal, Hardship_Review_CrownCourt, Passported);
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
        verify(maatCourtDataService).getHardshipReviewFromRepId(1234, null);
        verify(maatCourtDataService).getIOJAppealFromRepId(1234, null);
    }

    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvokedAndExceptionIsThrown_ThenAssessmentSummaryIsNotSet() {
        when(maatCourtDataService.getPassportAssessmentFromRepId(1234, null)).thenReturn(new PassportAssessmentDTO());

        assessmentSummaryService.addAssessmentSummaryToMeansResponse(meansAssessmentResponse, null);

        assertThat(meansAssessmentResponse.getAssessmentSummary().size()).isEqualTo(0);
        verify(maatCourtDataService).getPassportAssessmentFromRepId(1234, null);
    }

    private PassportAssessmentDTO buildPassportAssessmentDTO() {
        return PassportAssessmentDTO.builder()
                .assessmentDate(LocalDateTime.of(2021, 12, 20, 10, 0))
                .id(178)
                .repId(1234)
                .result("FAIL")
                .pastStatus("COMPLETE")
                .rtCode(null)
                .build();
    }

    private HardshipReviewDTO buildHardshipReviewDTO() {
        return HardshipReviewDTO.builder()
                .reviewDate(LocalDateTime.of(2021, 12, 20, 10, 0))
                .id(178)
                .reviewResult("FAIL")
                .status(HardshipReviewStatus.COMPLETE)
                .build();
    }

    private IOJAppealDTO buildIOJAppealDTO() {
        return IOJAppealDTO.builder()
                .appealSetupDate(LocalDateTime.of(2021, 12, 20, 10, 0))
                .id(178)
                .decisionResult("PASS").iapsStatus("COMPLETE")
                .build();
    }

}
