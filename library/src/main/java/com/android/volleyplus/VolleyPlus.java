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

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


/**
 * Singleton
 *
 * Created by liujian (xiaojianmailbox@gmail.com) on 2016/9/28 15:12.
 */
public class VolleyPlus {

	private boolean isInitialized = false;

	private static VolleyPlus sInstance;

	private RequestQueue requestQueue;

	private ImageLoader  imageLoader;

	private String baseUrl;

	private VolleyPlus(){}


	public static VolleyPlus getInstance(){
		if(null == sInstance){
			sInstance = new VolleyPlus();
		}
		return sInstance;
	}

	/**
	 * Force {@link #getInstance()} to create a new instance, when called in the next time.
	 */
	public static void destroyedInstance(){
		sInstance = null;
	}



	public void init(Context context, VolleyConfiguration configuration){
		this.baseUrl = configuration.baseUrl;
		requestQueue = Volley.newRequestQueue(context, configuration.httpStack);
		imageLoader = new ImageLoader(requestQueue, configuration.imageCache);
		isInitialized = true;
	}


	/**
	 * Get the base url
	 */
	public String getBaseUrl(){
		return this.baseUrl;
	}


	/**
	 * Get the request queue
	 */
	public RequestQueue getRequestQueue(){
		checkInit();
		return requestQueue;
	}


	/**
	 * Do a request
	 * @param request
	 */
	public <T> void  enqueue(Request<T> request){
		checkInit();
		requestQueue.add(request);
	}


	/**
	 * Cancel a request
	 * @param tag   request's tag
	 */
	public void cancel(Object tag){
		checkInit();
		if(tag != null){
			requestQueue.cancelAll(tag);
		}
	}


	/**
	 * Cancel all request
	 */
	public void cancelAll(){
		checkInit();
		requestQueue.cancelAll(new RequestQueue.RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return true;
			}
		});
	}


	private void checkInit(){
		if(!isInitialized){
			throw new IllegalStateException("VolleyPlus must be init before using");
		}
	}


	/**
	 * Get the image loader
	 */
	public ImageLoader getImageLoader(){
		checkInit();
		return imageLoader;
	}


	/**
	 * Load image and show
	 * @param requestUrl           image request url
	 * @param imageView           {@link ImageView} view
	 * @param defaultImageResId   Default image resource ID to use, or 0 if it doesn't exist.
	 * @param errorImageResId     Error image resource ID to use, or 0 if it doesn't exist.
	 */
	public Bitmap loadImage(String requestUrl, ImageView imageView, int defaultImageResId, int errorImageResId){
		checkInit();
		ImageLoader.ImageContainer imageContainer = imageLoader.get(requestUrl,
				ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId));
		return imageContainer.getBitmap();
	}


	/**
	 * Load image and show
	 * @param requestUrl  image request url
	 * @param imageView   {@link ImageView} view
	 */
	public Bitmap loadImage(String requestUrl, ImageView imageView){
		return loadImage(requestUrl, imageView, 0, 0);
	}


	/**
	 * Issues a bitmap request with the given URL if that image is not available
	 * in the cache, and returns a bitmap that contains all of the data
	 * relating to the request (as well as the default image if the requested
	 * image is not available).
	 * @param requestUrl The url of the remote image
	 * @param imageListener The listener to call when the remote image is loaded
	 * @param maxWidth The maximum width of the returned image.
	 * @param maxHeight The maximum height of the returned image.
	 * @param scaleType The ImageViews ScaleType used to calculate the needed image size.
	 * @return A container object that contains all of the properties of the request, as well as
	 *     the currently available image (default if remote is not loaded).
	 */
	public Bitmap loadImage(String requestUrl, ImageLoader.ImageListener imageListener,
	                        int maxWidth, int maxHeight, ImageView.ScaleType scaleType){
		ImageLoader.ImageContainer imageContainer = imageLoader.get(requestUrl, imageListener, maxWidth, maxHeight, scaleType);

		return imageContainer.getBitmap();
	}

}
