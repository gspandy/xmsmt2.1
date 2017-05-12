Date.prototype.format = function(format)
{
    var o =
    {
        "M+" : this.getMonth()+1, //month
        "d+" : this.getDate(),    //day
        "h+" : this.getHours(),   //hour
        "m+" : this.getMinutes(), //minute
        "s+" : this.getSeconds(), //second
        "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
        "S" : this.getMilliseconds() //millisecond
    }
    if(/(y+)/.test(format))
    format=format.replace(RegExp.$1,(this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
    if(new RegExp("("+ k +")").test(format))
    format = format.replace(RegExp.$1,RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
    return format;
}

Date.prototype.addDays = function(d)
{
    this.setDate(this.getDate() + parseInt(d));
};


Date.prototype.addWeeks = function(w)
{
    this.addDays(w * 7);
};


Date.prototype.addMonths= function(m)
{
    var d = this.getDate();
    this.setMonth(this.getMonth() + parseInt(m));

    if (this.getDate() < d)
        this.setDate(0);
};


Date.prototype.addYears = function(y)
{
    var m = this.getMonth();
    this.setFullYear(this.getFullYear() + parseInt(y));

    if (m < this.getMonth())
     {
        this.setDate(0);
     }
};

Date.prototype.showMonthFirstDay = function()     
{   
	this.setDate(1);
	return this;
};  

Date.prototype.showMonthLastDay = function()     
{          
	this.addMonths(1);
	var   MonthNextFirstDay=new   Date(this.getYear(),this.getMonth(),1);     
    var   MonthLastDay=new   Date(MonthNextFirstDay-86400000);     
	return MonthLastDay;
};     
//获得本周的开端日期   
Date.prototype.getWeekStartDate = function() {  
	var weekStartDate = new Date(this.getYear(), this.getMonth(), this.getDate()-this.getDay());  
	return weekStartDate;  
};
  
//获得本周的停止日期   
Date.prototype.getWeekEndDate = function() {  
	var weekEndDate = new Date(this.getYear(), this.getMonth(), this.getDate() + (6 - this.getDay()));  
	return weekEndDate;  
}; 

