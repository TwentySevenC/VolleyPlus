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

package com.android.volleyplus;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The Base request class for uploading images. if you want to upload images to a server,
 * you need to define a request which extending this class.
 *
 * Created by liujian (xiaojianmailbox@gmail.com) on 2016/9/28 15:06.
 */
public abstract class MultipartRequest<T> extends BaseRequest<T>{

    /* To hold the parameter name and the images to upload */
    private Map<String,File> fileUploads;

    /* To hold the parameter name and the string content to upload */
    private Map<String,String> stringUploads;

    /**
     *
     * @param imageUploads
     * @param stringUploads
     * @param listener
     */
    public MultipartRequest( Map<String, File> imageUploads, Map<String, String> stringUploads,
                             ResponseListener<T> listener) {
        super(Request.Method.POST, listener);
        this.fileUploads = imageUploads;
        this.stringUploads = stringUploads;
    }


    /**
     *
     * @param listener
     */
    public MultipartRequest(ResponseListener<T> listener){
        super(Request.Method.POST, listener);
        this.fileUploads = new HashMap<>();
        this.stringUploads = new HashMap<>();
    }


    /**
     *
     * @param imageName the iamge's name such as "xxxxx.jpg"
     * @param image
     */
    public void addFileUpload(String imageName,File image) {
        fileUploads.put(imageName, image);
    }

    public void addStringUpload(String param,String content) {
        stringUploads.put(param,content);
    }

    /**
     * Set upload name-value parameter
     * @param map
     */
    public void setStringUpload(Map<String, String> map){
        this.stringUploads = map;
    }


    /**
     * Set upload files
     * @param map
     */
    public void setFileUpload(Map<String, File> map){
        this.fileUploads = map;
    }

    /**
     * Files to upload
     */
    public Map<String,File> getFileUploads() {
        return fileUploads;
    }

    /**
     * Parameters to upload
     */
    public Map<String,String> getStringUploads() {
        return stringUploads;
    }


    @Override
    protected Map<String, String> getPostParams() throws AuthFailureError {
        return null;
    }

    @Override
    protected Map<String, String> getQueryParams() throws AuthFailureError {
        return null;
    }


    /**
     * Set the priority to low
     */
    @Override
    public Request.Priority getPriority() {
        return Request.Priority.LOW;
    }


    @Override
    public String toString(){
        String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
        String headers = null;
        String body = null;
        String stringParts = null;
        String imageParts = null;

        try{

            if (getHeaders() != null) {
                headers = getHeaders().toString();
            }

            if (getParams() != null) {
                body = getParams().toString();
            }

            if(getStringUploads() != null){
                stringParts = getStringUploads().toString();
            }

            if(getFileUploads() != null){
                imageParts = getFileUploads().toString();
            }

        }catch (AuthFailureError authFailureError){
            authFailureError.printStackTrace();
        }

        return "[ ] " + getUrl() + " " + trafficStatsTag + " "
                + getPriority()  + " " + "[headers]" + " " + headers + " " + "[body]" +
                " " + body + "[stringPart]" + " " + stringParts + "[imagePart]" + " " + imageParts;
    }
}
