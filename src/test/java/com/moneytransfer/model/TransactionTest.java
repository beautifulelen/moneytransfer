package com.moneytransfer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransactionTest {
    private String fromClientName = "client1";
    private String toClientName = "client2";

    private Transaction transaction;

    @Before
    public void before() {
        transaction = new Transaction(10, fromClientName, toClientName);
    }

    @Test
    public void testGetAmount() {
        assertEquals(transaction.getAmount(), 10);
    }

    @Test
    public void testGetToClientName() {
        assertEquals(transaction.getToClientName(), toClientName);
    }

    @Test
    public void testGetFromClientName() {
        assertEquals(transaction.getFromClientName(), fromClientName);
    }
}
