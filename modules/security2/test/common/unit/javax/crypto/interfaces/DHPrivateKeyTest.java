/*
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
* @author Vera Y. Petrashkova
* @version $Revision$
*/

package javax.crypto.interfaces;

import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;

import junit.framework.TestCase;

import java.math.BigInteger;


/**
 * Tests for <code>DHPrivateKey</code> class field
 * 
 */
public class DHPrivateKeyTest extends TestCase {

    /**
     * Constructor for DHPrivateKey.
     * 
     * @param arg0
     */
    public DHPrivateKeyTest(String arg0) {
        super(arg0);
    }

    /**
     * Test for <code>serialVersionUID</code> field
     */
    public void testField() {
        checkDHPrivateKey key = new checkDHPrivateKey();
        assertEquals("Incorrect serialVersionUID",
                key.getSerVerUID(), //DHPrivateKey.serialVersionUID
                2211791113380396553L);
    }
    
    public class checkDHPrivateKey implements DHPrivateKey {
        public String getAlgorithm() {
            return "SecretKey";
        }
        public String getFormat() {
            return "Format";
        }
        public byte[] getEncoded() {
            return new byte[0];
        }
        public long getSerVerUID() {
            return serialVersionUID;
        }
        public BigInteger getX() {
            return null;
        }
        public DHParameterSpec getParams() {
            return null;
        }
    }
}
