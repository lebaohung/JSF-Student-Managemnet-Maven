package com.synergix.repository.Student;

import com.synergix.model.Student;
import com.synergix.repository.JdbcConnection;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class StudentRepo implements Serializable, IStudentRepo {
    private static final long serialVersionUID = 1L;
    private static final String SELECT_ALL_STUDENTS = "SELECT * FROM student ORDER BY id ASC ;";
    private static final String INSERT_STUDENT = "INSERT INTO public.student(\n" +
            "\tsname, email, phone)\n" +
            "\tVALUES (?, ?, ?);";
    private static final String SELECT_STUDENT_BY_ID = "SELECT * FROM student WHERE id = ?;";
    private static final String UPDATE_STUDENT = "UPDATE public.student\n" +
            "\tSET sname=?, email=?, phone=? WHERE id = ?";
    private static final String DELETE_STUDENT = "DELETE FROM public.student\n" +
            "\tWHERE id=?;";

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
                students.add(student);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return students;
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

                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
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
    }

    @Override
    public Student getById(Integer studentId) {
        Student student = new Student();
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT_BY_ID);
            preparedStatement.setInt(1, studentId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet != null) {
                resultSet.next();
                student.setId(resultSet.getInt(1));
                student.setsName(resultSet.getString(2));
                student.setEmail(resultSet.getString(3));
                student.setPhone(resultSet.getString(4));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
                preparedStatement.setInt(4, student.getId());
                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
    }

    @Override
    public void delete(Integer id) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}