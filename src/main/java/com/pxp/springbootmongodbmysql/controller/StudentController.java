package com.pxp.springbootmongodbmysql.controller;

import com.pxp.springbootmongodbmysql.model.StudentModel;
import com.pxp.springbootmongodbmysql.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    @Autowired
    StudentService studentService;

    @RequestMapping(value = "info", method = RequestMethod.GET)
    public String info(){
        return "The application is up...";
    }

    @RequestMapping(value = "createresource", method = RequestMethod.POST)
    public String createStudent(@RequestBody StudentModel studentModel){
        return studentService.createResource(studentModel);
    }

    @RequestMapping(value = "readresources", method = RequestMethod.GET)
    public List<StudentModel> readResources(){
        return studentService.readResources();
    }

    @RequestMapping(value = "updateresource", method = RequestMethod.PUT)
    public String updateStudent(@RequestBody StudentModel studentModel){
        return studentService.updateResource(studentModel);
    }

    @RequestMapping(value = "deleteresource", method = RequestMethod.DELETE)
    public String deleteStudent(@RequestBody StudentModel studentModel){
        return studentService.deleteResource(studentModel);
    }
}
