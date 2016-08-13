package iuv.cns.wechat.weutils;

import iuv.cns.utils.Constants;
import iuv.cns.utils.DateUtil;
import iuv.cns.utils.NetUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 根据语义： 链接微信服务器去获取……【内容】
 * 
 * @author CaiYongji.
 * 
 */
public class ConnectWeChatTo {
	private final static Log LOG = LogFactory.getLog(ConnectWeChatTo.class);
	/**
	 * 根据OPENID去获取用户信息 区别于根据UNIONID获取用户信息
	 * 
	 * @param accessToken
	 * @param openid
	 * @return
	 * @throws IOException
	 */
	public static String getUserInfo(String openid) throws Exception {
		return NetUtil.connectUrlResponse(String.format(Constants.GET_USER_INFO_BY_OPENID_URL, AccessToken.token(), openid));
	}

	public static String getToken() throws Exception {
		return NetUtil.connectUrlResponse(Constants.TOKEN_URL);
	}
	
	/**
	 * 获取openid
	 * @param code
	 * @return 返回数据中可以包含unionid
	 * @throws Exception
	 */
	public static String getOpenidByCode(String code) throws Exception {
		return NetUtil.connectUrlResponse(String.format(Constants.GET_USER_OPENID_URL, code));
	}
	
	/**
	 * 统一下单
	 * @param data
	 * @return 注意是【xml】信息·未特别标注则为json
	 * @throws Exception
	 */
	public static String placeOrder(String data) throws Exception {
		return NetUtil.connectUrlResponsePostData(Constants.WECHAT_PAY_URL, data);
	}

	/**
	 * 获取jsApiTicket
	 * @return
	 * @throws Exception 
	 */
	public static String getJsTicket() throws Exception {
		return NetUtil.connectUrlResponse(String.format(Constants.GET_JSAPI_TICKET_URL, AccessToken.token()));
	}
	
	/**
	 * 获取cardApiTicket
	 * @return
	 * @throws Exception 
	 */
	public static String getCardTicket() throws Exception {
		return NetUtil.connectUrlResponse(String.format(Constants.GET_CAR_API_TICKET, AccessToken.token()));
	}
	
	/**
	 * 下发优惠券【证书】
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String sendCoupon(String data) throws Exception {
		return NetUtil.connectUrlWithP12ToPostXml(Constants.WECHAT_PAY_COUPON_URL, data);
	}
	
	/**
	 * 增减会员卡积分
	 * @param vipCode 用户会员卡号
	 * @param bonus 积分（减为负数）
	 * @return
	 * @throws Exception
	 */
	public static boolean addBonus(String vipCode,int bonus){
		Map<String, Object> dataMap=new HashMap<String, Object>();
		dataMap.put("code", vipCode);
		dataMap.put("card_id", Constants.VIP_CAR_ID);
		dataMap.put("add_bonus", bonus);
		String urlStr=String.format(Constants.VIP_BONUS_URL, AccessToken.token());
		try {
			String result = NetUtil.connectUrlResponsePostData(urlStr, JSONObject.fromObject(dataMap).toString());
			if (!"ok".equals(JSONObject.fromObject(result).getString("errmsg"))) {
				throw new Exception(result);
			}
		} catch (Exception e) {
			LOG.error("变更积分出错：会员卡号【"+vipCode+"】积分【"+bonus+"】",e);
			return false;
		}
		return true;
	}
	
	/**
	 * 发送消息模板
	 * @param dataMap
	 * @return
	 */
	public static boolean sendTemplateMsg(Map<String, Object> dataMap) {
		try {
			String result=NetUtil.connectUrlResponsePostData(String.format(Constants.WECHAT_SEND_TEMPLATE_MESSAGE_URL, AccessToken.token()), JSONObject.fromObject(dataMap).toString());
			if (!"ok".equals(JSONObject.fromObject(result).getString("errmsg"))) {
				throw new Exception(result);
			}
		} catch (Exception e) {
			LOG.error("发送模板消息出错：openId【"+dataMap.get("touser")+"】模板ID【"+dataMap.get("template_id")+"】",e);
			return false;
		}
		return true;
	}
	
