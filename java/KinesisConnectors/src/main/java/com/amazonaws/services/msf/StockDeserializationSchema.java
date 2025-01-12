package com.amazonaws.services.msf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class StockDeserializationSchema implements DeserializationSchema<String> {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(StockDeserializationSchema.class);
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    		.setDateFormat(df)
    		.registerModule(new JavaTimeModule());
    
    @Override
    public String deserialize(byte[] message) throws IOException {
        // byte[] を String に変換
        String jsonString = new String(message, StandardCharsets.UTF_8);
        LOGGER.info("jsonString: {}", jsonString);
        
        // JSON文字列をJsonNode にマッピング
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        LOGGER.info("jsonNode: {}", jsonNode);
        
        // JSON文字列をStock にマッピング
        Stock stock = objectMapper.readValue(jsonString, Stock.class);
        LOGGER.info("stock: {}", stock);
        
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