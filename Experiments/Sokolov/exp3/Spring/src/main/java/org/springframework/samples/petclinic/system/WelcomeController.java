package org.springframework.samples.petclinic.system;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Welcome</br> Go to localhost:8080/owners/create to create dummy data </br>" +
                "</br> Go to localhost:8080/pet/create to create dummy pets </br>" +
                "</br> Seed owners (your existing): http://localhost:8080/owners/create </br>" +
                "</br> Seed pets: http://localhost:8080/pet/create </br>" +
                "</br> List pets: http://localhost:8080/pet/all </br>" +
                "</br> Find by id: http://localhost:8080/pet/1 </br>" +
                "</br> Find by owner: http://localhost:8080/pet/owner/{ownerId} </br>";
    }
}
