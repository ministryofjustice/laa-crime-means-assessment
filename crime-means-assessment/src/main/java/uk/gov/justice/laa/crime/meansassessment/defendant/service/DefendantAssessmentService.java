package uk.gov.justice.laa.crime.meansassessment.defendant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.repository.DefendantAssessmentRepository;


@RequiredArgsConstructor
@Service
public class DefendantAssessmentService {

    private final DefendantAssessmentRepository defendantAssessmentRepository;

    public DefendantAssessmentEntity findById(String defendantAssessmentId) {
        return defendantAssessmentRepository.findById(defendantAssessmentId).orElse(null);
    }
}
