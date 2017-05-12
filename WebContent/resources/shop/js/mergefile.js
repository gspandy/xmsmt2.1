function Speed() {
	this.PARA = [ "flag1=7807", "flag2=1", "flag3=1", "flag4=", "flag5=" ];
	this.rate = 0.2;
	this.isDisableRate = false;
	this.baseTimePoint = null;
	this.timePoint = [];
	this.fullTimePoint = [];
	this.isSended = false
}
Speed.prototype = {
	URL : "https://www.tenpay.com/cgi-bin/r.cgi",
	getStmp : function() {
		return (new Date()).getTime()
	},
	setDomainID : function(id) {
		var re = /^\d+$/;
		if (re.test(id)) {
			this.PARA[1] = "flag2=" + id;
			return true
		}
		return false
	},
	setPageID : function(id) {
		var re = /^\d+$/;
		if (re.test(id)) {
			this.PARA[2] = "flag3=" + id;
			return true
		}
		return false
	},
	setUserQQ : function(qq) {
		var re = /^[1-9][0-9]{4,10}$/;
		if (re.test(qq)) {
			this.PARA[3] = "flag4=" + qq;
			return true
		}
		return false
	},
	makeUrl : function() {
		var result;
		result = this.URL + "?" + (this.PARA.concat(this.timePoint)).join("&");
		return result
	},
	setBasePoint : function(stmp) {
		if (this.baseTimePoint === null) {
			this.baseTimePoint = stmp || this.getStmp()
		}
	},
	addPoint : function() {
		this.setBasePoint();
		var t = this.getStmp(), len = this.timePoint.length;
		this.timePoint.push((len + 1) + "=" + (t - this.baseTimePoint));
		this.fullTimePoint.push(t)
	},
	applyPoints : function(arr) {
		if (arr instanceof Array) {
			var result = [], btp = arr.shift();
			this.baseTimePoint = btp;
			for ( var i = 0, len = arr.length; i < len; i++) {
				result.push((i + 1) + "=" + (arr[i] - btp));
				this.fullTimePoint.push(arr[i])
			}
			this.timePoint = result;
			result = null;
			btp = null
		}
	},
	setRate : function(r) {
		this.rate = r
	},
	isSend : function() {
		var r = Math.random();
		return (this.isDisableRate || (this.rate > r))
	},
	send : function() {
		var img = new Image();
		if (this.isSend()) {
			img.src = this.makeUrl();
			this.isSended = true
		}
		img = null
	}
};
var Logger = (function() {
	var G_SEPARATOR = "/";
	var G_LOGGER = {};
	function _Log(action) {
		this.attrs = {
			action : action,
			op : "",
			time : "0",
			cost : 0,
			attach : "",
			retcode : "-1",
			retmsg : ""
		};
		this.timepoints = [];
		this.flowpoints = []
	}
	_Log.prototype = {
		checkAttr : function(name) {
			return (name in this.attrs)
		},
		setAttribute : function(name, value) {
			if (this.checkAttr(name)) {
				this.attrs[name] = value
			}
		},
		getAttribute : function(name) {
			if (this.checkAttr(name)) {
				return this.attrs[name]
			} else {
				return null
			}
		},
		setAttributes : function(attrs) {
			for ( var name in attrs) {
				if (attrs.hasOwnProperty(name)) {
					this.setAttribute(name, attrs[name])
				}
			}
		},
		getAttributes : function() {
			return this.attrs
		},
		formatAttributes : function() {
			var attrs = this.getAttributes();
			var a = [];
			for ( var name in attrs) {
				if (attrs.hasOwnProperty(name)) {
					a.push(name + "=" + attrs[name])
				}
			}
			return a.join("&")
		},
		fixedPoints : function() {
			this.attrs.time = this.formatTimePoint();
			this.attrs.op = this.formatFlowPoint()
		},
		resetPoints : function() {
			this.timepoints = [];
			this.flowpoints = []
		},
		setPoint : function(flow) {
			this.flowpoints.push(flow);
			this.timepoints.push(new Date().getTime())
		},
		formatFlowPoint : function() {
			return this.flowpoints.join(G_SEPARATOR)
		},
		formatTimePoint : function() {
			var p = [];
			for ( var i = 0, size = this.timepoints.length; i < size; i++) {
				p.push(this.timepoints[i] - this.timepoints[0])
			}
			if (p.length > 0) {
				this.setAttribute("cost", p[p.length - 1])
			}
			return p.join(G_SEPARATOR)
		},
		send : function(debug) {
			this.fixedPoints();
			var img = new Image();
			var url = "https://www.tenpay.com/app/v1.0/"
					+ (debug ? "test_" : "") + "jslog.cgi";
			img.src = url + "?" + this.formatAttributes()
		},
		bindError : function(retcode, ret) {
			bindError(this, retcode, ret, true)
		}
	};
	function bindError(logger, retcode, ret, useCache) {
		window.onerror = function(message, url, line) {
			if (true !== useCache) {
				logger.resetPoints()
			}
			logger.setPoint("window_onerror");
			logger.setAttribute("retcode", retcode);
			logger.setAttribute("attach", "l=" + line + ";m="
					+ encodeURIComponent(message.replace(/\s/g, "_")) + ";u="
					+ encodeURIComponent(url));
			logger.send();
			return (true === ret)
		}
	}
	(function main() {
		var meta = document.getElementById("logger_error");
		if (null != meta) {
			G_LOGGER = G_LOGGER || {};
			if (!G_LOGGER.global_on_error) {
				G_LOGGER.global_on_error = new _Log("global_on_error")
			}
			bindError(G_LOGGER.global_on_error, "90010001", true)
		}
	})();
	return {
		getLogger : function(action) {
			var logger = null;
			if (!action || typeof (action) != "string") {
				throw new Error(
						"Illegal parameter! Logger.getLogger(action)::action = "
								+ action)
			} else {
				G_LOGGER = G_LOGGER || {};
				if (!G_LOGGER[action]) {
					G_LOGGER[action] = new _Log(action)
				}
				logger = G_LOGGER[action];
				return {
					send : function(debug) {
						logger.send(debug)
					},
					setPoint : function(flow) {
						logger.setPoint(flow)
					},
					resetPoints : function() {
						logger.resetPoints()
					},
					setAttach : function(attach) {
						logger.setAttribute("attach", attach)
					},
					setRetcode : function(retcode) {
						logger.setAttribute("retcode", retcode)
					},
					setRetmsg : function(retmsg) {
						logger.setAttribute("retmsg", retmsg)
					},
					bindError : function(retcode, ret) {
						logger.bindError(retcode, ret)
					},
					destory : function() {
						G_LOGGER[logger.getAttribute("action")] = null
					}
				}
			}
		},
		bindError : bindError,
		destory : function() {
			G_LOGGER = null
		}
	}
})();
var g_qqCertLoadFunction = [];
function onQQCertLoadFinish() {
	if (TENPAYCTL.CheckIsSupport() == "0") {
		for ( var key = 0; key < g_qqCertLoadFunction.length; ++key) {
			if (g_qqCertLoadFunction[key]) {
				var tempFun = g_qqCertLoadFunction[key];
				g_qqCertLoadFunction[key] = null;
				tempFun()
			}
		}
	}
}
var TENPAYCTL = {
	DOMAIN : "tenpay.com"
};
TENPAYCTL.MinCodeBaseVersion = "1.2.0.6";
TENPAYCTL.OSControlVersion = {
	"windows nt" : {
		Min : "1206",
		Max : "2015"
	},
	"mac os x" : {
		Min : "2001",
		Max : "2003"
	},
	iphone : {
		Min : "1206",
		Max : "1206"
	},
	ipad : {
		Min : "1206",
		Max : "1206"
	},
	android : {
		Min : "1206",
		Max : "1206"
	}
};
TENPAYCTL.UnSupportVersion = [ "1211", "2011" ];
TENPAYCTL.GetBrowseEnv = function() {
	var OSName, OSVersion, ProcessType, BrowseName, BrowseVersion;
	var userAgent = navigator.userAgent.toLowerCase();
	var platform = navigator.platform.toLowerCase();
	var isWin = (platform == "win32") || (platform == "windows")
			|| (platform == "win64");
	var isMac = (platform == "mac68k") || (platform == "macppc")
			|| (platform == "macintosh") || (platform == "macintel");
	var isiPhone = (platform == "iphone") || (platform == "ipod");
	var isiPad = (platform == "ipad");
	var isAndroid = (String(platform).indexOf("linux arm") > -1);
	var isLinux = (String(platform).indexOf("linux") > -1) && (!isAndroid);
	if (isWin) {
		OSName = "windows nt"
	} else {
		if (isMac) {
			OSName = "mac os x"
		} else {
			if (isLinux) {
				OSName = "linux"
			} else {
				if (isiPhone) {
					OSName = "iphone"
				} else {
					if (isiPad) {
						OSName = "ipad"
					} else {
						if (isAndroid) {
							OSName = "android"
						} else {
							OSName = "unknown"
						}
					}
				}
			}
		}
	}
	var env = null;
	if ((env = userAgent.match(/(windows nt) ([\d.]+[\d]+)/))
			|| (env = userAgent.match(/(mac os x) ([\d_.]+\d+)/))
			|| (env = userAgent.match(/(iphone) os ([\d_]+\d+)/))
			|| (env = userAgent.match(/(ipad);[\s\S]* cpu os ([\d_]+\d+)/))
			|| (env = userAgent.match(/(android) ([\d.]+[\d]+)/))) {
		OSVersion = env[2]
	} else {
		OSVersion = "unknown"
	}
	ProcessType = "x86";
	if (env = userAgent.match(/wow64;/)) {
		ProcessType = "wow64"
	} else {
		if (env = userAgent.match(/win64; x64;/)) {
			ProcessType = "x64"
		}
	}
	if ((env = userAgent.match(/(msie) ([\d.]+)/))
			|| (env = userAgent.match(/(qqbrowser)\/([\d.]+)/))
			|| (env = userAgent.match(/ (maxthon)\/([\d.]+)/))
			|| (env = userAgent.match(/ (se) ([\d.]+)/))
			|| (env = userAgent.match(/(firefox)\/([\d.]+)/))
			|| (env = userAgent.match(/(chrome)\/([\d.]+)/))
			|| (env = userAgent.match(/(opera).([\d.]+)/)) || 0) {
		BrowseName = env[1];
		BrowseVersion = env[2];
		if (BrowseName == "qqbrowser" && OSName == null) {
			OSName = "windows nt";
			OSVersion = "6.1"
		}
	} else {
		if ((env = userAgent.match(/version\/([\d.]+).*(safari)/))) {
			BrowseName = env[2];
			BrowseVersion = env[1]
		} else {
			BrowseName = "unknown";
			BrowseVersion = 0
		}
	}
	var allData;
	allData = {
		OSName : OSName,
		OSVersion : OSVersion,
		ProcessType : ProcessType,
		BrowseName : BrowseName,
		BrowseVersion : BrowseVersion
	};
	return allData
};
TENPAYCTL.IsBrowseNeedLoadComplete = function() {
	var browseEnv = TENPAYCTL.GetBrowseEnv();
	if (browseEnv.OSName == "iphone" || browseEnv.OSName == "ipad") {
		if (typeof (g_tenpaycertIOSobject) == "undefined") {
			return true
		}
	}
	return false
};
TENPAYCTL.IsIOSBrowse = function() {
	if (typeof YUI != "undefined" && typeof YUI.Env != "undefined"
			&& YUI.Env.noCtrlMode == true) {
		return false
	}
	var browseEnv = TENPAYCTL.GetBrowseEnv();
	if (TENPAYCTL.CheckIsSupport() == "0"
			&& (browseEnv.OSName == "iphone" || browseEnv.OSName == "ipad")
			&& browseEnv.BrowseName == "qqbrowser") {
		return true
	}
	return false
};
TENPAYCTL.runQQCertFunction = function(vFun) {
	if (TENPAYCTL.IsIOSBrowse()) {
		if (typeof (parent.TENPAYCTL.runQQCertFunction) != "undefined"
				&& this.runQQCertFunction != parent.TENPAYCTL.runQQCertFunction) {
			parent.TENPAYCTL.runQQCertFunction(vFun);
			return
		}
		g_qqCertLoadFunction.push(vFun);
		if (typeof (g_tenpaycertIOSobject) != "undefined") {
			var url = "https://BAEA0695-03A4-43BB-8495-C7025E1A8F42-RunTask";
			location.href = url
		}
	} else {
		vFun()
	}
};
TENPAYCTL.CheckEnv = function(current_env) {
	var support_env = [ {
		OSName : "windows nt",
		ProcessType : "x86",
		BrowseName : "msie",
		BMinVersion : "6.0"
	}, {
		OSName : "windows nt",
		ProcessType : "wow64",
		BrowseName : "msie",
		BMinVersion : "6.0"
	}, {
		OSName : "windows nt",
		ProcessType : "x64",
		BrowseName : "msie",
		BMinVersion : "6.0"
	}, {
		OSName : "windows nt",
		BrowseName : "maxthon"
	}, {
		OSName : "windows nt",
		BrowseName : "se"
	}, {
		OSName : "windows nt",
		BrowseName : "qqbrowser"
	}, {
		OSName : "windows nt",
		BrowseName : "firefox",
		BMinVersion : "3.0"
	}, {
		OSName : "windows nt",
		BrowseName : "chrome",
		BMinVersion : "4.0"
	}, {
		OSName : "windows nt",
		BrowseName : "safari",
		BMinVersion : "5.0"
	}, {
		OSName : "mac os x",
		BrowseName : "safari"
	}, {
		OSName : "mac os x",
		BrowseName : "chrome"
	}, {
		OSName : "mac os x",
		BrowseName : "firefox"
	}, {
		OSName : "mac os x",
		BrowseName : "qqbrowser"
	}, {
		OSName : "iphone",
		BrowseName : "qqbrowser",
		BMinVersion : "43"
	}, {
		OSName : "iphone"
	}, {
		OSName : "ipad"
	}, {
		OSName : "android"
	} ];
	if ("iphone" == current_env.OSName) {
		current_env.BrowseVersion = current_env.BrowseVersion.substring(0, 2)
	}
	for ( var i = 0; i < support_env.length; i++) {
		if (support_env[i].OSName) {
			if (support_env[i].OSName != current_env.OSName) {
				continue
			}
		}
		if (support_env[i].OSVersion) {
			if (support_env[i].OSVersion != current_env.OSVersion) {
				continue
			}
		}
		if (support_env[i].ProcessType) {
			if (support_env[i].ProcessType != current_env.ProcessType) {
				continue
			}
		}
		if (support_env[i].BrowseName) {
			if (support_env[i].BrowseName != current_env.BrowseName) {
				continue
			}
		}
		if (support_env[i].BMinVersion) {
			if (parseFloat(support_env[i].BMinVersion) > parseFloat(current_env.BrowseVersion)) {
				return 10102
			}
		}
		if (support_env[i].BMaxVersion) {
			if (parseFloat(support_env[i].BMaxVersion) <= parseFloat(current_env.BrowseVersion)) {
				return 10103
			}
		}
		if (support_env[i].BMaxOSVersion) {
			if (parseFloat(support_env[i].BMaxOSVersion) <= parseFloat(current_env.OSVersion)) {
				return 10104
			}
		}
		return 0
	}
	if ((navigator.appName == "Netscape") || (navigator.appName == "Opera")
			|| (navigator.appName == "Microsoft Internet Explorer")) {
		return 0
	}
	return 10101
};
TENPAYCTL.CheckIsSupport = function() {
	var current_env = TENPAYCTL.GetBrowseEnv();
	var ret = TENPAYCTL.CheckEnv(current_env);
	return ret
};
TENPAYCTL.isEnvSupportCtrl = function() {
	var current_env = TENPAYCTL.GetBrowseEnv();
	var isSupport = true;
	if (current_env.OSName == "windows nt"
			&& current_env.BrowseName == "unknown") {
		isSupport = false
	}
	if ((navigator.appName == "Netscape" || navigator.appName == "Opera")
			&& (current_env.OSName == "iphone" || current_env.OSName == "ipad")) {
		isSupport = false
	}
	if (current_env.OSName == "android") {
		isSupport = false
	}
	if (navigator.userAgent.indexOf("AdobeAIR") != -1) {
		isSupport = false
	}
	return isSupport
};
TENPAYCTL.AlertError = function(num) {
	switch (num) {
	case 1:
		try {
			console.log("未创建控件对象，请页面检查逻辑")
		} catch (ex) {
		}
		break;
	case 2:
		try {
			console.log("没有找到控件对象")
		} catch (ex) {
		}
		break;
	default:
		try {
			console.log("Error")
		} catch (ex) {
		}
	}
	return null
};
TENPAYCTL.TenpayctrlJSON = {};
TENPAYCTL.getTenpayctrlById = function(strID) {
	if (TENPAYCTL.TenpayctrlJSON[strID]) {
		return TENPAYCTL.TenpayctrlJSON[strID]
	} else {
		return null
	}
};
TENPAYCTL.judgeTenpayctrlById = function(strID) {
	if (TENPAYCTL.TenpayctrlJSON[strID]) {
		return true
	} else {
		return false
	}
};
TENPAYCTL.AndroidQQEditCtrl = function() {
	this.Version = 0;
	this.Mode = 0;
	this.id;
	this.ctrl;
	this.SetPassWordId = function(pwid) {
		this.id = pwid;
		this.ctrl = window.QQPassword;
		this.Version = window.QQPassword.getVersion();
		this.Mode = window.QQPassword.getMode();
		this.ctrl.SelectRsaKey(0)
	};
	this.SetSalt = function(seed) {
		return this.ctrl.SetSalt(seed)
	};
	this.SelectRsaKey = function(num) {
		return this.ctrl.SelectRsaKey(num)
	};
	this.GetSha1Value = function() {
		this.ctrl.SetPassword(document.getElementById(this.id).value);
		return this.ctrl.GetSha1Value()
	};
	this.GetSha1Value2 = function() {
		this.ctrl.SetPassword(document.getElementById(this.id).value);
		return this.ctrl.GetSha1Value2()
	};
	this.GetSha1Value3 = function() {
		this.ctrl.SetPassword(document.getElementById(this.id).value);
		return this.ctrl.GetSha1Value3()
	};
	this.GetRsaPassword = function() {
		this.ctrl.SetPassword(document.getElementById(this.id).value);
		return this.ctrl.GetRsaPassword()
	};
	this.GetRsaPassword2 = function() {
		this.ctrl.SetPassword(document.getElementById(this.id).value);
		return this.ctrl.GetRsaPassword2()
	};
	this.GetRsaPassword3 = function() {
		this.ctrl.SetPassword(document.getElementById(this.id).value);
		return this.ctrl.GetRsaPassword3()
	};
	this.GetRemitRSAPassword = function() {
		this.ctrl.SetPassword(document.getElementById(this.id).value);
		return this.ctrl.GetRemitRSAPassword()
	};
	this.GetInputInfo = function() {
		this.ctrl.SetPassword(document.getElementById(this.id).value);
		return this.ctrl.GetInputInfo()
	}
};
TENPAYCTL.AndroidQQCertCtrl = function() {
	this.ctrl = window.QQCertificate;
	this.Version = window.QQCertificate.getVersion();
	this.HostName = window.QQCertificate.getHostName();
	this.Base64Encode = function(strsrc) {
		return this.ctrl.Base64Encode(strsrc)
	};
	this.Base64Decode = function(strsrc) {
		return this.ctrl.Base64Decode(strsrc)
	};
	this.SetChallenge = function(unstructuredname, challengepassword) {
		return this.ctrl.SetChallenge(unstructuredname, challengepassword)
	};
	this.SetSubject = function(subject) {
		return this.ctrl.SetSubject(subject)
	};
	this.GetCSR = function() {
		return this.ctrl.GetCSR()
	};
	this.ImportCert = function(cert) {
		return this.ctrl.ImportCert(cert)
	};
	this.CertSign = function(certid, message) {
		return this.ctrl.CertSign(certid, message)
	};
	this.FindCert = function(keyname, keyvalue) {
		return this.ctrl.FindCert(keyname, keyvalue)
	};
	this.IsCertExist = function(certid) {
		return this.ctrl.IsCertExist(certid)
	};
	this.DelCert = function(certid) {
		return this.ctrl.DelCert(certid)
	}
};
TENPAYCTL.QQEditCtrl = function() {
	this.ctrl;
	this.id;
	this.objPara;
	this.loadFinish = false;
	this.OSName;
	this.noCtrlMode = false;
	var m_id;
	this.setId = function(ctrlId) {
		m_id = ctrlId
	};
	this.Version = {
		valueOf : function() {
			return this.toString()
		},
		toString : function() {
			var ctrlLatest = document.getElementById(m_id);
			if (ctrlLatest) {
				return ctrlLatest.Version
			}
			return null
		}
	};
	this.Mode = {
		valueOf : function() {
			return this.toString()
		},
		toString : function() {
			var ctrlLatest = document.getElementById(m_id);
			if (ctrlLatest) {
				return ctrlLatest.Mode
			}
			return null
		}
	}
};
TENPAYCTL.QQEditCtrl.prototype.create = function(para) {
	var obj;
	var ret = 0;
	var IsInstall;
	var strPara;
	var Version;
	var strStyle;
	strPara = " ";
	strStyle = " ";
	if (para.ctrlId) {
		strPara += 'id="' + para.ctrlId + '" ';
		strStyle += 'id="' + para.ctrlId + '" ';
		this.id = para.ctrlId;
		this.setId(para.ctrlId);
		TENPAYCTL.TenpayctrlJSON[para.ctrlId] = this
	} else {
		ret = 20101;
		return ret
	}
	if (!para.parentNode) {
		ret = 20101;
		return ret
	}
	if (para.w) {
		strPara += 'width="' + para.w + '" ';
		if (para.h) {
			strStyle += ' style= "width:' + para.w + "px;height:" + para.h
					+ 'px;" '
		}
	}
	if (para.h) {
		strPara += 'height="' + para.h + '" '
	}
	if (para.tabIndex) {
		strPara += 'tabindex="' + para.tabIndex + '" ';
		strStyle += 'tabindex="' + para.tabIndex + '" '
	}
	this.objPara = para;
	var objUnSetup = '<a id="unSetup_'
			+ para.ctrlId
			+ '" href="javascript:void(0); " onclick="TFL.PC.setup(); " style="display: inline-block !important;color:red !important; font-size:12px !important; border:1px solid red !important;width:'
			+ (para.w - 2)
			+ "px !important;line-height:"
			+ (para.h - 2)
			+ 'px !important;text-align:center !important;background-color:#fff !important;z-index:1;">请点此输入密码</a>';
	if ("undefined" != typeof para.unSetupContent) {
		objUnSetup = para.unSetupContent
	}
	ret = TENPAYCTL.CheckIsSupport();
	if (ret) {
		return ret
	}
	var current_env = TENPAYCTL.GetBrowseEnv();
	this.OSName = current_env.OSName;
	if (!TENPAYCTL.isEnvSupportCtrl() && para.useNoCtrlMode === true) {
		this.noCtrlMode = true
	}
	if (this.noCtrlMode) {
		obj = '<input type="password" id="'
				+ para.ctrlId
				+ '" style="width:100%;height:100%;line-height:100%;border:none;font-size:20px;" />'
	} else {
		if (navigator.appName == "Microsoft Internet Explorer") {
			if (!(para.codebase && para.codebase == 0)) {
				var filename = "";
				if (current_env.OSVersion < 6) {
					filename = "tenpaycert_xp.cab"
				} else {
					if (current_env.ProcessType == "x64") {
						filename = "tenpaycert64.cab"
					} else {
						filename = "tenpaycert.cab"
					}
				}
				strPara += ' codebase="https://www.tenpay.com/download/'
						+ filename + "#Version=" + TENPAYCTL.MinCodeBaseVersion
						+ '" '
			}
			obj = '<object classid="clsid:E787FD25-8D7C-4693-AE67-9406BC6E22DF"'
					+ strPara + ">" + objUnSetup + "</object>"
		} else {
			if ((navigator.appName == "Netscape")
					|| (navigator.appName == "Opera")) {
				if (current_env.OSName == "iphone"
						|| current_env.OSName == "ipad") {
					if (typeof (parent.g_tenpayeditIOSobject) != "undefined") {
						g_tenpayeditIOSobject = parent.g_tenpayeditIOSobject
					}
					if (typeof (g_tenpayeditIOSobject) == "undefined"
							&& current_env.BrowseName != "safari") {
						return 20107
					}
					var iOSAppDownUrl;
					var iOSSchema;
					if (current_env.OSName == "ipad") {
						iOSAppDownUrl = "http://itunes.apple.com/cn/app/id426097375";
						iOSSchema = "mttbrowserhd://url=www.tenpay.com/index.shtml"
					}
					if (current_env.OSName == "iphone") {
						iOSAppDownUrl = "http://itunes.apple.com/cn/app/id370139302";
						iOSSchema = "mttbrowser://url=www.tenpay.com/index.shtml"
					}
					if (typeof (g_tenpayeditIOSobject) == "undefined") {
						if (confirm("提醒：使用iOS浏览器访问财付通必须启动QQ浏览器的安全支持。\n 如果没安装，请点击取消后再在页面上点击安装QQ浏览器按钮。\n如果您已经安装过QQ浏览器，可以直接启动QQ浏览器，现在启动吗？")) {
							setTimeout(function() {
								document.location = iOSSchema
							}, 500)
						}
						obj = '<a id="unSetup_'
								+ para.ctrlID
								+ '" href="'
								+ iOSAppDownUrl
								+ '" style="color:red !important; font-size:12px !important; border:1px solid red !important;width:'
								+ (para.w - 2)
								+ "px !important;line-height:"
								+ (para.h - 2)
								+ 'px !important;text-align:center !important;background-color:#fff !important;position:absolute;z-index:1;">点此安装QQ浏览器</a>';
						if ("undefined" != typeof para.unSetupContent) {
							obj = para.unSetupContent
						}
						return 20108
					} else {
						obj = '<input type="password" ' + strStyle + " />"
					}
				} else {
					if (current_env.OSName == "android") {
						if (typeof (window.QQPassword) == "undefined") {
							obj = ""
						} else {
							obj = '<input type="password" ' + strStyle + " />"
						}
					} else {
						obj = '<object type="application/qqedit"' + strPara
								+ ">" + objUnSetup + "</object>"
					}
				}
			} else {
				ret = 20102;
				return ret
			}
		}
	}
	if (para.additionContent) {
		obj += para.additionContent
	}
	if (para.submitName) {
		obj += ' <input type="hidden" id="' + para.submitName + '" name="'
				+ para.submitName + '" /> '
	}
	document.getElementById(para.parentNode).innerHTML = obj;
	if (this.noCtrlMode) {
		var noCtrlObj = new function() {
			this.Version = 1111;
			this.Mode = 1;
			this.SetEditFocus = function() {
				this.focus()
			};
			this.GetInputInfo = function() {
				return this.value.length << 16
			};
			this.isCreatedCtrl = function() {
				return true
			};
			this.ClearText = function() {
				this.value = ""
			};
			this.SetSalt = function(seed) {
				this.Salt = seed
			}
		};
		var objQQEdit = document.getElementById(para.ctrlId);
		if (objQQEdit) {
			for ( var o in noCtrlObj) {
				if (noCtrlObj.hasOwnProperty(o)) {
					objQQEdit[o] = noCtrlObj[o]
				}
			}
			objQQEdit.style.fontSize = "12px";
			this.ctrl = objQQEdit
		}
	} else {
		if (current_env.OSName == "iphone" || current_env.OSName == "ipad") {
			if (typeof (QQPassword) == "undefined"
					&& typeof (parent.QQPassword) != "undefined") {
				var script = document.createElement("script");
				script.type = "text/javascript";
				script.text = "function SetSalt(seed){       this.Salt=seed;} function SelectRsaKey(index){ this.RSAKey=index;} function GetSha1Value(){ this.SetPassWord(1); return( parent.g_tenpayeditIOSobject.Sha1Value);} function GetSha1Value2(){ this.SetPassWord(2); return(parent.g_tenpayeditIOSobject.Sha1Value);} function GetSha1Value3(){ this.SetPassWord(3); return(parent.g_tenpayeditIOSobject.Sha1Value);} function GetRsaPassword(){ this.SetPassWord(1); return(parent.g_tenpayeditIOSobject.RsaPassword);} function GetRsaPassword2(){ this.SetPassWord(2); return(parent.g_tenpayeditIOSobject.RsaPassword);} function GetRsaPassword3(){ this.SetPassWord(3); return(parent.g_tenpayeditIOSobject.RsaPassword);} function GetRemitRSAPassword(){ this.SetPassWord(4); return(parent.g_tenpayeditIOSobject.RsaPassword);} function GetInputInfo(){this.SetPassWord(1); return(parent.g_tenpayeditIOSobject.InputInfo);} function SetPassWordId(pswId) { this.pswId = pswId;} function SetPassWord(num){ var pw = document.getElementById(this.pswId).value; url='http://E787FD25-8D7C-4693-AE67-9406BC6E22DF?RSAKey='+this.RSAKey+'&Salt='+this.Salt+'&pw='+encodeURIComponent(pw)+'&num='+num;    g_tenpayeditIOSobject.RsaPassword=''; window.open(url); while(parent.g_tenpayeditIOSobject.RsaPassword.length==0){}; }function QQPassword() {       this.Salt='313233343536'; this.RSAKey=0; this.Sha1Value='';this.RsaPassword='';this.InputInfo=0; this.Version=parent.g_tenpayeditIOSobject.Version; this.Mode=1; this.pswId='';this.SetSalt=SetSalt; this.SelectRsaKey=SelectRsaKey; this.GetSha1Value=GetSha1Value; this.GetSha1Value2=GetSha1Value2; this.GetSha1Value3=GetSha1Value3; this.GetRsaPassword=GetRsaPassword; this.GetRsaPassword2=GetRsaPassword2; this.GetRsaPassword3=GetRsaPassword3; this.GetRemitRSAPassword=GetRemitRSAPassword; this.GetInputInfo=GetInputInfo;  this.SetPassWord=SetPassWord; this.SetPassWordId=SetPassWordId; }; ";
				document.getElementsByTagName("head")[0].appendChild(script);
				this.ctrl = new QQPassword()
			} else {
				this.ctrl = new QQPassword()
			}
			this.Mode = this.ctrl.Mode;
			this.Version = this.ctrl.Version;
			this.ctrl.SetPassWordId(para.ctrlId);
			if (this.Version < TENPAYCTL.OSControlVersion[current_env.OSName].Min) {
				ret = 20104
			} else {
				if (this.Version < TENPAYCTL.OSControlVersion[current_env.OSName].Max) {
					ret = 20106
				}
			}
			if (para.version) {
				if (this.Version < para.version) {
					ret = 20105
				}
			}
			var objQQEdit = document.getElementById(para.ctrlId);
			if (objQQEdit) {
				for ( var o in this.ctrl) {
					if (this.ctrl.hasOwnProperty(o)) {
						objQQEdit[o] = this.ctrl[o]
					}
				}
			}
		} else {
			if (current_env.OSName == "android") {
				if (typeof window.QQPassword == "undefined") {
					return 20103
				}
				this.ctrl = new TENPAYCTL.AndroidQQEditCtrl();
				this.ctrl.SetPassWordId(this.id);
				this.Mode = this.ctrl.Mode;
				this.Version = this.ctrl.Version;
				var objQQEdit = document.getElementById(para.ctrlId);
				if (objQQEdit) {
					for ( var o in this.ctrl) {
						if (this.ctrl.hasOwnProperty(o)) {
							objQQEdit[o] = this.ctrl[o]
						}
					}
				}
			} else {
				this.ctrl = document.getElementById(this.id);
				this.Mode = this.ctrl.Mode;
				if (this.Mode != 1) {
					ret = 20103;
					this.ctrl = null;
					return ret
				}
				this.Version = this.ctrl.Version;
				if (this.Version < TENPAYCTL.OSControlVersion[current_env.OSName].Min) {
					ret = 20104
				} else {
					if (this.Version < TENPAYCTL.OSControlVersion[current_env.OSName].Max) {
						ret = 20106
					}
				}
				if (para.version) {
					if (this.Version < para.version) {
						ret = 20105
					}
				}
				if (new RegExp(TENPAYCTL.UnSupportVersion.join("|"))
						.test(this.Version)
						&& current_env.OSName == "windows nt"
						&& current_env.OSVersion >= 6) {
					ret = 20104
				}
				this.bindEvent()
			}
		}
	}
	return ret
};
TENPAYCTL.QQEditCtrl.prototype.checkValid = function() {
	if (this.OSName == "windows nt") {
		if (this.ctrl == null) {
			var ctrlLatest = document.getElementById(this.id);
			if (typeof (ctrlLatest.Version) != "undefined") {
				this.ctrl = ctrlLatest
			}
		}
	}
};
TENPAYCTL.QQEditCtrl.prototype.bindEvent = function() {
	if (this.ctrl == null) {
		return
	}
	var para = this.objPara;
	if (para.focus_callback) {
		this.ctrl.OnEditFocus = para.focus_callback
	}
	if (para.blur_callback) {
		this.ctrl.OnEditBlur = para.blur_callback
	}
	if (para.enter_callback) {
		this.ctrl.OnEditEnterKeyUp = para.enter_callback
	}
	if (para.ctrlEvn) {
		this.ctrl.OnEditEnterKeyUp = para.ctrlEvn
	}
};
TENPAYCTL.QQEditCtrl.prototype.getExeDownloadPath = function() {
	var current_env = TENPAYCTL.GetBrowseEnv();
	if (current_env.OSName == "windows nt") {
		if (current_env.OSVersion == "unknown") {
			current_env.OSVersion = 6
		}
		if (parseFloat(current_env.OSVersion) >= 6) {
			return "https://www.tenpay.com/download/tenpaycert.exe"
		} else {
			return "https://www.tenpay.com/download/tenpaycert_xp.exe"
		}
	} else {
		if (current_env.OSName == "mac os x") {
			return "https://www.tenpay.com/download/tenpaycert.dmg"
		} else {
			if (current_env.OSName == "iphone") {
				return "http://itunes.apple.com/cn/app/id370139302"
			} else {
				return "https://www.tenpay.com"
			}
		}
	}
};
TENPAYCTL.QQEditCtrl.prototype.startUpdate = function(url) {
	if (this.OSName == "android" || this.OSName == "iphone"
			|| this.OSName == "ipad") {
		return -99
	} else {
		this.checkValid();
		if (this.ctrl) {
			if (this.Version < 1212) {
				return -3
			} else {
				return this.ctrl.StartUpdate(url)
			}
		} else {
			return TENPAYCTL.AlertError(1)
		}
	}
};
TENPAYCTL.QQEditCtrl.prototype.showBorder = function(isShow) {
	if (this.OSName == "android" || this.OSName == "iphone"
			|| this.OSName == "ipad") {
		return -99
	} else {
		this.checkValid();
		if (this.ctrl) {
			this.ctrl.ShowBorder = isShow;
			return 1
		} else {
			return TENPAYCTL.AlertError(1)
		}
	}
};
TENPAYCTL.QQEditCtrl.prototype.setSalt = function(seed) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.SetSalt(seed)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.selectRsaKey = function(num) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.SelectRsaKey(num)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.getSha1Value = function() {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.GetSha1Value()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.getSha1Value2 = function() {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.GetSha1Value2()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.getSha1Value3 = function() {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.GetSha1Value3()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.getRsaPassword = function() {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.GetRsaPassword()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.getRsaPassword2 = function() {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.GetRsaPassword2()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.getRsaPassword3 = function() {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.GetRsaPassword3()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.getRemitRSAPassword = function() {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.GetRemitRSAPassword()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.getInputInfo = function() {
	this.checkValid();
	if (this.ctrl && "undefined" != typeof (this.ctrl.GetInputInfo)) {
		return this.ctrl.GetInputInfo()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQEditCtrl.prototype.setEditFocus = function() {
	if (this.OSName == "android" || this.OSName == "iphone"
			|| this.OSName == "ipad") {
		document.getElementById(this.id).focus()
	} else {
		this.checkValid();
		if (this.ctrl && "undefined" != typeof (this.ctrl.SetEditFocus)) {
			return this.ctrl.SetEditFocus()
		} else {
			return TENPAYCTL.AlertError(1)
		}
	}
};
TENPAYCTL.QQEditCtrl.prototype.clearText = function() {
	if (this.OSName == "android" || this.OSName == "iphone"
			|| this.OSName == "ipad") {
		document.getElementById(this.id).value = ""
	} else {
		this.checkValid();
		if (this.ctrl) {
			return this.ctrl.ClearText()
		} else {
			return TENPAYCTL.AlertError(1)
		}
	}
};
TENPAYCTL.QQEditCtrl.prototype.isCreatedCtrl = function() {
	this.checkValid();
	return !(undefined == this.ctrl || null == this.ctrl)
};
TENPAYCTL.QQCertCtrl = function() {
	this.ctrl;
	this.OSName;
	this.Version = {
		valueOf : function() {
			return this.toString()
		},
		toString : function() {
			var ctrlLatest = document.getElementById("js_tenpay_cert");
			if (ctrlLatest) {
				return ctrlLatest.Version
			}
			return null
		}
	};
	this.HostName = {
		valueOf : function() {
			return this.toString()
		},
		toString : function() {
			var ctrlLatest = document.getElementById("js_tenpay_cert");
			if (ctrlLatest) {
				return ctrlLatest.HostName
			}
			return null
		}
	}
};
TENPAYCTL.QQCertCtrl.prototype.create = function(para) {
	var obj;
	var ret;
	var strPara;
	var Version;
	ret = TENPAYCTL.CheckIsSupport();
	if (ret) {
		return ret
	}
	if (document.getElementById("js_tenpay_cert")) {
		this.ctrl = document.getElementById("js_tenpay_cert");
		this.Version = this.ctrl.Version;
		ret = 30102;
		return ret
	}
	TENPAYCTL.TenpayctrlJSON.js_tenpay_cert = this;
	var current_env = TENPAYCTL.GetBrowseEnv();
	this.OSName = current_env.OSName;
	if (navigator.appName == "Microsoft Internet Explorer") {
		if (!(para.codebase && para.codebase == 0)) {
			var filename = "";
			if (current_env.OSVersion < 6) {
				filename = "tenpaycert_xp.cab"
			} else {
				if (current_env.ProcessType == "x64") {
					filename = "tenpaycert64.cab"
				} else {
					filename = "tenpaycert.cab"
				}
			}
			strPara = ' codebase="https://www.tenpay.com/download/' + filename
					+ "#Version=" + TENPAYCTL.MinCodeBaseVersion + '" '
		}
		obj = '<object id="js_tenpay_cert" classid="clsid:BAEA0695-03A4-43BB-8495-C7025E1A8F42"'
				+ strPara + ' width="0" height="0"></object>'
	} else {
		if ((navigator.appName == "Netscape") || (navigator.appName == "Opera")) {
			if (current_env.OSName == "iphone" || current_env.OSName == "ipad") {
				if (typeof (parent.g_tenpaycertIOSobject) != "undefined") {
					g_tenpaycertIOSobject = parent.g_tenpaycertIOSobject
				}
				if (typeof (g_tenpaycertIOSobject) == "undefined") {
					return 20107
				}
				obj = '<object id="js_tenpay_cert" width="0" height="0"></object>'
			} else {
				obj = '<object id="js_tenpay_cert" type="application/qqcert" width="0" height="0"></object>'
			}
		} else {
			if (current_env.OSName == "android") {
				if (typeof (window.QQCertificate) == "undefined") {
					obj = ""
				} else {
					obj = '<object id="js_tenpay_cert" width="0" height="0"></object>'
				}
			} else {
				ret = 30103;
				return ret
			}
		}
	}
	if (para.div_id) {
		document.getElementById(para.div_id).innerHTML = obj
	} else {
		if (current_env.OSName == "iphone" || current_env.OSName == "ipad") {
			var newDiv = document.createElement("div");
			newDiv.innerHTML = obj;
			newDiv.id = "parentCert";
			newDiv.className = "newDiv";
			document.body.appendChild(newDiv)
		} else {
			document.write(obj)
		}
	}
	if (current_env.OSName == "iphone" || current_env.OSName == "ipad") {
		this.ctrl = g_tenpaycertIOSobject;
		this.Version = this.ctrl.Version;
		this.HostName = this.ctrl.HostName
	} else {
		if (current_env.OSName == "android") {
			if (typeof window.QQCertificate == "undefined") {
				this.ctrl = null;
				return 30104
			}
			this.ctrl = new TENPAYCTL.AndroidQQCertCtrl();
			this.Version = this.ctrl.Version;
			this.HostName = this.ctrl.HostName;
			var objQQCert = document.getElementById("js_tenpay_cert");
			if (objQQCert) {
				for ( var o in this.ctrl) {
					if (this.ctrl.hasOwnProperty(o)) {
						objQQCert[o] = this.ctrl[o]
					}
				}
			}
		} else {
			Version = document.getElementById("js_tenpay_cert").Version;
			if (!Version) {
				ret = 30104;
				this.ctrl = null;
				return ret
			}
			if (Version < TENPAYCTL.OSControlVersion[current_env.OSName].Min) {
				ret = 30105
			} else {
				if (Version < TENPAYCTL.OSControlVersion[current_env.OSName].Max) {
					ret = 30107
				}
			}
			if (para.version) {
				if (Version < para.version) {
					ret = 30106
				}
			}
			this.ctrl = document.getElementById("js_tenpay_cert");
			this.Version = Version;
			this.HostName = this.ctrl.HostName
		}
	}
	return ret
};
TENPAYCTL.QQCertCtrl.prototype.checkValid = function() {
	if (this.OSName == "windows nt") {
		if (this.ctrl == null) {
			var ctrlLatest = document.getElementById("js_tenpay_cert");
			if (typeof (ctrlLatest.Version) != "undefined") {
				this.ctrl = ctrlLatest
			}
		}
	}
};
TENPAYCTL.QQCertCtrl.prototype.bindObjMethod = function() {
	var objQQCert = document.getElementById("js_tenpay_cert");
	if (objQQCert != null) {
		for ( var o in g_tenpaycertIOSobject) {
			if (g_tenpaycertIOSobject.hasOwnProperty(o)) {
				objQQCert[o] = g_tenpaycertIOSobject[o]
			}
		}
	}
};
TENPAYCTL.QQCertCtrl.prototype.findCert = function(keyname, keyvalue) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.FindCert(keyname, keyvalue)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.isCertExist = function(certid) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.IsCertExist(certid)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.getCertInfo = function(certid, type) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.GetCertInfo != "undefined") {
			var ret = this.ctrl.GetCertInfo(certid, type);
			var retArray = ret.split("|");
			return retArray
		} else {
			return null
		}
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.delCert = function(certid) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.DelCert(certid)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.setChallenge = function(unstructuredname,
		challengepassword) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.SetChallenge(unstructuredname, challengepassword)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.setSubject = function(subject) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.SetSubject(subject)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.getCSR = function() {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.GetCSR()
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.importCert = function(cert) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.ImportCert(cert)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.base64Encode = function(strsrc) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.Base64Encode(strsrc)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.base64Decode = function(strsrc) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.Base64Decode(strsrc)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.certSign = function(certid, message) {
	this.checkValid();
	if (this.ctrl) {
		return this.ctrl.CertSign(certid, message)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKGetSN = function(type) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKGetSN == "undefined") {
			return -99
		}
		return this.ctrl.UKGetSN(type)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKGetCSR = function(type, keysn) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKGetCSR == "undefined") {
			return -99
		}
		return this.ctrl.UKGetCSR(type, keysn)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKImportCert = function(type, keysn, password,
		cert) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKImportCert == "undefined") {
			return -99
		}
		return this.ctrl.UKImportCert(type, keysn, password, cert)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKCertSign = function(type, keysn, certsn,
		message) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKCertSign == "undefined") {
			return -99
		}
		return this.ctrl.UKCertSign(type, keysn, certsn, message)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKExportCert = function(type, keysn) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKExportCert == "undefined") {
			return -99
		}
		return this.ctrl.UKExportCert(type, keysn)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKGetSN_ND = function(type) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKGetSN_ND == "undefined") {
			return -99
		}
		return this.ctrl.UKGetSN_ND(type)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKGetCSR_ND = function(type, keysn, password) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKGetCSR_ND == "undefined") {
			return -99
		}
		return this.ctrl.UKGetCSR_ND(type, keysn, password)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKImportCert_ND = function(type, keysn,
		password, cert) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKImportCert_ND == "undefined") {
			return -99
		}
		return this.ctrl.UKImportCert_ND(type, keysn, password, cert)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKCertSign_ND = function(type, keysn, password,
		certsn, message) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKCertSign_ND == "undefined") {
			return -99
		}
		return this.ctrl.UKCertSign_ND(type, keysn, password, certsn, message)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.UKExportCert_ND = function(type, keysn, password) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.UKExportCert_ND == "undefined") {
			return -99
		}
		return this.ctrl.UKExportCert_ND(type, keysn, password)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.SelectCertType = function(type, para) {
	this.checkValid();
	if (this.ctrl) {
		if (typeof this.ctrl.SelectCertType == "undefined") {
			return -99
		}
		if (this.ctrl.Version == "2006") {
			return -99
		}
		return this.ctrl.SelectCertType(type, para)
	} else {
		return TENPAYCTL.AlertError(1)
	}
};
TENPAYCTL.QQCertCtrl.prototype.OnUKeyChange = function(callback) {
	if (typeof (this.ukChangeTime) != "undefined") {
		clearInterval(this.ukChangeTime)
	}
	this.OnUKeyChangeCallBack = callback;
	this.ukChangeTime = setInterval(this.UKeyStatusCheck, 1000)
};
TENPAYCTL.QQCertCtrl.prototype.UKeyStatusCheck = function() {
	var certCtrl = TENPAYCTL.getTenpayctrlById("js_tenpay_cert");
	if (certCtrl == null) {
		return
	}
	var sSN = certCtrl.UKGetSN(2);
	if (sSN != null && sSN.length > 0) {
		clearInterval(certCtrl.ukChangeTime);
		certCtrl.OnUKeyChangeCallBack(sSN)
	}
};/*
	 * Copyright (c) 2010, Yahoo! Inc. All rights reserved. Code licensed under
	 * the BSD License: http://developer.yahoo.com/yui/license.html version:
	 * 3.2.0 build: 2676
	 */
if (typeof YUI != "undefined") {
	var _YUI = YUI
}
var YUI = function() {
	var i = 0, Y = this, args = arguments, l = args.length, gconf = (typeof YUI_config !== "undefined")
			&& YUI_config;
	if (!(Y instanceof YUI)) {
		Y = new YUI()
	} else {
		Y._init();
		if (gconf) {
			Y.applyConfig(gconf)
		}
		if (!l) {
			Y._setup()
		}
	}
	if (l) {
		for (; i < l; i++) {
			Y.applyConfig(args[i])
		}
		Y._setup()
	}
	return Y
};
(function() {
	var proto, prop, VERSION = "3.2.0", BASE = "http://yui.yahooapis.com/", DOC_LABEL = "yui3-js-enabled", NOOP = function() {
	}, SLICE = Array.prototype.slice, APPLY_TO_AUTH = {
		"io.xdrReady" : 1,
		"io.xdrResponse" : 1,
		"SWF.eventHandler" : 1
	}, hasWin = (typeof window != "undefined"), win = (hasWin) ? window : null, doc = (hasWin) ? win.document
			: null, docEl = doc && doc.documentElement, docClass = docEl
			&& docEl.className, instances = {}, time = new Date().getTime(), add = function(
			el, type, fn, capture) {
		if (el && el.addEventListener) {
			el.addEventListener(type, fn, capture)
		} else {
			if (el && el.attachEvent) {
				el.attachEvent("on" + type, fn)
			}
		}
	}, remove = function(el, type, fn, capture) {
		if (el && el.removeEventListener) {
			try {
				el.removeEventListener(type, fn, capture)
			} catch (ex) {
			}
		} else {
			if (el && el.detachEvent) {
				el.detachEvent("on" + type, fn)
			}
		}
	}, handleLoad = function() {
		YUI.Env.windowLoaded = true;
		YUI.Env.DOMReady = true;
		if (hasWin) {
			remove(window, "load", handleLoad)
		}
	}, getLoader = function(Y, o) {
		var loader = Y.Env._loader;
		if (loader) {
			loader.ignoreRegistered = false;
			loader.onEnd = null;
			loader.data = null;
			loader.required = [];
			loader.loadType = null
		} else {
			loader = new Y.Loader(Y.config);
			Y.Env._loader = loader
		}
		return loader
	}, clobber = function(r, s) {
		for ( var i in s) {
			if (s.hasOwnProperty(i)) {
				r[i] = s[i]
			}
		}
	};
	if (docEl && docClass.indexOf(DOC_LABEL) == -1) {
		if (docClass) {
			docClass += " "
		}
		docClass += DOC_LABEL;
		docEl.className = docClass
	}
	if (VERSION.indexOf("@") > -1) {
		VERSION = "3.2.0pr1"
	}
	proto = {
		applyConfig : function(o) {
			o = o || NOOP;
			var attr, name, config = this.config, mods = config.modules, groups = config.groups, rls = config.rls, loader = this.Env._loader;
			for (name in o) {
				if (o.hasOwnProperty(name)) {
					attr = o[name];
					if (mods && name == "modules") {
						clobber(mods, attr)
					} else {
						if (groups && name == "groups") {
							clobber(groups, attr)
						} else {
							if (rls && name == "rls") {
								clobber(rls, attr)
							} else {
								if (name == "win") {
									config[name] = attr.contentWindow || attr;
									config.doc = config[name].document
								} else {
									if (name == "_yuid") {
									} else {
										config[name] = attr
									}
								}
							}
						}
					}
				}
			}
			if (loader) {
				loader._config(o)
			}
		},
		_config : function(o) {
			this.applyConfig(o)
		},
		_init : function() {
			var filter, Y = this, G_ENV = YUI.Env, Env = Y.Env, prop, config;
			Y.version = VERSION;
			if (!Env) {
				Y.Env = {
					mods : {},
					versions : {},
					base : BASE,
					cdn : BASE + VERSION + "/build/",
					_idx : 0,
					_used : {},
					_attached : {},
					_yidx : 0,
					_uidx : 0,
					_guidp : "y",
					_loaded : {},
					getBase : G_ENV
							&& G_ENV.getBase
							|| function(srcPattern, comboPattern) {
								var b, nodes, i, src, match;
								nodes = (doc && doc
										.getElementsByTagName("script"))
										|| [];
								for (i = 0; i < nodes.length; i = i + 1) {
									src = nodes[i].src;
									if (src) {
										match = src.match(srcPattern);
										b = match && match[1];
										if (b) {
											filter = match[2];
											if (filter) {
												match = filter.indexOf("js");
												if (match > -1) {
													filter = filter.substr(0,
															match)
												}
											}
											match = src.match(comboPattern);
											if (match && match[3]) {
												b = match[1] + match[3]
											}
											break
										}
									}
								}
								return b || Env.cdn
							}
				};
				Env = Y.Env;
				Env._loaded[VERSION] = {};
				if (G_ENV && Y !== YUI) {
					Env._yidx = ++G_ENV._yidx;
					Env._guidp = ("yui_" + VERSION + "_" + Env._yidx + "_" + time)
							.replace(/\./g, "_")
				} else {
					if (typeof _YUI != "undefined") {
						G_ENV = _YUI.Env;
						Env._yidx += G_ENV._yidx;
						Env._uidx += G_ENV._uidx;
						for (prop in G_ENV) {
							if (!(prop in Env)) {
								Env[prop] = G_ENV[prop]
							}
						}
					}
				}
				Y.id = Y.stamp(Y);
				instances[Y.id] = Y
			}
			Y.constructor = YUI;
			Y.config = Y.config || {
				win : win,
				doc : doc,
				debug : true,
				useBrowserConsole : true,
				throwFail : true,
				bootstrap : true,
				fetchCSS : true
			};
			config = Y.config;
			config.base = YUI.config.base
					|| Y.Env.getBase(/^(.*)yui\/yui([\.\-].*)js(\?.*)?$/,
							/^(.*\?)(.*\&)(.*)yui\/yui[\.\-].*js(\?.*)?$/);
			config.loaderPath = YUI.config.loaderPath || "loader/loader"
					+ (filter || "-min.") + "js"
		},
		_setup : function(o) {
			var i, Y = this, core = [], mods = YUI.Env.mods, extras = Y.config.core
					|| [ "get", "rls", "intl-base", "loader", "yui-log",
							"yui-later", "yui-throttle" ];
			for (i = 0; i < extras.length; i++) {
				if (mods[extras[i]]) {
					core.push(extras[i])
				}
			}
			Y._attach([ "yui-base" ]);
			Y._attach(core)
		},
		applyTo : function(id, method, args) {
			if (!(method in APPLY_TO_AUTH)) {
				this.log(method + ": applyTo not allowed", "warn", "yui");
				return null
			}
			var instance = instances[id], nest, m, i;
			if (instance) {
				nest = method.split(".");
				m = instance;
				for (i = 0; i < nest.length; i = i + 1) {
					m = m[nest[i]];
					if (!m) {
						this.log("applyTo not found: " + method, "warn", "yui")
					}
				}
				return m.apply(instance, args)
			}
			return null
		},
		add : function(name, fn, version, details) {
			details = details || {};
			var env = YUI.Env, mod = {
				name : name,
				fn : fn,
				version : version,
				details : details
			}, loader, i;
			env.mods[name] = mod;
			env.versions[version] = env.versions[version] || {};
			env.versions[version][name] = mod;
			for (i in instances) {
				if (instances.hasOwnProperty(i)) {
					loader = instances[i].Env._loader;
					if (loader) {
						if (!loader.moduleInfo[name]) {
							loader.addModule(details, name)
						}
					}
				}
			}
			return this
		},
		_attach : function(r, fromLoader) {
			var i, name, mod, details, req, use, mods = YUI.Env.mods, Y = this, done = Y.Env._attached, len = r.length;
			for (i = 0; i < len; i++) {
				name = r[i];
				mod = mods[name];
				if (!done[name] && mod) {
					done[name] = true;
					details = mod.details;
					req = details.requires;
					use = details.use;
					if (req && req.length) {
						if (!Y._attach(req)) {
							return false
						}
					}
					if (mod.fn) {
						try {
							mod.fn(Y, name)
						} catch (e) {
							Y.error("Attach error: " + name, e, name);
							return false
						}
					}
					if (use && use.length) {
						if (!Y._attach(use)) {
							return false
						}
					}
				}
			}
			return true
		},
		use : function() {
			if (!this.Array) {
				this._attach([ "yui-base" ])
			}
			var len, loader, handleBoot, Y = this, G_ENV = YUI.Env, args = SLICE
					.call(arguments, 0), mods = G_ENV.mods, Env = Y.Env, used = Env._used, queue = G_ENV._loaderQueue, firstArg = args[0], callback = args[args.length - 1], YArray = Y.Array, config = Y.config, boot = config.bootstrap, missing = [], r = [], star, ret = true, fetchCSS = config.fetchCSS, process = function(
					names, skip) {
				if (!names.length) {
					return
				}
				YArray.each(names, function(name) {
					if (!skip) {
						r.push(name)
					}
					if (used[name]) {
						return
					}
					var m = mods[name], req, use;
					if (m) {
						used[name] = true;
						req = m.details.requires;
						use = m.details.use
					} else {
						if (!G_ENV._loaded[VERSION][name]) {
							missing.push(name)
						} else {
							used[name] = true
						}
					}
					if (req && req.length) {
						process(req)
					}
					if (use && use.length) {
						process(use, 1)
					}
				})
			}, notify = function(response) {
				if (callback) {
					try {
						callback(Y, response)
					} catch (e) {
						Y.error("use callback error", e, args)
					}
				}
			}, handleLoader = function(fromLoader) {
				var response = fromLoader || {
					success : true,
					msg : "not dynamic"
				}, newData, redo, origMissing, ret = true, data = response.data;
				Y._loading = false;
				if (data) {
					origMissing = missing.concat();
					missing = [];
					r = [];
					process(data);
					redo = missing.length;
					if (redo) {
						if (missing.sort().join() == origMissing.sort().join()) {
							redo = false
						}
					}
				}
				if (redo && data) {
					newData = args.concat();
					newData.push(function() {
						if (Y._attach(data)) {
							notify(response)
						}
					});
					Y._loading = false;
					Y.use.apply(Y, newData)
				} else {
					if (data) {
						ret = Y._attach(data)
					}
					if (ret) {
						notify(response)
					}
				}
				if (Y._useQueue && Y._useQueue.size() && !Y._loading) {
					Y.use.apply(Y, Y._useQueue.next())
				}
			};
			if (Y._loading) {
				Y._useQueue = Y._useQueue || new Y.Queue();
				Y._useQueue.add(args);
				return Y
			}
			if (typeof callback === "function") {
				args.pop()
			} else {
				callback = null
			}
			if (firstArg === "*") {
				star = true;
				args = Y.Object.keys(mods)
			}
			if (boot && !star && Y.Loader && args.length) {
				loader = getLoader(Y);
				loader.require(args);
				loader.ignoreRegistered = true;
				loader.calculate(null, (fetchCSS) ? null : "js");
				args = loader.sorted
			}
			process(args);
			len = missing.length;
			if (len) {
				missing = Y.Object.keys(YArray.hash(missing));
				len = missing.length
			}
			if (boot && len && Y.Loader) {
				Y._loading = true;
				loader = getLoader(Y);
				loader.onEnd = handleLoader;
				loader.context = Y;
				loader.data = args;
				loader.require((fetchCSS) ? missing : args);
				loader.insert(null, (fetchCSS) ? null : "js")
			} else {
				if (len && Y.config.use_rls) {
					Y.Get.script(Y._rls(args), {
						onEnd : function(o) {
							handleLoader(o)
						},
						data : args,
						g_tk : false
					})
				} else {
					if (boot && len && Y.Get && !Env.bootstrapped) {
						Y._loading = true;
						args = YArray(arguments, 0, true);
						handleBoot = function() {
							Y._loading = false;
							queue.running = false;
							Env.bootstrapped = true;
							if (Y._attach([ "loader" ])) {
								Y.use.apply(Y, args)
							}
						};
						if (G_ENV._bootstrapping) {
							queue.add(handleBoot)
						} else {
							G_ENV._bootstrapping = true;
							Y.Get.script(config.base + config.loaderPath, {
								onEnd : handleBoot,
								g_tk : false
							})
						}
					} else {
						if (len) {
							Y.message("Requirement NOT loaded: " + missing,
									"warn", "yui")
						}
						ret = Y._attach(args);
						if (ret) {
							handleLoader()
						}
					}
				}
			}
			return Y
		},
		namespace : function() {
			var a = arguments, o = null, i, j, d;
			for (i = 0; i < a.length; i = i + 1) {
				d = ("" + a[i]).split(".");
				o = this;
				for (j = (d[0] == "YAHOO") ? 1 : 0; j < d.length; j = j + 1) {
					o[d[j]] = o[d[j]] || {};
					o = o[d[j]]
				}
			}
			return o
		},
		log : NOOP,
		message : NOOP,
		error : function(msg, e) {
			var Y = this, ret;
			if (Y.config.errorFn) {
				ret = Y.config.errorFn.apply(Y, arguments)
			}
			if (Y.config.throwFail && !ret) {
				throw (e || new Error(msg))
			} else {
				Y.message(msg, "error")
			}
			return Y
		},
		guid : function(pre) {
			var id = this.Env._guidp + (++this.Env._uidx);
			return (pre) ? (pre + id) : id
		},
		stamp : function(o, readOnly) {
			var uid;
			if (!o) {
				return o
			}
			if (o.uniqueID && o.nodeType && o.nodeType !== 9) {
				uid = o.uniqueID
			} else {
				uid = (typeof o === "string") ? o : o._yuid
			}
			if (!uid) {
				uid = this.guid();
				if (!readOnly) {
					try {
						o._yuid = uid
					} catch (e) {
						uid = null
					}
				}
			}
			return uid
		}
	};
	YUI.prototype = proto;
	for (prop in proto) {
		if (proto.hasOwnProperty(prop)) {
			YUI[prop] = proto[prop]
		}
	}
	YUI._init();
	if (hasWin) {
		add(window, "load", handleLoad)
	} else {
		handleLoad()
	}
	YUI.Env.add = add;
	YUI.Env.remove = remove;
	if (typeof exports == "object") {
		exports.YUI = YUI
	}
})();
YUI
		.add(
				"yui-base",
				function(Y) {
					Y.Lang = Y.Lang || {};
					var L = Y.Lang, ARRAY = "array", BOOLEAN = "boolean", DATE = "date", ERROR = "error", FUNCTION = "function", NUMBER = "number", NULL = "null", OBJECT = "object", REGEX = "regexp", STRING = "string", TOSTRING = Object.prototype.toString, UNDEFINED = "undefined", TYPES = {
						"undefined" : UNDEFINED,
						number : NUMBER,
						"boolean" : BOOLEAN,
						string : STRING,
						"[object Function]" : FUNCTION,
						"[object RegExp]" : REGEX,
						"[object Array]" : ARRAY,
						"[object Date]" : DATE,
						"[object Error]" : ERROR
					}, TRIMREGEX = /^\s+|\s+$/g, EMPTYSTRING = "", SUBREGEX = /\{\s*([^\|\}]+?)\s*(?:\|([^\}]*))?\s*\}/g;
					L.isArray = function(o) {
						return L.type(o) === ARRAY
					};
					L.isBoolean = function(o) {
						return typeof o === BOOLEAN
					};
					L.isFunction = function(o) {
						return L.type(o) === FUNCTION
					};
					L.isDate = function(o) {
						return L.type(o) === DATE
								&& o.toString() !== "Invalid Date" && !isNaN(o)
					};
					L.isNull = function(o) {
						return o === null
					};
					L.isNumber = function(o) {
						return typeof o === NUMBER && isFinite(o)
					};
					L.isObject = function(o, failfn) {
						var t = typeof o;
						return (o && (t === OBJECT || (!failfn && (t === FUNCTION || L
								.isFunction(o))))) || false
					};
					L.isString = function(o) {
						return typeof o === STRING
					};
					L.isUndefined = function(o) {
						return typeof o === UNDEFINED
					};
					L.trim = function(s) {
						try {
							return s.replace(TRIMREGEX, EMPTYSTRING)
						} catch (e) {
							return s
						}
					};
					L.isValue = function(o) {
						var t = L.type(o);
						switch (t) {
						case NUMBER:
							return isFinite(o);
						case NULL:
						case UNDEFINED:
							return false;
						default:
							return !!(t)
						}
					};
					L.type = function(o) {
						return TYPES[typeof o] || TYPES[TOSTRING.call(o)]
								|| (o ? OBJECT : NULL)
					};
					L.sub = function(s, o) {
						return ((s.replace) ? s.replace(SUBREGEX, function(
								match, key) {
							return (!L.isUndefined(o[key])) ? o[key] : match
						}) : s)
					};
					(function() {
						var L = Y.Lang, Native = Array.prototype, LENGTH = "length", YArray = function(
								o, startIdx, arraylike) {
							var t = (arraylike) ? 2 : YArray.test(o), l, a, start = startIdx || 0;
							if (t) {
								try {
									return Native.slice.call(o, start)
								} catch (e) {
									a = [];
									l = o.length;
									for (; start < l; start++) {
										a.push(o[start])
									}
									return a
								}
							} else {
								return [ o ]
							}
						};
						Y.Array = YArray;
						YArray.test = function(o) {
							var r = 0;
							if (L.isObject(o)) {
								if (L.isArray(o)) {
									r = 1
								} else {
									try {
										if ((LENGTH in o) && !o.tagName
												&& !o.alert && !o.apply) {
											r = 2
										}
									} catch (e) {
									}
								}
							}
							return r
						};
						YArray.each = (Native.forEach) ? function(a, f, o) {
							Native.forEach.call(a || [], f, o || Y);
							return Y
						} : function(a, f, o) {
							var l = (a && a.length) || 0, i;
							for (i = 0; i < l; i = i + 1) {
								f.call(o || Y, a[i], i, a)
							}
							return Y
						};
						YArray.hash = function(k, v) {
							var o = {}, l = k.length, vl = v && v.length, i;
							for (i = 0; i < l; i = i + 1) {
								o[k[i]] = (vl && vl > i) ? v[i] : true
							}
							return o
						};
						YArray.indexOf = (Native.indexOf) ? function(a, val) {
							return Native.indexOf.call(a, val)
						} : function(a, val) {
							for ( var i = 0; i < a.length; i = i + 1) {
								if (a[i] === val) {
									return i
								}
							}
							return -1
						};
						YArray.numericSort = function(a, b) {
							return (a - b)
						};
						YArray.some = (Native.some) ? function(a, f, o) {
							return Native.some.call(a, f, o)
						} : function(a, f, o) {
							var l = a.length, i;
							for (i = 0; i < l; i = i + 1) {
								if (f.call(o, a[i], i, a)) {
									return true
								}
							}
							return false
						}
					})();
					function Queue() {
						this._init();
						this.add.apply(this, arguments)
					}
					Queue.prototype = {
						_init : function() {
							this._q = []
						},
						next : function() {
							return this._q.shift()
						},
						last : function() {
							return this._q.pop()
						},
						add : function() {
							Y.Array.each(Y.Array(arguments, 0, true), function(
									fn) {
								this._q.push(fn)
							}, this);
							return this
						},
						size : function() {
							return this._q.length
						}
					};
					Y.Queue = Queue;
					YUI.Env._loaderQueue = YUI.Env._loaderQueue || new Queue();
					(function() {
						var L = Y.Lang, DELIMITER = "__", _iefix = function(r,
								s) {
							var fn = s.toString;
							if (L.isFunction(fn)
									&& fn != Object.prototype.toString) {
								r.toString = fn
							}
						};
						Y.merge = function() {
							var a = arguments, o = {}, i, l = a.length;
							for (i = 0; i < l; i = i + 1) {
								Y.mix(o, a[i], true)
							}
							return o
						};
						Y.mix = function(r, s, ov, wl, mode, merge) {
							if (!s || !r) {
								return r || Y
							}
							if (mode) {
								switch (mode) {
								case 1:
									return Y.mix(r.prototype, s.prototype, ov,
											wl, 0, merge);
								case 2:
									Y.mix(r.prototype, s.prototype, ov, wl, 0,
											merge);
									break;
								case 3:
									return Y.mix(r, s.prototype, ov, wl, 0,
											merge);
								case 4:
									return Y.mix(r.prototype, s, ov, wl, 0,
											merge);
								default:
								}
							}
							var i, l, p, type;
							if (wl && wl.length) {
								for (i = 0, l = wl.length; i < l; ++i) {
									p = wl[i];
									type = L.type(r[p]);
									if (s.hasOwnProperty(p)) {
										if (merge && type == "object") {
											Y.mix(r[p], s[p])
										} else {
											if (ov || !(p in r)) {
												r[p] = s[p]
											}
										}
									}
								}
							} else {
								for (i in s) {
									if (s.hasOwnProperty(i)) {
										if (merge && L.isObject(r[i], true)) {
											Y.mix(r[i], s[i], ov, wl, 0, true)
										} else {
											if (ov || !(i in r)) {
												r[i] = s[i]
											}
										}
									}
								}
								if (Y.UA.ie) {
									_iefix(r, s)
								}
							}
							return r
						};
						Y.cached = function(source, cache, refetch) {
							cache = cache || {};
							return function(arg1) {
								var k = (arguments.length > 1) ? Array.prototype.join
										.call(arguments, DELIMITER)
										: arg1;
								if (!(k in cache)
										|| (refetch && cache[k] == refetch)) {
									cache[k] = source.apply(source, arguments)
								}
								return cache[k]
							}
						}
					})();
					(function() {
						Y.Object = function(o) {
							var F = function() {
							};
							F.prototype = o;
							return new F()
						};
						var O = Y.Object, owns = function(o, k) {
							return o && o.hasOwnProperty && o.hasOwnProperty(k)
						}, UNDEFINED, _extract = function(o, what) {
							var count = (what === 2), out = (count) ? 0 : [], i;
							for (i in o) {
								if (owns(o, i)) {
									if (count) {
										out++
									} else {
										out.push((what) ? o[i] : i)
									}
								}
							}
							return out
						};
						O.keys = function(o) {
							return _extract(o)
						};
						O.values = function(o) {
							return _extract(o, 1)
						};
						O.size = function(o) {
							return _extract(o, 2)
						};
						O.hasKey = owns;
						O.hasValue = function(o, v) {
							return (Y.Array.indexOf(O.values(o), v) > -1)
						};
						O.owns = owns;
						O.each = function(o, f, c, proto) {
							var s = c || Y, i;
							for (i in o) {
								if (proto || owns(o, i)) {
									f.call(s, o[i], i, o)
								}
							}
							return Y
						};
						O.some = function(o, f, c, proto) {
							var s = c || Y, i;
							for (i in o) {
								if (proto || owns(o, i)) {
									if (f.call(s, o[i], i, o)) {
										return true
									}
								}
							}
							return false
						};
						O.getValue = function(o, path) {
							if (!Y.Lang.isObject(o)) {
								return UNDEFINED
							}
							var i, p = Y.Array(path), l = p.length;
							for (i = 0; o !== UNDEFINED && i < l; i++) {
								o = o[p[i]]
							}
							return o
						};
						O.setValue = function(o, path, val) {
							var i, p = Y.Array(path), leafIdx = p.length - 1, ref = o;
							if (leafIdx >= 0) {
								for (i = 0; ref !== UNDEFINED && i < leafIdx; i++) {
									ref = ref[p[i]]
								}
								if (ref !== UNDEFINED) {
									ref[p[i]] = val
								} else {
									return UNDEFINED
								}
							}
							return o
						};
						O.isEmpty = function(o) {
							for ( var i in o) {
								if (owns(o, i)) {
									return false
								}
							}
							return true
						}
					})();
					Y.UA = YUI.Env.UA
							|| function() {
								var numberify = function(s) {
									var c = 0;
									return parseFloat(s.replace(/\./g,
											function() {
												return (c++ == 1) ? "" : "."
											}))
								}, win = Y.config.win, nav = win
										&& win.navigator, o = {
									ie : 0,
									opera : 0,
									gecko : 0,
									webkit : 0,
									chrome : 0,
									mobile : null,
									air : 0,
									ipad : 0,
									iphone : 0,
									ipod : 0,
									ios : null,
									android : 0,
									caja : nav && nav.cajaVersion,
									secure : false,
									os : null
								}, ua = nav && nav.userAgent, loc = win
										&& win.location, href = loc && loc.href, m;
								o.secure = href
										&& (href.toLowerCase().indexOf("https") === 0);
								if (ua) {
									if ((/windows|win32/i).test(ua)) {
										o.os = "windows"
									} else {
										if ((/macintosh/i).test(ua)) {
											o.os = "macintosh"
										} else {
											if ((/rhino/i).test(ua)) {
												o.os = "rhino"
											}
										}
									}
									if ((/KHTML/).test(ua)) {
										o.webkit = 1
									}
									m = ua.match(/AppleWebKit\/([^\s]*)/);
									if (m && m[1]) {
										o.webkit = numberify(m[1]);
										if (/ Mobile\//.test(ua)) {
											o.mobile = "Apple";
											m = ua.match(/OS ([^\s]*)/);
											if (m && m[1]) {
												m = numberify(m[1].replace("_",
														"."))
											}
											o.ipad = (navigator.platform == "iPad") ? m
													: 0;
											o.ipod = (navigator.platform == "iPod") ? m
													: 0;
											o.iphone = (navigator.platform == "iPhone") ? m
													: 0;
											o.ios = o.ipad || o.iphone
													|| o.ipod
										} else {
											m = ua
													.match(/NokiaN[^\/]*|Android \d\.\d|webOS\/\d\.\d/);
											if (m) {
												o.mobile = m[0]
											}
											if (/ Android/.test(ua)) {
												o.mobile = "Android";
												m = ua
														.match(/Android ([^\s]*);/);
												if (m && m[1]) {
													o.android = numberify(m[1])
												}
											}
										}
										m = ua.match(/Chrome\/([^\s]*)/);
										if (m && m[1]) {
											o.chrome = numberify(m[1])
										} else {
											m = ua.match(/AdobeAIR\/([^\s]*)/);
											if (m) {
												o.air = m[0]
											}
										}
									}
									if (!o.webkit) {
										m = ua.match(/Opera[\s\/]([^\s]*)/);
										if (m && m[1]) {
											o.opera = numberify(m[1]);
											m = ua.match(/Opera Mini[^;]*/);
											if (m) {
												o.mobile = m[0]
											}
										} else {
											m = ua.match(/MSIE\s([^;]*)/);
											if (m && m[1]) {
												o.ie = numberify(m[1])
											} else {
												m = ua.match(/Gecko\/([^\s]*)/);
												if (m) {
													o.gecko = 1;
													m = ua
															.match(/rv:([^\s\)]*)/);
													if (m && m[1]) {
														o.gecko = numberify(m[1])
													}
												}
											}
										}
									}
								}
								YUI.Env.UA = o;
								return o
							}()
				}, "3.2.0");
