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
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.formats.json.JsonSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.fasterxml.jackson.databind.JsonNode;

public class StreamingJob {
    private static final Logger LOG = LoggerFactory.getLogger(StreamingJob.class);

    // Name of the local JSON resource with the application properties in the same format as they are received from the Amazon Managed Service for Apache Flink runtime
    private static final String LOCAL_APPLICATION_PROPERTIES_RESOURCE = "flink-application-properties-dev.json";

    private static boolean isLocal(StreamExecutionEnvironment env) {
        return env instanceof LocalStreamEnvironment;
    }

    /**
     * Load application properties from Amazon Managed Service for Apache Flink runtime or from a local resource, when the environment is local
     */
    private static Map<String, Properties> loadApplicationProperties(StreamExecutionEnvironment env) throws IOException {
        if (isLocal(env)) {
            LOG.info("Loading application properties from '{}'", LOCAL_APPLICATION_PROPERTIES_RESOURCE);
            return KinesisAnalyticsRuntime.getApplicationProperties(
                    StreamingJob.class.getClassLoader()
                            .getResource(LOCAL_APPLICATION_PROPERTIES_RESOURCE).getPath());
        } else {
            LOG.info("Loading application properties from Amazon Managed Service for Apache Flink");
            return KinesisAnalyticsRuntime.getApplicationProperties();
        }
    }

    private static KinesisStreamsSource<JsonNode> createKinesisSource(Properties inputProperties) {
        // Properties を Map<String, String> に変換
        Map<String, String> propertiesMap = inputProperties.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> String.valueOf(e.getKey()),  // Key を String に変換
                    e -> String.valueOf(e.getValue()) // Value を String に変換
                ));
        return KinesisStreamsSource.<JsonNode>builder()
                .setStreamArn(inputProperties.getProperty("stream.arn"))
                .setSourceConfig(Configuration.fromMap(propertiesMap))
                .setDeserializationSchema(new JsonDeserializationSchema<>(JsonNode.class))
                .build();
    }
    
    private static KinesisStreamsSink<JsonNode> createKinesisSink(Properties outputProperties) {
        return KinesisStreamsSink.<JsonNode>builder()
        		.setStreamName(outputProperties.getProperty("stream.name"))
                .setKinesisClientProperties(outputProperties)
                .setSerializationSchema(new JsonSerializationSchema<>())
                .setPartitionKeyGenerator(element -> String.valueOf(element.hashCode()))
                .build();
    }

    
    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final Map<String, Properties> applicationProperties = loadApplicationProperties(env);
        
        LOG.warn("Application properties: {}", applicationProperties);

        // Kinesis source 
        KinesisStreamsSource<JsonNode> source = createKinesisSource(applicationProperties.get("InputStreamGroup"));
        LOG.warn("source: " + source);
        DataStream<JsonNode> input = env.fromSource(source,
                WatermarkStrategy.noWatermarks(),
                "Kinesis source",
                TypeInformation.of(JsonNode.class));
        LOG.warn("input: " + input);

        // Kinesis sink
        KinesisStreamsSink<JsonNode> sink = createKinesisSink(applicationProperties.get("OutputStreamGroup"));
        LOG.warn("sink: " + sink.toString());
        DataStreamSink<JsonNode> aaa = input.sinkTo(sink);
        LOG.warn("aaa: " + aaa);

        JobExecutionResult result = env.execute("Flink Kinesis Source and Sink examples");
        LOG.warn("result: " + result);
    }
}
