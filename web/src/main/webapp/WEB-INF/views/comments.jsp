<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.lickthespring.web.entity.Comment" %>
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>전체 방명록</title>
    </head>
    <body>
        <h1>전체 방명록</h1>
        <div style="margin-bottom: 10px;"><a href="/">메인으로</a></div>

        <%
            List<Comment> comments = (List<Comment>)request.getAttribute("comments");
            int count = (int) request.getAttribute("count");

            for (int id = 0; id < count; id++) {
                Comment comment = comments.get(id);
        %>
        <div style="border:1px solid #CCC; margin:5px 10px; padding:10px;">
            <div><%=(id+1)%> / ${count}</div>
            <div style="font-weight:bold"><%=comment.getName()%></div>
            <div><%=comment.getComment()%></div>
        </div>
        <%
            }
        %>
    </body>
</html>