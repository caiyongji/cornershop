package iuv.cns.msgplat;

import iuv.cns.repo.Bo;
import iuv.cns.utils.Constants;
import iuv.cns.utils.NetUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * 限制：
 * 短信验证码，使用同一个签名，对同一个手机号码发送短信验证码，允许
 * 【每分钟1条】，
 * 累计
 * 【每小时7条】。 
 * 短信通知，使用同一签名、同一模板，对同一手机号发送短信通知，
 * 允许
 * 【每天50条】（自然日）。
 */
@Component
public class BigFish {
	private final static Log LOG = LogFactory.getLog(BigFish.class);
	@Autowired
	Bo bo;

	/**
	 * 
	 * @param toUser 用户手机号
	 * @param template 模板
	 * @param param 模板变量键值对
	 * @return
	 */
	public String sendMessage(String toUser,String template,Map<String, String> param) {
		String result="";
		if (!bo.isMsgMoreToday(toUser, template)) {
			LOG.info("下发短信数量超出额定数量,手机【"+template+"】模板【"+template+"】");
			return result;
		}
		try {
			String urlStr=Constants.ALI_BIGFISH_MSG_URL
					+"?sms_type=normal"
					+ "&sms_free_sign_name="+Constants.ALI_SIGN_NAME
					+ "&rec_num="+toUser
					+ "&sms_template_code="+template
					+ "&sms_param="+URLEncoder.encode(JSONObject.fromObject(param).toString(), "UTF-8");
			Map<String, String> headers=new HashMap<String, String>();
			headers.put("X-Ca-Key", Constants.ALI_CA_KEY);
			headers.put("X-Ca-Secret", Constants.ALI_SECRET);
			result=NetUtil.connectUrlGetData(urlStr, headers);
			if (!JSONObject.fromObject(result).getBoolean("success")) {
				throw new Exception(result);
			}
			LOG.info("成功下发短信：\n手机号【"+toUser+"】\n模板【"+template+"】内容【"+JSONObject.fromObject(param).toString()+"】\n");
			//插入数据库
			bo.storeSnsMsg(toUser, template);
		} catch (Exception e) {
			LOG.error("下发短信出错【"+toUser+"】：【"+result+"】",e);
		}
		return result;
	}
	public static void main(String[] args) {
//		Map<String, String> map= new HashMap<String, String>();
//		map.put("customer","蔡永吉【蔡永吉】");
//		System.out.println(sendMessage("18604284123", "SMS_6807841",map));;
//		Map<String, String> param=new HashMap<>();
//		String abc=JSONObject.fromObject(null).toString();
//		System.out.println(abc);
	}
}