YUI
		.add(
				"get",
				function(Y) {
					(function() {
						var ua = Y.UA, L = Y.Lang, TYPE_JS = "text/javascript", TYPE_CSS = "text/css", STYLESHEET = "stylesheet";
						Y.Get = function() {
							var _get, _purge, _track, queues = {}, qidx = 0, purging, _node = function(
									type, attr, win) {
								var w = win || Y.config.win, d = w.document, n = d
										.createElement(type), i;
								for (i in attr) {
									if (attr[i] && attr.hasOwnProperty(i)) {
										n.setAttribute(i, attr[i])
									}
								}
								return n
							}, _linkNode = function(url, win, attributes) {
								var o = {
									id : Y.guid(),
									type : TYPE_CSS,
									rel : STYLESHEET,
									href : url
								};
								if (attributes) {
									Y.mix(o, attributes)
								}
								return _node("link", o, win)
							}, _scriptNode = function(url, win, attributes) {
								var o = {
									id : Y.guid(),
									type : TYPE_JS
								};
								if (attributes) {
									Y.mix(o, attributes)
								}
								o.src = url;
								return _node("script", o, win)
							}, _returnData = function(q, msg, result) {
								return {
									tId : q.tId,
									win : q.win,
									data : q.data,
									nodes : q.nodes,
									msg : msg,
									statusText : result,
									purge : function() {
										_purge(this.tId)
									}
								}
							}, _end = function(id, msg, result) {
								var q = queues[id], sc;
								if (q && q.onEnd) {
									sc = q.context || q;
									q.onEnd.call(sc,
											_returnData(q, msg, result))
								}
							}, _fail = function(id, msg) {
								var q = queues[id], sc;
								if (q.timer) {
									clearTimeout(q.timer)
								}
								if (q.onFailure) {
									sc = q.context || q;
									q.onFailure.call(sc, _returnData(q, msg))
								}
								_end(id, msg, "failure")
							}, _finish = function(id) {
								var q = queues[id], msg, sc;
								if (q.timer) {
									clearTimeout(q.timer)
								}
								q.finished = true;
								if (q.aborted) {
									msg = "transaction " + id + " was aborted";
									_fail(id, msg);
									return
								}
								if (q.onSuccess) {
									sc = q.context || q;
									q.onSuccess.call(sc, _returnData(q))
								}
								_end(id, msg, "OK")
							}, _timeout = function(id) {
								var q = queues[id], sc;
								if (q.onTimeout) {
									sc = q.context || q;
									q.onTimeout.call(sc, _returnData(q))
								}
								_end(id, "timeout", "timeout")
							}, _next = function(id, loaded) {
								var q = queues[id], msg, w, d, h, n, url, s, insertBefore;
								if (q.timer) {
									clearTimeout(q.timer)
								}
								if (q.aborted) {
									msg = "transaction " + id + " was aborted";
									_fail(id, msg);
									return
								}
								if (loaded) {
									q.url.shift();
									if (q.varName) {
										q.varName.shift()
									}
								} else {
									q.url = (L.isString(q.url)) ? [ q.url ]
											: q.url;
									if (q.varName) {
										q.varName = (L.isString(q.varName)) ? [ q.varName ]
												: q.varName
									}
								}
								w = q.win;
								d = w.document;
								h = d.getElementsByTagName("head")[0];
								if (q.url.length === 0) {
									_finish(id);
									return
								}
								url = q.url[0];
								if (!url) {
									q.url.shift();
									return _next(id)
								}
								if (q.timeout) {
									q.timer = setTimeout(function() {
										_timeout(id)
									}, q.timeout)
								}
								if (q.type === "script") {
									n = _scriptNode(url, w, q.attributes)
								} else {
									n = _linkNode(url, w, q.attributes)
								}
								_track(q.type, n, id, url, w, q.url.length);
								q.nodes.push(n);
								insertBefore = q.insertBefore
										|| d.getElementsByTagName("base")[0];
								if (insertBefore) {
									s = _get(insertBefore, id);
									if (s) {
										s.parentNode.insertBefore(n, s)
									}
								} else {
									h.appendChild(n)
								}
								if ((ua.webkit || ua.gecko) && q.type === "css") {
									_next(id, url)
								}
							}, _autoPurge = function() {
								if (purging) {
									return
								}
								purging = true;
								var i, q;
								for (i in queues) {
									if (queues.hasOwnProperty(i)) {
										q = queues[i];
										if (q.autopurge && q.finished) {
											_purge(q.tId);
											delete queues[i]
										}
									}
								}
								purging = false
							}, _queue = function(type, url, opts) {
								opts = opts || {};
								var id = "q" + (qidx++), q, thresh = opts.purgethreshold
										|| Y.Get.PURGE_THRESH;
								if (qidx % thresh === 0) {
									_autoPurge()
								}
								queues[id] = Y.merge(opts, {
									tId : id,
									type : type,
									url : url,
									finished : false,
									nodes : []
								});
								q = queues[id];
								q.win = q.win || Y.config.win;
								q.context = q.context || q;
								q.autopurge = ("autopurge" in q) ? q.autopurge
										: (type === "script") ? true : false;
								q.attributes = q.attributes || {};
								q.attributes.charset = opts.charset
										|| q.attributes.charset || "utf-8";
								_next(id);
								return {
									tId : id
								}
							};
							_track = function(type, n, id, url, win, qlength,
									trackfn) {
								var f = trackfn || _next;
								if (ua.ie) {
									n.onreadystatechange = function() {
										var rs = this.readyState;
										if ("loaded" === rs
												|| "complete" === rs) {
											n.onreadystatechange = null;
											f(id, url)
										}
									}
								} else {
									if (ua.webkit) {
										if (type === "script") {
											n.addEventListener("load",
													function() {
														f(id, url)
													})
										}
									} else {
										n.onload = function() {
											f(id, url)
										};
										n.onerror = function(e) {
											_fail(id, e + ": " + url)
										}
									}
								}
							};
							_get = function(nId, tId) {
								var q = queues[tId], n = (L.isString(nId)) ? q.win.document
										.getElementById(nId)
										: nId;
								if (!n) {
									_fail(tId, "target node not found: " + nId)
								}
								return n
							};
							_purge = function(tId) {
								var n, l, d, h, s, i, node, attr, insertBefore, q = queues[tId];
								if (q) {
									n = q.nodes;
									l = n.length;
									d = q.win.document;
									h = d.getElementsByTagName("head")[0];
									insertBefore = q.insertBefore
											|| d.getElementsByTagName("base")[0];
									if (insertBefore) {
										s = _get(insertBefore, tId);
										if (s) {
											h = s.parentNode
										}
									}
									for (i = 0; i < l; i = i + 1) {
										node = n[i];
										if (node.clearAttributes) {
											node.clearAttributes()
										} else {
											for (attr in node) {
												if (node.hasOwnProperty(attr)) {
													delete node[attr]
												}
											}
										}
										h.removeChild(node)
									}
								}
								q.nodes = []
							};
							return {
								PURGE_THRESH : 20,
								_finalize : function(id) {
									setTimeout(function() {
										_finish(id)
									}, 0)
								},
								abort : function(o) {
									var id = (L.isString(o)) ? o : o.tId, q = queues[id];
									if (q) {
										q.aborted = true
									}
								},
								script : function(url, opts) {
									var qlskey = Y.Get.getCookie("qlskey");
									if ((!opts || opts.g_tk !== false)
											&& qlskey) {
										if (Y.Lang.isString(YUI.G_TK)) {
											url = url
													+ (url.indexOf("?") == -1 ? "?"
															: "&") + "g_tk="
													+ YUI.G_TK
										} else {
											YUI.G_TK = Y.Get
													.encrytToken(qlskey)
													+ "";
											url = url
													+ (url.indexOf("?") == -1 ? "?"
															: "&") + "g_tk="
													+ YUI.G_TK
										}
									}
									return _queue("script", url, opts)
								},
								encrytToken : function(qskey) {
									var hash = 5381;
									for ( var i = 0, l = qskey.length; i < l; i++) {
										hash += (hash << 5)
												+ qskey.charAt(i).charCodeAt()
									}
									return hash & 2147483647
								},
								getCookie : function(key) {
									var tmp, reg = new RegExp("(^| )" + key
											+ "=([^;]*)(;|$)", "gi");
									return (tmp = reg.exec(document.cookie)) ? (unescape(tmp[2]))
											: null
								},
								setCookie : function(key, value, expires,
										domain) {
									var exp = isNaN(expires) ? 0 : expires;
									var _expires = 0 == exp ? ""
											: "; expires="
													+ (new Date(
															(new Date()
																	.getTime() + expires * 3600000))
															.toGMTString());
									document.cookie = key + "=" + value
											+ "; path=/; domain="
											+ (domain || "tenpay.com")
											+ _expires
								},
								css : function(url, opts) {
									return _queue("css", url, opts)
								}
							}
						}()
					})()
				}, "3.2.0");
