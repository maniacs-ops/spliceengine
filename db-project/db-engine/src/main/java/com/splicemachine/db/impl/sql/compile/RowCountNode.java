/*

   Derby - Class org.apache.derby.impl.sql.compile.RowCountNode

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

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.reference.ClassName;
import com.splicemachine.db.iapi.services.classfile.VMOpcode;
import com.splicemachine.db.iapi.services.compiler.MethodBuilder;
import com.splicemachine.db.iapi.services.sanity.SanityManager;
import com.splicemachine.db.iapi.sql.compile.C_NodeTypes;
import com.splicemachine.db.iapi.sql.compile.CostEstimate;
import com.splicemachine.db.iapi.sql.compile.Optimizer;
import com.splicemachine.db.iapi.sql.dictionary.DataDictionary;

/**
 * The result set generated by this node (RowCountResultSet) implements the
 * filtering of rows needed for the <result offset clause> and the <fetch first
 * clause>.  It sits on top of the normal SELECT's top result set, but under any
 * ScrollInsensitiveResultSet. The latter's positioning is needed for the correct
 * functioning of <result offset clause> and <fetch first clause> in the
 * presence of scrollable and/or updatable result sets and CURRENT OF cursors.
 */
public final class RowCountNode extends SingleChildResultSetNode{
    /**
     * If not null, this represents the value of a <result offset clause>.
     */
    private ValueNode offset;
    /**
     * If not null, this represents the value of a <fetch first clause>.
     */
    private ValueNode fetchFirst;
    /**
     * True if the offset/fetchFirst clauses were added by JDBC LIMIT syntax.
     */
    private boolean hasJDBClimitClause;


    /**
     * Initializer for a RowCountNode
     *
     * @throws StandardException
     */
    public void init(Object childResult,
                     Object rcl,
                     Object offset,
                     Object fetchFirst,
                     Object hasJDBClimitClause)
            throws StandardException{

        init(childResult,null);
        resultColumns=(ResultColumnList)rcl;
        this.offset=(ValueNode)offset;
        this.fetchFirst=(ValueNode)fetchFirst;
        this.hasJDBClimitClause=(hasJDBClimitClause!=null) && (Boolean)hasJDBClimitClause;
    }


    /**
     * Optimize this SingleChildResultSetNode.
     *
     * @param dataDictionary The DataDictionary to use for optimization
     * @param predicates     The PredicateList to optimize.  This should
     *                       be a join predicate.
     * @param outerRows      The number of outer joining rows
     * @throws StandardException Thrown on error
     * @return ResultSetNode    The top of the optimized subtree
     */
    @Override
    public ResultSetNode optimize(DataDictionary dataDictionary,
                                  PredicateList predicates,
                                  double outerRows) throws StandardException{
		/* We need to implement this method since a NRSN can appear above a
		 * SelectNode in a query tree.
		 */
        childResult=childResult.optimize(
                dataDictionary,
                predicates,
                outerRows);

        Optimizer optimizer=
                getOptimizer(
                        (FromList)getNodeFactory().getNode(
                                C_NodeTypes.FROM_LIST,
                                getNodeFactory().doJoinOrderOptimization(),
                                getContextManager()),
                        predicates,
                        dataDictionary,
                        null);
        costEstimate=optimizer.newCostEstimate();
        fixCost();
        return this;
    }

    public void fixCost() throws StandardException {
        if (fetchFirst != null && fetchFirst instanceof NumericConstantNode) {
            long totalRowCount = costEstimate.getEstimatedRowCount();
            long fetchCount = ((NumericConstantNode)fetchFirst).getValue().getInt();
            double factor = (double)fetchCount/(double)totalRowCount;
            costEstimate.setEstimatedRowCount(fetchCount);
            costEstimate.setSingleScanRowCount(fetchCount);
            costEstimate.setEstimatedHeapSize((long)(costEstimate.getEstimatedHeapSize()*factor));
            costEstimate.setRemoteCost((long)(costEstimate.getRemoteCost()*factor));
        }
        else
        if (offset != null && offset instanceof NumericConstantNode) {
            long totalRowCount = costEstimate.getEstimatedRowCount();
            long offsetCount = ((NumericConstantNode)offset).getValue().getInt();
            costEstimate.setEstimatedRowCount(totalRowCount-offsetCount >=1? totalRowCount-offsetCount:1); // Snap to 1
        } else {
            // Nothing
        }
    }

