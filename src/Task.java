/**
 * 项目代办事项
 * 2016-3-18 21:25:41创意
 * @author CaiYongji.
 *
 */
public class Task {

/**
 * TODO 项目迁移所需
 * nginx 配置
 * tomcat 官方
 * jre1.7-80 官方（先卸载原有openjdk）
 * 替换US_export_policy.jar local_policy.jar
 * 存放p12证书到指定路径
 * 配置对应JDBC
 * 解析对应域名2016-4-19 13:49:16
 */
	
/**
 * 项目迁移（重新配置服务器·北京）
 * 1、nginx1.6.3
 * 	1.0
 * 	yum install nginx
 * 	1.1配置nginx.conf
 * 	配置文件在百度云/cornershop/LINUX
 * 2.jre 7u80
 *  2.0
 *  http://download.oracle.com/otn-pub/java/jdk/7u80-b15/jre-7u80-linux-x64.rpm?AuthParam=1462436582_b462d747f60d09dc18eed003b9cdba5e
 * 	2.1 安装lrzsz
 * 	yum install lrzsz
 * 	2.2 安装jre
 * 	yum install jre-7u80-linux-x64.rpm
 * 3.tomcat
 *  3.0 解压缩
 *  unzip apache-tomcat-7.0.69.zip
 *  mv apache-tomcat-7.0.69 /usr/share/tomcat7
 *  3.1 配置service tomcat start
 *  将 tomcat(百度云)文件复制到 /etc/init.d
 *  3.2 授权
 *  chmod 777 tomcat 
 * 4.CA
 * 	4.0 rz [cns.p12 rootca.pem]
 * 5.微信加密需要JCE无限制权限策略文件
 * 5.0 下载地址
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
 * 5.1 security路径
 *  cd /usr/java/jre1.7.0_80/lib/security
 *  rm local_policy.jar US_export_policy.jar 
 *  
 */


/**
 * CAI 【待办】封装filter过滤非微信浏览器请求
 * CAI UI固定-“露底”伪BUG，UI不友善
 * CAI js封装到include
 * CAI android支付问题
 * CAI sessionStorage 缓存图片
 * COMPLETE WEUI.MIN.CSS替换成min版·最优：CDN加速
 * TODO 家庭会员概念，多人共享积分
 * TODO 错误页改成，请先关注公众号
 * TODO 设置nginx 错误页
 * CAI 内容分享提示参数错误【禁止分享】需要引入jsapi(请求微信次数似乎影响浏览速度，同时需要解决)
 * TODO 会员中心、关注页“露底”UI改善
 * CAI 暂用微信官方全部接口，实现快速部署，以完成业务逻辑
 * CAI 添加微信支付双向证书，完成促销活动
 * TODO 增加购物车按钮，提示敬请期待及配送规则
 * CAI 【重要！！！！】待办：1.关注短信通知，显得大气！2.会员删除会员卡短信通知。3.活动中心添加“最新活动”，【弹出】活动【多】图文消息4.“我的”菜单中【单链接】增加配送说明、会员卡领取说明等说明
 */



/**
 * MIND 今日2016-3-18 21:28:19更新自助核销卡券功能·微信初衷：结合实体店·可利用：活动兑换抽奖，自动领取积分
 * 
 */


/**
 * LOG
 * 
 * 截止(开始LOG)2016-3-21 14:56:00 微信公众平台接口调用、加密解密、支付功能全部测试通过，存在一定业务逻辑问题，可能需要重构
 * 2016-3-21 14:55:16 添加css webkit-overflow-scrolling 回弹效果
 * 2016-3-21 14:56:46 解决H5在微信中“露底”UI问题（同时引发，页面假死（显示源自cornershop后，整个页面锁定）问题）
 * 2016-3-21 15:24:42 移除“防露底”，代码收藏，【附录1】，因为强制布局的话会组织IPHONE点击头部，内容回到顶端事件（双刃剑啊~~）
 * 2016-3-21 22:39:39 android 和IOS H5样式不同，如webkit-overflow-scrolling在android中不支持【小米NOTE测试】
 * 2016-3-22 14:10:47 通用图片，缓存 img 转BASE64 存储，（data：（替换：成:）image/png;base64,iVBORw0K...）
 * 2016-3-22 15:25:52 加入lazyload.min.js 第三方，作者jieyou
 * 2016-3-22 16:36:02 取消异步加载，影响了原有的缓存使用逻辑。
 * 2016-3-23 15:13:34 添加领取会员页面（加入待办：家庭会员想法）
 * 2016-3-28 18:07:46 变更zepto模块为：zepto event ajax form selector touch
 * 2016-3-28 18:08:15 轮播插件固定，判断左右滑动，并且滑动时不产生body的滚动效果
 * 2016-3-28 18:09:02 初步设计商品列表，2列式，活动页 4列
 * 2016-3-29 23:04:35 导入 http-client 等3个jar包
 * 2016-3-30 01:46:35 微信服务器p12证书坑死我了（正在解决……）
 * 2016-3-30 02:52:26 命令历史： ./keytool -import -noprompt -trustcacerts -alias cns -file rootca.pem -keystore /usr/java/jre1.7.0_80/lib/security/cacerts
 * 2016-3-30 03:07:01 命令1：./keytool -import -noprompt -trustcacerts -alias cns -file rootca.pem -keystore ~/mykeystore
 * 2016-3-30 03:06:41 命令2：./keytool -importkeystore -srckeystore ~/mykeystore -destkeystore /usr/java/jre1.7.0_80/lib/security/cacerts
 * 2016-3-30 03:09:01 命令3：./keytool -delete -alias cns -keystore /usr/java/jre1.7.0_80/lib/security/cacerts  -storepass changeit
 * 2016-3-30 10:05:56 命令4：1.已配置环境变量
 * 						   2.从网页导出的微信cer格式整数 
 * 							keytool -import -alias wxca -keystore /usr/java/jre1.7.0_80/lib/security/cacerts  -file wxca.cer  -trustcacerts
 * 2016-4-1 15:02:50 （微信小店自有接口）支付成功后：测试未关注用户是否能接受到消息 -【否】（即使关注了也收不到，服务端调用接口不一样吧……）
 * 2016-4-2 16:03:02 测试A用户请求，回复给B用户（不可以！！！）
 * 2016-4-3 23:56:38 客服接口可发送给任意用户
 * 2016-4-3 23:56:47 商城初版已完成
 * 2016-4-14 17:53:39 添加短信发送限制：同一天、统一模板、同一手机号发送短信有上限
 * 2016-4-25 02:14:46 为了添加购物车功能，做了非常大的改动：1、商城整体做成web浏览
 * 												2、图片cdn加速
 * 												3、重新布局商城：单独页面内新增分类、商城、购物车、我的Tab
 * 												4、做了很多简单、快速、方便的设计，微信调试html真他妈坑
 * 												5、这相当于一次整体的重构
 * 2016-4-25 17:10:48 微信CSS样式在html中无效，需单独引用在css文件中
 * 2016-4-27 13:56:35 写了个异步队列同步的JS算法，觉得挺NB的，特意记录下：详见《附录2》
 * 2016-5-3 21:31:48 ！！！重要！！！！！！重要！！！！！！重要！！！---->版本留存：
 * 						删除优惠券、现金券、积分减免全部统计功能百度云留存版本号：45
 * 2016-5-4 17:32:06 知识积累：ibatis中#号转义用“##”
 * 2016-5-5 17:40:11 配置阿里云北京RDS/ECS
 * 
 * 
 */
/**
 * 附录1
 */
//  <style type="text/css">
//  	body,html{height:100%;}
//	.container{overflow:hidden;position:absolute;top:0;right:0;bottom:0;left:0}
//	.page{overflow-y:auto;-webkit-overflow-scrolling:touch;position:absolute;top:0;right:0;bottom:0;left:0}
//  </style>
	
/**
 * 附录2
 */
//	var syncCartLock=false;
//	var shouldUpdateCart=false;
//	function syncCart(){
//		if(!syncCartLock){
//        	syncCartLock=true;
//        	console.log("【购物车未锁定】开始更新购物车");
//			setTimeout(function () {
//				console.log("等待3秒开始更新购物车--------------------真正执行！！！");
//				setTimeout(function () {
//					console.log("更新购物车【成功】，耗时5秒");
//			    	syncCartLock=false;
//			    	if(shouldUpdateCart){
//			    		console.log("存在【变化】再更新一次");
//			    		syncCart();
//			    		shouldUpdateCart=false;
//			    	}else{
//			    		console.log("真正执行【完毕】");
//			    	}
//			    }, 5000);
//		    }, 3000);
//        }else{
//        	shouldUpdateCart=true;
//        	console.log("购物车被【锁定】设置【需要更新标记】");
//        }
//	}
}
