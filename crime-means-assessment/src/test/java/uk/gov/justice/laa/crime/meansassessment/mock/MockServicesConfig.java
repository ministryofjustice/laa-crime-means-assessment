package uk.gov.justice.laa.crime.meansassessment.mock;


import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@TestConfiguration
public class MockServicesConfig {
    @MockBean(name = "messageListenerContainer")
    private DefaultJmsListenerContainerFactory messageListenerContainer;

    @MockBean(name = "jmsTemplate")
    private JmsTemplate jmsTemplate;

    @MockBean(name = "sqsConnectionFactory")
    private SQSConnectionFactory sqsConnectionFactory;


}


