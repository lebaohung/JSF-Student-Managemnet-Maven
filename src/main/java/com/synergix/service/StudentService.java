package com.synergix.service;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class StudentService implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SELECT_ALL_STUDENTS = "select * from student;";
    private static final String INSERT_STUDENT = "INSERT INTO public.student(\n" +
            "\tsname, email, phone)\n" +
            "\tVALUES (?, ?, ?);";
    private static final String UPDATE_STUDENT = "UPDATE public.student\n" +
            "\tSET id=?, sname=?, email=?, phone=?";
    private static final String DELETE_STUDENT = "DELETE FROM public.student\n" +
            "\tWHERE id=?;";

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public List<StudentBean> getStudentsList() {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<StudentBean> students = new ArrayList<>();


        try (
                Connection connection = JdbcConnection.getConnection();
                ) {
            preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();

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

    public void saveStudent(StudentBean studentBean)  {
        if (studentBean.getsName() == null || studentBean.getsName().equals("")) {
            this.setMessage("Name Required! ");
            return;
        }

        PreparedStatement preparedStatement = null;
        int result = 0;

        try (
                Connection connection = JdbcConnection.getConnection();
                ) {
            preparedStatement = connection.prepareStatement(INSERT_STUDENT);
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

    private void clear(StudentBean studentBean) {
        studentBean.setsName(null);
        studentBean.setEmail(null);
        studentBean.setId(0);
        studentBean.setPhone(null);
    }
}
