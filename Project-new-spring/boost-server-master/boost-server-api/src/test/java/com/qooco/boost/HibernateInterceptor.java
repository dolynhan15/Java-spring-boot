package com.qooco.boost;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

public class HibernateInterceptor extends EmptyInterceptor {
    private static boolean transactionCompleted;

    public static boolean unsetTransactionCompleted() {
        var result = transactionCompleted;
        transactionCompleted = false;
        return result;
    }

    public void afterTransactionCompletion(Transaction tx) {
        transactionCompleted = true;
    }
}
