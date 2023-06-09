package com.goodee.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.goodee.model.vo.Member;

public class MemberDao {	// 데이터 처리

/*
 * DAO(Data Access Object)
 * : DB에 직접 접근해서 사용자의 요청에 맞는 SQL문을 실행한 후 결과를 받음.(JDBC이용)
 *   결과를 Controller로 반환함.
 * 	
 * >> Statement와 PreparedStatement의 특징
 *  - 두 객체 모두 sql문을 실행하고 결과를 받아내는 객체, Connection객체를 이용해서 생성(둘 중 하나 이용)
 *  - Statement가 PreparedStatement의 부모(상속구조)
 *  
 *  - Statement와 PreparedStatement의 차이점
 *   - Statement는 sql문을 전달하면서 바로 실행하는 객체(즉, sql문을 완성형태로 만들어야 함 => 사용자가 입력한 값이 완전히 채워진 상태, 그렇지 않으면 SQL예외 발생)  
 * 
 *  > 기존의 Statement 방식
 *   1) Connection 객체를 통해 Statement 객체 생성
 *     - stmt = conn.createStatement();
 *   2) Statement 객체를 통해서 완성된 sql문 실행 및 결과 받기
 *     - rset = stmt.executeXXX(완성된 sql문);
 *   
 *  - PreparedStatement 같은 경우 미완성 sql문을 잠시 보관해두었다가 나중에 완성한 후 실행할 수 있는 객체
 *    (즉, 사용자가 입력한 값을 채워두지 않고 각각 들어갈 공간(?사용))을 미리 확보만 해두면 됨)
 *   
 *  > PreparedStatement 방식
 *   1) Connection 객체를 통해 PreparedStatement 객체 생성
 *     - pstmt = conn.prepareStatement(미완성 sql문);
 *   2) pstmt에 담긴 sql문이 미완성 상태일 때, 우선 완성시켜야 함
 *     - pstmt.setXXX(1, 대체할 값);
 *       pstmt.setXXX(2, 대체할 값);
 *   3) sql문 실행 및 결과 받기
 *     - 결과(rset 또는 int변수) = pstmt.executeXXX();   
 */
	
