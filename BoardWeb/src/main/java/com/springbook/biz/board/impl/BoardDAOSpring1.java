package com.springbook.biz.board.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.springbook.biz.board.BoardVO;

// 1. JdbcDaoSupport 클래스 상속하기
// => 메서드에서 매번 connection, mapping 해 줄 필요없이 그냥 JdbcDaoSupport.getJdbcTemplate() 쓰면 된다.
// ==> return jdbcTemplate객체

// DAO(Data Access Object)
// 이 DAO 사용하려면 BoardServiceImple에서 @Autowired된 객체 타입 이걸로 바꿔줘야 함!
@Repository
public class BoardDAOSpring1 extends JdbcDaoSupport {

	// JDBC 관련 변수는 이제 필요없다. xml설정과 dbcp 이용으로 자동화했음

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

	@Autowired
	// applicationContext-board.xml 에다가 DataSource를 bean 등록해줬기 때문에
	// 컨테이너가 이 메서드를 @Autowired로 자동 호출할 때, bean등록되어 메모리에 존재하면, 객체를 인자로 넘김.
	public void setSuperDataSurce(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	// A factory for connections to the physical data source that this DataSource
	// object represents. An alternative to the DriverManager facility, a DataSource
	// objectis the preferred means of getting a connection. An object that
	// implementsthe DataSource interface will typically beregistered with a naming
	// service based on theJava™ Naming and Directory (JNDI) API.
	// => DataSource 클래스는 DriverManager의 더 좋은 대안이다. connection 얻을 때 이거 쓰삼

	// CRUD 기능의 메소드 구현 ========================================
	// 글 등록 create
	public void insertBoard(BoardVO vo) {
		System.out.println("===> Spring JDBC로 insertBoard() 기능 처리");
		getJdbcTemplate().update(BOARD_INSERT, vo.getTitle(), vo.getWriter(), vo.getContent());
	}

	// 글 수정 update
	public void updateBoard(BoardVO vo) {
		System.out.println("===> Spring JDBC로 updateBoard() 기능 처리");
		getJdbcTemplate().update(BOARD_UPDATE, vo.getTitle(), vo.getContent(), vo.getSeq());
	}

	// 글 삭제 delete
	public void deleteBoard(BoardVO vo) {
		System.out.println("===> Spring JDBC로 deleteBoard() 기능 처리");
		getJdbcTemplate().update(BOARD_DELETE, vo.getSeq());
	}

	// 글 상세 조회 read
	public BoardVO getBoard(BoardVO vo) {
		System.out.println("===> Spring JDBC로 getBoard() 기능 처리");
		Object[] args = { vo.getSeq() }; // args말고 object 하나만 하고 싶어도, 메서드 인자로 배열이 들어가야해서 불가
		return getJdbcTemplate().queryForObject(BOARD_GET, args, new BoardRowMapper());
	}

	// 글 목록 조회 read
	public List<BoardVO> getBoardList(BoardVO vo) {
		System.out.println("===> Spring JDBC로 getBoardList() 기능 처리");
		Object[] args = { vo.getSearchKeyword() };
		// 검색조건을 vo에서 가져와서 검색 조건에 따른 쿼리 설정
		// queryForList()는 테이블에 컬럼이 하나인 경우에만 가능하다. 지금 쓸 경우 다음과 같은 에러 발생
		// IncorrectResultSetColumnCountException: Incorrect column count: expected 1, actual 6
		// 때문에 query()를 쓰고, RowMapper도 지정해줘야한다.
		if (vo.getSearchCondition().equals("TITLE")) {
			return getJdbcTemplate().query(BOARD_LIST_T, args, new BoardRowMapper());
		} else if (vo.getSearchCondition().equals("CONTENT")) {
			return getJdbcTemplate().query(BOARD_LIST_C, args, new BoardRowMapper());
		}
		return null;
	}

}
