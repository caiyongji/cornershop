package iuv.cns.webstie;

import iuv.cns.repo.Bo;
import iuv.cns.utils.Constants;
import iuv.cns.utils.NetUtil;
import iuv.cns.wechat.weutils.ConnectWeChatTo;
import iuv.cns.wechat.weutils.JsApiTicket;
import iuv.cns.wechat.weutils.MapUtil;
import iuv.cns.wechat.weutils.WeSign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/sign")
public class SignAction {
	private final static Log LOG = LogFactory.getLog(SignAction.class);

	@Autowired
	Bo bo;
	
	@ResponseBody
	@RequestMapping(value = "/config." + Constants.APP_SUFFIX)
	public Map<String, Object> config(@RequestParam Map<String, String> params) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String currentPath = params.get("currentPath");
		LOG.info("wx.config处理URL:" + currentPath);
		String timestamp = WeSign.timestamp();
		String nonceStr = WeSign.md5(timestamp);
		Map<String, String> signatureMap = new HashMap<String, String>();
		signatureMap.put("noncestr", nonceStr);
		String jsApiTicket = JsApiTicket.ticket();
		signatureMap.put("jsapi_ticket", jsApiTicket);
		signatureMap.put("timestamp", timestamp);
		signatureMap.put("url", currentPath);
		String signature = WeSign.sha1Sign(signatureMap);

		resultMap.put("appId", Constants.APP_ID);
		resultMap.put("timestamp", timestamp);
		resultMap.put("nonceStr", nonceStr);
		resultMap.put("signature", signature);
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/pay." + Constants.APP_SUFFIX)
	public Map<String, Object> pay(@RequestParam Map<String, String> params, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String openId = params.get("openId");
		/**
		 * 确认地址
		 */
		String uAddress = params.get("uAddress");
		if (!checkAddress(uAddress)) {
			resultMap.put("errMsg", "此地址不支持配送");
			LOG.info("地址不可配送："+openId+","+uAddress);
			return resultMap;
		}
		/**
		 * 确认商品金额
		 */
		String productsStr = params.get("payProducts");
		LOG.info("payProducts:" + productsStr);
		JSONArray products = JSONArray.fromObject(productsStr);
		List<Map<String, String>> productList = new ArrayList<Map<String, String>>();
		List<String> productIds = new ArrayList<String>();
		for (Object object : products) {
			Map<String, String> map = new HashMap<String, String>();
			int productCount = 0;
			try {
				productCount = JSONObject.fromObject(object).getInt("productCount");
			} catch (Exception e) {
				LOG.error("【1461750270】Exception前台竟然传进了非数字字符：openId【" + openId + "】");
				continue;
			}
			if (productCount > 0) {
				String productId = JSONObject.fromObject(object).getString("productId");
				productIds.add(productId);
				map.put("productId", productId);
				map.put("productCount", String.valueOf(productCount));
				productList.add(map);
			}
		}
		int productTotalPrice=0;
		List<Map<String, String>> productDetailList=bo.getProductDetailByIds(productIds);
		for (Map<String, String> map : productDetailList) {
			if (!"1".equals(map.get("productStatus"))) {
				resultMap.put("errMsg", "商品：【"+map.get("productName")+"】已经售罄。");
				LOG.info("商品售罄："+openId+","+map.get("productName"));
				return resultMap;
			}
			for (Map<String, String> proCountMap : productList) {
				if (map.get("productId").equals(proCountMap.get("productId"))) {
					map.put("productCount",proCountMap.get("productCount"));
					productTotalPrice=productTotalPrice+Integer.valueOf(map.get("productPrice"))*Integer.valueOf(proCountMap.get("productCount"));
					break;
				}
			}
		}
		
		/**
		 * 存入数据库
		 */
		String outTradeNo=WeSign.nonceStr();
		for (Map<String, String> map : productDetailList) {
			map.put("openId", openId);
			map.put("outTradeNo", outTradeNo);
			map.put("uName", params.get("uName"));
			map.put("uPhone", params.get("uPhone"));
			map.put("uAddress", uAddress);
			map.put("totalFee", String.valueOf(productTotalPrice));
		}
		LOG.info(JSONArray.fromObject(productDetailList).toString());
		bo.makePreOrder(productDetailList);
		/**
		 * 开始统一下单
		 */
		LOG.info("wx.chooseWXPay处理openId:" + openId);
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", Constants.APP_ID);
		map.put("mch_id", Constants.MCH_ID);
		map.put("device_info", "WEB");
		// 随机字符串，当前时间MD5获取32位随机字符
		map.put("nonce_str", WeSign.nonceStr());
		map.put("body", productDetailList.get(0).get("productName")+"等"+productDetailList.size()+"种商品");
		// 使用MD5时间戳作为订单ID
		map.put("out_trade_no", outTradeNo);
		map.put("total_fee", String.valueOf(productTotalPrice));
		map.put("spbill_create_ip", NetUtil.getIpAddr(request));
		map.put("notify_url", Constants.WECHAT_PAY_CALLBACK_URL);
		map.put("trade_type", "JSAPI");
		map.put("openid", openId);
		String sign = WeSign.md5Sign(map, true);
		map.put("sign", sign);
		String prepayId = "";
		try {
			String returnXml = ConnectWeChatTo.placeOrder(MapUtil.asXml(map));
			LOG.info("统一下单返回信息【" + returnXml + "】");
			Document document = DocumentHelper.parseText(returnXml);
			Element root = document.getRootElement();
			prepayId = root.elementText("prepay_id");
		} catch (Exception e) {
			LOG.error("支付订单：POST微信服务器异常", e);
		}
		String timestamp = WeSign.timestamp();
		String nonceStr = WeSign.md5(timestamp);
		String packageStr = "prepay_id=" + prepayId;
		Map<String, String> paySignMap = new HashMap<String, String>();
		paySignMap.put("appId", Constants.APP_ID);
		paySignMap.put("timeStamp", timestamp);
		paySignMap.put("nonceStr", nonceStr);
		paySignMap.put("package", packageStr);
		paySignMap.put("signType", "MD5");
		String paySign = WeSign.md5Sign(paySignMap, true);
		resultMap.put("appid", Constants.APP_ID);
		resultMap.put("timestamp", timestamp);
		resultMap.put("nonceStr", nonceStr);
		resultMap.put("packageStr", packageStr);
		resultMap.put("signType", "MD5");
		resultMap.put("paySign", paySign);
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/test." + Constants.APP_SUFFIX)
	public String test(@RequestParam Map<String, String> params) {
		LOG.info("test~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		return "test";
	}
	
	private boolean checkAddress(String uAddress) {
		List<String> list=bo.getAddressKeywords();
		for (String address : list) {
			if (uAddress.contains(address)) {
				return true;
			}
		}
		return false;
	}
}
