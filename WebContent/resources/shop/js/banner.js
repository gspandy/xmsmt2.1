var banner_currId=0;
var imgCount;
var timeout;
var interval=6000;
var showstatus = 0;
$(function(){  
    imgCount=$(".h_banner .h_banner_imgs li").length; 
    $(".h_banner .h_banner_imgs li:gt(0)").hide(); 
    $(".h_banner .num li:eq(0)").addClass("curr"); 
    timeout=setTimeout("banner_change(1)",interval);
   
    $(".h_banner .num li").click(function(){
        cid=parseInt($(this).attr("id"));
        clearTimeout(timeout);
        banner_change(cid);
        //alert(banner_currId); 
    });
});
function banner_change(currId){
    if (showstatus==1) return false;
    showstatus=1;
    banner_currId=currId;
    //alert(banner_currId);
    $(".h_banner .h_banner_imgs li:visible").fadeOut(700);
    $(".h_banner .h_banner_imgs li").eq(currId).fadeIn(700,function(e){showstatus=0;});
    $(".h_banner .roll_intro li:visible").hide();
    $(".h_banner .roll_intro li").eq(currId).show();	
    $(".h_banner .num li").removeClass("curr");    
    $(".h_banner .num li").eq(currId).addClass("curr"); 
    banner_currId++;
    if(banner_currId>=imgCount) banner_currId=0;    
    timeout=setTimeout("banner_change(banner_currId)",interval);
    
};
function rleft()
{   
	clearTimeout(timeout);
    if(banner_currId==0 ){
    	tmpid = imgCount-2;
    }else{ 
        if(banner_currId==1){
        	tmpid = imgCount-1;
        }else{
        	tmpid=banner_currId-2;
        }
    }
    banner_change(tmpid)
}
function rright()
{   
    clearTimeout(timeout);
    tmpid=banner_currId;
    if(banner_currId==1 ){
        tmpid = banner_currId;
    }
    banner_change(tmpid)
}