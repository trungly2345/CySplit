package coms309;

import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")

@RestController
public class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Welcome " + name;
    }


}

