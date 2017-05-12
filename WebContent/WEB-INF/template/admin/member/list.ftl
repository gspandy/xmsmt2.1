<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.member.list")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
    <LINK rel=stylesheet type=text/css href="${base}/resources/admin/css/jquery.msgbox.css">
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
    <SCRIPT type=text/javascript src="${base}/resources/admin/js/jquery.dragndrop.min.js"></SCRIPT>
    <SCRIPT type=text/javascript src="${base}/resources/admin/js/jquery.msgbox.min.js"></SCRIPT>

    <style type="text/css">
        .moreTable th {
            width: 80px;
            line-height: 25px;
            padding: 5px 10px 5px 0px;
            text-align: right;
            font-weight: normal;
            color: #333333;
            background-color: #f8fbff;
        }

        .moreTable td {
            line-height: 25px;
            padding: 5px;
            color: #666666;
        }

        .promotion {
            color: #cccccc;
        }
    </style>
    <script type="text/javascript">
        $().ready(function() {

        [@flash_message /]

            var $listForm = $("#listForm");
            var $impButton = $("#impButton");
            var $filterSelect = $("#filterSelect");
            var $filterOption = $("#filterOption a");
            $filterSelect.mouseover(function() {
		         var $this = $(this);
		         var offset = $this.offset();
		         var $menuWrap = $this.closest("div.menuWrap");
		         var $popupMenu = $menuWrap.children("div.popupMenu");
		         $popupMenu.css({left: offset.left, top: offset.top + $this.height() + 2}).show();
		         $menuWrap.mouseleave(function() {
			     $popupMenu.hide();
		        });
	         });
	         // 筛选选项
	       $filterOption.click(function() {
		      var $this = $(this);
		      var $dest = $("#" + $this.attr("name"));
		      if ($this.hasClass("checked")) {
			       $dest.val("");
		         } else {
			       $dest.val($this.attr("val"));
		       }
		        $listForm.submit();
		        return false;
	         });
            $impButton.click(function() {
                    $.dialog({
                title: "导入会员",
            [@compress single_line = true]
                content: '<form action="${base}/admin/member/importData.jhtml" id="impForm" method="post" enctype="multipart/form-data">
                        <table id="moreTable" class="moreTable">
                    <tr>
                    <th>
            文件(.xls):
                <\/th>
                    <td>
            <input type="file" id="file" name="file"/> <input type="submit" value="导入" class="button">
            <\/td>
            <\/tr>
            <\/table></form>',
            [/@compress]
                width: 470,
                modal: true,
                ok : "关闭"

            });
            $(".dialogBottom .button")[1].style.display = "none";

        });
        var $shopkeeper = $("#shopkeeper");

        $shopkeeper.click(function() {

            var ids="";
            $('input[type="checkbox"][name="ids"]:checked').each(
                    function() {
                        if ("checked" == $(this).attr("checked")) {
                            ids += $(this).attr('value')+',';
                        }
                    }
            );

            if(ids.length == 0) {
                $.dialog({
                    type: "warn",
                    content: "请选择会员"
                });
                $(".dialogBottom .button")[1].style.display = "none";

                return;
            }
            $("#memberIds").val(ids);
            new $.msgbox({
                closeImg: 'close.gif',
                content:'${base}/admin/smContent/listSel.jhtml',
                type :'iframe',
                title: '短信模板'
            }).show();


            $(".jMsgbox-contentWrap")[0].parentNode.style.height="400px";
            $(".jMsgbox-contentWrap")[0].style.height="400px";
            $(".jMsgbox-contentWrap")[0].parentNode.style.width="600px";
            $(".jMsgbox-mainWrap")[0].style.width="55%";
            debugger
//             $(".jMsgbox-mainWrap")[0].style.marginTop="-50%";
           
            topf();
        });

    });

        function setSmIds(smIds) {
           if(smIds) {
               if(smIds.indexOf(",") > -1) {
                   $.dialog({
                       type: "warn",
                       content: "只能选择一条记录"
                   });
                   $(".dialogBottom .button")[1].style.display = "none";
               } else {
                   $.ajax({
                       url: "${base}/admin/member/reqShopkeeper.jhtml",
                       type: "POST",
                       data: {memberIds: $("#memberIds").val(), smContentIds:smIds},
                       dataType: "json",
                       cache: false,
                       success: function(message) {
                          if(message) {
                              if(message.type == "success") {
                                  $(".jMsgbox-closeWrap")[0].click();
                                  $.message(message);
                              }
                          }
                       }
                   });
               }
           } else {
               $.dialog({
                   type: "warn",
                   content: "请选择一条短信模板"
               });
               $(".dialogBottom .button")[1].style.display = "none";
           }
        }
    </script>
