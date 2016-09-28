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

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 *
 * Created by liujian (xiaojianmailbox@gmail.com) on 2016/9/28 14:51
 */

public abstract class BaseRequest<T> extends Request<T> {

    /**Default charset for encoding**/
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String HEADER_COOKIE = "Cookie";

    /**Http request method**/
    private int method;

    /**Callback interface for https request**/
    private ResponseListener<T> mResponseListener;


    /**
     * @param method Http request method
     * @param listener listener
     */
    public BaseRequest(int method, ResponseListener<T> listener) {
        super(method, null, listener);
        this.method = method;
        mResponseListener = listener;

        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, 1));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    /**
     * Get the url for request, different request need its corresponding url
     * which implemented by {@link #subPath()} extented by the child class
     */
    @Override
    public String getUrl() {
        String url = VolleyPlus.getInstance().getBaseUrl() + subPath();

        if(method == Method.POST){
            return url;
        }else if(method == Method.GET){
            try {
                Map<String, String> queryParams = getQueryParams();
                if(queryParams == null || queryParams.size() < 1){
                    return url;
                }else{
                    return url + "?" + encodeParameters(queryParams, DEFAULT_CHARSET);
                }
            } catch (AuthFailureError authFailureError) {
                authFailureError.printStackTrace();
            }
        }
        return null;
    }


    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    protected String encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    encodedParams.append('&');
                }
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return getPostParams();
    }


    /**
     * Set a callback interface for the request
     * @param listener {@link ResponseListener}
     */
    public void setResponseListener(ResponseListener<T> listener){
        mResponseListener = listener;
    }


    /**
     * Get the callback interface
     * @return a listener {@link ResponseListener}
     */
    public ResponseListener getResponseLisener(){
        return mResponseListener;
    }


    /**
     * Get the related url (excepted the domain)
     */
    protected abstract String subPath();


    /**
     * Returns a Map of parameters to be used for a POST or PUT request.  Can throw {@link AuthFailureError}
     * as authentication may be required to provide these values.
     *
     * @return a key-value map
     *
     * @throws AuthFailureError in the event of auth failure
     */
    protected abstract Map<String, String> getPostParams() throws AuthFailureError;


    /**
     * Returns a Map of parameters to be used for a GET request.  Can throw {@link AuthFailureError}
     * as authentication may be required to provide these values.
     *
     * @return a key-value map
     *
     * @throws AuthFailureError in the event of auth failure
     */
    protected abstract Map<String, String> getQueryParams() throws AuthFailureError;


    /**
     * Subclass must be implement this to parse a json/xml string and return a entity. This method
     * will be called in {@link #parseNetworkResponse}
     * @param json  the row network response data, a json or xml string
     * @return  The parsed entity
     * @throws JSONException
     */
    protected abstract T parseNetworkEntity(String json) throws JSONException;


    @Override
    protected void deliverResponse(T response) {
        mResponseListener.onResponse(response);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response){
        if(VolleyLog.DEBUG){
            NetworkResponse responseLog = response;
            Log.d("VolleyLog", "[Request] " + this.toString() + "\n[Response] " + "statusCode: " +
                    responseLog.statusCode + " \nheaders: " + responseLog.headers.toString() + " " +
                    "\ndata: " + new String(responseLog.data));
        }

        String parsed;
        try{
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e){
            parsed = new String(response.data);
        }

        try{
            T entity = parseNetworkEntity(parsed);
            return Response.success(entity, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e){
            return Response.error(new ParseError(e));
        }
    }


    @Override
    public String toString() {
        String preRequest = super.toString();
        String headers = null;
        String body = null;
        try {

            if (getHeaders() != null) {
                headers = getHeaders().toString();
            }

            if (getParams() != null) {
                body = getParams().toString();
            }
        } catch (AuthFailureError authFailureError) {
            //
        }

        return preRequest + "[headers]" + " " + headers + " " + "[body]" +
                " " + body;
    }



    /**Callback interface for delivering parsed response**/
    public interface ResponseListener<T> extends Response.ErrorListener{
        /**
         * Called when a response is received.
         * @param response a successful response
         */
        void onResponse(T response);
    }

}
