package org.wolkenproject.core;

import java.util.*;

public class TransactionPool {
    private Map<byte[], TransactionI> transactions;

    public TransactionPool() {
        transactions = new HashMap<>();
    }

    public boolean contains(byte[] txid) {
        return transactions.containsKey(txid);
    }

    public Set<byte[]> getNonDuplicateTransactions(Set<byte[]> list) {
        Set<byte[]> result = new HashSet<>();

        for (byte[] txid : list) {
            if (!contains(txid)) {
                result.add(txid);
            }
        }

        return result;
    }

    public TransactionI getTransaction(byte[] txid) {
        return transactions.get(txid);
    }
}