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

package com.splicemachine.derby.impl.sql.execute.operations.window;

import java.io.Externalizable;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperationContext;
import com.splicemachine.derby.impl.sql.execute.operations.iapi.WarningCollector;

/**
 * @author  jyuan on 7/25/14.
 */
public interface WindowContext extends WarningCollector,Externalizable {

    void init(SpliceOperationContext context) throws StandardException;

    /**
     * The list of window functions that will be executed on the columns
     * of each row.<br/>
     * If there is more than one <code>WindowAggregator</code> int this
     * list, it is because they all share identical <code>over()</code>
     * clauses.  They were batched up this way so that they can all be
     * applied to the same <code>ExecRow</code>.
     * @return the list of window functions to be applied to a given row.
     */
    WindowAggregator[] getWindowFunctions();

    ExecRow getSortTemplateRow() throws StandardException;

    ExecRow getSourceIndexRow();

    /**
     * All aggregators in this list of window functions will
     * use the same key columns.
     * @return the key column array for all functions in this collection.
     */
    int[] getKeyColumns();

    /**
     * All aggregators in this list of window functions will
     * use the same key orders.
     * @return the key orders array for all functions in this collection.
     */
    boolean[] getKeyOrders();

    /**
     * All aggregators in this list of window functions will
     * use the same partition.
     * @return the partition array for all functions in this collection.
     */
    int[] getPartitionColumns();

    FrameDefinition getFrameDefinition();

    int[] getSortColumns();
}
