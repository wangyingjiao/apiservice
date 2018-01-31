package com.thinkgem.jeesite.open.send;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.Base64Encoder;
import com.thinkgem.jeesite.common.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HTTPClientUtils {

    public static String postClient(String url, String jsonStr) throws Exception {

		HttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);

		StringEntity requestEntity = new StringEntity(jsonStr, "UTF-8");

		httpPost.setEntity(requestEntity);

		HttpResponse response = httpclient.execute(httpPost);

		HttpEntity responseEntity = response.getEntity();
		
		return EntityUtils.toString(responseEntity);

	}
	
	public static String postClientWithSSL(String url, String jsonStr, Map<String, String> headers, String filePath, String password) throws Exception { //支持增加header
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(filePath), password.toCharArray());
		CloseableHttpClient httpclient = createHttpClientWithCert(keyStore, password,
				null, 200, 5, 1000, 3000, null, 0, null, null);

		HttpPost httpPost = new HttpPost(url);

		StringEntity requestEntity = new StringEntity(jsonStr, "UTF-8");

		httpPost.setEntity(requestEntity);
		if(headers != null){
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}

		HttpResponse response = httpclient.execute(httpPost);

		HttpEntity responseEntity = response.getEntity();

		return EntityUtils.toString(responseEntity);
	}

	public static String postClient(String url, String jsonStr, Map<String, String> headers) throws Exception { //支持增加header
		SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {//信任所有
				return true;
			}
		}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		HttpPost httpPost = new HttpPost(url);

		StringEntity requestEntity = new StringEntity(jsonStr, "UTF-8");

		httpPost.setEntity(requestEntity);
		if(headers != null){
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}

		HttpResponse response = httpclient.execute(httpPost);

		HttpEntity responseEntity = response.getEntity();

		return EntityUtils.toString(responseEntity);
	}
	public static String wmsPostClient(String url, Map<String,Object> paramsMap) throws Exception {
		SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {//信任所有
				return true;
			}
		}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params=new ArrayList<>();
		for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
			params.add(new BasicNameValuePair(String.valueOf(entry.getKey()),String.valueOf(entry.getValue())));
		}
		UrlEncodedFormEntity urlEncodedFormEntity=new UrlEncodedFormEntity(params,"UTF-8");
		httpPost.setEntity(urlEncodedFormEntity);

		HttpResponse response = httpclient.execute(httpPost);

		HttpEntity responseEntity = response.getEntity();

		return EntityUtils.toString(responseEntity);
	}
	public static String getClient(String url) throws Exception {

		HttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpGet);

		HttpEntity responseEntity = response.getEntity();
		
		return EntityUtils.toString(responseEntity);
	}

	public static String getClient(String url, Map<String, String> headers) throws Exception { //增加对header的支持

		HttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			httpGet.setHeader(entry.getKey(), entry.getValue());
		}

		HttpResponse response = httpclient.execute(httpGet);

		HttpEntity responseEntity = response.getEntity();

		return EntityUtils.toString(responseEntity);
	}

    public static String encryptPostClinet(String url, String jsonStr) throws Exception {
		SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {//信任所有
				return true;
			}
		}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

        String body = Base64Encoder.encode(jsonStr).replace("\r", "").replace("\n", "");
        String md5 = MD5Util.getStringMD5(body + Global.getConfig("openEncryptPassword_gasq"));
		
        HttpPost httpPost = new HttpPost(url);

        StringEntity requestEntity = new StringEntity(body, "UTF-8");

        httpPost.setEntity(requestEntity);

        httpPost.addHeader("md5", md5);

        HttpResponse response = httpclient.execute(httpPost);

        HttpEntity responseEntity = response.getEntity();
		return EntityUtils.toString(responseEntity);
    }

	public static String encryptPostClinetUseGivenSecretKey(String url, String jsonStr, String secretKey) throws Exception { //暂时用于小马,使用给定app id，获取secret进行加密
		SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {//信任所有
				return true;
			}
		}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		String body = Base64Encoder.encode(jsonStr).replace("\r", "").replace("\n", "");

		String md5 = MD5Util.getStringMD5(body + secretKey);

		HttpPost httpPost = new HttpPost(url);

		StringEntity requestEntity = new StringEntity(body, "UTF-8");

		httpPost.setEntity(requestEntity);

		httpPost.addHeader("md5", md5);
		httpPost.addHeader("appId", "xmgj");
		HttpResponse response = httpclient.execute(httpPost);

		HttpEntity responseEntity = response.getEntity();
		return EntityUtils.toString(responseEntity);
	}

	public static String encryptPostClinet(String url, String jsonStr, String token) throws Exception {

		String body = Base64Encoder.encode(jsonStr).replace("\r", "").replace("\n", "");
		String md5 = MD5Util.getStringMD5(body + Global.getConfig("openEncryptPassword_gasq"));

		HttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);

		StringEntity requestEntity = new StringEntity(body, "UTF-8");

		httpPost.setEntity(requestEntity);

		httpPost.addHeader("md5", md5);
		httpPost.addHeader("token", token);
		HttpResponse response = httpclient.execute(httpPost);

		HttpEntity responseEntity = response.getEntity();
		return EntityUtils.toString(responseEntity);
	}

	public static String postClinet(String url, String jsonStr) throws Exception {

		String body = jsonStr;

		HttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);

		StringEntity requestEntity = new StringEntity(body, "UTF-8");

		httpPost.setEntity(requestEntity);

		httpPost.setHeader("Content-Type", "application/json");

		HttpResponse response = httpclient.execute(httpPost);

		HttpEntity responseEntity = response.getEntity();

		return EntityUtils.toString(responseEntity);
	}

	public static String postClinetForGapay(String url, String jsonStr, String imei) throws Exception {
		SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {//信任所有
				return true;
			}
		}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		HttpPost httpPost = new HttpPost(url);

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(10000)
				.setConnectionRequestTimeout(10000)
				.setConnectTimeout(10000)
				.build();
		httpPost.setConfig(requestConfig);
		String body = jsonStr;
		StringEntity requestEntity = new StringEntity(body, "UTF-8");
		httpPost.setEntity(requestEntity);
		httpPost.setHeader("imei", imei);
		HttpResponse response = httpclient.execute(httpPost);
		HttpEntity responseEntity = response.getEntity();
		return EntityUtils.toString(responseEntity);
	}

	private static String format(String jsonStr) {
		int level = 0;
		StringBuffer jsonForMatStr = new StringBuffer();
		for (int i = 0; i < jsonStr.length(); i++) {
			char c = jsonStr.charAt(i);
			if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
				jsonForMatStr.append(getLevelStr(level));
			}
			switch (c) {
			case '{':
			case '[':
				jsonForMatStr.append(c + "\n");
				level++;
				break;
			case ',':
				jsonForMatStr.append(c + "\n");
				break;
			case '}':
			case ']':
				jsonForMatStr.append("\n");
				level--;
				jsonForMatStr.append(getLevelStr(level));
				jsonForMatStr.append(c);
				break;
			default:
				jsonForMatStr.append(c);
				break;
			}
		}

		return jsonForMatStr.toString();

	}

	private static String getLevelStr(int level) {
		StringBuffer levelStr = new StringBuffer();
		for (int levelI = 0; levelI < level; levelI++) {
			levelStr.append("    ");
		}
		return levelStr.toString();
	}

	public static void main(String[] args) throws Exception {

		HTTPClientUtils
				.getClient(
						"http://121.40.138.123/app?Action=Dialout&ActionID=10425108-9494-41f7-a8d4-0d6c3&Account=" +
								"N00000001060&Exten=13611308035&FromExten=8014&PBX=cc.ali.1.3");

	}

	@SuppressWarnings("unused")
	public static CloseableHttpClient createHttpClientWithCert(KeyStore keyStore, String keyStorePassword,
															   KeyStore trustStoreFile, int connMaxTotal, int connDefaultMaxPerRoute, int validateInactivityMillSeconds,
															   int connEvictIdleConnectionsTimeoutMillSeconds, String proxyHost, int proxyPort, String proxyUsername,
															   String proxyPassword) {
		SSLContext sslcontext = null;
		try {
			if(trustStoreFile==null){//不需要服务端证书 update 2017-09-21
				sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, keyStorePassword.toCharArray()).build();
			}else{
				sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
						.loadTrustMaterial(trustStoreFile, new TrustSelfSignedStrategy()).build();
			}
		} catch (Exception e) {
			throw new RuntimeException("key store fail", e);
		}

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		// allHostsValid);

		// Create a registry of custom connection socket factories for supported
		// protocol schemes.
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("https", sslsf).register("http", PlainConnectionSocketFactory.INSTANCE).build();

		// Create a connection manager with custom configuration.
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

		// Create socket configuration
		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();// 小数据网络包
		// Configure the connection manager to use socket configuration either
		// by default or for a specific host.
		connManager.setDefaultSocketConfig(socketConfig);
		// Validate connections after 1 sec of inactivity
		connManager.setValidateAfterInactivity(validateInactivityMillSeconds);

		// Configure total max or per route limits for persistent connections
		// that can be kept in the pool or leased by the connection manager.
		connManager.setMaxTotal(connMaxTotal);
		connManager.setDefaultMaxPerRoute(connDefaultMaxPerRoute);

		// Use custom cookie store if necessary.
		CookieStore cookieStore = new BasicCookieStore();
		// Use custom credentials provider if necessary.
		// CredentialsProvider credentialsProvider = new
		// BasicCredentialsProvider();
		// Create global request configuration
		RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT)
				.setExpectContinueEnabled(true).build();

		HttpHost proxy = null;
		if (StringUtils.isNotEmpty(proxyHost)) {
			proxy = new HttpHost(proxyHost, proxyPort, "http");
		}
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		if (StringUtils.isNotEmpty(proxyUsername) && StringUtils.isNotEmpty(proxyPassword)) {
			credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(proxyUsername,
					proxyPassword));
		}

		// Create an HttpClient with the given custom dependencies and
		// configuration.
		CloseableHttpClient httpclient;
		if (proxy == null) {
			httpclient = HttpClients.custom().setConnectionManager(connManager).setDefaultCookieStore(cookieStore)
					.setDefaultRequestConfig(defaultRequestConfig).evictExpiredConnections()
					.evictIdleConnections(connEvictIdleConnectionsTimeoutMillSeconds, TimeUnit.MILLISECONDS)
					.setSSLSocketFactory(sslsf).build();
		} else {
			httpclient = HttpClients.custom().setConnectionManager(connManager).setProxy(proxy)
					.setDefaultCredentialsProvider(credsProvider).setDefaultCookieStore(cookieStore)
					.setDefaultRequestConfig(defaultRequestConfig).evictExpiredConnections()
					.evictIdleConnections(connEvictIdleConnectionsTimeoutMillSeconds, TimeUnit.MILLISECONDS)
					.setSSLSocketFactory(sslsf).build();
		}
		return httpclient;
	}
}
