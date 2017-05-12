/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.wit.net
 * License: http://www.wit.net/license
 * 
 * JavaScript - Common
 * Version: 3.0
 */

var wit = {
	base: "${base}",
	locale: "${locale}",
	propertyBase:"http://app.pmsaas.net"
};

var setting = {
	priceScale: "${setting.priceScale}",
	priceRoundType: "${setting.priceRoundType}",
	currencySign: "${setting.currencySign}",
	currencyUnit: "${setting.currencyUnit}",
	uploadImageExtension: "${setting.uploadImageExtension}",
	uploadFlashExtension: "${setting.uploadFlashExtension}",
	uploadMediaExtension: "${setting.uploadMediaExtension}",
	uploadFileExtension: "${setting.uploadFileExtension}"
};

var messages = {
	"b2c.message.success": "${message("b2c.message.success")}",
	"b2c.message.error": "${message("b2c.message.error")}",
	"b2c.dialog.ok": "${message("b2c.dialog.ok")}",
	"b2c.dialog.cancel": "${message("b2c.dialog.cancel")}",
	"b2c.dialog.deleteConfirm": "${message("b2c.dialog.deleteConfirm")}",
	"b2c.dialog.clearConfirm": "${message("b2c.dialog.clearConfirm")}",
	"b2c.browser.title": "选择文件",
	"b2c.browser.upload": "本地上传",
	"b2c.browser.parent": "上级目录",
	"b2c.browser.orderType": "排序方式",
	"b2c.browser.name": "名称",
	"b2c.browser.size": "大小",
	"b2c.browser.type": "类型",
	"b2c.browser.select": "选择文件",
	"b2c.upload.sizeInvalid": "上传文件大小超出限制",
	"b2c.upload.typeInvalid": "上传文件格式不正确",
	"b2c.upload.invalid": "上传文件格式或大小不正确",
	"b2c.validate.required": "${message("b2c.validate.required")}",
	"b2c.validate.email": "${message("b2c.validate.email")}",
	"b2c.validate.url": "${message("b2c.validate.url")}",
	"b2c.validate.date": "${message("b2c.validate.date")}",
	"b2c.validate.dateISO": "${message("b2c.validate.dateISO")}",
	"b2c.validate.pointcard": "${message("b2c.validate.pointcard")}",
	"b2c.validate.number": "${message("b2c.validate.number")}",
	"b2c.validate.digits": "${message("b2c.validate.digits")}",
	"b2c.validate.minlength": "${message("b2c.validate.minlength")}",
	"b2c.validate.maxlength": "${message("b2c.validate.maxlength")}",
	"b2c.validate.rangelength": "${message("b2c.validate.rangelength")}",
	"b2c.validate.min": "${message("b2c.validate.min")}",
	"b2c.validate.max": "${message("b2c.validate.max")}",
	"b2c.validate.range": "${message("b2c.validate.range")}",
	"b2c.validate.accept": "${message("b2c.validate.accept")}",
	"b2c.validate.equalTo": "${message("b2c.validate.equalTo")}",
	"b2c.validate.remote": "${message("b2c.validate.remote")}",
	"b2c.validate.integer": "${message("b2c.validate.integer")}",
	"b2c.validate.positive": "${message("b2c.validate.positive")}",
	"b2c.validate.negative": "${message("b2c.validate.negative")}",
	"b2c.validate.decimal": "${message("b2c.validate.decimal")}",
	"b2c.validate.pattern": "${message("b2c.validate.pattern")}",
	"b2c.validate.extension": "${message("b2c.validate.extension")}"
};

// 添加Cookie
function addCookie(name, value, options) {
	if (arguments.length > 1 && name != null) {
		if (options == null) {
			options = {};
		}
		if (value == null) {
			options.expires = -1;
		}
		if (typeof options.expires == "number") {
			var time = options.expires;
			var expires = options.expires = new Date();
			expires.setTime(expires.getTime() + time * 1000);
		}
		document.cookie = encodeURIComponent(String(name)) + "=" + encodeURIComponent(String(value)) + (options.expires ? "; expires=" + options.expires.toUTCString() : "") + (options.path ? "; path=" + options.path : "") + (options.domain ? "; domain=" + options.domain : ""), (options.secure ? "; secure" : "");
	}
}

