import java.net.URLEncoder;




public class Test {

	public static void main(String[] args) throws Exception {
/*		int x=1;
		if (x<2) {
			System.out.println("if");
		}else if (x<3) {
			System.out.println("else if");
		}else {
			System.out.println("else");
		}*/
		
		/*System.out.println(DateUtil.getyyyyMMdd());*/
		
//		System.out.println(" coupon 112coupon ".replace("coupon", "").trim());
		
//		System.out.println("pHj5ms9rcj9ZmD76-94ACPw8UJoQ".length());
//		String result="<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[]]></return_msg><appid><![CDATA[wx54d7fcb7b8b81146]]></appid><mch_id><![CDATA[1317965601]]></mch_id><sub_mch_id><![CDATA[]]></sub_mch_id><device_info><![CDATA[]]></device_info><nonce_str><![CDATA[fd0f7b2c50dc333a78f0c732d8d4a8c4]]></nonce_str><result_code><![CDATA[SUCCESS]]></result_code><coupon_stock_id><![CDATA[375850]]></coupon_stock_id><resp_count><![CDATA[1]]></resp_count><success_count><![CDATA[1]]></success_count><failed_count><![CDATA[0]]></failed_count><openid><![CDATA[oHj5ms0AeEoa9cl1iMIXyrDcNV2o]]></openid><ret_code><![CDATA[SUCCESS]]></ret_code><coupon_id><![CDATA[191471368]]></coupon_id><partner_trade_no><![CDATA[131796560120160419000006]]></partner_trade_no></xml>";
//		Document document =DocumentHelper.parseText(result);
//        Element root = document.getRootElement();
//        System.out.println(root.elementText("result_code"));
		
//		String result="";
//		result=ConnectWeChatTo.getCouponStockId("195468999", "oHj5ms0AeEoa9cl1iMIXyrDcNV2o");
//		System.out.println(result);
		System.out.println(URLEncoder.encode("products/asdf中文.jpg"));
	}

}
