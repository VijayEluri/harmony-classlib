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

/**
* @author Vera Y. Petrashkova
* @version $Revision$
*/

package org.apache.harmony.security.tests.java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertificateException;

import junit.framework.TestCase;

import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.security.tests.support.cert.MyCertPathBuilderSpi;

import tests.support.Support_Exec;

/**
 * Tests for <code>CertPathBuilder</code> class constructors and
 * methods.
 * 
 */

public class CertPathBuilder1Test extends TestCase {

    /**
     * Constructor for CertPathBuilderTests.
     * @param name
     */
    public CertPathBuilder1Test(String name) {
        super(name);
    }
    public static final String srvCertPathBuilder = "CertPathBuilder";

    public static final String defaultType = "PKIX";    
    public static final String [] validValues = {
            "PKIX", "pkix", "PkiX", "pKiX" };
     
    private static String [] invalidValues = SpiEngUtils.invalidValues;
    
    private static boolean PKIXSupport = false;

    private static Provider defaultProvider;
    private static String defaultProviderName;
    
    private static String NotSupportMsg = "";

    public static final String DEFAULT_TYPE_PROPERTY = "certpathbuilder.type";

    static {
        defaultProvider = SpiEngUtils.isSupport(defaultType,
                srvCertPathBuilder);
        PKIXSupport = (defaultProvider != null);
        defaultProviderName = (PKIXSupport ? defaultProvider.getName() : null);
        NotSupportMsg = defaultType.concat(" is not supported");
    }
    private static CertPathBuilder[] createCPBs() {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return null;
        }
        try {
            CertPathBuilder[] certPBs = new CertPathBuilder[3];
            certPBs[0] = CertPathBuilder.getInstance(defaultType);
            certPBs[1] = CertPathBuilder.getInstance(defaultType,
                    defaultProviderName);
            certPBs[2] = CertPathBuilder.getInstance(defaultType,
                    defaultProvider);
            return certPBs;
        } catch (Exception e) {
            return null;
        }
    }    
    
    /**
     * @tests java.security.cert.CertPathBuilder#getDefaultType()
     */
    public void test_getDefaultType() throws Exception {

        // Regression for HARMONY-2785

        // test: default value  
        assertNull(Security.getProperty(DEFAULT_TYPE_PROPERTY));
        assertEquals("PKIX", CertPathBuilder.getDefaultType());

        // test: security property. fork new VM to keep testing env. clean
        Support_Exec.execJava(new String[] { DefaultType.class.getName() },
                null, true);
    }

    public static class DefaultType {

        public static void main(String[] args) {

            Security.setProperty(DEFAULT_TYPE_PROPERTY, "MyType");
            assertEquals("MyType", CertPathBuilder.getDefaultType());

            Security.setProperty(DEFAULT_TYPE_PROPERTY, "AnotherType");
            assertEquals("AnotherType", CertPathBuilder.getDefaultType());
        }
    }
    
    /**
     * Test for <code>getInstance(String algorithm)</code> method
	 * Assertion:
	 * throws NullPointerException when algorithm is null 
	 * throws NoSuchAlgorithmException when algorithm  is not correct
	 * or it is not available
     */
    public void testCertPathBuilder02() {
        try {
            CertPathBuilder.getInstance(null);
            fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                CertPathBuilder.getInstance(invalidValues[i]);
                fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }
    
    /**
     * Test for <code>getInstance(String algorithm)</code> method
	 * Assertion: returns CertPathBuilder object
     */ 
    public void testCertPathBuilder03() throws NoSuchAlgorithmException  {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        for (int i = 0; i < validValues.length; i++) {
            CertPathBuilder cpb = CertPathBuilder.getInstance(validValues[i]);
            assertEquals("Incorrect algorithm", cpb.getAlgorithm(), validValues[i]);
        }
    }
    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
	 * Assertion: throws IllegalArgumentException when provider is null or empty
	 * 
	 * FIXME: verify what exception will be thrown if provider is empty
     */  
    public void testCertPathBuilder04()
            throws NoSuchAlgorithmException, NoSuchProviderException  {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < validValues.length; i++) {        
            try {
                CertPathBuilder.getInstance(validValues[i], provider);
                fail("IllegalArgumentException must be thrown thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                CertPathBuilder.getInstance(validValues[i], "");
                fail("IllegalArgumentException must be thrown thrown");
            } catch (IllegalArgumentException e) {
            }
        }
    }
    
    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
	 * Assertion: 
	 * throws NoSuchProviderException when provider has invalid value
     */
    public void testCertPathBuilder05()
            throws NoSuchAlgorithmException  {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        for (int i = 0; i < validValues.length; i++ ) {
            for (int j = 1; j < invalidValues.length; j++) {
                try {
                    CertPathBuilder.getInstance(validValues[i], invalidValues[j]);
                    fail("NoSuchProviderException must be hrown");
                } catch (NoSuchProviderException e1) {
                }
            }
        }        
    }
    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
	 * Assertion: 
	 * throws NullPointerException when algorithm is null 
	 * throws NoSuchAlgorithmException when algorithm  is not correct
     */
    public void testCertPathBuilder06()
            throws NoSuchAlgorithmException, NoSuchProviderException  {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        try {
            CertPathBuilder.getInstance(null, defaultProviderName);
            fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                CertPathBuilder.getInstance(invalidValues[i], defaultProviderName);
                fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e1) {
            }
        }        
    }
    
    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
	 * Assertion: returns CertPathBuilder object
     */
    public void testCertPathBuilder07()
            throws NoSuchAlgorithmException, NoSuchProviderException  {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        CertPathBuilder certPB;
        for (int i = 0; i < validValues.length; i++) {
            certPB = CertPathBuilder.getInstance(validValues[i], defaultProviderName);
            assertEquals("Incorrect algorithm", certPB.getAlgorithm(), validValues[i]);
            assertEquals("Incorrect provider name", certPB.getProvider().getName(), defaultProviderName);
        }        
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
	 * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testCertPathBuilder08()
            throws NoSuchAlgorithmException  {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        Provider prov = null;
        for (int t = 0; t < validValues.length; t++ ) {
            try {
                CertPathBuilder.getInstance(validValues[t], prov);
                fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e1) {
            }
        }        
    }
    
    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
	 * Assertion: 
	 * throws NullPointerException when algorithm is null 
	 * throws NoSuchAlgorithmException when algorithm  is not correct
     */
    public void testCertPathBuilder09()
            throws NoSuchAlgorithmException, NoSuchProviderException  {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        try {
            CertPathBuilder.getInstance(null, defaultProvider);
            fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                CertPathBuilder.getInstance(invalidValues[i], defaultProvider);
                fail("NoSuchAlgorithm must be thrown");
            } catch (NoSuchAlgorithmException e1) {
            }
        }
    }
    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
	 * Assertion: returns CertPathBuilder object
     */
    public void testCertPathBuilder10()
            throws NoSuchAlgorithmException, NoSuchProviderException  {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        CertPathBuilder certPB;
        for (int i = 0; i < invalidValues.length; i++) {
            certPB = CertPathBuilder.getInstance(validValues[i], defaultProvider);
            assertEquals("Incorrect algorithm", certPB.getAlgorithm(), validValues[i]);
            assertEquals("Incorrect provider name", certPB.getProvider(), defaultProvider);
        }        
    }
    /**
     * Test for <code>build(CertPathParameters params)</code> method
	 * Assertion: throws InvalidAlgorithmParameterException params is null
     */
    public void testCertPathBuilder11()
            throws NoSuchAlgorithmException, NoSuchProviderException, 
            CertPathBuilderException {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }        
        CertPathBuilder [] certPB = createCPBs();
        assertNotNull("CertPathBuilder objects were not created", certPB);
        for (int i = 0; i < certPB.length; i++ ){
            try {
                certPB[i].build(null);
                fail("InvalidAlgorithmParameterException must be thrown");
            } catch(InvalidAlgorithmParameterException e) {
            }
        }
    }
    /**
     * Test for 
     * <code>CertPathBuilder</code> constructor
     * Assertion: returns CertPathBuilder object
     */
    public void testCertPathBuilder12()
            throws CertificateException, NoSuchProviderException, 
            NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            CertPathBuilderException {
        if (!PKIXSupport) {
            fail(NotSupportMsg);
            return;
        }
        CertPathBuilderSpi spi = new MyCertPathBuilderSpi();
        CertPathBuilder certPB = new myCertPathBuilder(spi, 
                    defaultProvider, defaultType);
        assertEquals("Incorrect algorithm", certPB.getAlgorithm(), defaultType);
        assertEquals("Incorrect provider", certPB.getProvider(), defaultProvider);
        try {
            certPB.build(null);
            fail("CertPathBuilderException must be thrown ");
        } catch (CertPathBuilderException e) {            
        }
        certPB = new myCertPathBuilder(null, null, null);
        assertNull("Incorrect algorithm", certPB.getAlgorithm());
        assertNull("Incorrect provider", certPB.getProvider());            
        try {
            certPB.build(null);
            fail("NullPointerException must be thrown ");
        } catch (NullPointerException e) {            
        }
    }
    public static void main(String args[]) {
        junit.textui.TestRunner.run(CertPathBuilder1Test.class);
    }  
    
}
/**
 * Addifional class to verify CertPathBuilder constructor
 */
class myCertPathBuilder extends CertPathBuilder {

    public myCertPathBuilder(CertPathBuilderSpi spi, Provider prov, String type) {
        super(spi, prov, type);
    }
}
