/*

   Derby - Class com.splicemachine.db.iapi.error.DerbySQLException

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
package com.splicemachine.db.iapi.error;

/**
 * DerbySQLException should be implemented by Derby's SQLException
 * sub classes to allow the exception handling mechanism to distinguish between
 * ordinary SQLExceptions and Derby generated ones. 
 * @see com.splicemachine.db.impl.jdbc.EmbedSQLException
 */
public interface DerbySQLException {
	
	/**
	 * Returns true if this instance of DerbySQLException wraps
     * a StandardException object.
	 * @return true if this exception wraps a StandardException object
	 */
    public boolean isSimpleWrapper();

}
