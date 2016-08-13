;
(function($) {
	$.fn.slider = function(options) {
		var defaults = {
			caisliNum : false, // 是否添加圈圈
			caisliTitle : false, // 是否添加标题
			caisliTouch : false, // 是否支持滑动
			caisliTime : 2000 // 间隔时间
		};
		var opts = $.extend({}, defaults, options);
		return this.each(function() {
					var obj = $(this);
					var caisliCon = obj.find(".caisli_con");
					var imgLen = obj.find(".caisli_con li").length;
					var imgIndex = 0;
					var transLeft = 0;

					// 设置包含图片容器的宽度
					var caisliConWidth = 100 * imgLen;
					var imgWidth = (100 / imgLen).toFixed(5);
					obj.find(".caisli_con").css("width", caisliConWidth + "%");
					obj.find(".caisli_con li").css("width", imgWidth + "%");

					// 添加支持滑动
					if (opts.caisliTouch) {
						var startPosition, endPosition, deltaX, deltaY, moveLength;
						$(obj).bind('touchstart', function(e) {
							var touch = e.touches[0];
							startPosition = {
								x : touch.pageX,
								y : touch.pageY
							}
						}).bind(
								'touchmove',
								function(e) {
									e.preventDefault();
									var touch = e.touches[0];
									endPosition = {
										x : touch.pageX,
										y : touch.pageY
									};

									deltaX = endPosition.x - startPosition.x;
									deltaY = endPosition.y - startPosition.y;
									moveLength = Math.sqrt(Math.pow(Math
											.abs(deltaX), 2)
											+ Math.pow(Math.abs(deltaY), 2));
								}).bind('touchend', function(e) {
							if (deltaX < 100 && moveLength > 100) { // 向左划动
								clearInterval(caisli);
								imgIndex++;
								if (imgIndex == imgLen) {
									imgIndex = 0;
								}
								caisliSlide(imgIndex);
								caisli = setInterval(function() {
									caisliSlide(imgIndex);
									imgIndex++;
									if (imgIndex == imgLen) {
										imgIndex = 0;
									}
								}, opts.caisliTime);
							} else if (deltaX > 100 && moveLength > 100) { // 向右划动
								clearInterval(caisli);
								imgIndex--;
								if (imgIndex == -1) {
									imgIndex = imgLen - 1;
								}
								caisliSlide(imgIndex);
								caisli = setInterval(function() {
									caisliSlide(imgIndex);
									imgIndex++;
									if (imgIndex == imgLen) {
										imgIndex = 0;
									}
								}, opts.caisliTime);
							}
							deltaX = 0;
							moveLength = 0;
						});
					}
					// 添加标题
					if (opts.caisliTitle) {
						obj.append("<div class='caisli_title'><h3></h3></div>");
						$(".caisli_title h3").html(
								obj.find(".caisli_con li").eq(0).find("img").attr(
										"alt"));
					}
					// 添加圈圈
					if (opts.caisliNum) {
						obj.append("<div class='caisli_num'><span class='num_on'></span></div>");
						for (var i = 0; i < imgLen - 1; i++) {
							$(".caisli_num").append("<span></span>");
						}
						var numSpan = obj.find(".caisli_num span");
						// 兼容PC端
						numSpan.mouseover(function() {
							imgIndex = $(this).index();
							caisliSlide(imgIndex);
						});
					}

					// 滑动定时器
					caisli = setInterval(function() {
						imgIndex++;
						if (imgIndex == imgLen) {
							imgIndex = 0;
						}
						caisliSlide(imgIndex);
					}, opts.caisliTime);
					// 公共滑动函数
					function caisliSlide(index) {
						$(".caisli_title h3").html(obj.find(".caisli_con li").eq(index).find("img").attr("alt"));
						transLeft = -imgWidth * index + "%";
						caisliCon.css({
							"transform" : "translate(" + transLeft + ")",
							"-webkit-transform" : "translate(" + transLeft+ ")"
						});
						numSpan.removeClass("num_on").eq(index).addClass("num_on");
					}
				})
	}

})(Zepto);