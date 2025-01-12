package com.amazonaws.services.msf;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kinesis.sink.KinesisStreamsSink;
import org.apache.flink.connector.kinesis.source.KinesisStreamsSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StreamingJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingJob.class);

    // Name of the local JSON resource with the application properties in the same format 
    // as they are received from the Amazon Managed Service for Apache Flink runtime
	private static final String LOCAL_APPLICATION_PROPERTIES_RESOURCE = "flink-application-properties-dev.json";

    private static boolean isLocal(StreamExecutionEnvironment env) {
        return env instanceof LocalStreamEnvironment;
    }

    /**
     * Load application properties from Amazon Managed Service for Apache Flink runtime or from a local resource, when the environment is local
     */
    private static Map<String, Properties> loadApplicationProperties(StreamExecutionEnvironment env) throws IOException {
        if (isLocal(env)) {
            LOGGER.info("Loading application properties from '{}'", LOCAL_APPLICATION_PROPERTIES_RESOURCE);
            return KinesisAnalyticsRuntime.getApplicationProperties(
            	StreamingJob.class.getClassLoader()
                    .getResource(LOCAL_APPLICATION_PROPERTIES_RESOURCE).getPath());
        } else {
            LOGGER.info("Loading application properties from Amazon Managed Service for Apache Flink");
            return KinesisAnalyticsRuntime.getApplicationProperties();
        }
    }

    private static KinesisStreamsSource<String> createKinesisSource(Properties inputProperties) {
        // Properties を Map<String, String> に変換
        Map<String, String> propertiesMap = inputProperties.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> String.valueOf(e.getKey()),  // Key を String に変換
                    e -> String.valueOf(e.getValue()) // Value を String に変換
                ));
        return KinesisStreamsSource.<String>builder()
                .setStreamArn(inputProperties.getProperty("stream.arn"))
                .setSourceConfig(Configuration.fromMap(propertiesMap))
                .setDeserializationSchema(new StockDeserializationSchema())
                .build();
    }
    
    private static KinesisStreamsSink<String> createKinesisSink(Properties outputProperties) {
        return KinesisStreamsSink.<String>builder()
                .setStreamArn(outputProperties.getProperty("stream.arn"))
                .setKinesisClientProperties(outputProperties)
                .setSerializationSchema(new StockSerializationSchema())
                .setPartitionKeyGenerator(element -> String.valueOf(element.hashCode()))
                .build();
    }

    
    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final Map<String, Properties> applicationProperties = loadApplicationProperties(env);
        
        LOGGER.info("Application properties: {}", applicationProperties);

        // Kinesis source 
        KinesisStreamsSource<String> source = createKinesisSource(applicationProperties.get("InputStreamGroup"));
        LOGGER.info("source: " + source);
        DataStream<String> input = env.fromSource(source, WatermarkStrategy.noWatermarks(),
                "Kinesis source", TypeInformation.of(String.class));
        LOGGER.info("input: " + input);

        

        // Kinesis sink
        KinesisStreamsSink<String> sink = createKinesisSink(applicationProperties.get("OutputStreamGroup"));
        LOGGER.info("sink: " + sink);
        DataStreamSink<String> aaa = input.sinkTo(sink);
        LOGGER.info("aaa: " + aaa);

        JobExecutionResult result = env.execute("Flink Kinesis Source and Sink examples");
        LOGGER.info("result: " + result);
    }
}
