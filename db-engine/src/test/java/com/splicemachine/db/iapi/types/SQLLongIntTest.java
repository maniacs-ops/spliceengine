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
package com.splicemachine.db.iapi.types;

import com.splicemachine.db.iapi.error.StandardException;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Order;
import org.apache.hadoop.hbase.util.PositionedByteRange;
import org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange;
import org.apache.spark.sql.catalyst.expressions.UnsafeRow;
import org.apache.spark.sql.catalyst.expressions.codegen.BufferHolder;
import org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * Test Class for SQLLongint
 *
 */
public class SQLLongIntTest {

        @Test
        public void addTwo() throws StandardException {
            SQLLongint long1 = new SQLLongint(100l);
            SQLLongint long2 = new SQLLongint(100l);
            Assert.assertEquals("Integer Add Fails", 200l, long1.plus(long1, long2, null).getLong(),0l);
        }
    
        @Test
        public void subtractTwo() throws StandardException {
            SQLLongint long1 = new SQLLongint(200l);
            SQLLongint long2 = new SQLLongint(100l);
            Assert.assertEquals("Integer subtract Fails",100l,long1.minus(long1, long2, null).getLong(),0l);
        }
        @Test(expected = StandardException.class)
        public void testPositiveOverFlow() throws StandardException {
            SQLLongint long1 = new SQLLongint(Long.MAX_VALUE);
            SQLLongint long2 = new SQLLongint(1);
            long1.plus(long1,long2,null);
        }

        @Test(expected = StandardException.class)
        public void testNegativeOverFlow() throws StandardException {
                SQLLongint long1 = new SQLLongint(Long.MIN_VALUE);
                SQLLongint long2 = new SQLLongint(1);
                long1.minus(long1, long2, null);
        }
    
        @Test
        public void serdeValueData() throws Exception {
                UnsafeRow row = new UnsafeRow(1);
                UnsafeRowWriter writer = new UnsafeRowWriter(new BufferHolder(row),1);
                SQLLongint value = new SQLLongint(100l);
                SQLLongint valueA = new SQLLongint();
                value.write(writer, 0);
                Assert.assertEquals("SerdeIncorrect",100l,row.getLong(0),0l);
                valueA.read(row,0);
                Assert.assertEquals("SerdeIncorrect",100l,valueA.getLong(),0l);
            }

        @Test
        public void serdeNullValueData() throws Exception {
                UnsafeRow row = new UnsafeRow(1);
                UnsafeRowWriter writer = new UnsafeRowWriter(new BufferHolder(row),1);
                SQLLongint value = new SQLLongint();
                SQLLongint valueA = new SQLLongint();
                value.write(writer, 0);
                Assert.assertTrue("SerdeIncorrect", row.isNullAt(0));
                value.read(row, 0);
                Assert.assertTrue("SerdeIncorrect", valueA.isNull());
            }
    
        @Test
        public void serdeKeyData() throws Exception {
                SQLLongint value1 = new SQLLongint(100l);
                SQLLongint value2 = new SQLLongint(200l);
                SQLLongint value1a = new SQLLongint();
                SQLLongint value2a = new SQLLongint();
                PositionedByteRange range1 = new SimplePositionedMutableByteRange(value1.encodedKeyLength());
                PositionedByteRange range2 = new SimplePositionedMutableByteRange(value2.encodedKeyLength());
                value1.encodeIntoKey(range1, Order.ASCENDING);
                value2.encodeIntoKey(range2, Order.ASCENDING);
                Assert.assertTrue("Positioning is Incorrect", Bytes.compareTo(range1.getBytes(), 0, 9, range2.getBytes(), 0, 9) < 0);
                range1.setPosition(0);
                range2.setPosition(0);
                value1a.decodeFromKey(range1);
                value2a.decodeFromKey(range2);
                Assert.assertEquals("1 incorrect",value1.getLong(),value1a.getLong(),0l);
                Assert.assertEquals("2 incorrect",value2.getLong(),value2a.getLong(),0l);
        }
    
}
