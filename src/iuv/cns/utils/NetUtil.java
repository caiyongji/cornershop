package iuv.cns.utils;

import iuv.cns.wechat.weutils.AccessToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetUtil {
	private final static Log LOG = LogFactory.getLog(NetUtil.class);

	/**
	 * 【并不准确】 ip地址可以被修改访问
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		/**
		 * 解决x-forwarded-for中多IP问题
		 */
		if (StringUtil.isValid(ip) && ip.contains(",")) {
			ip = ip.split(",")[0];
		}
		return ip;
	}

	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	/**
	 * 封装HTTP请求（GET方式） ~参数在URL中
	 * 
	 * @param urlStr
	 * @return
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static String connectUrlResponse(String urlStr) throws Exception {
		LOG.info("请求服务器：URL：【" + urlStr + "】");
		String result = "";
		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
		TrustManager[] tm = { X509TRUSTMANAGER };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		URL url = new URL(urlStr);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setSSLSocketFactory(ssf);
		connection.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("GET");
		connection.connect();
		// 将返回的输入流转换成字符串
		InputStream inputStream = connection.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		bufferedReader.close();
		inputStreamReader.close();
		// 释放资源
		inputStream.close();
		inputStream = null;
		connection.disconnect();
		return result;
	}

	/**
	 * 封装HTTP请求（POST方式）
	 * 
	 * @param urlStr 地址
	 * @param data POST数据：json/xml
	 * @return
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static String connectUrlResponsePostData(String urlStr, String data) throws Exception {
		LOG.info("请求服务器：URL：【" + urlStr + "】 DATA：【" + data + "】");
		String result = "";
		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
		TrustManager[] tm = { X509TRUSTMANAGER };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		URL url = new URL(urlStr);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setSSLSocketFactory(ssf);
		connection.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		// connection.setRequestProperty("Content-type", "text/html");
		// connection.setRequestProperty("Accept-Charset", "UTF-8");
		// connection.setRequestProperty("contentType", "UTF-8");
		connection.connect();
		// 输出流
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.write(data.getBytes());
		out.flush();
		out.close();
		// 将返回的输入流转换成字符串
		InputStream inputStream = connection.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		bufferedReader.close();
		inputStreamReader.close();
		// 释放资源
		inputStream.close();
		inputStream = null;
		connection.disconnect();
		return result;
	}

	/**
	 * SSL使用证书请求URL并post数据
	 * @param urlStr
	 * @param data
	 * @return
	 * @throws Exception
	 * 
	 * KeyStore Types
	 * The types in this section can be specified when generating an instance of KeyStore.
	 * 
	 * Type Description 
	 * 【jceks】 The proprietary keystore implementation provided by the SunJCE provider. 
	 * 【jks】 The proprietary keystore implementation provided by the SUN provider. 
	 * 【pkcs12】 The transfer syntax for personal identity information as defined in PKCS #12. 
	 */
	public static String connectUrlWithP12ToPostXml(String urlStr, String data) throws Exception {
		LOG.info("请求服务器：URL：【" + urlStr + "】 DATA：【" + data + "】");
		String result = "";
		//keyStore
		KeyStore keyStore=KeyStore.getInstance("PKCS12");//类型：jceks、jks、pkcs12
		FileInputStream keyStoreStream=new FileInputStream(Constants.CA_KEY_DIR);
		keyStore.load(keyStoreStream, Constants.CA_PWD);
		keyStoreStream.close();
		KeyManagerFactory keyManagerFactory=KeyManagerFactory.getInstance("SunX509");//算法：SunX509、SunJSSE
		keyManagerFactory.init(keyStore, Constants.CA_PWD);
		
//		//trustStore
//		KeyStore trustStore=KeyStore.getInstance("PKCS12");
//		FileInputStream trustStoreStream=new FileInputStream(Constants.CA__TRUST_DIR);
//		trustStore.load(trustStoreStream, null);
//		trustStoreStream.close();
//		TrustManagerFactory trustManagerFactory=TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//		trustManagerFactory.init(trustStore);
		
		//SSL
		SSLContext sslContext = SSLContext.getInstance("TLSv1");//协议：SSL、TLS、TLSv1、SSLv3
		sslContext.init(
				keyManagerFactory.getKeyManagers(),//本机证书(服务器判断是否信任本机)
				//注意：此处如果为null，则调用jre路径下的cacerts文件。如：%JRE_HOME%/lib/security/cacerts
				//则需要在jre路径下执行：
				//keytool -import -alias [NAME] -keystore ../lib/security/cacerts  -file [CACERTS]  -trustcacerts
				null,//信任证书（本机判断是否信任服务器）
				new SecureRandom()
				);
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		
		URL url = new URL(urlStr);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setSSLSocketFactory(sslSocketFactory);
		connection.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.connect();
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.write(data.getBytes());
		out.flush();
		out.close();
		InputStream inputStream = connection.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		inputStream = null;
		connection.disconnect();
		return result;
	}
	
	/**
	 * 
	 * @param urlStr
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	public static String connectUrlGetData(String urlStr,Map<String, String> headers) throws Exception {
		LOG.info("请求服务器GET(带headers)：URL：【" + urlStr + "】 ");
		String result = "";
		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
		TrustManager[] tm = { X509TRUSTMANAGER };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		URL url = new URL(urlStr);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setSSLSocketFactory(ssf);
		connection.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("GET");
		Set<String> keySet=headers.keySet();
		for (String key : keySet) {
			connection.setRequestProperty(key, headers.get(key));
		}
		connection.connect();
		// 将返回的输入流转换成字符串
		InputStream inputStream = connection.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		bufferedReader.close();
		inputStreamReader.close();
		// 释放资源
		inputStream.close();
		inputStream = null;
		connection.disconnect();
		return result;
	}

	private static X509TrustManager X509TRUSTMANAGER = new X509TrustManager() {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO 【安全隐患】X509证书管理器信任一切？
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	};

	public static void test() throws Exception {
		String accessToken = AccessToken.token();

		String getGroupUrl = "https://api.weixin.qq.com/merchant/group/getall?access_token=" + accessToken;
		String getProductUrl = "https://api.weixin.qq.com/merchant/group/getbyid?access_token=" + accessToken;
		String getProductDetailUrl = "https://api.weixin.qq.com/merchant/get?access_token=" + accessToken;
		JSONObject groupsJson = JSONObject.fromObject(connectUrlResponse(getGroupUrl));
		JSONArray groupsDetail = groupsJson.getJSONArray("groups_detail");
		List<String> groupIds = new ArrayList<String>();
		for (Object object : groupsDetail) {
			JSONObject jsonObject = JSONObject.fromObject(object);
			groupIds.add(jsonObject.getString("group_id"));

		}
		List<String> productIds = new ArrayList<String>();
		for (String group_id : groupIds) {
			String data = "{\"group_id\":\"" + group_id + "\"}";
			JSONObject groupJson = JSONObject.fromObject(connectUrlResponsePostData(getProductUrl, data));
			JSONObject group_detail = groupJson.getJSONObject("group_detail");
			@SuppressWarnings("unchecked")
			List<String> product_list = group_detail.getJSONArray("product_list");
			for (String product_id : product_list) {
				productIds.add(product_id);
			}
		}
		for (String productId : productIds) {
			String data = "{\"product_id\":\"" + productId + "\"}";
			String result = connectUrlResponsePostData(getProductDetailUrl, data);
			LOG.info(result);
		}
	}

	public static void main(String[] args) {
		try {
			test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String encode(String string) {
		String result="";
		try {
			result=URLEncoder.encode(string,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("【1461820787】encode错误，不可能发生问题！");
		}
		return result;
	}
}
