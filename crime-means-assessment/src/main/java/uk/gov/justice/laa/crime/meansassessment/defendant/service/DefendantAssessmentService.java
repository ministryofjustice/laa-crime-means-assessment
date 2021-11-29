package uk.gov.justice.laa.crime.meansassessment.defendant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.repository.DefendantAssessmentRepository;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class DefendantAssessmentService {

    private final DefendantAssessmentRepository defendantAssessmentRepository;

    public DefendantAssessmentEntity findById(String defendantAssessmentId) {
        return defendantAssessmentRepository.findById(defendantAssessmentId).orElse(null);
    }

    public DefendantAssessmentEntity save(DefendantAssessmentEntity defendantAssessmentEntity){
        if(! StringUtils.hasLength(defendantAssessmentEntity.getId() )) {
            defendantAssessmentEntity.setId(UUID.randomUUID().toString());
        }
        return defendantAssessmentRepository.save(defendantAssessmentEntity);
    }

    public DefendantAssessmentEntity update(DefendantAssessmentEntity defendantAssessmentEntity){
        return defendantAssessmentRepository.save(defendantAssessmentEntity);
    }

    public String deleteById(String defendantAssessmentId){
        String responseMessage = null;
        var defendantAssessmentEntity = new DefendantAssessmentEntity();
        defendantAssessmentEntity.setId(defendantAssessmentId);
        defendantAssessmentRepository.delete(defendantAssessmentEntity);

        return new StringBuilder("Successfully defendant assessmentId with id: ").append(defendantAssessmentId).toString();
    }
}
