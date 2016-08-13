<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>测试地址 </title>
	<!-- CSS引入 -->
	<link rel="stylesheet" href="https://res.wx.qq.com/open/libs/weui/0.4.0/weui.min.css"/>
    <!-- JS引入 -->
  	<script type="text/javascript" src="/js/zepto.min.js"></script>
    <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>
  </head>
  <script type="text/javascript">
    $(function(){
  	});
  	wx.config({
	    debug: false,
	    appId: '${appId}',
	    timestamp: ${timestamp},
	    nonceStr: '${nonceStr}',
	    signature: '${signature}',
	    jsApiList: [
	    		'openAddress',
	    		'hideOptionMenu'
	    ] 
	});
	wx.ready(function () {
		//wx.hideOptionMenu();
		//alert(location.href.split('#')[0]);
    });
    function goAddress(){
   		wx.openAddress({
          success: function (res) {
                alert(JSON.stringify(res));
                document.form1.address1.value         = res.provinceName;
                document.form1.address2.value         = res.cityName;
                document.form1.address3.value         = res.countryName;
                document.form1.detail.value           = res.detailInfo;
                document.form1.national.value         = res.nationalCode;
                document.form1.user.value            = res.userName;
                document.form1.phone.value            = res.telNumber;
                document.form1.postcode.value         = res.postalCode;
                document.form1.errmsg.value         = res.errMsg;
                document.form1.qq.value             = 1354386063;
              }
            });
    } 
    function showNow(){
   	 alert(location.href.split('#')[0]);
    }
    function caiyongji(){
    	alert("caiyongji");
    	var re=abc().id;
    	alert(re);
    }
    var abc=function(){
    	var abcd={"id":"2"};
    	return abcd;
    }
  </script>
  
  <body ontouchstart>
	<div style="overflow:scroll">
	    <a id="goAddress" href="javascript:goAddress();" class="weui_btn weui_btn_primary">共享地址</a><br>
	    <a id="showNow" href="javascript:showNow();" class="weui_btn weui_btn_primary">显示当前URL</a><br>
	    <a href="javascript:caiyongji();" class="weui_btn weui_btn_primary">测试函数调用</a><br>
	</div>
  </body>
</html>
