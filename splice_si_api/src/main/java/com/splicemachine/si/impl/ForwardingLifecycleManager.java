/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.splicemachine.si.impl;

import com.splicemachine.si.api.txn.Txn;
import com.splicemachine.si.api.txn.TxnLifecycleManager;
import com.splicemachine.si.api.txn.TxnView;

import java.io.IOException;

/**
 * @author Scott Fines
 * Date: 6/24/14
 */
public class ForwardingLifecycleManager implements TxnLifecycleManager{
		private final TxnLifecycleManager lifecycleManager;

		public ForwardingLifecycleManager(TxnLifecycleManager lifecycleManager) {
				this.lifecycleManager = lifecycleManager;
		}

		@Override
		public Txn beginTransaction() throws IOException {
				Txn txn = lifecycleManager.beginTransaction();
				afterStart(txn);
				return txn;
		}

		@Override
		public Txn beginTransaction(byte[] destinationTable) throws IOException {
				Txn txn = lifecycleManager.beginTransaction(destinationTable);
				afterStart(txn);
				return txn;
		}

		@Override
		public Txn beginTransaction(Txn.IsolationLevel isolationLevel) throws IOException {
				Txn txn = lifecycleManager.beginTransaction(isolationLevel);
				afterStart(txn);
				return txn;
		}

		@Override
		public Txn beginTransaction(Txn.IsolationLevel isolationLevel, byte[] destinationTable) throws IOException {
				Txn txn = lifecycleManager.beginTransaction(isolationLevel, destinationTable);
				afterStart(txn);
				return txn;
		}

		@Override
		public Txn beginChildTransaction(TxnView parentTxn, byte[] destinationTable) throws IOException {
				Txn txn = lifecycleManager.beginChildTransaction(parentTxn, destinationTable);
				afterStart(txn);
				return txn;
		}

		@Override
		public Txn beginChildTransaction(TxnView parentTxn, Txn.IsolationLevel isolationLevel, byte[] destinationTable) throws IOException {
				Txn txn = lifecycleManager.beginChildTransaction(parentTxn, isolationLevel, destinationTable);
				afterStart(txn);
				return txn;
		}

		@Override
		public Txn beginChildTransaction(TxnView parentTxn, Txn.IsolationLevel isolationLevel, boolean additive, byte[] destinationTable) throws IOException {
				Txn txn = lifecycleManager.beginChildTransaction(parentTxn, isolationLevel, additive, destinationTable);
				afterStart(txn);
				return txn;
		}

		@Override
		public Txn chainTransaction(TxnView parentTxn, Txn.IsolationLevel isolationLevel, boolean additive, byte[] destinationTable, Txn txnToCommit) throws IOException {
				Txn txn = lifecycleManager.chainTransaction(parentTxn,isolationLevel, additive,destinationTable,txnToCommit);
				afterStart(txn);
				return txn;
		}

        @Override
        public void enterRestoreMode() {
            lifecycleManager.enterRestoreMode();
        }

        @Override
		public Txn elevateTransaction(Txn txn, byte[] destinationTable) throws IOException {
				Txn txn1 = lifecycleManager.elevateTransaction(txn, destinationTable);
				afterStart(txn1);
				return txn1;
		}

		@Override
		public long commit(long txnId) throws IOException {
				return lifecycleManager.commit(txnId);
		}

		@Override
		public void rollback(long txnId) throws IOException {
				lifecycleManager.rollback(txnId);
		}


		protected void afterStart(Txn txn){
				//no-op by default
		}

}
