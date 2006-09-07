/* Copyright 2004 The Apache Software Foundation or its licensors, as applicable
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package java.util.logging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

/**
 * A <code>Handler</code> writes description of logging event into a specified
 * file or a rotating set of files.
 * <p>
 * If a set of files are used, when a given amount of data has been written to
 * one file, this file is closed, and another file is opened. The name of these
 * files are generated by given name pattern, see below for details.
 * </p>
 * <p>
 * By default the IO buffering mechanism is enabled, but when each log record is
 * complete, it is flushed out.
 * </p>
 * <p>
 * <code>XMLFormatter</code> is default formatter for <code>FileHandler</code>.
 * </p>
 * <p>
 * <code>MemoryHandler</code> will read following <code>LogManager</code>
 * properties for initialization, if given properties are not defined or has
 * invalid values, default value will be used.
 * <ul>
 * <li>java.util.logging.FileHandler.level specifies the level for this
 * <code>Handler</code>, defaults to <code>Level.ALL</code>.</li>
 * <li>java.util.logging.FileHandler.filter specifies the <code>Filter</code>
 * class name, defaults to no <code>Filter</code>.</li>
 * <li>java.util.logging.FileHandler.formatter specifies the
 * <code>Formatter</code> class, defaults to
 * <code>java.util.logging.XMLFormatter</code>.</li>
 * <li>java.util.logging.FileHandler.encoding specifies the character set
 * encoding name, defaults to the default platform encoding.</li>
 * <li>java.util.logging.FileHandler.limit specifies an maximum bytes to write
 * to any one file, defaults to zero, which means no limit.</li>
 * <li>java.util.logging.FileHandler.count specifies how many output files to
 * rotate, defaults to 1.</li>
 * <li>java.util.logging.FileHandler.pattern specifies name pattern for the
 * output files. See below for details. Defaults to "%h/java%u.log".</li>
 * <li>java.util.logging.FileHandler.append specifies whether this
 * <code>FileHandler</code> should append onto existing files, defaults to
 * false.</li>
 * </ul>
 * </p>
 * <p>
 * Name pattern is a string that may includes some special sub-strings, which
 * will be replaced to generate output files:
 * <ul>
 * <li>"/" represents the local pathname separator</li>
 * <li>"%t" represents the system temporary directory</li>
 * <li>"%h" represents the home directory of current user, which is specified
 * by "user.home" system property</li>
 * <li>"%g" represents the generation number to distinguish rotated logs</li>
 * <li>"%u" represents a unique number to resolve conflicts</li>
 * <li>"%%" represents percent sign character '%'</li>
 * </ul>
 * </p>
 * Normally, the generation numbers are not larger than given file count and
 * follow the sequence 0, 1, 2.... If the file count is larger than one, but the
 * generation field("%g") has not been specified in the pattern, then the
 * generation number after a dot will be added to the end of the file name,
 * </p>
 * <p>
 * The "%u" unique field is used to avoid conflicts and set to 0 at first. If
 * one <code>FileHandler</code> tries to open the filename which is currently
 * in use by another process, it will repeatedly increment the unique number field
 * and try again. If the "%u" component has not been included in the file name
 * pattern and some contention on a file does occur then a unique numerical
 * value will be added to the end of the filename in question immediately to the
 * right of a dot. The unique IDs for avoiding conflicts is only guaranteed to work
 * reliably when using a local disk file system.
 * </p>
 * 
 */
public class FileHandler extends StreamHandler {

    /*
     * ---------------------------------------------
     * constants
     * ---------------------------------------------
     */
    private static final int DEFAULT_COUNT = 1;

    private static final int DEFAULT_LIMIT = 0;

    private static final boolean DEFAULT_APPEND = false;

    private static final String DEFAULT_PATTERN = "%h/java%u.log"; //$NON-NLS-1$

