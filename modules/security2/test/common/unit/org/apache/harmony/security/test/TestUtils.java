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

package org.apache.harmony.security.test;

import java.io.File;

/**
 * Test utility class
 * 
 */
public class TestUtils {
    /**
     * Relative (to the project home) test root path
     */
    public static final String TEST_ROOT = System.getProperty("TEST_SRC_DIR", "test/common/unit")+ File.separator;

    /**
     * No need to instantiate
     */
    private TestUtils() {
    }

    /**
     * Prints byte array <code>data</code> as hex to the
     * <code>System.out</code> in the customizable form.
     *
     * @param perLine how many numbers put on single line
     * @param prefix custom output number prefix
     * @param delimiter custom output number delimiter
     * @param data data to be printed
     */
    public static void printAsHex(int perLine,
                                  String prefix,
                                  String delimiter,
                                  byte[] data) {
        for (int i=0; i<data.length; i++) {
            String tail = Integer.toHexString(0x000000ff & data[i]);
            if (tail.length() == 1) {
                tail = "0" + tail; 
            }
            System.out.print(prefix + "0x" + tail + delimiter);

            if (((i+1)%perLine) == 0) {
                System.out.println("");
            }
        }
        System.out.println("");
    }
}
