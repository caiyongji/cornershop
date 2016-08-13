<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>支付测试 </title>
	<!-- CSS引入 -->
	<link rel="stylesheet" href="https://res.wx.qq.com/open/libs/weui/0.4.0/weui.min.css"/>
    <!-- JS引入 -->
    <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>
  	<script type="text/javascript" src="/js/zepto.min.js"></script>
  </head>
  <script type="text/javascript">
	/* wx.checkJsApi({
    jsApiList: ['openAddress'], // 需要检测的JS接口列表，所有JS接口列表见附录2,
    success: function(res) {
        // 以键值对的形式返回，可用的api值true，不可用为false
        // 如：{"checkResult":{"chooseImage":true},"errMsg":"checkJsApi:ok"}
        alert(res.errMsg);
    }
	}); */
    
    $(function(){
  		if("1"==sessionStorage.getItem("testpay")){
			alert("本页已缓存");
		}else{
			sessionStorage.setItem("testpay","1");
		}
  		
  		$("body").on("touchstart", function (event) {
		    //event.preventDefault();
		});
		wx.config({
		    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
		    appId: '${appid}', // 必填，公众号的唯一标识
		    timestamp: ${timeStamp}, // 必填，生成签名的时间戳
		    nonceStr: '${nonceStr}', // 必填，生成签名的随机串
		    signature: '${signature}',// 必填，签名，见附录1
		    jsApiList: ['chooseWXPay'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
		});
		wx.ready(function () {
			//wx.hideOptionMenu();
			//alert("timestamp:${timeStamp}\nnonceStr:${nonceStr}\njsApiTicket:${jsApiTicket}\nurl:"+location.href.split('#')[0]);
	    });
  	});
    
    function pay(){
    	wx.chooseWXPay({
            'appId': '${appid}',
            'timestamp': '${timeStamp}',
            'nonceStr': '${nonceStr}',
            'package': '${packageStr}',
            'signType': '${signType}',
            'paySign': '${paySign}',
            success: function (res) {
                alert(res.errMsg);
            },
            cancel: function () {
                alert(res.errMsg+res.errMsg);
            },
            error: function (e) {
                alert(res.errMsg+res.errMsg);
            }
        });
    }
    function goAddress(){
   		wx.openAddress({
		     success: function () { 
		          // 用户成功拉出地址 
		          alert("1");
		     },
		     cancel: function () { 
		          // 用户取消拉出地址
		          alert("1");
			}
		});
    }
	function goDetail(){
		if (typeof WeixinJSBridge == "undefined") return false;
			
		var pid = "pHj5msysa6GdgdQal3LAZjAp6qGQ";//只需要传递
		WeixinJSBridge.invoke('openProductViewWithPid',{
		"pid":pid
		},function(res){
			// 返回res.err_msg,取值 
			// open_product_view_with_id:ok 打开成功
			//alert(res.err_msg);
			if (res.err_msg != "open_product_view_with_id:ok"){
				WeixinJSBridge.invoke('openProductView',{
					"productInfo":"{\"product_id\":\""+pid+"\",\"product_type\":1}"
				},function(res){ 
					//alert(res.err_msg);
				});
			}
		});
	} 
	function showNow(){
   	 alert(location.href.split('#')[0]);
    }
  </script>
  
  <body ontouchstart>
	<div style="overflow:scroll">
		<div class="weui_cells weui_cells_access">
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>appId:${appid}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>timeStamp:${timeStamp}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>nonceStr:${nonceStr}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>packageStr:${packageStr}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>signType:${signType}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>paySign:${paySign}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>signature:${signature}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>openId: ${openid}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>prepayId: ${prepayId}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
			<a class="weui_cell" href="javascript:;">
				<div class="weui_cell_bd weui_cell_primary">
					<p>accessToken: ${accessToken}</p>
				</div>
				<div class="weui_cell_ft"></div>
			</a>
		</div>
		<a href="javascript:alert('111');">baidu.com</a><br>
	    <a id="payBtn" href="javascript:pay();" class="weui_btn weui_btn_primary">结算</a><br>
	    <a id="goDetail" href="javascript:goDetail();" class="weui_btn weui_btn_primary">商品详情</a><br>
	    <a id="goAddress" href="javascript:goAddress();" class="weui_btn weui_btn_primary">共享地址</a><br>
	    <a id="showNow" href="javascript:showNow();" class="weui_btn weui_btn_primary">显示当前URL</a><br>
	    <img id="testImg">
	</div>
  </body>
</html>
