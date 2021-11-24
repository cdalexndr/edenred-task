package com.edenred.data.util;

import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DbUtils {

    public static boolean isInsideTransaction() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }
}
