package iuv.cns.wechat.weutils;

import iuv.cns.msgplat.BigFish;
import iuv.cns.repo.Bo;
import iuv.cns.utils.Constants;
import iuv.cns.utils.DateUtil;
import iuv.cns.utils.StringUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Message {
	private final static Log LOG = LogFactory.getLog(Message.class);
	
	private final String RECOMMEND_PRODUCT_URL="http://mp.weixin.qq.com/bizmall/mallgroup?biz=MzAwOTgyMjIzOA==&group_id=457691128&showwxpaytitle=1#wechat_redirect";
	
	@Autowired
	Bo bo;
	@Autowired
	BigFish bigFish;
	public String buildXml(Map<String, String> msgMap) {
		String xml="";
		String msgType="";
		//存入数据库
//		bo.recordEventLog(msgMap);
		//日志记录
		LOG.info("EVENT请求："+JSONObject.fromObject(msgMap));
		if ("subscribe".equals(msgMap.get("Event"))) {
			msgType="subscribe";
		}else if ("unsubscribe".equals(msgMap.get("Event"))) {
			msgType="unsubscribe";
		}else if (StringUtil.isValid(msgMap.get("MsgType"))&&!"event".equals(msgMap.get("MsgType"))) {
			msgType="message";
		}else if ("SUCCESS".equals(msgMap.get("result_code"))) {
			msgType="paymsg";
		}else if ("merchant_order".equals(msgMap.get("Event"))) {
			msgType="merchant_order";
		}else if ("user_get_card".equals(msgMap.get("Event"))) {
			msgType="user_get_card";
		}else if ("submit_membercard_user_info".equals(msgMap.get("Event"))) {
			msgType="submit_membercard_user_info";
		}else if ("bonus_shop".equals(msgMap.get("EventKey"))) {
			msgType="bonus_shop";
		}else if ("vip_info".equals(msgMap.get("EventKey"))) {
			msgType="vip_info";
		}else if ("order_info".equals(msgMap.get("EventKey"))) {
			msgType="order_info";
		}else if ("unorder_info".equals(msgMap.get("EventKey"))) {
			msgType="unorder_info";
		}else if ("TD".equals(msgMap.get("EventKey"))) {
			msgType="TD";
		}else if ("we_pay_card".equals(msgMap.get("EventKey"))) {
			msgType="we_pay_card";
		}else if ("user_del_card".equals(msgMap.get("Event"))) {
			msgType="user_del_card";
		}
		
		switch (msgType) {
		case "":
			break;
		/**
		 * 订阅
		 */
		case "subscribe":
			xml=subscribe(msgMap);
			break;
		/**
		 * 取消订阅
		 */
		case "unsubscribe":
			//下发短信
			unsubscribe(msgMap);
//			xml="";
			break;
		/**
		 * 用户回复消息
		 * TODO 暂时判断逻辑 取MsgType非event情况（未确认逻辑严谨度）
		 */
		case "message":
			xml=dealMessage(msgMap);
			break;
		/**
		 * 领取会员卡
		 */
		case "user_get_card":
//			userGetVip(msgMap);
//			xml="";
			break;
		/**
		 * 激活会员卡
		 */
		case "submit_membercard_user_info":
			activeVipCard(msgMap);
//			xml="";
			break;
		/**
		 * 【自定义】调用微信支付接口付款成功通知
		 */
		case "paymsg":
			Set<String> keyset=msgMap.keySet();
			for (String key : keyset) {
				LOG.info("【key:"+key+";value:"+msgMap.get(key)+"】");
			}
			LOG.info("【【【【【【【【【"+msgMap.get("time_end")+"成功下单 金额："+msgMap.get("total_fee")+"单位："+msgMap.get("fee_type")+"微信订单号："+msgMap.get("transaction_id")+"商户订单号:"+msgMap.get("out_trade_no")+"】】】】】】】】】】】】】】】");
			xml=forwordToCustomerService(msgMap);
			//TODO 发送用户支付成功模板消息
			break;
		/**
		 * 【内置】微信内置系统购物通知
		 */
		case "merchant_order":
			merchantOrder(msgMap);
			//微信支付独立于微信公众号，所以回传的服务器无法处理公众号消息请求
//			xml=willNotSendToCustomer(msgMap);
			break;
		/**
		 * 积分商城
		 */
		case "bonus_shop": 
			xml=textMessage(msgMap, "Coming soon ...");
			break;
		/**
		 * 会员信息
		 */
		case "vip_info":
			xml=vipInfo(msgMap);
			break;
		/**
		 * 订单信息
		 */
		case "order_info":
			xml=orderInfoTop10(msgMap);
			break;
		/**
		 * 订单信息
		 */
		case "unorder_info":
			xml=unorderInfo(msgMap);
			break;
		/**
		 * 获取现金券
		 */
		case "we_pay_card":
			xml=getWePayCard(msgMap);
			break;
		/**
		 * 退订订单
		 */
		case "TD":
			xml=textMessage(msgMap, "请回复：TD[订单编号]进行退订。如：\nTD12927088079436958888\n如需帮助，请联系客服。\n"+wish());
			break;
		/**
		 * 删除会员卡
		 */
		case "user_del_card":
			xml=delCard(msgMap);
			break;
		default:
			break;
		}
		return xml;
	}

	private String delCard(Map<String, String> msgMap) {
		String xml="";
		String result="";
		try {
			String vipCard=bo.getVipCodeByOpenId(msgMap.get("FromUserName"));
			result=ConnectWeChatTo.getVipInfo(vipCard); 
			JSONObject json=JSONObject.fromObject(result);
			String nickname=json.getString("nickname");
			String bonus=json.getString("bonus");
			JSONArray array=json.getJSONObject("user_info").getJSONArray("common_field_list");
			String mobile="";
			for (Object o : array) {
				JSONObject jo=JSONObject.fromObject(o);
				if ("USER_FORM_INFO_FLAG_MOBILE".equals(jo.getString("name"))) {
					mobile=jo.getString("value");
					break;
				}
			}
//			尊敬的${nickname}，您已成功删除钻石会员卡，我们将为您保留积分${bonus}。重新领取会员卡请点击公众号菜单-->活动中心-->领取会员。";
			Map<String, String> map=new HashMap<String, String>();
			map.put("nickname", nickname);
			map.put("bonus", bonus);
			//大鱼平台下发短信
			bigFish.sendMessage(mobile, "SMS_7295724", map);
			//返回消息
			xml=textMessage(msgMap, "尊敬的"+nickname+"，您已成功删除钻石会员卡，我们将为您保留积分"+bonus+"。重新领取会员卡请点击公众号菜单-->活动中心-->领取会员。");
		} catch (Exception e) {
			LOG.error(msgMap.get("FromUserName")+"删除会员卡，并且下发短信失败【"+result+"】", e);
		}
		return xml;
	}

	/**
	 * 方便构造格式
	 * @param value
	 * @param color
	 * @return
	 */
	public static Map<String, String> valueColorMap(String value,String color) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", value);
		map.put("color", color);
		return map;
	}
	
	/**
	 * 关注时返回消息
	 * @param msgMap
	 * @return
	 */
	private String subscribe(Map<String, String> msgMap) {
		//插入数据库
		bo.regUser(msgMap.get("FromUserName"),msgMap.get("EventKey"));
		//下发最新活动信息
		String result="";
		try {
			result=ConnectWeChatTo.sendNewsMessageByKF(msgMap.get("FromUserName"), "4WdCngwlq2vyIThOdp2TCQnViZtIHvgJ-eoAVL0hm-8");
			LOG.info("下发活动信息返回结果:"+result);
		} catch (Exception e) {
			LOG.error("返回："+result,e);
		}
		//回复文本消息
		String xml=textMessage(msgMap, "感谢您的关注！\n更多优惠请点选\"活动中心\"菜单。\n"+wish());
		return xml;
	}
	/**
	 * 返回会员信息
	 * @param msgMap
	 * @return
	 */
	private String vipInfo(Map<String, String> msgMap) {
		String xml="";
		String result="";
		try {
			String vipCard=bo.getVipCodeByOpenId(msgMap.get("FromUserName"));
			if (vipCard==null||"null".equals(vipCard)||"".equals(vipCard)) {
				result="NOCODE";
				throw new Exception(result);
			}
			result=ConnectWeChatTo.getVipInfo(vipCard); 
			JSONObject json=JSONObject.fromObject(result);
			String nickname=json.getString("nickname");
			String bonus=json.getString("bonus");
			String cardStatus=json.getString("user_card_status");
			String extraMsg="";
			if ("DELETE".equals(cardStatus)) {
				extraMsg="【温馨提示】\n系统检测到您已删除钻石会员卡，您将不再获取会员积分。\n如需重新领取请点击\"活动中心\"-->\"领取会员\"-->\"领取卡券\"-->\"查看已领取的会员卡\"-->\"领取\"。\n";
			}
			JSONArray array=json.getJSONObject("user_info").getJSONArray("common_field_list");
			String mobile="";
			for (Object o : array) {
				JSONObject jo=JSONObject.fromObject(o);
				if ("USER_FORM_INFO_FLAG_MOBILE".equals(jo.getString("name"))) {
					mobile=jo.getString("value");
					break;
				}
			}
			xml=textMessage(msgMap,"尊敬的💎钻石会员，您的会员信息如下：\n"
					+ "会员："+nickname+"\n"
					+ "积分："+bonus+"\n"
					+ "手机："+mobile+"\n"
					+extraMsg
					+wish());
		} catch (Exception e) {
			if ("NOCODE".equals(result)) {
				LOG.info("【未获取到"+msgMap.get("FromUserName")+"的会员code码】");
			} else {
				LOG.error("获取用户信息失败【"+result+"】",e);
			}
			
			/*
			 *未绑定会员卡用户
			 *推荐绑定会员卡
			 */
			try {
				result=ConnectWeChatTo.sendVipCard(msgMap.get("FromUserName"));
			} catch (Exception e2) {
				LOG.error("通过客服发送会员领取失败【"+result+"】", e2);
			}
			xml=textMessage(msgMap,"您尚未领取钻石会员。");
		}
		return xml;
	}
	
	/**
	 * 转给客服系统处理信息
	 * @param msgMap
	 * @return
	 */
	private String forwordToCustomerService(Map<String, String> msgMap) {
		String xml="";
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		Element toUserNameElement = root.addElement("ToUserName");
		Element fromUserNameElement = root.addElement("FromUserName");
		Element createTimeElement = root.addElement("CreateTime");
		Element msgTypeElement = root.addElement("MsgType");
		
		fromUserNameElement.setText(msgMap.get("ToUserName"));
		toUserNameElement.setText(msgMap.get("FromUserName"));
		createTimeElement.setText(msgMap.get("CreateTime"));
		
		//收到表情等 TODO 以“【收到不支持的消息类型，暂无法显示】”判断
		if ("【收到不支持的消息类型，暂无法显示】".equals(msgMap.get("Content"))) {
			createTimeElement.setText(String.valueOf(Calendar.getInstance().getTimeInMillis()));
			msgTypeElement.setText("text");
			Element contentElement = root.addElement("Content");
			contentElement.setText("暂不支持此消息类型！");
		}else {//转发给客服
			msgTypeElement.setText("transfer_customer_service");
		}
		xml=document.asXML();
		return xml;
	}
	
	private String returnSuccess(Map<String, String> msgMap) {
		String xml="";
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		Element returnCodeElement = root.addElement("return_code");
		returnCodeElement.setText("SUCCESS");
		xml=document.asXML();
		return xml;
	}
	
	/**
	 * 不应该返回的消息
	 * eg:如果微信小店交易成功，则会发送支付通知请求，原本可直接返回空串，
	 * BUT：如果一旦在某些情况下可以同样返回消息给用户，我想知道，这个接口可以这样用！（这是一种期待……）
	 * @param msgMap
	 * @return
	 */
	private String willNotSendToCustomer(Map<String, String> msgMap) {
		String xml="";
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		Element toUserNameElement = root.addElement("ToUserName");
		toUserNameElement.setText(msgMap.get("FromUserName"));
		Element fromUserNameElement = root.addElement("FromUserName");
		fromUserNameElement.setText(msgMap.get("ToUserName"));
		Element createTimeElement = root.addElement("CreateTime");
		createTimeElement.setText(String.valueOf(Calendar.getInstance().getTimeInMillis()));
		Element msgTypeElement = root.addElement("MsgType");
		msgTypeElement.setText("text");
		Element contentElement = root.addElement("Content");
		contentElement.setText("令人意外的事情发生了，请联系客服哦~~~。开口令：XxShXxNoXxSu2CsTM");
		xml=document.asXML();
		return xml;
	}
	
	private void activeVipCard(Map<String, String> msgMap) {
		//插入数据库
		bo.activeVipCard(msgMap);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("touser", msgMap.get("FromUserName"));
		map.put("template_id", "KMD5lORdpxSVHOInaSChAazliaBZNIFWR_k1r0f_js4");//绑定会员通知
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("first", Message.valueColorMap("恭喜您！您已成功激活钻石会员！详情：", "#000000"));
		data.put("keyword1", Message.valueColorMap(msgMap.get("UserCardCode"), "#00c800"));
		data.put("keyword2", Message.valueColorMap("永久", "#00c800"));
		data.put("remark", Message.valueColorMap(wish(), "#000000"));
		map.put("data", data);
		//发送模板消息
		ConnectWeChatTo.sendTemplateMsg(map);
	}
	
	/**
	 * 取消订阅
	 * @param msgMap
	 */
	private void unsubscribe(Map<String, String> msgMap) {
		String result="";
		try {
			String vipCard=bo.getVipCodeByOpenId(msgMap.get("FromUserName"));
			result=ConnectWeChatTo.getVipInfo(vipCard); 
			JSONObject json=JSONObject.fromObject(result);
			String nickname=json.getString("nickname");
			String bonus=json.getString("bonus");
			JSONArray array=json.getJSONObject("user_info").getJSONArray("common_field_list");
			String mobile="";
			for (Object o : array) {
				JSONObject jo=JSONObject.fromObject(o);
				if ("USER_FORM_INFO_FLAG_MOBILE".equals(jo.getString("name"))) {
					mobile=jo.getString("value");
					break;
				}
			}
			if (StringUtil.isValid(mobile)) {
//				result="尊敬的"+nickname+"，我们将为您保留积分"+bonus+"\n，期待与您再次相会，再见！";
				Map<String, String> map=new HashMap<String, String>();
				map.put("nickname", nickname);
				map.put("bonus", bonus);
				bigFish.sendMessage(mobile, "SMS_7235808", map);
			}else {
				LOG.error("用户取消关注，并且没有获取到手机号码："+msgMap.get("FromUserName"));
//				List<String> phones=bo.getPhonesInOrders(msgMap.get("FromUserName"));
//				if (phones.isEmpty()) {
//					LOG.error("用户取消关注，并且没有获取到手机号码："+msgMap.get("FromUserName"));
//				}
//				for (int i = 0; i < phones.size(); i++) {
//					if (i<5) {
//						bigFish.sendMessage(phones.get(i), "SMS_7310141", new HashMap<String, String>());
//					}else {
//						LOG.error("用户【"+msgMap.get("FromUserName")+"】没有绑定会员卡，并且他的手机号太TM多了，默认只发送5个手机，未发送手机号码：【"+phones.get(i)+"】");
//					}
//				}
			}
		} catch (Exception e) {
			LOG.error(msgMap.get("FromUserName")+"取消关注，并且下发短信失败【"+result+"】", e);
		}
	}
	
	/**
	 * 支付成功
	 * @param msgMap
	 */
	private void merchantOrder(Map<String, String> msgMap){
		JSONObject order;
		Map<String, Object> orderMap=new HashMap<String, Object>();
		//获取订单详情
		try {
			String result=ConnectWeChatTo.getDetailByOrderId(msgMap.get("OrderId"));
			if (!"ok".equals(JSONObject.fromObject(result).getString("errmsg"))) {
				throw new Exception(result);
			}
			LOG.info("获取订单返回JSON："+result);
			order=JSONObject.fromObject(result).getJSONObject("order");
			@SuppressWarnings("unchecked")
			Set<String> orderKeys=order.keySet();
			for (String orderKey : orderKeys) {
				if ("order_status".equals(orderKey)) {
					orderMap.put(orderKey, "XD");
					continue;
				}
				orderMap.put(orderKey, order.get(orderKey));
			}
			//订单信息存入数据库
			bo.recordOrder(orderMap);
			LOG.info("订单详情"+order.toString());
		} catch (Exception e) {
			LOG.error("获取订单详情失败【订单】【"+msgMap.get("OrderId")+"】",e);
			return;
		}
		String user=order.getString("buyer_nick");
		String productName=order.getString("product_name");
		String productCount=order.getString("product_count");
		double totalPrice=order.getDouble("order_total_price")/100;
		String orderId=order.getString("order_id");
		String receiverName=order.getString("receiver_name");
		String receiverMobile=StringUtil.isValid(order.getString("receiver_mobile"))?order.getString("receiver_mobile"):order.getString("receiver_phone");
		String address=order.getString("receiver_province")+order.getString("receiver_city")+order.getString("receiver_zone")+order.getString("receiver_address");
		//下发通知
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		//确认配送地址
		if (!checkAddrValide(order)) {//配送地址错误
			map.put("touser", msgMap.get("FromUserName"));
			map.put("template_id", "btLdD0yliSe2Uz-441NxUMEhXvpM3NFHGmHVpJhzUxk");//下单失败通知
			data.put("first", Message.valueColorMap("尊敬的"+user+"，您的订单交易失败并已进入退款流程，给您带来不便我们深表歉意：", "#000000"));
			data.put("keyword1", Message.valueColorMap("您所在的区域暂不支持配送", "#610B21"));//失败原因
			data.put("keyword2", Message.valueColorMap(DateUtil.getTimeStamp(), "#000000"));//失败时间
			data.put("remark", Message.valueColorMap("预计24小时内将您的款项退回支付账户。", "#000000"));
			map.put("data", data);
			//发送模板消息
			ConnectWeChatTo.sendTemplateMsg(map);
			//微信通知管理员
			String content="尊敬的管理员：\n"
					+ "有一笔交易因【配送地址错误】被系统自动退回，请及时将款项退回用户账户：\n"
					+ "订单号：【"+orderId+"元】\n"
					+ "金额：【"+totalPrice+"】\n";
			notifySuperManager(content);
			//更新数据库条目-->退货
			bo.updateOrderTDSign(orderId,msgMap.get("FromUserName"));
			return;
		} else {//下发购物成功通知
			double sumPrice=bo.getTotalPrice(msgMap.get("FromUserName"))*1.0/100;
			String mixMessage;
			//小于最少发货订单金额但购买加急服务
			if (sumPrice<Constants.MIN_ORDER_SUM_PRICE) {//小于最少发货订单金额
				mixMessage="您当前的订单总额为"
						+sumPrice+"元，未达到即时配送订单金额。"
								+ "\n默认【24小时内配送】。"
								+ "\n如需即时配送需达到"
						+Constants.MIN_ORDER_SUM_PRICE
						+"元。\n如需购买多个商品，建议使用购物车。";
			}else {//大于等于最少发货订单金额
				mixMessage="您当前的订单总额为"
						+sumPrice+"元。"
								+ "\n默认【即时配送】。\n如需购买多个商品，建议使用购物车。";
			}
			map.put("touser", msgMap.get("FromUserName"));
			map.put("template_id", "z7O15YYE_Z3oYYgqIKHlFSFXLZKOIZ846fTwqoj9Pf4");//商品购买成功通知
			data.put("first", Message.valueColorMap("尊敬的"+user+",您已成功购买：", "#000000"));
			data.put("keyword1", Message.valueColorMap(productName, "#00c800"));//商品名称
			data.put("keyword2", Message.valueColorMap(productCount, "#00c800"));//购买数量
			data.put("keyword3", Message.valueColorMap(String.valueOf(totalPrice), "#00c800"));//付款金额
			data.put("keyword4", Message.valueColorMap("\n"+DateUtil.getTimeStamp(), "#000000"));//付款时间
			data.put("keyword5", Message.valueColorMap("\n"+orderId, "#000000"));//订单编号
			data.put("remark", Message.valueColorMap(mixMessage+wish(), "#000000"));
			map.put("data", data);
			//发送模板消息
			ConnectWeChatTo.sendTemplateMsg(map);
			//发送短信
/*			Map<String, String> snsMap=new HashMap<String, String>();
			snsMap.put("product", productName);
			snsMap.put("price", String.valueOf(totalPrice));
			snsMap.put("time", DateUtil.getTimeStamp()+"\n配送地址"+order.getString("receiver_city")+order.getString("receiver_address")+"\n客户电话:"+order.getString("receiver_mobile"));
			BigFish.sendMessage(Constants.NOTICE_PHONE, "SMS_7245254", snsMap);*/
			//微信通知管理员
			String content="尊敬的管理员：\n"
					+ "您收到了一笔新的订单：\n"
					+ "金额：【"+totalPrice+"元】\n"
					+ "客户姓名："+receiverName+"\n"
					+ "电话："+receiverMobile+"\n"
					+ "商品："+productName+"\n"
					+ "数量："+productCount+"\n"
					+ "地址："+address+"\n";
			notifySuperManager(content,Constants.SUPER_SEND_GOODS_NOTIFY_PREFIX+orderId);
		}
		//获取vip账号
		String vipCode=bo.getVipCodeByOpenId(msgMap.get("FromUserName"));
		//积分计算规则
		int bonus=(int)Math.ceil(totalPrice);//向上取整
		//下发积分
		if (StringUtil.isValid(vipCode)) {
			ConnectWeChatTo.addBonus(vipCode,bonus);
		}else {
			LOG.info("用户【"+msgMap.get("FromUserName")+"】未绑定会员卡");
		}
		
	}
	/**
	 * 确认配送地址可用
	 * @param order
	 * @return
	 */
	private boolean checkAddrValide(JSONObject order) {
		if ("大连市".equals(order.getString("receiver_city"))) {
			return true;
		}
		return false;
	}

	private void userGetVip(Map<String, String> msgMap){
		bo.activeVipCard(msgMap);
	}
	
	private String textMessage(Map<String,String> msgMap,String text) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		Element toUserNameElement = root.addElement("ToUserName");
		toUserNameElement.setText(msgMap.get("FromUserName"));
		Element fromUserNameElement = root.addElement("FromUserName");
		fromUserNameElement.setText(msgMap.get("ToUserName"));
		Element createTimeElement = root.addElement("CreateTime");
		createTimeElement.setText(WeSign.timestamp());
		Element msgTypeElement = root.addElement("MsgType");
		msgTypeElement.setText("text");
		Element contentElement = root.addElement("Content");
		contentElement.setText(text);
		return document.asXML();
	}
	private String dealMessage(Map<String, String> msgMap) {
		String xml="";
		String content=msgMap.get("Content");
		//管理员专用
		if (checkSuperManager(msgMap)) {
			xml=superDeal(msgMap);
		}
		//退订商品
		if ("text".equals(msgMap.get("MsgType"))&&StringUtil.isValid(realMsg("TD", content))){
			if (realMsg("TD", content).length()==20) {
				String orderId=realMsg("TD", content);
				if (bo.updateOrderTDSign(orderId,msgMap.get("FromUserName"))) {
					List<Map<String, String>> list=bo.selectOrderInfoByOrderId(orderId);
					Map<String, String> orderMap=list.get(0);
					String user=orderMap.get("buyer_nick");
					String price=Double.valueOf(orderMap.get("order_total_price"))/100+"元";
					Map<String, Object> map = new HashMap<String, Object>();
					Map<String, Object> data = new HashMap<String, Object>();
					map.put("touser", msgMap.get("FromUserName"));
					map.put("template_id", "46eI1gIyPn0-LJiNO5dJX-EzPoWrdc1vOjPZAb2U_jo");//订单取消通知
					data.put("first", Message.valueColorMap("尊敬的"+user+",您已成功取消订单：", "#000000"));
					data.put("keyword1", Message.valueColorMap(orderId, "#610B21"));//订单编号
					data.put("keyword2", Message.valueColorMap(price, "#000000"));//订单金额
					data.put("remark", Message.valueColorMap("预计24小时内将您的款项退回支付账户。"+wish(), "#000000"));
					map.put("data", data);
					//发送模板消息
					ConnectWeChatTo.sendTemplateMsg(map);
//					xml=textMessage(msgMap, "退订成功！\n预计24小时内将您的款项退回支付账户。\n"+wish());
					notifySuperManager("TD退订订单\n"
							+ "订单："+orderId+"\n"
							+ "金额："+price+"\n"
							+ "客户："+orderMap.get("receiver_name")+"\n"
							+ "电话："+(StringUtil.isValid(orderMap.get("receiver_mobile"))?orderMap.get("receiver_mobile"):orderMap.get("receiver_phone"))+"\n"
							+ "商品："+orderMap.get("product_name")+"\n"
							+ "数量："+orderMap.get("product_count")+"\n"
							+ "地址："+orderMap.get("receiver_province")+orderMap.get("receiver_city")+orderMap.get("receiver_zone")+orderMap.get("receiver_address")+"\n");
				}else {
					xml=textMessage(msgMap, "退订失败！\n您的订单["+orderId+"]已发货或您已退订过此订单！\n如有疑问，请联系在线客服。");//或者此订单不是该用户的订单
				}
			} else {
				xml=textMessage(msgMap, "您输入的订单号码不正确！如需帮助，请联系客服。");
			}
				
		}
		//获取现金券
		else if ("text".equals(msgMap.get("MsgType"))&&StringUtil.isValid(realMsg(Constants.GET_CARD_PREFIX_STR, content))) {
			boolean isCodeRight=false;
			List<Map<String, String>> list=bo.getWePayCard(DateUtil.getyyyyMMdd());
			String errMsg="系统错误！详询在线客服。";
			for (Map<String, String> map : list) {
				String code= (Constants.GET_CARD_PREFIX_STR+map.get("card_code")).trim().toUpperCase();
				if (code.equals(content.trim().toUpperCase())) {
					String result=ConnectWeChatTo.sendCoupon(msgMap.get("FromUserName"), map.get("pay_card_id"));
					try {
						Document document =DocumentHelper.parseText(result);
				        Element root = document.getRootElement();
						if (!StringUtil.isValid(root.elementText("err_code_des"))) {
							isCodeRight=true;
						}else {
							errMsg=root.elementText("err_code_des");
						}
					} catch (Exception e) {
						LOG.error("领取现金券时出错，使用临时字符串匹配方案，XML：【"+result+"】",e);
					}
					LOG.info("用户领取现金券结果："+result);
					break;
				}
			}
			if (isCodeRight) {
				xml=textMessage(msgMap, "已成功下发！\n详情请查看\"微信支付\"公众号通知。");
			}else {
				xml=textMessage(msgMap, "领取失败！原因："+errMsg);
			}
		} 
		//转发给客服
		else {
			LOG.info("未捕获用户信息xml："+xml);
			if ("".equals(xml)) {//判断没有自动处理
				if (checkSuperManager(msgMap)) {//下达管理员操作说明
					xml=textMessage(msgMap, Constants.SUPER_MANAGER_CODE_INDRODUCTION);
				}else {
					xml=forwordToCustomerService(msgMap);//转发给客服
				}
			}
		}
		return xml;
	}
	/**
	 * 管理员专用
	 * @param msgMap
	 */
	private String superDeal(Map<String, String> msgMap) {
		String xml="";
		String result="";
		String content=msgMap.get("Content");
		//下发会员卡（给自己）
		if ("text".equals(msgMap.get("MsgType"))&&content.contains("vip")) {
			try {
				result=ConnectWeChatTo.sendVipCard(msgMap.get("FromUserName"));
			} catch (Exception e) {
				LOG.error("下发会员卡出错返回："+result,e);
				result=e.getMessage();
			}
		}
		//下发代金券(给自己)
		else if ("text".equals(msgMap.get("MsgType"))&&content.contains("coupon")) {
			result=ConnectWeChatTo.sendCoupon(msgMap);
		}
		//同步分组、商品
		else if ("text".equals(msgMap.get("MsgType"))&&"SYNC".equals(content)) {//完全匹配
			List<Map<String, String>> groupList=ConnectWeChatTo.getAllGroup();
			if (groupList.isEmpty()) {
				result="从微信获取分组信息为空";
			}else {
				if (sysncGroup(groupList)) {
					result="1.同步分组信息成功";
					if (sysncProduct(groupList)) {
						result+="2.同步商品信息成功";
					}else {
						result+="2.同步商品信息失败：数据库端";
					}
				}else {
					result="同步分组信息失败：数据库端";
				}
			}
		}
		//标记发货
		else if ("text".equals(msgMap.get("MsgType"))&&StringUtil.isValid(realMsg(Constants.SUPER_SEND_GOODS_NOTIFY_PREFIX, content))) {
			String orderId=realMsg(Constants.SUPER_SEND_GOODS_NOTIFY_PREFIX, content);
			//result=ConnectWeChatTo.sendCoupon(msgMap);
			if (bo.updateOrderFHSign(orderId)) {
				result="订单["+orderId+"]已成功标记发货";
			}else {
				result="该订单已被标记发货！无需重复标记。";
			}
			LOG.info("标记发货结果:"+result);
			String weResult=ConnectWeChatTo.signFHWithOrderId(orderId);
			LOG.info("请求微信服务器标记发货结果:"+weResult);
		}
		//查询现金券状态
		else if ("text".equals(msgMap.get("MsgType"))&&StringUtil.isValid(realMsg("CAIW", content))) {
			String payCardId=realMsg("CAIW", content);
			result=ConnectWeChatTo.getPayCardStatus(payCardId);
			try {
				Document document = DocumentHelper.parseText(result);
				Element root = document.getRootElement();
				List<Element> list=root.elements();
				result="";
				for (Element element : list) {
					result+=element.getName()+":"+element.getText()+"\n";
				}
			} catch (DocumentException e) {
				LOG.error("查询现金券状态xml转换异常",e);
			}
			
		}
		//管理员标记退货
		else if ("text".equals(msgMap.get("MsgType"))&&StringUtil.isValid(realMsg("SPTD", content))) {
			String payCardId=realMsg("SPTD", content);
		}
		//强制给客服发送消息
		else if ("text".equals(msgMap.get("MsgType"))&&StringUtil.isValid(realMsg("KF", content))) {
			String toKfMessage=realMsg("KF", content);
			msgMap.put("Content", toKfMessage);
			xml=forwordToCustomerService(msgMap);
		}
		//管理员之间群发消息
		else if ("text".equals(msgMap.get("MsgType"))&&StringUtil.isValid(realMsg("DJ", content))) {
			String djMsg=realMsg("DJ", content);
			notifySuperManager("【消息互传】：\n"+djMsg);
			xml=textMessage(msgMap, "互传消息发送成功。");
		}
		if (!"".equals(result)) {
			xml=textMessage(msgMap, result);
		}else {
			//DO NOTHING 交给普通信息管理dealMessage处理
		}
		return xml;
	}

	private boolean checkSuperManager(Map<String, String> msgMap) {
		String openId=msgMap.get("FromUserName");
		for (String manager : Constants.SUPERMANAGERS) {
			if (openId.equals(manager)) {
				return true;
			}
		}
		return false;
	}
	
	
	private String getWePayCard(Map<String, String> msgMap) {
		String message="您可直接回复以下命令领取现金券：\n";
		List<Map<String, String>> list=bo.getWePayCard(DateUtil.getyyyyMMdd());
		if (list.isEmpty()) {
			return textMessage(msgMap, "活动暂未开始，敬请留意最新活动通知！");
		}
		for (Map<String, String> map : list) {
			message+="["+(Constants.GET_CARD_PREFIX_STR+map.get("card_code")).toLowerCase()+"] "
					+ ""+map.get("card_desc")+"\n";
		}
		return textMessage(msgMap, message);
	}
	
	public void notifySuperManager(String... args) {
		for (String managerOpenId : Constants.SUPERMANAGERS) {
			String result="";
			try {
				for (String content : args) {
					result=ConnectWeChatTo.sendMessageByKF(managerOpenId, content);
				}
				LOG.info("微信通知管理员返回结果:"+result);
			} catch (Exception e) {
				LOG.error("返回："+result,e);
			}
		}
	}
	

	private String orderInfoTop10(Map<String, String> msgMap) {
		String xml="";
		try {
			String result="您近期没有消费。\n最新优惠活动尽在\"活动中心\"，赶快试试吧。\n"+wish();
			List<Map<String, String>> list=bo.selectOrderInfoTop10(msgMap.get("FromUserName"));
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map=list.get(i);
				if (i==0) {
					result="您最近的交易订单如下：\n";
				}
				String orderTime=DateUtil.getTimeStamp(Long.valueOf(map.get("order_create_time")+"000"));
				String status=map.get("order_status");
				String orderStatus="";
				switch (status) {
				case "XD":
					orderStatus="[订单已打印]";
					break;
				case "FH":
					orderStatus="[正在配送]";
					break;
				case "TD":
					orderStatus="(已退货)";
					break;
				case "CP":
					orderStatus="(已完成)";
					break;
				default:
					break;
				}
				result+=""+(i+1)+"、"+orderStatus+"\n";
				result+="时间："+orderTime+"\n";
				result+="品名："+map.get("product_name")+"\n";
				result+="数量："+map.get("product_count")+"\n";
				result+="金额："+(Double.valueOf(map.get("product_price"))/100)+"元\n";
			}
			xml=textMessage(msgMap, result);
		} catch (Exception e) {
			LOG.error("查询订单top10出错",e);
		}
		return xml;
	}
	

	private String unorderInfo(Map<String, String> msgMap) {
		String xml="";
		try {
			String result="恭喜！\n您的商品已全部查收！\n"+wish();
			List<Map<String, String>> list=bo.selectUnorderInfo(msgMap.get("FromUserName"));
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map=list.get(i);
				if (i==0) {
					result="您的待收商品如下：\n";
				}
				String status=map.get("order_status");
				String orderStatus="";
				switch (status) {
				case "XD":
					orderStatus="[订单已打印]";
					break;
				case "FH":
					orderStatus="[正在配送]";
					break;
				default:
					break;
				}
				String orderTime=DateUtil.getTimeStamp(Long.valueOf(map.get("order_create_time")+"000"));
				result+=""+(i+1)+"、"+orderStatus+"\n";
				result+="品名："+map.get("product_name")+"\n";
				result+="数量："+map.get("product_count")+"\n";
				result+="金额："+(Double.valueOf(map.get("product_price"))/100)+"元\n";
				result+="时间："+orderTime+"\n";
				result+="单号：\n"+map.get("order_id")+"\n";
			}
			xml=textMessage(msgMap, result);
		} catch (Exception e) {
			LOG.error("查询待收订单出错",e);
		}
		return xml;
	}
	/**
	 * 祝福语
	 * @return
	 */
	private String wish() {
		String wish=bo.getWish();
		if (StringUtil.isValid(wish)) {
			return wish;
		}
		return Constants.WISHES;
	}
	private String realMsg(String code,String content){
		if (content.toUpperCase().startsWith(code.toUpperCase())) {
			return content.substring(code.length(),content.length());
		}
		return "";
	}
	
	public static void main(String[] args) {
//		System.out.println(System.currentTimeMillis());
//		System.out.println(WeSign.timestamp());
//		System.out.println(realMsg("ab", "abcbab"));
	}
	
	private boolean sysncGroup(List<Map<String, String>> groupList){
		bo.emptyGroup();
		return bo.sysncGroup(groupList);
	}
	private boolean sysncProduct(List<Map<String, String>> groupList) {
		List<Map<String, String>> allProducts=ConnectWeChatTo.getAllProducts();
		for (Map<String, String> group : groupList) {
			String groupId=group.get("groupId");
			List<String> groupProductIds=ConnectWeChatTo.getAllProductByGroupId(groupId);
			for (String groupProductId : groupProductIds) {
				for (Map<String, String> productMap : allProducts) {
					if (groupProductId.equals(productMap.get("productId"))) {
						productMap.put("groupId", groupId);
					}
				}
			}
		}
		bo.emptyProducts();
		return bo.sysncAllProducts(allProducts);
	}
	/**
	 * ----------此处以下处理自有商城逻辑--------------------------------此处以下处理自有商城逻辑---------------------------------------此处以下处理自有商城逻辑-------------------------
	 */

	public void dealRealOrder(Map<String, String> msgMap) {
		String openId=msgMap.get("openid");
		int bonus=(int)Math.ceil(Double.valueOf(msgMap.get("total_fee"))/100);//向上取整
		/**
		 * 下发积分
		 */
		sendVipBonus(openId,bonus);
		/**
		 * 存入当前订单到订单状态表
		 */
		List<Map<String, String>> list=bo.getOrderDetailByOutTradeNo(msgMap);
		String result="";
		if (!list.isEmpty()) {
			Map<String, String> map=list.get(0);
			result+="时间："+map.get("oTime")+"\n";
			result+="姓名："+map.get("uName")+"\n";
			result+="电话："+map.get("uPhone")+"\n";
			result+="地址："+map.get("uAddress")+"\n";
			result+="订单编号："+msgMap.get("transaction_id")+"\n";
			result+="商户单号："+msgMap.get("out_trade_no")+"\n";
			result+="商品：\n"+map.get("products")+"\n";
			result+="合计："+(Double.valueOf(msgMap.get("total_fee"))/100)+"元\n";
			result+="实际支付："+(Double.valueOf(msgMap.get("cash_fee"))/100)+"元\n";
			result+="\n--------------\n\n\n";
		}
		LOG.info(result);
	}
	
	private void sendVipBonus(String openId,int bonus) {
		//获取vip账号
		String vipCode=bo.getVipCodeByOpenId(openId);
		//积分计算规则
		//下发积分
		if (StringUtil.isValid(vipCode)) {
			ConnectWeChatTo.addBonus(vipCode,bonus);
		}else {
			LOG.info("用户【"+openId+"】未绑定会员卡");
		}
	}
	
}
