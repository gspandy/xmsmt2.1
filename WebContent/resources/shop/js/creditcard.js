var blUsePasswdCol = 0	// blUsePasswdCol 全局变量标示是否使用控件, 0 -- 使用，非0 -- 不使用
//var PasswdColClassId = "clsid:D151948A-F3F3-41FE-BE76-07C64ACE8B53";//"clsid:E787FD25-8D7C-4693-AE67-9406BC6E22DF";          // 控件的classid
var PasswdColVersion = "1,1,0,4"	// 控件的版本号
var PasswdColClassId = "clsid:E787FD25-8D7C-4693-AE67-9406BC6E22DF";          // 控件的classid
var minSaveAmount = 0.01;     // 最小的充值金额
var maxSaveAmount = 5000;   // 最大的充值金额
/*
// 强制使用控件
//判断浏览器类型，非IE则不启动控件
var ver = navigator.appVersion;
if (ver.indexOf("MSIE") != -1)
{
blUsePasswdCol = 0;
}else{
blUsePasswdCol = 1;
}
*/
function getEditLength(ctrl)
{
var len = ctrl.GetInputInfo();
var len1 = (len>>16)&65535;
return len1;
}
function checkEditLength(ctrl)
{
var len = ctrl.GetInputInfo();
var len1 = (len>>16)&65535;
if(len1== 0)
{
return false;
}
return true;
}
function checkEditInput(ctrl)
{
var len = ctrl.GetInputInfo();
if(len&32 || len&64)
{
//alert("支付密码长度必须大于8字节小于16字节,且不允许包含空格!");
return false;
}
return true;
}
function checkEditInput2(ctrl,obj)
{
var len = ctrl.GetInputInfo();
var olen = obj.GetInputInfo();
var olen1 = (olen>>16)&65535;
var len1 = (len>>16)&65535;
if(( len1< 8 || len1 > 16 || len&32 || len&64)||
( olen1< 8 || olen1 > 16 || olen&32 || olen&64))
{
//alert("支付密码长度必须大于8字节小于16字节,且不允许包含空格!");
return false;
}
if(olen != len)
{
//alert("两次输入的支付密码不一致。");
return false;
}
return true;
}
// 获取通用密码(rsap)
function getStrongPasswd(ctrl, seed)
{
ctrl.SetSalt(seed);
shap = ctrl.GetSha1Value();
rsap = ctrl.GetRsaPassword();
return (seed + shap + rsap);
}
// 获取字符串的字节长度
function strlen(str)
{
var len;
var i;
len = 0;
for (i=0;i<str.length;i++)
{
if (str.charCodeAt(i)>255) len+=2; else len++;
}
return len;
}
// 检查是否为数字
function checkIsInteger(str)
{
if (str == "")
return false;
if (str.search(/^[0-9]+$/) < 0)
return false;
else
return true;
}
// 检查是否为有效的密码，密码只允许由ascii组成，此函数只在修改或注册密码时使用
function checkValidPasswd(str)
{
var reg = /^[\x00-\x7f]+$/;
if (! reg.test(str))
{
return false;
}
if (str.length < 6 || str.length > 16)
{
return false;
}
return true;
}
// 检查是否为有效的suggest
function checkValidSuggest(str)
{
if (str.length > 256)
{
return false;
}
return true;
}
/* Function : check userid by regexp
* Parameter: val string which be checked
*
* return   : combin with 1 2 4.for exmaple 5 respact 1&4
*	1 qq no
*	2 email
*	4 mobile code
*/
function UidCheckFormat(val, utype)
{
ret = 0;
var type=utype;
//if(!utype)
//	type="Q";
pattern1 = /^([0-9]{5,10})$/;
//pattern2 = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-]+)\.([a-zA-Z0-9_])+$/;
//pattern2 = /^([A-Za-z0-9_\-])(([\-.]|[_]+)?([A-Za-z0-9_\-]+))*(@)[1]([A-Za-z0-9_\-])((([-]+)?([A-Za-z0-9_\-]+))?)*((.[A-Za-z]{2,3})?(.[A-Za-z]{2,6}))$/;
pattern2 = /^[a-zA-Z0-9\-_.]{1,}@[a-zA-Z0-9\-_]{1,}(\.[a-zA-Z0-9\-_]{2,}){1,}$/;
pattern3 = /^((1[35][0-9]{9})|(0[1-9][0-9]{9,10}))$/;
if(!type || type=="Q")
{
if(pattern1.test(val))
{
var intVal = parseInt(val,10);
if(intVal > 4294967295)
ret = 0;
else
ret += 1;
}
}
if(!type || type=="E")
{
if(pattern2.test(val)) ret += 2;
}
/*
if(!type || type=="M")
{
if(pattern3.test(val)) ret += 4;
}
*/
return ret;
}
// 检查是否为有效的uin
function checkUin(str, utype)
{
if(UidCheckFormat(str,utype) == 0)
{
return false;
}
else
return true;
}
/*
// 检查是否为有效的uin
function checkUin(str)
{
if (!checkIsInteger(str) || str.length > 15)
{
return false;
}
return true;
}
*/
// 检查是否为中文
function isChn(str)
{
var reg = /^[\u4E00-\u9FA5]+$/;
if(!reg.test(str))
{
return false;
}
return true;
}
// 检查是否为有效的真实姓名，只能含有中文或大写的英文字母
function isValidTrueName(strName)
{
var str = Trim(strName);
//判断是否为全英文大写或全中文，可以包含空格
var reg = /^[A-Z \u4E00-\u9FA5]+$/;
if(reg.test(str))
{
return true;
}
return false;
}
// 检查是否为有效的姓名，只能含有中文或大小写的英文字母
function isValidName(strName)
{
var str = Trim(strName);
//判断是否为全英文大小写或全中文，可以包含空格
var reg = /^[A-Za-z \u4E00-\u9FA5]+$/;
if(reg.test(str))
{
return true;
}
return false;
}
// 检查是否为有效的日期(如 2005-06-01)
function isDate(Date)
{
var datetime = Date;
var year,month,day;
if(Date.search(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/) < 0)
{
return false;
}
year = datetime.substring(0,4);
if (parseInt(year, 10) < 1000)
{
return false;
}
month = datetime.substring(5,7);
if (parseInt(month, 10) < 1 || parseInt(month, 10) > 12)
{
return false;
}
day = datetime.substring(8, 10);
if (parseInt(day, 10) < 1 || parseInt(day, 10) > 31)
{
return false;
}
return true;
}
// 检查是否为有效的email
function checkMail(str)
{
var myReg = /^[_\-\.a-zA-Z0-9]+@([_\-a-zA-Z0-9]+\.)+[a-zA-Z0-9]{2,3}$/;
if(myReg.test(str))
return true;
return false;
}
// 检查是否为有效的固定电话号码
function checkIsTelePhone(str)
{
if(str.search(/^[-0-9]+$/) >= 0)
return true;
else
return false;
}
// 检查是否为有效的银行类型
function checkBankType(str)
{
if (str < 1001 || str > 9999)
{
return false;
}
return true;
}
// 检查是否为有效的开户地区
function checkBankArea(str)
{
if (str < 1 || str > 31)
{
return false;
}
return true;
}
// 检查是否为有效的开户城市
function checkBankCity(str)
{
// 目前只检查是否为整数
if (!checkIsInteger(str))
{
return false;
}
return true;
}
// 检查是否为有效的银行账户
function checkBankId(str)
{
var pattern = /^[0-9]+$/;
//	var pattern = /^[0-9]{4,10}$/;
if(pattern.test(str) && str.length<=32 )
return true;
else
return false;
}
function checkBankIdWithType(banktype, bankid)
{
if(!checkBankId(bankid))
{
window.alert("请输入一个有效的银行卡号，只能包含数字，最大长度为32个数字.");
return false;
}
switch(banktype)
{
case "1001":	// 招行, 11位、12位、13位、16位,17位,18位,20位 、21位,23位
if (bankid.length != 11 && bankid.length != 12 && bankid.length != 13 && bankid.length != 15 && bankid.length!= 16 && bankid.length!= 17 && bankid.length!=18  && bankid.length!= 20 && bankid.length!= 21 && bankid.length!= 23)
{
window.alert("请输入一个有效的银行卡号，中国招商银行的卡号只能为11位、12位、13位、15位、16位、17位、18位、20位、21位或23位");
return false;
}
break;
case "1002":	// 工行, 19位
if (bankid.length != 19 && bankid.length != 16)
{
window.alert("请输入一个有效的银行卡号，中国工商银行的卡号只能为16位或19位");
return false;
}
break;
case "1005":	// 农行
//		if (bankid.length < 11 || bankid.length > 32)
//    	{
//    	   alert("请填写有效的银行卡号。");
//    	   return false;
//        }
break;
case "1003":	//中国建设银行
case "1004":	//上海浦东发展银行
//case "1005":	//中国农业银行
case "1006":	//中国民生银行
case "1008":	//深圳发展银行
case "1009":	//兴业银行
case "1010":	// 深圳商业银行
case "1020":	//中国交通银行
case "1021":	//中信实业银行
case "1022":	//中国光大银行
case "1023":	//农村合作信用社
case "1024":	//上海银行
case "1025":	//华夏银行
case "1026":	//中国银行
case "1027":	//广东发展银行
case "1028":	//深圳商业银行
case "1032":	//北京银行
case "1099":	//其他银行
break;
default:
window.alert("对不起，该银行类型不支持");
return false;
break;
}
return true;
}
// 检查是否为有效的身份证
function checkIdCard(str)
{
if(str.search(/^[0-9]+[Xx]*$/) >= 0  || checkHkId(str))
return true;
else
return false;
}
// 检查18位身份证是否有效
function checkID18(strTemp)
{
var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
var nTemp = 0, i;
if(strTemp.length==18)
{
for(i = 0; i < strTemp.length-1; i ++)
{
nTemp += strTemp.substr(i, 1) * arrInt[i];
}
if(strTemp.substr(17, 1).toUpperCase() != arrCh[nTemp % 11])
{
return false;
}
}
return true;
}
function checkHkId(Id)
{
function GetCode(char)
{
var arrChar = new Array(" ","I","R","A","J","S","B","K","T","C","L","U","D","M","V","E","N","W","F","O","X","G","P","Y","H","Q","Z");
var arrCode = new Array(58,18,27,10,19,28,11,20,29,12,21,30,13,22,31,14,23,32,15,24,33,16,25,34,17,26,35);
for(var i=0;i<arrChar.length; i++)
{
if(arrChar[i]==char)
return arrCode[i];
}
return parseInt(char);
}
Id = Id.toUpperCase();
var   regex=/^[A-Z ]{0,1}[A-Z]{1,1}\d{6}\([0-9A]\)$/;
if(Id.search(regex) < 0)
return false;
if(Id.length == 10)
Id=" " + Id;
var sum=9*GetCode(Id.substr(0,1)) +
8*GetCode(Id.substr(1,1)) +
7*GetCode(Id.substr(2,1)) +
6*GetCode(Id.substr(3,1)) +
5*GetCode(Id.substr(4,1)) +
4*GetCode(Id.substr(5,1)) +
3*GetCode(Id.substr(6,1)) +
2*GetCode(Id.substr(7,1)) ;
var  checkdigit=11-sum%11;
if(checkdigit==10)
checkdigit="A";
if(checkdigit==11)
checkdigit="0";
if (checkdigit!=Id.substr(9,1))
return  false;
return true;
}
// 检查是否为有效的金额(包括小数点后二位)，以元为单位
// 返回值：
// 		true : 正确
//      false: 错误
function checkValidAmount(num)
{
var len = num.length;
// "." 不能出现在第一个字符及最后一个字符
if (num.charAt(0) == '.' || num.charAt(len - 1) == '.')
return false;
// 小数点后最多只能包含两个有效数字(如果“.”号存在，而且它的存在位置在到数第2个以内则认为错误)
var idx = 0;
if ((idx = num.indexOf('.')) >= 0 && idx < len - 1 - 2)
return false;
// 数字开头，可以包含小数点
if(num.search(/^[0-9]+[.]?[0-9]*$/) >= 0)
return true;
else
return false;
return true;
}
// 检查是否为超过充值范围
// 返回值：
//      "true" : 正确
//      其它   : 错误
function checkValidSaveAmount(num)
{
var retVal = "true";
if (num < minSaveAmount)
{
retVal = "对不起，单次充值的金额最少为" + minSaveAmount + "元";
}
else if (num > maxSaveAmount)
{
retVal = "对不起，单次充值的金额最大为" + maxSaveAmount + "元";
}
return retVal;
}
// 根据银行编号获取银行名称
// 输入：
//     bankId : 银行编号
// 输出：
//     正确时返回银行名, 错误时返回 "未定义"
function getBankNameById(bankId)
{
var bankName = "未定义";
switch (bankId)
{
case 1001:        // 招行
case "1001":
bankName = "招商银行"; break;
case 1002:        // 工行
case "1002":
bankName = "中国工商银行"; break;
case 1003:        // 建行
case "1003":
bankName = "中国建设银行"; break;
case 1004:	// 浦发
case "1004":
bankName = "浦东发展银行"; break;
case 1005:	// 农行
case "1005":
bankName = "中国农业银行"; break;
case 1006:
case "1006":
bankName = "中国民生银行"; break;
case 1007:
case "1007":
bankName = "农行国际卡"; break;
case 1008:
case "1008":
bankName = "深圳发展银行"; break;
case 1009:
case "1009":
bankName = "兴业银行"; break;
case 1010:
case "1010":
bankName = "深圳商业银行"; break;
case 1020:
case "1020":
bankName = "中国交通银行"; break;
case 1021:
case "1021":
bankName = "中信实业银行"; break;
case 1022:
case "1022":
bankName = "中国光大银行"; break;
case 1023:
case "1023":
bankName = "农村合作信用社"; break;
case 1024:
case "1024":
bankName = "上海银行"; break;
case 1025:
case "1025":
bankName = "华夏银行"; break;
case 1026:
case "1026":
bankName = "中国银行"; break;
case 1027:
case "1027":
bankName = "广东发展银行"; break;
case 1032:
case "1032":
bankName = "北京银行"; break;
case 1099:
case "1099":
bankName = "其他银行"; break;
default:
bankName = "未定义"; break;
}
return bankName;
}
// 根据卡种编号获取卡种名称
//
function getCardNameByCardId(card_type)
{
var typeName = "未知";
switch (card_type)
{
case 0:		// 未知
typeName = "未知";
break;
case 1:		// 借记卡
typeName = "借记卡";
break;
case 2:		// 信用卡
typeName = "信用卡";
break;
default:
typeName = "未定义";
break;
}
return typeName;
}
// 根据绑定状态编号获取状态名
//
function getBindStatusNameById(bind_status)
{
var typeName = "未定义";
switch (bind_status)
{
case 0:		// 未定义
typeName = "未定义";
break;
case 1:		// 开启
typeName = "开启";
break;
case 2:		// 关闭
typeName = "关闭";
break;
default:
typeName = "未定义";
break;
}
return typeName;
}
// 根据支付类型编号获取支付类型名称
// 输入：
//     typeId : 类型编号
// 输出：
//     正确时返回编号名, 错误时返回"未定义"
function getPayTypeNameByPayTypeId(typeId)
{
var typeName = "未定义";
switch (typeId)
{
case 1:        // c2c
typeName = "C2C付款";
break;
case 2:        // b2c
typeName = "B2C付款";
break;
case 3:        // 充值
typeName = "充值";
break;
case 4:        // 快速交易
typeName = "快速交易";
break;
case 5:        // 收款/付款
typeName = "收款/付款";
break;
case 6:        // 收款/付款
typeName = "收款/付款";
break;
default:
typeName = "未定义";
break;
}
return typeName;
}
// 根据支付类型编号获取支付类型名称
// 输入：
//     typeId : 类型编号
// 输出：
//     正确时返回编号名, 错误时返回"未定义"
/*
C2C    1
B2C    2
FASTPAY     3
收款/付款   4
转账   5
商户结算    6
直扣交易    7
购物券（发行）类 8
*/
function getPayTypeNameByPayTypeId2(typeId)
{
var typeName = "未定义";
switch (typeId)
{
case 1:        // c2c
typeName = "C2C付款";
break;
case 2:        // b2c
typeName = "B2C付款";
break;
case 3:        // 快速交易
typeName = "快速交易";
break;
case 4:        // 收款/付款
typeName = "收款/付款";
break;
case 5:        // 收款/付款
typeName = "转账";
break;
case 6:
typeName = "商户结算";
break;
case 7:
typeName = "直扣交易";
break;
case 8://购物券（发行）类 8
typeName = "购物券（发行）类";
break;
default:
typeName = "未定义";
break;
}
return typeName;
}
// 根据支付类型编号获取交易状态名称
// 输入：
//     typeId : 类型编号
// 输出：
//     正确时返回编号名, 错误时返回"未定义"
function getPayStateNameByPayTypeId(typeid)
{
var typeName = "未定义";
switch (typeid)
{
case 1:        // c2c
typeName = "等待支付";
break;
case 2:        // b2c
typeName = "买方支付成功";
break;
case 3:        // b2c
typeName = "已收到货";
break;
case 4:        // b2c
typeName = "交易结束";
break;
case 5:        // b2c
typeName = "支付失败";
break;
case 6:        // b2c
typeName = "给卖家打款失败";
break;
case 7:        // b2c
typeName = "转入退款";
break;
case 8:
typeName = "等待收款方确认";
break;
case 9:
typeName = "已转账";
break;
case 10:
typeName = "拒绝转账";
break;
case 11:
typeName = "已过期";
break;
default:
typeName = "未定义";
break;
}
return typeName;
}
// 提现状态
// 根据状态编号获取对应的名称
// 输入：
//     typeid : 状态编号
// 输出：
//     正确时返回编号名, 错误时返回 "未定义"
function getDrawingStatusNameById(typeid)
{
var typeName = "未定义";
// 1：成功 2：失败 3：等待付款(未导出) 4：付款中
switch (typeid)
{
case 1:
typeName = "提现成功";
break;
case 2:
typeName = "提现失败";
break;
case 3:
typeName = "已申请";
break;
case 4:
typeName = "已提交银行";
break;
default:
typeName = "未定义";
break;
}
return typeName;
}
// 密码复杂度检查
// 返回值
//  "true" 	检查通过
//	其它	错误信息
function checkPasswd(passwd)
{
// 检查passwd的长度是否大于8
var len = passwd.length;
if (len < 8 || len > 16) {
return "密码长度不能小于8位，不能大于16位";
}
// 检查密码是否为纯数字
if(passwd.search(/^[0-9]*$/) >= 0)
return "密码不能为纯数字，推荐字母(区分大小写)和数字等相结合的方式";
// 检查密码是否为纯小写字母
if(passwd.search(/^[a-z]*$/) >= 0)
return "密码不能为纯字母，推荐字母(区分大小写)和数字等相结合的方式";
// 检查密码是吉伯为纯大写字母
if(passwd.search(/^[A-Z]*$/) >= 0)
return "密码不能为纯字母，推荐字母(区分大小写)和数字等相结合的方式";
return "true";
}
function checkMobileVerifyCode(str)
{
if(str.search(/^[0-9a-zA-Z]{8}$/) < 0)
return false;
return true;
}
function checkAddCode(str)
{
if(str.search(/^[0-9a-zA-Z]{4}$/) < 0)
return false;
return true;
}
// 检查是否为有效的手机号码
function checkIsMobile(str)
{
if(str.search(/^1[35][0-9]{9}$/) >= 0)
return true;
else
return false;
}
// 跳转
function jump(surl)
{
//window.alert("跳转链接:" + surl);
surl = surl.replace(/=&/g, '%3d&');         // 对于含=的值，需要转换成%3d
surl = surl.replace(/==&/g, '%3d&');        // 对于含=的值，需要转换成%3d
// 如果没有指定跳转链接，则后退
if (surl == "back")
{
history.back(-1);
return;
}
else if (surl == "close")
{
self.close();
return;
}
// 跳转到指定链接
//window.location.href= surl;
location.replace(surl)
return;
}
/*
==================================================================
LTrim(string):去除左边的空格
==================================================================
*/
function LTrim(str)
{
var whitespace = new String(" \t\n\r");
var s = new String(str);
if (whitespace.indexOf(s.charAt(0)) != -1)
{
var j=0, i = s.length;
while (j < i && whitespace.indexOf(s.charAt(j)) != -1)
{
j++;
}
s = s.substring(j, i);
}
return s;
}
/*
==================================================================
RTrim(string):去除右边的空格
==================================================================
*/
function RTrim(str)
{
var whitespace = new String(" \t\n\r");
var s = new String(str);
if (whitespace.indexOf(s.charAt(s.length-1)) != -1)
{
var i = s.length - 1;
while (i >= 0 && whitespace.indexOf(s.charAt(i)) != -1)
{
i--;
}
s = s.substring(0, i+1);
}
return s;
}
/*
==================================================================
Trim(string):去除前后空格
==================================================================
*/
function TrimInputValue(inpobj)
{
inpobj.value = Trim(inpobj.value);
}
function Trim(str)
{
return RTrim(LTrim(str));
}
/*
*剔除form中输入框的前后空格
*/
function TrimForm(form)
{
if(!form)
{
return;
}
for(var i=0; i < form.length; i++)
{
if(form.elements[i].type.toUpperCase() == "TEXT")
{
form.elements[i].value = Trim(form.elements[i].value);
}
}
}
/*
==================================================================
省份编码及城市编码处理
==================================================================
*/
var where = new Array(31);
function comefrom(loca, locacity, locaid, locacityids)
{
this.loca = loca;
this.locacity = locacity;
this.locaid = locaid;
this.locacityids = locacityids;
}
where[0] = new comefrom("请选择省份名","请选择城市名", "", "");
where[1] = new comefrom("北京", "北京", "1", "10");
where[2] = new comefrom("上海", "上海", "2", "21");
where[3] = new comefrom("天津", "天津", "3", "22");
where[4] = new comefrom("重庆", "重庆", "4", "23");
where[5] = new comefrom("河北", "石家庄|张家口|承德|秦皇岛|唐山|廊坊|保定|沧州|衡水|邢台|邯郸", "5", "311|313|314|335|315|316|312|317|318|319|310");
where[6] = new comefrom("山西", "太原|大同|朔州|阳泉|长治|晋城|忻州|离石|榆次|临汾|运城", "6", "351|352|349|353|355|356|350|358|354|357|359");
where[7] = new comefrom("内蒙古", "呼和浩特(*)|包头|乌海|赤峰|海拉尔|乌兰浩特|通辽|锡林浩特|集宁|东胜|临河|阿拉善左旗", "7", "471|472|473|476|470|482|475|479|474|477|478|483");
where[8] = new comefrom("辽宁", "沈阳(*)|朝阳|阜新|铁岭|抚顺|本溪|辽阳|鞍山|丹东|大连|营口|盘锦|锦州|葫芦岛", "8", "24|421|418|410|413|414|419|412|415|411|417|427|416|429");
where[9] = new comefrom("吉林", "长春(*)|白城|松原|吉林|四平|辽源|通化|白山|延吉", "9", "431|436|438|432|434|437|435|439|433");
where[10] = new comefrom("黑龙江", "哈尔滨(*)|齐齐哈尔|黑河|大庆|伊春|鹤岗|佳木斯|双鸭山|七台河|鸡西|牡丹江|绥化|加格达奇", "10", "451|452|456|459|458|468|454|469|464|467|453|455|457");
where[11] = new comefrom("江苏", "南京(*)|苏州|徐州|连云港|宿迁|淮安|盐城|扬州|泰州|南通|镇江|常州|无锡", "11", "25|512|516|518|527|517|515|514|523|513|511|519|510");
where[12] = new comefrom("浙江", "杭州(*)|湖州|嘉兴|舟山|宁波|绍兴|金华|台州|温州|丽水|衢州", "12", "571|572|573|580|574|575|579|576|577|578|570");
where[13] = new comefrom("安徽", "合肥(*)|宿州|淮北|阜阳|蚌埠|淮南|滁州|马鞍山|芜湖|铜陵|安庆|黄山|六安|巢湖|贵池|宣城", "13", "551|557|561|558|552|554|550|555|553|562|556|559|564|565|566|563");
where[14] = new comefrom("福建", "福州(*)|南平|三明|莆田|泉州|厦门|漳州|龙岩|宁德|福安|邵武|石狮|永安|武夷山", "14", "591|599|598|594|595|592|596|597|593|5930|5990|5950|5980|5991");
where[15] = new comefrom("江西", "南昌(*)|九江|景德镇|鹰潭|新余|萍乡|赣州|上饶|临川|宜春|吉安|抚州", "15", "791|792|798|701|790|799|797|793|794|795|796|7940");
where[16] = new comefrom("山东", "济南(*)|聊城|德州|东营|淄博|潍坊|烟台|威海|青岛|日照|临沂|枣庄|济宁|泰安|莱芜|滨州|菏泽", "16", "531|635|534|546|533|536|535|631|532|633|539|632|537|538|634|543|530");
where[17] = new comefrom("河南", "郑州(*)|三门峡|洛阳|焦作|新乡|鹤壁|安阳|濮阳|开封|商丘|许昌|漯河|平顶山|南阳|信阳|济源|周口|驻马店", "17", "371|398|379|391|373|392|372|393|378|370|374|395|375|377|376|3910|394|396");
where[18] = new comefrom("湖北", "武汉(*)|十堰|襄樊|荆门|孝感|黄冈|鄂州|黄石|咸宁|荆州|宜昌|恩施|仙桃|潜江", "18", "27|719|710|724|712|713|711|714|715|716|717|718|728|7281");
where[19] = new comefrom("湖南", "长沙(*)|张家界|常德|益阳|岳阳|株洲|湘潭|衡阳|郴州|永州|邵阳|怀化|娄底|吉首", "19", "731|744|736|737|730|733|732|734|735|746|739|745|738|743");
where[20] = new comefrom("广东", "广州(*)|深圳|清远|韶关|河源|梅州|潮州|汕头|揭阳|汕尾|惠州|东莞|珠海|中山|江门|佛山|茂名|湛江|阳江|云浮|肇庆", "20", "20|755|763|751|762|753|768|754|663|660|752|769|756|760|750|757|668|759|662|766|758");
where[21] = new comefrom("广西", "南宁(*)|桂林|柳州|贺州|玉林|钦州|北海|防城港|百色|河池|贵港|梧州", "21", "771|773|772|774|775|777|779|770|776|778|7750|7740");
where[22] = new comefrom("海南", "海口(*)|三亚|儋州", "22", "898|899|890");
where[23] = new comefrom("四川", "成都(*)|广元|绵阳|德阳|南充|广安|遂宁|内江|乐山|自贡|泸州|宜宾|攀枝花|巴中|达州|资阳|雅安|西昌", "23", "28|839|816|838|817|826|825|832|833|813|830|831|812|827|818|8320|835|834");
where[24] = new comefrom("贵州", "贵阳(*)|六盘水|遵义|毕节|铜仁|安顺|凯里|都匀|兴义", "24", "851|858|852|857|856|853|855|854|859");
where[25] = new comefrom("云南", "昆明(*)|曲靖|玉溪|丽江|昭通|思茅|临沧|保山|潞西|泸水|中甸|大理|楚雄|个旧|文山|景洪|红河", "25", "871|874|877|888|870|879|883|875|692|886|887|872|878|873|876|691|8730");
where[26] = new comefrom("西藏", "拉萨(*)|那曲|昌都|林芝|乃东|日喀则|噶尔", "26", "891|896|895|894|893|892|897");
where[27] = new comefrom("陕西", "西安(*)|延安|铜川|渭南|咸阳|宝鸡|汉中|榆林|商洛|安康", "27", "29|911|919|913|910|917|916|912|914|915");
where[28] = new comefrom("甘肃", "兰州(*)|嘉峪关|白银|天水|酒泉|张掖|金昌|庆阳|平凉|定西|陇南|临夏|武威", "28", "931|937|943|938|9370|936|935|934|933|932|939|930");
where[29] = new comefrom("宁夏", "银川(*)|石嘴山|吴忠|固原", "29", "951|952|953|954");
where[30] = new comefrom("青海", "西宁(*)|平安|海晏|共和|同仁|玛沁|玉树|德令哈", "30", "971|972|970|974|973|975|976|977");
where[31] = new comefrom("新疆", "乌鲁木齐(*)|克拉玛依|石河子|喀什|阿克苏|和田|吐鲁番|哈密|阿图什|博乐|昌吉|库尔勒|伊犁|奎屯|塔城|阿勒泰", "31", "991|990|993|998|997|903|995|902|908|909|994|996|999|992|901|906");
function CCookie()
{
this.SetCookie = setCookie;
this.GetCookie = getCookie;
this.DelCookie = deleteCookie;
}
function getExpDate(days, hours, minutes)
{
var expDate = new Date( );
if (typeof days == "number" && typeof hours == "number" &&
typeof hours == "number")
{
expDate.setDate(expDate.getDate( ) + parseInt(days));
expDate.setHours(expDate.getHours( ) + parseInt(hours));
expDate.setMinutes(expDate.getMinutes( ) + parseInt(minutes));
return expDate.toGMTString( );
}
}
// utility function called by getCookie( )
function getCookieVal(offset)
{
var endstr = document.cookie.indexOf (";", offset);
if (endstr == -1)
{
endstr = document.cookie.length;
}
return unescape(document.cookie.substring(offset, endstr));
}
// primary function to retrieve cookie by name
function getCookie(name)
{
var arg = name + "=";
var alen = arg.length;
var clen = document.cookie.length;
var i = 0;
while (i < clen)
{
var j = i + alen;
if (document.cookie.substring(i, j) == arg)
{
return getCookieVal(j);
}
i = document.cookie.indexOf(" ", i) + 1;
if (i == 0) break;
}
return "";
}
// store cookie value with optional details as needed
function setCookie(name, value, expires, path, domain, secure)
{
document.cookie = name + "=" + escape (value) +
((expires) ? "; expires=" + expires : "") +
((path) ? "; path=" + path : "") +
((domain) ? "; domain=" + domain : "") +
((secure) ? "; secure" : "");
}
// remove the cookie by setting ancient expiration date
function deleteCookie(name,path,domain)
{
var exp = new Date();
exp.setTime (exp.getTime() - 1);
if (getCookie(name)) {
document.cookie = name + "=" +
((path) ? "; path=" + path : "") +
((domain) ? "; domain=" + domain : "") +
"; expires="+exp.toGMTString();
}
}
/* ----------------------------------------------------------------
* 封装XMLHttpRequest对象,提供一致的接口供系统其他模块使用
* CHttpRequest.js
* gaussgao 20060518
* ----------------------------------------------------------------
XMLHttpRequest 对象方法 方法 描述
abort()
停止当前请求
getAllResponseHeaders()
作为字符串返问完整的headers
getResponseHeader("headerLabel")
作为字符串返问单个的header标签
open("method","URL"[,asyncFlag[,"userName"[, "password"]]])
设置未决的请求的目标 URL, 方法, 和其他参数
send(content)
发送请求
setRequestHeader("label", "value")
设置header并和请求一起发送
XMLHttpRequest 对象属性 属性 描述
onreadystatechange
状态改变的事件触发器
readyState
对象状态(integer):
0 = 未初始化
1 = 读取中
2 = 已读取
3 = 交互中
4 = 完成
responseText 	1
服务器进程返回数据的文本版本
responseXML 	2
服务器进程返回数据的兼容DOM的XML文档对象
status
服务器返回的状态码, 如：404 = "文件末找到" 、200 ="成功"
statusText
服务器返回的状态文本信息
*/
/*功能:创建对象,并设置属性
*/
function CHttpRequest(fCallback,mode,restype)
{
//创建对象
this.m_HttpRequest = createXMLHttpRequest();
//属性
this.m_bAsyncFlag = mode;
if( restype )
this.m_iResType	= restype;
else
this.m_iResType	= 2;	//responseXml
//方法
this.Post = CHttpRequest_Post;
this.Get  = CHttpRequest_Get;
this.m_fCallBack = fCallback;
}
function CHttpRequest_Get(dst,para)
{
var now = new Date();
var rnd = Math.floor(Math.random() * 100000);
var ptm = now.getSeconds().toString()+rnd.toString()+now.getMinutes().toString();
para += "&ptm="+ptm.toString();
var http = this;
function tm_back()
{
if(http.m_HttpRequest.readyState == 4)
{
if(http.m_HttpRequest.status == 200)
{
if(http.m_iResType == 1)
http.m_fCallBack(4,200,http.m_HttpRequest.responseText);
else
http.m_fCallBack(4,200,http.m_HttpRequest.responseXML);
}
else
{
http.m_fCallBack(4,http.m_HttpRequest.status,null);
}
}
else
{
http.m_fCallBack(http.m_HttpRequest.readyState,null,null);
}
}
this.m_HttpRequest.onreadystatechange = tm_back;
this.m_HttpRequest.open("GET",dst+"?"+para,this.m_bAsyncFlag);
this.m_HttpRequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=GB2312");
this.m_HttpRequest.setRequestHeader("Cache-Control","no-cache");
this.m_HttpRequest.send("");
}
function CHttpRequest_Post(dst,para)
{
var http = this;
function tm_back()
{
if(http.m_HttpRequest.readyState == 4)
{
if(http.m_HttpRequest.status == 200)
{
if(http.m_iResType == 1)
http.m_fCallBack(4,200,http.m_HttpRequest.responseText);
else
http.m_fCallBack(4,200,http.m_HttpRequest.responseXML);
}
else
{
http.m_fCallBack(4,http.m_HttpRequest.status,null);
}
}
else
{
http.m_fCallBack(http.m_HttpRequest.readyState,null,null);
}
}
this.m_HttpRequest.onreadystatechange = tm_back;
this.m_HttpRequest.open("POST",dst,this.m_bAsyncFlag);
this.m_HttpRequest.setRequestHeader("Content-Length",para.length);
this.m_HttpRequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
//	this.m_HttpRequest.setRequestHeader("Content_Type","application/x-www-form-urlencoded;charset=GB2312");
//this.m_HttpRequest.setRequestHeader("Content_Type","text/xml;charset=GB2312");
this.m_HttpRequest.send(para);
}
function getXMLPrefix()
{
if (getXMLPrefix.prefix)
return getXMLPrefix.prefix;
var prefixes = ["MSXML3", "MSXML2", "MSXML", "Microsoft"];
var obj, obj2;
for(var i = 0; i < prefixes.length; i++)
{
try
{
//Attempt to create an XmlHttp object using the current prefix
obj = new ActiveXObject(prefixes[i] + ".XmlHttp");
return getXMLPrefix.prefix = prefixes[i];
}
catch (ex)
{};
}
throw new Error("您没有安装XML解析器,请使用INTERNET EXPLORE 5以上的浏览器.");
}
function createXMLHttpRequest()
{
try
{
// Attempt to create it "the Mozilla way"
if (window.XMLHttpRequest)
{
return new XMLHttpRequest();
}
// Guess not - now the IE way
if (window.ActiveXObject)
{
return new ActiveXObject(getXMLPrefix() + ".XmlHttp");
}
}
catch (ex)
{
alert(ex.message);
};
return false;
}
//examples
/*-------------------------------------------------------
//回调函数:执行状态,返回码,返回内容
function CallBack_Test(state,sts,str)
{
switch(state)
{
case 0://initting
case 1://reading
case 2://readed
case 3://interact
alert(state);
break;
case 4://complete
switch(sts)
{
case 200://success
alert(str);break;
case 404://notfound
alert(sts);
default:
break;
}
default:
break;
}
}
//执行过程
function GetTest()
{
var http = new CHttpRequest(CallBack_Test,false,1);
http.Get("/test.xml","a=1&b=2");
}
GetTest();
-------------------------------------------------------------*/
/*
* 处理:设计到请求页面，等待页面，结果页面的交替
*/
function CTrans()
{
this.CommitByForm = CTrans_CommitByForm;
this.CommitByStr = CTrans_CommitByString;
this.Callback = CTrans_Callback;
this.Disable = CTrans_Disable;
this.ClearInput = CTrans_ClearInput;
this.ShowWaiting = CTrans_ShowWaiting;
this.ShowComplete = CTrans_ShowComplete;
this.GenRequest = CTrans_GenReqData;
this.DoResult=null;
this.sReqDat = "";
this.sActUrl = "";
this.bDoing	= false;
this.bNoDisable=false;
this.objCanvan = 0;
}
function CTrans_CommitByString(str,acturl,bNoDisable, DoResult)
{
var reqdat = str;
this.sReqDat = reqdat;
this.sActUrl = acturl;
this.bNoDisable=bNoDisable;
//	alert(DoResult);
if(DoResult){
//alert("ture");
this.DoResult=DoResult;
var http = new CHttpRequest(this.Callback,true,2);
}else{
this.DoResult=null;
var http = new CHttpRequest(this.Callback,false,2);
}
this.Disable(true);
http.Get(acturl,reqdat);
}
function CTrans_CommitByForm(frm, DoResult, bNoDisable,method)
{
this.sReqDat = this.GenRequest(frm, method);
this.sActUrl = frm.action;
this.bNoDisable=bNoDisable;
if(DoResult){
this.DoResult=DoResult;
var http = new CHttpRequest(this.Callback,true,2);
}else{
this.DoResult=null;
var http = new CHttpRequest(this.Callback,false,2);
}
if(this.bDoing)
return false;
this.bDoing = true;
this.Disable(true);
if(method && method=="post"){
http.Post(frm.action,this.sReqDat);
}else{
http.Get(frm.action,this.sReqDat);
}
//http.Post(frm.action,reqdat);
//frm.submit();
return true;
}
function CTrans_ShowWaiting()
{
//this.Disable(true);
//更改这里，在等待的时候显示友好界面
}
function CTrans_ShowComplete()
{
//this.Disable(false);
if(this.m_iRetcode == 0)
{
;
}
else
{
if(this.m_iRetcode == 1)
{
alert("您未登录或登录超时，请先登录财付通系统。");
top.location.href="/zft/login.shtml?u1=" + encodeURIComponent(window.location.href);
}
else
{
if(this.m_sRetmsg=="")
this.m_sRetmsg="系统繁忙";
var sLoadUrl=window.document.URL.replace(/#$/g, "").split("?")[0];
if(sLoadUrl.indexOf("pay_new.cgi")<0 && sLoadUrl.indexOf("pay.cgi")<0 && sLoadUrl.indexOf("register_1.shtml")<0)
{
if(this.m_sRetmsg.indexOf("1197")>=0){
if(confirm(this.m_sRetmsg+"\n是否重新发送激活邮件到您的邮箱")){
g_CReSendEmail.Send(this.m_xmlRes.GetValue("u"), "2");
}
}else{
alert(this.m_sRetmsg);
}
if(this.m_sRetmsg.indexOf("密码")>=0 && this.m_sRetmsg.indexOf("密码保护")<0 &&  this.m_sRetmsg.indexOf("1064")<0)
{
if(sLoadUrl.indexOf("register_1.shtml")>=0 && sLoadUrl != "" && this.sReqDat != "")
sLoadUrl += "?" + this.sReqDat;
else
sLoadUrl = "";
if(sLoadUrl=="")
window.location.reload();
else
{
window.location.href=sLoadUrl;
}
}
}
}
}
}
function CTrans_ClearInput()
{
var coll = document.all;
if (coll!=null)
{
for (i=0; i < coll.length; i++ )
if((coll.item(i).tagName.toUpperCase() == 'INPUT' ) ||
(coll.item(i).tagName.toUpperCase() == 'TEXTAREA' )||
(coll.item(i).tagName.toUpperCase() == 'IMG' )||
(coll.item(i).tagName.toUpperCase() == 'OBJECT' ))
{
if((coll.item(i).name) && coll.item(i).type)
{
if( (coll.item(i).name != "") &&
(coll.item(i).type.toUpperCase() != "HIDDEN") &&
(coll.item(i).type.toUpperCase() != 'SUBMIT') &&
(coll.item(i).type.toUpperCase() != 'BUTTON') )
coll.item(i).value = "" ;
}
}
}
}
function CTrans_Disable(flag)
{
if(this.bNoDisable)
return;
var coll = document.all;
if (coll!=null)
{
for (i=0; i < coll.length; i++ )
if((coll.item(i).tagName.toUpperCase() == 'INPUT' ) ||
(coll.item(i).tagName.toUpperCase() == 'TEXTAREA' )||
(coll.item(i).tagName.toUpperCase() == 'IMG' )||
(coll.item(i).tagName.toUpperCase() == 'OBJECT' ))
{
coll.item(i).disabled = flag ;
}
}
}
/*
* 定义全局变量g_CCftUser
*/
var g_CTrans =null ;
if(!g_CTrans)
g_CTrans = new CTrans();
function CTrans_Callback(state,sts,str)
{
g_CTrans.bDoing	= false;
switch(state)
{
case 0://initting
//		break;
case 1://reading
//		break;
case 2://readed
//		break;
case 3://interact
g_CTrans.ShowWaiting();
break;
case 4://complete
g_CTrans.Disable(false);
switch(sts)
{
case 200://success
g_CTrans.m_xmlRes = new CXml(str);
g_CTrans.m_iRetcode = parseInt(g_CTrans.m_xmlRes.GetValue("retcode"),10);
g_CTrans.m_sRetmsg = g_CTrans.m_xmlRes.GetValue("retmsg");
g_CTrans.m_sTid = g_CTrans.m_xmlRes.GetValue("tid");
if(!g_CTrans.DoResult)
g_CTrans.ShowComplete();
else
g_CTrans.DoResult(g_CTrans.m_xmlRes);
break;
case 404://notfound
default:
g_CTrans.m_iRetcode = 99;
g_CTrans.m_sRetmsg = "系统故障：访问的页面不存在！";
g_CTrans.m_xmlRes = new CXml("<?xml version=\"1.0\" encoding=\"gb2312\" ?><root><retcode>"+sts+"</retcode> <retmsg>["+sts+"]系统故障：访问的页面出错</retmsg> </root>");
if(!g_CTrans.DoResult)
g_CTrans.ShowComplete();
else
g_CTrans.DoResult(g_CTrans.m_xmlRes);
break;
}
default:
break;
}
}
function CTrans_GenReqData(form, method)
{
var reqdat = "";
var first_flag = true;
var bEncodeGb = false;
if(method && method=="post")
bEncodeGb = true;
for(var i=0; i < form.length; i++)
{
if(form.elements[i].type.toUpperCase() == "CHECKBOX" && !form.elements[i].checked)
continue;
if(form.elements[i].type.toUpperCase() == "RADIO" && !form.elements[i].checked)
continue;
if(form.elements[i].name!="" && form.elements[i].value !=""){
if(first_flag){
first_flag = false;
if(bEncodeGb)
reqdat = reqdat + form.elements[i].name + "="+ encodeURIComponent(form.elements[i].value);
else
reqdat = reqdat + form.elements[i].name + "="+ (form.elements[i].value).replace(/%/g,"%25").replace(/\r\n/g,"%0D%0A").replace(/=/g,"%3D").replace(/&/g,"%26").replace(/\?/g,"%3F").replace(/\+/g,"%2B");
}else{
if(bEncodeGb)
reqdat = reqdat + "&" + form.elements[i].name + "="+ encodeURIComponent(form.elements[i].value);
else
reqdat = reqdat + "&" + form.elements[i].name + "=" + (form.elements[i].value).replace(/%/g,"%25").replace(/\r\n/g,"%0D%0A").replace(/=/g,"%3D").replace(/&/g,"%26").replace(/\?/g,"%3F").replace(/#/g,"%23").replace(/\+/g,"%2B");
}
}
}
//alert(reqdat);
return reqdat;
}
/*
* 操作XML文件，避免不存在的节点抛出异常
*/
function CXml(indat)
{
//	alert(typeof(indat));
if(typeof(indat) == "string")
{
    if("undefined" != typeof(DOMParser))
    {
        var parser = new DOMParser();
        this.m_xmlObj = parser.parseFromString(indat, "application/xml");
    }
    else if(window.ActiveXObject)
    {
        this.m_xmlObj = new ActiveXObject("Microsoft.XMLDOM");
        this.m_xmlObj.async = false;
        this.m_xmlObj.loadXML(indat);
        //while(obj.readyState != 4) {};//
    }
    else
    {
        var url = "data:text/xml;charset=utf-8," + encodeURIComponent(indat);
        var request = new XMLHttpRequest( );
        request.open("GET", url, false);
        request.send(null);
        this.m_xmlObj = request.responseXML;
        request = null;
    }
}
else if(typeof(indat) == "object")
{
this.m_xmlObj = indat;
}
else
{
throw new Error("CHttpRequest 返回一个不能识别的对象.");
}
this.GetValue = CXml_GetValue;
this.GetNodes = CXml_GetNodes;
this.GetChildValue = CXml_GetChildValue;
this.GetChildNodes = CXml_GetChildNodes;
this.GetParentNode = CXml_GetParentNode;
this.GetAttValue = CXml_GetAttValue;
this.GetNodeValue = CXml_GetNodeValue;
}
function CXml_GetValue(node_name)
{
var res;
try
{
res = this.m_xmlObj.getElementsByTagName(node_name)[0].firstChild.data
}
catch(ex)
{
res = "";
}
return res;
}
function CXml_GetNodes(node_name)
{
var res;
try
{
res = this.m_xmlObj.getElementsByTagName(node_name);
}
catch(ex)
{
res = "";
}
return res;
}
function CXml_GetChildNodes(node,node_name)
{
var res;
try
{
res = node.getElementsByTagName(node_name);
}
catch(ex)
{
res = "";
}
return res;
}
function CXml_GetChildValue(node, child_node_name)
{
var res;
try
{
res = node.getElementsByTagName(child_node_name)[0].firstChild.data
}
catch(ex)
{
res = "";
}
return res;
}
function CXml_GetParentNode(node)
{
var res;
try
{
res = node.parentNode;
}
catch(ex)
{
res = "";
}
return res;
}
function CXml_GetAttValue(node, att_name)
{
var res = "";
try
{
for(var i=0; i<node.attributes.length; i++)
{
if(node.attributes[i].nodeName == att_name)
{
res = node.attributes[i].nodeValue;
break;
}
}
}
catch(ex)
{
res = "";
}
return res;
}
function CXml_GetNodeValue(node)
{
var res;
try
{
res = node.firstChild.data
}
catch(ex)
{
res = "";
}
return res;
}
/*
* 获取自身URL的参数内容和参数值
* CSelfUrl.js
*/
function CSelfUrl()
{
this.m_sUrl = "";
this.m_sQueryString = "";
this.m_sDomin = "";
this.Init = CSelfUrl_Init;
this.GetPara = CSelfUrl_GetPara;
this.ParsePara = CSelfUrl_ParsePara;
this.GetFormString = CSelfUrl_GetFormString;
this.GetCookie = CSelfUrl_GetCookie;
}
function CSelfUrl_Init()
{
this.m_sUrl = window.document.URL.replace(/#$/g, "");
this.m_sQueryString = this.m_sUrl.split("?")[1];
this.m_sDomin = this.m_sUrl.split("?")[0];
}
function CSelfUrl_GetFormString(form)
{
var reqdat = "";
var first_flag = true;
for(var i=0; i < form.length; i++)
{
if(form.elements[i].type.toUpperCase() == "CHECKBOX" && !form.elements[i].checked)
{
continue;
}
if(form.elements[i].type.toUpperCase() == "RADIO" && !form.elements[i].checked)
{
continue;
}
if(form.elements[i].name == "tm")
continue;
if(form.elements[i].name!="")
{
if(first_flag)
{
first_flag = false;
//reqdat = reqdat + form.elements[i].name + "="+ (form.elements[i].value).replace("\r\n","%0D%0A");
reqdat = reqdat + form.elements[i].name + "="+ encodeURIComponent(form.elements[i].value)
}
else
{
//reqdat = reqdat + "&" + form.elements[i].name + "="+ (form.elements[i].value).replace("\r\n","%0D%0A");
reqdat = reqdat + "&" + form.elements[i].name + "="+ encodeURIComponent(form.elements[i].value)
}
}
}
//	alert(reqdat);
return reqdat;
}
function CSelfUrl_GetPara(para_name)
{
var queryStr;
var named;
this.Init();
queryStr = this.m_sQueryString;
if(queryStr == null)
{
queryStr = "";
}
named = queryStr.split("&");
var len = named.length;
for (var i = 0; i < len; ++i)
{
//alert(named[i]);
var nv = named[i];
var j = nv.indexOf("=");
if (j == -1)
continue;
var varName = nv.substring(0, j);
var varVal = nv.substring(j+1, nv.length);
if (varName==para_name)
{
return decodeURIComponent(varVal);
}
}
return "";
}
function CSelfUrl_ParsePara()
{
var queryStr;
var named;
this.Init();
queryStr = this.m_sQueryString;
if(queryStr == null)
{
queryStr = "";
}
named = queryStr.split("&");
var len = named.length;
for (var i = 0; i < len; ++i)
{
//alert(named[i]);
var nv = named[i];
var j = nv.indexOf("=");
if (j == -1)
continue;
var varName = nv.substring(0, j);
var varVal = nv.substring(j+1, nv.length);
ele = document.getElementById(varName);
if(!ele)
continue;
if (ele.tagName.toUpperCase() == 'TEXTAREA' || ele.tagName.toUpperCase() == 'INPUT' || ele.tagName.toUpperCase() == 'SELECT')
{
if(ele.type.toUpperCase() == "RADIO")
{
ele = document.getElementsByName(varName);
if(!ele)
continue;
for(var j=0; j<ele.length; j++)
{
if(ele[j].value == decodeURIComponent(varVal))
{
ele[j].checked = true;
break;
}
}
}
else
ele.value = decodeURIComponent(varVal);
}
else
{
ele.innerHTML = decodeURIComponent(varVal);
}
}
}
function CSelfUrl_GetCookie(cookiename)
{
var allcookies=document.cookie;
var pos=allcookies.indexOf(cookiename+"=") ;
if (pos!=-1)
{
var start=pos +cookiename.length+1 //从现有的cookie(字符串)中提现有值,(cookie名+"="的长度)
var end=allcookies.indexOf(";",start);
if (end==-1)
end=allcookies.length;
var value= allcookies.substring(start,end); //提取值
value=unescape(value); //解码
return value;
}
return "";
}
var g_CSelfUrl = null ;
if(!g_CSelfUrl)
g_CSelfUrl = new CSelfUrl();
/*************************************************
* md5相关处理函数
*************************************************/
var hexcase = 1;
var b64pad = "";
var chrsz = 8;
var mode = 32;
function preprocess(form)
{
var str = "";
str += form.verifycode.value;
str = str.toUpperCase();
form.p.value = md5(md5_3(form.p.value)+str);
return true;
}
function md5_3(s)
{
var tmp = new Array;
tmp = core_md5(str2binl(s), s.length * chrsz);
tmp = core_md5(tmp, 16 * chrsz);
tmp = core_md5(tmp, 16 * chrsz);
return binl2hex(tmp);
}
function md5(s)
{
return hex_md5(s);
}
function hex_md5(s)
{
return binl2hex(core_md5(str2binl(s), s.length * chrsz));
}
function b64_md5(s)
{
return binl2b64(core_md5(str2binl(s), s.length * chrsz));
}
function str_md5(s)
{
return binl2str(core_md5(str2binl(s), s.length * chrsz));
}
function hex_hmac_md5(key, data)
{
return binl2hex(core_hmac_md5(key, data));
}
function b64_hmac_md5(key, data)
{
return binl2b64(core_hmac_md5(key, data));
}
function str_hmac_md5(key, data)
{
return binl2str(core_hmac_md5(key, data));
}
function md5_vm_test()
{
return hex_md5("abc") == "900150983cd24fb0d6963f7d28e17f72";
}
function core_md5(x, len)
{
x[len >> 5] |= 0x80 << ((len) % 32);
x[(((len + 64) >>> 9) << 4) + 14] = len;
var a = 1732584193;
var b =  - 271733879;
var c =  - 1732584194;
var d = 271733878;
for (var i = 0; i < x.length; i += 16)
{
var olda = a;
var oldb = b;
var oldc = c;
var oldd = d;
a = md5_ff(a, b, c, d, x[i + 0], 7,  - 680876936);
d = md5_ff(d, a, b, c, x[i + 1], 12,  - 389564586);
c = md5_ff(c, d, a, b, x[i + 2], 17, 606105819);
b = md5_ff(b, c, d, a, x[i + 3], 22,  - 1044525330);
a = md5_ff(a, b, c, d, x[i + 4], 7,  - 176418897);
d = md5_ff(d, a, b, c, x[i + 5], 12, 1200080426);
c = md5_ff(c, d, a, b, x[i + 6], 17,  - 1473231341);
b = md5_ff(b, c, d, a, x[i + 7], 22,  - 45705983);
a = md5_ff(a, b, c, d, x[i + 8], 7, 1770035416);
d = md5_ff(d, a, b, c, x[i + 9], 12,  - 1958414417);
c = md5_ff(c, d, a, b, x[i + 10], 17,  - 42063);
b = md5_ff(b, c, d, a, x[i + 11], 22,  - 1990404162);
a = md5_ff(a, b, c, d, x[i + 12], 7, 1804603682);
d = md5_ff(d, a, b, c, x[i + 13], 12,  - 40341101);
c = md5_ff(c, d, a, b, x[i + 14], 17,  - 1502002290);
b = md5_ff(b, c, d, a, x[i + 15], 22, 1236535329);
a = md5_gg(a, b, c, d, x[i + 1], 5,  - 165796510);
d = md5_gg(d, a, b, c, x[i + 6], 9,  - 1069501632);
c = md5_gg(c, d, a, b, x[i + 11], 14, 643717713);
b = md5_gg(b, c, d, a, x[i + 0], 20,  - 373897302);
a = md5_gg(a, b, c, d, x[i + 5], 5,  - 701558691);
d = md5_gg(d, a, b, c, x[i + 10], 9, 38016083);
c = md5_gg(c, d, a, b, x[i + 15], 14,  - 660478335);
b = md5_gg(b, c, d, a, x[i + 4], 20,  - 405537848);
a = md5_gg(a, b, c, d, x[i + 9], 5, 568446438);
d = md5_gg(d, a, b, c, x[i + 14], 9,  - 1019803690);
c = md5_gg(c, d, a, b, x[i + 3], 14,  - 187363961);
b = md5_gg(b, c, d, a, x[i + 8], 20, 1163531501);
a = md5_gg(a, b, c, d, x[i + 13], 5,  - 1444681467);
d = md5_gg(d, a, b, c, x[i + 2], 9,  - 51403784);
c = md5_gg(c, d, a, b, x[i + 7], 14, 1735328473);
b = md5_gg(b, c, d, a, x[i + 12], 20,  - 1926607734);
a = md5_hh(a, b, c, d, x[i + 5], 4,  - 378558);
d = md5_hh(d, a, b, c, x[i + 8], 11,  - 2022574463);
c = md5_hh(c, d, a, b, x[i + 11], 16, 1839030562);
b = md5_hh(b, c, d, a, x[i + 14], 23,  - 35309556);
a = md5_hh(a, b, c, d, x[i + 1], 4,  - 1530992060);
d = md5_hh(d, a, b, c, x[i + 4], 11, 1272893353);
c = md5_hh(c, d, a, b, x[i + 7], 16,  - 155497632);
b = md5_hh(b, c, d, a, x[i + 10], 23,  - 1094730640);
a = md5_hh(a, b, c, d, x[i + 13], 4, 681279174);
d = md5_hh(d, a, b, c, x[i + 0], 11,  - 358537222);
c = md5_hh(c, d, a, b, x[i + 3], 16,  - 722521979);
b = md5_hh(b, c, d, a, x[i + 6], 23, 76029189);
a = md5_hh(a, b, c, d, x[i + 9], 4,  - 640364487);
d = md5_hh(d, a, b, c, x[i + 12], 11,  - 421815835);
c = md5_hh(c, d, a, b, x[i + 15], 16, 530742520);
b = md5_hh(b, c, d, a, x[i + 2], 23,  - 995338651);
a = md5_ii(a, b, c, d, x[i + 0], 6,  - 198630844);
d = md5_ii(d, a, b, c, x[i + 7], 10, 1126891415);
c = md5_ii(c, d, a, b, x[i + 14], 15,  - 1416354905);
b = md5_ii(b, c, d, a, x[i + 5], 21,  - 57434055);
a = md5_ii(a, b, c, d, x[i + 12], 6, 1700485571);
d = md5_ii(d, a, b, c, x[i + 3], 10,  - 1894986606);
c = md5_ii(c, d, a, b, x[i + 10], 15,  - 1051523);
b = md5_ii(b, c, d, a, x[i + 1], 21,  - 2054922799);
a = md5_ii(a, b, c, d, x[i + 8], 6, 1873313359);
d = md5_ii(d, a, b, c, x[i + 15], 10,  - 30611744);
c = md5_ii(c, d, a, b, x[i + 6], 15,  - 1560198380);
b = md5_ii(b, c, d, a, x[i + 13], 21, 1309151649);
a = md5_ii(a, b, c, d, x[i + 4], 6,  - 145523070);
d = md5_ii(d, a, b, c, x[i + 11], 10,  - 1120210379);
c = md5_ii(c, d, a, b, x[i + 2], 15, 718787259);
b = md5_ii(b, c, d, a, x[i + 9], 21,  - 343485551);
a = safe_add(a, olda);
b = safe_add(b, oldb);
c = safe_add(c, oldc);
d = safe_add(d, oldd);
}
if (mode == 16)
{
return Array(b, c);
}
else
{
return Array(a, b, c, d);
}
}
function md5_cmn(q, a, b, x, s, t)
{
return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s), b);
}
function md5_ff(a, b, c, d, x, s, t)
{
return md5_cmn((b & c) | ((~b) & d), a, b, x, s, t);
}
function md5_gg(a, b, c, d, x, s, t)
{
return md5_cmn((b & d) | (c & (~d)), a, b, x, s, t);
}
function md5_hh(a, b, c, d, x, s, t)
{
return md5_cmn(b ^ c ^ d, a, b, x, s, t);
}
function md5_ii(a, b, c, d, x, s, t)
{
return md5_cmn(c ^ (b | (~d)), a, b, x, s, t);
}
function core_hmac_md5(key, data)
{
var bkey = str2binl(key);
if (bkey.length > 16)
bkey = core_md5(bkey, key.length * chrsz);
var ipad = Array(16), opad = Array(16);
for (var i = 0; i < 16; i++)
{
ipad[i] = bkey[i] ^ 0x36363636;
opad[i] = bkey[i] ^ 0x5C5C5C5C;
}
var hash = core_md5(ipad.concat(str2binl(data)), 512+data.length * chrsz);
return core_md5(opad.concat(hash), 512+128);
}
function safe_add(x, y)
{
var lsw = (x & 0xFFFF) + (y & 0xFFFF);
var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
return (msw << 16) | (lsw & 0xFFFF);
}
function bit_rol(num, cnt)
{
return (num << cnt) | (num  >>> (32-cnt));
}
function str2binl(str)
{
var bin = Array();
var mask = (1 << chrsz) - 1;
for (var i = 0; i < str.length * chrsz; i += chrsz)
bin[i >> 5] |= (str.charCodeAt(i / chrsz) & mask) << (i % 32);
return bin;
}
function binl2str(bin)
{
var str = "";
var mask = (1 << chrsz) - 1;
for (var i = 0; i < bin.length * 32; i += chrsz)
str += String.fromCharCode((bin[i >> 5] >>> (i % 32)) & mask);
return str;
}
function binl2hex(binarray)
{
var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
var str = "";
for (var i = 0; i < binarray.length * 4; i++)
{
str += hex_tab.charAt((binarray[i >> 2] >> ((i % 4) * 8+4)) & 0xF) +
hex_tab.charAt((binarray[i >> 2] >> ((i % 4) * 8)) & 0xF);
}
return str;
}
function binl2b64(binarray)
{
var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
var str = "";
for (var i = 0; i < binarray.length * 4; i += 3)
{
var triplet = (((binarray[i >> 2] >> 8 * (i % 4)) & 0xFF) << 16) | ((
(binarray[i + 1 >> 2] >> 8 * ((i + 1) % 4)) & 0xFF) << 8) | ((binarray[i
+ 2 >> 2] >> 8 * ((i + 2) % 4)) & 0xFF);
for (var j = 0; j < 4; j++)
{
if (i * 8+j * 6 > binarray.length * 32)
str += b64pad;
else
str += tab.charAt((triplet >> 6 * (3-j)) & 0x3F);
}
}
return str;
}
/*************************************************
* 财付通COOKIE相关处理函数
*************************************************/
function CCookie()
{
this.SetCookie = setCookie;
this.GetCookie = getCookie;
this.DelCookie = deleteCookie;
}
function getExpDate(days, hours, minutes)
{
var expDate = new Date( );
if (typeof days == "number" && typeof hours == "number" &&
typeof hours == "number")
{
expDate.setDate(expDate.getDate( ) + parseInt(days));
expDate.setHours(expDate.getHours( ) + parseInt(hours));
expDate.setMinutes(expDate.getMinutes( ) + parseInt(minutes));
return expDate.toGMTString( );
}
}
// utility function called by getCookie( )
function getCookieVal(offset)
{
var endstr = document.cookie.indexOf (";", offset);
if (endstr == -1)
{
endstr = document.cookie.length;
}
return unescape(document.cookie.substring(offset, endstr));
}
// primary function to retrieve cookie by name
function getCookie(name)
{
var arg = name + "=";
var alen = arg.length;
var clen = document.cookie.length;
var i = 0;
while (i < clen)
{
var j = i + alen;
if (document.cookie.substring(i, j) == arg)
{
return getCookieVal(j);
}
i = document.cookie.indexOf(" ", i) + 1;
if (i == 0) break;
}
return "";
}
// store cookie value with optional details as needed
function setCookie(name, value, expires, path, domain, secure)
{
document.cookie = name + "=" + escape (value) +
((expires) ? "; expires=" + expires : "") +
((path) ? "; path=" + path : "") +
((domain) ? "; domain=" + domain : "") +
((secure) ? "; secure" : "");
}
// remove the cookie by setting ancient expiration date
function deleteCookie(name,path,domain)
{
if (getCookie(name)) {
document.cookie = name + "=" +
((path) ? "; path=" + path : "") +
((domain) ? "; domain=" + domain : "") +
"; expires=Thu, 01-Jan-70 00:00:01 GMT";
}
}
/*
* 定义全局变量g_CCookie
*/
var g_CCookie =null ;
if(!g_CCookie)
g_CCookie = new CCookie();
/*************************************************
* 财付通数字证书相关处理函数:使用公司QQCERT控件
*************************************************/
function CQQCert()
{
//是否使用QQCERT控件
this.m_bUseFlag = true;
//控件版本
this.m_sVersion = "1,0,1,1";
//控件GUID
this.m_sClassid = "CLSID:BAEA0695-03A4-43BB-8495-C7025E1A8F42";
//控件路径
this.m_sLocate = "https://www.tenpay.com/download/tenpaycert_xp.cab";
//绘出控件
this.Draw = CQQCert_Draw;
this.Import = CQQCert_Import;
this.FindCrt = CQQCert_FindCrt;
this.DelCrt = CQQCert_DelCrt;
this.MakeCSR = CQQCert_MakeCSR;
this.Sign = CQQCert_Sign;
this.HostName = CQQCert_HostName;
this.Version = CQQCert_Version;
this.Base64Decode = CQQCert_Base64Decode;
this.Base64Encode = CQQCert_Base64Encode;
this.IsObjOk = CQQCert_IsObjOk
this.IsCnExist = CQQCert_IsCnExist;
}
function CQQCert_Draw(idname, other, htmlparentid)
{
var strObj;
strObj = "<OBJECT id=\"" + idname + "\" ";
strObj+= "classid=\"" + this.m_sClassid + "\" ";
strObj+= " codebase=\"" + this.m_sLocate + "#Version=" + this.m_sVersion + "\" ";
if(other)
strObj+=" " + other;
strObj+= " height=0 width=0></OBJECT>";
if(htmlparentid)
{
var obj=document.getElementById(htmlparentid);
obj.innerHTML=strObj;
}
else
{
document.write(strObj);
try{
if(document.getElementById(idname).Version< parseInt(g_CQQCertVersion)){
document.write("<a href=\"/download/tenpaycert_xp.exe\"><font style=\"font-size:12px\" color=ff6600>未安装数字证书控件？");
document.write("请点此安装控件后刷新</a>");
}
} catch(er) {}
}
}
function CQQCert_Import(ctl,crt)
{
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
{
return false;
}
var ret,re;
var cerstr;
//alert(!ctl);
//alert(!crt);
ret = ctl.ImportCert(crt);
//alert("ret="+ret);
if(ret==-1)
{
return false;
}
return true;
}
function CQQCert_FindCrt(ctl,keyvalue, keyname)
{
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
{
return "";
}
if(!keyname)
keyname="SN";
var cn=ctl.FindCert(keyname, keyvalue);
if(!cn||cn==null||cn=="")
{
return "";
}
else
{
return cn;
}
}
function CQQCert_DelCrt(ctl,keyvalue,cn, keyname)
{
var iRet=-1;
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
{
return false;
}
if (!cn)
{
cn = GetLocalCert(ctl,keyvalue);
}
if (cn == "")
{
return false;
}
try
{
//    iRet = ctl.DelCert(this.FindCrt(ctl,keyvalue, keyname));
iRet = ctl.DelCert(cn);
}
catch(e)
{
return false;
}
return (iRet==1);
}
function CQQCert_MakeCSR(ctl,subject,uin,sid)
{
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
{
return "";
}
var ret,csr;
ret = ctl.SetChallenge(uin,sid);
if(ret!=1){
return "";
}
ret = ctl.SetSubject(subject);
if(ret!=1){
return "";
}
csr = ctl.GetCSR();
return csr;
}
function CQQCert_Sign(ctl,keyvalue,message,cn,keyname)
{
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
{
return "";
}
if (!cn)
{
cn = GetLocalCert(ctl,keyvalue);
}
if (cn == "")
{
return "";
}
return ctl.CertSign(cn,message);
}
function CQQCert_IsCnExist(ctl, cn)
{
/*
var str=ctl.CertSign(cn,ctl.Base64Encode("1"));
if(str && str != "")
return true;
else
return false;
*/
var res;
try
{	res = ctl.IsCertExist(cn);	}
catch(e)
{	res = -1;	}
if(res == 1)
return true;
else
return false;
}
function CQQCert_HostName(ctl)
{
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
{
return "";
}
return ctl.HostName;
}
function CQQCert_Version(ctl)
{
return ctl.Version;
}
function CQQCert_Base64Encode(ctl,str)
{
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
{
return "";
}
return ctl.Base64Encode(str);
}
function CQQCert_Base64Decode(ctl,str)
{
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
{
return "";
}
return ctl.Base64Decode(str);
}
function CQQCert_IsObjOk(ctl)
{
if (!(g_CQQCert.Version(ctl) >= parseInt(g_CQQCertVersion)))
return false;
else
return true;
}
var g_CQQCert = null;
var g_CQQCertVersion = 1011;
if(g_CQQCert == null)
g_CQQCert = new CQQCert();
function WriteCertSignCookie(srcstr)
{
if (g_CCookie.GetCookie("certuserflag") != "1")
{
return true;
}
var QQCertCtrl = window.top.document.getElementById("QQCertCtrl");
if (!QQCertCtrl)
{
return false;
}
if (!(g_CQQCert.Version(QQCertCtrl) >= parseInt(g_CQQCertVersion)))
{
if(g_CQQCert.m_bUseFlag)
{
}
return false;
}
//	var cn = g_CQQCert.FindCrt(QQCertCtrl,g_CCookie.GetCookie("qluid"));
var cn = GetLocalCert(QQCertCtrl,g_CCookie.GetCookie("qluid"));
if (cn == "")
{
top.location.href = "/certificates/tenpay_safe_tips2.shtml";
return false;
}
g_CCookie.SetCookie(
"cn",
cn,
"",
"/",
"tenpay.com",
"");
var signstr = g_CQQCert.Sign(QQCertCtrl,g_CCookie.GetCookie("qluid"),g_CQQCert.Base64Encode(QQCertCtrl,srcstr));
g_CCookie.SetCookie(
"srcstr",
srcstr,
"",
"/",
"tenpay.com",
"");
signstr = encodeURIComponent(signstr);
g_CCookie.SetCookie(
"signstr",
signstr,
"",
"/",
"tenpay.com",
"");
return true;
}
function WriteCertSignCookie2(CertCtrlId, iUid, sSrc, sSeq)
{
var QQCertCtrl = window.top.document.getElementById(CertCtrlId);
if (!QQCertCtrl)
return false;
if (!(g_CQQCert.Version(QQCertCtrl) >= parseInt(g_CQQCertVersion)))
return false;
//	var cn = g_CQQCert.FindCrt(QQCertCtrl, iUid);
var cn = GetLocalCert(QQCertCtrl, iUid);
if (cn == "")
return false;
var signstr = g_CQQCert.Sign(QQCertCtrl,iUid,g_CQQCert.Base64Encode(QQCertCtrl, md5(g_CQQCert.Base64Encode(QQCertCtrl,sSeq+sSrc)).toUpperCase()));
g_CCookie.SetCookie("cn", cn,"","/","tenpay.com","");
g_CCookie.SetCookie("signseq", sSeq,"","/","tenpay.com","");
g_CCookie.SetCookie("signstr", signstr,"","/","tenpay.com","");
return true;
}
function CertStat()
{
var result = 0;
if (g_CCookie.GetCookie("certuserflag") == "1")
{
result += 1;
}
else
{
result += 0;
return result;
}
var QQCertCtrl = window.top.document.getElementById("QQCertCtrl");
if(!QQCertCtrl)
var QQCertCtrl = document.getElementById("QQCertCtrl");
if (!QQCertCtrl)
{
return result;
}
if (g_CQQCert.Version(QQCertCtrl) >= parseInt(g_CQQCertVersion))
{
result += 2;
}
else
{
result += 0;
return result;
}
//	var cn = g_CQQCert.FindCrt(QQCertCtrl,g_CCookie.GetCookie("qluid"));
var cn = GetLocalCert(QQCertCtrl,g_CCookie.GetCookie("qluid"));
if (cn != "")
{
result += 4;
}
else
{
result += 0;
}
return result;
}
function CertUserHasActive()
{
var result = CertStat();
if (result == 0)
{
return true;
}
else if (result == 1)
{
return false;
}
else if (result == 3 || 7 == result)
{
return true;
}
else
{
// error
return false;
}
}
function CertUserHasCert()
{
var result = CertStat();
if (result == 0)
{
return 0;
}
else if (result == 1)
{
return 1;
}
else if (result == 3)
{
return 1;
}
else if (7 == result)
{
var m_cn = GetLocalCert();
if (m_cn > 0)
{
return 2;
}
else
return 1;
}
else
{
// error
return 0;
}
}
function CertStat_wallet()
{
var result = 0;
if ("1" == g_CCftUser.m_sCertuserflag)
{
result += 1;
}
else
{
result += 0;
return result;
}
var QQCertCtrl = window.top.document.getElementById("QQCertCtrl");
if(!QQCertCtrl)
var QQCertCtrl = document.getElementById("QQCertCtrl");
if (!QQCertCtrl)
{
return result;
}
if (g_CQQCert.Version(QQCertCtrl) >= parseInt(g_CQQCertVersion))
{
result += 2;
}
else
{
result += 0;
return result;
}
//	var cn = g_CQQCert.FindCrt(QQCertCtrl,g_CCftUser.m_sInnerid);
var cn = GetLocalCert(QQCertCtrl,g_CCftUser.m_sInnerid);
if (cn != "")
{
result += 4;
}
else
{
result += 0;
}
return result;
}
function CertUserHasActive_wallet()
{
var result = CertStat_wallet();
if (result == 0)
{
return true;
}
else if (result == 1)
{
return false;
}
else if (result == 3 || 7 == result)
{
return true;
}
else
{
// error
return false;
}
}
function WriteCertSignCookie_wallet(srcstr)
{
if (g_CCftUser.m_sCertuserflag != "1")
{
return true;
}
var QQCertCtrl = window.top.document.getElementById("QQCertCtrl");
if(!QQCertCtrl)
var QQCertCtrl = document.getElementById("QQCertCtrl");
if (!QQCertCtrl)
{
return false;
}
if (!(g_CQQCert.Version(QQCertCtrl) >= parseInt(g_CQQCertVersion)))
{
if(g_CQQCert.m_bUseFlag)
{
}
return false;
}
//	var cn = g_CQQCert.FindCrt(QQCertCtrl,g_CCftUser.m_sInnerid);
var cn = GetLocalCert(QQCertCtrl,g_CCftUser.m_sInnerid);
if (cn == "")
{
TB_show('','#TB_inline?height=150&width=225&inlineId=div_no_cert&ex=noclick_notitle','');
//		top.location.href = "/certificates/tenpay_safe_tips2.shtml";
return false;
}
g_CCookie.SetCookie(
"cn",
cn,
"",
"/",
"tenpay.com",
"");
var signstr = g_CQQCert.Sign(QQCertCtrl,g_CCftUser.m_sInnerid,g_CQQCert.Base64Encode(QQCertCtrl,srcstr));
g_CCookie.SetCookie(
"srcstr",
srcstr,
"",
"/",
"tenpay.com",
"");
signstr = encodeURIComponent(signstr);
g_CCookie.SetCookie(
"signstr",
signstr,
"",
"/",
"tenpay.com",
"");
return true;
}
function CertStat_client()
{
var result = 0;
if (g_CCookie.GetCookie("certuserflag") == "1")
{
result += 1;
}
else
{
result += 0;
return result;
}
var QQCertCtrl = window.top.document.getElementById("QQCertCtrl");
if(!QQCertCtrl)
QQCertCtrl = document.getElementById("QQCertCtrl");
if (!QQCertCtrl)
{
return result;
}
if (g_CQQCert.Version(QQCertCtrl) >= parseInt(g_CQQCertVersion))
{
result += 2;
}
else
{
result += 0;
return result;
}
//	var cn = g_CQQCert.FindCrt(QQCertCtrl,g_CCookie.GetCookie("qluid"));
var cn = GetLocalCert(QQCertCtrl,g_CCookie.GetCookie("qluid"));
if (cn != "")
{
result += 4;
}
else
{
result += 0;
}
return result;
}
function CertUserHasActive_client()
{
var result = CertStat_client();
if (result == 0)
{
return true;
}
else if (result == 1)
{
return false;
}
else if (result == 3 || 7 == result)
{
return true;
}
else
{
// error
return false;
}
}
function WriteCertSignCookie_client(srcstr)
{
if (g_CCookie.GetCookie("certuserflag") != "1")
{
return true;
}
var QQCertCtrl = window.top.document.getElementById("QQCertCtrl");
if (!QQCertCtrl)
{
return false;
}
if (!(g_CQQCert.Version(QQCertCtrl) >= parseInt(g_CQQCertVersion)))
{
if(g_CQQCert.m_bUseFlag)
{
}
return false;
}
//	var cn = g_CQQCert.FindCrt(QQCertCtrl,g_CCookie.GetCookie("qluid"));
var cn = GetLocalCert(QQCertCtrl,g_CCookie.GetCookie("qluid"));
if (cn == "")
{
TB_show('','#TB_inline?height=150&width=225&inlineId=div_no_cert&ex=noclick_notitle','');
//		top.location.href = "/certificates/tenpay_safe_tips2.shtml";
return false;
}
g_CCookie.SetCookie(
"cn",
cn,
"",
"/",
"tenpay.com",
"");
var signstr = g_CQQCert.Sign(QQCertCtrl,g_CCookie.GetCookie("qluid"),g_CQQCert.Base64Encode(QQCertCtrl,srcstr));
g_CCookie.SetCookie(
"srcstr",
srcstr,
"",
"/",
"tenpay.com",
"");
signstr = encodeURIComponent(signstr);
g_CCookie.SetCookie(
"signstr",
signstr,
"",
"/",
"tenpay.com",
"");
return true;
}
function GetLocalCert(ctl,innerid,certlist)
{
if (!ctl)
{
ctl = document.getElementById("QQCertCtrl");
if (!ctl)
ctl = top.document.getElementById("QQCertCtrl");
}
if (!innerid)
{
innerid = g_CCookie.GetCookie("qluid");
}
if (!certlist)
{
certlist = g_CCookie.GetCookie("certlist");
}
var cns = certlist.split("-");
var cn;
var i = 0;
var flag = -1;
for (i=0; i<cns.length; i++)
{
if (cns[i] == "")
continue;
cn = parseInt(cns[i]);
try
{
flag = g_CQQCert.IsCnExist(ctl,cn.toString());
}
catch(e)
{
flag = -1;
}
if (flag == 1)
{
// cert exist, get if the cert can be used
var res= "";
try
{
res = g_CQQCert.Sign(
ctl,
innerid,
g_CQQCert.Base64Encode(ctl,"0123456789abcdef"),
cn.toString());
}
catch(e)
{
res = "";
}
if (res != "")
{
// cert exist, and it can be used
return cn;
}
else
// cert can't be used, and it is useless
continue;
}
}
return "";
}

var bankInfoArray = [];

function BankInfo(bankid, bankname, bankabbr, visible, picurl, tips, tday, rpmtip, area, bin, branch) {
  this.bankid = bankid;
  this.bankname = bankname;
  this.bankabbr = bankabbr;
  this.visible = visible;
  this.picurl = picurl;
  this.tips = tips;
  this.tday = tday;
  this.rpmtip = rpmtip;
  this.area = area || "|";
  this.bin = bin || "";
  this.branch = branch || bankname;
}

function getBankInfo (banktype) {
  for (var i = 0; i < bankInfoArray.length; i++)
    if (banktype == bankInfoArray[i].bankid) return bankInfoArray[i];
  return new BankInfo();
};

//支持还款的银行列表
bankInfoArray.push(new BankInfo("1050", "工商银行", "icbc", true, "/zft/images/bank_2.gif", "工商银行到账时间：第<strong>2</strong>日，今天还款，预计明天下午即可到账。", "1~2", "需要<em>1-2</em>天，<em>建议您提前3天进行还款</em>", "1|10", "370246|370247|370248|370249|427010|427019|427020|427029|427030|427039|438125|438126|451804|451810|451811|451871|45806|45807|458441|489734|489735|489736|513685|518750|524047|526836|530970|53098|530990|543098|558360|622200|622210|622215|622220|622225|622230|622231|622232|622233|622234|622235|622237|622238|622239|622240|622245|628288"));
bankInfoArray.push(new BankInfo("1001", "招商银行", "cmb", false, "/zft/images/bank_1.gif", "招商银行到账时间：第<strong>2</strong>日，今天还款，预计明天下午即可到账。", "1~2", "需要<em>1-2</em>天，<em>建议您提前3天进行还款</em>", "2|21", "356885|356886|356887|356888|356889|356890|370285|370286|3702873|3702893|439188|439225|439226|439227|479228|479229|518710|518718|521302|545619|545620|545621|545623|545947|545948|552534|552587|622575|622576|622577|622578|622579|622581|622582"));
bankInfoArray.push(new BankInfo("3017", "招商银行", "cmb", true, "/zft/images/bank_1.gif", "招商银行到账时间：第<strong>2</strong>日，今天还款，预计明天下午即可到账。", "1~2", "需要<em>1-2</em>天，<em>建议您提前3天进行还款</em>", "2|21", "356885|356886|356887|356888|356889|356890|370285|370286|3702873|3702893|439188|439225|439226|439227|479228|479229|518710|518718|521302|545619|545620|545621|545623|545947|545948|552534|552587|622575|622576|622577|622578|622579|622581|622582"));
bankInfoArray.push(new BankInfo("3001", "兴业银行", "cib", true, "/zft/images/bank_12.gif", "兴业银行到账时间：<strong>实时到账</strong>", "", "<em>实时到账</em>", "2|21"));
bankInfoArray.push(new BankInfo("1020", "交通银行", "bankcomm", true, "/zft/images/bank_16.gif", "交通银行到账时间：<strong>1</strong>～<strong>2</strong>个工作日，今天还款，预计明天下午即可到账。", "1~2", "需要<em>1-2</em>天，<em>建议您提前3天进行还款</em>", "2|21", "53783|49104|601428|405512|66601428|66405512|622254|622255|622256|622257|955590|955591|955592|955593|622250|622251|622252|622253|458124|458123|521899|520169|552853|622258|622259|622260|622261|400672|888888|628218|628216|522964|434910|622656|622284|622285"));
bankInfoArray.push(new BankInfo("1010", "平安银行", "pingan", false, "/zft/images/bank_1010.gif", "平安银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "2|21", "356869|526855|622155|622156|627066|528020", "深圳平安银行股份有限公司上海分行"));
//add by carlli
bankInfoArray.push(new BankInfo("3014", "平安银行", "pingan", true, "/zft/images/bank_1010.gif", "平安银行到账时间：<strong>实时到账</strong>", "", "<em>实时到账</em>", "2|21", "", "深圳平安银行股份有限公司上海分行"));
bankInfoArray.push(new BankInfo("1008", "深圳发展银行", "sdb", true, "/zft/images/bank_09.gif", "深发展银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|755", "435744|435745|483536|622525|622526|998801|998802", "深圳发展银行信用卡中心"));
bankInfoArray.push(new BankInfo("3027", "中国银行", "bofc", true, "/zft/images/bank_15.gif", "中国银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|20", "42410848|51847648|62275248|62275348", "中国银行深圳市分行"));
//bankInfoArray.push(new BankInfo("1027", "广东发展银行", "gdb", true, "/zft/images/bank_27.gif", "广东发展银行到账时间：<strong>1</strong>～<strong>2</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|20", "406365|406366|428911|436768|436769|436770|436771|487013|491032|491033|491034|491035|491036|491037|491038|493427|518364|520152|520382|523961|523966|528931|541709|541710|548844|552794|558894|622555|622556|622557|622558|622559|622560");
bankInfoArray.push(new BankInfo("3018", "广东发展银行", "gdb", true, "/zft/images/bank_27.gif", "广东发展银行到账时间：<strong>1</strong>～<strong>2</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|20", "406365|406366|428911|436768|436769|436770|436771|487013|491032|491033|491034|491035|491036|491037|491038|493427|518364|520152|520382|523961|523966|528931|541709|541710|548844|552794|558894|622555|622556|622557|622558|622559|622560"));
bankInfoArray.push(new BankInfo("3021", "光大银行", "cebb", true, "/zft/images/bank_index_11.jpg", "光大银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|755", "437790|303000|406252|406254|481699|543159|622655|622659|622658|622650|356837|356838|356839|356840|425862", "中国光大银行深圳分行"));
bankInfoArray.push(new BankInfo("3022", "建设银行", "ccb", true, "/zft/images/bank_3.gif", "建设银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|755", "356895|434061|4367|453242|489591|489592|491031|531693|532450|532458|552801|622166|622168|622700|628366", "中国建设银行股份有限公司深圳市分行"));
bankInfoArray.push(new BankInfo("3023", "浦发银行", "spdb", true, "/zft/images/bank_6.gif", "浦发银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|755", "456418|622116|622117|622176|622177|517650|622277|622252|622521|356851|356850|404738|404739|498451|622228", "上海浦东发展银行深圳分行"));
bankInfoArray.push(new BankInfo("3015", "中信银行", "zxyh", true, "/zft/images/bank_1044.gif", "中信银行到账时间：<strong>实时到账</strong>", "", "<em>实时到账</em>", "2|21", "", "中信银行深圳分行"));
bankInfoArray.push(new BankInfo("1006", "民生银行", "cmbc", true, "/zft/images/bank_7.gif", "民生银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|755", "35685|421869|421870|421871|407405|512466|517636|528948|552288|556610|622600|622601|622602|622603", "中国民生银行深圳分行"));
bankInfoArray.push(new BankInfo("3025", "上海银行", "shbank", true, "/zft/images/bank_8.gif", "上海银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "2|21", "356827|356828|356829|356830|402673|402674|438600|486466|519498|520131|622148|622149|524031|622269|622268|548838", "上海银行信用卡中心"));
bankInfoArray.push(new BankInfo("3024", "农业银行", "abc", true, "/zft/images/bank_4.gif", "农业银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|755", "622830|622820|622837|520083|519413|622836|558730|404118|628268|404117|403361", "中国农业银行深圳市分行分行银行卡业务部"));
bankInfoArray.push(new BankInfo("3008", "宁波银行", "nbcb", false, "/zft/images/bank_nbcb.gif", "宁波银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "12|571", "622282|622575|622318", "宁波银行股份有限公司杭州分行"));

bankInfoArray.push(new BankInfo("3026", "北京银行", "bjb", true, "/zft/images/bank_bj.gif", "北京银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "1|10", "522001|622853|622163|628203", "北京银行"));
bankInfoArray.push(new BankInfo("3020", "华夏银行", "hxb", false, "/zft/images/bank_13.gif", "华夏银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "20|755", "622636|622637|539867|539868|528708|528709|523959|628318", "华夏银行股份有限公司深圳分行营业部"));
bankInfoArray.push(new BankInfo("3009", "汉口银行", "hkb", true, "/zft/images/bank_hk.gif", "汉口银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "18|27", "622625|622626|622566|622567|628200", "汉口银行信用卡中心"));

bankInfoArray.push(new BankInfo("3010", "南京银行", "njb", true, "/zft/images/bank_nj.gif", "南京银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "11|25", "622303|622903", "南京银行股份有限公司营业部"));
bankInfoArray.push(new BankInfo("3011", "江苏银行", "jsb", true, "/zft/images/bank_js.gif", "江苏银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "11|25", "622283", "江苏银行股份有限公司营业部"));
bankInfoArray.push(new BankInfo("3012", "杭州银行", "hzb", true, "/zft/images/bank_hz.gif", "杭州银行到账时间：<strong>2</strong>～<strong>3</strong>个工作日到账，节假日顺延。", "2~3", "需要<em>2-3</em>天，<em>建议您提前5天进行还款</em>", "12|571", "", "杭州银行股份有限公司营业部"));


var payBankList = [];//["bankid","名称","缩写",是否显示]
payBankList.push(["1001","招商银行","cmb",true]);
payBankList.push(["1002","工商银行","icbc",true]);
payBankList.push(["1003","建设银行","ccb",true]);
payBankList.push(["1005","农业银行","abc",true]);
payBankList.push(["1041","民生银行","cmsb",true]);
payBankList.push(["1008","深圳发展银行","szdb",true]);
payBankList.push(["1009","兴业银行","cib",true]);
//payBankList.push(["1032","北京银行","bankofbeijing",true]);
payBankList.push(["1021","中信银行","citic-bank",true]);
payBankList.push(["1051","广东发展银行","gdb",true]);
payBankList.push(["1010","平安银行","pingan-bank",true]);
//payBankList.push(["1026","中国银行","china-bank",true]);
payBankList.push(["1004","浦发银行","spdb",true]);
payBankList.push(["1022","光大银行","ceb",true]);
//payBankList.push(["1020","交通银行","boco",true]);
payBankList.push(["1025","华夏银行","hxb",true]);
payBankList.push(["1022","光大银行","cebb",true]);
payBankList.push(["3004","建设银行","ccb",true]);
payBankList.push(["3005","浦发银行","spdb",true]);
payBankList.push(["3007","农业银行","abc",true]);
payBankList.push(["1024","上海银行","shbank",true]);
payBankList.push(["1032","北京银行","bankofbeijing",true]);
payBankList.push(["1026","中国银行","bofc",true]);
