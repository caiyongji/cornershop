package iuv.cns.wechat;

import iuv.cns.repo.Bo;
import iuv.cns.utils.Constants;
import iuv.cns.utils.DateUtil;
import iuv.cns.utils.MailUtil;
import iuv.cns.utils.NetUtil;
import iuv.cns.utils.StringUtil;
import iuv.cns.wechat.weutils.Message;
import iuv.cns.wechat.weutils.WeSign;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 响应公众号事件
 * 只接收来自微信服务器的请求
 */
@Controller
@RequestMapping(value = "/v")
public class WechatListener {
	private final static Log LOG = LogFactory.getLog(WechatListener.class);
	@Autowired
	Message message;
	@Autowired
	Bo bo;
	@RequestMapping(value = "/r." + Constants.APP_SUFFIX)
	public void r(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		// 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");

        //验证是否来自微信服务器
        if (!WeSign.check(signature, timestamp, nonce)) {
        	LOG.error("[【r】:ILLEGALREQUEST]--【非法请求】Time:【"+DateUtil.getTimeStampString()+"】Ip:【"+NetUtil.getIpAddr(request)+"】");
        	return;
        }
    	/**
    	 * 用于第一次校验服务器
    	 */
     	String echostr = request.getParameter("echostr");
    	if (StringUtil.isValid(echostr)) {
    		try {
				PrintWriter out = response.getWriter();
				out.print(echostr);
				out.close();
			} catch (Exception e) {
				LOG.error("校验服务器发生了异常:",e);
			}
    		return;
		}
    	/**
    	 * 响应微信服务器事件
    	 */
    	String encryptType = request.getParameter("encrypt_type");
    	//请求信息
    	Map<String, String> msgMap;
    	// 回复消息
    	String returnXml;
    	if ("aes".equals(encryptType)) {//加密模式
    		msgMap=WeSign.parseXmlCrypt(request);
    		returnXml = WeSign.encryptMsg(message.buildXml(msgMap), timestamp, nonce);
		}else {//明文模式
			msgMap=WeSign.parseXml(request);
			returnXml = message.buildXml(msgMap);
		}
		
		try {
			PrintWriter out = response.getWriter();
			out.print(returnXml);
			out.close();
		} catch (IOException e) {
			LOG.error("返回XML时产生异常：【尝试返回空串】", e);
			try {
				PrintWriter out = response.getWriter();
				out.print("");
				out.close();
			} catch (IOException e1) {
				LOG.error("返回XML时产生异常：【" + returnXml + "】", e1);
			}
		}
	}
	/**
	 * 接收支付通知
	 * @param params
	 * @param model
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/p." + Constants.APP_SUFFIX)
	public void p(@RequestParam Map<String, String> params, Model model, HttpServletRequest request,
			HttpServletResponse response) {
    	/**
    	 * 响应微信服务器事件
    	 */
    	Map<String, String> msgMap=WeSign.parseXml(request);
    	Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		if (!StringUtil.isValid(msgMap.get("result_code"))) {
			LOG.error("[【疑似DDNS】:ILLEGALREQUEST]--【非法请求】Time:【"+DateUtil.getTimeStampString()+"】Ip:【"+NetUtil.getIpAddr(request)+"】");
			MailUtil.sendMail("【疑似DDNS】", "[【疑似DDNS】:ILLEGALREQUEST]--【非法请求】Time:【"+DateUtil.getTimeStampString()+"】Ip:【"+NetUtil.getIpAddr(request)+"】");
			return;
		}
		Set<String> set = msgMap.keySet();
		String logMsg="统一下单返回msgMap数据：";
		for (String key : set) {
			logMsg+="【"+key+":"+msgMap.get(key)+"】";
		}
		LOG.info(logMsg);
		if ("SUCCESS".equals(msgMap.get("result_code"))) {
			/**
			 * 【重要】确认是否是虚假请求
			 */
			if (checkIsRealOrder(msgMap)) {
				/**
				 * 插入真正支付订单库
				 */
				//过滤重复请求
				if (!bo.checkRealOrderExist(msgMap.get("out_trade_no"))) {
					bo.makeRealOrder(msgMap);
					message.dealRealOrder(msgMap);
				}
				Element element = root.addElement("return_code");
				element.setText("SUCCESS");
				LOG.info("【【【【【【【【【成功支付:订单："+msgMap.get("transaction_id")+"金额："+msgMap.get("cash_fee")+"】】】】】】】】】】】】】】");
			} else {
				LOG.error("[【虚假订单消息】:ILLEGALREQUEST]--【非法请求】Time:【"+DateUtil.getTimeStampString()+"】Ip:【"+NetUtil.getIpAddr(request)+"】");
				MailUtil.sendMail("【重要】【攻击】【黑客】虚假订单", "请调查以下信息：\n微信单号："+msgMap.get("transaction_id"));
				return;
			}
		}else {
			Element element = root.addElement("return_code");
			element.setText("SUCCESS");
			Element element1 = root.addElement("return_msg");
			//错误信息，返回错误代码
			element1.setText(msgMap.get("err_code"));
			//记录错误
			LOG.error("【重要】支付错误，代码："+msgMap.get("err_code")+"描述："+msgMap.get("err_code_des")+"微信单号："+msgMap.get("transaction_id"));
		}
		String returnXml = document.asXML();
		try {
			PrintWriter out = response.getWriter();
			out.print(returnXml);
			out.close();
		} catch (IOException e) {
			LOG.error("返回XML时产生异常：【" + returnXml + "】", e);
		}
	}
	
	/**
	 * 检验订单支付是否是真实消息
	 * @param msgMap
	 * @return
	 */
	private boolean checkIsRealOrder(Map<String, String> msgMap) {
		String sign=msgMap.get("sign");
		msgMap.remove("sign");
		if (WeSign.md5Sign(msgMap, true).equals(sign)) {
			return true;
		}
		return false;
	}
}
