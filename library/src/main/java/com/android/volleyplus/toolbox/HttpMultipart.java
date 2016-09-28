/*
 * Copyright (c) 2016 Liujian.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.volleyplus.toolbox;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * It only designed for upload images, not any other kind of files,
 * cause the subtype is only image/* {@link #getMimeTypeForImage(File)}
 * but this class can extend in the future.
 * Known more about MultipartEntity
 * @see <a href="http://stackoverflow.com/questions/16958448/what-is-http-multipart-request"></a>
 * @see <a href="http://stackoverflow.com/questions/32240177/working-post-multipart-request-with-volley-and-without-httpentity"/>
 *
 * Created by liujian (xiaojianmailbox@gmail.com) on 2016/9/28 15:07.
 */
public class HttpMultipart{
    private static final String TAG = "HttpMultipart";
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final String CR_LF = "\r\n";
    private static final String TWO_DASHES = "--";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private final String boundary;
    private final String charset;
    private final Map<String, String> stringParts;
    private final Map<String, File> imageParts;

    private final int MAX_FIXED_SIZE = 5 * 1024 * 1024;

    public HttpMultipart(String charset, String boundary){
        if(boundary == null){
            throw new IllegalArgumentException("Multipart boundary may not be null");
        }else{
            this.charset = charset;
            this.boundary = boundary;
            this.stringParts = new HashMap<>();
            this.imageParts = new HashMap<>();
        }
    }

    public HttpMultipart(String boundary){
        this(DEFAULT_CHARSET, boundary);
    }

    public HttpMultipart(){
        this(DEFAULT_CHARSET, generateBoundary());
    }


    public String getBoundary(){
        return this.boundary;
    }

    public String getCharset(){
        return this.charset;
    }

    public Map<String, String> getStringParts(){
        return this.stringParts;
    }

    public Map<String, File> getImageParts(){
        return this.imageParts;
    }

    /**
     * Add one key-value into the {@link #stringParts}
     * @param key
     * @param value
     */
    public void addStringPart(String key, String value){
        if(key != null && value != null){
            this.stringParts.put(key, value);
        }
    }

    public void addAllStringParts(Map<String, String> parts){
        if(parts != null && parts.size() > 0){
            this.stringParts.putAll(parts);
        }
    }

    /**
     * Add a image into the {@link #imageParts}
     * @param key
     * @param file
     */
    public void addImagePart(String key, File file){
        if(key != null && file != null){
            this.imageParts.put(key, file);
        }
    }

    public void addAllImageParts(Map<String, File> parts){
        if(parts != null && parts.size() > 0){
            this.imageParts.putAll(parts);
        }
    }


    /**
     * Write the mutilpart body to http connection
     * @param connection HttpUrlConnection {@link HttpURLConnection}
     * @throws IOException
     */
    public void doWriteTo(HttpURLConnection connection) throws IOException {
        DataOutputStream requestData = null;

        try{
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            // Set the Http headers
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", generateContentType(this.boundary, this.charset));

            OutputStream output = connection.getOutputStream();
            requestData = new DataOutputStream(output);

            if(this.stringParts.size() > 0){
                for(Map.Entry<String, String> entry : stringParts.entrySet()){
                    writeStringPart(requestData, entry.getKey(), entry.getValue());
                }
            }

            if(this.imageParts.size() > 0){
                for(Map.Entry<String, File> entry : imageParts.entrySet()){
                    writeFilePart(requestData, entry.getKey(), entry.getValue());
                }
            }

            requestData.writeBytes(TWO_DASHES + boundary + TWO_DASHES + CR_LF);
            requestData.flush();

        }finally {
            if(requestData != null){
                requestData.close();
            }
        }
    }


    /**
     * Generate a content type
     * @param boundary boundry
     * @param charset
     * @return
     */
    protected String generateContentType(String boundary, String charset) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("multipart/form-data; boundary=");
        buffer.append(boundary);
        if(charset != null) {
            buffer.append("; charset=");
            buffer.append(charset);
        }

        return buffer.toString();
    }


    /**
     * Returns a rondom boundary
     */
    private static String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30;

        for(int i = 0; i < count; ++i) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }

        return buffer.toString();
    }


    /**
     *  Write parameter name-value to output stream
     * @param outputStream  {@link DataOutputStream}
     * @param parameterName parameter's key
     * @param parameterValue parameter's value
     * @throws IOException
     */
    protected void writeStringPart(DataOutputStream outputStream, String parameterName, String parameterValue) throws IOException {
        outputStream.writeBytes(TWO_DASHES + boundary + CR_LF);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + CR_LF);
        outputStream.writeBytes("Content-Type: text/plain; charset=" + this.charset + CR_LF);
        outputStream.writeBytes(CR_LF);
        outputStream.writeBytes(parameterValue + CR_LF);
    }


    /**
     * Write file to output stream
     * @param outputStream {@link DataOutputStream}
     * @param paramerName   file name
     * @param file          {@link File} file to upload
     */
    protected void writeFilePart(DataOutputStream outputStream, String paramerName, File file) throws IOException {
        outputStream.writeBytes(TWO_DASHES + boundary + CR_LF);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + paramerName + "\"; filename=\"" + file.getName() + "\"" + CR_LF);
        outputStream.writeBytes("Content-Type: " + getMimeTypeForImage(file) + CR_LF);
        outputStream.writeBytes(CR_LF);

        FileInputStream fileInputStream = new FileInputStream(file);
        int bytesRead;
        byte[] buffer = new byte[8192];
        while((bytesRead = fileInputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.writeBytes(CR_LF);
    }


    /**
     * Get the Mime type form a image name such as "xxxxx.png".
     * This method is not always reliable, maybe a better one is needed in future.
     * @param image
     * @return
     */
    protected String getMimeTypeForImage(File image){
        String name = image.getName();
        return "image/" + name.substring(name.lastIndexOf(".") + 1);
    }

}
