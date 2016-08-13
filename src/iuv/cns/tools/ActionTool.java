package iuv.cns.tools;

import iuv.cns.utils.Constants;
import iuv.cns.utils.NetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ActionTool {
	private final static String ACCESSTOKEN=
"cmFBTBFDHwt4ScPc1iM22155clUY_kFRBS02nB9jGZd_UxMCJPXGcfe-**********";
	
	public static void main(String[] args) throws Exception{
		String result="";
//		result=getMaterial();
//		getQrCodeTicket();
//		getProductGroup();
//		delProductGroup("448294265");
		result=getProdctByStatus(0);//商品状态(0-全部, 1-上架, 2-下架)
//		totalDelProduct();
//		result=signFH("12927088079441239855");
//		result=signAllFH();
//		result=getVipInfoByCard("113733152624");
//		result=createProduct();
//		result=getProductDetailById("pHj5ms3Xf7miKhIHa-iSwIk1VKh4");
//		result=getProductGroup();
//		result=getAllProductByGroupId("457691128");
//		result=getProductDetail("pHj5ms0n94vgrIUizOYCE26tcDPg");
		System.out.println(result);
	}
	/**
	 * 获取二维码
	 */
	private static String getQrCodeTicket() throws Exception{
		String qrPostData="{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": "
				+ "{\"scene_id\": 9001}}}";
		/** 获取二维码 */
		String url="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+ACCESSTOKEN;
		String result=NetUtil.connectUrlResponsePostData(url, qrPostData);
		System.out.println(result);
		String ticket=JSONObject.fromObject(result).getString("ticket");
		System.out.println(ticket);
//		result=NetUtil.connectUrlResponse("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket);
//		System.out.println(result);
		return ticket;
	}
	/**
	 * 获取素材
	 */
	private static String getMaterial() throws Exception{
		Map<String, String> map=new HashMap<String, String>();
		map.put("type", "news");
		map.put("offset", "0");
		map.put("count", "20");
		String url="https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token="+ACCESSTOKEN;
		String result=NetUtil.connectUrlResponsePostData(url, JSONObject.fromObject(map).toString());
		System.out.println(result);
		return result;
	}
	
	private static String getProductGroup() throws Exception{
		String url="https://api.weixin.qq.com/merchant/group/getall?access_token="+ACCESSTOKEN;
		String result=NetUtil.connectUrlResponse(url);
		System.out.println(result);
		return result;
	}
	
	private static String delProductGroup(String groupId) throws Exception{
		String url="https://api.weixin.qq.com/merchant/group/del?access_token="+ACCESSTOKEN;
		String result=NetUtil.connectUrlResponsePostData(url, "{\"group_id\":"+groupId+"}");
		System.out.println(result);
		return result;
	}
	
	private static String getProdctByStatus(int status) throws Exception{
		String url="https://api.weixin.qq.com/merchant/getbystatus?access_token="+ACCESSTOKEN;
		String result=NetUtil.connectUrlResponsePostData(url, "{\"status\":"+status+"}");
		System.out.println(result);
		return result;
	}
	
	private static String delProductById(String productId) throws Exception{
		String url="https://api.weixin.qq.com/merchant/del?access_token="+ACCESSTOKEN;
		String result=NetUtil.connectUrlResponsePostData(url, "{\"product_id\":"+productId+"}");
		System.out.println(result);
		return result;
	}
	
	private static void totalDelProduct() throws Exception{
		String result=getProdctByStatus(2);
		JSONArray array=JSONObject.fromObject(result).getJSONArray("products_info");
		for (Object obj : array) {
			String productId=JSONObject.fromObject(obj).getString("product_id");
			String r=delProductById(productId);
			System.out.println(productId+":"+r);
		}
	}
	
	private static String signFH(String orderId) throws Exception{
		String data="{\"order_id\":\""+orderId+"\""
				+ ",\"need_delivery\":0}";
		String result=NetUtil.connectUrlResponsePostData(Constants.SIGN_WECHAT_FH_URL+ACCESSTOKEN, data);
		return result;
	}
	private static String signFH(String[] orderIds) throws Exception{
		String result="";
		for (String orderId : orderIds) {
			String data="{\"order_id\":\""+orderId+"\""
					+ ",\"need_delivery\":0}";
			result+=NetUtil.connectUrlResponsePostData(Constants.SIGN_WECHAT_FH_URL+ACCESSTOKEN, data);
			result+="\n";
		}
		return result;
	}
	
	private static String signAllFH() throws Exception{
		List<String> orderList=new ArrayList<String>();
		String url="https://api.weixin.qq.com/merchant/order/getbyfilter?access_token="+ACCESSTOKEN;
		String result=NetUtil.connectUrlResponsePostData(url, "{\"status\":2}");
		JSONArray array=JSONObject.fromObject(result).getJSONArray("order_list");
		for (Object order : array) {
			String orderId=JSONObject.fromObject(order).getString("order_id");
			orderList.add(orderId);
		}
		
		String[] orderIds =orderList.toArray(new String[orderList.size()]);
		String rt=signFH(orderIds);
		System.out.println(rt);
		return rt;
	}
	
	private static String getVipInfoByCard(String vipCard) {
		String result="";
		Map<String, String> dataMap=new HashMap<String, String>();
		dataMap.put("card_id", Constants.VIP_CAR_ID);
		dataMap.put("code", vipCard);
		try {
			result=NetUtil.connectUrlResponsePostData(String.format(Constants.GET_VIP_INFO_URL, ACCESSTOKEN), JSONObject.fromObject(dataMap).toString());
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}
	
	private static String createProduct(){
		String result="";
		String jsonString="{\"product_base\": {\"category_id\": [\"537074298\"],\"property\": [{\"id\": \"1075741879\",\"vid\": \"1079749967\"},{\"id\": \"1075754127\",\"vid\": \"1079795198\"},{\"id\": \"1075777334\",\"vid\": \"1079837440\"}],\"name\": \"testaddproduct\",\"sku_info\": [{\"id\": \"1075741873\",\"vid\": [\"1079742386\",\"1079742363\"]}],"
				+ "\"main_img\": \"http://img.cornershop.cn/\","
				+ " \"img\": [\"http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjD3ulEKogfsiaua49pvLfUS8Ym0GSYjViaLic0FD3vN0V8PILcibEGb2fPfEOmw/0\"],\"detail\": [{\"text\": \"test first\"},{\"img\": \"http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjD3ul1UcLcwxrFdwTKYhH9Q5YZoCfX4Ncx655ZK6ibnlibCCErbKQtReySaVA/0\"},{\"text\": \"test again\"}],\"buy_limit\": 10},\"sku_list\": [{\"sku_id\": \"1075741873:1079742386\",\"price\": 30,\"icon_url\": \"http://mmbiz.qpic.cn/mmbiz/4whpV1VZl28bJj62XgfHPibY3ORKicN1oJ4CcoIr4BMbfA8LqyyjzOZzqrOGz3f5KWq1QGP3fo6TOTSYD3TBQjuw/0\",\"product_code\": \"testing\",\"ori_price\": 9000000,\"quantity\": 800},{\"sku_id\": \"1075741873:1079742363\",\"price\": 30,\"icon_url\": \"http://mmbiz.qpic.cn/mmbiz/4whpV1VZl28bJj62XgfHPibY3ORKicN1oJ4CcoIr4BMbfA8LqyyjzOZzqrOGz3f5KWq1QGP3fo6TOTSYD3TBQjuw/0\",\"product_code\": \"testingtesting\",\"ori_price\": 9000000,\"quantity\": 800}],\"attrext\": {\"location\": {\"country\": \"中国\",\"province\": \"广东省\",\"city\": \"广州市\",\"address\": \"T.I.T创意园\"},\"isPostFree\": 0,\"isHasReceipt\": 1,\"isUnderGuaranty\": 0,\"isSupportReplace\": 0},\"delivery_info\": {\"delivery_type\": 0,\"template_id\": 0, \"express\": [{\"id\": 10000027, \"price\": 100}, {\"id\": 10000028, \"price\": 100}, {\"id\": 10000029, \"price\": 100}]}}";
		try {
			result=NetUtil.connectUrlResponsePostData(String.format("https://api.weixin.qq.com/merchant/create?access_token=%s", ACCESSTOKEN), jsonString);
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}
	
	private static String getProductDetailById(String pid) {
		
		String result="";
		String jsonString="{\"product_id\": \""+pid+"\"}";
		try {
			result=NetUtil.connectUrlResponsePostData("https://api.weixin.qq.com/merchant/get?access_token="+ACCESSTOKEN, jsonString);
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}
	
	public static String getAllProductByGroupId(String groupId) {
		String result="";
		String data="{\"group_id\": "+groupId+"}";
		try {
			result=NetUtil.connectUrlResponsePostData(Constants.SHOP_GET_ALL_PRODUCTS_BY_GROUP_URL+ACCESSTOKEN, data);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String getProductDetail(String productId) {
		String result="";
		String data="{\"product_id\": \""+productId+"\"}";
		try {
			result=NetUtil.connectUrlResponsePostData(Constants.SHOP_PRODUCT_DETAIL_URL+ACCESSTOKEN, data);
		} catch (Exception e) {
		}
		return result;
	}
}