	public int insertMember(Member m) {
		
		int result = 0;
		
		Connection		   conn = null;
		PreparedStatement pstmt = null;
//		Statement  stmt = null;
		
//		String sql = "INSERT INTO MEMBER VALUES(SEQ_UNO.NEXTVAL, '"+ m.getUserId()  + "', "
//															  + "'"+ m.getUserPwd() + "', "
//															  + "'"+ m.getUserName() + "', "
//															  + "'"+ m.getGender() + "', "
//															  	   + m.getAge() + ", "
//															  + "'"+ m.getEmail() + "', "
//															  + "'"+ m.getPhone() + "', "
//															  + "'"+ m.getAddress() + "', "
//															  + "'"+ m.getHobby() + "', SYSDATE)";
						
		String sql = "INSERT INTO MEMBER VALUES(SEQ_UNO.NEXTVAL,?,?,?,?,?,?,?,?,?,SYSDATE)";
		//위의 넣어줄 값들을 물음표로 대체에서 넣어줌.
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
//			stmt = conn.createStatement();
			pstmt = conn.prepareStatement(sql);
			
			// pstmt.setString(물음표순서, 대체할값) => '대체할값'(양옆에 홑따옴표로 감싸준 데이터가 들어감)
			// pstmt.setInt(물음표순서,대체할값) => 홑따옴표없이 데이터가 들어감
			// 순서대로 입력해야 됨
			pstmt.setString(1, m.getUserId());
			pstmt.setString(2, m.getUserPwd());
			pstmt.setString(3, m.getUserName());
			pstmt.setString(4, m.getGender());
			pstmt.setInt(5, m.getAge());
			pstmt.setString(6, m.getEmail());
			pstmt.setString(7, m.getPhone());
			pstmt.setString(8, m.getAddress());
			pstmt.setString(9, m.getHobby());//위에 넣어준 순서랑 같아야함.
			
			result = pstmt.executeUpdate();//실행은 되지만 오류 발생 안함
			
			if(result > 0) {  // 성공
				conn.commit();
			}else {           // 실패
				conn.rollback();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	// 전체 회원을 조회하는 메서드
	public ArrayList<Member> selectList() {
		ArrayList<Member> list = new ArrayList<>();
		
		Connection 		  conn = null;
		PreparedStatement pstmt = null;
		ResultSet 		  rset = null;
		
		String sql = "SELECT * FROM MEMBER";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC", "JDBC");
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				Member m = new Member();
				
				// rset에서 user_no 컬럼을 읽어서 m객체의 setter(setUserNo)를 이용하여 입력
				m.setUserNo(rset.getInt("user_no"));
				m.setUserId(rset.getString("user_id"));
				m.setUserPwd(rset.getString("user_pwd"));
				m.setUserName(rset.getString("user_name"));
				m.setGender(rset.getString("gender"));
				m.setEmail(rset.getString("email"));
				m.setAge(rset.getInt("age"));
				m.setPhone(rset.getString("phone"));
				m.setAddress(rset.getString("address"));
				m.setHobby(rset.getString("hobby"));
				m.setEnrollDate(rset.getDate("enroll_date"));
				
				list.add(m);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;	
	}
	
	// 회원 아이디로 검색 요청시 조회하는 메서드
	public Member selectByUserId(String userId) {
		Member m = null;
		
		Connection 		  conn = null;
		PreparedStatement pstmt = null;
		ResultSet		  rset = null;
		
		String sql = "SELECT * FROM MEMBER WHERE USER_ID = ?";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC", "JDBC");
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			rset = pstmt.executeQuery();
			
			if (rset.next()) {
				m = new Member();
				
				// rset에서 user_no 컬럼을 읽어서 m객체의 setter(setUserNo)를 이용하여 입력
				m.setUserNo(rset.getInt("user_no"));
				m.setUserId(rset.getString("user_id"));
				m.setUserPwd(rset.getString("user_pwd"));
				m.setUserName(rset.getString("user_name"));
				m.setGender(rset.getString("gender"));
				m.setEmail(rset.getString("email"));
				m.setAge(rset.getInt("age"));
				m.setPhone(rset.getString("phone"));
				m.setAddress(rset.getString("address"));
				m.setHobby(rset.getString("hobby"));
				m.setEnrollDate(rset.getDate("enroll_date"));
				
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return m;
	}
	
	public ArrayList<Member> selectByUserName(String keyword) {
		ArrayList<Member> list = new ArrayList<>();
		
		Connection 		  conn = null;
		PreparedStatement pstmt = null;
		ResultSet 		  rset = null;
		/*
		   WHERE절에 %를 포함해야 하는 경우 2가지 방법
		    방법1) String sql = "SELECT * FROM MEMBER WHERE USER_NAME LIKE '%' || ? || '%'";
		    방법2) String sql = "SELECT * FROM MEMBER WHERE USER_NAME LIKE ? OR USER_ID LIKE ?"; 
		*/
		String sql = "SELECT * FROM MEMBER WHERE USER_NAME LIKE ? OR USER_ID LIKE ?";
	
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			pstmt = conn.prepareStatement(sql);
			// 방법1일 경우: pstmt.setString(1, userName);
			// 방법2일 경우 
			pstmt.setString(1, "%" + keyword + "%");
			pstmt.setString(2, "%" + keyword + "%");
			
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				Member m = new Member();
				
				// rset에서 user_no컬럼을 읽어서 m객체의 setter(setUserNo)를 이용하여 입력
				m.setUserNo(rset.getInt("user_no"));  
				m.setUserId(rset.getString("user_id"));
				m.setUserPwd(rset.getString("user_pwd"));
				m.setUserName(rset.getString("user_name"));
				m.setGender(rset.getString("gender"));
				m.setEmail(rset.getString("email"));
				m.setAge(rset.getInt("age"));
				m.setPhone(rset.getString("phone"));
				m.setAddress(rset.getString("address"));
				m.setHobby(rset.getString("hobby"));
				m.setEnrollDate(rset.getDate("enroll_date"));
				
				list.add(m);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}				
		return list;
	}
	
	// Controller에서 요청하는 회원정보 변경을 수행하는 메서드 
	public int updateMember(Member m) {
		int result = 0;

		Connection 		   conn = null;
		PreparedStatement  pstmt = null;
		
		String sql = "UPDATE MEMBER SET USER_PWD = ?,"
				+       "    EMAIL = ?,"
				+       "    PHONE = ?,"
				+       "    ADDRESS = ?"
				+     "WHERE USER_ID = ?";
		
//		System.out.println(sql);
				
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, m.getUserPwd());
			pstmt.setString(2, m.getEmail());
			pstmt.setString(3, m.getPhone());
			pstmt.setString(4, m.getAddress());
			pstmt.setString(5, m.getUserId());
			
			result = pstmt.executeUpdate();
			
			if(result > 0) {  // 성공
				conn.commit();
			}else {           // 실패
				conn.rollback();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	// Controller에서 요청한 회원정보 삭제 작업을 수행할 메서드
	public int deleteMember(String userId)  {
		int result = 0;
		
		Connection 		   conn = null;
		PreparedStatement  pstmt = null;
		
		String sql = "DELETE FROM MEMBER WHERE USER_ID = ? ";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);//넘어온 값이 m값이 아닌 그냥 userid라 그대로 넣어줌
			result = pstmt.executeUpdate();
			
			if(result > 0) { //성공
				conn.commit();
			}else {
				conn.rollback();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
				try {
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return result;
	}
	
	// 회원 아이디와 비밀번호를 이용해서 해당 회원이 존재하는지 데이터베이스를 조회하는 메서드
	public Member loginMember(String userId,String userPwd) {
		Member m = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		String sql = "SELECT * FROM MEMBER WHERE USER_ID = ? AND USER_PWD = ?";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, userPwd);
			
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				m = new Member();
				// 데이터베이스 안에 컬럼명
				m.setUserNo(rset.getInt("user_no"));
				m.setUserId(rset.getString("user_id"));
				m.setUserPwd(rset.getString("user_pwd"));
				m.setUserName(rset.getString("user_name"));
				m.setGender(rset.getString("gender"));
				m.setAge(rset.getInt("age"));
				m.setEmail(rset.getString("email"));
				m.setPhone(rset.getString("phone"));
				m.setAddress(rset.getString("address"));
				m.setHobby(rset.getString("hobby"));
				m.setEnrollDate(rset.getDate("enroll_date"));
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return m;
	}
	
	
}
