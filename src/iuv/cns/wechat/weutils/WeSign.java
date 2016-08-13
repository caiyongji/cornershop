package iuv.cns.wechat.weutils;

import iuv.cns.utils.Constants;
import iuv.cns.utils.DateUtil;
import iuv.cns.utils.MailUtil;
import iuv.cns.utils.NetUtil;
import iuv.cns.utils.StringUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
/**
 * 微信工具类
 */
public class WeSign {
	private final static Log LOG = LogFactory.getLog(WeSign.class);
	/**
	 *校验请求是否来自微信服务器
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	private static int sequence=0;
	private static String dateSign="";
	public static boolean check(String signature, String timestamp, String nonce) {
		if (!StringUtil.isValid(signature)||!StringUtil.isValid(timestamp)||!StringUtil.isValid(nonce)) {
			return false;
		}
        String[] arr = new String[] { Constants.TOKEN, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = hex(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("WeSign.check()验证微信服务器时异常",e);
        }

        content = null;
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}
	
	private static String hex(byte[] arr) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < arr.length; ++i) {  
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1,3));  
        }  
        return sb.toString();  
    }  
//	private static String byteToStr(byte[] byteArray) {
//        String strDigest = "";
//        for (int i = 0; i < byteArray.length; i++) {
//            strDigest += byteToHexStr(byteArray[i]);
//        }
//        return strDigest;
//    }
//	private static String byteToHexStr(byte mByte) {
//        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
//        char[] tempArr = new char[2];
//        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
//        tempArr[1] = Digit[mByte & 0X0F];
//
//        String s = new String(tempArr);
//        return s;
//    }
	/**
	 * 解析【加密】请求内容
	 * @param request
	 * @return
	 */
	public static Map<String, String> parseXmlCrypt(HttpServletRequest request){
	       // 将解析结果存储在HashMap中
	       Map<String, String> map = new HashMap<String, String>();
	       try {

		       // 从request中取得输入流
		       InputStream inputStream = request.getInputStream();

		       BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
		       String line;
		       StringBuffer buf=new StringBuffer();
		       while((line=reader.readLine())!=null){
		        buf.append(line);
		       }
		       reader.close();
		       inputStream.close();

		       WXBizMsgCrypt wxCeypt=new WXBizMsgCrypt(Constants.TOKEN, Constants.ENCODING_AES_KEY, Constants.APP_ID);
		       // 微信加密签名
		       String msgSignature = request.getParameter("msg_signature");
		       // 时间戳
		       String timestamp = request.getParameter("timestamp");
		       // 随机数
		       String nonce = request.getParameter("nonce");
//		       LOG.info("加密的："+buf.toString());
		       String respXml=wxCeypt.decryptMsg(msgSignature, timestamp, nonce, buf.toString());

		       //SAXReader reader = new SAXReader();
		       Document document =DocumentHelper.parseText(respXml);
		       // 得到xml根元素
		       Element root = document.getRootElement();
		       // 得到根元素的所有子节点
		       List<Element> elementList = root.elements();

		       // 遍历所有子节点
		       for (Element e : elementList) {
				map.put(e.getName(), e.getText());
//				LOG.info(e.getName()+e.getText());
			}

		       // 释放资源
		       //inputStream.close();
		       //inputStream = null;
		} catch (Exception e) {
			LOG.error("解析【加密】请求内容异常",e);
		}
	       return map;
	   }

	/**
	 * 解析【明文】请求内容
	 * @param request
	 * @return
	 */
	public static Map<String, String> parseXml(HttpServletRequest request) {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();

		try {
			// 从request中取得输入流
			InputStream inputStream = request.getInputStream();
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
			String line;
			StringBuffer buf=new StringBuffer();
			while((line=reader.readLine())!=null){
				buf.append(line);
			}
			reader.close();
			inputStream.close();
			inputStream = null;
//			LOG.info("未加密的："+buf.toString());
			Document document = DocumentHelper.parseText(buf.toString());
			// 得到xml根元素
			Element root = document.getRootElement();
			// 得到根元素的所有子节点
			List<Element> elementList = root.elements();

			// 遍历所有子节点
			for (Element e : elementList) {
				map.put(e.getName(), e.getText());
//				LOG.info(e.getName()+e.getText());
			}
		} catch (Exception e) {
			LOG.error("解析【明文】请求内容异常",e);
		}

		return map;
	}

	/**
	 * 加密xml
	 * 
	 * @param replyMsg
	 * @param timeStamp
	 * @param nonce
	 * @return
	 */
	public static String encryptMsg(String replyMsg, String timestamp, String nonce) {
		String result = "";
		try {
			WXBizMsgCrypt cryptor = new WXBizMsgCrypt(Constants.TOKEN, Constants.ENCODING_AES_KEY, Constants.APP_ID);
			result = cryptor.encryptMsg(replyMsg, timestamp, nonce);
		} catch (AesException e) {
			LOG.error("【加密】内容时出现异常", e);
		}
		return result;
	}

