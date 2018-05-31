package com.bookmarks.config;

import com.alibaba.fastjson.JSON;
import com.bookmarks.entity.City;
import com.bookmarks.repository.CityRepository;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leonid on 18/5/23.
 */
@EnableKafka
@Configuration
@EnableKafkaStreams
public class KafkaConfig {
    /******************** kafka stream ************************/

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "118.89.164.254:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "testStreams");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new StreamsConfig(props);
    }

//    @Bean
//    public StreamsBuilderFactoryBean myKStreamBuilder(StreamsConfig streamsConfig) {
//        return new StreamsBuilderFactoryBean(streamsConfig);
//    }

    @Bean
    @Autowired
    public KStream<Integer, String> mykStream(StreamsBuilder kStreamBuilder, CityRepository cityRepository) {
        KStream<Integer, String> stream = kStreamBuilder.stream("leonid2");
        // Fluent KStream API
        stream
                .mapValues((e) -> {
                    City city = new City();
                    city.setName(e);
                    city.setState("Q");
                    city = cityRepository.save(city);
                    return JSON.toJSONString(city);
                })
                .to("leonid3");
        System.out.println("进入leonid2");
        stream.print();
        return stream;
}

    @Bean
    public KStream<Integer, String> mykStream2(StreamsBuilder kStreamBuilder) {
        KStream<Integer, String> stream = kStreamBuilder.stream("leonid3");
        System.out.println("进入leonid3");
        stream.print();
        return stream;
    }

    /*
    @Bean
    public KStream<Integer, String> kStream(StreamsBuilder kStreamBuilder) {
        KStream<Integer, String> stream = kStreamBuilder.stream("streamingTopic1");
        stream
                .mapValues(String::toUpperCase)
                .groupByKey()
                .reduce((String value1, String value2) -> value1 + value2,
                        TimeWindows.of(1000),
                        "windowStore")
                .toStream()
                .map((windowedId, value) -> new KeyValue<>(windowedId.key(), value))
                .filter((i, s) -> s.length() > 40)
                .to("streamingTopic2");

        stream.print();

        return stream;
    }
    */

    /******************** kafka stream ************************/

    /******************** kafka admin 创建topic ************************/

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                StringUtils.arrayToCommaDelimitedString(new String[]{"118.89.164.254:9092"}));
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic("leonid3", 10, (short) 1);
    }

    /******************** kafka admin 创建topic ************************/


    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        //118.89.164.254:9092
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "118.89.164.254:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "leo");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //props.put(ConsumerConfig.GROUP_ID_CONFIG, "Agent-Server-1.0.2");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "118.89.164.254:9092");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public Listener listener() {
        return new Listener();
    }

}
