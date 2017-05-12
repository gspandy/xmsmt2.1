//首页产品下拉效果
	    
	    $('.pull_down ul li').on('mousemove',function(){
	    	$(this).find('.buynow').show();
	    });
		  $('.pull_down ul li').on('mouseleave',function(){
			$(this).find('.buynow').hide();
		});
		//列表加入购物车效果
		$('.result ul li').on('mousemove',function(e){
			$(this).find('.buynow').show();
	    });
		$('.result ul li').on('mouseleave',function(e){
			$(this).find('.buynow').hide();
		});

		if($('.w_nav_pull').length > 0){
			for(var q=8;q<100;q++){
				$('.path li').eq(q).addClass('w_none');
			}
		}
		
	    var navli = $('.w_nav_pull ul').children('li').length;
	    if(navli>7){
	    	$('.navmore').on('click',function(){
		    	$('.w_nav_pull ul li').toggleClass('navblock');
		    	if($('.w_nav_pull ul li').hasClass('navblock')){
		    		$('.navmore a').text('点击收起');
		    	}else{
		    		$('.navmore a').text('查看更多');
		    	}
		    });
	    }else{
	    	$('.navmore a').text('');
	    }
	    if($('.w_nav_pull').length > 0){
			for(var q=6;q<navli;q++){
				$('.w_nav_pull li').eq(q).addClass('w_none');
			}
		}
		//导航菜单效果
	    $('.categoryFont').on('mousemove',function(){
	    	$('.w_nav_pull').show();
	    });
	    $('.w_nav_pull').on('mousemove','ul li',function(){
	    	$('.w_nav_pull ul li .w_nav_pullbottom .w_nav_pullright').hide();
	    	$('.w_nav_pull ul li').removeClass('w_lihover');
	    	$(this).addClass('w_lihover');
	    	if($(this).find('.w_nav_pullright').children('div').length > 0){
		    	$(this).find('.w_nav_pullright').show();
	    	}
	    });
	    $('.w_nav_pull').on('mouseleave','ul li',function(){
	    	$('.w_nav_pull ul li .w_nav_pullbottom .w_nav_pullright').hide();
	    	$('.w_nav_pull ul li').removeClass('w_lihover');
	    });
	    $('.w_nav_pull').on('mouseleave',function(){
	    	$('.w_nav_pull').hide();
	    	$('.w_nav_pull ul li').removeClass('w_lihover');
	    });

	    $('#searchBg').on('mousemove',function(){
	    	$('.w_nav_pull').hide();
	    });
	    //秒杀商品
		$('.wcx_father1').on('mousemove',function(){
			$('.pro_tab li').removeClass('cur');
			$('.wcx_father1').addClass('cur');
			$('.wcx_son1').show();
			$('.wcx_son2').hide();
			$('.wcx_son3').hide();
		});
		$('.wcx_father2').on('mousemove',function(){
			$('.pro_tab li').removeClass('cur');
			$('.wcx_father2').addClass('cur');
			$('.wcx_son2').show();
			$('.wcx_son1').hide();
			$('.wcx_son3').hide();
		});
		$('.wcx_father3').on('mousemove',function(){
			$('.pro_tab li').removeClass('cur');
			$('.wcx_father3').addClass('cur');
			$('.wcx_son3').show();
			$('.wcx_son1').hide();
			$('.wcx_son2').hide();
		});

		//最优惠的商品
		if($('.wcx_son2ul').length > 0){
			var cpscoller = $('.wcx_son2ul').children('li').length;
			$('.wcx_son2ul').css('width',cpscoller * 196 + 'px');
			var cpscollerint = cpscoller * 196 - 980;
			var i='';
			if(cpscoller <=5){
				$('.prev').hide();
				$('.next').hide();
			}else{
				$('.prev').on('click',function(){
					if(-i < cpscollerint){
						i = i - 980 ;
						$('.wcx_son2ul').animate({left:i + "px"});
						return i;
					}
				});
				$('.next').on('click',function(){
					if(i < 0){
						i = i + 980 ;
						$('.wcx_son2ul').animate({left:i + "px"});
						return i;
					}
				});
			}
		};
		//您可能喜欢的商品
		if($('.wcx_son3ul').length > 0){
			var cpscoller_1 = $('.wcx_son3ul').children('li').length;
			$('.wcx_son3ul').css('width',cpscoller_1 * 196 + 'px');
			var cpscollerint_1 = cpscoller_1 * 196 - 980;
			var b='';
			if(cpscoller_1 <=5){
				$('.prev1').hide();
				$('.next1').hide();
			}else{
				$('.prev1').on('click',function(){
					if(-b < cpscollerint_1){
						b = b - 980 ;
						$('.wcx_son3ul').animate({left:b + "px"});
						return b;
					}
				});
				$('.next1').on('click',function(){
					if(b < 0){
						b = b + 980 ;
						$('.wcx_son3ul').animate({left:b + "px"});
						return b;
					}
				});
			}
		};
		//限时促销
		if($('.wcx_son1ul').length > 0){
			var cpscoller_2 = $('.wcx_son1ul').children('li').length;
			$('.wcx_son1ul').css('width',cpscoller_2 * 196 + 'px');
			var cpscollerint_2 = cpscoller_2 * 196 - 980;
			var c='';
			if(cpscoller_2 <=5){
				$('.prev0').hide();
				$('.next0').hide();
			}else{
				$('.prev0').on('click',function(){
					if(-c < cpscollerint_2){
						c = c - 980 ;
						$('.wcx_son1ul').animate({left:c + "px"});
						return c;
					}
				});
				$('.next0').on('click',function(){
					if(c < 0){
						c = c + 980 ;
						$('.wcx_son1ul').animate({left:c + "px"});
						return c;
					}
				});
			}
		};

		// 商铺分类
		$('.categroy-emersion-main').on('mousemove','ul li.sec',function(e){
			$('.categroy-emersion-main ul li.sec .w_secconter').hide();
	    	if($(this).find('.w_secconter').children('a').length > 0){
		    	$(this).find('.w_secconter').show();
	    	}
		});
		$('.categroy-emersion-main').on('mouseout','ul li.sec',function(e){
			$(this).find('.w_secconter').hide();
		});

		var cpnavli = $('.categroy-emersion-main ul').children('li').length;
	    if(cpnavli>7){
			$('.w_all').on('click',function(){
				if($('.categroy-emersion-main ul li').hasClass('w_block')){
					$('.categroy-emersion-main ul li').removeClass('w_block');
					$('.w_all a').text('查看所有分类>>');
				}else{
					$('.categroy-emersion-main ul li').addClass('w_block');
					$('.w_all a').text('点击收起>>');
				}
				
			});
		}else{
	    		$('.w_all').hide();
	    }
		//商铺导航菜单效果
		if($('.w_nav_pulls').length > 0){
			//商铺列表
			 var navlis = $('.w_nav_pulls ul').children('li').length;
			if(navlis>7){
				$('.navmores').on('click',function(){
			    	if($('.w_nav_pulls ul li').hasClass('navblock')){
						$('.w_nav_pulls ul li').removeClass('navblock');
			    		$('.navmores a').text('查看更多');
			    	}else{
						$('.w_nav_pulls ul li').addClass('navblock');
			    		$('.navmores a').text('点击收起');
			    	}
			    });
			}else{
	    		$('.navmores a').text('');
	    	}
			
		    if($('.w_nav_pulls').length > 0){
				for(var i=6;i<navlis;i++){
					$('.w_nav_pulls li').eq(i).addClass('w_none');
				}
			}
		    $('.categoryFonts').on('mousemove',function(){
		    	$('.w_nav_pulls').show();
		    });
		    $('.w_nav_pulls').on('click','ul li',function(){
		    	if($(this).find('.w_nav_pullsbottom').hasClass('w_show')){
		    		$(this).find('.w_nav_pullsbottom').hide().removeClass('w_show');
			    	$(this).find('span').text('+');
		    	}else{
		    		$(this).find('.w_nav_pullsbottom').show().addClass('w_show');
			    	$(this).find('span').text('-');
		    	}
		    	
		    });
		    $('.w_nav_pulls').on('mouseleave',function(){
	    		$('.w_nav_pulls').hide();
	    	});
		}
		//组合搭配
		if($('.w_tset_contr').length > 0){
			 $('.w_tset_contr').on('click','ul li',function(){
		    	if($(this).hasClass('hover')){
		    		$(this).removeClass('hover');
		    	}else{
		    		$(this).addClass('hover');
		    	}
		    });
		}
		if($('.w_tset_cont_q').length > 0){
			var tuanlis = $('.w_tset_cont_q ul').children('li').length;
			$('.w_tset_cont_q ul').css('width',tuanlis * 188 + 'px');
		}
		
		//团购页面滚动
		if($('.w_tset_cont_q').length > 0){
			var tuanlis = $('.w_tset_cont_q ul').children('li').length;
			$('.w_tset_cont_q ul').css('width',tuanlis * 192 + 'px');
			var tuanliscont = tuanlis * 192 - 960;
			var f='';
			if(tuanlis <=5){
				$('.w_buttonl').hide();
				$('.w_buttonr').hide();
			}else{
				$('.w_buttonl').on('click',function(){
					if(-f < tuanliscont){
						f = f - 192 ;
						$('.w_tset_cont_qul').animate({left:f + "px"});
						return f;
					}
				});
				$('.w_buttonr').on('click',function(){
					if(f < 0){
						f = f + 192 ;
						$('.w_tset_cont_qul').animate({left:f + "px"});
						return f;
					}
				});
			}
		};

		//商铺列表滚动
		if($('.w_shoplist').length > 0){
			var slscoller = $('.w_shoplist ul').children('li').length;
			$('.w_shoplist ul').css('width',slscoller * 320 + 'px');
			var slscollerint = slscoller * 320 - 960;
			var u='';
			if(slscoller <=3){
				$('.prev3').hide();
				$('.next3').hide();
			}else{
				$('.prev3').on('click',function(e){
					if(-u < slscollerint){
						u = u - 980 ;
						$('.w_shoplist ul').animate({left:u + "px"});
						return u;
					}
				});
				$('.next3').on('click',function(e){
					if(u < 0){
						u = u + 980 ;
						$('.w_shoplist ul').animate({left:u + "px"});
						return u;
					}
				});
			}
		};

		//手机注册
		if($('.w_switching').length > 0){
			$('.w_phone').on('click',function(){
				$('.w_pregister').show();
				$('.w_eregister').hide();
				$('.w_uregister').hide();
				$('.w_phone').addClass('w_hover');
				$('.w_em').removeClass('w_hover');
				$('.w_ur').removeClass('w_hover');
			});
			$('.w_em').on('click',function(){
				$('.w_pregister').hide();
				$('.w_eregister').show();
				$('.w_uregister').hide();
				$('.w_phone').removeClass('w_hover');
				$('.w_em').addClass('w_hover');
				$('.w_ur').removeClass('w_hover');
			});
			$('.w_ur').on('click',function(){
				$('.w_pregister').hide();
				$('.w_eregister').hide();
				$('.w_uregister').show();
				$('.w_phone').removeClass('w_hover');
				$('.w_em').removeClass('w_hover');
				$('.w_ur').addClass('w_hover');
			});
		}
		//批量订货  分类点击
		if($('.w_classpf').length > 0){
			// $('.w_classpf').on('click','ul li',function(){
			// 	if($(this).hasClass('w_hauto')){
			// 		$(this).removeClass('w_hauto');
			// 		$(this).find('.w_classtag').removeClass('w_classhove');
			// 	}else{
			// 		$(this).addClass('w_hauto');
			// 		$(this).find('.w_classtag').addClass('w_classhove');
			// 	}
			// });
			$('.w_classtag1').on('click',function(){
				$('.w_classli1').toggleClass('w_hauto');
				$('.w_classtag1').toggleClass('w_classhove');
			});
			$('.w_classtag2').on('click',function(){
				$('.w_classli2').toggleClass('w_hauto');
				$('.w_classtag2').toggleClass('w_classhove');
			});
			$('.w_classtag3').on('click',function(){
				$('.w_classli3').toggleClass('w_hauto');
				$('.w_classtag3').toggleClass('w_classhove');
			});
			$('.w_classtag4').on('click',function(){
				$('.w_classli4').toggleClass('w_hauto');
				$('.w_classtag4').toggleClass('w_classhove');
			});
			$('.w_classtag5').on('click',function(){
				$('.w_classli5').toggleClass('w_hauto');
				$('.w_classtag5').toggleClass('w_classhove');
			});
			$('.w_classtag6').on('click',function(){
				$('.w_classli6').toggleClass('w_hauto');
				$('.w_classtag6').toggleClass('w_classhove');
			});
			$('.w_classtag7').on('click',function(){
				$('.w_classli7').toggleClass('w_hauto');
				$('.w_classtag7').toggleClass('w_classhove');
			});
			$('.w_classtag8').on('click',function(){
				$('.w_classli8').toggleClass('w_hauto');
				$('.w_classtag8').toggleClass('w_classhove');
			});
			$('.w_classtag9').on('click',function(){
				$('.w_classli9').toggleClass('w_hauto');
				$('.w_classtag9').toggleClass('w_classhove');
			});
			$('.w_classtag10').on('click',function(){
				$('.w_classli10').toggleClass('w_hauto');
				$('.w_classtag10').toggleClass('w_classhove');
			});
			$('.w_classtag11').on('click',function(){
				$('.w_classli11').toggleClass('w_hauto');
				$('.w_classtag11').toggleClass('w_classhove');
			});
			$('.w_classtag12').on('click',function(){
				$('.w_classli12').toggleClass('w_hauto');
				$('.w_classtag12').toggleClass('w_classhove');
			});
		}
		//单位
		if($('.w_detailunit').length > 0){
			
			$('.w_detailunit').on('click',function(){
				if($(this).hasClass('w_dtw')){
					$(this).removeClass('w_dtw');
					$(this).find('.w_detailunitc').hide();
				}else{
					$(this).addClass('w_dtw');
					$(this).find('.w_detailunitc').show();
				}
			});
			$('.w_detailunitc ul li').on('click',function(){

				$('.w_detailunitc').hide();
				var t=$(this).text();
				$(this).parent().parent().parent().find('span').text(t);
			});

		}
		//面包屑导航
		if($('.path').length > 0){
			$('.path li').eq(0).addClass('w_firstlist');
		}
		//菜单边
		if($('.mainNavFont').length > 0){
			var mnleng = $('.mainNavFont ul').children('li').length;
				o = mnleng-1;
			$('.mainNavFont li').eq(0).addClass('w_navfirstd');
			$('.mainNavFont li').eq(o).addClass('w_navlastd');
		}
		//批量订货字母
		if($('.w_letter').length > 0){
			$('.w_letter ul li a').on('click',function(){
				if($('.w_letter ul li a.w_letterup').length<3){
					if($(this).hasClass('w_letterup')){
						$(this).removeClass('w_letterup');
					}else{
						$(this).addClass('w_letterup');
					}
				}else if($(this).hasClass('w_letterup')){
					$(this).removeClass('w_letterup');
				}
				
			});
			$('.w_lreset').on('click',function(){
				$('.w_letter ul li a').removeClass('w_letterup');
			});
		}