	/**
	 * 根据订单ID获取详细信息
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public static String getDetailByOrderId(String orderId) throws Exception{
		Map<String, String> map=new HashMap<String, String>();
		map.put("order_id", orderId);
		return NetUtil.connectUrlResponsePostData(String.format(Constants.WECHAT_PAY_MERCHANT_ORDER_DETAIL_BY_ID, AccessToken.token()), JSONObject.fromObject(map).toString());
	}
	
	public static String sendVipCard(String openId) throws Exception{
		Map<String, Object> dataMap=new HashMap<String, Object>();
		dataMap.put("touser", openId);
		dataMap.put("msgtype", "wxcard");
		Map<String, String> map=new HashMap<String, String>();
		map.put("card_id", Constants.VIP_CAR_ID);
		Map<String, String> signMap=new HashMap<String, String>();
		String timestamp=WeSign.timestamp();
		signMap.put("timestamp", timestamp);
		String nonceStr=WeSign.nonceStr();
		signMap.put("nonce_str", nonceStr);
		signMap.put("api_ticket", CardApiTicket.ticket());
		signMap.put("card_id", Constants.VIP_CAR_ID);
		String signature=WeSign.sha1Sign2(signMap);
		Map<String, String> extMap= new HashMap<String, String>();
		extMap.put("timestamp", timestamp);
		extMap.put("nonce_str", nonceStr);
		extMap.put("signature", signature);
		map.put("card_ext", JSONObject.fromObject(extMap).toString());
		dataMap.put("wxcard", JSONObject.fromObject(map));
		return NetUtil.connectUrlResponsePostData(String.format(Constants.KF_SEND_MESSAGE_URL, AccessToken.token()), JSONObject.fromObject(dataMap).toString());
	}
	
	/**
	 * 通过客服接口下发【文本】消息
	 * @param openId
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String sendMessageByKF(String openId,String content) throws Exception{
		Map<String, Object> dataMap=new HashMap<String, Object>();
		dataMap.put("touser", openId);
		dataMap.put("msgtype", "text");
		Map<String, String> map=new HashMap<String, String>();
		map.put("content", content);
		dataMap.put("text", JSONObject.fromObject(map));
		return NetUtil.connectUrlResponsePostData(String.format(Constants.KF_SEND_MESSAGE_URL, AccessToken.token()), JSONObject.fromObject(dataMap).toString());
	}
	
	/**
	 * 通过客服接口下发【图文】消息
	 * @param openId
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String sendNewsMessageByKF(String openId,String mediaId) throws Exception{
		Map<String, Object> dataMap=new HashMap<String, Object>();
		dataMap.put("touser", openId);
		dataMap.put("msgtype", "mpnews");
		Map<String, String> map=new HashMap<String, String>();
		map.put("media_id", mediaId);
		dataMap.put("mpnews", JSONObject.fromObject(map));
		return NetUtil.connectUrlResponsePostData(String.format(Constants.KF_SEND_MESSAGE_URL, AccessToken.token()), JSONObject.fromObject(dataMap).toString());
	}
	
	/**
	 * 管理员专用
	 * @param msgMap
	 * @return
	 */
	public static String sendCoupon(Map<String, String> msgMap) {
		String result="";
		String tradeNo="1317965601"+DateUtil.getyyyyMMdd()+WeSign.sequence();
		String timeStamp=WeSign.timestamp();
		String nonceStr=WeSign.md5(timeStamp);
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("coupon_stock_id", msgMap.get("Content").replace("coupon", "").trim());
		map.put("openid_count", "1");
		map.put("partner_trade_no", tradeNo);
		map.put("openid", msgMap.get("FromUserName"));
		map.put("appid", Constants.APP_ID);
		map.put("mch_id", Constants.MCH_ID);
		map.put("nonce_str", nonceStr);
		String sign=WeSign.md5Sign(map, true);
		map.put("sign", sign);
		String data=MapUtil.asXml(map);
		try {
			result=sendCoupon(data);
			return result;
		} catch (Exception e) {
			LOG.error("发放优惠券result:【"+result+"】",e);
			return e.getMessage();
		}
	}
	
	public static String sendCoupon(String openId,String cardId) {
		String result="";
		String tradeNo="1317965601"+DateUtil.getyyyyMMdd()+WeSign.sequence();
		String timeStamp=WeSign.timestamp();
		String nonceStr=WeSign.md5(timeStamp);
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("coupon_stock_id", cardId);
		map.put("openid_count", "1");
		map.put("partner_trade_no", tradeNo);
		map.put("openid", openId);
		map.put("appid", Constants.APP_ID);
		map.put("mch_id", Constants.MCH_ID);
		map.put("nonce_str", nonceStr);
		String sign=WeSign.md5Sign(map, true);
		map.put("sign", sign);
		String data=MapUtil.asXml(map);
		try {
			result=sendCoupon(data);
			return result;
		} catch (Exception e) {
			LOG.error("发放优惠券失败:【"+result+"】",e);
			return e.getMessage();
		}
	}
	
