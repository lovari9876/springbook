<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

<!-- Popper JS -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<title>글 목록</title>

<style>
h1 {
	text-align: center;
}

p {
	text-align: center;
}

div {
	text-align: center;
}

#container {
	margin: 0 auto;
}

table {
	margin-left: auto;
	margin-right: auto;
}

th {
	background-color: #cddc3994; /* hex code of lime green */
}
</style>

</head>
<body>

	<div id="container">
	
		<h1>글 목록</h1>
		<hr>
		<h3>
			${userName}님 환영합니다...<a href="logout.do">Log-out</a>
		</h3>

		<!-- 검색 시작 -->
		<form action="getBoardList.do" method="post">
			<table border="1" cellpadding="0" cellspacing="0" width="700">
				<tr>
					<td align="right">
						<select name="searchCondition">
						<!-- 
								<option value="TITLE">제목</option>
								<option value="CONTENT">내용</option> -->
								
							<c:forEach items="${conditionMap }" var="option">
								<option value="${option.value }">${option.key }</option>
							</c:forEach>
								
						</select>
					<input name="searchKeyword" type="text" />
					<input type="submit" value="검색" />
					</td>					
				</tr>
			</table>
		</form>
		<!-- 검색 종료 -->
	
		<table border="1" cellpadding="0" cellspacing="0" width="700">
			<tr>
				<th width="100">번호</th>
				<th width="200">제목</th>
				<th width="150">작성자</th>
				<th width="150">등록일</th>
				<th width="100">조회수</th>
			</tr>
			<c:forEach items="${boardList }" var="board">		
				<tr>
					<td>${board.seq }</td>
					<td align="left"><a href="getBoard.do?seq=${board.seq }">${board.title }</a></td>
					<td>${board.writer }</td>
					<td>${board.regDate }</td>
					<td>${board.cnt }</td>
				</tr>
			</c:forEach>			
		</table>
		<br>
		<a href="insertBoard.jsp">새글 등록</a>
		
	</div>
	
</body>
</html>