    /*
     * ---------------------------------------------
     * class variables
     * ---------------------------------------------
     */
    //maintain all file locks hold by this process
    private static Hashtable<String, FileLock> allLocks = new Hashtable<String, FileLock>();

    /*
     * ---------------------------------------------
     * instance variables
     * ---------------------------------------------
     */

    //the count of files which the output cycle through
    private int count;

    //the size limitation in byte of log file
    private int limit;

    //whether the FileHandler should open a existing file for output in append mode
    private boolean append;

    //the pattern for output file name
    private String pattern;

    //maintain a LogManager instance for convenience
    private LogManager manager;

    //output stream, which can measure the output file length
    private MeasureOutputStream output;

    //used output file
    private File[] files;

    //output file lock
    FileLock lock = null;

    //current output file name
    String fileName = null;
    
    //current unique ID
    int uniqueID = -1;

    /*
     * ---------------------------------------------
     * constructors
     * ---------------------------------------------
     */
    /**
     * Construct a <code>FileHandler</code> using <code>LogManager</code> 
     * properties or their default value
     * 
     * @throws IOException			
     * 				if any IO exception happened
     * @throws SecurityException	
     * 				if security manager exists and it determines that caller 
     * 				does not have the required permissions to control this handler,
     * 				required permissions include <code>LogPermission("control")</code>
     * 				and other permission like <code>FilePermission("write")</code>, 
     * 				etc.
     * 								
     */
    public FileHandler() throws IOException {
        init(null, null, null, null);
    }

    //init properties
    private void init(String p, Boolean a, Integer l, Integer c) throws IOException{
        //check access
        manager = LogManager.getLogManager();
        manager.checkAccess();
        initProperties(p, a, l, c);
        initOutputFiles();
    }

    private void initOutputFiles() throws FileNotFoundException, IOException {
        FileOutputStream fileStream = null;
        FileChannel channel = null;
        while (true) {
            //try to find a unique file which is not locked by other process
            uniqueID++;
            //FIXME: improve performance here
            for (int generation = 0; generation < count; generation++) {
                //cache all file names for rotation use
                files[generation] = new File(parseFileName(generation, uniqueID));
            }
            fileName = files[0].getAbsolutePath();
            synchronized (allLocks) {
                //if current process has held lock for this fileName
                //continue to find next file
                if (null != allLocks.get(fileName)) {
                    continue;
                }
                if(files[0].exists() && (!append || files[0].length() >= limit)){
                    for (int i = count - 1; i > 0; i--) {
                        if (files[i].exists()) {
                            files[i].delete();
                        }
                        files[i - 1].renameTo(files[i]);
                    }
                }
                fileStream = new FileOutputStream(fileName, append);
                channel = fileStream.getChannel();
                /*
                 * if lock is unsupported and IOException thrown, just let the
                 * IOException throws out and exit otherwise it will go into an
                 * undead cycle
                 */
                lock = channel.tryLock();
                if (null == lock) {
                    try{
                        fileStream.close();
                    }catch(Exception e){
                        //ignore
                    }
                    continue;
                }
				allLocks.put(fileName, lock);
				break;
            }
        }
        output = new MeasureOutputStream(new BufferedOutputStream(fileStream),
                files[0].length());
        setOutputStream(output);
    }

    private void initProperties(String p, Boolean a, Integer l, Integer c) {
        super.initProperties("ALL", null, "java.util.logging.XMLFormatter",  //$NON-NLS-1$//$NON-NLS-2$
                null);
        String className = this.getClass().getName();
        pattern = (null == p) ? getStringProperty(className + ".pattern", //$NON-NLS-1$
                DEFAULT_PATTERN) : p;
        if (null == pattern || "".equals(pattern)) { //$NON-NLS-1$
            throw new NullPointerException("Pattern cannot be empty"); //$NON-NLS-1$
        }
        append = (null == a) ? getBooleanProperty(className + ".append", //$NON-NLS-1$
                DEFAULT_APPEND) : a.booleanValue();
        count = (null == c) ? getIntProperty(className + ".count", //$NON-NLS-1$
                DEFAULT_COUNT) : c.intValue();
        limit = (null == l) ? getIntProperty(className + ".limit", //$NON-NLS-1$
                DEFAULT_LIMIT) : l.intValue();
        count = count < 1 ? DEFAULT_COUNT : count;
        limit = limit < 0 ? DEFAULT_LIMIT : limit;
        files = new File[count];
    }