</head>
<body>
<input type="hidden" value="" id="memberIds">
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.member.list")} <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list.jhtml" method="get">
	<input type="hidden" id="isRegister" name="isRegister" value="[#if isRegister??]${isRegister?string("true", "false")}[/#if]" />
		<div class="bar">
          
			<div class="buttonWrap">
                <a href="javascript:;" id="impButton" class="iconButton"><span class="addIcon">&nbsp;</span>导入会员</a>
				<a href="javascript:;" class="iconButton" id="shopkeeper">
					<span class="editIcon">&nbsp;</span>邀请店长
				</a>
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div class="menuWrap">
					<a href="javascript:;" id="pageSizeSelect" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<div class="popupMenu">
						<ul id="pageSizeOption">
							<li>
								<a href="javascript:;"[#if page.pageSize == 10] class="current"[/#if] val="10">10</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 20] class="current"[/#if] val="20">20</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 50] class="current"[/#if] val="50">50</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 100] class="current"[/#if] val="100">100</a>
							</li>
						</ul>
					</div>
				</div>
				<div class="menuWrap">
					<a href="javascript:;" id="filterSelect" class="button">
						会员筛选<span class="arrow">&nbsp;</span>
					</a>
					<div class="popupMenu">
						<ul id="filterOption" class="check">
							<li>
								<a href="javascript:;" name="isRegister" val="true"[#if isRegister?? && isRegister] class="checked"[/#if]>已注册</a>
							</li>
							<li>
								<a href="javascript:;" name="isRegister" val="false"[#if isRegister?? && !isRegister] class="checked"[/#if]>未注册</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<a href="${base}/download/xmsmt.xls" id="impButton" class="iconButton"><span class="addIcon">&nbsp;</span>下载模板</a>
			<div class="menuWrap">
				<div class="search">
					<span id="searchPropertySelect" class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<div class="popupMenu">
					<ul id="searchPropertyOption">
						<li>
							<a href="javascript:;"[#if page.searchProperty == "mobile"] class="current"[/#if] val="mobile">${message("Member.mobile")}</a>
						</li>
					</ul>
				</div>
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="username">${message("Member.name")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="email">${message("Member.mobile")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createDate">${message("admin.common.createDate")}</a>
				</th>
				<th>
					是否注册
				</th>
				<th>
					<span>${message("admin.common.handle")}</span>
				</th>
			</tr>
			[#list page.content as member]
				<tr>
					<td>
						<input type="checkbox" name="ids"  value="${member.id}" />
					</td>
					<td>
						${member.name}
					</td>
					<td>
						${member.mobile}
					</td>
					<td>
						<span title="${member.createDate?string("yyyy-MM-dd HH:mm:ss")}">${member.createDate}</span>
					</td>
					<td>
						[#if member.loginDate!=null]
							已注册
						[/#if]
						[#if member.loginDate==null]
							未注册
						[/#if]
					</td>
					<td>
						<a href="view.jhtml?id=${member.id}">[${message("admin.common.view")}]</a>
					</td>
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
<script type="text/javascript">
function topf(){
	
	var topH=window.parent.topHeight();
	$("div.jMsgbox-mainWrap").css("top",topH);

}

	</script>
</html>