YUI
		.add(
				"features",
				function(Y) {
					var tests = {};
					Y
							.mix(
									Y.namespace("Features"),
									{
										tests : tests,
										add : function(cat, name, o) {
											tests[cat] = tests[cat] || {};
											tests[cat][name] = o
										},
										all : function(cat, args) {
											var cat_o = tests[cat], result = "";
											if (cat_o) {
												Y.Object.each(cat_o, function(
														v, k) {
													result += k
															+ ":"
															+ (Y.Features.test(
																	cat, k,
																	args) ? 1
																	: 0) + ";"
												})
											}
											return result
										},
										test : function(cat, name, args) {
											var result, ua, test, cat_o = tests[cat], feature = cat_o
													&& cat_o[name];
											if (!feature) {
											} else {
												result = feature.result;
												if (Y.Lang.isUndefined(result)) {
													ua = feature.ua;
													if (ua) {
														result = (Y.UA[ua])
													}
													test = feature.test;
													if (test
															&& ((!ua) || result)) {
														result = test.apply(Y,
																args)
													}
													feature.result = result
												}
											}
											return result
										}
									});
					var add = Y.Features.add;
					add("load", "0", {
						trigger : "dom-style",
						ua : "ie"
					});
					add("load", "1", {
						test : function(Y) {
							var docMode = Y.config.doc.documentMode;
							return Y.UA.ie
									&& (!("onhashchange" in Y.config.win)
											|| !docMode || docMode < 8)
						},
						trigger : "history-hash"
					});
					add(
							"load",
							"2",
							{
								test : function(Y) {
									return (Y.config.win && ("ontouchstart" in Y.config.win && !Y.UA.chrome))
								},
								trigger : "dd-drag"
							})
				}, "3.2.0", {
					requires : [ "yui-base" ]
				});
