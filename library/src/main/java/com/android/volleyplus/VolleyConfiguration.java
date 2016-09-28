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


import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volleyplus.toolbox.LruBitmapCache;

import static com.android.volleyplus.toolbox.Utils.checkNotNull;


/**
 * Volley configuration class
 *
 * Created by liujian (xiaojianmailbox@gmail.com) on 2016/9/28 18:40.
 */
public final class VolleyConfiguration {

	final String baseUrl;
	final HttpStack httpStack;
	final ImageLoader.ImageCache imageCache;


	private VolleyConfiguration(Builder builder){
		baseUrl = builder.baseUrl;
		httpStack = builder.httpStack;
		imageCache = builder.imageCache;
	}


	/**
	 * Build a new {@link VolleyConfiguration}.
	 * <p>
	 * Calling {@link #baseUrl} is required before calling {@link #build()}. All other methods
	 * are optional.
	 */
	public static class Builder {
		private String baseUrl;
		private HttpStack httpStack;
		private ImageLoader.ImageCache imageCache;

		public Builder baseUrl(String baseUrl){
			this.baseUrl = checkNotNull(baseUrl, "baseUrl == null");
			return this;
		}


		public Builder client(HttpStack httpStack){
			this.httpStack = checkNotNull(httpStack, "httpStack == null");
			return this;
		}


		public Builder imageCache(ImageLoader.ImageCache imageCache){
			this.imageCache = checkNotNull(imageCache, "imageCache == null");
			return this;
		}


		/**
		 * Create the {@link VolleyConfiguration} instance using the configured values.
		 *
		 * Note: If either {@link #client(HttpStack)} or {@link #imageCache(ImageLoader.ImageCache)}
		 * is called, default {@link HttpStack} or {@link ImageLoader.ImageCache} will be created and
		 * used.
		 */
		public VolleyConfiguration build(){
			this.initEmptyFieldsWithDefaultValues();
			return new VolleyConfiguration(this);
		}


		private void initEmptyFieldsWithDefaultValues(){

			if(baseUrl == null){
				throw new IllegalArgumentException("Base URL required.");
			}

			if(httpStack == null){
				httpStack = new HurlStack();
			}

			if(imageCache == null){
				imageCache = new LruBitmapCache();
			}
		}
	}

}
