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

package java.security;

import java.security.spec.AlgorithmParameterSpec;

import org.apache.harmony.security.SpiEngUtils;
import junit.framework.TestCase;


/**
 * Tests for <code>AlgorithmParameterGenerator</code> class constructors and
 * methods.
 * 
 */

public class AlgorithmParameterGeneratorTest1 extends TestCase {
    /**
     * Constructor for AlgorithmParameterGeneratorTests.
     * 
     * @param arg0
     */
    public AlgorithmParameterGeneratorTest1(String arg0) {
        super(arg0);
    }

    private static String[] invalidValues = SpiEngUtils.invalidValues;
    private static String validAlgName = "DSA";
    private static String[] algs =  {
            "DSA", "dsa", "Dsa", "DsA", "dsA" }; 
            
    public static final String srvAlgorithmParameterGenerator = "AlgorithmParameterGenerator";


    private static String validProviderName = null;

    private static Provider validProvider = null;;

    private static boolean DSASupported = false;

    static {
        validProvider = SpiEngUtils.isSupport(
                validAlgName,
                srvAlgorithmParameterGenerator);
        DSASupported = (validProvider != null);
        validProviderName = (DSASupported ? validProvider.getName() : null);
    }

    protected AlgorithmParameterGenerator[] createAPGen() {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return null;
        }
        AlgorithmParameterGenerator[] apg = new AlgorithmParameterGenerator[3];
        try {
            apg[0] = AlgorithmParameterGenerator.getInstance(validAlgName);
            apg[1] = AlgorithmParameterGenerator.getInstance(validAlgName,
                    validProvider);
            apg[2] = AlgorithmParameterGenerator.getInstance(validAlgName,
                    validProviderName);
            return apg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: 
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     * 
     */
    public void testAlgorithmParameterGenerator01()
            throws NoSuchAlgorithmException {
        try {
            AlgorithmParameterGenerator.getInstance(null);
            fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                AlgorithmParameterGenerator.getInstance(invalidValues[i]);
                fail("NoSuchAlgorithmException should be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: returns AlgorithmParameterGenerator instance 
     * when algorithm is DSA
     */
    public void testAlgorithmParameterGenerator02()
            throws NoSuchAlgorithmException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        AlgorithmParameterGenerator apg;
        for (int i = 0; i < algs.length; i++) {
            apg = AlgorithmParameterGenerator.getInstance(algs[i]);
            assertEquals("Incorrect algorithm", apg.getAlgorithm(), algs[i]);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method 
     * Assertion: 
     * throws IllegalArgumentException if provider is null or empty
     */
    public void testAlgorithmParameterGenerator03()
            throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        String provider = null;
        for (int i = 0; i < algs.length; i++) {
            try {
                AlgorithmParameterGenerator.getInstance(algs[i], provider);
                fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
            try {
                AlgorithmParameterGenerator.getInstance(algs[i], "");
                fail("IllegalArgumentException must be thrown when provider is empty");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method 
     * Assertion: throws NoSuchProviderException if provider is not
     * available
     */
    public void testAlgorithmParameterGenerator04()
            throws NoSuchAlgorithmException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        for (int i = 0; i < algs.length; i++) {
            for (int j = 1; j < invalidValues.length; j++) {
                try {
                    AlgorithmParameterGenerator.getInstance(algs[i],
                            invalidValues[j]);
                    fail("NoSuchProviderException must be thrown (provider: "
                            .concat(invalidValues[j]));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method 
     * Assertion: 
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     */
    public void testAlgorithmParameterGenerator05()
            throws NoSuchProviderException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        try {
            AlgorithmParameterGenerator.getInstance(null, validProviderName);
            fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                AlgorithmParameterGenerator.getInstance(invalidValues[i],
                        validProviderName);
                fail("NoSuchAlgorithmException must be thrown when (algorithm: "
                        .concat(invalidValues[i].concat(")")));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method 
     * Assertion: return AlgorithmParameterGenerator
     */
    public void testAlgorithmParameterGenerator06()
            throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        AlgorithmParameterGenerator apg;
        for (int i = 0; i < algs.length; i++) {
            apg = AlgorithmParameterGenerator.getInstance(algs[i],
                    validProviderName);
            assertEquals("Incorrect algorithm", algs[i], apg.getAlgorithm());
            assertEquals("Incorrect provider", apg.getProvider().getName(),
                    validProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method 
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testAlgorithmParameterGenerator07()
            throws NoSuchAlgorithmException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        Provider provider = null;
        for (int i = 0; i < algs.length; i++) {
            try {
                AlgorithmParameterGenerator.getInstance(algs[i], provider);
                fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method 
     * Assertion: 
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     */
    public void testAlgorithmParameterGenerator08() {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        try {
            AlgorithmParameterGenerator.getInstance(null, validProvider);
            fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                AlgorithmParameterGenerator.getInstance(invalidValues[i],
                        validProvider);
                fail("NoSuchAlgorithmException must be thrown (algorithm: "
                        .concat(invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method 
     * Assertion: returns AlgorithmParameterGenerator object
     */
    public void testAlgorithmParameterGenerator09()
            throws NoSuchAlgorithmException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        AlgorithmParameterGenerator apg;
        for (int i = 0; i < algs.length; i++) {
            apg = AlgorithmParameterGenerator.getInstance(algs[i],
                    validProvider);
            assertEquals("Incorrect algorithm", apg.getAlgorithm(), algs[i]);
            assertEquals("Incorrect provider", apg.getProvider(), validProvider);
        }
    }

    /**
     * Test for <code>generateParameters()</code> method
     * Assertion: returns AlgorithmParameters object
     */  
    public void testAlgorithmParameterGenerator10()
            throws NoSuchAlgorithmException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        AlgorithmParameterGenerator apg = AlgorithmParameterGenerator
                .getInstance(validAlgName);
        apg.init(512);
        AlgorithmParameters ap = apg.generateParameters();
        assertEquals("Incorrect algorithm", ap.getAlgorithm().toUpperCase(),
                apg.getAlgorithm().toUpperCase());
    }

    /**
     * Test for <code>init(int size)</code> and
     * <code>init(int size, SecureRandom random<code> methods
     * Assertion: throws InvalidParameterExceptiom when size is incorrect
     */
    public void testAlgorithmParameterGenerator11() throws InvalidParameterException {        
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        int [] keys = {-10000, -512, -1, 0, 10000};
        SecureRandom random = new SecureRandom();
        AlgorithmParameterGenerator[] apgs = createAPGen();
        assertNotNull("AlgorithmParameterGenerator objects were not created",
                apgs);

        for (int i = 0; i < apgs.length; i++) {
            for (int j = 0; j < keys.length; j++) {
                apgs[i].init(keys[j]);
                apgs[i].init(keys[j], random);
                apgs[i].init(keys[j], null);
            }
            apgs[i].init(1024);
            apgs[i].init(1024, random);
        }
    }
    
    /**
     * Test for <code>init(AlgorithmParameterSpec param)</code> and 
     * <code>init(AlgorithmParameterSpec param, SecureRandom random<code> 
     * methods
     * Assertion: throws InvalidAlgorithmParameterExceptiom when param is null
     */
    public void testAlgorithmParameterGenerator12() {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        SecureRandom random = new SecureRandom();
        AlgorithmParameterSpec aps = null;
        AlgorithmParameterGenerator[] apgs = createAPGen();
        assertNotNull("AlgorithmParameterGenerator objects were not created",
                apgs);
        for (int i = 0; i < apgs.length; i++) {
            try {
                apgs[i].init(aps, random);
                fail("InvalidAlgorithmParameterException must be throws when param is null");
            } catch (InvalidAlgorithmParameterException e) {                    
            }
        }        
    }
    
    /**
     * Test for <code>AlgorithmParameterGenerator</code> constructor 
     * Assertion: returns AlgorithmParameterGenerator object
     */
    public void testAlgorithmParameterGeneratorConstr() throws NoSuchAlgorithmException {
        if (!DSASupported) {
            fail(validAlgName + " algorithm is not supported");
            return;
        }
        AlgorithmParameterGeneratorSpi spi = new MyAlgorithmParameterGeneratorSpi();
        AlgorithmParameterGenerator apg = 
                new myAlgPG(spi, validProvider, validAlgName);
        assertEquals("Incorrect algorithm", apg.getAlgorithm(), validAlgName);
        assertEquals("Incorrect provider",apg.getProvider(),validProvider);
        try {
            apg.init(-10, null);
            fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        
        apg = new myAlgPG(null, null, null);
        assertNull("Incorrect algorithm", apg.getAlgorithm());
        assertNull("Incorrect provider", apg.getProvider());
        try {
            apg.init(-10, null);
            fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(AlgorithmParameterGeneratorTest1.class);
    }
}
/**
 * Addifional class to verify AlgorithmParameterGenerator constructor
 */
class myAlgPG extends AlgorithmParameterGenerator {
    public myAlgPG(AlgorithmParameterGeneratorSpi spi, Provider prov, String alg) {
        super(spi, prov, alg);
    }
}
