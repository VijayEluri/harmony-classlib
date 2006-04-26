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
* @author Vladimir N. Molotkov
* @version $Revision$
*/

package java.security.spec;

import java.math.BigInteger;

import junit.framework.TestCase;


/**
 * Tests for <code>DSAPrivateKeySpec</code>
 * 
 */
public class DSAPrivateKeySpecTest extends TestCase {

    /**
     * Constructor for DSAPrivateKeySpecTest.
     * @param name
     */
    public DSAPrivateKeySpecTest(String name) {
        super(name);
    }

    /**
     * Test for constructor
     */
    public final void testDSAPrivateKeySpec() {
        KeySpec ks = new DSAPrivateKeySpec(
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"));
        
        assertTrue(ks instanceof DSAPrivateKeySpec);
    }

    /**
     * getG() test
     */
    public final void testGetG() {
        DSAPrivateKeySpec dpks = new DSAPrivateKeySpec(
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"));
        
        assertEquals(4, dpks.getG().intValue());
    }

    /**
     * getP() test
     */
    public final void testGetP() {
        DSAPrivateKeySpec dpks = new DSAPrivateKeySpec(
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"));
        
        assertEquals(2, dpks.getP().intValue());
    }

    /**
     * getQ() test
     */
    public final void testGetQ() {
        DSAPrivateKeySpec dpks = new DSAPrivateKeySpec(
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"));
        
        assertEquals(3, dpks.getQ().intValue());
    }

    /**
     * getX() test
     */
    public final void testGetX() {
        DSAPrivateKeySpec dpks = new DSAPrivateKeySpec(
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"));
        
        assertEquals(1, dpks.getX().intValue());
    }

}
