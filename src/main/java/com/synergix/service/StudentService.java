package com.synergix.service;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@SessionScoped
public class StudentService implements Serializable, IStudentService {
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
    public List<StudentBean> getAll() {
        List<StudentBean> students = new ArrayList<>();

        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                StudentBean studentBean = new StudentBean();
                studentBean.setId(resultSet.getInt(1));
                studentBean.setsName(resultSet.getString(2));
                studentBean.setEmail(resultSet.getString(3));
                studentBean.setPhone(resultSet.getString(4));
                students.add(studentBean);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return students;
    }

    @Override
    public void save(StudentBean studentBean) {
        if (validateStudent(studentBean)) {
            int result = 0;
            try (
                    Connection connection = JdbcConnection.getConnection();
            ) {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT);
                preparedStatement.setString(1, studentBean.getsName());
                preparedStatement.setString(2, studentBean.getEmail());
                preparedStatement.setString(3, studentBean.getPhone());

                result = preparedStatement.executeUpdate();
                if (result == 1) this.setMessage("Add new Student successfully");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                clear(studentBean);
            }
        }
    }

    public void clear(StudentBean studentBean) {
        studentBean.setsName(null);
        studentBean.setEmail(null);
        studentBean.setId(0);
        studentBean.setPhone(null);
    }

    @Override
    public String edit(Integer id) {
        StudentBean editStudent = null;
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
                editStudent = new StudentBean();
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
    public void update(StudentBean studentBean) {
        if (validateStudent(studentBean)) {
            int result = 0;
            try (
                    Connection connection = JdbcConnection.getConnection();
            ) {
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT);
                preparedStatement.setString(1, studentBean.getsName());
                preparedStatement.setString(2, studentBean.getEmail());
                preparedStatement.setString(3, studentBean.getPhone());
                preparedStatement.setInt(4, studentBean.getId());
                result = preparedStatement.executeUpdate();
                if (result == 1) this.setMessage("Edit Student ID " + studentBean.getId() + " successfully");
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

    private boolean validateStudent(StudentBean studentBean) {
        if (studentBean.getsName() == null || studentBean.getsName().equals("")) {
            this.setMessage("Name Required! ");
            return false;
        }
        return true;
    }
}
