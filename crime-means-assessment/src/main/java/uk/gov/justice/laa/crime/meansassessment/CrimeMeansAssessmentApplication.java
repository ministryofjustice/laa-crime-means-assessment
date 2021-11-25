package uk.gov.justice.laa.crime.meansassessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class CrimeMeansAssessmentApplication {

    public static void main(String[] args) {

        SpringApplication.run(CrimeMeansAssessmentApplication.class);
    }
}
