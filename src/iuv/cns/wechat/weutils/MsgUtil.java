package iuv.cns.wechat.weutils;

import iuv.cns.utils.Constants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;

/**
 * 消息工具类
 */
public class MsgUtil {
	private final static Log LOG = LogFactory.getLog(MsgUtil.class);
	/**
	 * 加密xml
	 * @param replyMsg
	 * @param timeStamp
	 * @param nonce
	 * @return
	 */
	public static String encryptMsg(String replyMsg, String timeStamp, String nonce) {
		String result="";
		try {
			WXBizMsgCrypt cryptor = new WXBizMsgCrypt(Constants.TOKEN, Constants.ENCODING_AES_KEY, Constants.APP_ID);
			result= cryptor.encryptMsg(replyMsg, timeStamp, nonce);
		} catch (AesException e) {
			LOG.error("【加密】内容时出现异常",e);
		}
		return result;
	}
}
