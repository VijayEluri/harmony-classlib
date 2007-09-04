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
package org.apache.harmony.pack200;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Parses the file band headers (not including the actual bits themselves).
 * At the end of this parse call, the input stream will be positioned at the
 * start of the file_bits themselves, and there will be Sum(file_size) bits
 * remaining in the stream with BYTE1 compression. A decent implementation
 * will probably just stream the bytes out to the reconstituted Jar rather
 * than caching them.
 * 
 */
public class FileBands extends BandSet {

    private byte[][] fileBits;

    private long[] fileModtime;

    private String[] fileName;

    private long[] fileOptions;

    private long[] fileSize;

    private String[] cpUTF8;
    
    /**
     * @param header
     */
    public FileBands(Segment segment) {
        super(segment);
        this.cpUTF8 = segment.getCpBands().getCpUTF8();
    }

    /* (non-Javadoc)
     * @see org.apache.harmony.pack200.BandSet#pack(java.io.OutputStream)
     */
    public void pack(OutputStream outputStream) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.harmony.pack200.BandSet#unpack(java.io.InputStream)
     */
    public void unpack(InputStream in) throws IOException,
            Pack200Exception {
        int numberOfFiles = header.getNumberOfFiles();
        SegmentOptions options = header.getOptions();
        if (false && System.getProperty("debug.pack200") != null) {
            // TODO HACK
            fileSize = new long[numberOfFiles];
            fileModtime = new long[numberOfFiles];
            fileOptions = new long[numberOfFiles];
            fileName = new String[numberOfFiles];
            Arrays.fill(fileName, "");
            return;
        }
        long last;
        fileName = parseReferences("file_name", in, Codec.UNSIGNED5,
                numberOfFiles, cpUTF8);
        fileSize = new long[numberOfFiles];
        if (options.hasFileSizeHi()) {
            last = 0;
            for (int i = 0; i < numberOfFiles; i++) {
                fileSize[i] = (last = Codec.UNSIGNED5.decode(in, last)) << 32;
            }
        }
        last = 0;
        for (int i = 0; i < numberOfFiles; i++) {
            fileSize[i] |= (last = Codec.UNSIGNED5.decode(in, last));
        }
        fileModtime = new long[numberOfFiles];
        if (options.hasFileModtime()) {
            last = 0;
            for (int i = 0; i < numberOfFiles; i++) {
                fileModtime[i] |= (last = Codec.DELTA5.decode(in, last));
            }
        }
        fileOptions = new long[numberOfFiles];
        if (options.hasFileOptions()) {
            last = 0;
            for (int i = 0; i < numberOfFiles; i++) {
                fileOptions[i] |= (last = Codec.UNSIGNED5.decode(in, last));
            }
        }
    }
    

    public void processFileBits(InputStream in) throws IOException,
            Pack200Exception {
        // now read in the bytes
        int numberOfFiles = header.getNumberOfFiles();
        fileBits = new byte[numberOfFiles][];
        for (int i = 0; i < numberOfFiles; i++) {
            int size = (int) fileSize[i];
            // TODO This buggers up if file_size > 2^32. Probably an array is
            // not the right choice, and
            // we should just serialize the bugger here?
            fileBits[i] = new byte[size];
            for (int j = 0; j < size; j++) {
                fileBits[i][j] = (byte) Codec.BYTE1.decode(in);
            }
        }
    }

    public byte[][] getFileBits() {
        return fileBits;
    }

    public long[] getFileModtime() {
        return fileModtime;
    }

    public String[] getFileName() {
        return fileName;
    }

    public long[] getFileOptions() {
        return fileOptions;
    }

    public long[] getFileSize() {
        return fileSize;
    }

}