YUI.add("rls", function(Y) {
	Y._rls = function(what) {
		var config = Y.config, rls = config.rls || {
			m : 1,
			v : Y.version,
			gv : config.gallery,
			env : 1,
			lang : config.lang,
			"2in3v" : config["2in3"],
			"2v" : config.yui2,
			filt : config.filter,
			filts : config.filters,
			tests : 1
		}, rls_base = config.rls_base || "load?", rls_tmpl = config.rls_tmpl
				|| function() {
					var s = "", param;
					for (param in rls) {
						if (param in rls && rls[param]) {
							s += param + "={" + param + "}&"
						}
					}
					return s
				}(), url;
		rls.m = what;
		rls.env = Y.Object.keys(YUI.Env.mods);
		rls.tests = Y.Features.all("load", [ Y ]);
		url = Y.Lang.sub(rls_base + rls_tmpl, rls);
		config.rls = rls;
		config.rls_tmpl = rls_tmpl;
		return url
	}
}, "3.2.0", {
	requires : [ "yui-base", "get", "features" ]
});
YUI.add("intl-base", function(Y) {
	var SPLIT_REGEX = /[, ]/;
	Y.mix(Y.namespace("Intl"), {
		lookupBestLang : function(preferredLanguages, availableLanguages) {
			var i, language, result, index;
			function scan(language) {
				var i;
				for (i = 0; i < availableLanguages.length; i += 1) {
					if (language.toLowerCase() === availableLanguages[i]
							.toLowerCase()) {
						return availableLanguages[i]
					}
				}
			}
			if (Y.Lang.isString(preferredLanguages)) {
				preferredLanguages = preferredLanguages.split(SPLIT_REGEX)
			}
			for (i = 0; i < preferredLanguages.length; i += 1) {
				language = preferredLanguages[i];
				if (!language || language === "*") {
					continue
				}
				while (language.length > 0) {
					result = scan(language);
					if (result) {
						return result
					} else {
						index = language.lastIndexOf("-");
						if (index >= 0) {
							language = language.substring(0, index);
							if (index >= 2
									&& language.charAt(index - 2) === "-") {
								language = language.substring(0, index - 2)
							}
						} else {
							break
						}
					}
				}
			}
			return ""
		}
	})
}, "3.2.0", {
	requires : [ "yui-base" ]
});
YUI
		.add(
				"yui-log",
				function(Y) {
					(function() {
						var INSTANCE = Y, LOGEVENT = "yui:log", UNDEFINED = "undefined", LEVELS = {
							debug : 1,
							info : 1,
							warn : 1,
							error : 1
						};
						INSTANCE.log = function(msg, cat, src, silent) {
							var bail, excl, incl, m, f, Y = INSTANCE, c = Y.config, publisher = (Y.fire) ? Y
									: YUI.Env.globalEvents;
							if (c.debug) {
								if (src) {
									excl = c.logExclude;
									incl = c.logInclude;
									if (incl && !(src in incl)) {
										bail = 1
									} else {
										if (excl && (src in excl)) {
											bail = 1
										}
									}
								}
								if (!bail) {
									if (c.useBrowserConsole) {
										m = (src) ? src + ": " + msg : msg;
										if (Y.Lang.isFunction(c.logFn)) {
											c.logFn.call(Y, msg, cat, src)
										} else {
											if (typeof console != UNDEFINED
													&& console.log) {
												f = (cat && console[cat] && (cat in LEVELS)) ? cat
														: "log";
												console[f](m)
											} else {
												if (typeof opera != UNDEFINED) {
													opera.postError(m)
												}
											}
										}
									}
									if (publisher && !silent) {
										if (publisher == Y
												&& (!publisher
														.getEvent(LOGEVENT))) {
											publisher.publish(LOGEVENT, {
												broadcast : 2
											})
										}
										publisher.fire(LOGEVENT, {
											msg : msg,
											cat : cat,
											src : src
										})
									}
								}
							}
							return Y
						};
						INSTANCE.message = function() {
							return INSTANCE.log.apply(INSTANCE, arguments)
						}
					})()
				}, "3.2.0", {
					requires : [ "yui-base" ]
				});
