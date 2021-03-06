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
package com.splicemachine.dbTesting.functionTests.util.streams;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ReadOnceByteArrayInputStream extends ByteArrayInputStream {

    private boolean isClosed;
    
    public ReadOnceByteArrayInputStream(byte[] arg0) {
        super(arg0);
    }

    public ReadOnceByteArrayInputStream(byte[] arg0, int arg1, int arg2) {
        super(arg0, arg1, arg2);
    }
    
    public boolean markSupported()
    {
        return false;
    }
    
    public void close() throws IOException
    {
        isClosed = true;
        super.close();
    }
    
    public int read(byte[] b,
            int off,
            int len)
    {
        if (isClosed)
            return -1;
        return super.read(b, off, len);
    }
    
    public int read()
    {
        if (isClosed)
            return -1;
        return super.read();
    }

}
