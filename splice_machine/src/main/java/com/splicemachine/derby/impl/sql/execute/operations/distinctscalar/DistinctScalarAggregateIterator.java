package com.splicemachine.derby.impl.sql.execute.operations.distinctscalar;

import com.splicemachine.constants.SpliceConstants;
import com.splicemachine.derby.iapi.sql.execute.SpliceRuntimeContext;
import com.splicemachine.derby.impl.sql.execute.operations.SpliceBaseOperation;
import com.splicemachine.derby.impl.sql.execute.operations.framework.AbstractStandardIterator;
import com.splicemachine.derby.impl.sql.execute.operations.framework.GroupedRow;
import com.splicemachine.derby.utils.StandardIterator;
import com.splicemachine.derby.utils.marshall.KeyEncoder;
import com.splicemachine.pipeline.exception.Exceptions;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRow;

import java.io.IOException;

/**
 * @author Scott Fines
 * Created on: 11/1/13
 */
public class DistinctScalarAggregateIterator extends AbstractStandardIterator {
    private final DistinctAggregateBuffer buffer;
//    private MultiFieldEncoder keyEncoder;
    private KeyEncoder keyHasher;
    private boolean completed;
//    private int[] keyColumns;
		private int rowsRead;

		public DistinctScalarAggregateIterator(DistinctAggregateBuffer buffer,
																					 StandardIterator<ExecRow> source, KeyEncoder keyHasher) {
    	super(source);
        this.buffer = buffer;
        this.keyHasher = keyHasher;
    }

    public GroupedRow next(SpliceRuntimeContext spliceRuntimeContext) throws StandardException, IOException {
    	if (!completed) {
	        boolean shouldContinue;
	        GroupedRow toReturn = null;
	        do{
							SpliceBaseOperation.checkInterrupt(rowsRead, SpliceConstants.interruptLoopCheck);
	            ExecRow nextRow = source.next(spliceRuntimeContext);
	            shouldContinue = nextRow!=null;
	            if(!shouldContinue)
	                continue; //iterator exhausted, break from the loop
	            toReturn = buffer.add(getGroupingKey(nextRow),nextRow);;
	            shouldContinue = toReturn==null;
							rowsRead++;
	        }while(shouldContinue);
	
	        if(toReturn!=null)
	            return toReturn;
	        completed=true;
    	}
            if(buffer.size()>0)
                return buffer.getFinalizedRow();
            return null;
    }

		public int getRowsRead(){
			return rowsRead;
		}

    private byte[] getGroupingKey(ExecRow row) throws StandardException {
//        if(keyEncoder==null){
//            keyEncoder = MultiFieldEncoder.create(SpliceDriver.getKryoPool(),row.nColumns());
//        }
//        keyEncoder.reset();
				try {
						return keyHasher.getKey(row);
				} catch (IOException e) {
						throw Exceptions.parseException(e);
				}
//        ((KeyMarshall)keyHasher).encodeKey(row.getRowArray(), keyColumns, null, null, keyEncoder);
//        return keyEncoder.build();
    }
}