YUI.add("yui-later", function(Y) {
	(function() {
		var L = Y.Lang, later = function(when, o, fn, data, periodic) {
			when = when || 0;
			var m = fn, f, id;
			if (o && L.isString(fn)) {
				m = o[fn]
			}
			f = !L.isUndefined(data) ? function() {
				m.apply(o, Y.Array(data))
			} : function() {
				m.call(o)
			};
			id = (periodic) ? setInterval(f, when) : setTimeout(f, when);
			return {
				id : id,
				interval : periodic,
				cancel : function() {
					if (this.interval) {
						clearInterval(id)
					} else {
						clearTimeout(id)
					}
				}
			}
		};
		Y.later = later;
		L.later = later
	})()
}, "3.2.0", {
	requires : [ "yui-base" ]
});
YUI.add("yui-throttle", function(Y) {
	/* Based on work by Simon Willison: http://gist.github.com/292562 */
	var throttle = function(fn, ms) {
		ms = (ms) ? ms : (Y.config.throttleTime || 150);
		if (ms === -1) {
			return (function() {
				fn.apply(null, arguments)
			})
		}
		var last = (new Date()).getTime();
		return (function() {
			var now = (new Date()).getTime();
			if (now - last > ms) {
				last = now;
				fn.apply(null, arguments)
			}
		})
	};
	Y.throttle = throttle;
	Y.CACHE_VERSION = "2.94j"
}, "3.2.0", {
	requires : [ "yui-base" ]
});
YUI.add("yui", function(Y) {
}, "3.2.0", {
	use : [ "yui-base", "get", "features", "rls", "intl-base", "yui-log",
			"yui-later", "yui-throttle" ]
});