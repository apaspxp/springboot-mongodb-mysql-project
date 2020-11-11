package com.pxp.springbootmongodbmysql.service;

import com.pxp.springbootmongodbmysql.document.Course;
import com.pxp.springbootmongodbmysql.entity.Student;
import com.pxp.springbootmongodbmysql.model.CourseModel;
import com.pxp.springbootmongodbmysql.model.StudentModel;
import com.pxp.springbootmongodbmysql.repository.CourseRepository;
import com.pxp.springbootmongodbmysql.repository.StudentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StudentService {
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Transactional
    public String createResource(StudentModel studentModel){
        if (!studentRepository.existsByEmail(studentModel.getEmail())){
            Student student = new Student();
            BeanUtils.copyProperties(studentModel, student);
            try {
                studentRepository.save(student);
                studentModel.getCourses().stream().forEach(c -> {
                    Course course = new Course();
                    c.setEmail(studentModel.getEmail());
                    BeanUtils.copyProperties(c, course);
                    try {
                        courseRepository.save(course);
                    }catch (Exception e){
                        throw e;
                    }

                });
            }catch (Exception e){
                throw e;
            }

            return "Resource added successfully!";
        }else {
            return "Duplicate resource";
        }
    }

    public List<StudentModel> readResources(){
        List<StudentModel> students = new ArrayList<>();
        List<Student> studentList = new ArrayList<>();
        try {
            studentList = studentRepository.findAll(); //Fetch all the Students from the database.
        }catch (Exception e){
            throw e;
        }

        if (studentList.size() > 0){ //If the above list is not empty then return the list after unwrapping all the records
            studentList.stream().forEach(s -> { //Traverse through the reords
                StudentModel studentModel = new StudentModel();
                studentModel.setFirstName(s.getFirstName());
                studentModel.setLastName(s.getLastName());
                studentModel.setEmail(s.getEmail());
                List<Course> courseList = new ArrayList<>();
                List<CourseModel> courses = new ArrayList<>();
                try {
                    courseList = courseRepository.findCourseByEmail(s.getEmail()); //Fetch all the courses by email ID.
                }catch (Exception e){
                    throw e;
                }
                if (courseList.size() > 0){
                    courseList.stream().forEach(c -> {
                        CourseModel courseModel = new CourseModel();
                        BeanUtils.copyProperties(c, courseModel);
                        courses.add(courseModel);
                    });
                }
                studentModel.setCourses(courses);
                students.add(studentModel);
            });
        }
        return students;
    }

    @Transactional
    public String updateResource(StudentModel studentModel){
        if (studentRepository.existsByEmail(studentModel.getEmail())){ //Check for availability of resource by email ID. Update if resource exists.
            Student student = studentRepository.findByEmail(studentModel.getEmail()).get(0);
            BeanUtils.copyProperties(studentModel, student);
            try {
                studentRepository.save(student); //Update Student
                List<Course> courses = courseRepository.findCourseByEmail(studentModel.getEmail()); //Read the courses from the database
                for (int i = 0; i < studentModel.getCourses().size(); i++){
                    BeanUtils.copyProperties(studentModel.getCourses().get(i),courses.get(i));
                }

                courses.stream().forEach(c -> {
                    Course course = courseRepository.findById(c.getId()).get();
                    BeanUtils.copyProperties(c, course);
                    course.setEmail(studentModel.getEmail());
                    courseRepository.save(course);
                });
//                studentModel.getCourses().stream().forEach(c -> { //Traverse through the studentModel to fetch all the courses
//                    try {
//                        Course course = courseRepository.findCourseByEmail(studentModel.getEmail()); //Read the courses from the database
//                        if (Objects.nonNull(courses)){ //Update the course if exists
//                            courses.stream().forEach(c1 -> System.out.println(c1.getId()));
//                            c.setEmail(studentModel.getEmail());
//                            BeanUtils.copyProperties(c, course);
//                            try {
//                                courseRepository.save(course);
//                            }catch (Exception e){
//                                throw e;
//                            }
//                        }
//                    }catch (Exception e){
//                        throw e;
//                    }
//                });
            }catch (Exception e){
                throw e;
            }
            return "Resource was updated successfully";
        }else{
            return "Resource does not exist";
        }
    }

    @Transactional
    public String deleteResource(StudentModel studentModel){
        if (studentRepository.existsByEmail(studentModel.getEmail())){
            try {
                studentRepository.deleteByEmail(studentModel.getEmail());
                try {
                    courseRepository.deleteByEmail(studentModel.getEmail());
                }catch (Exception e){
                    throw e;
                }
            }catch (Exception e){
                throw e;
            }
            return "Removed successfully!";
        }else {
            return "Resource does not exist.";
        }
    }
}