	/**
	 * md5加密
	 * @param str
	 * @return
	 */
	public static String md5(String str){
		 MessageDigest md = null;
	        String tmpStr = null;
	        try {
	            md = MessageDigest.getInstance("MD5");
	            byte[] digest = md.digest(str.getBytes());
	            tmpStr = hex(digest);
	        } catch (NoSuchAlgorithmException e) {
	            LOG.error("MD5加密异常",e);
	        }
	        return tmpStr;
	}
	
	/**
	 * sha1加密
	 * @param sign
	 * @return
	 */
	public static String sha1(String str) {
		MessageDigest md = null;
        String tmpStr = null;
        try {
            md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(str.getBytes());
            tmpStr = hex(digest);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("SHA1加密异常",e);
        }
        return tmpStr;
	}
	
	/**
	 * MD5签名算法【默认小写】
	 * @param map
	 * @return
	 */
	public static String md5Sign(Map<String, String> map){
		return md5Sign(map,false);
	}
	/**
	 * MD5签名算法
	 * @param map 统一下单API各参数
	 * @param upperCase 是否大写
	 * ！！！所传map的value值不可为空
	 * @return
	 */
	public static String md5Sign(Map<String, String> map,boolean upperCase){
		String sign="";
		Set<String> set =map.keySet();
		if (set.isEmpty()) {
			return "";
		}
		String[] keys=set.toArray(new String[set.size()]);
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++) {
			if (i==keys.length-1) {
				sign+=keys[i]+"="+map.get(keys[i]);
			}else {
				sign+=keys[i]+"="+map.get(keys[i])+"&";
			}
		}
		sign+="&key="+Constants.WECHAT_PAY_KEY;
		//MD5加密
		sign=upperCase?md5(sign).toUpperCase():md5(sign);
		 return sign;
	}
	
	/**
	 * sha1签名算法【默认小写】
	 * @param map
	 * @return
	 */
	public static String sha1Sign(Map<String, String> map){
		return sha1Sign(map,false);
	}
	/**
	 * 卡券扩展字段cardExt加密
	 * @param map
	 * @return
	 */
	public static String sha1Sign2(Map<String, String> map){
		String sign="";
		Collection<String> set =map.values();
		if (set.isEmpty()) {
			return "";
		}
		String[] values=set.toArray(new String[set.size()]);
		Arrays.sort(values);
		for (int i = 0; i < values.length; i++) {
			sign+=values[i];
		}
		sign=sha1(sign);
		return sign;
	}
	/**
	 * sha1签名算法
	 * @param map jsapi初始化
	 * @param upperCase 是否大写
	 * @return
	 */
	public static String sha1Sign(Map<String, String> map,boolean upperCase){
		String sign="";
		Set<String> set =map.keySet();
		if (set.isEmpty()) {
			return "";
		}
		String[] keys=set.toArray(new String[set.size()]);
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++) {
			if (i==keys.length-1) {
				sign+=keys[i]+"="+map.get(keys[i]);
			}else {
				sign+=keys[i]+"="+map.get(keys[i])+"&";
			}
		}
		LOG.info("SHA1加密签名字符串string1：【"+sign+"】");
		//sha1加密
		sign=upperCase?sha1(sign).toUpperCase():sha1(sign);
		LOG.info("SHA1加密签名：【"+sign+"】");
		return sign;
	}
	
	/**
	 * 根据useragent判断请求是否来自微信浏览器
	 *	TODO 【安全隐患】不严谨判断请求是否来自微信浏览器
	 * @param request
	 * @return
	 */
	public static boolean checkIsWechatBrowser(HttpServletRequest request) {
		String userAgent=NetUtil.getUserAgent(request);
		if (StringUtil.isValid(userAgent)&&userAgent.contains("MicroMessenger")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取时间戳
	 * 精确到秒·即共十位
	 * @return
	 */
	public static String timestamp() {
		return String.valueOf(System.currentTimeMillis()).substring(0, 10);
	}
	
	/**
	 * @return 随机字符串，当前时间MD5获取32位随机字符
	 */
	public static String nonceStr() {
		return md5(String.valueOf(System.currentTimeMillis()));
	}
	/**
	 * 小于1,000,000万的数字字符串
	 * 保持6位
	 * @return
	 */
	public synchronized static String sequence(){
		String result="";
		sequence+=1;
		//每日清零
		if (!dateSign.equals(DateUtil.getyyyyMMdd())) {
			dateSign=DateUtil.getyyyyMMdd();
			sequence=0;
		}
		if (sequence>=1000000) {
			LOG.error("恭喜超出每天壹佰万单！");
			MailUtil.sendMail("恭喜超出每天壹佰万单！", "恭喜超出每天壹佰万单！");
			return "000000";
		}
		if (sequence<10) {
			result="00000"+sequence;
		}else if (sequence<100) {
			result="0000"+sequence;
		}else if (sequence<1000) {
			result="000"+sequence;
		}else if (sequence<10000) {
			result="00"+sequence;
		}else if (sequence<100000) {
			result="0"+sequence;
		}else {
			result=String.valueOf(sequence);
		}
		return result;
	}
}
