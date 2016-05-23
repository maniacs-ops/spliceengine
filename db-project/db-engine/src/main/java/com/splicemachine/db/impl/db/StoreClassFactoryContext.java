/*

   Derby - Class com.splicemachine.db.impl.db.StoreClassFactoryContext

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to you under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package com.splicemachine.db.impl.db;

import com.splicemachine.db.iapi.services.loader.ClassFactoryContext;
import com.splicemachine.db.iapi.services.loader.ClassFactory;
import com.splicemachine.db.iapi.services.loader.JarReader;
import com.splicemachine.db.iapi.services.locks.CompatibilitySpace;
import com.splicemachine.db.iapi.services.property.PersistentSet;
import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.store.access.AccessFactory;
import com.splicemachine.db.iapi.services.context.ContextManager;

/**
*/
final class StoreClassFactoryContext extends ClassFactoryContext {

	private final AccessFactory store;
	private final JarReader	jarReader;

	StoreClassFactoryContext(ContextManager cm, ClassFactory cf, AccessFactory store, JarReader jarReader) {
		super(cm, cf);
		this.store = store;
		this.jarReader = jarReader;
	}

	public CompatibilitySpace getLockSpace() throws StandardException {
		if (store == null)
			return null;
		return store.getTransaction(getContextManager()).getLockSpace();
	}

	public PersistentSet getPersistentSet() throws StandardException {
		if (store == null)
			return null;
		return store.getTransaction(getContextManager());
	}
	public JarReader getJarReader() {

		return jarReader;
	}
}

