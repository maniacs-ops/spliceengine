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
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;

/**
 *
 * Test Class for SQLDecimal
 *
 */
public class SQLDecimalTest {

        @Test
        public void addTwo() throws StandardException {
            SQLDecimal decimal1 = new SQLDecimal(new BigDecimal(100.0d));
            SQLDecimal decimal2 = new SQLDecimal(new BigDecimal(100.0d));
            Assert.assertEquals("Integer Add Fails", new BigDecimal(200.0d), decimal1.plus(decimal1, decimal2, null).getBigDecimal());
        }
    
        @Test
        public void subtractTwo() throws StandardException {
            SQLDecimal decimal1 = new SQLDecimal(new BigDecimal(200.0d));
            SQLDecimal decimal2 = new SQLDecimal(new BigDecimal(100.0d));
            Assert.assertEquals("Integer subtract Fails",new BigDecimal(100.0d),decimal1.minus(decimal1, decimal2, null).getBigDecimal());
        }
        @Test
        public void serdeValueData() throws Exception {
                UnsafeRow row = new UnsafeRow(1);
                UnsafeRowWriter writer = new UnsafeRowWriter(new BufferHolder(row),1);
                SQLDecimal value = new SQLDecimal(new BigDecimal(100.0d));
                SQLDecimal valueA = new SQLDecimal(null,value.precision,value.getScale());
                writer.reset();
                value.write(writer, 0);
                Assert.assertEquals("SerdeIncorrect",new BigDecimal(100.0d),row.getDecimal(0,value.getDecimalValuePrecision(),value.getDecimalValueScale()).toJavaBigDecimal());
                valueA.read(row,0);
                Assert.assertEquals("SerdeIncorrect",new BigDecimal(100.0d),valueA.getBigDecimal());
            }

        @Test
        public void serdeNullValueData() throws Exception {
                UnsafeRow row = new UnsafeRow(1);
                UnsafeRowWriter writer = new UnsafeRowWriter(new BufferHolder(row),1);
                SQLDecimal value = new SQLDecimal();
                SQLDecimal valueA = new SQLDecimal();
                value.write(writer, 0);
                Assert.assertTrue("SerdeIncorrect", row.isNullAt(0));
                value.read(row, 0);
                Assert.assertTrue("SerdeIncorrect", valueA.isNull());
            }
        // TODO
        @Test
        @Ignore
        public void serdeKeyData() throws Exception {
                SQLDecimal value1 = new SQLDecimal(new BigDecimal(100.0d));
                SQLDecimal value2 = new SQLDecimal(new BigDecimal(200.0d));
                SQLDecimal value1a = new SQLDecimal();
                value1a.setPrecision(value1.getPrecision());
                value1a.setScale(value1.getScale());
                SQLDecimal value2a = new SQLDecimal();
                value2a.setPrecision(value2.getPrecision());
                value2a.setScale(value2.getScale());
                PositionedByteRange range1 = new SimplePositionedMutableByteRange(value1.encodedKeyLength());
                PositionedByteRange range2 = new SimplePositionedMutableByteRange(value2.encodedKeyLength());
                value1.encodeIntoKey(range1, Order.ASCENDING);
                value2.encodeIntoKey(range2, Order.ASCENDING);
                Assert.assertTrue("Positioning is Incorrect", Bytes.compareTo(range1.getBytes(), 0, 9, range2.getBytes(), 0, 9) < 0);
                range1.setPosition(0);
                range2.setPosition(0);
                value1a.decodeFromKey(range1);
                value2a.decodeFromKey(range2);
                Assert.assertEquals("1 incorrect",value1.getBigDecimal(),value1a.getBigDecimal());
                Assert.assertEquals("2 incorrect",value2.getBigDecimal(),value2a.getBigDecimal());
        }
}
