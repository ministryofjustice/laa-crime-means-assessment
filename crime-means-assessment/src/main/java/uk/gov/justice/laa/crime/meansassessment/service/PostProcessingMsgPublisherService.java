package uk.gov.justice.laa.crime.meansassessment.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.PostProcessing;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostProcessingMsgPublisherService {

    private final JmsTemplate defaultJmsTemplate;
    private final Gson gson;

    @Value("${aws.queue.post-processing-queue}")
    private String postProcessingQueue;

    public void publishMessage(PostProcessing messageBody) {

        String message = gson.toJson(messageBody, PostProcessing.class);
        defaultJmsTemplate.convertAndSend(postProcessingQueue, message);


        log.info("Post processing request is published MAAT ID: {}", messageBody.getRepId());


    }
}
