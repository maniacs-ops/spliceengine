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

package com.splicemachine.derby.test.framework;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class SpliceFunctionWatcher extends TestWatcher {
	public static final String CREATE_FUNCTION = "create function ";
	private static final Logger LOG = Logger.getLogger(SpliceFunctionWatcher.class);
	protected String functionName;
	protected String schemaName;
	protected String createString;
	protected String userName;
	protected String password;
	public SpliceFunctionWatcher(String functionName,String schemaName, String createString) {
		this.functionName = functionName.toUpperCase();
		this.schemaName = schemaName.toUpperCase();
		this.createString = createString;		
	}
	public SpliceFunctionWatcher(String functionName,String schemaName, String createString, String userName, String password) {
		this(functionName, schemaName, createString);
		this.userName = userName;
		this.password = password;
	}
	
	@Override
	protected void starting(Description description) {
		LOG.trace("Starting");
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = (userName==null)?SpliceNetConnection.getConnection():SpliceNetConnection.getConnectionAs(userName, password);
			rs = connection.getMetaData().getTables(null, schemaName, functionName, null);
			if (rs.next()) {
				executeDrop(schemaName,functionName);
			}
			connection.commit();
			statement = connection.createStatement();
			statement.execute(CREATE_FUNCTION + schemaName + "." + functionName + " " + createString);
			connection.commit();
		} catch (Exception e) {
			LOG.error("Create function statement is invalid ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(statement);
			DbUtils.commitAndCloseQuietly(connection);
		}
		super.starting(description);
	}
	@Override
	protected void finished(Description description) {
		LOG.trace("finished");
		executeDrop(schemaName,functionName);
	}
	
	public static void executeDrop(String schemaName,String functionName) {
		LOG.trace("executeDrop");
		Connection connection = null;
		Statement statement = null;
		try {
			connection = SpliceNetConnection.getConnection();
			statement = connection.createStatement();
			statement.execute("drop function " + schemaName.toUpperCase() + "." + functionName.toUpperCase());
			connection.commit();
		} catch (Exception e) {
			LOG.error("error Dropping " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(statement);
			DbUtils.commitAndCloseQuietly(connection);
		}
	}

}
