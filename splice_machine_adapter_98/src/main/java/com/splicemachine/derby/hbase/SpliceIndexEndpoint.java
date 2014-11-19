package com.splicemachine.derby.hbase;

import java.io.IOException;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.splicemachine.coprocessor.SpliceMessage;
import com.splicemachine.coprocessor.SpliceMessage.SpliceIndexService;
import com.splicemachine.pipeline.coprocessor.BatchProtocol;
import com.splicemachine.pipeline.impl.BulkWrites;
import com.splicemachine.pipeline.impl.BulkWritesResult;

public class SpliceIndexEndpoint extends SpliceIndexService implements BatchProtocol, Coprocessor, IndexEndpoint{
		SpliceBaseIndexEndpoint endpoint;
		
		@Override
		public void start(CoprocessorEnvironment env) {
				endpoint = new SpliceBaseIndexEndpoint();
				endpoint.start(env);
		}
		
		@Override
		public void stop(CoprocessorEnvironment env) {
				endpoint.stop(env);
		}

		@Override
		public byte[] bulkWrites(byte[] bulkWrites) throws IOException {
			return endpoint.bulkWrites(bulkWrites);
		}

		@Override
		public Service getService() {
			return this;
		}
	    @Override
	    public void bulkWrite(RpcController rpcController, SpliceMessage.BulkWriteRequest bulkWriteRequest, 
	    		RpcCallback<com.splicemachine.coprocessor.SpliceMessage.BulkWriteResponse> callback) {
			 SpliceMessage.BulkWriteResponse.Builder writeResponse = SpliceMessage.BulkWriteResponse.newBuilder();
		        try {
		            writeResponse.setBytes(com.google.protobuf.ByteString.copyFrom(endpoint.bulkWrites(bulkWriteRequest.getBytes().toByteArray())));
		        } catch (java.io.IOException e) {
		            org.apache.hadoop.hbase.protobuf.ResponseConverter.setControllerException(rpcController, e);
		        }
		        callback.run(writeResponse.build());
		}

		@Override
		public BulkWritesResult bulkWrite(BulkWrites bulkWrites)
				throws IOException {
			return endpoint.bulkWrite(bulkWrites);
		}

		@Override
		public SpliceBaseIndexEndpoint getBaseIndexEndpoint() {
			return endpoint;
		}
}