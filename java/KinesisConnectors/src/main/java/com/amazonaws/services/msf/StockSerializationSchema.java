package com.amazonaws.services.msf;

import org.apache.flink.api.common.serialization.SerializationSchema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StockSerializationSchema implements SerializationSchema<String> {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String element) {
        try {
            return objectMapper.writeValueAsBytes(element);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize byte[]", e);
        }
    }
}
