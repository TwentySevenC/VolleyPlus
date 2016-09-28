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

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Bitmap cache
 *
 * Created by liujian (xiaojianmailbox@gmail.com) on 2016/9/28 15:10.
 */
public class LruBitmapCache extends LruCache<String, Bitmap>
		implements ImageLoader.ImageCache{

	private static int DEFAULT_CACHE_SIZE = getDefaultCacheSize();

	/**
	 * @param maxSize for caches that do not override {@link #sizeOf}, this is
	 *                the maximum number of entries in the cache. For all other caches,
	 *                this is the maximum sum of the sizes of the entries in this cache.
	 */
	public LruBitmapCache(int maxSize) {
		super(maxSize);
	}

	public LruBitmapCache(){
		this(DEFAULT_CACHE_SIZE);
	}

	public LruBitmapCache(Context ctx){
		this(getCacheSize(ctx));
	}


	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}


	// Returns a cache size equal to approximately two screens worth of images.
	public static int getCacheSize(Context context){
		final DisplayMetrics displayMetrics = context.getResources().
				getDisplayMetrics();
		final int screenWidth = displayMetrics.widthPixels;
		final int screenHeight = displayMetrics.heightPixels;
		// 4 bytes per pixel
		final int screenBytes = screenWidth * screenHeight * 4;

		return screenBytes * 2;
	}


	//Default cache size, maxMemory / 16
	public static int getDefaultCacheSize(){
		final int maxMemory = (int)Runtime.getRuntime().maxMemory();

		final int cacheSize = maxMemory / 16;

		return cacheSize;
	}
}
