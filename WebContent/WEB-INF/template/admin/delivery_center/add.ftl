<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.deliveryCenter.add")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=1.2"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $areaId = $("#areaId");
	var $communityId = $("#communityId");
	var timeout;
	[@flash_message /]
	function getCommunity() {
		if ($areaId.val()>0) {
			$.ajax({
				url: "${base}/admin/tenant/get_community.jhtml",
				type: "GET",
				data: {areaId:$areaId.val()},
				dataType: "json",
				cache: false,
				success: function(map) {
					var opt="<option value=0>--请选择--</option>";
					for (var key in map) {  
						opt = opt +"<option value="+key+">"+map[key]+"</option>";
					}  
					$communityId.html(opt);
				}
			});
		} else {
			$communityId.html("<option value=0>--无--</option>");
		}
	}
	function areaSelect(){
		clearTimeout(timeout);
		timeout = setTimeout(function() {
			getCommunity();	
		}, 500);
	}
	
	// 地区选择
	$areaId.lSelect({
		url: "${base}/admin/common/area.jhtml",
		fn:areaSelect
	});
		
	// 表单验证
	$inputForm.validate({
		rules: {
			sn: "required",
			name: "required",
			contact: "required",
			areaId: "required",
			address: "required"
		}
	});

});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.deliveryCenter.add")}
	</div>
	<div id="map" style="position:absolute;margin-top:110px;margin-left:380px;display: none;">
    <div style="width:520px;height:340px;border:1px solid gray" id="mapform"></div>
    <p><input type="button" onclick="closeMap()" value="关闭" /></p>
  </div>
	<form id="inputForm" action="save.jhtml" method="post">
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>店内码:
				</th>
				<td>
					<input type="text" name="sn" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryCenter.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryCenter.contact")}:
				</th>
				<td>
					<input type="text" name="contact" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryCenter.area")}:
				</th>
				<td>
					<span class="fieldSet">
						<input type="hidden" id="areaId" name="areaId" />
					</span>
				</td>
			</tr>
			<tr>
				<th>
					所属社区:
				</th>
				<td>
					<span class="fieldSet">
				   	<select id="communityId" name="community.id">
									<option value="">--请选择--</option>
	  				</select>
					</span>
				</td>
			</tr>
			<!--<tr>
				<th>
					<span class="requiredField">*</span>地理位置:
				</th>
				<td >
					<input type="hidden" name="locationX" id="locationX" class="text"/>
					<input type="hidden" name="locationY" id="locationY" class="text"/>
					<span id="location"></span><input type="button"  class="button" onclick="openMap();" value="获取" />
				</td>
			</tr>-->
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryCenter.address")}:
				</th>
				<td >
					<input type="text" name="address" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryCenter.zipCode")}:
				</th>
				<td >
					<input type="text" name="zipCode" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryCenter.phone")}:
				</th>
				<td>
					<input type="text" name="phone" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryCenter.mobile")}:
				</th>
				<td>
					<input type="text" name="mobile" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryCenter.isDefault")}:
				</th>
				<td>
					<input type="checkbox" name="isDefault" />
					<input type="hidden" name="_isDefault" value="false" />
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryCenter.memo")}
				</th>
				<td>
					<input type="text" name="memo" class="text" maxlength="200" />
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