    private void findNextGeneration() {
        super.close();
        for (int i = count - 1; i > 0; i--) {
            if (files[i].exists()) {
                files[i].delete();
            }
            files[i - 1].renameTo(files[i]);
        }
        try {
            output = new MeasureOutputStream(new BufferedOutputStream(
                    new FileOutputStream(files[0])));
        } catch (FileNotFoundException e1) {
            this.getErrorManager().error("Error happened when open log file.", //$NON-NLS-1$
                    e1, ErrorManager.OPEN_FAILURE);
        }
        setOutputStream(output);
    }

    /**
     *  Transform the pattern to the valid file name, replacing
     *  any patterns, and applying generation and uniqueID if
     *  present
     *
     *  @param gen generation of this file
     *  @param uniqueID distinguishing id of this file
     *  @return transformed filename ready for use
     */
    private String parseFileName(int gen, int uniqueID) {
        int cur = 0;
        int next = 0;
        boolean hasUniqueID = false;
        boolean hasGeneration = false;

        //TODO privilege code? 

        String tempPath = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
        boolean tempPathHasSepEnd = (tempPath == null ? false : tempPath.endsWith(File.separator));

        String homePath = System.getProperty("user.home"); //$NON-NLS-1$
        boolean homePathHasSepEnd = (homePath == null ? false : homePath.endsWith(File.separator));

        StringBuffer sb = new StringBuffer();
        pattern = pattern.replace('/', File.separatorChar);

        char[] value = pattern.toCharArray();
        while ((next = pattern.indexOf('%', cur)) >= 0) {
            if (++next < pattern.length()) {
                switch (value[next]) {
                case 'g':
                    sb.append(value, cur, next - cur - 1).append(gen);
                    hasGeneration = true;
                    break;
                case 'u':
                    sb.append(value, cur, next - cur - 1).append(uniqueID);
                    hasUniqueID = true;
                    break;
                case 't':
                    /*
                     *  we should probably try to do something cute here like
                     *  lookahead for adjacent '/'
                     */
                    sb.append(value, cur, next - cur - 1).append(tempPath);
                    if (!tempPathHasSepEnd) {
                        sb.append(File.separator);
                    }
                    break;
                case 'h':
                    sb.append(value, cur, next - cur - 1).append(homePath);
                    if (!homePathHasSepEnd){
                        sb.append(File.separator);
                    }
                    break;
                case '%':
                    sb.append(value, cur, next - cur - 1).append('%');
                    break;
                default:
                    sb.append(value, cur, next - cur);
                }
                cur = ++next;
            } else {
                // fail silently
            }
        }

        sb.append(value, cur, value.length - cur);

        if (!hasGeneration && count > 1) {
            sb.append(".").append(gen); //$NON-NLS-1$
        }

        if (!hasUniqueID && uniqueID > 0) {
            sb.append(".").append(uniqueID); //$NON-NLS-1$
        }

        return sb.toString();
    }

    //get boolean LogManager property, if invalid value got, using default value
    private boolean getBooleanProperty(String key, boolean defaultValue) {
        String property = manager.getProperty(key);
        if (null == property) {
            return defaultValue;
        }
        boolean result = defaultValue;
        if ("true".equalsIgnoreCase(property)) { //$NON-NLS-1$
            result = true;
        } else if ("false".equalsIgnoreCase(property)) { //$NON-NLS-1$
            result = false;
        }
        return result;
    }

