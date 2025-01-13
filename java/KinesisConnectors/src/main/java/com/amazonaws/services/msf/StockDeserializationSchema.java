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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class StockDeserializationSchema implements DeserializationSchema<Stock> {
    
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StockDeserializationSchema.class);
	
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    		.setDateFormat(DF)
    		.registerModule(new JavaTimeModule());
    
    @Override
    public Stock deserialize(byte[] message) throws IOException {
        // byte[] を String に変換
        String jsonString = new String(message, StandardCharsets.UTF_8);
        LOGGER.info("jsonString: {}", jsonString);
        
        // JSON文字列をStock にマッピング
        Stock stock = objectMapper.readValue(jsonString, Stock.class);
        LOGGER.info("stock: {}", stock);
        
        return stock;
    }

    @Override
    public boolean isEndOfStream(Stock nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Stock> getProducedType() {
        return TypeInformation.of(Stock.class);
    }
}