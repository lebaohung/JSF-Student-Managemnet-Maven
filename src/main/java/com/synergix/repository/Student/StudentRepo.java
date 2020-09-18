package com.synergix.repository.Student;

import com.synergix.model.Student;
import com.synergix.repository.IPagingRepository;
import com.synergix.repository.IRepository;
import com.synergix.repository.JdbcConnection;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Named
public class StudentRepo implements Serializable, IStudentRepo, IPagingRepository<Student> {
    private static final long serialVersionUID = 1L;
    private static final String SELECT_ALL_STUDENTS = "SELECT * FROM student ORDER BY id ASC ;";
    private static final String SELECT_ALL_STUDENTS_BY_PAGE = "SELECT * FROM student ORDER BY id ASC LIMIT ? OFFSET ?;";
    private static final String INSERT_STUDENT = "INSERT INTO public.student(\n" +
            "\tsname, email, phone, birthday)\n" +
            "\tVALUES (?, ?, ?, ?);";
    private static final String SELECT_STUDENT_BY_ID = "SELECT * FROM student WHERE id = ?;";
    private static final String UPDATE_STUDENT = "UPDATE public.student\n" +
            "\tSET sname=?, email=?, phone=?, birthday=? WHERE id = ?";
    private static final String DELETE_STUDENT_ID_IN_CLASS = "DELETE FROM student_and_sclass WHERE student_id = ?;";
    private static final String DELETE_STUDENT = "DELETE FROM public.student WHERE id=?;";
    private static final String COUNT_STUDENTS = "SELECT COUNT(id) FROM student;";
    private static final String GET_ALL_STUDENTS_ID = "SELECT id FROM student";

    @Override
    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt(1));
                student.setsName(resultSet.getString(2));
                student.setEmail(resultSet.getString(3));
                student.setPhone(resultSet.getString(4));
//                student.setsClassId(resultSet.getInt(5));
                students.add(student);
            }
        } catch (Exception e) {
            FacesMessage getAllError = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot get student list ", null);
            FacesContext.getCurrentInstance().addMessage("studentsList", getAllError);
            System.out.println(e.getMessage());
        }
        return students;
    }

    @Override
    public List<Student> getAllByPage(int page, int pageSize) {
        int start = (page - 1) * pageSize;
        List<Student> students = new ArrayList<>();
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS_BY_PAGE);
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, start);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt(1));
                student.setsName(resultSet.getString(2));
                student.setEmail(resultSet.getString(3));
                student.setPhone(resultSet.getString(4));
                student.setBirthday(resultSet.getDate(5));
                students.add(student);
            }
        } catch (Exception e) {
            FacesMessage getAllError = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot get student list ", null);
            FacesContext.getCurrentInstance().addMessage("studentsList", getAllError);
            System.out.println(e.getMessage());
        }
        return students;
    }

    @Override
    public int count() {
        int studentNumber = 0;
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(COUNT_STUDENTS);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                studentNumber = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            FacesMessage countPageError = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Student list empty ", null);
            FacesContext.getCurrentInstance().addMessage("studentsList", countPageError);
            System.out.println(throwables.getMessage());
        }
        return studentNumber;
    }

    @Override
    public void save(Student student) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT);
            preparedStatement.setString(1, student.getsName());
            preparedStatement.setString(2, student.getEmail());
            preparedStatement.setString(3, student.getPhone());
            if (student.getBirthday() == null) {
                preparedStatement.setDate(4, null);
            } else {
                preparedStatement.setDate(4, new Date(student.getBirthday().getTime()));
            }
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            FacesMessage saveStudentError = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot save student ID " + student.getId(), null);
            FacesContext.getCurrentInstance().addMessage("studentsList", saveStudentError);
            throwables.printStackTrace();
        } finally {
            clear(student);
        }
    }

    public void clear(Student student) {
        student.setsName(null);
        student.setEmail(null);
        student.setId(0);
        student.setPhone(null);
//        student.setsClassId(0);
    }

    @Override
    public Student getById(Integer studentId) {
        Student student = new Student();
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT_BY_ID);
        ) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet != null) {
                resultSet.next();
                student.setId(resultSet.getInt(1));
                student.setsName(resultSet.getString(2));
                student.setEmail(resultSet.getString(3));
                student.setPhone(resultSet.getString(4));
                student.setBirthday(resultSet.getDate(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    @Override
    public void update(Student student) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT);
            preparedStatement.setString(1, student.getsName());
            preparedStatement.setString(2, student.getEmail());
            preparedStatement.setString(3, student.getPhone());
            preparedStatement.setInt(5, student.getId());
            if (student.getBirthday() == null) {
                preparedStatement.setDate(4, null);
            } else {
                preparedStatement.setDate(4, new Date(student.getBirthday().getTime()));
            }
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            FacesMessage updateStudentError = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot update student ID " + student.getId(), null);
            FacesContext.getCurrentInstance().addMessage("studentsList", updateStudentError);
            throwables.printStackTrace();
        }
    }

    @Override
    public void delete(Integer studentId) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            connection.setAutoCommit(false);
            try (
                    PreparedStatement deleteStudentIdInClass = connection.prepareStatement(DELETE_STUDENT_ID_IN_CLASS);
                    PreparedStatement deleteStudent = connection.prepareStatement(DELETE_STUDENT);
            ) {
                deleteStudentIdInClass.setInt(1, studentId);
                deleteStudent.setInt(1, studentId);
                deleteStudentIdInClass.executeUpdate();
                deleteStudent.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                FacesMessage deleteErrorMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete student ID " + studentId, null);
                FacesContext.getCurrentInstance().addMessage("studentsList", deleteErrorMsg);
                try {
                    connection.rollback();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException e) {
            FacesMessage deleteErrorMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete student ID " + studentId, null);
            FacesContext.getCurrentInstance().addMessage("studentsList", deleteErrorMsg);
        }
    }

    public List<Integer> getAllStudentsId() {
        List<Integer> studentsIdList = new ArrayList<>();
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_STUDENTS_ID);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                studentsIdList.add(resultSet.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return studentsIdList;
    }
}
