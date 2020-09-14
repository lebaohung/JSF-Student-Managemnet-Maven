package com.synergix.repository.SClass;

import com.synergix.model.SClass;
import com.synergix.repository.IPagingRepository;
import com.synergix.repository.JdbcConnection;

import javax.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named(value = "sClassRepo")
public class SClassRepo implements Serializable, ISClassRepo, IPagingRepository<SClass> {
    private static final long serialVersionUID = 1L;
    private static final String SELECT_ALL_CLASSES = "SELECT * FROM sclass ORDER BY id ASC ;";
    private static final String SELECT_ALL_CLASSES_BY_PAGE = "SELECT * FROM sclass ORDER BY id ASC LIMIT ? OFFSET ?;";
    private static final String INSERT_CLASS = "INSERT INTO public.sclass(name) VALUES (?);";
    private static final String SELECT_CLASS_BY_ID = "SELECT * FROM sclass WHERE id = ?;";
    private static final String UPDATE_CLASS = "UPDATE public.sclass SET name=? WHERE id = ?;";
    private static final String DELETE_CLASS = "DELETE FROM public.sclass WHERE id=?;";
    private static final String COUNT_CLASS_SIZE = "SELECT COUNT(id) FROM student_and_sclass GROUP BY sclass_id HAVING sclass_id = ?;";
    private static final String COUNT_ClASSES = "SELECT COUNT(id) FROM sclass;";
    private static final String SELECT_STUDENTS_BY_CLASS_ID = "SELECT * FROM student_and_sclass WHERE sclass_id = ? ORDER BY id;";
    private static final String SELECT_MENTOR_BY_CLASS_ID = "SELECT student_id FROM student_and_sclass WHERE MENTOR = TRUE AND SCLASS_ID = ?;";
    private static final String UPDATE_MENTOR_BY_CLASS_ID = "UPDATE student_and_sclass SET mentor = false WHERE sclass_id = ? AND mentor = true;";
    private static final String SET_MENTOR_BY_CLASS_ID = "UPDATE student_and_sclass SET mentor = TRUE WHERE sclass_id = ? and student_id = ?;";

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
    public List<SClass> getAllByPage(int page, int pageSize) {
        int start = (page - 1) * pageSize;
        List<SClass> sClasses = new ArrayList<>();
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_CLASSES_BY_PAGE);
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, start);
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

    public int count() {
        int classNumber = 0;
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(COUNT_ClASSES);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                classNumber = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            System.out.println("Class list empty");
        }
        return classNumber;
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
        if (classId == null) return null;
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
            System.out.println("Exception at sClassRepo getByID()");
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
    public void delete(Integer classId) throws SQLException {
        Connection connection = JdbcConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CLASS);
        preparedStatement.setInt(1, classId);
        preparedStatement.executeUpdate();
    }

    public int countClassSize(Integer classId) {
        int classSize = 0;
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(COUNT_CLASS_SIZE);
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

    public Map<Integer, Boolean> getStudentsByClassId(Integer sClassId) {
        Map<Integer, Boolean> studentIdMap = new HashMap<>();
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENTS_BY_CLASS_ID);
            preparedStatement.setInt(1, sClassId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                studentIdMap.put(resultSet.getInt(1), resultSet.getBoolean(4));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return studentIdMap;
    }

    public Integer getSClassMentorId(Integer sClassId) {
        Integer studentId = null;
        Connection connection = JdbcConnection.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_MENTOR_BY_CLASS_ID);
            preparedStatement.setInt(1, sClassId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                studentId = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return studentId;
    }

    public void setSClassMentor(Integer sClassId, Integer studentId) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            PreparedStatement updateMentor = connection.prepareStatement(UPDATE_MENTOR_BY_CLASS_ID);
            updateMentor.setInt(1, sClassId);
            updateMentor.execute();

            PreparedStatement setMentor = connection.prepareStatement(SET_MENTOR_BY_CLASS_ID);
            setMentor.setInt(1, sClassId);
            setMentor.setInt(2, studentId);
            setMentor.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
