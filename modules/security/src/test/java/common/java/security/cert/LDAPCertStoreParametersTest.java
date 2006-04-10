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

package java.security.cert;

import junit.framework.TestCase;

/**
 * Tests for <code>java.security.cert.LDAPCertStoreParameters</code>
 * fields and methods
 * 
 */
public class LDAPCertStoreParametersTest extends TestCase {

    /**
     * Constructor for LDAPCertStoreParametersTest.
     * @param name
     */
    public LDAPCertStoreParametersTest(String name) {
        super(name);
    }

    //
    // Tests
    //

    /**
     * Test #1 for <code>LDAPCertStoreParameters()</code> constructor<br>
     * Assertion: Creates an instance of <code>LDAPCertStoreParameters</code>
     * with the default parameter values (server name "localhost", port 389)
     */
    public final void testLDAPCertStoreParameters01() {
        CertStoreParameters cp = new LDAPCertStoreParameters();
        assertTrue("isLDAPCertStoreParameters",
                cp instanceof LDAPCertStoreParameters);
    }

    /**
     * Test #2 for <code>LDAPCertStoreParameters()</code> constructor<br>
     * Assertion: Creates an instance of <code>LDAPCertStoreParameters</code>
     * with the default parameter values (server name "localhost", port 389)
     */
    public final void testLDAPCertStoreParameters02() {
        LDAPCertStoreParameters cp = new LDAPCertStoreParameters();
        assertTrue("host", "localhost".equals(cp.getServerName()));
        assertTrue("port", cp.getPort() == 389);
    }

    /**
     * Test #1 for <code>LDAPCertStoreParameters(String)</code> constructor<br>
     * Assertion: Creates an instance of <code>LDAPCertStoreParameters</code>
     * with the specified server name and a default port of 389
     */
    public final void testLDAPCertStoreParametersString01() {
        CertStoreParameters cp = new LDAPCertStoreParameters("myhost");
        assertTrue("isLDAPCertStoreParameters",
                cp instanceof LDAPCertStoreParameters);
    }

    /**
     * Test #2 for <code>LDAPCertStoreParameters(String)</code> constructor<br>
     * Assertion: Creates an instance of <code>LDAPCertStoreParameters</code>
     * with the specified server name and a default port of 389
     */
    public final void testLDAPCertStoreParametersString02() {
        String serverName = "myhost";
        LDAPCertStoreParameters cp = new LDAPCertStoreParameters(serverName);
        assertTrue("host", serverName.equals(cp.getServerName()));
        assertTrue("port", cp.getPort() == 389);
    }

    /**
     * Test #3 for <code>LDAPCertStoreParameters(String)</code> constructor<br>
     * Assertion: throws <code>NullPointerException</code> -
     * if <code>serverName</code> is <code>null</code>
     */
    public final void testLDAPCertStoreParametersString03() {
        try {
            new LDAPCertStoreParameters(null);
            fail("NPE expected");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test #1 for <code>LDAPCertStoreParameters(String, int)</code> constructor<br>
     * Assertion: Creates an instance of <code>LDAPCertStoreParameters</code>
     * with the specified parameter values
     */
    public final void testLDAPCertStoreParametersStringint01() {
        CertStoreParameters cp = new LDAPCertStoreParameters("myhost", 1098);
        assertTrue("isLDAPCertStoreParameters",
                cp instanceof LDAPCertStoreParameters);
    }

    /**
     * Test #2 for <code>LDAPCertStoreParameters(String, int)</code> constructor<br>
     * Assertion: Creates an instance of <code>LDAPCertStoreParameters</code>
     * with the specified parameter values
     */
    public final void testLDAPCertStoreParametersStringint02() {
        String serverName = "myhost";
        int portNumber = 1099;
        LDAPCertStoreParameters cp =
            new LDAPCertStoreParameters(serverName, portNumber);
        assertTrue("host", serverName.equals(cp.getServerName()));
        assertTrue("port", cp.getPort() == portNumber);
    }

    /**
     * Test #3 for <code>LDAPCertStoreParameters(String, int)</code> constructor<br>
     * Assertion: throws <code>NullPointerException</code> -
     * if <code>serverName</code> is <code>null</code>
     */
    public final void testLDAPCertStoreParametersStringint03() {
        try {
            new LDAPCertStoreParameters(null, 0);
            fail("NPE expected");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test for <code>clone()</code> method<br>
     * Assertion: Returns a copy of this object
     */
    public final void testClone() {
        LDAPCertStoreParameters cp1 =
            new LDAPCertStoreParameters("myhost", 1100);
        LDAPCertStoreParameters cp2 = (LDAPCertStoreParameters)cp1.clone();
        // check that that we have new object
        assertTrue("newObject", cp1 != cp2);
        assertTrue("hostsTheSame",
                cp1.getServerName().equals(cp2.getServerName()));
        assertTrue("portsTheSame", cp1.getPort() == cp2.getPort());
    }

    /**
     * Test for <code>toString()</code> method<br>
     * Assertion: returns the formatted string describing parameters
     */
    public final void testToString() {
        LDAPCertStoreParameters cp1 =
            new LDAPCertStoreParameters("myhost", 1101);

        assertNotNull(cp1.toString());
    }

    /**
     * Test for <code>toString()</code> method<br>
     * Assertion: returns the port number
     */
    public final void testGetPort() {
        int portNumber = -1099;
        LDAPCertStoreParameters cp =
            new LDAPCertStoreParameters("serverName", portNumber);
        assertTrue(cp.getPort() == portNumber);
    }

    /**
     * Test for <code>toString()</code> method<br>
     * Assertion: returns the server name (never <code>null</code>)
     */
    public final void testGetServerName() {
        LDAPCertStoreParameters cp =
            new LDAPCertStoreParameters("serverName");
        assertNotNull(cp.getServerName());
    }

}
