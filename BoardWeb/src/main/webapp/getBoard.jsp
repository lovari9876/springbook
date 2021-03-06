<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

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

<title>글 상세</title>

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
	
		<h1>글 상세</h1>
		<hr>
		<h3>
			<a href="logout.do">Log-out</a>
		</h3>
		
		<!-- session에 저장된 attribute객체 board를 EL로 꺼내쓸 수 있다. -->
		<form action="updateBoard.do" method="post">
			<input name="seq" type="hidden" value="${board.seq}" />
			<table border="1" cellpadding="0" cellspacing="0" >
				<tr>
					<th width="70">제목</th>
					<td align="left">
						<input name="title" type="text" value="${board.title}" />
					</td>					
				</tr>
				<tr>
					<th>작성자</th>
					<td align="left">${board.writer}</td>
				</tr>
				<tr>
					<th>내용</th>
					<td align="left">
						<textarea name="content" cols="40" rows="10">${board.content}</textarea>
					</td>					
				</tr>
				<tr>
					<th>등록일</th>
					<td align="left">${board.regDate}</td>					
				</tr>
				<tr>
					<th>조회수</th>
					<td align="left">${board.cnt}</td>					
				</tr>
				<tr>
					<td align="center" colspan="2">
						<input type="submit" value="글 수정" />
					</td>
				</tr>		
			</table>
		</form>
		
		<br>
		<hr>
		
		<a href="insertBoard.jsp">새글 등록</a>&nbsp;&nbsp;|&nbsp;&nbsp;
		<a href="deleteBoard.do?seq=${board.seq}">글 삭제</a>&nbsp;&nbsp;|&nbsp;&nbsp;
		<a href="getBoardList.do">목록으로</a>
		
	</div>
	
</body>
</html>