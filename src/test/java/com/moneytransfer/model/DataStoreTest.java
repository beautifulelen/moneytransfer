package com.moneytransfer.model;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junitx.util.PrivateAccessor;

public class DataStoreTest {

    private String fromClientName = "client1";
    private String toClientName = "client2";
    private Transaction transaction = new Transaction(10, fromClientName, toClientName);

    private Account accountClient1 = new Account(fromClientName, 100);
    private Account accountClient2 = new Account(toClientName, 100);

    @After
    public void after() throws NoSuchFieldException {
        PrivateAccessor.setField(DataStore.getInstance(), "instance", null);
    }

    @Test
    public void testDoTransaction() {
        DataStore.getInstance().createAccount(accountClient1);
        DataStore.getInstance().createAccount(accountClient2);

        DataStore.getInstance().doTransaction(transaction);
        assertEquals(accountClient1.getBalance(), 90);
        assertEquals(accountClient2.getBalance(), 110);
    }

    @Test
    public void testLock() {
        DataStore.getInstance().createAccount(accountClient1);
        assertTrue(DataStore.getInstance().lock(fromClientName));
        assertFalse(DataStore.getInstance().lock(fromClientName));
    }

    @Test
    public void testValidTransation() {
        DataStore.getInstance().createAccount(accountClient1);
        DataStore.getInstance().createAccount(accountClient2);

        assertTrue(DataStore.getInstance().isValidTransaction(transaction));
    }

    @Test
    public void testNotValidTransation() {
        DataStore.getInstance().createAccount(accountClient1);
        DataStore.getInstance().createAccount(accountClient2);

        Transaction badTransaction = new Transaction();
        assertFalse(DataStore.getInstance().isValidTransaction(badTransaction));

        badTransaction = new Transaction(110, fromClientName, toClientName);
        assertFalse(DataStore.getInstance().isValidTransaction(badTransaction));

        badTransaction = new Transaction(0, "noName1", "noName2");
        assertFalse(DataStore.getInstance().isValidTransaction(badTransaction));
    }

    @Test
    public void testUnlock() {
        DataStore.getInstance().createAccount(accountClient1);
        assertFalse(DataStore.getInstance().unlock(fromClientName));

        DataStore.getInstance().lock(fromClientName);
        assertTrue(DataStore.getInstance().unlock(fromClientName));
    }

    @Test
    public void testCreateAndGetAccount() {
        assertNull(DataStore.getInstance().getAccount(fromClientName));
        DataStore.getInstance().createAccount(accountClient1);
        assertEquals(DataStore.getInstance().getAccount(fromClientName), accountClient1);
    }

    @Test
    public void testGetTransactions() {
        DataStore.getInstance().createAccount(accountClient1);
        DataStore.getInstance().createAccount(accountClient2);

        assertEquals(DataStore.getInstance().getTransactions().size(), 0);
        DataStore.getInstance().doTransaction(transaction);
        assertEquals(DataStore.getInstance().getTransactions().size(), 1);
    }
}
