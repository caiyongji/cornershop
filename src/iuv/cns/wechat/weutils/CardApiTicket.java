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
 * 卡券apiticket
 */
public class CardApiTicket {
	private final static Log LOG = LogFactory.getLog(CardApiTicket.class);
	private static String TICKET;
	private static int EXPIRESIN;// s
	private static long TIMESTAMP;

	/**
	 * 对外获取TICKET静态方法。
	 * 代码可简化，但可读性会变差。
	 * 只在需要时获取，并不自动获取。
	 * 
	 * @return
	 */
	public static String ticket() {
		boolean ticketIsUseful = StringUtil.isValid(TICKET)
				&& EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000 > Constants.EXPIRES_REMAIN;

		if (ticketIsUseful) {
			LOG.info("【"+TICKET+"】TICKET剩余时间：" + (EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000) + "秒");
			return TICKET;
		} else {
			if (successlyGetCardApiTicketFromWe()) {
				return TICKET;
			} else {
				// LOG.error("获取TICKET失败");
				// // 出事啦！给蔡永吉发邮件吧~~
				if (!MailUtil.sendMail("【CARDAPITICKET】出事啦", "请在【"
						+ (EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000) + "秒之内解决】")) {
					LOG.fatal("作为一个托管的服务器，我竟然联系不上主人了。");
				}
				// 老版本TICKET
				return TICKET;
			}
		}
	}

	/**
	 * 从微信服务器获取JSAPITICKET 
	 * 2016-3-6 11:53:04增加同步声明：（COPY 2016-3-18 10:01:41）
	 * 	保证只请求一次，如果请求成功，则多线程队列后的全部返回TRUE
	 * 
	 * @return是否成功
	 */
	private static synchronized boolean successlyGetCardApiTicketFromWe() {
		boolean ticketIsUseful = StringUtil.isValid(TICKET)
				&& EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000 > Constants.EXPIRES_REMAIN;
		if (ticketIsUseful) {
			return true;
		} else {
			String result = "";
			try {
				result = ConnectWeChatTo.getCardTicket();
				JSONObject json = JSONObject.fromObject(result);
				EXPIRESIN = json.getInt("expires_in");
				TIMESTAMP = Calendar.getInstance().getTimeInMillis();
				// 最后获取，不会因为上方报错而更改可用TICKET
				TICKET = json.getString("ticket");
				LOG.info("【获取CARDAPITICKET】" + DateUtil.getTimeStampString() + "--JSAPITICKET:【" + TICKET
						+ "】expires_in:【" + EXPIRESIN + "】秒");
			} catch (Exception e) {
				LOG.error("获取【CARDAPITICKET】失败", e);
				return false;
			}
			return true;
		}
	}

}
