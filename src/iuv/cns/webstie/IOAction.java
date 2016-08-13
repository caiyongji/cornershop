package iuv.cns.webstie;

import iuv.cns.repo.Bo;
import iuv.cns.utils.Constants;
import iuv.cns.utils.DateUtil;
import iuv.cns.utils.NetUtil;
import iuv.cns.utils.StringUtil;
import iuv.cns.wechat.weutils.ConnectWeChatTo;
import iuv.cns.wechat.weutils.WeSign;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/io")
public class IOAction {
	private final static Log LOG = LogFactory.getLog(IOAction.class);

	@Autowired
	Bo bo;

	@RequestMapping(value = "/cns." + Constants.APP_SUFFIX)
	public String cns(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		LOG.info("【user-agent】" + request.getHeader("User-Agent"));
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[sendPay：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		String thisPath = Constants.HTTP_DOMAIN + "/io/cns.cns";
		String openId = params.get("openId");
		String state = params.get("state");
		if (!StringUtil.isValid(openId) && !StringUtil.isValid(state)) {
			String thisUrl = "";
			try {
				thisUrl = URLEncoder.encode(thisPath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.error("理论上不会出现");
			}
			String jumpUrl = String.format(Constants.REDIRECT_URL_BASE, thisUrl, "INIT");
			LOG.info("因无openId跳转URL:" + jumpUrl);
			return jumpUrl;
		} else if ("INIT".equals(state)) {
			String code = params.get("code");
			if (StringUtil.isValid(code)) {
				String result = "";
				try {
					result = ConnectWeChatTo.getOpenidByCode(code);
					JSONObject json = JSONObject.fromObject(result);
					openId = json.getString("openid");
				} catch (Exception e1) {
					LOG.error("根据CODE【" + code + "】获取OPENID失败，返回result【" + result + "】", e1);
				}
				if (!StringUtil.isValid(openId)) {
					LOG.error("【CODE错误异常1461328098】无法从微信服务器获取OPENID，说明微信服务器异常！");
					return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
				}
				String jumpUrl = "redirect:" + thisPath + "?openId=" + openId;
				LOG.info("获取到openId跳转非受制微信URL:" + jumpUrl);
				return jumpUrl;
			}
			LOG.error("【微信服务器异常1461328085】重新跳转，但无法从微信服务器获取CODE，说明微信服务器异常！");
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		LOG.info("openId【" + openId + "】");
		model.addAttribute("openId", openId);
		model.addAttribute("IMG_SERVER_DOMAIN", Constants.IMG_SERVER_DOMAIN);
		model.addAttribute("MIN_ORDER_SUM_PRICE", Constants.MIN_ORDER_SUM_PRICE * 100);// 单位：分
		return "cornershop";
	}

	@RequestMapping(value = "/detail." + Constants.APP_SUFFIX)
	public String productdetail(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		model.addAttribute("productid", params.get("productid"));
		return "productdetail";
	}

	@RequestMapping(value = "/vip." + Constants.APP_SUFFIX)
	public String vip(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[vip：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		model.addAttribute("vip", "viptest");
		return "vip";
	}

	/**
	 * 会员中心 https://open.weixin.qq.com/connect/oauth2/authorize?appid=
	 * wx54d7fcb7b8b81146
	 * &redirect_uri=http%3a%2f%2fcornershop.cn%2fio%2fvipcenter
	 * .cns&response_type=code&scope=snsapi_base#wechat_redirect
	 * 
	 * @param params
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/vipcenter." + Constants.APP_SUFFIX)
	public String vipcenter(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[vipcenter：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		String code = params.get("code");
		if (!StringUtil.isValid(code)) {
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		// 获取OPENID
		String openid = "";
		JSONObject json1 = new JSONObject();
		try {
			String jsonResult = ConnectWeChatTo.getOpenidByCode(code);
			json1 = JSONObject.fromObject(jsonResult);
			openid = json1.getString("openid");
		} catch (Exception e1) {
			LOG.error("【根据CODE获取OPENID失败】" + json1, e1);
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		// 根据OPENID获取用户信息
		JSONObject json2 = new JSONObject();
		try {
			String userInfoStr = ConnectWeChatTo.getUserInfo(openid);
			json2 = JSONObject.fromObject(userInfoStr);
			/**
			 * 未关注，则报错 跳转到关注页面
			 */
			json2.getString("nickname");
		} catch (Exception e) {
			LOG.error("请求【" + json2 + "】获取用户【" + openid + "】信息时连接出错：", e);
			return Constants.TO_SUBSCRIBE_PAGE;
		}
		String nickname = json2.getString("nickname");
		String sex = json2.getString("sex");
		switch (sex) {
		case "1":
			sex = "男";
			break;
		case "2":
			sex = "女";
			break;
		default:
			sex = "未知";
			break;
		}
		String headimgurl = json2.getString("headimgurl");
		model.addAttribute("nickname", nickname);
		model.addAttribute("sex", sex);
		model.addAttribute("headimgurl", headimgurl);
		return "vipcenter";
	}

	@RequestMapping(value = "/testvipcenter." + Constants.APP_SUFFIX)
	public String testvipcenter(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String nickname = "小米壳";
		String sex = "1";
		switch (sex) {
		case "1":
			sex = "男";
			break;
		case "2":
			sex = "女";
			break;
		default:
			sex = "未知";
			break;
		}
		String headimgurl = "http://wx.qlogo.cn/mmopen/PiajxSqBRaEL823cpCH1RdOSQItRiaaJjh5TDqXNsYqqLibAaib93BSeIaibt32uTxoj26BTiaGHTOVO75UGNAFU3fbg/0";
		model.addAttribute("nickname", nickname);
		model.addAttribute("sex", sex);
		model.addAttribute("headimgurl", headimgurl);
		return "vipcenter";
	}

	@ResponseBody
	@RequestMapping(value = "/shopCart." + Constants.APP_SUFFIX)
	public Map<String, Object> initShopCart(@RequestParam Map<String, String> params, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[shopCart：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return resultMap;
		}
		String openId = params.get("openId");
		List<Map<String, String>> products = bo.getUserShopCart(openId);
		for (Map<String, String> map : products) {
			String pImg = "products/" + NetUtil.encode(map.get("pNm")) + ".jpg";
			map.put("pImg", pImg);
		}
		LOG.info("initShopCart-->products:" + JSONArray.fromObject(products).toString());
		resultMap.put("products", products);
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/goPay." + Constants.APP_SUFFIX)
	public Map<String, Object> goPay(@RequestParam Map<String, String> params, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (!WeSign.checkIsWechatBrowser(request)) {
				LOG.error("[goPay：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
						+ NetUtil.getIpAddr(request) + "】");
				return resultMap;
			}
			String openId = params.get("openId");
			String totalPrice = params.get("totalPrice");
			List<Map<String, String>> addressList = bo.getUserAddress(openId);
			String uAddress;
			String uName;
			String uPhone;
			if (addressList.isEmpty()) {
				uAddress = "请选择收货地址";
				uName = "";
				uPhone = "";
			} else {
				Map<String, String> addressMap = addressList.get(0);
				uAddress = addressMap.get("uAddress");
				uName = addressMap.get("uName");
				uPhone = addressMap.get("uPhone");
			}
			resultMap.put("uAddress", uAddress);
			resultMap.put("uName", uName);
			resultMap.put("uPhone", uPhone);
			// 已达到活动标准：活动减免
			List<Map<String, String>> minusList = bo.getActivityEvents(totalPrice);
			LOG.error("minusList:" + JSONArray.fromObject(minusList).toString());
			if (!minusList.isEmpty()) {
				Map<String, String> minusMap = minusList.get(0);
				resultMap.put("minus", minusMap);
			}
			// 即将到活动标准：活动促销
			List<Map<String, String>> promotionList = bo.getPromotionEvents(totalPrice);
			LOG.error("promotionList:" + JSONArray.fromObject(promotionList).toString());
			if (!promotionList.isEmpty()) {
				Map<String, String> promotionMap = promotionList.get(0);
				resultMap.put("promotion", promotionMap);
			}
			LOG.info("openId:" + openId + ",totalPrice:" + totalPrice);
		} catch (Exception e) {
			LOG.error("测试错误", e);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/catalog." + Constants.APP_SUFFIX)
	public List<Map<String, Object>> catalog(@RequestParam Map<String, String> params, HttpServletRequest request) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[catalog：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return list;
		}
		List<Map<String, String>> catalogList = bo.getCatalog();
		Set<String> catlogIdSet = new HashSet<String>();
		for (Map<String, String> map : catalogList) {
			catlogIdSet.add(map.get("catalogId"));
		}
		String[] arrays = catlogIdSet.toArray(new String[catlogIdSet.size()]);
		Arrays.sort(arrays);
		for (String catalogId : arrays) {
			Map<String, Object> catalogMap = new HashMap<String, Object>();
			List<Map<String, String>> groupList = new ArrayList<Map<String, String>>();
			boolean flag = true;
			for (Map<String, String> map : catalogList) {
				if (catalogId.equals(map.get("catalogId"))) {
					if (flag) {
						catalogMap.put("catalogId", map.get("catalogId"));
						catalogMap.put("catalogName", map.get("catalogName"));
						flag = false;
					}
					Map<String, String> groupMap = new HashMap<String, String>();
					groupMap.put("groupId", map.get("groupId"));
					groupMap.put("groupName", map.get("groupName"));
					String groupImg = NetUtil.encode(map.get("groupImg"));
					groupMap.put("groupImg", groupImg);
					groupList.add(groupMap);
				}
				catalogMap.put("groupList", groupList);
			}
			list.add(catalogMap);
		}
		return list;
	}

	@ResponseBody
	@RequestMapping(value = "/cataProducts." + Constants.APP_SUFFIX)
	public List<Map<String, String>> cataProducts(@RequestParam Map<String, String> params, HttpServletRequest request) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[cataProducts：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return list;
		}
		String cog = params.get("cog");
		String cid = params.get("cid");
		String sortOrd = params.get("sort");
		if ("cat".equals(cog)) {
			list = bo.getProductsByCatalog(cid, sortOrd);
		} else if ("grp".equals(cog)) {
			list = bo.getProductsByGroup(cid, sortOrd);
		}
		for (Map<String, String> map : list) {
			String pImg = "products/" + NetUtil.encode(map.get("pNm")) + ".jpg";
			map.put("pImg", pImg);
		}
		return list;
	}

	@ResponseBody
	@RequestMapping(value = "/updateShopCart." + Constants.APP_SUFFIX)
	public void updateShopCart(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[updateShopCart：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
		}
		String openId = (String) params.get("openId");
		String productsStr = (String) params.get("products");
		LOG.info("更新购物车products:" + productsStr);
		bo.emptyUserShopCart(openId);
		JSONArray products = JSONArray.fromObject(productsStr);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
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
				map.put("productId", productId);
				map.put("productCount", String.valueOf(productCount));
				list.add(map);
			}
		}
		if (!list.isEmpty()) {
			params.put("products", list);
			bo.insertUserShopCart(params);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/shop." + Constants.APP_SUFFIX)
	public List<Map<String, Object>> shop(@RequestParam Map<String, String> params, HttpServletRequest request) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[catalog：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return list;
		}
		List<Map<String, String>> shopGroupList = bo.getShop();
		Set<String> groupIdsSet = new HashSet<String>();
		for (Map<String, String> map : shopGroupList) {
			groupIdsSet.add(map.get("groupId"));
		}
		String[] arrays = groupIdsSet.toArray(new String[groupIdsSet.size()]);
		Arrays.sort(arrays);
		for (String groupId : arrays) {
			Map<String, Object> groupMap = new HashMap<String, Object>();
			List<Map<String, String>> itemsList = new ArrayList<Map<String, String>>();
			boolean flag = true;
			for (Map<String, String> map : shopGroupList) {
				if (groupId.equals(map.get("groupId"))) {
					if (flag) {
						groupMap.put("groupId", map.get("groupId"));
						groupMap.put("groupCols", map.get("groupCols"));
						groupMap.put("groupFunc", map.get("groupFunc"));
						groupMap.put("groupTitle", map.get("groupTitle"));
						flag = false;
					}
					Map<String, String> itemMap = new HashMap<String, String>();
					itemMap.put("itemId", map.get("itemId"));
					itemMap.put("itemImg", map.get("itemImg"));
					itemMap.put("itemFunc", map.get("itemFunc"));
					itemMap.put("itemTitle", map.get("itemTitle"));
					itemsList.add(itemMap);
				}
				groupMap.put("items", itemsList);
			}
			list.add(groupMap);
		}
		return list;
	}

	@ResponseBody
	@RequestMapping(value = "/updateUserAddress." + Constants.APP_SUFFIX)
	public void updateUserAddress(@RequestParam Map<String, String> params, HttpServletRequest request) {
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[updateUserAddress：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
		}
		bo.insertUserAddress(params);
	}

	@ResponseBody
	@RequestMapping(value = "/getUsrMine." + Constants.APP_SUFFIX)
	public Map<String, Object> getUsrMine(@RequestParam Map<String, String> params, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[updateUserAddress：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return resultMap;
		}
		String openId = params.get("openId");
		JSONObject userJson = new JSONObject();
		try {
			String userInfoStr = ConnectWeChatTo.getUserInfo(openId);
			userJson = JSONObject.fromObject(userInfoStr);
			String nickname = userJson.getString("nickname");
			String headimgurl = userJson.getString("headimgurl");
			resultMap.put("nickname", nickname);
			resultMap.put("headimgurl", headimgurl);
			String vipCard=bo.getVipCodeByOpenId(openId);
			if (StringUtil.isValid(vipCard)) {
				String result=ConnectWeChatTo.getVipInfo(vipCard); 
				JSONObject vipJson=JSONObject.fromObject(result);
				String bonus=vipJson.getString("bonus");
				String cardStatus=vipJson.getString("user_card_status");
				resultMap.put("vipStatus", cardStatus);
				resultMap.put("vipKind", "DIAMOND");
				resultMap.put("vipBonus", bonus);
			}else {
				resultMap.put("vipKind", "NORMAL");
			}
		} catch (Exception e) {
			LOG.error("请求【" + userJson + "】获取用户【" + openId + "】信息时连接出错：", e);
		}
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getUsrOrder." + Constants.APP_SUFFIX)
	public List<Map<String, String>> getUsrOrder(@RequestParam Map<String, String> params, HttpServletRequest request) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[updateUserAddress：ILLEGALREQUEST]--【非法请求】Time:【" + DateUtil.getTimeStampString() + "】Ip:【"
					+ NetUtil.getIpAddr(request) + "】");
			return list;
		}
		list=bo.getUsrOrders(params);
		return list;
	}
}