package uk.gov.justice.laa.crime.meansassessment.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.service.EligibilityChecker;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EligibilityServiceFactory {

    private Map<Client, EligibilityChecker> checkers;

    @Autowired
    public EligibilityServiceFactory(Set<EligibilityChecker> checkers) {
        initCheckers(checkers);
    }

    public EligibilityChecker getChecker(Client strategyName) {
        return checkers.get(strategyName);
    }

    private void initCheckers(Set<EligibilityChecker> strategySet) {
        checkers = new HashMap<>();
        strategySet.forEach(
                strategy -> checkers.put(strategy.getCheckerByClientName(), strategy));
    }
}
