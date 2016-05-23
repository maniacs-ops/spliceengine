/*

   Derby - Class com.splicemachine.db.impl.tools.ij.ijWarningResult

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package com.splicemachine.db.impl.tools.ij;

import java.sql.SQLWarning;

/**
 * This is an impl for just returning warnings from
 * JDBC objects we don't want the caller to touch.
 * They are already cleared from the underlying
 * objects, doing clearSQLWarnings here is redundant.
 *
 */
class ijUnsupportedCommandResult extends ijResultImpl {

	String command;
	SQLWarning warn;
	
	ijUnsupportedCommandResult(String c) {
		command = c;
	}

	public boolean isUnsupportedCommand() { return true; }
	public String toString() { return "\'" + command + "\' is not supported."; }
	public SQLWarning getSQLWarnings() { return warn; }
	public void clearSQLWarnings() { warn = null; }
}
