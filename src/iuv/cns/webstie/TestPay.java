package iuv.cns.webstie;

import iuv.cns.repo.Bo;
import iuv.cns.utils.Constants;
import iuv.cns.utils.DateUtil;
import iuv.cns.utils.NetUtil;
import iuv.cns.utils.StringUtil;
import iuv.cns.wechat.weutils.ConnectWeChatTo;
import iuv.cns.wechat.weutils.JsApiTicket;
import iuv.cns.wechat.weutils.MapUtil;
import iuv.cns.wechat.weutils.WeSign;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/t")
public class TestPay {
	@Autowired
	SqlMapClientTemplate sqlMapClientTemplate;
	@Autowired
	Bo bo;
	
	private final static Log LOG = LogFactory.getLog(TestPay.class);
	
	@RequestMapping(value = "/cns." + Constants.APP_SUFFIX)
	public String cns(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		LOG.info("【user-agent】"+request.getHeader("User-Agent"));
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[sendPay：ILLEGALREQUEST]--【非法请求】Time:【"+DateUtil.getTimeStampString()+"】Ip:【"+NetUtil.getIpAddr(request)+"】");
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		String thisPath=Constants.HTTP_DOMAIN+"/t/cns.cns";
		String openId=params.get("openId");
		String state=params.get("state");
		if (!StringUtil.isValid(openId)&&!StringUtil.isValid(state)) {
			String thisUrl="";
			try {
				thisUrl = URLEncoder.encode(thisPath,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String jumpUrl=String.format(Constants.REDIRECT_URL_BASE, thisUrl,"INIT");
			LOG.info("因无openId跳转URL:"+jumpUrl);
			return jumpUrl;
		}else if ("INIT".equals(state)) {
			String code=params.get("code");
			if (StringUtil.isValid(code)) {
				String result="";
				try {
					result=ConnectWeChatTo.getOpenidByCode(code);
					JSONObject json = JSONObject.fromObject(result);
					openId = json.getString("openid");
				} catch (Exception e1) {
					LOG.error("根据CODE【"+code+"】获取OPENID失败，返回result【"+result+"】",e1);
				}
				if (!StringUtil.isValid(openId)) {
					LOG.error("【CODE错误异常1461328098】无法从微信服务器获取OPENID，说明微信服务器异常！");
					return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
				}
				String jumpUrl="redirect:"+thisPath+"?openId="+openId;
				LOG.info("获取到openId跳转非受制微信URL:"+jumpUrl);
				return jumpUrl;
			}
			LOG.error("【微信服务器异常1461328085】重新跳转，但无法从微信服务器获取CODE，说明微信服务器异常！");
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		LOG.info("openId【"+openId+"】");
		model.addAttribute("openId",openId);
		model.addAttribute("IMG_SERVER_DOMAIN",Constants.IMG_SERVER_DOMAIN);
		return "pay/testcns";
	}
	
	@RequestMapping(value = "/t." + Constants.APP_SUFFIX)
	public String test(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("in111");
		String connectDB="111";
		try {
			connectDB=(String) sqlMapClientTemplate.queryForObject("cns.wechat.getVipCodeByOpenId","oHj5ms0AeEoa9cl1iMIXyrDcNV2o");
			System.out.println(connectDB);
			System.out.println(bo.getVipCodeByOpenId("oHj5ms0AeEoa9cl1iMIXyrDcNV2o"));
		} catch (Exception e) {
			LOG.error("测试数据库链接出错：",e);
		}
		model.addAttribute("test", connectDB);
		return "pay/test";
	}
	
	@RequestMapping(value = "/weui." + Constants.APP_SUFFIX)
	public String weui(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		return "pay/weui";
	}
	
	@RequestMapping(value = "/sendPay." + Constants.APP_SUFFIX)
	public String sendPay(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		/**
		 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx54d7fcb7b8b81146&redirect_uri=http%3A%2F%2Fcornershop.cn%2Ft%2FsendPay.cns&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect 
		 */
		LOG.info("【user-agent】"+request.getHeader("User-Agent"));
		if (!WeSign.checkIsWechatBrowser(request)) {
			LOG.error("[sendPay：ILLEGALREQUEST]--【非法请求】Time:【"+DateUtil.getTimeStampString()+"】Ip:【"+NetUtil.getIpAddr(request)+"】");
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		//TODO 【安全隐患】暂未开启HTTPS安全认证【小站可能慢】
		//redirect_uri=http%3A%2F%2Fcornershop.cn%2Ft%2FsendPay.cns
		String code=params.get("code");
		String state=params.get("state");
		if (!StringUtil.isValid(code)) {
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
//		String state=params.get("state");
		String openid="";
		String result="";
		try {
			result=ConnectWeChatTo.getOpenidByCode(code);
			JSONObject json = JSONObject.fromObject(result);
			openid = json.getString("openid");
		} catch (Exception e1) {
			LOG.error("根据CODE【"+code+"】获取OPENID失败，返回result【"+result+"】",e1);
		}
		if (!StringUtil.isValid(openid)) {
			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
		}
		Map<String, String> map=new HashMap<String, String>();
		map.put("appid", Constants.APP_ID);
		map.put("mch_id", Constants.MCH_ID);
		map.put("device_info", "WEB");
		//随机字符串，当前时间MD5获取32位随机字符
		map.put("nonce_str", WeSign.md5(String.valueOf(System.currentTimeMillis())));
		map.put("body", "Ipad mini  16G  白色");
		//非必填，但在支付成功通知中会回传，可自行设置参数，以用来确认实际付款
		map.put("attach", "iphone6s 128G 白色、DELL电脑、手机等16种商品");
		//使用MD5时间戳作为订单ID
		map.put("out_trade_no", WeSign.md5(String.valueOf(System.currentTimeMillis())));
		map.put("total_fee", "1");
		map.put("spbill_create_ip", NetUtil.getIpAddr(request));
		map.put("notify_url", Constants.WECHAT_PAY_CALLBACK_URL);
		map.put("trade_type", "JSAPI");
		map.put("openid", openid);
		String sign=WeSign.md5Sign(map,true);
		map.put("sign", sign);
		String prepayId="";
		String accessToken="";
		try {
			String returnXml=ConnectWeChatTo.placeOrder(MapUtil.asXml(map));
			LOG.info("【订单信息】---------------------------------------"+returnXml);
			Document document = DocumentHelper.parseText(returnXml);
			Element root = document.getRootElement();
			prepayId=root.elementText("prepay_id");
			accessToken=root.elementText("access_token");
		} catch (Exception e) {
			LOG.error("支付订单：POST微信服务器异常",e);
		}
		String timeStamp=WeSign.timestamp();
		String nonceStr=WeSign.md5(timeStamp);
		String packageStr="prepay_id="+prepayId;
		model.addAttribute("appid", Constants.APP_ID);
		model.addAttribute("timeStamp", timeStamp);
		model.addAttribute("nonceStr", nonceStr);
		model.addAttribute("packageStr", packageStr);
		model.addAttribute("signType", "MD5");
		Map<String, String> paySignMap=new HashMap<String, String>();
		paySignMap.put("appId", Constants.APP_ID);
		paySignMap.put("timeStamp", timeStamp);
		paySignMap.put("nonceStr", nonceStr);
		paySignMap.put("package", packageStr);
		paySignMap.put("signType", "MD5");
		String paySign=WeSign.md5Sign(paySignMap,true);
		model.addAttribute("paySign", paySign);
		
		Map<String, String> signatureMap=new HashMap<String, String>();
		signatureMap.put("noncestr", nonceStr);
		String jsApiTicket=JsApiTicket.ticket();
		signatureMap.put("jsapi_ticket", jsApiTicket);
		signatureMap.put("timestamp", timeStamp);
		signatureMap.put("url", "http://cornershop.cn/t/sendPay.cns?code="+code+"&state="+state);
		String signature=WeSign.sha1Sign(signatureMap);
		//TODO del
		model.addAttribute("jsApiTicket", jsApiTicket);
		model.addAttribute("signature", signature);
		//wx.editAddress所需签名（历史版本）
//		Map<String, String> addrSignMap=new HashMap<String, String>();
//		addrSignMap.put("accesstoken", accessToken);
//		addrSignMap.put("appid", Constants.APP_ID);
//		addrSignMap.put("noncestr", nonceStr);
//		addrSignMap.put("timestamp", timeStamp);
//		addrSignMap.put("url", "http://cornershop.cn/t/pay/sendPay.cns?code="+code+"&state="+state);
//		String addrSign=WeSign.sha1Sign(addrSignMap);
//		model.addAttribute("addrSign",addrSign);
		
		//显示用，可删除
		model.addAttribute("prepayId",prepayId);
		model.addAttribute("openid",openid);
		model.addAttribute("accessToken",accessToken);
		return "pay/testpay";
	}
	
	@RequestMapping(value = "/openAddress." + Constants.APP_SUFFIX)
	public String openAddress(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		LOG.info("【user-agent】"+request.getHeader("User-Agent"));
//		if (!WeSign.checkIsWechatBrowser(request)) {
//			LOG.error("[sendPay：ILLEGALREQUEST]--【非法请求】Time:【"+DateUtil.getTimeStampString()+"】Ip:【"+NetUtil.getIpAddr(request)+"】");
//			return Constants.REDIRECT_TO_WECHAT_ERROR_PAGE;
//		}
		String timestamp=WeSign.timestamp();
		String nonceStr=WeSign.md5(timestamp);
		Map<String, String> signatureMap=new HashMap<String, String>();
		signatureMap.put("noncestr", nonceStr);
		String jsApiTicket=JsApiTicket.ticket();
		signatureMap.put("jsapi_ticket", jsApiTicket);
		signatureMap.put("timestamp", timestamp);
		signatureMap.put("url", "http://cornershop.cn/t/openAddress.cns");
		String signature=WeSign.sha1Sign(signatureMap);
		
		model.addAttribute("appId",Constants.APP_ID);
		model.addAttribute("timestamp",timestamp);
		model.addAttribute("nonceStr",nonceStr);
		model.addAttribute("signature",signature);
		return "pay/testaddress";
	}
	
	public static void main(String[] args) throws DocumentException {
		/**
		 * 支付·签名
		 */
		Map<String, String> map= new HashMap<>();
		map.put("appId", "wx2421b1c4370ec43b");
		map.put("timeStamp", "1395712654");
		map.put("nonceStr", "e61463f8efa94090b1f366cccfbbb444");
		map.put("package", "prepay_id=u802345jgfjsdfgsdg888");
		map.put("signType", "MD5");
		String sign="";
		Set<String> set =map.keySet();
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
		sign=WeSign.md5(sign).toUpperCase();
		System.out.println(WeSign.md5Sign(map,true));
		
		/**
		 * 解析xml
		 */
		String xml="<xml><appid>wx54d7fcb7b8b81146</appid><body>Ipad mini  16G  白色</body><detail>Ipad mini  16G  白色</detail><device_info>WEB</device_info><fee_type>CNY</fee_type><mch_id>1317965601</mch_id><nonce_str>B3175F3FE79D83775CB58B82B7C5B94D</nonce_str><notify_url>http://cornershop.cn/t/sendPay.cns</notify_url><openid>oHj5ms0AeEoa9cl1iMIXyrDcNV2o</openid><out_trade_no>998ATest</out_trade_no><sign>BBE73502B84D26DF32A984F463C88CBD</sign><spbill_create_ip>123.246.52.68</spbill_create_ip><total_fee>2</total_fee><trade_type>JSAPI</trade_type></xml>";
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		String prepayId=root.elementText("prepay_id");
		System.out.println(prepayId);
		
		/**
		 * URL decode
		 */
		System.out.println(URLDecoder.decode("http%3A%2F%2Ffront.lewaimai.com%2Findex.php%3Fr%3Dlewaimaishow%2Flewaimaigod%26adminId%3D12045%26shopId%3D33769"));
		
		
		/**
		 * substring
		 */
		System.out.println("1234567890123".substring(0, 10));
		
		
	}
}