// 获取Cookie
function getCookie(name) {
	if (name != null) {
		var value = new RegExp("(?:^|; )" + encodeURIComponent(String(name)) + "=([^;]*)").exec(document.cookie);
		return value ? decodeURIComponent(value[1]) : null;
	}
}

// 移除Cookie
function removeCookie(name, options) {
	addCookie(name, null, options);
}

// 货币格式化
function currency(value, showSign, showUnit) {
	if (value != null) {
		var price;
		if (setting.priceRoundType == "roundHalfUp") {
			price = (Math.round(value * Math.pow(10, setting.priceScale)) / Math.pow(10, setting.priceScale)).toFixed(setting.priceScale);
		} else if (setting.priceRoundType == "roundUp") {
			price = (Math.ceil(value * Math.pow(10, setting.priceScale)) / Math.pow(10, setting.priceScale)).toFixed(setting.priceScale);
		} else {
			price = (Math.floor(value * Math.pow(10, setting.priceScale)) / Math.pow(10, setting.priceScale)).toFixed(setting.priceScale);
		}
		if (showSign) {
			price = setting.currencySign + price;
		}
		if (showUnit) {
			price += setting.currencyUnit;
		}
		return price;
	}
}

// 多语言
function message(code) {
	if (code != null) {
		var content = messages[code] != null ? messages[code] : code;
		if (arguments.length == 1) {
			return content;
		} else {
			if ($.isArray(arguments[1])) {
				$.each(arguments[1], function(i, n) {
					content = content.replace(new RegExp("\\{" + i + "\\}", "g"), n);
				});
				return content;
			} else {
				$.each(Array.prototype.slice.apply(arguments).slice(1), function(i, n) {
					content = content.replace(new RegExp("\\{" + i + "\\}", "g"), n);
				});
				return content;
			}
		}
	}
}

(function($) {

	var zIndex = 100;

	// 检测登录
	$.checkLogin = function() {
		var result = false;
		$.ajax({
			url: wit.base + "/login/check.jhtml",
			type: "GET",
			dataType: "json",
			cache: false,
			async: false,
			success: function(data) {
				result = data;
			}
		});
		return result;
	}

	// 跳转登录
	$.redirectLogin = function (redirectUrl, message) {
		var href = wit.base + "/login.jhtml";
		if (redirectUrl != null) {
			href += "?redirectUrl=" + encodeURIComponent(redirectUrl);
		}
		if (message != null) {
			$.message("warn", message);
			setTimeout(function() {
				location.href = href;
			}, 1000);
		} else {
			location.href = href;
		}
	}

	// 消息框
	var $message;
	var messageTimer;
	$.message = function() {
		var message = {};
		if ($.isPlainObject(arguments[0])) {
			message = arguments[0];
		} else if (typeof arguments[0] === "string" && typeof arguments[1] === "string") {
			message.type = arguments[0];
			message.content = arguments[1];
		} else {
			return false;
		}
		
		if (message.type == null || message.content == null) {
			return false;
		}
		
		if ($message == null) {
			$message = $('<div class="xxMessage"><div class="messageContent message' + message.type + 'Icon"><\/div><\/div>');
			if (!window.XMLHttpRequest) {
				$message.append('<iframe class="messageIframe"><\/iframe>');
			}
			$message.appendTo("body");
		}
		
		$message.children("div").removeClass("messagewarnIcon messageerrorIcon messagesuccessIcon").addClass("message" + message.type + "Icon").html(message.content);
		$message.css({"margin-left": - parseInt($message.outerWidth() / 2), "z-index": zIndex ++}).show();
		
		clearTimeout(messageTimer);
		messageTimer = setTimeout(function() {
			$message.hide();
		}, 3000);
		return $message;
	}

	// 令牌	
	$(document).ajaxSend(function(event, request, settings) {
		if (!settings.crossDomain && settings.type != null && settings.type.toLowerCase() == "post") {
			var token = getCookie("token");
			if (token != null) {
				request.setRequestHeader("token", token);
			}
		}
	});
	
	$(document).ajaxComplete(function(event, request, settings) {
		var loginStatus = request.getResponseHeader("loginStatus");
		var tokenStatus = request.getResponseHeader("tokenStatus");
		
		if (loginStatus == "accessDenied") {
			$.redirectLogin(location.href, "${message("b2c.login.accessDenied")}");
		} else if (tokenStatus == "accessDenied") {
			var token = getCookie("token");
			if (token != null) {
				$.extend(settings, {
					global: false,
					headers: {token: token}
				});
				$.ajax(settings);
			}
		}
	});

})(jQuery);

