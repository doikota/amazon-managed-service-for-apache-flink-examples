package com.amazonaws.services.msf;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Captures the key elements of a stock trade, such as the ticker symbol, price,
 * number of shares, the type of the trade (buy or sell), and an id uniquely identifying
 * the trade.
 */
public class Stock {

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime eventTime;
	private String ticker;
    private double price;

    public Stock() {
    }

    public Stock(LocalDateTime eventTime, String ticker, double price) {
    	this.eventTime = eventTime;
        this.ticker = ticker;
        this.price = price;
    }

    public LocalDateTime getEventTime() {
		return eventTime;
	}

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s %s $%.02f", eventTime, ticker, price);
    }

}
