package com.splicemachine.derby.stream.iapi;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.impl.sql.execute.ValueRow;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.db.iapi.sql.Activation;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.stream.function.Partitioner;
import com.splicemachine.derby.utils.marshall.KeyHashDecoder;

import java.io.InputStream;

/**
 * Higher level constructs for getting datasets and manipulating the processing mechanisms.
 *
 * @author jleach
 */
public interface DataSetProcessor {

    <Op extends SpliceOperation, V> ScanSetBuilder<V> newScanSet(Op spliceOperation,String tableName) throws StandardException;

    <Op extends SpliceOperation, V> IndexScanSetBuilder<V> newIndexScanSet(Op spliceOperation,String tableName) throws StandardException;

    <V> DataSet<V> getEmpty();

    <V> DataSet<V> getEmpty(String name);

    /**
     * Generates a single row dataset from a value.
     */
    <V> DataSet<V> singleRowDataSet(V value);

    <V> DataSet<V> singleRowDataSet(V value, Object caller);
    
    /**
     * Creates a dataset from a provided Iterable.
     */
    <V> DataSet<V> createDataSet(Iterable<V> value);

    <V> DataSet<V> createDataSet(Iterable<V> value, String name);

    /**
     * Creates a single row PairDataSet
     */
    <K,V> PairDataSet<K, V> singleRowPairDataSet(K key, V value);

    /**
     * Creates an operation context for executing a function.
     *
     */
    <Op extends SpliceOperation> OperationContext<Op> createOperationContext(Op spliceOperation);

    /**
     * Creates an operation context based only on the supplied activation
     */
    <Op extends SpliceOperation> OperationContext<Op> createOperationContext(Activation activation);

    /**
     * Sets the job group for execution.
     */
    void setJobGroup(String jobName, String jobDescription);

    /**
     * Reads a whole text file from path.
     */
    PairDataSet<String,InputStream> readWholeTextFile(String path);

    PairDataSet<String,InputStream> readWholeTextFile(String path, SpliceOperation op);

    /**
     * Reads a text file that will be split in blocks when splittable compression algorithms are
     * utilized.
     */
    DataSet<String> readTextFile(String path);

    DataSet<String> readTextFile(String path, SpliceOperation op);
    
    /**
     * Gets an empty PairDataSet
     */
    <K,V> PairDataSet<K, V> getEmptyPair();

    /**
     * Sets the scheduler pool for execution (if appropriate)
     */
    void setSchedulerPool(String pool);

    /**
     * Sets whether failures are swallowed vs. being thrown up the stack.
     */
    void setPermissive();

    void setFailBadRecordCount(int failBadRecordCount);

    void clearBroadcastedOperation();

    /*
     * Stops the given job
     */
    void stopJobGroup(String jobName);

    Partitioner getPartitioner(DataSet<LocatedRow> dataSet, ExecRow template, int[] keyDecodingMap, boolean[] keyOrder);
}
