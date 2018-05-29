package com.bookmarks.web;

import com.bookmarks.config.Listener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.Callable;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

/**
 * Created by Leonid on 18/5/11.
 */
@RestController
@RequestMapping("/test")
public class IndexController {

    @Autowired
    private Listener listener;

    @Autowired
    private KafkaTemplate<Integer, String> template;

    @RequestMapping("/test1")
    public Object index(String data) {
        template.send("leonid2", 0, data);
        //template.send("leonid4",1, 1, "leonid is good father");
        //template.send("leonid4", 0,0, "leonid love him son");
        template.flush();
        //template.
        //assertTrue(this.listener.latch1.await(10, TimeUnit.SECONDS));
        return "ok";
    }


    //@KafkaListener(id = "leo2",topicPartitions={@TopicPartition(topic = "leonid4", partitions = "1")})
    public void listen(ConsumerRecord<?,?> consumerRecord) {
        System.out.println("===============> 进入监听 Listener 1:" + consumerRecord.value());
    }


}
