package iuv.cns.utils;

public interface Constants {
	/*---------------------------------------------------项目全局配置*/
	/** 项目后缀 */
	String APP_SUFFIX = "cns";
	String HTTP_DOMAIN ="http://cornershop.cn";
	String IMG_SERVER_DOMAIN ="http://img.cornershop.cn";
	/*---------------------------------------------------微信公众平台*/
	String APP_ID = "**********";
	String APP_SECRET = "**********";
	String TOKEN = "**********";
	String ENCODING_AES_KEY = "**********";
	/*---------------------------------------------------微信公众平台URL*/
	String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APP_ID
			+ "&secret=" + APP_SECRET;
	String GET_USER_INFO_BY_OPENID_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
	String REDIRECT_URL_BASE="redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="+APP_ID+"&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect";
	String GET_USER_OPENID_URL="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APP_ID+"&secret="+APP_SECRET+"&code=%s&grant_type=authorization_code";
	String GET_JSAPI_TICKET_URL="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

	/*---------------------------------------------------技术配置*/
	/** token刷新【最少】剩余时间（单位：秒） */
	int EXPIRES_REMAIN = 600;
	/*---------------------------------------------------管理员邮箱配置*/
	String Mail_USER = "**********@caiyongji.com";
	String Mail_PASSWD = "**********";
	String Mail_TO = "**********@126.com";
	String Mail_FROM = "**********@caiyongji.com";
	String Mail_SMPTHOST = "smtp.**********.com";
	/*---------------------------------------------------微信支付*/
	String WECHAT_PAY_URL="https://api.mch.weixin.qq.com/pay/unifiedorder";
	/** 商户号 */
	String MCH_ID="**********";
	/** 支付密钥 由TOKEN md5加密得*/
	String WECHAT_PAY_KEY="**********";
	/** 微信支付回调页 */
	String WECHAT_PAY_CALLBACK_URL="http://cornershop.cn/v/p.cns";
	/** 优惠券发放接口地址 */
	String WECHAT_PAY_COUPON_URL="https://api.mch.weixin.qq.com/mmpaymkttransfers/send_coupon";
	/** 查询优惠券批次接口地址 */
	String WECHAT_PAY_COUPON_STOCK_URL="https://api.mch.weixin.qq.com/mmpaymkttransfers/querycouponsinfo";
	/** key证书路径 */
	String CA_KEY_DIR="/usr/share/tomcat7/ca/cns.p12";
	/** strust证书路径 */
	String CA__TRUST_DIR="/usr/share/tomcat7/ca/rootca.pem";
	/** 证书密码 */
	char[] CA_PWD=MCH_ID.toCharArray();
	/** 根据商品ID查看商品明细 */
	String WECHAT_PAY_MERCHANT_PRODUCT_DETAIL_BY_ID="https://api.weixin.qq.com/merchant/get?access_token=%s";
	/** 根据订单ID查看订单明细 */
	String WECHAT_PAY_MERCHANT_ORDER_DETAIL_BY_ID="https://api.weixin.qq.com/merchant/order/getbyid?access_token=%s";
	/*---------------------------------------------------错误页*/
	/** 跳转到微信报错页 */
	String REDIRECT_TO_WECHAT_ERROR_PAGE="redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="+APP_ID+"&redirect_uri=&response_type=code&scope=snsapi_base&state=#wechat_redirect";
	String TO_SUBSCRIBE_PAGE="subscribe";
	/*---------------------------------------------------短信平台*/
	/** 大鱼短信平台接口 */
	String ALI_BIGFISH_MSG_URL="https://ca.aliyuncs.com/gw/alidayu/sendSms";
	/** 大鱼key */
	String ALI_CA_KEY="**********";
	/** 大鱼secret */
	String ALI_SECRET="**********";
	/** 短信平台签名 */
	String ALI_SIGN_NAME="街角小店";
	/** 短信平台同一天、统一模板、同一手机号发送短信有上限 */
	int ALI_MSG_MAX_COUNT=3;
	/*---------------------------------------------------SNS短信模板*/
	/** 下单成功通知商家短信模板 */
	String SNS_TEMPLATE_TO_BUSINESS="";
	/** 取消关注发送短信模板 */
	String SNS_TEMPLATE_UNSUBCRIBE="";
	
	/*---------------------------------------------------微信模板消息*/
	String WECHAT_SEND_TEMPLATE_MESSAGE_URL="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
	
	/*---------------------------------------------------会员卡券*/
	/** 会员卡（微信目前2016-4-2 02:07:43只支持【单】会员卡） */
	String VIP_CAR_ID="pHj5ms1JKypGR-**********";
	/** 获取会员信息 */
	String GET_VIP_INFO_URL="https://api.weixin.qq.com/card/membercard/userinfo/get?access_token=%s";
	/** 更新会员信息 */
	String VIP_BONUS_URL="https://api.weixin.qq.com/card/membercard/updateuser?access_token=%s";
	/** 获取卡券api_ticket */
	String GET_CAR_API_TICKET="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=wx_card";
	/** 领现金券命令前缀 */
	String GET_CARD_PREFIX_STR="CNS";
	/** 查询代金券状态URL */
	String GET_PAY_CARD_STATUS_URL="https://api.mch.weixin.qq.com/mmpaymkttransfers/query_coupon_stock";
	/*---------------------------------------------------订单通知手机*/
	String NOTICE_PHONE="**********";
	/*---------------------------------------------------客服发送消息接口*/
	String KF_SEND_MESSAGE_URL="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
	/*---------------------------------------------------超级管理员*/
	String[] SUPERMANAGERS={"oHj5ms0AeEoa9cl1iMIXyrDcNV2o","oHj5ms3OeYstfvhm1jnAROBQs7gY"};
	/** 标记发货命令前缀 */
	String SUPER_SEND_GOODS_NOTIFY_PREFIX="FH";
	/** 管理员命令说明 */
	String SUPER_MANAGER_CODE_INDRODUCTION="尊敬的管理员用户，您拥有以下命令特权：\n"
			+ "1、FH+[订单编号]：将订单标记为发货\n"
			+ "2、CAIW+[现金券编号]：查询现金券使用情况\n"
			+ "3、KF+[消息内容]：强制发送消息给客服\n"
			+ "4、DJ+[消息内容]：管理员消息互传\n"
			+ "5、SYNC：同步商品价格等信息到数据库";
	/** 默认祝福语 */
	String WISHES="街角小店祝您购物愉快。";
	/** 最低订单金额 */
	double MIN_ORDER_SUM_PRICE=39.00;
	/** 标记发货请求URL */
	String SIGN_WECHAT_FH_URL="https://api.weixin.qq.com/merchant/order/setdelivery?access_token=";
	/*---------------------------------------------------微信小店*/
	/** 获取所有商品分组 */
	String SHOP_GET_ALL_GROUPS_URL="https://api.weixin.qq.com/merchant/group/getall?access_token=";
	/** 根据分组获取商品 */
	String SHOP_GET_ALL_PRODUCTS_BY_GROUP_URL="https://api.weixin.qq.com/merchant/group/getbyid?access_token=";
	/** 根据商品ID获取商品详细信息 */
	String SHOP_PRODUCT_DETAIL_URL="https://api.weixin.qq.com/merchant/get?access_token=";
	/** 获取指定状态的商品 */
	String SHOP_GET_PRODUCTS_BY_STATUS_URL="https://api.weixin.qq.com/merchant/getbystatus?access_token=";
	
}
