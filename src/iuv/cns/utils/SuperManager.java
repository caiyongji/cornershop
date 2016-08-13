package iuv.cns.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 获取超级用户OpenId
 * 获取间隔10分
 */
public class SuperManager {
	private final static Log LOG = LogFactory.getLog(SuperManager.class);
	private static String[] MANGER;
	private final static int EXPIRESIN=600;// s
	private static long TIMESTAMP;

	@Deprecated
	public static String[] managers() {
		boolean isUseful = checkUsefull(MANGER)
				&& EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000 > 0;

		if (isUseful) {
			LOG.info("超级管理员SuperManager剩余时间：" + (EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000) + "秒");
			return MANGER;
		} else {
			if (successlyGetSMFromWe()) {
				return MANGER;
			} else {
				// // 出事啦！给蔡永吉发邮件吧~~
				if (!MailUtil.sendMail("【SuperManager】出事啦", "请在【"
						+ (EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000) + "秒之内解决】")) {
					LOG.fatal("作为一个托管的服务器，我竟然联系不上主人了。");
				}
				// 老版本MANGER
				return MANGER;
			}
		}
	}

	@Deprecated
	private static synchronized boolean successlyGetSMFromWe() {
		boolean isUseful = checkUsefull(MANGER)
				&& EXPIRESIN - (Calendar.getInstance().getTimeInMillis() - TIMESTAMP) / 1000 > 0;
		if (isUseful) {
			return true;
		} else {
			try {
				TIMESTAMP = Calendar.getInstance().getTimeInMillis();
				List<String> list=new ArrayList<String>();//链接数据库获取超级管理员信息 TODO 貌似不符合spring架构
				MANGER = list.toArray(new String[list.size()]);
				LOG.info("【获取ACCESS_TOKEN】" + DateUtil.getTimeStampString() + "--access_token:【" + MANGER
						+ "】expires_in:【" + EXPIRESIN + "】秒");
			} catch (Exception e) {
				LOG.error("获取【AccessToken】失败", e);
				return false;
			}
			return true;
		}
	}
	
	private static boolean checkUsefull(String[] manager) {
		if (null!=manager&&manager.length>0) {
			return true;
		}
		return false;
	}

}
