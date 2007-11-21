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

import org.apache.harmony.pack200.bytecode.OperandManager;

/**
 * This class is used to determine which init method should
 * be called, based on the last class which was sent a
 * constructor message.
 */
public class NewInitMethodRefForm extends InitMethodReferenceForm {

    public NewInitMethodRefForm(int opcode, String name,
            int[] rewrite) {
        super(opcode, name, rewrite);
        // TODO Auto-generated constructor stub
    }

    public int getOperandType() {
        return TYPE_NEWINITMETHODREF;
    }
    
    public boolean hasNewInitMethodRefOperand() {
        return true;
    }

    protected String context(OperandManager operandManager) {
        return operandManager.getNewClass();
    }
}