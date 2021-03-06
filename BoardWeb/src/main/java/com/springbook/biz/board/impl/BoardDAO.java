package com.springbook.biz.board.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.springbook.biz.board.BoardVO;
import com.springbook.biz.common.JDBCUtil;

// query들을 다 mysql버젼으로 수정하여 작성함

// DAO(Data Access Object)¿
@Repository("BoardDAO")
public class BoardDAO {
	// JDBC 관련 변수
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet rs = null;

	// SQL 명령어들
	private final String BOARD_INSERT = "insert into board(seq, title, writer, content) "
			+ "values((select ifnull(max(seq), 0)+1 from board temp),?,?,?)";
	// 예 ) SELECT IFNULL(SALARY, 0) FROM EMPLOYEE_SALARY
	// 설명 - SALARY 값이 NULL 이면 0을, NULL 이 아니면 SALARY 값을 출력
	// ** MySql에서는 subquery에서 같은 테이블 쓸 때 alias 없이 쓸 수 없음
	private final String BOARD_UPDATE = "update board set title=?, content=? where seq=?";
	private final String BOARD_DELETE = "delete from board where seq=?";
	private final String BOARD_GET = "select * from board where seq=?";
	private final String BOARD_LIST = "select * from board order by seq desc";
	// 검색 위한 쿼리
	// %는 0개 이상의 문자를 의미하는 와일드 카드=> ?가 ''일 때, '%%'처리되어 모든 결과 리턴
	// 책처럼 '%'||?||'%'로 하면 orcle에서는 검색 결과 리턴. 그러나 mysql에서는 모든 row를 리턴한다.
	// => oracle에서는 '%keyword%'나 '%'||'keyword'||'%' 가 같은 검색 결과를 리턴하지만,
	// => mysql에서는 첫번째는 검색결과, 두번째는 전체row를 리턴하는 것이다.
	// ==> mysql에서는 ||연산자가 정확히 합집합의 결과를 가져오므로, preparedStatement를 사용하기 위해서는
	//		CONCAT('%', ?, '%')을 써야 한다. 그러면, 따옴표 문제와 or연산자 문제가 전부 해결된다.
	private final String BOARD_LIST_T = "select * from board where title like CONCAT('%', ?, '%') order by seq desc";
	private final String BOARD_LIST_C = "select * from board where content like CONCAT('%', ?, '%') order by seq desc";

	// CRUD 기능의 메소드 구현 ========================================
	// 글 등록 create
	public void insertBoard(BoardVO vo) {
		System.out.println("===> JDBC로 insertBoard() 기능 처리");
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(BOARD_INSERT);
			stmt.setString(1, vo.getTitle());
			stmt.setString(2, vo.getWriter());
			stmt.setString(3, vo.getContent());
			stmt.executeUpdate(); // 정보 읽어보삼
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}

	// 글 수정 update
	public void updateBoard(BoardVO vo) {
		System.out.println("===> JDBC로 updateBoard() 기능 처리");
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(BOARD_UPDATE);
			stmt.setString(1, vo.getTitle());
			stmt.setString(2, vo.getContent());
			stmt.setInt(3, vo.getSeq());
			stmt.executeUpdate(); // 정보 읽어보삼
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}

	// 글 삭제 delete
	public void deleteBoard(BoardVO vo) {
		System.out.println("===> JDBC로 deleteBoard() 기능 처리");
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(BOARD_DELETE);
			stmt.setInt(1, vo.getSeq());
			stmt.executeUpdate(); // 정보 읽어보삼
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}

	// 글 상세 조회 read
	public BoardVO getBoard(BoardVO vo) {
		System.out.println("===> JDBC로 getBoard() 기능 처리");
		BoardVO board = null; // return 위해서 여기서 variable 만들기
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(BOARD_GET);
			stmt.setInt(1, vo.getSeq());
			rs = stmt.executeQuery();
			if (rs.next()) {
				board = new BoardVO();
				board.setSeq(rs.getInt("SEQ"));
				board.setTitle(rs.getString("TITLE"));
				board.setWriter(rs.getString("WRITER"));
				board.setContent(rs.getString("CONTENT"));
				board.setRegDate(rs.getDate("REGDATE"));
				board.setCnt(rs.getInt("CNT"));
				// 하나만 받을 거니깐 if문으로 시작해서 그냥 이렇게 끝낸다~
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}

		return board;
	}

	// 글 목록 조회 read
	public List<BoardVO> getBoardList(BoardVO vo) {
		System.out.println("===> JDBC로 getBoardList() 기능 처리");
		List<BoardVO> boardList = new ArrayList<BoardVO>();
		try {
			conn = JDBCUtil.getConnection();
			// 검색조건을 vo에서 가져와서 검색 조건에 따른 쿼리 설정
			if (vo.getSearchCondition().equals("TITLE")) {
				stmt = conn.prepareStatement(BOARD_LIST_T);
			} else if (vo.getSearchCondition().equals("CONTENT")) {
				stmt = conn.prepareStatement(BOARD_LIST_C);
			}
			// 검색어를 ?에 할당
			stmt.setString(1, vo.getSearchKeyword());
			rs = stmt.executeQuery();
			while (rs.next()) {
				BoardVO board = new BoardVO();
				board.setSeq(rs.getInt("SEQ"));
				board.setTitle(rs.getString("TITLE"));
				board.setWriter(rs.getString("WRITER"));
				board.setContent(rs.getString("CONTENT"));
				board.setRegDate(rs.getDate("REGDATE"));
				board.setCnt(rs.getInt("CNT"));
				boardList.add(board);
				// resultSet에서 커서 이동하면서 한 column 씩 읽어들이고, 하나의 board 객체를 만족하면
				// boardList에 add하면서 rs 커서가 마지막에 위치할 때까지 읽어들인다.
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}

		return boardList;
	}

}
