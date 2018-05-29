package com.bookmarks.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Leonid on 18/5/23.
 */
public class Listener {
    //private final CountDownLatch latch1 = new CountDownLatch(1);

    //@KafkaListener(id = "leo1", topicPartitions={@TopicPartition(topic = "leonid4", partitions = "0")})
    public void listen1(String foo) {
        System.out.println("===============> 进入监听 Listener 2:" + foo);
        //this.latch1.countDown();
    }

}
