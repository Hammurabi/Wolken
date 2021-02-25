package org.wokenproject.core;

import org.wokenproject.utils.Utils;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class TransactionPool {
    private Queue<TransactionI> transactions;
    private Set<byte[]>         txids;

    public TransactionPool()
    {
        transactions    = new PriorityQueue<>();
        txids           = new HashSet<>();
    }

    public boolean contains(byte[] txid)
    {
        return txids.contains(txid);
    }

    public Set<byte[]> getNonDuplicateTransactions(Set<byte[]> list) {
        Set<byte[]> result = new HashSet<>();

        for (byte[] txid : list)
        {
            if (!contains(txid))
            {
                result.add(txid);
            }
        }

        return result;
    }

    public TransactionI getTransaction(byte[] txid) {
        if (txids.contains(txid))
        {
            for (TransactionI transaction : transactions)
            {
                if (Utils.equals(transaction.getTransactionID(), txid))
                {
                    return transaction;
                }
            }
        }

        return null;
    }
}
