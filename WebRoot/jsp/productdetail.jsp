<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
  <head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>商品详情</title>
  	<!-- CSS引入 -->
	<link rel="stylesheet" href="https://res.wx.qq.com/open/libs/weui/0.4.0/weui.min.css"/>
	<!-- JS引入 -->
	<script type="text/javascript" src="/js/zepto.min.js"></script>
	<script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
  <style type="text/css">
  </style>
  </head>
  <script type="text/javascript">
  	$(function(){
  		wx.config({
		});
		wx.ready(function () {
			wx.hideOptionMenu();
	    });
  	});
  	function inbox(){
  		showToast("已加入购物车");
  	}
  	function godetail(productId){
  		wx.openProductSpecificView({
		    productId: ""+productId+"", // 商品id
		    viewType: '0' // 0.默认值，普通商品详情页1.扫一扫商品详情页2.小店商品详情页
		});
  	}
  </script>
  <body>
  <img alt="" src="/images/0.jpg" style="width:100%">
  <div class="weui_cells">
      <div class="weui_cell">
          <div class="weui_cell_bd weui_cell_primary" style="text-align:center">
              <p>测试商品${productid}</p>
          </div>
      </div>
  </div>
  <a id="payBtn" href="javascript:inbox();" class="weui_btn weui_btn_primary">加入购物车</a><br>
  <a href="javascript:godetail('pHj5ms5ulpQjyBItu36w4Jx0-x8Y');" class="weui_btn weui_btn_default">直接购买</a><br>
  <div class="weui_cells weui_cells_access">
      <a class="weui_cell" href="javascript:;">
          <div class="weui_cell_bd weui_cell_primary">
              <p>图片展示</p>
          </div>
          <div class="weui_cell_ft">
          </div>
      </a>
      <a class="weui_cell" href="javascript:;">
          <div class="weui_cell_bd weui_cell_primary">
              <p>规格参数</p>
          </div>
          <div class="weui_cell_ft">
          </div>
      </a>
      <a class="weui_cell" href="javascript:;">
          <div class="weui_cell_bd weui_cell_primary">
              <p>详细说明</p>
          </div>
          <div class="weui_cell_ft">
          </div>
      </a>
  </div>
  </body>
  <div id="toast" style="display: none;">
        <div class="weui_mask_transparent"></div>
        <div class="weui_toast">
            <i class="weui_icon_toast"></i>
            <p class="weui_toast_content" id="toastcontent">成功</p>
        </div>
  </div>
</html>
