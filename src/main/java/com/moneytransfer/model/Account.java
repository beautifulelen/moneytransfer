package com.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Account {

    @JsonProperty(required = true)
    private String clientName;

    @JsonProperty(required = true)
    private long balance;

    public Account() {
    }

    public Account(String clientName, long balance) {
        this.clientName = clientName;
        this.balance = balance;
    }

    public long getBalance() {
        return balance;
    }

    public String getClientName() {
        return clientName;
    }

    public void addAmount(long amount) {
        this.balance += amount;
    }

    public void reduceAmount(long amount) {
        this.balance -= amount;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Account account = (Account) object;
        if (!clientName.equals(account.clientName)) {
            return false;
        }
        return  (balance == account.balance);
    }
}
