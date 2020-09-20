package com.synergix.controller.sclass;

import com.synergix.model.SClass;
import com.synergix.model.Student;
import com.synergix.repository.SClass.SClassRepo;
import com.synergix.repository.Student.StudentRepo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Named(value = "sClassBean")
@ConversationScoped
public class SClassBean implements Serializable {

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 5;
    private static final String MANAGER_PAGE = "showManagerPage";
    private static final String DETAIL_PAGE = "showDetailPage";
    private static final int MINIMUM_LENGTH_NAME = 2;

    private int page = INIT_PAGE;
    private int pageCount;
    private String navigateSClassPage;
    private Student middleStudent;
    private SClass middleSClass;
    private Student middleMentor;
    private Map<Integer, Boolean> selectedSClassMap = new HashMap<>();
    private Map<Integer, Boolean> selectedStudentMap = new HashMap<>();
    private List<Integer> studentsIdListOfClass = new ArrayList<>();
    private List<Student> studentListOfClass = new ArrayList<>();
    private List<SClass> classes = new ArrayList<>();

    @Inject
    private Conversation conversation;

    @Inject
    private SClassRepo sClassRepo;

    @Inject
    private StudentRepo studentRepo;

    @PostConstruct
    public void initNavigator() {
        this.navigateSClassPage = MANAGER_PAGE;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageCount() {
        this.pageCount = (int) Math.ceil(this.count() / (double) PAGE_SIZE);
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getManagerPage() {
        return MANAGER_PAGE;
    }

    public String getDetailPage() {
        return DETAIL_PAGE;
    }

    public Student getMiddleStudent() {
        return middleStudent;
    }

    public void setMiddleStudent(Student middleStudent) {
        this.middleStudent = middleStudent;
    }

    public SClass getMiddleSClass() {
        return middleSClass;
    }

    public void setMiddleSClass(SClass middleSClass) {
        this.middleSClass = middleSClass;
    }

    public Student getMiddleMentor() {
        return middleMentor;
    }

    public void setMiddleMentor(Student middleMentor) {
        this.middleMentor = middleMentor;
    }

    public String getNavigateSClassPage() {
        return navigateSClassPage;
    }

    public void setNavigateSClassPage(String navigateSClassPage) {
        this.navigateSClassPage = navigateSClassPage;
    }

    public List<SClass> getClasses() {
        return classes;
    }

    public void setClasses(List<SClass> classes) {
        this.classes = classes;
    }

    public Map<Integer, Boolean> getSelectedSClassMap() {
        return selectedSClassMap;
    }

    public void setSelectedSClassMap(Map<Integer, Boolean> selectedSClassMap) {
        this.selectedSClassMap = selectedSClassMap;
    }

    public Map<Integer, Boolean> getSelectedStudentMap() {
        return selectedStudentMap;
    }

    public void setSelectedStudentMap(Map<Integer, Boolean> selectedStudentMap) {
        this.selectedStudentMap = selectedStudentMap;
    }

    public List<Integer> getSelectedSClassList() {
        return this.getSelectedSClassMap().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Integer> getSelectedStudentIdList() {
        return this.getSelectedStudentMap().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Integer> getStudentsIdListOfClass() {
        return studentsIdListOfClass;
    }

    public void setStudentsIdListOfClass(List<Integer> studentsIdListOfClass) {
        this.studentsIdListOfClass = studentsIdListOfClass;
    }

    public List<Student> getStudentListOfClass() {
        return studentListOfClass;
    }

    public void setStudentListOfClass(List<Student> studentListOfClass) {
        this.studentListOfClass = studentListOfClass;
    }

    public List<SClass> getAll() {
        return sClassRepo.getAll();
    }

    public void initConversation() {
        if (conversation.isTransient()) conversation.begin();
    }

    public void endConversation() {
        if (!conversation.isTransient()) conversation.end();
    }

    public int count() {
        return sClassRepo.count();
    }

    public void next() {
        if (this.getPage() >= this.getPageCount()) return;
        else this.page++;
        this.cancelAdd();
        this.getAllByPage();
        this.selectedSClassMap.clear();
    }

    public void previous() {
        if (this.getPage() <= 1) return;
        else this.page--;
        this.cancelAdd();
        this.getAllByPage();
        this.selectedSClassMap.clear();
    }

    public void getAllByPage() {
        this.endConversation();
        this.initConversation();
        this.classes = sClassRepo.getAllByPage(page, PAGE_SIZE);
    }

    public void create() {
        middleSClass = new SClass();
        this.getAllByPage();
        classes.add(middleSClass);
    }


    public void save(SClass sClass) {
        if (sClass != null) {
            sClassRepo.save(sClass);
        }
        this.cancelAdd();
    }

    public void update(SClass sClass) {
        if (sClass != null) {
            sClassRepo.update(sClass);
        }
    }

    public void cancelAdd() {
        middleSClass = null;
        this.getAllByPage();
    }

    public int countClassSize(Integer classId) {
        return sClassRepo.countClassSize(classId);
    }

    public void deleteSelectedSClass() {
        for (Integer sClassId : this.getSelectedSClassList()) {
            sClassRepo.delete(sClassId);
        }
        this.getAllByPage();
        this.selectedSClassMap.clear();
    }

    public void selectAll() {
        for (SClass sClass : this.getClasses()) {
            selectedSClassMap.put(sClass.getId(), true);
        }
    }

    public void unselectAll() {
        for (SClass sClass : this.getClasses()) {
            selectedSClassMap.put(sClass.getId(), false);
        }
    }

    public void moveToDetailPage(SClass sClass) {
        this.middleSClass = sClass;
        this.middleMentor = sClass.getMentor();
        this.clearStudentListOfClass();
        this.studentsIdListOfClass = sClassRepo.getStudentsIdListByClassId(sClass.getId());
        for (Integer studentId : this.studentsIdListOfClass) {
            studentListOfClass.add(studentRepo.getById(studentId));
        }
        this.selectedStudentMap.clear();
        this.navigateSClassPage = DETAIL_PAGE;
    }

    public void clearStudentListOfClass() {
        this.studentListOfClass.clear();
    }

    public void createStudent() {
        middleStudent = new Student();
        this.studentListOfClass.add(middleStudent);
    }

    public void cancelAddStudent(Integer sclassId) {
        middleStudent = null;
        this.clearStudentListOfClass();
        this.studentsIdListOfClass = sClassRepo.getStudentsIdListByClassId(sclassId);
        for (Integer studentId : this.studentsIdListOfClass) {
            studentListOfClass.add(studentRepo.getById(studentId));
        }
    }

    public void updateSClassMentor(Integer sClassId, Integer studentId) {
        sClassRepo.updateMentorByClassId(sClassId, studentId);
        middleMentor = studentRepo.getById(studentId);
    }

    public void updateStudent(Integer sClassId, Integer studentId) {
        sClassRepo.saveStudentIntoClass(sClassId, studentId);
        this.cancelAddStudent(sClassId);
    }

    public void selectAllStudents() {
        for (Integer studentId : studentsIdListOfClass) {
            selectedStudentMap.put(studentId, true);
        }
    }

    public void unselectAllStudents() {
        for (Integer studentId : studentsIdListOfClass) {
            selectedStudentMap.put(studentId, false);
        }
    }

    public void deleteSelectedStudent(SClass sClass) {
        for (Integer studentId : this.getSelectedStudentIdList()) {
            sClassRepo.deleteStudentInClass(sClass.getId(), studentId);
        }
        this.moveToDetailPage(sClass);
        this.selectedStudentMap.clear();
    }

    public Validator validatorName() {
        return new Validator() {
            @Override
            public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
                String sName = o.toString();
                if (sName.length() < MINIMUM_LENGTH_NAME) {
                    FacesMessage facesMessage = new FacesMessage("Minimum name length is 2. Please enter again!");
                    facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(facesMessage);
                }
            }
        };
    }

    public Validator validatorMentorId() {
        return new Validator() {
            @Override
            public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
                Integer studentId = Integer.parseInt(o.toString());
                if (!studentsIdListOfClass.contains(studentId)) {
                    FacesMessage notFoundIdMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not found student ID " + studentId + " in student list", null);
                    throw new ValidatorException(notFoundIdMsg);
                }
            }
        };
    }

    public Converter getMentorConverter() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                Integer mentorId = Integer.parseInt(s);
                if (!studentsIdListOfClass.contains(mentorId)) {
                    middleMentor = studentRepo.getById(mentorId);
                } else throw new ConverterException("Not found student ID " + mentorId + " in class");
                return middleMentor;
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                return o.toString();
            }
        };
    }

    public Validator validatorStudentId() {
        return new Validator() {
            @Override
            public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
                Integer studentId = Integer.parseInt(o.toString());
                List<Integer> studentIdList = studentRepo.getAllStudentsId();
                if (studentsIdListOfClass.contains(studentId)) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Student " + studentId + " existed in class", null));
                } else if (!studentIdList.contains(studentId)) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not found student ID " + studentId + " in student list", null));
                }

            }
        };
    }
}