    //get String LogManager property, if invalid value got, using default value
    private String getStringProperty(String key, String defaultValue) {
        String property = manager.getProperty(key);
        return property == null ? defaultValue : property;
    }

    //get int LogManager property, if invalid value got, using default value
    private int getIntProperty(String key, int defaultValue) {
        String property = manager.getProperty(key);
        int result = defaultValue;
        if (null != property) {
            try {
                result = Integer.parseInt(property);
            } catch (Exception e) {//ignore
            }
        }
        return result;
    }

    /**
     * Construct a <code>FileHandler</code>, the given name pattern is used as
     * output filename, the file limit is set to zero(no limit), and the file 
     * count is set to one, other configuration using <code>LogManager</code> 
     * properties or their default value
     * 
     * This handler write to only one file and no amount limit.
     *
     * @param  pattern
     * 				the name pattern of output file 
     * @throws IOException			
     * 				if any IO exception happened
     * @throws SecurityException	
     * 				if security manager exists and it determines that caller 
     * 				does not have the required permissions to control this handler,
     * 				required permissions include <code>LogPermission("control")</code>
     * 				and other permission like <code>FilePermission("write")</code>, 
     * 				etc.
     * 								
     */
    public FileHandler(String pattern) throws IOException {
        if(null == pattern){ 
            throw new NullPointerException("Pattern cannot be empty"); //$NON-NLS-1$
        }
        if("".equals(pattern)){
            throw new IllegalArgumentException();
        }
        init(pattern, null, new Integer(DEFAULT_LIMIT), new Integer(
                DEFAULT_COUNT));
    }

    /**
	 * Construct a <code>FileHandler</code>, the given name pattern is used
	 * as output filename, the file limit is set to zero(i.e. no limit applies),
	 * the file count is initialized to one, and the value of
	 * <code>append</code> becomes the new instance's append mode. Other
	 * configuration is done using <code>LogManager</code> properties.
	 * 
	 * This handler write to only one file and no amount limit.
	 * 
	 * @param pattern
	 *            the name pattern of output file
	 * @param append
	 *            the append mode
	 * @throws IOException
	 *             if any IO exception happened
	 * @throws SecurityException
	 *             if security manager exists and it determines that caller does
	 *             not have the required permissions to control this handler,
	 *             required permissions include
	 *             <code>LogPermission("control")</code> and other permission
	 *             like <code>FilePermission("write")</code>, etc.
	 * 
	 */
    public FileHandler(String pattern, boolean append) throws IOException {
        if(null == pattern || "".equals(pattern)){ //$NON-NLS-1$
            throw new NullPointerException("Pattern cannot be empty"); //$NON-NLS-1$
        }        
        init(pattern, Boolean.valueOf(append), new Integer(DEFAULT_LIMIT),
                new Integer(DEFAULT_COUNT));
    }

    /**
     * Construct a <code>FileHandler</code>, the given name pattern is used as
     * output filename, the file limit is set to given limit argument, and 
     * the file count is set to given count argument, other configuration using 
     * <code>LogManager</code> properties  or their default value
     * 
     * This handler is configured to write to a rotating set of count files, 
     * when the limit of bytes has been written to one output file, another file 
     * will be opened instead.  
     *
     * @param  pattern
     * 				the name pattern of output file
     * @param  limit
     * 				the data amount limit in bytes of one output file, cannot less
     * 				than one
     * @param  count
     * 				the maximum number of files can be used, cannot less than one 
     * @throws IOException			
     * 				if any IO exception happened
     * @throws SecurityException	
     * 				if security manager exists and it determines that caller 
     * 				does not have the required permissions to control this handler,
     * 				required permissions include <code>LogPermission("control")</code>
     * 				and other permission like <code>FilePermission("write")</code>, 
     * 				etc.
     * @throws IllegalArgumentException
     * 				if count<1, or limit<0 								
     */
    public FileHandler(String pattern, int limit, int count) throws IOException {
        if(null == pattern || "".equals(pattern)){ //$NON-NLS-1$
            throw new NullPointerException("Pattern cannot be empty"); //$NON-NLS-1$
        }        
        if (limit < 0 || count < 1) {
            throw new IllegalArgumentException(
                    "The limit and count property must larger than 0 and 1, respectively"); //$NON-NLS-1$
        }
        init(pattern, null, new Integer(limit), new Integer(count));
    }

