[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
﻿<!-- SIDEBAR -->
<div id="sidebar" class="sidebar sidebar-fixed">
	<div class="sidebar-menu nav-collapse">
		<ul>
			[#list mapResult.keySet() as mkey]
				[#list mapResult.get(mkey) as permission]
				[@shiro.hasPermission name = permission.authority]
				<li class="has-sub">
					<a href="javascript:;" class=""> <i class="fa fa-tachometer fa-fw"></i>
						<span class="menu-text">${message("Authority.AuthorityGroup."+mkey)}</span> <span class="arrow"></span>
					</a>
					<ul class="sub">
						[#list mapResult.get(mkey) as authority]
						[@shiro.hasPermission name = authority.authority]
						<li>
							<a class="" href="${base}/admin${authority.url}" target="iframe"><span class="sub-menu-text">${authority.name}</span></a>
						</li>
						[/@shiro.hasPermission]
						[/#list]
					</ul>
				</li>
				[#break /]
				[/@shiro.hasPermission]
				[/#list]
			[/#list]	
		</ul>
	</div>
</div>
