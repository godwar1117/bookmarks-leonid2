package com.bookmarks.web;

import com.bookmarks.config.Listener;
import com.bookmarks.distributed.DistributedLock;
import com.bookmarks.repository.CityRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RequestMapping("/test1")
    public Object index(String data) {
        //template.send("leonid2", 0, data);
        //template.send("leonid4",1, 1, "leonid is good father");
        //template.send("leonid4", 0,0, "leonid love him son");
        //template.flush();
        //template.
        //assertTrue(this.listener.latch1.await(10, TimeUnit.SECONDS));
        LockTread lockTread = new LockTread(redisTemplate);
        Thread t1 = new Thread(lockTread,"t1");
        Thread t2 = new Thread(lockTread,"t2");
        t1.start();
        t2.start();

        return "ok";
    }


    //@KafkaListener(id = "leo2",topicPartitions={@TopicPartition(topic = "leonid4", partitions = "1")})
    public void listen(ConsumerRecord<?,?> consumerRecord) {
        System.out.println("===============> 进入监听 Listener 1:" + consumerRecord.value());
    }


}

class LockTread implements Runnable {
    // 构造方法中 设置为true 就是公平锁 不设置就是非公平锁
    final DistributedLock lock;

    private RedisTemplate<String, String> redisTemplate;

    public LockTread(RedisTemplate<String, String> redisTemplate) {
        lock = new DistributedLock(redisTemplate);
    }

    @Override
    public void run() {
        try {
            if (lock.tryLock("test",1, TimeUnit.SECONDS)) {
                try {
                    //lock.lock();
                    Thread.sleep(5000);
                    System.out.println(Thread.currentThread().getName() + " ＋业务处理"); //任务处理
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock(); //释放锁
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " -获取锁失败");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
