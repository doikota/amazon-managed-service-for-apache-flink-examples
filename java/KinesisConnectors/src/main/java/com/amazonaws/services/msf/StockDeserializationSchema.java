package com.amazonaws.services.msf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StockDeserializationSchema implements DeserializationSchema<String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String deserialize(byte[] message) throws IOException {
        // byte[] を String に変換
        String jsonString = new String(message, StandardCharsets.UTF_8);
        // 変換した JSON 文字列を String にマッピング
//        return (String) objectMapper.readTree(jsonString);
        return jsonString;
    }

    @Override
    public boolean isEndOfStream(String nextElement) {
        return false;
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }
}