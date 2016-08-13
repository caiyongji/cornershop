<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>测试街角小店</title>
	<!-- CSS引入 -->
	<link rel="stylesheet" href="https://res.wx.qq.com/open/libs/weui/0.4.0/weui.min.css"/>
	<!-- JS引入 -->
	<script type="text/javascript" src="/js/zepto.min.js"></script>
	<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>
  </head>
  <style type="text/css">
  	.bfwhite:before{color: #FFFFFF;}
  	.cnsAdd,.cnsMinus{width:15px;height:15px;}
  	.shopCartItemsImg{width:20%;}
  	.shopCartItemsTitle{width:45%;}
  	.shopCartItemsCount{width:35%;}
  	.shopCartItemsInputCount{width:20%;text-align:center;}
  	.catalogTitle{font-size:20px;width:100%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;}
  	.catalogCells:before{border-top:0px solid white;}
  	.catalogCells{margin:0 0 10px 0;}
  	.catalogCells:after{border-bottom:0px solid white;}
  	.groupList{float: left;text-align:center;margin:5px;width:30%}
  	.groupImg{width:30px;height:30px;}
  	.groupName{overflow:hidden;text-overflow:ellipsis;white-space:nowrap;font-size:15px;color:#ACACAC}
  	.groupTitle{text-align:center;}
  </style>
  <script type="text/javascript">
	var openId="${openId}";
  	var configPack=configPackage(location.href.split('#')[0]);
  	wx.config({
	    debug: false,
	    appId: configPack.appId,
	    timestamp: configPack.timestamp,
	    nonceStr: configPack.nonceStr,
	    signature: configPack.signature,
	    jsApiList: [
	    		"openAddress",
	    		"hideOptionMenu",
	    		"openProductSpecificView"
	    ] 
	});
	wx.ready(function () {
		wx.hideOptionMenu();
		//alert(location.href.split('#')[0]);
    });
    $(function(){
    	<%-- 默认初始化商城--%>
    	tabShow("shop");
    	
  		var testProductName="旺旺小小酥（黑胡椒味）60g";
  		var src=encodeURI(testProductName+".jpg")
  		//$(".weui_media_title").text(testProductName);
  		//$(".weui_media_appmsg_thumb").attr("src","${IMG_SERVER_DOMAIN}/"+src);
  		$(".weui_media_appmsg_thumb").attr("src","${IMG_SERVER_DOMAIN}/"+src);
  		<%-- 初始化购物车 --%>
  		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			data : {},
			url : "/io/shopCart.cns",
			dataType : "json",
			success : function(data) {
				var products=data.products;
				if(products){
					for(var i=0;i<products.length;i++){
						var html="";
						html+="<div class=\"weui_media_box weui_media_appmsg\">";
						html+="    <div class=\"weui_media_hd shopCartItemsImg\">";
						html+="        <img onclick=\"showProductById('"+products[i].pId+"')\" class=\"weui_media_appmsg_thumb\" src=\"${IMG_SERVER_DOMAIN}/"+products[i].pImg+"\">";
						html+="    </div>";
						html+="    <div class=\"weui_media_bd shopCartItemsTitle\">";
						html+="        <h4 class=\"weui_media_title\">"+products[i].pNm+"</h4>";
						html+="        <p class=\"weui_media_desc\">单价："+parseInt(products[i].pPrice)*1.0/100+"元</p>";
						html+="    </div>";
						html+="    <div class=\"weui_cell_ft shopCartItemsCount\">";
						html+="    	<a href=\"javascript:minusCount('pro_"+products[i].pId+"');\"><img class=\"cnsMinus\" src=\"${IMG_SERVER_DOMAIN}/minus.svg\"></a>";
						html+="        <input id=\"pro_"+products[i].pId+"\" price=\""+products[i].pPrice+"\" onchange=\"checkVal('pro_"+products[i].pId+"')\" class=\"weui_input shopCartItemsInputCount\" type=\"number\" pattern=\"[0-9]*\" value=\"1\">";
						html+="        <a href=\"javascript:addCount('pro_"+products[i].pId+"');\"><img class=\"cnsAdd\" src=\"${IMG_SERVER_DOMAIN}/plus.svg\"></a>";
						html+="    </div>";
						html+="</div>";
						$("#shopCartItems").append(html);
						changeTotalPrice();
					}
				}
			}
		});
  	});
    function configPackage(currentPath) {
		var result;
		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			data : {
				currentPath : currentPath
			},
			url : "/sign/config.cns",
			dataType : "json",
			async : false,// 同步
			success : function(json) {
				result = json;
			}
		});
		return result;
	}
	function payPackage(productIds) {
		var result;
		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			data : {
				openId:openId,
				productIds:productIds
			},
			url : "/sign/pay.cns",
			dataType : "json",
			async : false,// 同步
			success : function(json) {
				result = json;
			}
		});
		return result;
	}
	function showCouponsActionSheet(){
		prependCouponActionSheet();
		showActionSheet(minusThis);
	}
	function showDirectCouponsActionSheet(){
		prependDirectCouponActionSheet();
		showActionSheet(function(obj){});
	}
	function showActionSheet(func){
		 var mask = $('#mask');
         var weuiActionsheet = $('#weui_actionsheet');
         weuiActionsheet.addClass('weui_actionsheet_toggle');
         mask.show().addClass('weui_fade_toggle').one('click', function () {
             hideActionSheet(weuiActionsheet, mask);
         });
         $('.weui_actionsheet_cell').one('click', function () {
		     func(this);
             hideActionSheet(weuiActionsheet, mask);
         });
         weuiActionsheet.unbind('transitionend').unbind('webkitTransitionEnd');

         function hideActionSheet(weuiActionsheet, mask) {
             weuiActionsheet.removeClass('weui_actionsheet_toggle');
             mask.removeClass('weui_fade_toggle');
             weuiActionsheet.on('transitionend', function () {
                 mask.hide();
             }).on('webkitTransitionEnd', function () {
                 mask.hide();
             })
         }
	}
	function prependCouponActionSheet(){
		$("#weui_actionsheet").empty();
		if(coupons==null){
			return;
		}
		var html="";
		html+="<div class=\"weui_actionsheet_menu\" style=\"overflow-y:auto\">";
		for(var i=0;i<coupons.length;i++){
			html+="<div class=\"weui_actionsheet_cell\" minus=\""+coupons[i].minus+"\">"+coupons[i].name+"</div>";
		}
		html+="</div>";
		html+="<div class=\"weui_actionsheet_action\">";
        html+="    <div class=\"weui_actionsheet_cell\" cancel=\"TRUE\">不使用优惠券</div>";
        html+="</div>";
		$("#weui_actionsheet").append(html);
	}
	function prependDirectCouponActionSheet(){
		$("#weui_actionsheet").empty();
		if(directCoupons==null){
			return;
		}
		var html="";
		html+="<div class=\"weui_actionsheet_menu\" style=\"overflow-y:auto\">";
		for(var i=0;i<directCoupons.length;i++){
			html+="<div class=\"weui_actionsheet_cell\">"+directCoupons[i].name+"(自动抵扣)</div>";
		}
		html+="</div>";
		html+="<div class=\"weui_actionsheet_action\">";
        html+="    <div class=\"weui_actionsheet_cell\" cancel=\"TRUE\">返回</div>";
        html+="</div>";
		$("#weui_actionsheet").append(html);
	}
	function minusThis(obj){
		if("TRUE"==$(obj).attr("cancel")){
			$("#couponMinusP").hide();
			return;
		}
		var minus=parseInt($(obj).attr("minus"));
		$("#couponMinus").text("-"+minus*1.0/100);
		var price=parseInt($("#payPrice").attr("price"));
		if(price-minus>=0){
			price-=minus;
		}else{
			price=0;
		}
		$("#payPrice").text(price*1.0/100);
		$("#couponMinusP").show();
	}
    function goAddress(elementId){
   		wx.openAddress({
			success: function (res) {
				var uName=res.userName;
				var uPhone=res.telNumber;
				var uAddress=res.provinceName+res.cityName+res.countryName+res.detailInfo;
				$("#"+elementId+"_hidden").attr("user",uName);
				$("#"+elementId+"_hidden").attr("phone",uPhone);
				$("#"+elementId+"_hidden").attr("address",uAddress);
				$("#"+elementId+"B").text(uName+"\n"+uPhone);
				$("#"+elementId).text(uAddress);
			}
		});
    }
    function showProductById(productId){
    	wx.openProductSpecificView({
		    productId: "pHj5ms9e4pl3pnp9QAFnxzMoJXGQ"
		});
    }
    function minusCount(obj){
    	var input=$("#"+obj);
    	var count=$(input).val();
    	if(isNaN(parseInt(count))){
    		$(input).val(1);
    		changeTotalPrice();
    		return;
    	}
    	if(count>0){
    		count--;
    	}else{
    		count=0;
    	}
    	$(input).val(count);
    	changeTotalPrice();
    }
    function addCount(obj){
    	var input=$("#"+obj);
    	var count=$(input).val();
    	if(isNaN(parseInt(count))){
    		$(input).val(1);
    		changeTotalPrice();
    		return;
    	}else{
    		count++;
    	}
    	$(input).val(count);
    	changeTotalPrice();
    }
    function checkVal(obj){
    	var input=$("#"+obj);
    	var count=$(input).val();
    	if(isNaN(parseInt(count))){
    		$(input).val(1);
    		changeTotalPrice();
    		showToast("只允许数字");
    	}
    	changeTotalPrice();
    }
    function showToast(content) {
		var $toast = $('#toast');
		if ($toast.css('display') != 'none') {
			return;
		}
		$("#toastcontent").text(content);
		$toast.show();
		setTimeout(function() {
			$toast.hide();
		}, 1500);
	}
	function changeTotalPrice(){
		var totalPrice=0;
		$("#tab .shopCartItemsInputCount").each(function(){
			totalPrice+=parseInt($(this).attr("price"))*parseInt($(this).val());
		});
		$("#totalPrice").attr("price",totalPrice);
		$("#totalPrice").text(totalPrice*1.0/100);
	}
	var tabNow;
	function tabShow(id){
		$("#"+tabNow).hide();
		tabNow=id;
		$("#"+id).show();
	}
	var coupons;
    var directCoupons;
	function goPay(){
		if(parseInt($("#totalPrice").attr("price"))<=0){
			showToast("没有可购买商品")
			return;
		}
		tabShow("goPay");
		var productKindsCount=0;
		$(".shopCartItemsCount input").each(function(){
			if($(this).val()!="0"){
				productKindsCount++;
			}
		});
		$("#productKindsCount").text("共计"+productKindsCount+"种商品");
		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			data : {
				openId:openId
			},
			url : "/io/goPay.cns",
			dataType : "json",
			success : function(data) {
				$("#shouldPayPrice").text($("#totalPrice").text());
				$("#payPrice").attr("price",$("#totalPrice").attr("price"));
				$("#payPrice").text($("#totalPrice").text());
				<%-- 构建地址 --%>
				$("#uAddressB").text(data.uName+"\n"+data.uPhone);
				$("#uAddress").text(data.uAddress);
				$("#uAddress_hidden").attr("user",data.uName);
				$("#uAddress_hidden").attr("phone",data.uPhone);
				$("#uAddress_hidden").attr("address",data.uAddress);
				<%-- 构建优惠券 --%>
				coupons=data.coupons;
				$("#couponsCount").text(data.coupons.length);
				<%-- 构建现金券券 --%>
				directCoupons=data.directCoupons;
				$("#directCouponsCount").text(data.directCoupons.length);
				if(data.directCoupons.length>0){
					var minusPrice=parseInt(data.directCoupons[0].minus)*1.0/100;
					$("#payContent").text("支付(再减"+minusPrice+"元)");
				}else{
					$("#payContent").text("支付");
				}
			}
		});
	}
	function pay(){
    	if(""==$("#uAddress_hidden").attr("user")||""==$("#uAddress_hidden").attr("phone")){
    		goAddress('uAddress');
    		return;
    	}
    	var payPack=payPackage("1");
    	wx.chooseWXPay({
    		debug: false,
            'appId': payPack.appid,
            'timestamp': payPack.timestamp,
            'nonceStr': payPack.nonceStr,
            'package': payPack.packageStr,
            'signType': payPack.signType,
            'paySign': payPack.paySign,
            success: function (res) {
                showMsg("支付成功","我们将尽快为您配送","backToShop");
                <%-- 清空购物车 --%>
                $("#shopCartItems").empty();
                $("#totalPrice").attr("price",0);
				$("#totalPrice").text("0.00");
            }
        });
    }
    function showMsg(title,desc,func){
    	$("#showMsg").empty();
    	var html="";
    	html+="<div class=\"weui_msg\">";
		html+="	<div class=\"weui_icon_area\"><i class=\"weui_icon_success weui_icon_msg\"></i></div>";
		html+="	<div class=\"weui_text_area\">";
		html+="	    <h2 class=\"weui_msg_title\">"+title+"</h2>";
		html+="	    <p class=\"weui_msg_desc\">"+desc+"</p>";
		html+="	</div>";
		html+="	<div class=\"weui_opr_area\">";
		html+="	    <p class=\"weui_btn_area\">";
		html+="	        <a href=\"javascript:"+func+"();\" class=\"weui_btn weui_btn_primary\">确定</a>";
		html+="	    </p>";
		html+="	</div>";
		html+="</div>";
		$("#showMsg").html(html);
		tabShow("showMsg");
    }
    function backToShop(){
    	tabShow("shop");
    }
    function catalog(){
    	if("INIT"==$("#catalog").attr("init")){
    		$.ajax({
				type : "POST",
				contentType : "application/x-www-form-urlencoded;charset=utf-8",
				data : {},
				url : "/io/catalog.cns",
				dataType : "json",
				success : function(data) {
					<%-- 构建HTML --%>
					var html="";
					var catalogId="";
					for(var i=0;i<data.length;i++){
						html+="<div class=\"weui_cells weui_cells_access catalogCells\">";
						html+="	<a class=\"weui_cell\" catalogId=\""+data[i].catalogId+"\" href=\"javascript:alert('"+data[i].catalogName+"');\">";
						html+="	    <div class=\"weui_cell_bd weui_cell_primary groupTitle\">";
						html+="	        <p><font color=\"#00c800\"  class=\"catalogTitle\">"+data[i].catalogName+"</font></p>";
						html+="	    </div>";
						html+="	</a>";
						html+="	<div>";
						html+="		<ul>";
						var groupList=data[i].groupList;
						for(var j=0;j<groupList.length;j++){
								html+="			<li groupId=\""+groupList[j].groupId+"\" onclick=\"javascript:alert('"+groupList[j].groupName+"');\" class=\"groupList\">";
								html+="		        	<img class=\"groupImg\" src=\"http://img.cornershop.cn/0.jpg\">";
								html+="		        	<p class=\"groupName\">"+groupList[j].groupName+"</p>";
								html+="			</li>";
						}
						html+="		</ul>";
						html+="	</div>";
						html+="</div>";
					}
					$("#catalog").html(html);
					<%-- 移除初始化 --%>
					$("#catalog").removeAttr("init");
				}
			});
    	}
    	tabShow("catalog");
    }
  </script>
  
  <body ontouchstart>
	<div class="container" id="container">
		<div class="tabbar">
			<div class="weui_tab">
			    <div class="weui_tab_bd" id="tab" style="overflow-x:hidden">
					  <div id="catalog" init="INIT" style="display: none;">
					   		<%-- 商品分类 --%>
					  </div>
					  <div id="shopCart" style="display: none;">
					  		<div class="weui_panel_bd" id="shopCartItems">
					  			<%-- 购物车商品 --%>
					        </div>
					        <div onclick="javascript:goPay();" class="weui_cell">
					            <div class="weui_cell_hd weui_cell_primary">
					           		 合计：<font color="#00c800" id="totalPrice" price="0">0.00</font>&nbsp;元
					            </div>
					            <div class="weui_cell_ft">
					            	<a href="javascript:;" class="weui_btn weui_btn_primary">去结算&nbsp;<i class="weui_icon_success_circle bfwhite"></i></a>
					            </div>
					        </div>
					  </div>
					  <div id="me" style="display: none;">
					  	<div class="weui_cell">
						      <div class="weui_cell_hd" style="width:50px;height:50px; border-radius:50%; overflow:hidden;">
						      <img src="http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0" alt="" style="width:50px;margin-right:5px;display:block">
						      </div>
						      <div class="weui_cell_bd weui_cell_primary" style="margin-left:10px" >
						          <p>小米壳</p>
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
						      <a class="weui_cell" href="javascript:goAddress();">
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
						      <a class="weui_cell" href="javascript:;">
						          <div class="weui_cell_bd weui_cell_primary">
						              <p>📱&nbsp;绑定手机</p>
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
						      <a class="weui_cell" href="javascript:;">
						          <div class="weui_cell_bd weui_cell_primary">
						              <p>📱&nbsp;绑定手机</p>
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
						      <a class="weui_cell" href="javascript:;">
						          <div class="weui_cell_bd weui_cell_primary">
						              <p>📱&nbsp;绑定手机</p>
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
					  </div>
					  <div id="shop" style="display: none;">
					  	<div class="weui_cell">
					            <div class="weui_cell_bd weui_cell_primary">
					                <div class="weui_uploader">
					                	<div class="weui_uploader_hd weui_cell">
					                        <div class="weui_cell_bd weui_cell_primary">图片上传</div>
					                        <div class="weui_cell_ft">0/2</div>
					                    </div>
					                    <div class="weui_uploader_bd">
					                        <ul class="weui_uploader_files">
					                            <li onclick="javascript:showProductById();" class="weui_uploader_file weui_uploader_status" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)">
					                            <div class="weui_uploader_status_content">商品链接</div>
					                            </li>
					                            <li class="weui_uploader_file weui_uploader_status" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)">
					                            	<div class="weui_uploader_status_content">
					                                    <i class="weui_icon_msg weui_icon_success"></i>
					                                </div>
					                            </li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file weui_uploader_status" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)">
					                                <div class="weui_uploader_status_content">
					                                    <i class="weui_icon_warn"></i>
					                                </div>
					                            </li>
					                            <li class="weui_uploader_file weui_uploader_status" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)">
					                                <div class="weui_uploader_status_content">50%</div>
					                            </li>
					                        </ul>
					                    </div>
					                </div>
					            </div>
					            <div class="weui_cell_bd weui_cell_primary">
					                <div class="weui_uploader">
					                    <div class="weui_uploader_bd">
					                        <ul class="weui_uploader_files">
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file weui_uploader_status" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)">
					                                <div class="weui_uploader_status_content">
					                                    <i class="weui_icon_warn"></i>
					                                </div>
					                            </li>
					                            <li class="weui_uploader_file weui_uploader_status" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)">
					                                <div class="weui_uploader_status_content">50%</div>
					                            </li>
					                        </ul>
					                    </div>
					                </div>
					            </div>
					        </div>
					        <div class="weui_cell">
					            <div class="weui_cell_bd weui_cell_primary">
					                <div class="weui_uploader">
					                	<div class="weui_uploader_hd weui_cell">
					                        <div class="weui_cell_bd weui_cell_primary">图片上传</div>
					                        <div class="weui_cell_ft">0/2</div>
					                    </div>
					                    <div class="weui_uploader_bd">
					                        <ul class="weui_uploader_files">
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)"></li>
					                            <li class="weui_uploader_file weui_uploader_status" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)">
					                                <div class="weui_uploader_status_content">
					                                    <i class="weui_icon_warn"></i>
					                                </div>
					                            </li>
					                            <li class="weui_uploader_file weui_uploader_status" style="background-image:url(${IMG_SERVER_DOMAIN}/0.jpg)">
					                                <div class="weui_uploader_status_content">50%</div>
					                            </li>
					                        </ul>
					                    </div>
					                </div>
					            </div>
					        </div>
					  </div>
					  <div id="goPay" style="display: none;">
					  		<div class="weui_cells weui_cells_access">
						  		<a class="weui_cell" href="javascript:goAddress('uAddress');">
						  			<div class="weui_cell_hd">
					  					<p style="width:20px;margin-right:5px;display:block">🚩</p>
					  				</div>
						            <div class="weui_cell_bd weui_cell_primary">
						            	<p><b id="uAddressB"></b></p>
						                <p id="uAddress"></p>
						                <input type="hidden" id="uAddress_hidden" user="" phone="" address=""> 
						            </div>
						            <div class="weui_cell_ft"></div>
						        </a>
						        <a class="weui_cell" href="javascript:tabShow('shopCart');">
						            <div class="weui_cell_bd weui_cell_primary">
						                <p id="productKindsCount"></p>
						            </div>
						            <div class="weui_cell_ft"></div>
						        </a>
						        <a class="weui_cell" href="javascript:showCouponsActionSheet();">
						            <div class="weui_cell_bd weui_cell_primary">
						                <p>可用优惠券</p>
						            </div>
						            <div class="weui_cell_ft"id="couponsCount">0</div>
						        </a>
						        <a class="weui_cell" href="javascript:showDirectCouponsActionSheet();">
						            <div class="weui_cell_bd weui_cell_primary">
						                <p>可用现金券</p>
						            </div>
						            <div class="weui_cell_ft"id="directCouponsCount">0</div>
						        </a>
						        <a class="weui_cell" href="javascript:;">
						            <div class="weui_cell_bd weui_cell_primary">
						                <p>积分减免</p>
						            </div>
						            <div class="weui_cell_ft"id="couponsCount">不可用</div>
						        </a>
						        <div class="weui_cell">
						            <div class="weui_cell_bd weui_cell_primary">
						                <p>金额：<font color="#00c800" id="shouldPayPrice">0.00</font></p>
						                <p id="couponMinusP" style="display:none">优惠券抵扣：<font color="#00c800" id="couponMinus">0.00</font></p>
						                <p>积分减免：<font color="#00c800" id="intgMinus">0.00</font></p>
						            </div>
						        </div>
					        </div>
					        <div onclick="javascript:pay();" class="weui_cell">
					            <div class="weui_cell_hd weui_cell_primary">
					           		 合计：<font color="#00c800" id="payPrice" price="0">0.00</font>&nbsp;元
					            </div>
					            <div class="weui_cell_ft">
					            	<a href="javascript:;" class="weui_btn weui_btn_primary"><font id="payContent">支付(再减18元)</font>&nbsp;<i class="weui_icon_safe_success bfwhite"></i></a>
					            </div>
					        </div>
					  </div>
					  <div class="msg"  id="showMsg" style="display: none;">
					  	<%--信息提示 --%>
					 </div>
			    </div>
			    <div class="weui_tabbar">
			        <a href="javascript:catalog();" class="weui_tabbar_item weui_bar_item_on">
			            <div class="weui_tabbar_icon">
			                <img src="/images/icon_nav_button.png" alt="">
			            </div>
			            <p class="weui_tabbar_label">分类</p>
			        </a>
			        <a href="javascript:tabShow('shop');" class="weui_tabbar_item">
			            <div class="weui_tabbar_icon">
			                <img src="/images/icon_nav_article.png" alt="">
			            </div>
			            <p class="weui_tabbar_label">商城</p>
			        </a>
			        <a href="javascript:tabShow('shopCart');" class="weui_tabbar_item">
			            <div class="weui_tabbar_icon">
			                <img src="/images/icon_nav_msg.png" alt="">
			            </div>
			            <p class="weui_tabbar_label">购物车</p>
			        </a>
			        <a href="javascript:tabShow('me');" class="weui_tabbar_item">
			            <div class="weui_tabbar_icon">
			                <img src="/images/icon_nav_cell.png" alt="">
			            </div>
			            <p class="weui_tabbar_label">我的</p>
			        </a>
			    </div>
			</div>
		</div>
	</div>
  </body>
  <div id="toast" style="display: none;">
        <div class="weui_mask_transparent"></div>
        <div class="weui_toast">
            <i class="weui_icon_toast"></i>
            <p class="weui_toast_content" id="toastcontent">成功</p>
        </div>
  </div>
  <div id="actionSheet_wrap">
    <div class="weui_mask_transition" id="mask" style="display: none;"></div>
    <div class="weui_actionsheet" id="weui_actionsheet">
  </div>
</div>
</html>
