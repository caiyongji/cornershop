package iuv.cns.utils;

import iuv.cns.wechat.weutils.ConnectWeChatTo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadUtil implements Runnable{
	private final static Log LOG = LogFactory.getLog(ThreadUtil.class);
	private final String key;
	private final CountDownLatch latch;
	private final ConcurrentHashMap<String, Object> resultMap;
	private final Map<String, Object> paramMap;
	public ThreadUtil(String key,Map<String, Object> paramMap,CountDownLatch latch,ConcurrentHashMap<String, Object> resultMap){
		this.key=key;
		this.paramMap=paramMap;
		this.latch=latch;
		this.resultMap=resultMap;
	}
	@Override
	public void run() {
		switch (key) {
		case "userinfo":
			connectWeChatToGetUserInfo();
			latch.countDown();
			break;

		default:
			break;
		}
	}
	
	private void connectWeChatToGetUserInfo(){
		String openid=(String)paramMap.get("openid");
		//根据OPENID获取用户信息
		JSONObject json2 = new JSONObject();
		try {
			String userInfoStr = ConnectWeChatTo.getUserInfo(openid);
			json2 = JSONObject.fromObject(userInfoStr);
			/**
			 * 未关注，则报错
			 * 跳转到关注页面
			 */
			json2.getString("nickname");
		} catch (Exception e) {
			LOG.error("请求【"+json2+"】获取用户【"+openid+"】信息时连接出错：",e);
			//return Constants.TO_SUBSCRIBE_PAGE;
		}
	}

}
