<%--
  Created by IntelliJ IDEA.
  User: ab
  Date: 15-9-7
  Time: 下午9:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link href="<%=request.getContextPath()%>/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
    <LINK rel=stylesheet type=text/css href="<%=request.getContextPath()%>/resources/admin/css/jquery.msgbox.css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/admin/js/jquery.js"></script>
    <SCRIPT type=text/javascript src="<%=request.getContextPath()%>/resources/admin/js/jquery.dragndrop.min.js"></SCRIPT>
    <SCRIPT type=text/javascript src="<%=request.getContextPath()%>/resources/admin/js/jquery.msgbox.min.js"></SCRIPT>
</head>
<script type="text/javascript">

    $().ready(function() {
        new $.msgbox({
            closeImg: 'close.gif',
            height:800,
            width:800,
            content:'<%=request.getContextPath()%>/admin/smContent/listSel.jhtml',
            type :'iframe',
            title: '从外部关闭msgbox'
        }).show();

                 debugger
        $("#test").click(function () {

        });
    });

    function colseSel() {
        debugger
        $(".jMsgbox-closeWrap")[0].click();
        var ifr = $("iframe")[0];
        if(ifr) {
            $("#xx").val(ifr.contentWindow.document.getElementById("username").value);
        }
    }
</script>
<body>
        <input id="test" name="test" value="test" type="button" >
        <input id="xx" type="text">
</body>
</html>