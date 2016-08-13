<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
  <head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>会员中心</title>
  	<!-- CSS引入 -->
	<link rel="stylesheet" href="https://res.wx.qq.com/open/libs/weui/0.4.0/weui.min.css"/>
	<!-- JS引入 -->
	<script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
	<script type="text/javascript" src="/js/zepto.min.js"></script>
  <style type="text/css">
  
  </style>
  </head>
  <script type="text/javascript">
  	$(function(){
  		var detailHtml="<p style=\"margin-bottom:11px;margin-top:11px;\"><\/p><div class=\"item_pic_wrp\" style=\"margin-bottom:8px;font-size:0;\"><img class=\"item_pic\" style=\"width:100%;\" alt=\"\" src=\"https:\/\/mmbiz.qpic.cn\/mmbiz\/zqIN8g9zTHRJqJibY8kRkj3IhwOGjPH1wuabTicHTa74ZAwwKrymcwBpiaqf72RmguPNJJ9fnNkPQZUvhUUDBvOsw\/0?wx_fmt=png\" ><\/div><p style=\"margin-bottom:11px;margin-top:11px;\">测试文字<br>测试连接：http:\/\/weidian.com\/s\/892052155?wfr=c<\/p>";
  		//$("#test11").html(detailHtml);
  	});
  </script>
  <body>
  <div class="weui_cell">
      <div class="weui_cell_hd" style="width:50px;height:50px; border-radius:50%; overflow:hidden;">
      <img src="${headimgurl}" alt="" style="width:50px;margin-right:5px;display:block">
      </div>
      <div class="weui_cell_bd weui_cell_primary" style="margin-left:10px">
          <p>${nickname}</p>
      </div>
      <div class="weui_cell_ft"></div>
  </div>
  <div class="weui_cells">
      <div class="weui_cell">
          <div class="weui_cell_bd weui_cell_primary">
              <p>会员级别：💎钻石会员</p>
          </div>
          <div class="weui_cell_ft">积分10000</div>
      </div>
  </div>
  <div class="weui_cells weui_cells_access">
      <a class="weui_cell" href="javascript:;">
          <div class="weui_cell_bd weui_cell_primary">
              <p>🕤&nbsp;时间轴</p>
          </div>
          <div class="weui_cell_ft">
          </div>
      </a>
      <a class="weui_cell" href="javascript:;">
          <div class="weui_cell_bd weui_cell_primary">
              <p>🚩&nbsp;收货地址</p>
          </div>
          <div class="weui_cell_ft">
          </div>
      </a>
      <a class="weui_cell" href="javascript:;">
          <div class="weui_cell_bd weui_cell_primary">
              <p>📱&nbsp;绑定手机</p>
          </div>
          <div class="weui_cell_ft">
          </div>
      </a>
  </div>
  <div class="weui_cell">
      <div class="weui_cell_bd weui_cell_primary">
          <p>&nbsp;</p>
      </div>
      <div class="weui_cell_ft">街角小店</div>
  </div>
  <div id="test11"></div>
  </body>
</html>
