package manytoone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import manytoone.Users.User;
import manytoone.Users.UserRepository;

@SpringBootApplication(scanBasePackages = {
        "manytoone"
})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    //trying to add new format user to the database on startup for testing. Will remove later. Will use the same construction to connect it with Groups.
    @Bean
    CommandLineRunner initUser(UserRepository userRepository) {
        return args -> {
            // Create a test user
            User testUser = new User(
                "testuser",         // userName
                "password123",      // userPassword
                "515-123-4567",    // phoneNumber
                "PayPal"           // paymentMethod
            );
            testUser.setName("Test User");
            testUser.setEmailId("testuser@iastate.edu");
            testUser.setIfActive(true);
            
            // Save the user to database
            if (!userRepository.existsByUserName("testuser")) {
                User savedUser = userRepository.save(testUser);
                System.out.println("Test user created with ID: " + savedUser.getId());
            } else {
                System.out.println("Test user already exists");
            } 
        };
    }
}