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
import android.util.Log;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * SslSocketFactory
 *
 * Created by liujian (xiaojianmailbox@gmail.com) on 2016/9/28 15:30.
 */
public class SslSocketFactory {

	/**
	 * the password to verify the stored data
	 */
	private static final String CLIENT_TRUST_PASSWORD = "changeit";

	/**
	 * the requested protocol to create a context for.
	 */
	private static final String CLIENT_AGREEMENT = "TLS";

	/**
	 * the name of the requested trust management algorithm.
	 */
	private static final String CLIENT_TRUST_MANAGER = "X509";

	/**
	 * the type of the returned {@code KeyStore}. BKS - BouncyCastle
	 */
	private static final String CLIENT_TRUST_KEYSTORE = "BKS";

	/**
	 * Retuns a sslSocketFactory that allows all hostname verifier
	 */
	public static javax.net.ssl.SSLSocketFactory createIgnoreSSLSocketFactory(){
		try {
			SSLContext context = SSLContext.getInstance(CLIENT_AGREEMENT);
			context.init(null, new TrustManager[]{new IgnoreCertTrustManager()}, null);
			return context.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Get a sslSocketFactory that allows one hostname verifier
	 * @param ctx {@link Context}
	 * @param resId the bks file's resource id
	 */
	public static javax.net.ssl.SSLSocketFactory getSslSocket(Context ctx, int resId){
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
			TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
			KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
			InputStream is= ctx.getResources().openRawResource(resId);
			try {
				tks.load(is,CLIENT_TRUST_PASSWORD.toCharArray());
			}finally {
				is.close();
			}
			//Initialize trust manager
			trustManager.init(tks);
			//Initialize SSLContext
			sslContext.init(null,trustManager.getTrustManagers(),null);

		} catch (Exception e) {
			Log.e("SslContextFactory", e.getMessage());
		}
		return sslContext.getSocketFactory();
	}





	static class IgnoreCertTrustManager implements X509TrustManager {


		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
