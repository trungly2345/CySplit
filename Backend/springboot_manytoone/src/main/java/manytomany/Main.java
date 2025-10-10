package manytoone;

import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// If your main class is already in package "manytoone", you usually don't need these two:
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
// You can remove these if Main is in the root package "manytoone"

public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner initUser(GroupRepository groupRepository) {
        return args -> {



        // Mock data testing

//         Group group1 = new Group("Trip to Paris", 5);
//         Group group2 = new Group("NYC Trip", 3);
//         Group group3 = new Group("Miami Spring Break 2025", 10);
//
//
//         groupRepository.save(group1);
//         groupRepository.save(group2);
//         groupRepository.save(group3);



        };
    }
}