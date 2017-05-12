<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>社区资料 - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=1.2"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $areaId = $("#areaId");
	
	[@flash_message /]
	
		// 地区选择
	$areaId.lSelect({
		url: "${base}/common/area.jhtml"
	});
	// 表单验证
	$inputForm.validate({
		rules: {
			code: "required",
			name: "required",
			areaId: "required",
			status: "required",
			tagIds: "required"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 社区资料
	</div>
	<div id="map" style="position:absolute;margin-top:110px;margin-left:380px;display: none;">
	    <div style="width:520px;height:340px;border:1px solid gray" id="mapform"></div>
	    <p><input type="button" onclick="closeMap()" value="关闭" /></p>
	</div>
	<form id="inputForm" action="save.jhtml" method="post"  enctype="multipart/form-data">
		<input type="hidden" name="id" value="${community.id}" />
		<input type="hidden" name="image" value="${community.image}" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>社区编码:
				</th>
				<td>
					<input type="text" name="code" class="text"/>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>社区名称:
				</th>
				<td>
					<input type="text" name="name" class="text" maxlength="255" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>主题图片:
				</th>
				<td>
					<span class="fieldSet">
				    <input type="file" id="file" name="file"/>
					</span>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>行政地区:
				</th>
				<td>
					<span class="fieldSet">
						<input type="hidden" id="areaId" name="areaId" />
					</span>
				</td>
			</tr>
			<tr>
			<th>
				<span class="requiredField">*</span>地理位置:
			</th>
			<td >
				<input type="hidden" name="locationX" id="locationX" class="text"/>
				<input type="hidden" name="locationY" id="locationY" class="text"/>
				<span id="location"></span><input type="button"  class="button" onclick="openMap();" value="获取" />
			</td>
			</tr>
		 	<tr>
				<th>
					<span class="requiredField">*</span>标签:
				</th>
				<td>
					[#list tags as tag]
						<label>
							<input type="checkbox" name="tagIds" value="${tag.id}"[#if community.tags?seq_contains(tag)] checked="checked"[/#if] />${tag.name}
						</label>
					[/#list]
				</td>
			</tr>
		 	<tr>
				<th>
					<span class="requiredField">*</span>状态:
				</th>
				<td>
						<select name="status">
							<option value="">--请选择--</option>
							<option value="wait" >待开通</option>
							<option value="open" >已开通</option>
							<option value="close" >已关闭</option>
						</select>
				</td>
			</tr>
			<tr>
				<th>
					描述:
				</th>
				<td>
					<textarea id="editor" name="descr" class="editor"></textarea>
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml'" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
<script type="text/javascript">
var map = new BMap.Map("mapform");                        // 创建Map实例
map.centerAndZoom("厦门市", 18);  
map.addControl(new BMap.NavigationControl());
map.addControl(new BMap.ScaleControl());
map.addControl(new BMap.OverviewMapControl());
map.addEventListener("click",function(e){   //单击地图，形成折线覆盖物
  newpoint = new BMap.Point(e.point.lng,e.point.lat);
  $("#locationX").val(e.point.lng);
  $("#locationY").val(e.point.lat);
  $("#location").html("(x:"+e.point.lng+",y:"+e.point.lat+")");
  map.clearOverlays();
  var mkr = new BMap.Marker(newpoint);
  map.addOverlay(mkr);  
});
function closeMap() {
  $("#map").hide();
}
function openMap()  {
					$.ajax({
						url: "${base}/common/area_key.jhtml",
						type: "GET",
						data: {id:$("#areaId").val()},
						dataType: "json",
						cache: false,
						success: function(area) {
						  map.centerAndZoom(area.fullName, 15);     // 初始化地图,设置中心点坐标和地图级别
						}
	        });
  $("#map").show();
}
</script>