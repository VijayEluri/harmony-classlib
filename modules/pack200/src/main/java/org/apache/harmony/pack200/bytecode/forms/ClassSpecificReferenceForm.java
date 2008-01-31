/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.pack200.bytecode.forms;

import org.apache.harmony.pack200.Pack200Exception;
import org.apache.harmony.pack200.SegmentConstantPool;
import org.apache.harmony.pack200.bytecode.ByteCode;
import org.apache.harmony.pack200.bytecode.ClassFileEntry;
import org.apache.harmony.pack200.bytecode.OperandManager;

/**
 * Abstract superclass of all classes that have class-specific
 * references to constant pool information.
 * These classes have a context (a string representing a pack200 class)
 * i.e., they send getClassSpecificPoolEntry instead of getConstantPoolEntry.
 *
 */
public abstract class ClassSpecificReferenceForm extends ReferenceForm {

    public ClassSpecificReferenceForm(int opcode, String name,
            int[] rewrite) {
        super(opcode, name, rewrite);
        // TODO Auto-generated constructor stub
    }

    protected abstract int getOffset(OperandManager operandManager);
    protected abstract int getPoolID();
    protected abstract String context(OperandManager operandManager);

    protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
        SegmentConstantPool globalPool = operandManager.globalConstantPool();
        ClassFileEntry[] nested = null;
        nested = new ClassFileEntry[] {
                globalPool.getClassSpecificPoolEntry(getPoolID(), offset, context(operandManager))
                };
        byteCode.setNested(nested);
        byteCode.setNestedPositions(new int[][] {{0, 2}});
    }

}
