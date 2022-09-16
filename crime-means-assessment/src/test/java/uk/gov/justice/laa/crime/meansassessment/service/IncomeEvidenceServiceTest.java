package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.IncomeEvidenceRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class IncomeEvidenceServiceTest {

    @InjectMocks
    private IncomeEvidenceService incomeEvidenceService;

    @Mock
    private IncomeEvidenceRepository incomeEvidenceRepository;

    @Test
    public void testIncomeEvidenceService_whenGetIncomeEvidenceByIdInvoked_shouldSuccess() {
        incomeEvidenceService.getIncomeEvidenceById("NINO");
        verify(incomeEvidenceRepository, times(1)).findById(any());
    }

}