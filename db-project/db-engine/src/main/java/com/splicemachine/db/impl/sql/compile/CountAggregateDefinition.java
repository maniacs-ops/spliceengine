/*

   Derby - Class org.apache.derby.impl.sql.compile.CountAggregateDefinition

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

package com.splicemachine.db.impl.sql.compile;

import com.splicemachine.db.iapi.sql.compile.AggregateDefinition;
import com.splicemachine.db.iapi.types.DataTypeDescriptor;

import com.splicemachine.db.iapi.reference.ClassName;


/**
 * Defintion for the COUNT()/COUNT(*) aggregates.
 *
 */
public class CountAggregateDefinition 
		implements AggregateDefinition
{
    private boolean isWindowFunction;

    public final boolean isWindowFunction() {
        return this.isWindowFunction;
    }

    public void setWindowFunction(boolean isWindowFunction) {
        this.isWindowFunction = isWindowFunction;
    }

	/**
	 * Niladic constructor.  Does nothing.  For ease
	 * Of use, only.
	 */
	public CountAggregateDefinition() { super(); }

	/**
	 * Determines the result datatype. We can run
	 * count() on anything, and it always returns a
	 * INTEGER (java.lang.Integer).
	 *
	 * @param inputType the input type, either a user type or a java.lang object
	 *
	 * @return the output Class (null if cannot operate on
	 *	value expression of this type.
	 */
	public final DataTypeDescriptor	getAggregator(DataTypeDescriptor inputType,
				StringBuffer aggregatorClass) 
	{
        if (isWindowFunction) {
            aggregatorClass.append(ClassName.WindowCountAggregator);
        }
        else {
            aggregatorClass.append(ClassName.CountAggregator);
        }
		/*
		** COUNT never returns NULL
		*/
		return DataTypeDescriptor.getBuiltInDataTypeDescriptor(java.sql.Types.BIGINT, false);
	}

}
