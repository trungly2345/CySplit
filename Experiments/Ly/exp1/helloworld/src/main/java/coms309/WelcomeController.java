package coms309;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class WelcomeController {

    // start with some variables
    @GetMapping("/{name}")
    public ResponseEntity<StudentDto> getStudentData(@PathVariable String name)  {
            if (name == null){
                throw new RuntimeException("Name is not found!");
            }
            var responseDto = new StudentDto();
            responseDto.setName(name);
            responseDto.setClassification("Senior");
            responseDto.setAge(23);
            responseDto.setMajor("Computer Science");


        return ResponseEntity.ok(responseDto);
    }






    }



