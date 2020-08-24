package com.synergix.service;

import com.synergix.model.Student;

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

    public List<Student> getStudentsList() {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Connection connection = JdbcConnection.getConnection();
        String stm = "select * from student";
        List<Student> students = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(stm);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt(1));
                student.setSname(resultSet.getString(2));
                student.setEmail(resultSet.getString(3));
                student.setPhone(resultSet.getString(4));
                students.add(student);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return students;
    }
}
