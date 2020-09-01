package com.synergix.repository.SClass;

import com.synergix.model.SClass;
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

@Named(value = "sClassRepo")
@SessionScoped
public class SClassRepo implements Serializable, ISClassRepo {
    private static final long serialVersionUID = 1L;
    private static final String SELECT_ALL_CLASSES = "SELECT * FROM sclass ORDER BY id ASC ;";
    private static final String INSERT_CLASS = "INSERT INTO public.sclass(name) VALUES (?);";
    private static final String SELECT_CLASS_BY_ID = "SELECT * FROM sclass WHERE id = ?;";
    private static final String UPDATE_CLASS = "UPDATE public.sclass SET name=? WHERE id = ?;";
    private static final String DELETE_CLASS = "DELETE FROM public.sclass WHERE id=?;";
    private static final String COUNT_SIZE_CLASS = "SELECT COUNT(id) FROM student GROUP BY sclass_id HAVING sclass_id = ?;";

    @Override
    public List<SClass> getAll() {
        List<SClass> sClasses = new ArrayList<>();
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_CLASSES);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                SClass sClass = new SClass();
                sClass.setId(resultSet.getInt(2));
                sClass.setName(resultSet.getString(1));
                sClasses.add(sClass);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sClasses;
    }

    @Override
    public void save(SClass sClass) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CLASS);
            preparedStatement.setString(1, sClass.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            clear(sClass);
        }
    }

    public void clear(SClass sClass) {
        sClass.setName(null);
        sClass.setId(0);
    }

    @Override
    public SClass getById(Integer classId) {
        SClass sClass = new SClass();
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CLASS_BY_ID);
            preparedStatement.setInt(1, classId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet != null) {
                resultSet.next();
                sClass.setName(resultSet.getString(1));
                sClass.setId(resultSet.getInt(2));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return sClass;
    }

    @Override
    public void update(SClass sClass) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CLASS);
            preparedStatement.setString(1, sClass.getName());
            preparedStatement.setInt(2, sClass.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void delete(Integer classId) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CLASS);
            preparedStatement.setInt(1, classId);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int countClassSize(Integer classId) {
        int classSize = 0;
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(COUNT_SIZE_CLASS);
            preparedStatement.setInt(1, classId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                classSize = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            System.out.println("Some Class empty");
        }
        return classSize;
    }
}
