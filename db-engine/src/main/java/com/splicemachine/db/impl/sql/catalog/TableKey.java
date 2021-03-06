/*
 * Apache Derby is a subproject of the Apache DB project, and is licensed under
 * the Apache License, Version 2.0 (the "License"); you may not use these files
 * except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Splice Machine, Inc. has modified this file.
 *
 * All Splice Machine modifications are Copyright 2012 - 2016 Splice Machine, Inc.,
 * and are licensed to you under the License; you may not use this file except in
 * compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package com.splicemachine.db.impl.sql.catalog;

import com.splicemachine.db.catalog.UUID;

/**
 * A TableKey represents a immutable unique identifier for a SQL object.
 * It has a schemaid and a name	. 
 *
 */

public final class TableKey
{
	private final String	tableName;
	private final UUID	schemaId;


	/**
	 * Constructor for when you have both the table and schema names.
	 *
	 * @param schemaUUID		The UUID of the schema being referecned
	 * @param tableName		The name of the table being referenced	 
	 */
	public TableKey(UUID schemaUUID, String tableName)
	{
		this.tableName = tableName;
		this.schemaId = schemaUUID;
	}

	/**
	 * Get the table name (without the schema name).
	 *
	 * @return Table name as a String
	 */

	String getTableName()
	{
		return tableName;
	}

	/**
	 * Get the schema id.
	 *
	 * @return Schema id as a String
	 */

	UUID getSchemaId()
	{
		return schemaId;
	}

	/**
	 * 2 TableKeys are equal if their both their schemaIds and tableNames are
	 * equal.
	 *
	 * @param otherTableKey	The other TableKey, as Object.
	 *
	 * @return boolean		Whether or not the 2 TableKey are equal.
	 */
	public boolean equals(Object otherTableKey)
	{
		if (otherTableKey instanceof TableKey) {

			TableKey otk = (TableKey) otherTableKey;
			if (tableName.equals(otk.tableName) && schemaId.equals(otk.schemaId))
				return true;
		}
		return false;
	}

	public int hashCode()
	{
		return tableName.hashCode();
	}

    @Override
    public String toString() {
        return String.format("TableKey{schemaID=%s,tableName=%s}",schemaId,tableName);
    }
}
