<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.lickthespring.web.entity.Comment" %>
<%@ page import="com.lickthespring.web.service.CommentService" %>
<%
    final CommentService commentService = CommentService.getInstance();
    int id = (int) request.getAttribute("id");
%>
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>방명록 상세보기</title>
    </head>
    <body>
        <h1>방명록 상세보기</h1>
        <div style="margin-bottom: 10px;"><a href="/">메인으로</a></div>

        <%
            try {
                Comment comment = commentService.findById(id - 1);
        %>
        <div style="border:1px solid #CCC; margin:5px 10px; padding:10px;">
            <div style="font-weight:bold"><%=comment.getName()%></div>
            <div><%=comment.getComment()%></div>
        </div>
        <%
            } catch (IllegalArgumentException e) {
                out.print("존재하지 않는 방명록입니다.");
                return;
            }
        %>
</html>