    /**
     * Get the final CostEstimate for this node.
     *
     * @return The final CostEstimate for this node, which is
     * the final cost estimate for the child node.
     */
    @Override
    public CostEstimate getFinalCostEstimate() throws StandardException{
		/*
		** The cost estimate will be set here if either optimize() or
		** optimizeIt() was called on this node.  It's also possible
		** that optimization was done directly on the child node,
		** in which case the cost estimate will be null here.
		*/
        if(costEstimate==null) {
            costEstimate = childResult.getFinalCostEstimate().cloneMe();
            fixCost();
            return costEstimate;
        }
        else{
            return costEstimate;
        }
    }


    /**
     * Generate code.
     *
     * @param acb activation class builder
     * @param mb  method builder
     * @throws StandardException Thrown on error
     */
    @Override
    public void generate(ActivationClassBuilder acb, MethodBuilder mb) throws StandardException{

        /* Get the next ResultSet #, so that we can number this ResultSetNode,
         * its ResultColumnList and ResultSet.
         */
        assignResultSetNumber();

        costEstimate=getFinalCostEstimate();childResult.getFinalCostEstimate();

        acb.pushGetResultSetFactoryExpression(mb);

        childResult.generate(acb,mb); // arg1

        acb.pushThisAsActivation(mb);  // arg2
        mb.push(resultSetNumber);      // arg3

        // arg4
        if(offset!=null){
            generateExprFun(acb,mb,offset);
        }else{
            mb.pushNull(ClassName.GeneratedMethod);
        }

        // arg5
        if(fetchFirst!=null){
            generateExprFun(acb,mb,fetchFirst);
        }else{
            mb.pushNull(ClassName.GeneratedMethod);
        }

        mb.push(hasJDBClimitClause);  // arg6

        mb.push(costEstimate.rowCount()); // arg7
        mb.push(costEstimate.getEstimatedCost()); // arg8
        mb.push(printExplainInformationForActivation()); // arg9

        mb.callMethod(VMOpcode.INVOKEINTERFACE, null, "getRowCountResultSet", ClassName.NoPutResultSet, 9);
    }


    private void generateExprFun( ExpressionClassBuilder ecb, MethodBuilder mb, ValueNode vn) throws StandardException{

        // Generates:
        //     Object exprFun { }
        MethodBuilder exprFun=ecb.newExprFun();

        /* generates:
         *    return  <dynamic parameter.generate(ecb)>;
         * and adds it to exprFun
         */
        vn.generateExpression(ecb,exprFun);
        exprFun.methodReturn();

        // we are done modifying exprFun, complete it.
        exprFun.complete();

        // Pass in the method that will be used to evaluates the dynamic
        // parameter in RowCountResultSet.
        ecb.pushMethodReference(mb,exprFun);
    }


    /**
     * Convert this object to a String.  See comments in QueryTreeNode.java
     * for how this should be done for tree printing.
     *
     * @return This object as a String
     */
    @Override
    public String toString(){
        if(SanityManager.DEBUG){
            return "offset: "+offset+"\n"+
                    "fetchFirst:"+fetchFirst+"\n"+
                    super.toString();
        }else{
            return "";
        }
    }

    @Override
    public String printExplainInformation(String attrDelim, int order) throws StandardException {
        StringBuilder sb = new StringBuilder();
        sb.append(spaceToLevel())
                .append("Limit(")
                .append("n=").append(order)
                .append(attrDelim).append(getFinalCostEstimate().prettyProcessingString(attrDelim));
                if (offset != null && offset instanceof NumericConstantNode) {
                    sb.append(attrDelim).append("offset=").append( ((NumericConstantNode)offset).getValue());
                }
                if (fetchFirst != null && fetchFirst instanceof NumericConstantNode) {
                    sb.append(attrDelim).append("fetchFirst=").append( ((NumericConstantNode)fetchFirst).getValue());
                }
                sb.append(")");
        return sb.toString();
    }

}