// 令牌
$().ready(function() {

	$("form").submit(function() {
		var $this = $(this);
		if ($this.attr("method") != null && $this.attr("method").toLowerCase() == "post" && $this.find("input[name='token']").size() == 0) {
			var token = getCookie("token");
			if (token != null) {
				$this.append('<input type="hidden" name="token" value="' + token + '" \/>');
			}
		}
	});
	
	

});

// 验证消息
if ($.validator != null) {
	$.extend($.validator.messages, {
		required: message("b2c.validate.required"),
		email: message("b2c.validate.email"),
		url: message("b2c.validate.url"),
		date: message("b2c.validate.date"),
		dateISO: message("b2c.validate.dateISO"),
		pointcard: message("b2c.validate.pointcard"),
		number: message("b2c.validate.number"),
		digits: message("b2c.validate.digits"),
		minlength: $.validator.format(message("b2c.validate.minlength")),
		maxlength: $.validator.format(message("b2c.validate.maxlength")),
		rangelength: $.validator.format(message("b2c.validate.rangelength")),
		min: $.validator.format(message("b2c.validate.min")),
		max: $.validator.format(message("b2c.validate.max")),
		range: $.validator.format(message("b2c.validate.range")),
		accept: message("b2c.validate.accept"),
		equalTo: message("b2c.validate.equalTo"),
		remote: message("b2c.validate.remote"),
		integer: message("b2c.validate.integer"),
		positive: message("b2c.validate.positive"),
		negative: message("b2c.validate.negative"),
		decimal: message("b2c.validate.decimal"),
		pattern: message("b2c.validate.pattern"),
		extension: message("b2c.validate.extension")
	});
	
	$.validator.setDefaults({
		errorClass: "fieldError",
		ignore: ".ignore",
		ignoreTitle: true,
		errorPlacement: function(error, element) {
			var fieldSet = element.closest("span.fieldSet");
			if (fieldSet.size() > 0) {
				error.appendTo(fieldSet);
			} else {
				error.insertAfter(element);
			}
		},
		submitHandler: function(form) {
			$(form).find(":submit").prop("disabled", true);
			form.submit();
		}
	});
}

 (function($) {

	var zIndex = 100;
	
	// 消息框
	var $message;
	var messageTimer;
	$.message = function() {
		var message = {};
		if ($.isPlainObject(arguments[0])) {
			message = arguments[0];
		} else if (typeof arguments[0] === "string" && typeof arguments[1] === "string") {
			message.type = arguments[0];
			message.content = arguments[1];
		} else {
			return false;
		}
		
		if (message.type == null || message.content == null) {
			return false;
		}
		
		if ($message == null) {
			$message = $('<div class="xxMessage"><div class="messageContent message' + message.type + 'Icon"><\/div><\/div>');
			if (!window.XMLHttpRequest) {
				$message.append('<iframe class="messageIframe"><\/iframe>');
			}
			$message.appendTo("body");
		}
		
		$message.children("div").removeClass("messagewarnIcon messageerrorIcon messagesuccessIcon").addClass("message" + message.type + "Icon").html(message.content);
		$message.css({"margin-left": - parseInt($message.outerWidth() / 2), "z-index": zIndex ++}).show();
		
		clearTimeout(messageTimer);
		messageTimer = setTimeout(function() {
			$message.hide();
		}, 3000);
		return $message;
	}

	// 对话框
	$.dialog = function(options) {
		var settings = {
			width: 320,
			height: "auto",
			modal: true,
			ok: message("b2c.dialog.ok"),
			cancel: message("b2c.dialog.cancel"),
			onShow: null,
			onClose: null,
			onOk: null,
			onCancel: null
		};
		$.extend(settings, options);
		
		if (settings.content == null) {
			return false;
		}
		
		var $dialog = $('<div class="xxDialog"><\/div>');
		var $dialogTitle;
		var $dialogClose = $('<div class="dialogClose"><\/div>').appendTo($dialog);
		var $dialogContent;
		var $dialogBottom;
		var $dialogOk;
		var $dialogCancel;
		var $dialogOverlay;
		if (settings.title != null) {
			$dialogTitle = $('<div class="dialogTitle"><\/div>').appendTo($dialog);
		}
		if (settings.type != null) {
			$dialogContent = $('<div class="dialogContent dialog' + settings.type + 'Icon"><\/div>').appendTo($dialog);
		} else {
			$dialogContent = $('<div class="dialogContent"><\/div>').appendTo($dialog);
		}
		if (settings.ok != null || settings.cancel != null) {
			$dialogBottom = $('<div class="dialogBottom"><\/div>').appendTo($dialog);
		}
		if (settings.ok != null) {
			$dialogOk = $('<input type="button" class="button" value="' + settings.ok + '" \/>').appendTo($dialogBottom);
		}
		if (settings.cancel != null) {
			$dialogCancel = $('<input type="button" class="button" value="' + settings.cancel + '" \/>').appendTo($dialogBottom);
		}
		if (!window.XMLHttpRequest) {
			$dialog.append('<iframe class="dialogIframe"><\/iframe>');
		}
		$dialog.appendTo("body");
		if (settings.modal) {
			$dialogOverlay = $('<div class="dialogOverlay"><\/div>').insertAfter($dialog);
		}
		
		var dialogX;
		var dialogY;
		if (settings.title != null) {
			$dialogTitle.text(settings.title);
		}
		$dialogContent.html(settings.content);
		$dialog.css({"width": settings.width, "height": settings.height, "margin-left": - parseInt(settings.width / 2), "z-index": zIndex ++});
		dialogShow();
		
		if ($dialogTitle != null) {
			$dialogTitle.mousedown(function(event) {
				$dialog.css({"z-index": zIndex ++});
				var offset = $(this).offset();
				if (!window.XMLHttpRequest) {
					dialogX = event.clientX - offset.left;
					dialogY = event.clientY - offset.top;
				} else {
					dialogX = event.pageX - offset.left;
					dialogY = event.pageY - offset.top;
				}
				$("body").bind("mousemove", function(event) {
					$dialog.css({"top": event.clientY - dialogY, "left": event.clientX - dialogX, "margin": 0});
				});
				return false;
			}).mouseup(function() {
				$("body").unbind("mousemove");
				return false;
			});
		}
		
		if ($dialogClose != null) {
			$dialogClose.click(function() {
				dialogClose();
				return false;
			});
		}
		
		if ($dialogOk != null) {
			$dialogOk.click(function() {
				if (settings.onOk && typeof settings.onOk == "function") {
					if (settings.onOk($dialog) != false) {
						dialogClose();
					}
				} else {
					dialogClose();
				}
				return false;
			});
		}
		
		if ($dialogCancel != null) {
			$dialogCancel.click(function() {
				if (settings.onCancel && typeof settings.onCancel == "function") {
					if (settings.onCancel($dialog) != false) {
						dialogClose();
					}
				} else {
					dialogClose();
				}
				return false;
			});
		}
		
		function dialogShow() {
			if (settings.onShow && typeof settings.onShow == "function") {
				if (settings.onShow($dialog) != false) {
					$dialog.show();
					$dialogOverlay.show();
				}
			} else {
				$dialog.show();
				$dialogOverlay.show();
			}
		}
		function dialogClose() {
			if (settings.onClose && typeof settings.onClose == "function") {
				if (settings.onClose($dialog) != false) {
					$dialogOverlay.remove();
					$dialog.remove();
				}
			} else {
				$dialogOverlay.remove();
				$dialog.remove();
			}
		}
		return $dialog;
	}

	// 文件浏览
	$.fn.extend({
		browser: function(options) {
			var settings = {
				type: "image",
				title: message("b2c.browser.title"),
				isUpload: true,
				browserUrl: wit.base + "/file/browser.jhtml",
				uploadUrl: wit.base + "/file/upload.jhtml",
				callback: null
			};
			$.extend(settings, options);
			
			var token = getCookie("token");
			var cache = {};
			return this.each(function() {
				var browserFrameId = "browserFrame" + (new Date()).valueOf() + Math.floor(Math.random() * 1000000);
				var $browserButton = $(this);
				$browserButton.click(function() {
					var $browser = $('<div class="xxBrowser"><\/div>');
					var $browserBar = $('<div class="browserBar"><\/div>').appendTo($browser);
					var $browserFrame;
					var $browserForm;
					var $browserUploadButton;
					var $browserUploadInput;
					var $browserParentButton;
					var $browserOrderType;
					var $browserLoadingIcon;
					var $browserList;
					if (settings.isUpload) {
						$browserFrame = $('<iframe id="' + browserFrameId + '" name="' + browserFrameId + '" style="display: none;"><\/iframe>').appendTo($browserBar);
						$browserForm = $('<form action="' + settings.uploadUrl + '" method="post" encType="multipart/form-data" target="' + browserFrameId + '"><input type="hidden" name="token" value="' + token + '" \/><input type="hidden" name="fileType" value="' + settings.type + '" \/><\/form>').appendTo($browserBar);
						$browserUploadButton = $('<a href="javascript:;" class="browserUploadButton button">' + message("b2c.browser.upload") + '<\/a>').appendTo($browserForm);
						$browserUploadInput = $('<input type="file" name="file" \/>').appendTo($browserUploadButton);
					}
					$browserParentButton = $('<a href="javascript:;" class="button">' + message("b2c.browser.parent") + '<\/a>').appendTo($browserBar);
					$browserBar.append(message("b2c.browser.orderType") + ": ");
					$browserOrderType = $('<select name="orderType" class="browserOrderType"><option value="name">' + message("b2c.browser.name") + '<\/option><option value="size">' + message("b2c.browser.size") + '<\/option><option value="type">' + message("b2c.browser.type") + '<\/option><\/select>').appendTo($browserBar);
					$browserLoadingIcon = $('<span class="loadingIcon" style="display: none;">&nbsp;<\/span>').appendTo($browserBar);
					$browserList = $('<div class="browserList"><\/div>').appendTo($browser);
	
					var $dialog = $.dialog({
						title: settings.title,
						content: $browser,
						width: 470,
						modal: true,
						ok: null,
						cancel: null
					});
					
					browserList("/");
					
					function browserList(path) {
						var key = settings.type + "_" + path + "_" + $browserOrderType.val();
						if (cache[key] == null) {
							$.ajax({
								url: settings.browserUrl,
								type: "GET",
								data: {fileType: settings.type, orderType: $browserOrderType.val(), path: path},
								dataType: "json",
								cache: false,
								beforeSend: function() {
									$browserLoadingIcon.show();
								},
								success: function(data) {
									createBrowserList(path, data);
									cache[key] = data;
								},
								complete: function() {
									$browserLoadingIcon.hide();
								}
							});
						} else {
							createBrowserList(path, cache[key]);
						}
					}
					
					function createBrowserList(path, data) {
						var browserListHtml = "";
						$.each(data, function(i, fileInfo) {
							var iconUrl;
							var title;
							if (fileInfo.isDirectory) {
								iconUrl = wit.base + "/resources/b2c/images/folder_icon.gif";
								title = fileInfo.name;
							} else if (new RegExp("^\\S.*\\.(jpg|jpeg|bmp|gif|png)$", "i").test(fileInfo.name)) {
								iconUrl = fileInfo.url;
								title = fileInfo.name + " (" + Math.ceil(fileInfo.size / 1024) + "KB, " + new Date(fileInfo.lastModified).toLocaleString() + ")";
							} else {
								iconUrl = wit.base + "/resources/b2c/images/file_icon.gif";
								title = fileInfo.name + " (" + Math.ceil(fileInfo.size / 1024) + "KB, " + new Date(fileInfo.lastModified).toLocaleString() + ")";
							}
							browserListHtml += '<div class="browserItem"><img src="' + iconUrl + '" title="' + title + '" url="' + fileInfo.url + '" isDirectory="' + fileInfo.isDirectory + '" \/><div>' + fileInfo.name + '<\/div><\/div>';
						});
						$browserList.html(browserListHtml);
						
						$browserList.find("img").bind("click", function() {
							var $this = $(this);
							var isDirectory = $this.attr("isDirectory");
							if (isDirectory == "true") {
								var name = $this.next().text();
								browserList(path + name + "/");
							} else {
								var url = $this.attr("url");
								if (settings.input != null) {
									settings.input.val(url);
								} else {
									$browserButton.prev(":text").val(url);
								}
								if (settings.callback != null && typeof settings.callback == "function") {
									settings.callback(url);
								}
								$dialog.next(".dialogOverlay").andSelf().remove();
							}
						});
						
						if (path == "/") {
							$browserParentButton.unbind("click");
						} else {
							var parentPath = path.substr(0, path.replace(/\/$/, "").lastIndexOf("/") + 1);
							$browserParentButton.unbind("click").bind("click", function() {
								browserList(parentPath);
							});
						}
						$browserOrderType.unbind("change").bind("change", function() {
							browserList(path);
						});
					}
					
					$browserUploadInput.change(function() {
						var allowedUploadExtensions;
						if (settings.type == "flash") {
							allowedUploadExtensions = setting.uploadFlashExtension;
						} else if (settings.type == "media") {
							allowedUploadExtensions = setting.uploadMediaExtension;
						} else if (settings.type == "file") {
							allowedUploadExtensions = setting.uploadFileExtension;
						} else {
							allowedUploadExtensions = setting.uploadImageExtension;
						}
						if ($.trim(allowedUploadExtensions) == "" || !new RegExp("^\\S.*\\.(" + allowedUploadExtensions.replace(/,/g, "|") + ")$", "i").test($browserUploadInput.val())) {
							$.message("warn", message("b2c.upload.typeInvalid"));
							return false;
						}
						$browserLoadingIcon.show();
						$browserForm.submit();
					});
					
					$browserFrame.load(function() {
						var text;
						var io = document.getElementById(browserFrameId);
						if(io.contentWindow) {
							text = io.contentWindow.document.body ? io.contentWindow.document.body.innerHTML : null;
						} else if(io.contentDocument) {
							text = io.contentDocument.document.body ? io.contentDocument.document.body.innerHTML : null;
						}
						if ($.trim(text) != "") {
							$browserLoadingIcon.hide();
							var data = $.parseJSON(text);
							if (data.message.type == "success") {
								if (settings.input != null) {
									settings.input.val(data.url);
								} else {
									$browserButton.prev(":text").val(data.url);
								}
								if (settings.callback != null && typeof settings.callback == "function") {
									settings.callback(data.url);
								}
								cache = {};
								$dialog.next(".dialogOverlay").andSelf().remove();
							} else {
								$.message(data.message);
							}
						}
					});
					
				});
			});
		}
	});

	// 令牌
	$(document).ajaxSend(function(event, request, settings) {
		if (!settings.crossDomain && settings.type != null && settings.type.toLowerCase() == "post") {
			var token = getCookie("token");
			if (token != null) {
				request.setRequestHeader("token", token);
			}
		}
	});
	
	$(document).ajaxComplete(function(event, request, settings) {
		var loginStatus = request.getResponseHeader("loginStatus");
		var tokenStatus = request.getResponseHeader("tokenStatus");
		
		if (loginStatus == "accessDenied") {
			$.message("warn", "登录超时，请重新登录");
			setTimeout(function() {
				location.reload(true);
			}, 2000);
		} else if (loginStatus == "unauthorized") {
			$.message("warn", "对不起，您无此操作权限！");
		} else if (tokenStatus == "accessDenied") {
			var token = getCookie("token");
			if (token != null) {
				$.extend(settings, {
					global: false,
					headers: {token: token}
				});
				$.ajax(settings);
			}
		}
	});

})(jQuery);


		//tab切换
		function tabChange(id,index){
	 
			$("#"+id).find("."+"tabTitle").find('li').each(function(index){
				$(this).removeClass('liActive');
						});
						$("#"+id).find("."+"tabTitle").find('li').eq(index-1).addClass('liActive');
						
					$("#"+id).find("."+"tabCon").children().children().each(function(index){
				$(this).css({'display':'none'});
						});
						$("#"+id).find("."+"tabCon").children().children().eq(index-1).css({'display':'block'});
			
			 
			}
		//floor tab切换
		function floorTabChange(id,index){
			$("#"+id).find("."+"floorXinDtR").find('li').each(function(index){
				$(this).removeClass('activeF');
						});
						$("#"+id).find("."+"floorXinDtR").find('li').eq(index-1).addClass('activeF');
						
		$("#"+id).find("."+"floorXinDdR").each(function(index){
			$(this).css({'display':'none'});
				});
						$("#"+id).find("."+"floorXinDdR").eq(index-1).css({'display':'block'});
			
			}
		/*滚动*/
		function slideX3(direction,id){
			var obj1 = $("#"+id).parent();
			var allWidth=parseInt(0-document.getElementById(id).offsetWidth);
			var width2= obj1.parent().width()+1;
			var width = $("#"+id).find('li').eq(0).outerWidth();
			//在获取左边距之前结束动画，第一个false是情况运动队列，第二个true表示是运动立马结束到最终状态
			obj1.stop(false,true);
			var left = parseInt(obj1.css('left'));
			if(direction=='left'&&left<0){
				
			obj1.animate({
       	left:left+width
       	}, 500, function() {
    	});
    	}
			if(left>allWidth+width2&&direction=='right'){
						 
			obj1.animate({
      	left:left-width
       	}, 500, function() {
    	});
		} }

		//2014-9-22
		//选项卡
		function setTab(name, cursel, n) {
			for (i = 1; i <= n; i++) {
				var menu = document.getElementById(name + i);
				var con = document.getElementById("con_" + name + "_" + i);
				menu.className = i == cursel ? "curr" : "";
				con.style.display = i == cursel ? "block" : "none";
			}
		}

		function setTabmore(name, cursel, n) {
			for (i = 1; i <= n; i++) {
				var menu = document.getElementById(name + i);
				var con = document.getElementById("con_" + name + "_" + i);
				var more = document.getElementById("more_" + name + "_" + i);
				menu.className = i == cursel ? "curr" : "";
				con.style.display = i == cursel ? "block" : "none";
				more.style.display = i == cursel ? "block" : "none";
			}
		}
	