	public static String getPayCardStatus(String payCardId) {
		String result="";
		Map<String, String> xmlMap= new HashMap<String, String>();
		xmlMap.put("appid", Constants.APP_ID);
		xmlMap.put("coupon_stock_id", payCardId);
		xmlMap.put("mch_id", Constants.MCH_ID);
		xmlMap.put("nonce_str", WeSign.nonceStr());
		String sign=WeSign.md5Sign(xmlMap, true);
		xmlMap.put("sign", sign);
		String xml=MapUtil.asXml(xmlMap);
		try {
			result=NetUtil.connectUrlResponsePostData(Constants.GET_PAY_CARD_STATUS_URL, xml);
		} catch (Exception e) {
			LOG.error("查询优惠券失败:【"+result+"】",e);
			return e.getMessage();
		}
		return result;
	}
	
	public static String getVipInfo(String vipCard) {
		String result="";
		Map<String, String> dataMap=new HashMap<String, String>();
		dataMap.put("card_id", Constants.VIP_CAR_ID);
		dataMap.put("code", vipCard);
		try {
			result=NetUtil.connectUrlResponsePostData(String.format(Constants.GET_VIP_INFO_URL, AccessToken.token()), JSONObject.fromObject(dataMap).toString());
		} catch (Exception e) {
			LOG.error("获取会员信息失败:【"+result+"】",e);
			return e.getMessage();
		}
		return result;
	}
	public static void main(String[] args) throws Exception {
		//113733152624
//		System.out.println(addBonus("675444690386", 8));//675444690386
		
/*		Map<String, Object> map = new HashMap<String, Object>();
		//oHj5msyANbsl1Z6OaGWX0wGeVRgU
		map.put("touser", "oHj5msyANbsl1Z6OaGWX0wGeVRgU");//oHj5ms0AeEoa9cl1iMIXyrDcNV2o
		map.put("template_id", "Y1nbi_oqTL46EDyWM8dG_hC8Qwd9QzJtHgQ4jzIkNVI");
		map.put("url", "https://mp.weixin.qq.com/s?__biz=MzAwOTgyMjIzOA==&mid=452669009&idx=1&sn=9b097ed2b664657203799dc9475f3dfc#rd");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("first", Message.valueColorMap("first:测试模板消息", "#00c800"));
		data.put("product", Message.valueColorMap("product:测试模板消息", "#00c800"));
		data.put("price", Message.valueColorMap("price:测试模板消息", "#00c800"));
		data.put("time", Message.valueColorMap("time:测试模板消息", "#00c800"));
		data.put("remark", Message.valueColorMap("remark:测试模板消息", "#00c800"));
		map.put("data", data);
		
		System.out.println(JSONObject.fromObject(map));
		sendTemplateMsg(map);*/
	}
	
	public static String signFHWithOrderId(String orderId) {
		String data="{\"order_id\":\""+orderId+"\""
				+ ",\"need_delivery\":0}";
		String result="";
		try {
			result=NetUtil.connectUrlResponsePostData(Constants.SIGN_WECHAT_FH_URL+AccessToken.token(), data);
		} catch (Exception e) {
			LOG.error("订单标记发货【"+orderId+"】失败result:【"+result+"】", e);
			result=e.getMessage();
		}
		return result;
	}
	
	public static String getCouponStockId(String couponId,String openId) {
		Map<String, String> xmlMap= new HashMap<String, String>();
		xmlMap.put("appid", Constants.APP_ID);
		xmlMap.put("coupon_id", couponId);
		xmlMap.put("mch_id", Constants.MCH_ID);
		xmlMap.put("nonce_str", WeSign.nonceStr());
		xmlMap.put("openid", openId);
		String sign=WeSign.md5Sign(xmlMap, true);
		xmlMap.put("sign", sign);
		String xml=MapUtil.asXml(xmlMap);
		String result="";
		try {
			result=NetUtil.connectUrlResponsePostData(Constants.WECHAT_PAY_COUPON_STOCK_URL, xml);
		} catch (Exception e) {
			LOG.error("查询现金券批次信息失败，现金券ID【"+couponId+"】，openId【"+openId+"】result:【"+result+"】", e);
			result=e.getMessage();
		}
		return result;
	}
	
