/*

   Derby - Class com.splicemachine.db.client.am.stmtcache.StatementKey

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

package com.splicemachine.db.client.am.stmtcache;

import java.sql.ResultSet;
import java.sql.Statement;
import com.splicemachine.db.shared.common.sanity.SanityManager;

/**
 * A key representing a <code>java.sql.PreparedStatement</code> or a
 * <code>java.sql.CallableStatement</code>.
 * <p>
 * The key takes a number of statement related attributes into account, and is
 * used to insert and look up cached statement objects in the JDBC statement
 * cache.
 * <p>
 * Key instances are created by a statement key factory.
 *
 * @see StatementKeyFactory
 */
//@Immutable
public class StatementKey {

    /** Tells if the key represents a <code>CallableStatement</code>. */
    private final boolean isCallableStatement;
    /** The SQL query of the statement. */
    private final String sql;
    /** The compilation schema for the statement. */
    private final String schema;
    /** The result set type for the statement. */
    private final int type;
    /** The result set concurrency for the statement. */
    private final int concurrency;
    /** Result set holdability for the statement. */
    private final int holdability;
    /** Tells if the associated statement returns auto-generated keys. */
    private final int autogeneratedKeys;

    /**
     * Creates a statement key with all the common properties.
     *
     * @param isCallableStatement <code>true</code> is this is a key for a
     *      <code>java.sql.CallableStatement</code>
     * @param sql SQL query string
     * @param schema compilation schema
     * @param rsType result set type
     * @param rsConcurrency result set concurrency
     * @param rsHoldability result set holdability
     * @param autogeneratedKeys if auto-generated keys are returned
     *
     * @throws IllegalArgumentException if {@code schema} is {@code null}
     */
    StatementKey(boolean isCallableStatement, String sql, String schema,
            int rsType, int rsConcurrency, int rsHoldability,
            int autogeneratedKeys) {
        if (schema == null) {
            // Not localized (yet), because this should never reach the user.
            throw new IllegalArgumentException("schema is <null>");
        }
        this.isCallableStatement = isCallableStatement;
        this.sql = sql;
        this.schema = schema;
        this.type = rsType;
        this.concurrency = rsConcurrency;
        this.holdability = rsHoldability;
        this.autogeneratedKeys = autogeneratedKeys;
        // In sane builds, make sure valid JDBC values are passed.
        if (SanityManager.DEBUG) {
            SanityManager.ASSERT(
                    rsType == ResultSet.TYPE_FORWARD_ONLY ||
                    rsType == ResultSet.TYPE_SCROLL_INSENSITIVE ||
                    rsType == ResultSet.TYPE_SCROLL_SENSITIVE,
                    "Invalid result set type: " + rsType);
            SanityManager.ASSERT(
                    rsConcurrency == ResultSet.CONCUR_READ_ONLY ||
                    rsConcurrency == ResultSet.CONCUR_UPDATABLE,
                    "Invalid result set concurrency: " + rsConcurrency);
            SanityManager.ASSERT(
                    rsHoldability == ResultSet.HOLD_CURSORS_OVER_COMMIT ||
                    rsHoldability == ResultSet.CLOSE_CURSORS_AT_COMMIT,
                    "Invalid result set holdability: " + rsHoldability);
            SanityManager.ASSERT(
                    autogeneratedKeys == Statement.NO_GENERATED_KEYS ||
                    autogeneratedKeys == Statement.RETURN_GENERATED_KEYS,
                    "Invalid autogenerated key value: " + autogeneratedKeys);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof StatementKey)) {
            return false;
        }
        final StatementKey other = (StatementKey)obj;
        if (this.holdability != other.holdability) {
            return false;
        }
        if (this.autogeneratedKeys != other.autogeneratedKeys) {
            return false;
        }
        if (this.isCallableStatement != other.isCallableStatement) {
            return false;
        }
        if (!this.schema.equals(other.schema)) {
            return false;
        }
        if (this.sql == null && other.sql != null) {
            return false;
        }
        if (!this.sql.equals(other.sql)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.concurrency != other.concurrency) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.isCallableStatement ? 1 : 0);
        hash = 47 * hash + (this.sql == null ? 3 : this.sql.hashCode());
        hash = 47 * hash + this.schema.hashCode();
        hash = 47 * hash + this.type;
        hash = 47 * hash + this.concurrency;
        hash = 47 * hash + this.holdability;
        hash = 47 * hash + this.autogeneratedKeys;
        return hash;
    }

    public String toString() {
        return "'" + sql + "' in '" + schema + "', rsh = " + holdability +
                ", rst = " + type + ", rsc = " + concurrency +
                ", autogenKeys = " + autogeneratedKeys +
                ", isCallableStatement = " + isCallableStatement;
    }
}
