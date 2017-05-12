if($('.w_nav_pull').length > 0){
	$('.w_nav_pull').show();
	$('.w_nav_pull').on('mouseleave',function(e){
		$('.w_nav_pull').show();
	});
	var navli = $('.w_nav_pull ul').children('li').length;
	if(navli > 7){
		$('.hotProduct').css('margin-top',356 + 'px');
	}else{
		$('.hotProduct').css('margin-top',(navli-1)*53 +36 + 'px');
	}
}
if($('.w_nav_pulls').length > 0 ){
	$('.w_nav_pulls').show();
	$('.w_nav_pulls').on('mouseleave',function(e){
		$('.w_nav_pulls').show();
	});
	var navsli = $('.w_nav_pulls ul').children('li').length;
	if(navsli > 7){
		$('.mainmenu').css('margin-top',355 + 'px');
	}else{
		$('.mainmenu').css('margin-top',(navsli-1)*45 +39 + 'px');
	}
}

    $('#searchBg').on('mousemove',function(e){
    	$('.w_nav_pull').show();
    });
	