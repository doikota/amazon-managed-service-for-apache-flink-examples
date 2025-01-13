package com.amazonaws.services.msf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class StockSerializationSchema implements SerializationSchema<Stock> {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StockSerializationSchema.class);
	
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
    		.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    		.setDateFormat(DF)
    		.registerModule(new JavaTimeModule());

    @Override
    public byte[] serialize(Stock stock) {
    	LOGGER.debug("stock: {}", stock);
        try {
            return objectMapper.writeValueAsBytes(stock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize byte[]", e);
        }
    }
}
