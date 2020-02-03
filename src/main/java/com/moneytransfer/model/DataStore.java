package com.moneytransfer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    private static DataStore instance;
    private Map<String, Account> accounts = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();
    private List<Account> lockedAccounts = new ArrayList<>();

    private DataStore() {
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void doTransaction(Transaction transaction) {
        Account accountFrom = getAccount(transaction.getFromClientName());
        Account accountTo = getAccount(transaction.getToClientName());
        accountFrom.reduceAmount(transaction.getAmount());
        accountTo.addAmount(transaction.getAmount());
        transactions.add(transaction);
    }

    public boolean lock(String clientName) {
        Account account = getAccount(clientName);
        if (account == null || lockedAccounts.indexOf(account) >= 0)
            return false;
        lockedAccounts.add(account);
        return true;
    }

    public boolean isValidTransaction(Transaction transaction) {
        Account fromAccount = getAccount(transaction.getFromClientName());
        Account toAccount = getAccount(transaction.getToClientName());
        return fromAccount != null &&
                toAccount != null &&
                !fromAccount.equals(toAccount) &&
                fromAccount.getBalance() >= transaction.getAmount();
    }

    public boolean unlock(String clientName) {
        Account account = getAccount(clientName);
        if (account != null && lockedAccounts.contains(account))
            return lockedAccounts.remove(account);
        return false;
    }

    public void createAccount(Account account) {
        accounts.put(account.getClientName(), account);
    }

    public Account getAccount(String clientName) {
        return accounts.get(clientName);
    }

    public List getTransactions() {
        return transactions;
    }

}

