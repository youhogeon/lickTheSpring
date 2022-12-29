<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>메인</title>
    </head>
    <body>
        <h1>Index</h1>
        <a href="/comments">방명록 모두보기</a>

        <form method="post" action="/comments">
            <input type="text" name="name" placeholder="Name">
            <br>
            <textarea name="comment" placeholder="Comment"></textarea>
            <br>
            <button type="submit">Submit</button>
        </form>
    </body>
</html>
