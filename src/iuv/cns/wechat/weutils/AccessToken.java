package iuv.cns.wechat.weutils;

import iuv.cns.utils.Constants;
import iuv.cns.utils.DateUtil;
import iuv.cns.utils.MailUtil;
import iuv.cns.utils.StringUtil;

import java.util.Calendar;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 微信AccessToken模型
 */
public class AccessToken {
	private final static Log LOG = LogFactory.getLog(AccessToken.class);
	private static String TOKEN;
	private static int EXPIRESIN;// s
	private static long TIMESTAMP;

	/**
	 * 对外获取TOKEN静态方法。
	 * 代码可简化，但可读性会变差。
	 * 只在需要时获取，并不自动获取。
	 * 
	 * @return
	 */
	public static String token() {
		boolean tokenIsUseful = StringUtil.isValid(TOKEN)
				&& EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000 > Constants.EXPIRES_REMAIN;

		if (tokenIsUseful) {
			LOG.info("【"+TOKEN+"】TOKEN剩余时间：" + (EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000) + "秒");
			return TOKEN;
		} else {
			if (successlyGetTokenFromWe()) {
				return TOKEN;
			} else {
				// LOG.error("获取TOKEN失败");
				// // 出事啦！给蔡永吉发邮件吧~~
				if (!MailUtil.sendMail("【TOKEN】出事啦", "请在【"
						+ (EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000) + "秒之内解决】")) {
					LOG.fatal("作为一个托管的服务器，我竟然联系不上主人了。");
				}
				// 老版本TOKEN
				return TOKEN;
			}
		}
	}

	/**
	 * 从微信服务器获取token 
	 * 2016-3-6 11:53:04增加同步声明：
	 * 	保证只请求一次，如果请求成功，则多线程队列后的全部返回TRUE
	 * 
	 * @return是否成功
	 */
	private static synchronized boolean successlyGetTokenFromWe() {
		boolean tokenIsUseful = StringUtil.isValid(TOKEN)
				&& EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000 > Constants.EXPIRES_REMAIN;
		if (tokenIsUseful) {
			return true;
		} else {
			String result = "";
			try {
				result = ConnectWeChatTo.getToken();
				JSONObject json = JSONObject.fromObject(result);
				EXPIRESIN = json.getInt("expires_in");
				TIMESTAMP = Calendar.getInstance().getTimeInMillis();
				// 最后获取，不会因为上方报错而更改可用TOKEN
				TOKEN = json.getString("access_token");
				LOG.info("【获取ACCESS_TOKEN】" + DateUtil.getTimeStampString() + "--access_token:【" + TOKEN
						+ "】expires_in:【" + EXPIRESIN + "】秒");
			} catch (Exception e) {
				LOG.error("获取【AccessToken】失败", e);
				return false;
			}
			return true;
		}
	}

}