	public static List<Map<String, String>> getAllGroup() {
		List<Map<String, String>> list=new ArrayList<Map<String, String>>();
		String result="";
		try {
			result=NetUtil.connectUrlResponse(Constants.SHOP_GET_ALL_GROUPS_URL+AccessToken.token());
			JSONArray groupsDetail=JSONObject.fromObject(result).getJSONArray("groups_detail");
			for (Object group : groupsDetail) {
				String groupId=JSONObject.fromObject(group).getString("group_id");
				String groupName=JSONObject.fromObject(group).getString("group_name");
				Map<String, String> map = new HashMap<String, String>();
				map.put("groupId", groupId);
				map.put("groupName", groupName);
				list.add(map);
			}
		} catch (Exception e) {
			LOG.error("获取全部商品分组失败result:【"+result+"】", e);
		}
		return list;
	}
	
	/**
	 * 根据分组ID获取商品ID列表
	 * @param groupId
	 * @return
	 */
	public static List<String> getAllProductByGroupId(String groupId) {
		List<String> list=new ArrayList<String>();
		String result="";
		String data="{\"group_id\": "+groupId+"}";
		try {
			result=NetUtil.connectUrlResponsePostData(Constants.SHOP_GET_ALL_PRODUCTS_BY_GROUP_URL+AccessToken.token(), data);
			@SuppressWarnings("unchecked")
			List<String> productList=(List<String>) JSONObject.fromObject(result).getJSONObject("group_detail").get("product_list");
			for (Object productId : productList) {
				list.add(String.valueOf(productId));
			}
		} catch (Exception e) {
			LOG.error("根据分组ID获取商品ID列表失败，分组ID【"+groupId+"】result:【"+result+"】", e);
		}
		return list;
	}
	
	/**
	 * 废弃，暂无用
	 * @param productId
	 * @return
	 */
	@Deprecated
	public static Map<String, String> getProductDetail(String productId) {
		Map<String, String> map = new HashMap<String, String>();
		String result="";
		String data="{\"product_id\": \""+productId+"\"}";
		try {
			result=NetUtil.connectUrlResponsePostData(Constants.SHOP_PRODUCT_DETAIL_URL+AccessToken.token(), data);
			JSONObject productBase=JSONObject.fromObject(result).getJSONObject("product_info").getJSONObject("product_base");
			String productName=productBase.getString("name");
			JSONObject sku=JSONObject.fromObject(productBase.getJSONArray("sku_list").get(0));
			String productPrice=sku.getString("price");
			map.put("productId", productId);
			map.put("productName", productName);
			map.put("productPrice", productPrice);
		} catch (Exception e) {
			LOG.error("获取商品详细信息失败【"+productId+"】result:【"+result+"】", e);
		}
		return map;
	}
	/**
	 * 获取全部商品详细信息
	 * @return
	 */
	public static List<Map<String, String>> getAllProducts() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String result="";
		String data="{\"status\": 0}";
		try {
			result=NetUtil.connectUrlResponsePostData(Constants.SHOP_GET_PRODUCTS_BY_STATUS_URL+AccessToken.token(), data);
			JSONArray array=JSONObject.fromObject(result).getJSONArray("products_info");
			for (Object object : array) {
				JSONObject product=JSONObject.fromObject(object);
				String productId=product.getString("product_id");
				String status=product.getString("status");
				JSONObject productBase=product.getJSONObject("product_base");
				Map<String, String> map = new HashMap<String, String>();
				String productName=productBase.getString("name");
				JSONObject sku=JSONObject.fromObject(product.getJSONArray("sku_list").get(0));
				String productPrice=sku.getString("price");
				String productOriPrice=sku.getString("ori_price");
//				String quantity=sku.getString("quantity");//库存
				map.put("productId", productId);
				map.put("status", status);
				map.put("productName", productName);
				map.put("productPrice", productPrice);
				map.put("productOriPrice", productOriPrice);
				list.add(map);
			}
		} catch (Exception e) {
			LOG.error("获取全部商品信息失败result:【"+result+"】", e);
		}
		return list;
	}
}
