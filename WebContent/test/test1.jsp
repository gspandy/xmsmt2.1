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
</head>
<body>
      <div style="width: 50px;height: 50px">
     <form action="<%=request.getContextPath()%>/import/importData.jhtml" method="post" enctype="multipart/form-data">
         <table>
             <tr>
                 <td><input type="file" id="file" name="file"/></td>

                 <td><input type="submit" value="导入"></td>
             </tr>
         </table>
     </form>
          <div>
</body>
</html>