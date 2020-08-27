package com.synergix.repository.Student;

import com.synergix.model.Student;
import com.synergix.repository.JdbcConnection;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

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
        for (Student student: students) {
            System.out.println(student.getId() + ",");
        }
        return students;
    }

    @Override
    public void save(Student student) {
        if (validateStudent(student)) {
            int result = 0;
            try (
                    Connection connection = JdbcConnection.getConnection();
            ) {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT);
                preparedStatement.setString(1, student.getsName());
                preparedStatement.setString(2, student.getEmail());
                preparedStatement.setString(3, student.getPhone());

                result = preparedStatement.executeUpdate();
                if (result == 1) this.setMessage("Add new Student successfully");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                clear(student);
            }
        }
    }

    public void clear(Student student) {
        student.setsName(null);
        student.setEmail(null);
        student.setId(0);
        student.setPhone(null);
    }

    @Override
    public String edit(Integer id) {
        Student editStudent = null;
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT_BY_ID);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet != null) {
                resultSet.next();
                editStudent = new Student();
                editStudent.setId(resultSet.getInt(1));
                editStudent.setsName(resultSet.getString(2));
                editStudent.setEmail(resultSet.getString(3));
                editStudent.setPhone(resultSet.getString(4));
            }
            sessionMap.put("editStudent", editStudent);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "/editStudent.xhtml?faces-redirect=true";
    }

    @Override
    public void update(Student student) {
        if (validateStudent(student)) {
            int result = 0;
            try (
                    Connection connection = JdbcConnection.getConnection();
            ) {
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT);
                preparedStatement.setString(1, student.getsName());
                preparedStatement.setString(2, student.getEmail());
                preparedStatement.setString(3, student.getPhone());
                preparedStatement.setInt(4, student.getId());
                result = preparedStatement.executeUpdate();
                if (result == 1) this.setMessage("Edit Student ID " + student.getId() + " successfully");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
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

    private boolean validateStudent(Student student) {
        if (student.getsName() == null || student.getsName().equals("")) {
            this.setMessage("Name Required! ");
            return false;
        }
        return true;
    }
}
