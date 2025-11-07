package manytoone.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")  // Base path for all user endpoints
public class UserController {

    @Autowired
    UserRepository userRepository;


    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    // Search users by username (for adding friends/to groups - returns minimal info)
    @GetMapping("/search/{username}")
    public ResponseEntity<UserSearchResponse> searchUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserSearchResponse(user.getId(), user.getUserName(), user.getUserRating()));
    }

    // Get user by ID
    @GetMapping("/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable int user_id) {
        User user = userRepository.findById(user_id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // Create new user
    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Check if username already exists
        if (userRepository.existsByUserName(user.getUserName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User newUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // Update user
    @PutMapping("/{user_id}")
    public ResponseEntity<User> updateUser(@PathVariable int user_id, @RequestBody User request) {
        try {
            User existingUser = userRepository.findById(user_id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if new username already exists and is not the current user
            if (!request.getUserName().equals(existingUser.getUserName()) && 
                userRepository.existsByUserName(request.getUserName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Update all fields
            existingUser.setUserName(request.getUserName());
            existingUser.setUserPassword(request.getUserPassword());
            existingUser.setPhoneNumber(request.getPhoneNumber());
            existingUser.setPaymentMethod(request.getPaymentMethod());
            existingUser.setUserRating(request.getUserRating());
            existingUser.setName(request.getName());
            existingUser.setEmailId(request.getEmailId());
            existingUser.setIfActive(request.isIfActive());

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        }
    }

    // Delete user
    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int user_id) {
        if (!userRepository.existsById(user_id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(user_id);
        return ResponseEntity.noContent().build();
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUserNameAndPassword(
            loginRequest.getUserName(), 
            loginRequest.getUserPassword()
        );
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(user);
    }
}