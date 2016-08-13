import iuv.cns.wechat.weutils.WeSign;

import java.util.HashMap;
import java.util.Map;


public class TestSignature {
	public static void main(String[] args) {
		Map<String, String> signatureMap=new HashMap<String, String>();
		signatureMap.put("noncestr", WeSign.nonceStr());
		signatureMap.put("jsapi_ticket", "**********");
		signatureMap.put("timestamp", WeSign.timestamp());
		signatureMap.put("url", "http://cornershop.cn/t/pay/sendPay.cns?code=");
		String signature=WeSign.sha1Sign(signatureMap);
		System.out.println(signature);
	}
}
