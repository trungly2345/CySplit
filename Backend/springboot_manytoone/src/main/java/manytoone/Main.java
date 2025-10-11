package manytoone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "manytoone",      // your Group + GroupController
        "manytomany"      // your GroupInvitationController, entity, repo
})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}