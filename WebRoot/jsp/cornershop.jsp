<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>街角小店</title>
	<!-- CSS引入 -->
	<link rel="stylesheet" href="https://res.wx.qq.com/open/libs/weui/0.4.0/weui.min.css"/>
	<link rel="stylesheet" href="/css/cornershop.css"/>
	<!-- JS引入 -->
	<script type="text/javascript" src="/js/zepto.min.js"></script>
	<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>
  </head>
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
   		goShop();
  		<%-- 初始化购物车 --%>
  		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			url : "/io/shopCart.cns",
			dataType : "json",
			data : {
				openId:openId
			},
			success : function(data) {
				var products=data.products;
				if(products){
					for(var i=0;i<products.length;i++){
						addProductToCart(products[i].pId,products[i].pNm,products[i].pPrice,products[i].pImg,products[i].pCount)
					}
					changeTotalPrice();
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
	function payPackage() {
		var result;
		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			data : {
				openId : openId,
				payProducts : payProducts,
				uName : $("#uAddress_hidden").attr("user"),
				uPhone : $("#uAddress_hidden").attr("phone"),
				uAddress : $("#uAddress_hidden").attr("address"),
				finalPrice : $("#payPrice").attr("price")
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
				$.ajax({
					type : "POST",
					contentType : "application/x-www-form-urlencoded;charset=utf-8",
					url : "/io/updateUserAddress.cns",
					data : {
						openId : openId,
						uName : uName,
						uPhone : uPhone,
						uAddress : uAddress
					}
				});
			}
		});
    }
    function showProductById(productId){
    	wx.openProductSpecificView({
		    productId: productId
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
    		$(input).val(count);
    	}else{
    		showDialog("确认删除？","您将从购物车移除该商品","removeObjById('cartPid_"+obj.substring(4)+"')");
    	}
    	changeTotalPrice();
    }
    function removeObjById(obj){
    	$("#"+obj).remove();
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
    function showToast(content,misec,icon) {
    	if(!misec){
    		misec=1500;
    	}
		var $toast = $('#toast');
		if ($toast.css('display') != 'none') {
			return;
		}
		$("#toast i").remove();
		if(!icon){
			$("#toastcontent").before("<i class=\"weui_icon_toast\"></i>");
		}else{
			$("#toastcontent").before("<i class=\""+icon+"\"></i>");
		}
		$("#toastcontent").text(content);
		$toast.show();
		setTimeout(function() {
			$toast.hide();
		}, misec);
	}
	function changeTotalPrice(){
		var totalPrice=0;
		$("#tab .shopCartItemsInputCount").each(function(){
			totalPrice+=parseInt($(this).attr("price"))*parseInt($(this).val());
		});
		$("#totalPrice").attr("price",totalPrice);
		$("#totalPrice").text(totalPrice*1.0/100);
		<%-- 同步购物车 --%>
		syncCart();
	}
	var syncCartLock=false;
	var shouldUpdateCart=false;
	function syncCart(misec){
		if(!misec){
			misec=3000;
		}
		if(!syncCartLock){
        	syncCartLock=true;
			setTimeout(function () {
				var length=$("#tab .shopCartItemsInputCount").length;
				var products = "[";
				$("#tab .shopCartItemsInputCount").each(function(i){
					var productId = $(this).attr("id").substring(4);
					var productCount = $(this).val();
					if(i==length-1){
						products+="{productId : \""+productId+"\",productCount : "+productCount+"}";
					}else{
						products+="{productId : \""+productId+"\",productCount : "+productCount+"},";
					}
				});
				products += "]";
			    $.ajax({
					type : "POST",
					contentType : "application/x-www-form-urlencoded;charset=utf-8",
					url : "/io/updateShopCart.cns",
					data : {
						openId : openId,
						products : products
					},
					complete : function(){
						syncCartLock=false;
				    	if(shouldUpdateCart){
				    		syncCart();
				    		shouldUpdateCart=false;
				    	}
					}
				});
		    }, misec);
        }else{
        	shouldUpdateCart=true;
        }
	}
	var tabNow="loading";
	function tabShow(id){
		$("#"+tabNow).hide();
		tabNow=id;
		$("#"+id).show();
	}
	var cogNow="loading";
	function cogShow(id){
		$("#"+cogNow).hide();
		cogNow=id;
		$("#"+id).show();
	}
    var payProducts="";
	function goPay(){
		if(parseInt($("#totalPrice").attr("price"))<=0){
			showToast("没有可购买商品");
			return;
		}
		var productKindsCount=0;
		payProducts = "[";
		var length=$(".shopCartItemsCount input").length;
		$(".shopCartItemsCount input").each(function(i){
			if($(this).val()!="0"){
				productKindsCount++;
			}
			var productId = $(this).attr("id").substring(4);
			var productCount = $(this).val();
			if(i==length-1){
				payProducts+="{productId : \""+productId+"\",productCount : "+productCount+"}";
			}else{
				payProducts+="{productId : \""+productId+"\",productCount : "+productCount+"},";
			}
		});
		payProducts += "]";
		
		$("#productKindsCount").html("共计<font color=\"#00C800\">"+productKindsCount+"</font>种商品");
		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			data : {
				openId:openId,
				totalPrice:$("#totalPrice").attr("price")
			},
			url : "/io/goPay.cns",
			dataType : "json",
			beforeSend : function(){
				tabShow("loading");
				<%-- 隐藏活动促销 --%>
				$("#eventMinus").hide();
				$("#eventPromotionDiv").hide();
			},
			success : function(data) {
				<%-- 构建地址 --%>
				$("#uAddressB").text(data.uName+"\n"+data.uPhone);
				$("#uAddress").text(data.uAddress);
				$("#uAddress_hidden").attr("user",data.uName);
				$("#uAddress_hidden").attr("phone",data.uPhone);
				$("#uAddress_hidden").attr("address",data.uAddress);
				<%-- 已达到活动标准：活动赠品·活动通知 --%>
				var eMinus=0;
				var payContent="支付";
				if(data.minus){
					var html="🎁&nbsp;"+data.minus.eHtml;
					var minusPrice=parseInt(data.minus.eMinus)*1.0/100;
					$("#eventMinus").html(html);
					$("#eventMinus").show();
					if(data.minus.eMinus){
						payContent="支付(再减"+(parseInt(data.minus.eMinus)*1.0/100)+"元)"
					}
				}
				$("#payContent").text(payContent);
				<%-- 即将到活动标准：再次促销 --%>
				if(data.promotion){
					var html="还差<font color=\"#00c800\">"
						+((parseInt(data.promotion.eFullPrice)-parseInt($("#totalPrice").attr("price")))*1.0/100)
						+"</font>元即可满足：\n";
					html+=data.promotion.eHtml;
					$("#eventPromotion").html(html);
					$("#eventPromotionDiv").show();
				}
				<%-- 结算价格 --%>
				$("#shouldPayPrice").text($("#totalPrice").text());
		    	$("#payPrice").attr("price",$("#totalPrice").attr("price"));
		    	$("#payPrice").text(parseInt($("#totalPrice").attr("price"))*1.0/100);
			},
			complete : function(){
				tabShow("goPay");
			}
		});
	}
	function pay(){
    	if(""==$("#uAddress_hidden").attr("user")||""==$("#uAddress_hidden").attr("phone")){
    		goAddress('uAddress');
    		return;
    	}
    	var payPack=payPackage();
    	if(payPack.errMsg){
    		showDialogOnly("下单失败",payPack.errMsg);
    		return;
    	}
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
				syncCart();
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
    	goShop();
    }
    function goShop(){
    	if("INIT"==$("#shop").attr("init")){
    		$.ajax({
				type : "POST",
				contentType : "application/x-www-form-urlencoded;charset=utf-8",
				url : "/io/shop.cns",
				beforeSend : function(){
					tabShow("loading");
				},
				success : function(data) {
					<%-- 构建HTML --%>
					var html="";
					var catalogId="";
					for(var i=0;i<data.length;i++){
						html+=buildShopItems(data[i])
					}
					$("#shop").html(html);
					<%-- 移除初始化标记 --%>
					$("#shop").removeAttr("init");
				},
				complete : function(){
					tabShow("shop");
				}
			});
    	}else{
    		tabShow("shop");
    	}
    }
    function buildShopItems(data){
    	var html="";
		html+="<div class=\"weui_cells weui_cells_access shopItemsCells\">";
		if(data.groupTitle){
			html+="	<a class=\"weui_cell\" href=\"javascript:"+data.groupFunc+";\">";
			html+="	    <div class=\"weui_cell_bd weui_cell_primary shopItemsTitle\">";
			html+="	        <p><font color=\"#00c800\"  class=\"shopItemTitle\">"+data.groupTitle+"</font></p>";
			html+="	    </div>";
			html+="	</a>";
		}
		html+="	<div>";
		html+="		<ul>";
		var items=data.items;
		for(var i=0;i<items.length;i++){
				html+="			<li class=\"shopItemsList"+data.groupCols+"\" onclick=\"javascript:"+items[i].itemFunc+";\">";
				html+="		        	<img class=\"shopItemImg\" src=\""+items[i].itemImg+"\">";
				if(items[i].itemTitle){
					html+="		        	<p class=\"shopItemName\">"+items[i].itemTitle+"</p>";
				}
				html+="			</li>";
		}
		html+="		</ul>";
		html+="	</div>";
		html+="</div>";
		return html;
    }
    function goCatalog(){
    	if("INIT"==$("#catalog").attr("init")){
    		$.ajax({
				type : "POST",
				contentType : "application/x-www-form-urlencoded;charset=utf-8",
				url : "/io/catalog.cns",
				beforeSend : function(){
					tabShow("loading");
				},
				success : function(data) {
					<%-- 构建HTML --%>
					var html="";
					var catalogId="";
					for(var i=0;i<data.length;i++){
						html+="<div class=\"weui_cells weui_cells_access catalogCells\">";
						html+="	<a class=\"weui_cell\" catalogId=\""+data[i].catalogId+"\" href=\"javascript:showCata('cat','"+data[i].catalogId+"','');\">";
						html+="	    <div class=\"weui_cell_bd weui_cell_primary groupTitle\">";
						html+="	        <p><font color=\"#00c800\"  class=\"catalogTitle\">"+data[i].catalogName+"</font></p>";
						html+="	    </div>";
						html+="	</a>";
						html+="	<div>";
						html+="		<ul>";
						var groupList=data[i].groupList;
						for(var j=0;j<groupList.length;j++){
								html+="			<li groupId=\""+groupList[j].groupId+"\" onclick=\"javascript:showCata('grp','"+groupList[j].groupId+"','');\" class=\"groupList\">";
								html+="		        	<img class=\"groupImg\" src=\"${IMG_SERVER_DOMAIN}/"+groupList[j].groupImg+"\">";
								html+="		        	<p class=\"groupName\">"+groupList[j].groupName+"</p>";
								html+="			</li>";
						}
						html+="		</ul>";
						html+="	</div>";
						html+="</div>";
					}
					$("#catalog").html(html);
					<%-- 移除初始化标记 --%>
					$("#catalog").removeAttr("init");
				},
				complete : function(){
					tabShow("catalog");
				}
			});
    	}else{
    		tabShow("catalog");
    	}
    }
    function showCata(cog,cid,sort){
    	var cataId=cog+"_"+cid;
    	if($("#"+cataId).attr("id")){
    		cogShow(cataId);
    		tabShow("cataProList");
    	}else{
    		$.ajax({
				type : "POST",
				contentType : "application/x-www-form-urlencoded;charset=utf-8",
				data : {
					cog:cog,
					cid:cid,
					sort:sort
				},
				url : "/io/cataProducts.cns",
				dataType : "json",
				beforeSend : function(){
					tabShow("loading");
				},
				success : function(products) {
					<%-- 构建排序 --%>
					var html="";
					html+="<div class=\"weui_panel_bd\" id=\""+cataId+"\" style=\"display: none;\">";
					html+="<div class=\"weui_navbar cataTab\">";
					html+="    <div id=\"price_"+cataId+"\" class=\"weui_navbar_item "+("PRICE"==sort?"weui_bar_item_on":"")+"\">";
					html+="        价格";
					html+="    </div>";
					html+="    <div id=\"sales_"+cataId+"\" class=\"weui_navbar_item "+("SALES"==sort?"weui_bar_item_on":"")+"\">";
					html+="        销量";
					html+="    </div>";
					html+="</div>";
					<%-- 构建商品列表 --%>
					for(var i=0;i<products.length;i++){
						html+="<div class=\"weui_media_box weui_media_appmsg\">";
						html+="    <div class=\"weui_media_hd shopCartItemsImg\">";
						html+="        <a href=\"javascript:showProductById('"+products[i].pId+"');\"><img class=\"weui_media_appmsg_thumb\" src=\"${IMG_SERVER_DOMAIN}/"+products[i].pImg+"\"></a>";
						html+="    </div>";
						html+="    <div class=\"weui_media_bd shopCartItemsTitle\">";
						html+="        <h4 class=\"weui_media_title style=\"white-space: normal;\">"+products[i].pNm+"</h4>";
						html+="        <p class=\"weui_media_desc\">"+products[i].pNm+"</p>";
						html+="        <p class=\"weui_media_desc\"><font class=\"cataProPrice\">￥"+parseInt(products[i].pPrice)*1.0/100+"</font>"+("0"==products[i].pOriPrice?"":("<s class=\"cataProOriPrice\">￥"+parseInt(products[i].pOriPrice)*1.0/100+"</s>"))+"销量："+products[i].pSales+"</p>";
						html+="    </div>";
						html+="    <div class=\"weui_cell_ft\">";
						html+="        <a href=\"javascript:addToCart('"+products[i].pId+"','"+products[i].pNm+"','"+products[i].pPrice+"','"+products[i].pImg+"\');\"><img src=\"/images/shopcart36.svg\"></a>";
						html+="    </div>";
						html+="</div>";
					}
					html+="</div>";
					$("#cataProList").append(html);
				},
				complete : function(){
					cogShow(cataId);
    				tabShow("cataProList");
    				if("PRICE"!=sort){
    					$("#price_"+cataId).one("click",function(){
	    					$("#"+cataId).remove();
	    					showCata(cog,cid,"PRICE");
	    				});
    				}
    				if("SALES"!=sort){
    					$("#sales_"+cataId).one("click",function(){
	    					$("#"+cataId).remove();
	    					showCata(cog,cid,"SALES");
	    				});
    				}
				}
			});
    	}
    }
    function showloadingToast(misec){
    	if(!misec){
    		misec=1000;
    	}
    	$('#loading').show();
        setTimeout(function () {
            $('#loading').hide();
        }, misec);
    }
    function addToCart(pId,pNm,pPrice,pImg){
    	if($("#cartPid_"+pId).attr("id")){
    		$("#pro_"+pId).val(parseInt($("#pro_"+pId).val())+1);
    	}else{
    		addProductToCart(pId,pNm,pPrice,pImg,1);
    	}
    	changeTotalPrice();
    	showToast("成功加入购物车",500);
    }
    function addProductToCart(pId,pNm,pPrice,pImg,pCount){
    	var html="";
    	html+="<div class=\"weui_media_box weui_media_appmsg\" id=\"cartPid_"+pId+"\">";
		html+="    <div class=\"weui_media_hd shopCartItemsImg\">";
		html+="        <a href=\"javascript:showProductById('"+pId+"');\"><img class=\"weui_media_appmsg_thumb\" src=\"${IMG_SERVER_DOMAIN}/"+pImg+"\"></a>";
		html+="    </div>";
		html+="    <div class=\"weui_media_bd shopCartItemsTitle\">";
		html+="        <h4 class=\"weui_media_title\">"+pNm+"</h4>";
		html+="        <p class=\"weui_media_desc\">"+pNm+"</p>";
		html+="        <p class=\"weui_media_desc\">单价："+parseInt(pPrice)*1.0/100+"元</p>";
		html+="    </div>";
		html+="    <div class=\"weui_cell_ft shopCartItemsCount\">";
		html+="    	   <a href=\"javascript:minusCount('pro_"+pId+"');\"><img class=\"cnsMinus\" src=\"/images/minus.svg\"></a>";
		html+="        <input id=\"pro_"+pId+"\" price=\""+pPrice+"\" onchange=\"checkVal('pro_"+pId+"')\" class=\"weui_input shopCartItemsInputCount\" type=\"number\" pattern=\"[0-9]*\" value=\""+pCount+"\">";
		html+="        <a href=\"javascript:addCount('pro_"+pId+"');\"><img class=\"cnsAdd\" src=\"/images/plus.svg\"></a>";
		html+="    </div>";
		html+="</div>";
		$("#shopCartItems").append(html);
    }
    function showDialog(title,desc,sureFunc,sureStr,cancelFunc,cancelStr){
    	$("#dialogCotent").empty();
    	if(!sureStr){
    		sureStr="确定";
    	}
    	if(!cancelStr){
    		cancelStr="取消";
    	}
    	var html="";
    	html+="<div class=\"weui_dialog_hd\"><strong class=\"weui_dialog_title\">"+title+"</strong></div>";
        html+="<div class=\"weui_dialog_bd\">"+desc+"</div>";
        html+="<div class=\"weui_dialog_ft\">";
        html+="    <a href=\"javascript:"+cancelFunc+";\" class=\"weui_btn_dialog default\">"+cancelStr+"</a>";
        html+="    <a href=\"javascript:"+sureFunc+";\" class=\"weui_btn_dialog primary\">"+sureStr+"</a>";
        html+="</div>";
        $("#dialogCotent").html(html);
        $("#dialog").show().on("click", ".weui_btn_dialog", function () {
        	$("#dialog").off("click").hide();
        });
    }
    function showDialogOnly(title,desc){
    	$("#dialogCotent").empty();
    	var html="";
    	html+="<div class=\"weui_dialog_hd\"><strong class=\"weui_dialog_title\">"+title+"</strong></div>";
        html+="<div class=\"weui_dialog_bd\">"+desc+"</div>";
        html+="<div class=\"weui_dialog_ft\">";
        html+="    <a href=\"javascript:;\" class=\"weui_btn_dialog primary\">确定</a>";
        html+="</div>";
        $("#dialogCotent").html(html);
        $("#dialog").show().on("click", ".weui_btn_dialog", function () {
        	$("#dialog").off("click").hide();
        });
    }
    function goMe(){
   		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			url : "/io/getUsrMine.cns",
			dataType : "json",
			data : {
				openId : openId
			},
			beforeSend : function(){
				tabShow("loading");
			},
			success : function(data) {
				<%-- 构建HTML --%>
				var html="";
				html+="<div class=\"weui_cell\">";
			    html+="  <div class=\"weui_cell_hd\" style=\"width:50px;height:50px; border-radius:50%; overflow:hidden;\">";
			    html+="  <img src=\""+data.headimgurl+"\" alt=\"\" style=\"width:50px;margin-right:5px;display:block\">";
			    html+="  </div>";
			    html+="  <div class=\"weui_cell_bd weui_cell_primary\" style=\"margin-left:10px\" >";
			    html+="      <p>"+data.nickname+"</p>";
			    html+="  </div>";
			    html+="  <div class=\"weui_cell_ft\"></div>";
			  	html+="</div>";
			  	html+="<div class=\"weui_cells\">";
			    html+="  <div class=\"weui_cell\">";
				if("DIAMOND"==data.vipKind){
					if("DELETE"==data.vipStatus){
						html+="      <div class=\"weui_cell_bd weui_cell_primary\">";
					    html+="          <p>会员级别：💎钻石会员</p>";
					    html+="      </div>";
					    html+="      <div class=\"weui_cell_ft\" style=\"font-size:10px\" onclick=\"directVip()\">(已删除)重新领取</div>";
					}else{
						html+="      <div class=\"weui_cell_bd weui_cell_primary\">";
					    html+="          <p>会员级别：💎钻石会员</p>";
					    html+="      </div>";
					    html+="      <div class=\"weui_cell_ft\">"+data.vipBonus+"</div>";
					}
				}else{
					html+="      <div class=\"weui_cell_bd weui_cell_primary\">";
				    html+="          <p>会员级别：普通会员</p>";
				    html+="      </div>";
				    html+="      <div class=\"weui_cell_ft\" style=\"font-size:10px\" onclick=\"directVip()\">领钻石会员</div>";
				}
			    html+="  </div>";
			  	html+="</div>";
			  	html+="<div class=\"weui_cells weui_cells_access\">";
			    html+="  <a class=\"weui_cell\" href=\"javascript:goOrderDetail();\">";
			    html+="      <div class=\"weui_cell_bd weui_cell_primary\">";
			    html+="          <p>📑&nbsp;订单详情</p>";
			    html+="      </div>";
			    html+="      <div class=\"weui_cell_ft\">";
			    html+="      </div>";
			    html+="  </a>";
			    html+="  <a class=\"weui_cell\" href=\"javascript:goAddress();\">";
			    html+="      <div class=\"weui_cell_bd weui_cell_primary\">";
			    html+="          <p>📍&nbsp;默认收货信息</p>";
			    html+="      </div>";
			    html+="      <div class=\"weui_cell_ft\">";
			    html+="      </div>";
			    html+="  </a>";
			  	html+="</div>";
			  	html+="<div class=\"weui_cell\">";
			    html+="  <div class=\"weui_cell_bd weui_cell_primary\">";
			    html+="      <p>&nbsp;</p>";
			    html+="  </div>";
			    html+="  <div class=\"weui_cell_ft\">街角小店</div>";
			 	html+=" </div>";
				$("#me").html(html);
				<%-- 移除初始化标记 --%>
				$("#me").removeAttr("init");
			},
			complete : function(){
				tabShow("me");
			}
		});
    }
    function directVip(){
    	window.location="http://mp.weixin.qq.com/bizmall/cardshelf?shelf_id=3&showwxpaytitle=1&biz=MzAwOTgyMjIzOA==&t=cardticket/shelf_list&scene=1000007#wechat_redirect";
    }
    function goOrderDetail(){
    	$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			url : "/io/getUsrOrder.cns",
			dataType : "json",
			data : {
				openId : openId,
				timeKind : "TODAY"
			},
			beforeSend : function(){
				tabShow("loading");
			},
			success : function(data) {
				<%-- 构建HTML --%>
				var html="";
				html+="<div class=\"weui_cells weui_cells_access orderRealCells\" style=\"margin-top:0;\">";
				html+="<div class=\"weui_cells_title orderRealDateTitle\"><i class=\"weui_icon_waiting\"></i>&nbsp;今日</div>";
				for(var i=0;i<data.length;i++){
					html+="<div class=\"weui_cells_title orderRealTitle\">"+data[i].oTime+"<font class=\"orderRealFontRight\">实际支付：<font color=\"#00c800\">"+(parseInt(data[i].cashFee)*1.0/100)+"</font>元</font></div>";
					html+="	<div class=\"weui_cell orderRealCell\">";
					html+="        <div class=\"weui_cell_bd weui_cell_primary orderRealP\">";
					var productsArray=data[i].products.split("\n");
					for(var j=0;j<productsArray.length;j++){
						html+="            <p>"+productsArray[j]+"</p>";
						if(j==productsArray.length-1){
							html+="            <p>总金额：<font color=\"#00c800\">"+(parseInt(data[i].totalFee)*1.0/100)+"</font></p>";
							html+="            <p>"+data[i].uName+"&nbsp;&nbsp;"+data[i].uPhone+"</p>";
							html+="            <p>"+data[i].uAddress+"</p>";
						}
					}
					html+="        </div>";
					html+="    </div>";
				}
				html+="</div>";
				html+="<div class=\"weui_cells_title orderRealDateTitle\" id=\"orderHistoryMore\"><i class=\"weui_icon_waiting_circle\"></i>&nbsp;点击查看更多...</div>";
				$("#orderDetail").html(html);
				<%-- 移除初始化标记 --%>
				$("#orderDetail").removeAttr("init");
			},
			complete : function(){
				tabShow("orderDetail");
				$("#orderHistoryMore").one('click',function(){
					showMoreOrderDetail();
					$("#orderHistoryMore").hide();
				});
			}
		});
    }
     function showMoreOrderDetail(){
    	$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			url : "/io/getUsrOrder.cns",
			dataType : "json",
			data : {
				openId : openId,
				timeKind : "HISTORY"
			},
			beforeSend : function(){
				tabShow("loading");
			},
			success : function(data) {
				<%-- 构建HTML --%>
				var html="";
				var historyDate="";
				html+="<div class=\"weui_cells weui_cells_access orderRealCells\" style=\"margin-top:0;\">";
				for(var i=0;i<data.length;i++){
					if(historyDate!=data[i].oDate){
						html+="<div class=\"weui_cells_title orderRealDateTitle\"><i class=\"weui_icon_waiting_circle\"></i>&nbsp;"+data[i].oDate+"</div>";
						historyDate=data[i].oDate;
					}
					html+="<div class=\"weui_cells_title orderRealTitle\">"+data[i].oTime+"<font class=\"orderRealFontRight\">实际支付：<font color=\"#00c800\">"+(parseInt(data[i].cashFee)*1.0/100)+"</font>元</font></div>";
					html+="	<div class=\"weui_cell orderRealCell\">";
					html+="        <div class=\"weui_cell_bd weui_cell_primary orderRealP\">";
					var productsArray=data[i].products.split("\n");
					for(var j=0;j<productsArray.length;j++){
						html+="            <p>"+productsArray[j]+"</p>";
						if(j==productsArray.length-1){
							html+="            <p>总金额：<font color=\"#00c800\">"+(parseInt(data[i].totalFee)*1.0/100)+"</font></p>";
							html+="            <p>"+data[i].uName+"&nbsp;&nbsp;"+data[i].uPhone+"</p>";
							html+="            <p>"+data[i].uAddress+"</p>";
						}
					}
					html+="        </div>";
					html+="    </div>";
				}
				html+="</div>";
				$("#orderDetail").append(html);
			},
			complete : function(){
				tabShow("orderDetail");
			}
		});
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
					  <div id="cataProList" style="display: none;">
					  		<%-- 分类商品列表 --%>
					  </div>
					  <div id="shop" init="INIT" style="display: none;">
					   		<%-- 商城 --%>
					  </div>
					  <div id="shopCart" style="display: none;">
					  		<div class="weui_panel_bd" id="shopCartItems">
					  			<%-- 购物车商品 --%>
					        </div>
					        <div class="weui_cell">
					            <div class="weui_cell_hd weui_cell_primary">
					           		 合计：<font color="#00c800" id="totalPrice" price="0">0.00</font>&nbsp;元
					            </div>
					            <div class="weui_cell_ft">
					            	<a href="javascript:goPay();" class="weui_btn weui_btn_primary">去结算&nbsp;<i class="weui_icon_success_circle bfwhite"></i></a>
					            </div>
					        </div>
					  </div>
					  <div id="me" init="INIT" style="display: none;">
					  	<%-- 我的 --%>
					  </div>
					  <div id="orderDetail" init="INIT" style="display: none;">
					  	<%-- 订单详情 --%>
					  </div>
					  <div id="goPay" style="display: none;">
					  		<div class="weui_cells weui_cells_access" style="margin-top:0;">
						  		<a class="weui_cell" href="javascript:goAddress('uAddress');">
						  			<div class="weui_cell_hd">
					  					<p style="width:20px;margin-right:5px;display:block">📍</p>
					  				</div>
						            <div class="weui_cell_bd weui_cell_primary">
						            	<p><b id="uAddressB"></b></p>
						                <p id="uAddress"></p>
						                <input type="hidden" id="uAddress_hidden" user="" phone="" address=""> 
						            </div>
						            <div class="weui_cell_ft"></div>
						        </a>
						        <a class="weui_cell" href="javascript:tabShow('shopCart');">
						        	<div class="weui_cell_hd">
					  					<p style="width:20px;margin-right:5px;display:block">🛍</p>
					  				</div>
						            <div class="weui_cell_bd weui_cell_primary">
						                <p id="productKindsCount"></p>
						            </div>
						            <div class="weui_cell_ft"></div>
						        </a>
						        <div class="weui_cell">
						            <div class="weui_cell_bd weui_cell_primary">
						                <p>金额：<font color="#00c800" id="shouldPayPrice">0.00</font></p>
						                <p id="eventMinus" style="display:none"></p>
						            </div>
						        </div>
						        <div class="weui_cell" id="eventPromotionDiv" style="display:none;font-size: 14px;color: #ACACAC;">
						        	<div class="weui_cell_hd">
					  					<p style="width:20px;margin-right:5px;display:block">📡</p>
					  				</div>
						            <div class="weui_cell_bd weui_cell_primary">
						                <p id="eventPromotion"></p>
						            </div>
						        </div>
					        </div>
					        <div class="weui_cell">
					            <div class="weui_cell_hd weui_cell_primary">
					           		 合计：<font color="#00c800" id="payPrice" price="0">0.00</font>&nbsp;元
					            </div>
					            <div class="weui_cell_ft">
					            	<a href="javascript:pay();" class="weui_btn weui_btn_primary"><font id="payContent">支付</font>&nbsp;<i class="weui_icon_safe_success bfwhite"></i></a>
					            </div>
					        </div>
					  </div>
					  <div class="msg"  id="showMsg" style="display: none;">
					  	<%--信息提示 --%>
					 </div>
			    </div>
			    <div class="weui_tabbar">
			        <a href="javascript:goCatalog();" class="weui_tabbar_item weui_bar_item_on">
			            <div class="weui_tabbar_icon">
			                <img src="/images/nav_catalog.svg" alt="">
			            </div>
			            <p class="weui_tabbar_label">分类</p>
			        </a>
			        <a href="javascript:goShop();" class="weui_tabbar_item">
			            <div class="weui_tabbar_icon">
			                <img src="/images/nav_shop.svg" alt="">
			            </div>
			            <p class="weui_tabbar_label">商城</p>
			        </a>
			        <a href="javascript:tabShow('shopCart');" class="weui_tabbar_item">
			            <div class="weui_tabbar_icon">
			                <img src="/images/nav_cart.svg" alt="">
			            </div>
			            <p class="weui_tabbar_label">购物车</p>
			        </a>
			        <a href="javascript:goMe();" class="weui_tabbar_item">
			            <div class="weui_tabbar_icon">
			                <img src="/images/nav_profile.svg" alt="">
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
            <p class="weui_toast_content" id="toastcontent">成功</p>
        </div>
  </div>
  <div id="loading" class="weui_loading_toast" style="display: none;">
	    <div class="weui_mask_transparent"></div>
	    <div class="weui_toast">
	        <div class="weui_loading">
	            <div class="weui_loading_leaf weui_loading_leaf_0"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_1"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_2"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_3"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_4"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_5"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_6"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_7"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_8"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_9"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_10"></div>
	            <div class="weui_loading_leaf weui_loading_leaf_11"></div>
	        </div>
	        <p class="weui_toast_content">数据加载中</p>
	    </div>
  </div>
  <div class="weui_dialog_confirm" id="dialog" style="display: none;">
	    <div class="weui_mask"></div>
	    <div class="weui_dialog" id="dialogCotent">
	        <%-- 弹出对话框 --%>
	    </div>
  </div>
  <div id="actionSheet_wrap">
    <div class="weui_mask_transition" id="mask" style="display: none;"></div>
    <div class="weui_actionsheet" id="weui_actionsheet">
  </div>
</div>
</html>