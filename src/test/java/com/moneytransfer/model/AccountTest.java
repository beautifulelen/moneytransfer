package com.moneytransfer.model;

import org.junit.Before;
import org.junit.Test;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class AccountTest {
    private String clientName = "Test";
    private long balance = 100;
    private Account account;

    @Before
    public void before() {
        account = new Account(clientName, balance);
    }

    @Test
    public void testEqual() {
        Account theSameAccount = new Account(clientName, 100);
        assertTrue(account.equals(theSameAccount));
    }

    @Test
    public void testNotEqual() {
        Account differentClientAccount = new Account("Test2", 100);
        assertFalse(account.equals(differentClientAccount));

        Account differentBalanceAccount = new Account(clientName, 200);
        assertFalse(account.equals(differentBalanceAccount));
    }

    @Test
    public void testAddAmount() {
        account.addAmount(10);
        assertTrue(account.getBalance() == 110);
    }


    @Test
    public void testReduceAmount() {
        account.reduceAmount(10);
        assertTrue(account.getBalance() == 90);
    }


    @Test
    public void testGetClientName() {
       assertEquals(account.getClientName(), clientName);
    }

    @Test
    public void testGetBalance() {
        assertTrue(account.getBalance() == 100);
    }
}
