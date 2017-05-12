//验证帐户输入信息
var checkAccount={
	minLength:6,
	maxLength:20,
	checkF:function(strValue){
		var rule;
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if(nullRule.exec(strValue)||strValue==""){
			msgs[msgs.length++]="您还没有输入帐户名！";
		}else{
			if(strValue.length<this.minLength||strValue.length>this.maxLength){
				msgs[msgs.length++]="您输入帐户名必须在"+this.minLength+"到"+this.maxLength+"位以内";
			}
			rule=/^[0-9a-zA-Z_]*$/;
			if(!rule.exec(strValue)){
				msgs[msgs.length++]="您输入帐户名不符合规则";
			} 
		}
		return msgs;
	}
}


//验证邮件地址输入信息
var checkEmail={
	checkF:function(strValue){
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if((nullRule.exec(strValue)||strValue=="")){
			msgs[msgs.length++]="您还没有输入邮件地址！";
		}else{
			if(!chkemail(strValue)){
				msgs[msgs.length++]="您输入的邮件格式不正确！";
			}
		}
		return msgs;
	}
}

//验证密码输入信息
var checkPassword={
	minLength:6,
	maxLength:20,
	checkF:function(strValue1,strValue2){
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if(nullRule.exec(strValue1)||strValue1==""){
			msgs[msgs.length++]="您还没有输入登录密码！";
		}else{
			if(strValue1.length<this.minLength||strValue1.length>this.maxLength){
				msgs[msgs.length++]="您输入的密码必须在"+this.minLength+"到"+this.maxLength+"位以内";
			}
			if(nullRule.exec(strValue2)||strValue2==""){
				msgs[msgs.length++]="您还没有输入确认密码！";
			}else{
				if(strValue1!=strValue2){
					msgs[msgs.length++]="您输入的登录密码和确认密码不一致！";
				}
			}
		}
		return msgs;
	}
}

var checkLoginPassword={
	minLength:6,
	maxLength:20,
	emsg:'您还没有输入登录密码！',
	checkF:function(strValue){
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if(nullRule.exec(strValue)||strValue==""){
			msgs[msgs.length++]=this.emsg;
		}
		return msgs;
	}
}

//验证验证码输入信息
var checkVerifyCode={
	checkF:function(strValue){
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if(nullRule.exec(strValue)||strValue==""){
			msgs[msgs.length++]="您还没有输入验证码！";
		}
	 	return msgs;
	}
}


var checkTerminal={
	checkF:function(strValue){
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if(nullRule.exec(strValue)||strValue==0){
			msgs[msgs.length++]="您还没有选择终端号！";
		}
	 	return msgs;
	}
}

var checkName={
	checkF:function(strValue){
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if(nullRule.exec(strValue)||strValue==0){
			msgs[msgs.length++]="您还没有输入姓名！";
		}
	 	return msgs;
	}
}
 var checkMobile={
	checkF:function(strValue){
		var phoneRule=/^[0-9]+$/;
        var nullRule=/^\s+$/;
        var msgs=new Array();
        if(nullRule.exec(strValue)||strValue==0){
            msgs[msgs.length++]="您还未输入手机号码！";
        }else
        if(phoneRule.exec(strValue)==null||strValue.length !=11){
			msgs[msgs.length++]="您输入的手机号码格式不正确！";
		}
	 	return msgs;
	}
}

 var checkCertId={
	checkF:function(strValue){
		var phoneRule=/^[0-9]+$/;
        var nullRule=/^\s+$/;
        var msgs=new Array();
        if(nullRule.exec(strValue)||strValue==0){
            msgs[msgs.length++]="您还未输证件号码！";
        }else
        if(phoneRule.exec(strValue)==null){
			msgs[msgs.length++]="您输入的证件号码格式不正确！";
		}
	 	return msgs;
	}
}

 var checkCertType={
	checkF:function(strValue){

        var nullRule=/^\s+$/;
        var msgs=new Array();
        if(nullRule.exec(strValue)||strValue.length ==0){
			msgs[msgs.length++]="您您还未选择证件类型！";
		}
	 	return msgs;
	}
}



 var checkDate={
	checkF:function(strValue){
		var nullRule=/^\s+$/;
		var rule=/^((1[8-9][0-9][0-9])|(2[0-9][0-9][0-9]))-((0?[1-9])|(1[0-2]))-((0?[1-9])|([1-2][0-9])|(3[0-1]))*$/;
		var msgs=new Array();
		if(!nullRule.exec(strValue)&&strValue!=""){
			if(!rule.exec(strValue)){
				msgs[msgs.length++]="您输入的出生日期格式不正确！";
			}
		}
	 	return msgs;
	}
}

var checkFormNull={
	txtValue:'',
	minLength:6,
	maxLength:20,
	errmsg:'',
	doubleRule:/^[+]?\d+(\.\d+)?$/,
	checkF:function(strValue){
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if(nullRule.exec(strValue)||strValue==0){
			msgs[msgs.length++]=this.errmsg;
		}
	 	return msgs;
	},
	checkD:function(strValue){
		var msgs=new Array();
		var rule=/^\d*$/;
		if(!rule.exec(strValue)){
			msgs[msgs.length++]=this.errmsg;
		}
		return msgs;
	},
	checkL:function(strValue){
		var msgs=new Array();
		if(strValue<this.txtValue){
			msgs[msgs.length++]=this.errmsg;
		}
		return msgs;
	},
    	checkM:function(strValue){
		var msgs=new Array();
		if(strValue>this.txtValue){
			msgs[msgs.length++]=this.errmsg;
		}
		return msgs;
	},
    checkL2:function(strValue){
		var nullRule=/^\s+$/;
		var msgs=new Array();
		if(!nullRule.exec(strValue)&&strValue!=""){
			if(strValue.length<this.minLength || strValue.length>this.maxLength){
				msgs[msgs.length++]=this.errmsg;
			}
		}
		return msgs;
	},
	checkDValue:function(strValue){
		var msgs=new Array();
		if(!this.doubleRule.exec(strValue)){
			msgs[msgs.length++]=this.errmsg;
		}
		return msgs;
	},
	checkS:function(strValue){
		var msgs=new Array();
		if(strValue==0){
			msgs[msgs.length++]=this.errmsg;
		}
		return msgs;
	},
        checkMobile:function(strValue){
		var msgs=new Array();
		if(strValue==0||strValue.length !=11){
			msgs[msgs.length++]=this.errmsg;
		}
		return msgs;
	},
    checkDateValue:function(strValue){
		var nullRule=/^\s+$/;
		var rule=/^((1[8-9][0-9][0-9])|(2[0-9][0-9][0-9]))-((0?[1-9])|(1[0-2]))-((0?[1-9])|([1-2][0-9])|(3[0-1]))*$/;
		var msgs=new Array();
		if(!nullRule.exec(strValue)&&strValue!=""){
			if(!rule.exec(strValue)){
				msgs[msgs.length++]=this.errmsg;
			}
		}
		
		return msgs;
	}
	
}



function joinMsg(arryMsg){
	var count = 1;
	var emsg = "";

	for(i=0;i<arryMsg.length;i++){
		for(j=0;j<arryMsg[i].length;j++){
			emsg=emsg+count+". "+arryMsg[i][j]+"\n";
			count++;
		}
	}
	return emsg;
}

