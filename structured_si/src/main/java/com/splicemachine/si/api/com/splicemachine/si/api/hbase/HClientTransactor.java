package com.splicemachine.si.api.com.splicemachine.si.api.hbase;

import com.splicemachine.si.api.TransactionId;
import com.splicemachine.si.api.TransactionStoreStatus;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;

import java.io.IOException;

public interface HClientTransactor {
    TransactionId transactionIdFromString(String transactionId);
    TransactionId transactionIdFromGet(Get get);
    TransactionId transactionIdFromScan(Scan scan);
    TransactionId transactionIdFromPut(Put put);

    void initializeGet(String transactionId, Get get) throws IOException;
    void initializeGet(String transactionId, Get get, boolean includeSIColumn) throws IOException;
    void initializeScan(String transactionId, Scan scan);
    void initializeScan(String transactionId, Scan scan, boolean includeSIColumn, boolean includeUncommittedAsOfStart);
    void initializePut(String transactionId, Put put);

    Put createDeletePut(TransactionId transactionId, Object rowKey);
    boolean isDeletePut(Mutation put);

    TransactionStoreStatus getTransactionStoreStatus();
}
