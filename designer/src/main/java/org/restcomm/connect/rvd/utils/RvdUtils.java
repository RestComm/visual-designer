package org.restcomm.connect.rvd.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.restcomm.connect.rvd.RvdContext;
import org.restcomm.connect.rvd.exceptions.RvdException;
import org.restcomm.connect.rvd.exceptions.StreamDoesNotFitInFile;
import org.restcomm.connect.rvd.model.client.WavItem;

import javax.servlet.http.HttpServletRequest;


public class RvdUtils {

    private static final int TEMP_DIR_ATTEMPTS = 10000;
    private static final int STREAM_COPY_BUFFER_SIZE = 1024;

    public RvdUtils() {
        // TODO Auto-generated constructor stub
    }

    public static File createTempDir() throws RvdException {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
          File tempDir = new File(baseDir, baseName + counter);
          if (tempDir.mkdir()) {
            return tempDir;
          }
        }
        throw new RvdException("Failed to create directory within "
            + TEMP_DIR_ATTEMPTS + " attempts (tried "
            + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
      }

    public static boolean isEmpty( String value) {
        if ( value == null || "".equals(value) )
            return true;
        return false;
    }

    // returns True when either the value is True OR null
    public static boolean isEmpty( Boolean value) {
        if ( value == null || value == false )
            return true;
        return false;
    }

    public static boolean safeEquals(String value1, String value2) {
        return value1 == null ? (value1 == value2) : (value1.equals(value2));
    }

    /**
     * Returns false if _value_ is null or false. True otherwise.
     *
     * @param booleanValue
     * @return
     */
    public static boolean isTrue( Boolean booleanValue) {
        if (booleanValue != null && booleanValue)
            return true;
        return false;
    }

    /**
     * Reduces Map<String,String[]> HttpServletRequest.getParameters() map multivalue parameters to
     * single value ones. It does this by keeping only the first array item.
     * @param requestMap
     * @return The parsed Map<String, String>
     */
    public static Map<String,String> reduceHttpRequestParameterMap(Map<String,String[]> requestMap) {
        Map<String,String> reducedMap = new HashMap<String,String>();
        for ( Entry<String,String[]> entry : requestMap.entrySet()) {
            reducedMap.put(entry.getKey(), entry.getValue()[0]); // parameter arrays should have at least one value
        }
        return reducedMap;
    }

    public static String buildHttpAuthorizationToken(String username, String password) {
        byte[] usernamePassBytes = (username + ":" + password).getBytes(Charset.forName("UTF-8"));
        String authenticationToken = Base64.encodeBase64String(usernamePassBytes);
        return authenticationToken;
    }

    public static String myUrlEncode(String value) {
        try {
            // TODO make sure plus characters in the original value are handled correctly
            return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            // TODO issue a warning here
            return value;
        }
    }

    /**
     * Copies data from a stream to a file. If the file already exists it's overwritten.
     * It won't transfer more than max_bytes bytes of data. If that happens the destination file
     * is removed and an exception is thrown.
     *
     * NOTE: Don't forget to close the stream afterwards!
     *
     * @param input
     * @param outputFile
     * @return
     */
    public static int streamToFile(InputStream input, File outputFile, Integer max_bytes) throws IOException, StreamDoesNotFitInFile {
        byte[] buffer = new byte[STREAM_COPY_BUFFER_SIZE];
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        try {
            int copiedSize = 0; // how much is actually written
            //IOUtils.copy(input, outputStream);
            int readCount = input.read(buffer);
            while (readCount > 0) {
                if (max_bytes != null  &&  ((copiedSize + readCount) > max_bytes)) {
                    outputStream.close();
                    FileUtils.deleteQuietly(outputFile);
                    throw new StreamDoesNotFitInFile();
                }
                outputStream.write(buffer, 0, readCount);
                copiedSize += readCount;
                readCount = input.read(buffer);
            }
            return copiedSize;
        } finally {
            outputStream.close();
        }
    }

    /**
     * Makes sure a url or path ends with "/"
     *
     * @param urlOrPath
     * @return
     */
    public static String addTrailingSlashIfMissing(String urlOrPath) {
        if (urlOrPath != null && ! urlOrPath.endsWith("/"))
            urlOrPath += "/";
        return urlOrPath;
    }

    /**
     * Removes the tailing "/" character. In case the path is reduced solely to "/" it does nothing.
     *
     * null or empty paths are returned intact
     *
     * @param urlOrPath
     * @return
     */
    public static String removeTrailingSlashIfPresent(String urlOrPath) {
        if (urlOrPath != null && urlOrPath.length() > 1 && urlOrPath.endsWith("/"))
            urlOrPath = urlOrPath.substring(0,urlOrPath.length()-1);
        return urlOrPath;
    }

    /**
     * Extracts the origin i.e. scheme://host:port section, from an http request object.
     * Note, there is no trailing slash in the resulting string.
     *
     * @param request
     * @return
     */
    public static String getOriginFromRequest(HttpServletRequest request) {
        if (request != null) {
            String origin = request.getScheme() + "://" + request.getServerName();
            int port = request.getServerPort();
            if (port != -1 && port != 80 && port != 443)
                origin += ":" +  port;
            return origin;
        }
        return null;
    }

    /**
     * Returns a WavItem list for all .wav files insude the /audio RVD directory. No project is involved here.
     * @param rvdContext
     * @return
     */
    public static List<WavItem> listBundledWavs(RvdContext rvdContext ) {
        List<WavItem> items = new ArrayList<WavItem>();

        String contextRealPath = RvdUtils.addTrailingSlashIfMissing(rvdContext.getServletContext().getRealPath("/"));
        String audioRealPath = contextRealPath + "audio";
        String contextPath = rvdContext.getServletContext().getContextPath();

        File dir = new File(audioRealPath);
        Collection<File> audioFiles = FileUtils.listFiles(dir, new SuffixFileFilter(".wav"), TrueFileFilter.INSTANCE );
        for (File anyFile: audioFiles) {
            WavItem item = new WavItem();
            String itemRelativePath = anyFile.getPath().substring(contextRealPath.length());
            String presentationName = anyFile.getPath().substring(contextRealPath.length() + "audio".length() );
            item.setUrl( contextPath + "/" + itemRelativePath );
            item.setFilename(presentationName);
            items.add(item);
        }
        return items;
    }
}
