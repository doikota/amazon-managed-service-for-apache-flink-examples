package com.amazonaws.services.msf;

/**
 * Captures the key elements of a stock trade, such as the ticker symbol, price,
 * number of shares, the type of the trade (buy or sell), and an id uniquely identifying
 * the trade.
 */
public class Stock {

    /**
     * Represents the type of the stock trade eg buy or sell.
     */
    public enum TradeType {
        BUY,
        SELL
    }

  
    private long id;
    private TradeType tradeType;
    private long quantity;
	private String eventTime;
	private String ticker;
	private double price;

	public Stock() {
	}

	public Stock(long id, TradeType tradeType, long quantity, String eventTime, String ticker, double price) {
		this.id = id;
		this.tradeType = tradeType;
		this.quantity = quantity;
		this.eventTime = eventTime;
		this.ticker = ticker;
		this.price = price;
	}

    public long getId() {
		return id;
	}

	public TradeType getTradeType() {
		return tradeType;
	}

	public long getQuantity() {
		return quantity;
	}

	public String getEventTime() {
		return eventTime;
	}

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }

    public void setId(long id) {
		this.id = id;
	}

	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

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
        return String.format("ID %d: %s %d %s $%.02f, %s",
                id, tradeType, quantity, ticker, price, eventTime);
    }

}
