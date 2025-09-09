package coms309;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Scanner;


@RestController
public class WelcomeController {


    @GetMapping("/{college}")
    public String college(@PathVariable String college){
        return "Iowa State University of Science Technology \n College Of " +college;
    }
    // Created a Get path with the name as a path variable
    @GetMapping("/{name}/")
    public ResponseEntity<StudentDto> getStudentData(@PathVariable String name)  {
            // If the name is null throw an exception that the name is not found and kills the program
            if (name == null){
                throw new RuntimeException("Name is not found!");
            }



            var responseDto = new StudentDto();
            responseDto.setWelcomeMessage("Welcome to 309 " + name +"\n");
            responseDto.setName(name);
            responseDto.setClassification("Senior");
            responseDto.setAge(23);
            responseDto.setMajor("Computer Science");


        return ResponseEntity.ok(responseDto);
    }






    }



