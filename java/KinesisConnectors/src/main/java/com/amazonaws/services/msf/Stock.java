package com.amazonaws.services.msf;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Captures the key elements of a stock trade, such as the ticker symbol, price,
 * number of shares, the type of the trade (buy or sell), and an id uniquely identifying
 * the trade.
 */
public class Stock {

    public Stock() {
    }

//    public Stock(LocalDateTime eventTime, String ticker, double price) {
    public Stock(String eventTime, String ticker, double price) {
    	this.eventTime = eventTime;
        this.ticker = ticker;
        this.price = price;
    }

//	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
//    private LocalDateTime eventTime;
	private String eventTime;
    
	private String ticker;
	
    private double price;

//    public LocalDateTime getEventTime() {
    public String getEventTime() {
		return eventTime;
	}

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }

//	public void setEventTime(LocalDateTime eventTime) {
    public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public void setPrice(double price) {
		this.price = price;
	}

    @Override
    public String toString() {
        return String.format("eventTime:%s, ticker:%s, price:%.02f", eventTime, ticker, price);
    }

}
