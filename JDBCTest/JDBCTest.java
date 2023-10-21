import java.io.*;
import java.sql.*;

public class JDBCTest {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static int inputId;

  public static void main(String[] args) throws IOException {

    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
    } catch (ClassNotFoundException e) {
      System.err.println("ClassNotFoundException : " + e.getMessage());
    }

    try {

      Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/XE", "scott", "tiger");

      while (true) {
        System.out.println();
        System.out.println("Menu(1: 학생 추가, 2: 학생 삭제, 3: 학생 수정, 4: 학생 검색, 5: 학과 출력, 6: 종료)");
        System.out.println("Menu에 있는 숫자 중 하나를 입력하세요.");
        int input = Integer.parseInt(br.readLine());
        switch (input) {
          case 1:
            addStudent(conn);
            break;
          case 2:
            deleteStudent(conn);
            break;
          case 3:
            updateStudent(conn);
            break;
          case 4:
            System.out.println("검색할 학생의 학번을 입력하세요.");
            searchStudent(conn);
            break;
          case 5:
            printStudent(conn);
            break;
          case 6:
            System.out.println("프로그램을 종료합니다.");
            return;
          default:
            System.out.println("올바른 숫자를 입력해주세요!");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    br.close();
  }

  private static void addStudent(Connection conn) throws IOException, SQLException {
    System.out.println("추가할 학생의 정보를 입력하세요.");

    System.out.println("학번을 입력하세요.");
    String inputId = br.readLine();
    Integer id = inputId.isEmpty() ? null : Integer.parseInt(inputId);

    System.out.println("이름을 입력하세요.");
    String inputName = br.readLine();
    String name = inputName.isEmpty() ? null : inputName;

    System.out.println("학과 번호를 입력하세요.");
    String inputDeptno = br.readLine();
    Integer deptno = inputDeptno.isEmpty() ? null : Integer.parseInt(inputDeptno);


    System.out.println("지도 교수 번호를 입력하세요.");
    String inputAdvisor = br.readLine();
    Integer advisor = inputAdvisor.isEmpty() ? null : Integer.parseInt(inputAdvisor);

    System.out.println("성별을 입력하세요.");
    String inputGen = br.readLine();
    String gen = inputGen.isEmpty() ? null : inputGen;

    System.out.println("주소를 입력하세요.");
    String inputAddr = br.readLine();
    String addr = inputAddr.isEmpty() ? null : inputAddr;

    System.out.println("생년월일을 입력하세요 (YYYY-MM-DD 형식).");
    String inputBirthdate = br.readLine();
    String birthdate = inputBirthdate.isEmpty() ? null : inputBirthdate;

    System.out.println("학점을 입력하세요.");
    String inputGrade = br.readLine();
    Double grade = inputGrade.isEmpty() ? null : Double.parseDouble(inputGrade);

    String insertDataSQL = "INSERT INTO STUDENT (sid, sname, deptno, advisor, gen, addr, birthdate, grade) VALUES (?, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";

    try (PreparedStatement preparedStatement = conn.prepareStatement(insertDataSQL)) {

      preparedStatement.setInt(1, id);

      preparedStatement.setObject(2, name != null ? name : null);

      if (deptno == null) {
        preparedStatement.setNull(3, Types.NUMERIC);
      } else {
        preparedStatement.setInt(3, deptno);
      }
      if (advisor == null) {
        preparedStatement.setNull(4, Types.NUMERIC);
      } else {
        preparedStatement.setInt(4, advisor);
      }
      preparedStatement.setObject(5, gen != null ? gen : null);
      preparedStatement.setObject(6, addr != null ? addr : null);
      preparedStatement.setObject(7, birthdate != null ? birthdate : null);

      if (grade == null) {
        preparedStatement.setNull(8, Types.NUMERIC);
      } else {
        preparedStatement.setDouble(8, grade);
      }

      preparedStatement.executeUpdate();
      System.out.println("학생 정보가 추가되었습니다.");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  private static void deleteStudent(Connection conn) throws IOException {
    System.out.println("삭제할 학생의 학번을 입력하세요.");
    inputId = Integer.parseInt(br.readLine());
    String deleteDataSQL = "DELETE FROM STUDENT WHERE sid = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(deleteDataSQL)) {
      preparedStatement.setInt(1, inputId);
      preparedStatement.execute();
      System.out.println("학생 정보가 삭제되었습니다.");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static void updateStudent(Connection conn) throws IOException {
    System.out.println("수정할 학생의 학번을 입력하세요");

    int inputId = searchStudent(conn);

    if (inputId == -1) {
      System.out.println("해당 정보가 없습니다!");
      return;
    }

    System.out.println();
    System.out.println("이름을 입력하세요.");
    String inputName = br.readLine();

    System.out.println("학과 번호를 입력하세요.");
    int inputDeptno = Integer.parseInt(br.readLine());

    System.out.println("지도 교수 번호를 입력하세요.");
    int inputAdvisor = Integer.parseInt(br.readLine());

    System.out.println("성별을 입력하세요.");
    String inputGen = br.readLine();

    System.out.println("주소를 입력하세요.");
    String inputAddr = br.readLine();

    System.out.println("생년월일을 입력하세요.");
    String inputBirthdate = br.readLine();

    System.out.println("학점을 입력하세요.");
    double inputGrade = Double.parseDouble(br.readLine());

    String updateDataSQL = "UPDATE STUDENT SET sname=?, deptno=?, advisor=?, gen=?, addr=?, birthdate=TO_DATE(?, 'YYYY-MM-DD'), grade=? WHERE sid = ?";

    try (PreparedStatement preparedStatement = conn.prepareStatement(updateDataSQL)) {
      preparedStatement.setString(1, inputName);
      preparedStatement.setInt(2, inputDeptno);
      preparedStatement.setInt(3, inputAdvisor);
      preparedStatement.setString(4, inputGen);
      preparedStatement.setString(5, inputAddr);
      preparedStatement.setString(6, inputBirthdate);
      preparedStatement.setDouble(7, inputGrade);
      preparedStatement.setInt(8, inputId);
      preparedStatement.executeUpdate();
      System.out.println("학생 정보가 수정되었습니다.");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  private static int searchStudent(Connection conn) throws IOException {
    inputId = Integer.parseInt(br.readLine());
    String selectDataSQL = "SELECT sid, sname, deptno, advisor, gen, addr, to_char(birthdate,'YYYY-MM-DD') , grade FROM STUDENT WHERE sid = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(selectDataSQL)) {
      preparedStatement.setInt(1, inputId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (!resultSet.next()) {
          return -1; // 결과가 없으면 함수 종료
        }
        do {
          int sid = resultSet.getInt("sid");
          String sname = resultSet.getString("sname");
          int deptno = resultSet.getInt("deptno");
          int advisor = resultSet.getInt("advisor");
          String gen = resultSet.getString("gen");
          String addr = resultSet.getString("addr");
          String birthdate = resultSet.getString("to_char(birthdate,'YYYY-MM-DD')");
          double grade = resultSet.getDouble("grade");
          System.out.println("학번: " + sid + "\n" + "이름: " + sname + "\n" + "학과: " + deptno + "\n" + "지도교수: " + advisor + "\n" + "성별: " + gen + "\n" + "주소: " + addr + "\n" + "생년월일: " + birthdate + "\n" + "학점: " + grade);
        } while (resultSet.next());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return inputId;
  }


  private static void printStudent(Connection conn) throws IOException {
    System.out.println("검색할 학과를 입력하세요");
    int inputdeptno = Integer.parseInt(br.readLine());
    String selectDataSQL = "SELECT s.sid, sname, dname, p.pname FROM STUDENT s, Department d, Professor p WHERE s.deptno = ? AND s.deptno = d.deptno AND s.advisor = p.pid";
    try (PreparedStatement preparedStatement = conn.prepareStatement(selectDataSQL)) {
      preparedStatement.setInt(1, inputdeptno);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (!resultSet.next()) {
          System.out.println("해당 정보가 없습니다!");
          return;
        }
        do {
          int sid = resultSet.getInt("sid");
          String sname = resultSet.getString("sname");
          String dname = resultSet.getString("dname");
          String pname = resultSet.getString("pname");

          System.out.println("학번: " + sid + " " + "학생 이름: " + sname + " " + "학과 이름: " + dname + " " + "지도교수 이름: " + pname);
          System.out.println();
        } while (resultSet.next());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}