    /**
     * Construct a <code>FileHandler</code>, the given name pattern is used as
     * output filename, the file limit is set to given limit argument, the file 
     * count is set to given count argument, and the append mode is set to given
     * append argument, other configuration using <code>LogManager</code> 
     * properties  or their default value
     * 
     * This handler is configured to write to a rotating set of count files, 
     * when the limit of bytes has been written to one output file, another file 
     * will be opened instead. 
     * 
     * @param  pattern
     * 				the name pattern of output file
     * @param  limit
     * 				the data amount limit in bytes of one output file, cannot less
     * 				than one
     * @param  count
     * 				the maximum number of files can be used, cannot less than one 
     * @param  append
     * 				the append mode
     * @throws IOException			
     * 				if any IO exception happened
     * @throws SecurityException	
     * 				if security manager exists and it determines that caller 
     * 				does not have the required permissions to control this handler,
     * 				required permissions include <code>LogPermission("control")</code>
     * 				and other permission like <code>FilePermission("write")</code>, 
     * 				etc.
     * @throws IllegalArgumentException
     * 				if count<1, or limit<0
     * 								
     */
    public FileHandler(String pattern, int limit, int count, boolean append)
            throws IOException {
        if(null == pattern || "".equals(pattern)){ //$NON-NLS-1$
            throw new NullPointerException("Pattern cannot be empty"); //$NON-NLS-1$
        }        
        if (limit < 0 || count < 1) {
            throw new IllegalArgumentException(
                    "The limit and count property must larger than 0 and 1, respectively"); //$NON-NLS-1$
        }
        init(pattern, Boolean.valueOf(append), new Integer(limit), new Integer(
                count));
    }

    /*
     * ---------------------------------------------
     * Methods overrides StreamHandler
     * ---------------------------------------------
     */
    /**
     * Flush and close all opened files.
     * 
     * @throws SecurityException	
     * 				if security manager exists and it determines that caller 
     * 				does not have the required permissions to control this handler,
     * 				required permissions include <code>LogPermission("control")</code>
     * 				and other permission like <code>FilePermission("write")</code>, 
     * 				etc.
     */
    public void close() {
        //release locks
        super.close();
        allLocks.remove(fileName);
        try {
            lock.release();
        } catch (IOException e) {
            //ignore
        }
    }

    /**
     * Publish a <code>LogRecord</code>
     * 
     * @param record the log record to be published
     */
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
        if (limit > 0 && output.getLength() >= limit) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    findNextGeneration();
                    return null;
                }
            });
        }
    }

    /**
     * This output stream use decorator pattern to add measure feature to OutputStream
     * which can detect the total size(in bytes) of output, the initial size can be set
     */
    class MeasureOutputStream extends OutputStream {

        OutputStream wrapped;

        long length;

        public MeasureOutputStream(OutputStream stream, long currentLength) {
            wrapped = stream;
            length = currentLength;
        }

        public MeasureOutputStream(OutputStream stream) {
            this(stream, 0);
        }

        public void write(int oneByte) throws IOException {
            wrapped.write(oneByte);
            length++;
        }

        public void write(byte[] bytes) throws IOException {
            wrapped.write(bytes);
            length += bytes.length;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            wrapped.write(b, off, len);
            length += len;
        }

        public void close() throws IOException {
            wrapped.close();
        }

        public void flush() throws IOException {
            wrapped.flush();
        }

        public long getLength() {
            return length;
        }

        public void setLength(long newLength) {
            length = newLength;
        }

    }

}


