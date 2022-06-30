package uk.gov.justice.laa.crime.meansassessment.service;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.justice.laa.crime.meansassessment.model.PostProcessing;
import uk.gov.justice.laa.crime.meansassessment.model.UserSession;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostProcessingMsgPublisherServiceTest {


    @InjectMocks
    private PostProcessingMsgPublisherService postProcessingMsgPublisherService;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private Gson gson;

    @Test
    public void givenPPMessageIsAvailable_whenMsgPublisherIsInvoked_thenMessageIsPublished() {
        //given
        PostProcessing postProcessing = PostProcessing
                .builder()
                .repId(1234)
                .laaTransactionId("test")
                .user(UserSession
                        .builder()
                        .username("test-s")
                        .id("test-session-id")
                        .build())
                .build();
        when(gson.toJson(postProcessing, PostProcessing.class)).thenReturn("Message");
        ReflectionTestUtils.setField(postProcessingMsgPublisherService, "postProcessingQueue", "Test Queue");

        //when
        postProcessingMsgPublisherService.publishMessage(postProcessing);

        //then
        verify(jmsTemplate, times(1)).convertAndSend("Test Queue", "Message");
    }
}
