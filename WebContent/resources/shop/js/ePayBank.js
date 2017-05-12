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
if (str < 102 || str > 999)
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

// 检查是否为有效的手机号码
function checkIsMobile(str)
{
if(!(/^1[3|4|5|8][0-9]\d{4,8}$/.test(str))) 
   return false; 
else 
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

var bankInfoArray = [];

function BankInfo(bankid, bankname, bankabbr, logo, bankCode, plugIns, flag) {
  this.bankid = bankid;
  this.bankname = bankname;
  this.bankabbr = bankabbr;
  this.logo = logo;
  this.bankCode = bankCode;
  this.plugIns = plugIns;
  this.flag = flag;
}

function getBankInfo (bankid) {
  for (var i = 0; i < bankInfoArray.length; i++)
    if (bankid == bankInfoArray[i].bankid) return bankInfoArray[i];
  return new BankInfo();
};

function getBankByCode (bankCode) {
  for (var i = 0; i < bankInfoArray.length; i++)
    if (bankCode == bankInfoArray[i].bankCode) return bankInfoArray[i];
  return new BankInfo();
};

//支持还款的银行列表
bankInfoArray.push(new BankInfo("102", "工商银行", "icbc", "/resources/shop/payment/10.gif","ICBC","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("103", "农业银行", "abc", "/resources/shop/payment/1.gif","ABC","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("104", "中国银行", "bofc", "/resources/shop/payment/16.gif","BOCSH","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("105", "建设银行", "ccb", "/resources/shop/payment/2.gif","CCB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("301", "交通银行", "bankcomm", "/resources/shop/payment/5.gif","BOCOM","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("302", "中信银行", "zxyh", "/resources/shop/payment/7.gif","CNCB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("303", "光大银行", "cebb", "/resources/shop/payment/3.gif","CEB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("304", "华夏银行", "hxb", "/resources/shop/payment/22.gif","HXB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("305", "民生银行", "cmbc", "/resources/shop/payment/4.gif","CMBC","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("306", "广发银行", "gdb", "/resources/shop/payment/6.gif","GDB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("307", "平安银行", "sdb", "/resources/shop/payment/18.gif","PAB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("308", "招商银行", "cmb", "/resources/shop/payment/17.gif","CMB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("309", "兴业银行", "cib", "/resources/shop/payment/27.gif","CIB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("310", "浦发银行", "spdb", "/resources/shop/payment/9.gif","SPDB","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("403", "邮政储蓄", "psbc", "/resources/shop/payment/403.gif","PSBC","ecpssPlugin","111"));
bankInfoArray.push(new BankInfo("801", "上海银行", "bosh", "/resources/shop/payment/801.gif","BOS","ecpssPlugin","100"));
bankInfoArray.push(new BankInfo("802", "上海农商", "srcb", "/resources/shop/payment/802.gif","SRCB","ecpssPlugin","100"));
bankInfoArray.push(new BankInfo("803", "北京银行", "bjb", "/resources/shop/payment/11.gif","BCCB","ecpssPlugin","100"));
bankInfoArray.push(new BankInfo("804", "中国银行(大额)", "bofc", "/resources/shop/payment/804.gif","BOC","ecpssPlugin","100"));
bankInfoArray.push(new BankInfo("902", "支付宝", "psbc", "/resources/shop/payment/alipay.gif","alipay","elinkBankPlugin","100"));
bankInfoArray.push(new BankInfo("903", "财付通", "psbc", "/resources/shop/payment/tenpay.gif","tenpay","elinkBankPlugin","000"));
