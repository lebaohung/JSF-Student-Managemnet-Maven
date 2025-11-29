package com.ptit.repository.SClass;

import com.ptit.model.SClass;
import com.ptit.model.Student;
import com.ptit.repository.IPagingRepository;
import com.ptit.repository.JdbcConnection;
import com.ptit.repository.Student.StudentRepo;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.INTEGER;

@Named(value = "sClassRepo")
public class SClassRepo implements Serializable, ISClassRepo, IPagingRepository<SClass> {
    private static final long serialVersionUID = 1L;
    private static final String GET_ALL_CLASSES = "SELECT * FROM sclass ORDER BY id ASC ;";
    private static final String GET_ALL_CLASSES_BY_PAGE = "SELECT * FROM sclass ORDER BY id ASC LIMIT ? OFFSET ?;";
    private static final String INSERT_CLASS = "INSERT INTO public.sclass(name) VALUES (?);";
    private static final String GET_CLASS_BY_ID = "SELECT * FROM sclass WHERE id = ?;";
    private static final String UPDATE_CLASS = "UPDATE public.sclass SET name=? WHERE id = ?;";
    private static final String DELETE_CLASS_ID_WITH_STUDENT = "DELETE FROM student_and_sclass WHERE sclass_id = ?;";
    private static final String DELETE_CLASS = "DELETE FROM public.sclass WHERE id=?;";
    private static final String COUNT_CLASS_SIZE = "SELECT COUNT(*) FROM student_and_sclass WHERE sclass_id = ?;";
    private static final String COUNT_CLASSES = "SELECT COUNT(id) FROM sclass;";
    private static final String GET_STUDENTS_BY_CLASS_ID = "SELECT student_id FROM student_and_sclass WHERE sclass_id = ? ORDER BY student_id;";
    private static final String UPDATE_MENTOR_BY_CLASS_ID = "UPDATE sclass SET mentor_id = ? WHERE id = ?;";
    private static final String DELETE_STUDENT_IN_CLASS = "DELETE FROM student_and_sclass WHERE sclass_id = ? and student_id = ?";
    private static final String SAVE_STUDENT_INTO_CLASS = "INSERT INTO student_and_sclass (sclass_id, student_id) VALUES (?, ?);";

    @Inject
    private StudentRepo studentRepo;

    @Override
    public List<SClass> getAll() {
        List<SClass> sClasses = new ArrayList<>();
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CLASSES);
        ) {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                SClass sClass = new SClass();
                sClass.setId(resultSet.getInt("id"));
                sClass.setName(resultSet.getString("name"));
                sClasses.add(sClass);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sClasses;
    }

    public int countClassSize(Integer classId) {
        int classSize = 0;
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(COUNT_CLASS_SIZE);
        ) {
            preparedStatement.setInt(1, classId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                classSize = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Some Class empty");        
        }
        return classSize;
    }

    @Override
    public List<SClass> getAllByPage(int page, int pageSize) {
        int start = (page - 1) * pageSize;
        List<SClass> sClasses = new ArrayList<>();
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CLASSES_BY_PAGE);
        ) {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, start);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                SClass sClass = new SClass();
                sClass.setId(resultSet.getInt("id"));
                sClass.setName(resultSet.getString("name"));
                int mentorId = resultSet.getInt("mentor_id");
                if (resultSet.wasNull() || mentorId == 0) {
                    sClass.setMentor(new Student());
                } else {
                    sClass.setMentor(studentRepo.getById(mentorId));
                }
                sClasses.add(sClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sClasses;
    }

    public int count() {
        int classNumber = 0;
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(COUNT_CLASSES);
        ) {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                classNumber = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return classNumber;
    }

    @Override
    public void save(SClass sClass) {
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CLASS);
        ) {
            preparedStatement.setString(1, sClass.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            clear(sClass);
        }
    }

    @Override
    public SClass getById(Integer classId) {
        if (classId == null) return null;
        SClass sClass = new SClass();
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_CLASS_BY_ID);
        ) {
            preparedStatement.setInt(1, classId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet != null && resultSet.next()) {
                sClass.setId(resultSet.getInt("id"));
                sClass.setName(resultSet.getString("name"));
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
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CLASS);
        ) {
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
            connection.setAutoCommit(false);
            try (
                    PreparedStatement deleteClassIdWithStudent = connection.prepareStatement(DELETE_CLASS_ID_WITH_STUDENT);
                    PreparedStatement deleteSClass = connection.prepareStatement(DELETE_CLASS);
            ) {
                deleteClassIdWithStudent.setInt(1, classId);
                deleteSClass.setInt(1, classId);
                deleteClassIdWithStudent.executeUpdate();
                deleteSClass.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getStudentsIdListByClassId(Integer sClassId) {
        List<Integer> studentsIdList = new ArrayList<>();
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENTS_BY_CLASS_ID);
        ) {
            preparedStatement.setInt(1, sClassId);
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

    public void saveStudentIntoClass(Integer sclassId, Integer studentId) {
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SAVE_STUDENT_INTO_CLASS);
        ) {
            preparedStatement.setInt(1, sclassId);
            if (studentId == null) {
                preparedStatement.setNull(2, INTEGER);
            } else {
                preparedStatement.setInt(2, studentId);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void clear(SClass sClass) {
        sClass.setName(null);
        sClass.setId(0);
    }

    public void updateMentorByClassId(Integer sClassId, Integer studentId) {
        try (
                Connection connection = JdbcConnection.getConnection();
                PreparedStatement updateMentor = connection.prepareStatement(UPDATE_MENTOR_BY_CLASS_ID);
        ) {
            if (studentId == null) {
                updateMentor.setNull(1, INTEGER);
            } else {
                updateMentor.setInt(1, studentId);
            }
            updateMentor.setInt(2, sClassId);
            updateMentor.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteStudentInClass(Integer sClassId, Integer studentId) {
        try (
                Connection connection = JdbcConnection.getConnection();
        ) {
            connection.setAutoCommit(false);
            try (
                    PreparedStatement checkMentor = connection.prepareStatement("SELECT mentor_id FROM sclass WHERE id = ?");
                    PreparedStatement clearMentor = connection.prepareStatement(UPDATE_MENTOR_BY_CLASS_ID);
                    PreparedStatement deleteStudentInClass = connection.prepareStatement(DELETE_STUDENT_IN_CLASS);
            ) {
                checkMentor.setInt(1, sClassId);
                ResultSet rs = checkMentor.executeQuery();
                if (rs.next()) {
                    int mentorId = rs.getInt("mentor_id");
                    if (!rs.wasNull() && mentorId == studentId) {
                        clearMentor.setNull(1, INTEGER);
                        clearMentor.setInt(2, sClassId);
                        clearMentor.executeUpdate();
                    }
                }
                
                deleteStudentInClass.setInt(1, sClassId);
                deleteStudentInClass.setInt(2, studentId);
                deleteStudentInClass.executeUpdate();
                
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                throw e;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
