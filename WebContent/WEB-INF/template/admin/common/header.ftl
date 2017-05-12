[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
﻿	<!-- HEADER -->
	<header class="navbar clearfix navbar-fixed-top" id="header">
		<div class="container">
				<div class="navbar-brand">
					<!-- COMPANY LOGO -->
					<a href="${base}/admin/common/main.jhtml">
						<img src="${base}/static/img/logo/logo.png" alt="Cloud Admin Logo" class="img-responsive" height="30" width="120">
					</a>
					<!-- /COMPANY LOGO -->
					<!-- TEAM STATUS FOR MOBILE -->
					<div class="visible-xs">
						<a href="#" class="team-status-toggle switcher btn dropdown-toggle">
							<i class="fa fa-users"></i>
						</a>
					</div>
					<!-- /TEAM STATUS FOR MOBILE -->
					<!-- SIDEBAR COLLAPSE -->
					<div id="sidebar-collapse" class="sidebar-collapse btn">
						<i class="fa fa-bars" 
							data-icon1="fa fa-bars" 
							data-icon2="fa fa-bars" ></i>
					</div>
					<!-- /SIDEBAR COLLAPSE -->
				</div>
				<!-- BEGIN TOP NAVIGATION MENU -->					
				<ul class="nav navbar-nav pull-right" style="min-width:210px">
				    [#if tenant!=null]
				    <li >
						 <a href="online.jhtml" style="padding-top: 13px;padding-left: 0px;padding-right: 0px;padding-bottom: 13px;">
						 [#if online!=1]
						      <img src="${base}/pic/sx.png" alt="" width="80px" />
						 [#else]
						      <img src="${base}/pic/xx.png" alt="" width="80px"/>
						 [/#if]
						 </a>
					</li>
				
					<li class="dropdown" id="chatBtn">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
						<i class="fa fa-envelope"></i>
						<span class="badge" id="chatNum"></span>
						</a>
					</li>
				    [/#if]
					<li style="display:none;" class="dropdown" id="header-message">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
						<i class="fa fa-envelope"></i>
						<span class="badge">3</span>
						</a>
						<ul class="dropdown-menu inbox">
							<li class="dropdown-title">
								<span><i class="fa fa-envelope-o"></i> 3 Messages</span>
								<span class="compose pull-right tip-right" title="Compose message"><i class="fa fa-pencil-square-o"></i></span>
							</li>
							<li>
								<a href="#">
									<img src="${base}/static/img/avatars/avatar2.jpg" alt="" />
									<span class="body">
										<span class="from">Jane Doe</span>
										<span class="message">
										Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse mole ...
										</span> 
										<span class="time">
											<i class="fa fa-clock-o"></i>
											<span>Just Now</span>
										</span>
									</span>
									 
								</a>
							</li>
							<li>
								<a href="#">
									<img src="${base}/static/img/avatars/avatar1.jpg" alt="" />
									<span class="body">
										<span class="from">Vince Pelt</span>
										<span class="message">
										Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse mole ...
										</span> 
										<span class="time">
											<i class="fa fa-clock-o"></i>
											<span>15 min ago</span>
										</span>
									</span>
									 
								</a>
							</li>
							<li>
								<a href="#">
									<img src="${base}/static/img/avatars/avatar8.jpg" alt="" />
									<span class="body">
										<span class="from">Debby Doe</span>
										<span class="message">
										Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse mole ...
										</span> 
										<span class="time">
											<i class="fa fa-clock-o"></i>
											<span>2 hours ago</span>
										</span>
									</span>
									 
								</a>
							</li>
							<li class="footer">
								<a href="#">See all messages <i class="fa fa-arrow-circle-right"></i></a>
							</li>
						</ul>
					</li>
					<!-- END TODO DROPDOWN -->
					<!-- BEGIN USER LOGIN DROPDOWN -->
					<li class="dropdown user" id="header-user">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<img alt="" src="${base}/static/img/avatars/avatar3.jpg" />
							<span class="username">[@shiro.principal /]</span>
							<i class="fa fa-angle-down"></i>
						</a>
						<ul class="dropdown-menu">
							<li><a href="../profile/edit.jhtml" target="iframe"><i class="fa fa-user"></i> ${message("admin.main.profile")}</a></li>
							<li><a href="../logout.jsp" target="_top"><i class="fa fa-power-off"></i> ${message("admin.main.logout")}</a></li>
						</ul>
					</li>
					<!-- END USER LOGIN DROPDOWN -->
				</ul>
				<!-- END TOP NAVIGATION MENU -->
		</div>
	</header>
	<!--/HEADER -->
