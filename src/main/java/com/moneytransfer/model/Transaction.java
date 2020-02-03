package com.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Transaction {

    @JsonProperty(required = true)
    private long amount;

    @JsonProperty(required = true)
    private String fromClientName;

    @JsonProperty(required = true)
    private String toClientName;

    public Transaction() {
    }

    public Transaction(long amount, String fromClientName, String toClientName) {
        this.amount = amount;
        this.fromClientName = fromClientName;
        this.toClientName = toClientName;
    }

    public long getAmount() {
        return amount;
    }

    public String getFromClientName() {
        return fromClientName;
    }

    public String getToClientName() {
        return toClientName;
    }

}
