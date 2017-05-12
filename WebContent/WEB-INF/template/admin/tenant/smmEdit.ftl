<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title>商家档案 - Powered By rsico</title>
    <meta name="author" content="rsico Team" />
    <meta name="copyright" content="rsico" />
    <link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
    <script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
    <script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
    <script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
    <script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
    <style type="text/css">
        .authorities label {
            min-width: 120px;
            _width: 120px;
            display: block;
            float: left;
            padding-right: 4px;
            _white-space: nowrap;
        }
    </style>
    <script type="text/javascript">
        $().ready(function() {

            var $inputForm = $("#inputForm");
            var $selectAll = $("#inputForm .selectAll");

        [@flash_message /]

            $selectAll.click(function() {
                var $this = $(this);
                var $thisCheckbox = $this.closest("tr").find(":checkbox");
                if ($thisCheckbox.filter(":checked").size() > 0) {
                    $thisCheckbox.prop("checked", false);
                } else {
                    $thisCheckbox.prop("checked", true);
                }
                return false;
            });

            // 表单验证
            $inputForm.validate({
                rules: {
                    leftCount: "required"
                }
            });


            $("#leftCount").change(function() {
                var $smsCount = $("#smsCount").val();
                if(!$smsCount) {
                    $("#smsCount").val(0);
                }

                var $leftCount = $("#leftCount").val();
                if(!$leftCount) {
                    $("#leftCount").val(0);
                }

                var $usedCount = $("#usedCount").val();
                if(!$usedCount) {
                    $("#usedCount").val(0);
                }

                $("#smsCount").val(Number($leftCount) + Number($usedCount));
            });

        });
    </script>
</head>
<body>
<div class="path">
    <a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;短信记录
</div>
<form id="inputForm" action="update.jhtml" method="post">
    <input type="hidden" name="id" value="${tenantSm.id}" />
    <table class="input">

        <tr>

            <th>
                短信总数                    :
            </th>
            <td>
                <input type="text" name="smsCount" id="smsCount" class="text" maxlength="200"  value="${tenantSm.smsCount}" readonly="true" />
            </td>
        </tr>

        <tr>

            <th>
                <span class="requiredField">*</span>剩余条数:
            </th>
            <td>
                <input type="text" name="leftCount" id="leftCount" class="text" maxlength="200" value="${tenantSm.leftCount}" />
            </td>
        </tr>

        <tr>
            <th>
                已用条数:
            </th>
            <td>
                <input type="text" name="usedCount" id="usedCount" class="text"  value="${tenantSm.usedCount}" readonly="true" />
            </td>
        </tr>

        <tr>

            <th>
                备注:
            </th>
            <td>
                <textarea name="remark" class="text">${tenantSm.remark}</textarea>
            </td>
        </tr>

        <tr>
            <td colspan="2">
                &nbsp;
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