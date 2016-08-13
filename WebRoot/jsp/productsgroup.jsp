<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
  <head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>街角小店</title>
  	<!-- CSS引入 -->
	<link rel="stylesheet" href="https://res.wx.qq.com/open/libs/weui/0.4.0/weui.min.css"/>
	<link rel="stylesheet" href="/css/caislider.css"/>
	<!-- JS引入 -->
	<script type="text/javascript" src="/js/zepto.min.js"></script>
	<script type="text/javascript" src="/js/caislider.js"></script>
	<script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
  <style type="text/css">
  .goods_list {
	text-align: justify;
    text-align-last: justify;
    font-size: 0;
    padding-left: 0;
    display: block;
  }
  .tr4 li{
    display: inline-block;
    vertical-align: top;
    text-align: left;
    padding-bottom: 19px;
    font-size: 14px;
    width: 25%;
  }
  .tr3 li{
    display: inline-block;
    vertical-align: top;
    text-align: left;
    padding-bottom: 19px;
    font-size: 14px;
    width: 33.33%;
  }
  .tr2 li{
    display: inline-block;
    vertical-align: top;
    text-align: left;
    padding-bottom: 19px;
    font-size: 14px;
    width: 50%;
  }
  .goods_list .cover{
    width: 100%;
    background-color: #eee;
  }
   .goods_list .title{
	display: block;
    font-weight: 400;
    font-style: normal;
    color: #999;
    width: auto;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 13px;
  }
  .goods_list .price{
    display: inline-block;
    vertical-align: bottom;
    font-size: 19px;
    line-height: 24px;
  }
  .goods_list .sub_price{
    display: inline-block;
    vertical-align: bottom;
    text-decoration: line-through;
    color: #999;
    font-size: 13px;
    margin-left: 3px;
  }
  .goods_list .sales{
    display: inline-block;
    vertical-align: bottom;
    color: #999;
    font-size: 13px;
    line-height: 24px;
    margin-right: 3px;
  }
  </style>
  </head>
  <script type="text/javascript">
  	$(function(){
	  	$(".caiSlider").slider({  
	        caisliTitle:false,  
	        caisliNum:true,  
	        caisliTouch:true,  
	        caisliTime:4000      
	    });  
  		var detailHtml="<p style=\"margin-bottom:11px;margin-top:11px;\"><\/p><div class=\"item_pic_wrp\" style=\"margin-bottom:8px;font-size:0;\"><img class=\"item_pic\" style=\"width:100%;\" alt=\"\" src=\"/images/0.jpg\" ><\/div><p style=\"margin-bottom:11px;margin-top:11px;\">测试文字<br>测试连接：http:\/\/weidian.com\/s\/892052155?wfr=c<\/p>";
  		//$("#test11").html(detailHtml);
  		
  		$(".goods_list img").tap(function(){
  			alert($(this).attr("productid"));
  		});
  	});
  	function goGoods(productId){
  		window.location.href="http://baidu.com";
  	}
  </script>
  <body>
 	<div class="caiSlider">   
	    <ul class="caisli_con">  
	        <li><a href="javascript:alert('click')"><img src="/images/566fa425N48f18d2f.jpg"></a></li>  
	        <li><img src="/images/566fa426N49fa72eb.jpg"></li>  
	        <li><img src="/images/566fa426N71be1f8c.jpg"></li>  
	        <li><img src="/images/566fa427Nac81301b.jpg"></li>  
	        <li><img src="/images/566fa427Nd08f202b.jpg"></li>
	    </ul>  
	</div>
  	<div style="margin-top: 20px;margin-bottom: 20px;position: relative;margin: 0 10px 10px;">
	<div>
	<ul class="goods_list tr4">
	<li>
		<img class="cover" src="/images/vip.jpg" productid="10001">
		<strong class="title">测试长文字长文字长文字长文字长文字长文字</strong>
		<span class="price">￥1</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	</ul>
	</div>
  	</div>
  	<div style="margin-top: 20px;margin-bottom: 20px;position: relative;margin: 0 10px 10px;">
	<div>
	<ul class="goods_list tr3">
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥9999</span>
		<span class="sub_price">￥99999</span>
		<span class="sales">销量:10000</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	</ul>
	</div>
  	</div>
  	<div style="margin-top: 20px;margin-bottom: 20px;position: relative;margin: 0 10px 10px;">
	<div>
	<ul class="goods_list tr2">
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥9999</span>
		<span class="sub_price">￥99999</span>
		<span class="sales">销量:10000</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	<li>
		<img class="cover" src="/images/0.jpg" productid="10001">
		<strong class="title">测试</strong>
		<span class="price">￥1</span>
		<span class="sub_price">￥10</span>
		<span class="sales">销量:100</span>
	</li>
	</ul>
	</div>
  	</div>
  </body>
</html>
