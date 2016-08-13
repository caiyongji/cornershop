<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
  <head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>领取钻石会员</title>
  	<!-- CSS引入 -->
	<link rel="stylesheet" href="https://res.wx.qq.com/open/libs/weui/0.4.0/weui.min.css"/>
  <style type="text/css">
  </style>
  </head>
  <body>
  <div class="weui_cells">
      <div class="weui_cell">
          <div class="weui_cell_bd weui_cell_primary" style="text-align:center">
              <p>长按识别二维码领取钻石会员</p>
          </div>
      </div>
  </div>
  <img src="/images/vip.jpg" width="100%">
  <div class="weui_cells">
      <div class="weui_cell">
          <div class="weui_cell_bd weui_cell_primary">
              <p>尊敬的${openid}您将领取的会员特权如下：</p>
          </div>
      </div>
  </div>
  <div class="weui_cells weui_cells_checkbox">
      <label class="weui_cell weui_check_label" for="s11">
          <div class="weui_cell_hd">
              <input type="checkbox" class="weui_check" name="checkbox1" id="s11" checked="checked" disabled>
              <i class="weui_icon_checked"></i>
          </div>
          <div class="weui_cell_bd weui_cell_primary">
              <p>首次激活赠送100积分</p>
          </div>
      </label>
      <label class="weui_cell weui_check_label" for="s12">
          <div class="weui_cell_hd">
              <input type="checkbox" name="checkbox1" class="weui_check" id="s12" checked="checked" disabled>
              <i class="weui_icon_checked"></i>
          </div>
          <div class="weui_cell_bd weui_cell_primary">
              <p>会员积分永不清零</p>
          </div>
      </label>
      <label class="weui_cell weui_check_label" for="s13">
          <div class="weui_cell_hd">
              <input type="checkbox" name="checkbox1" class="weui_check" id="s13" checked="checked" disabled>
              <i class="weui_icon_checked"></i>
          </div>
          <div class="weui_cell_bd weui_cell_primary">
              <p>会员积分任意金额可用</p>
          </div>
      </label>
      <label class="weui_cell weui_check_label" for="s14">
          <div class="weui_cell_hd">
              <input type="checkbox" name="checkbox1" class="weui_check" id="s14" checked="checked" disabled>
              <i class="weui_icon_checked"></i>
          </div>
          <div class="weui_cell_bd weui_cell_primary">
              <p>双倍、多倍积分累计</p>
          </div>
      </label>
  </div>
  <div class="weui_cell">
      <div class="weui_cell_bd weui_cell_primary">
          <p>&nbsp;</p>
      </div>
      <div class="weui_cell_ft">街角小店</div>
  </div>
  </body>
</html>
