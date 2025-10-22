package manytoone.Groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
import java.util.List;
import java.util.Optional;

@RestController
public class UserGroupController {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping("/users/{userId}/groups")
    public ResponseEntity<List<UserGroup>> getUserGroups(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userGroupRepository.findByUserId(userId));
    }

    @GetMapping("/groups/{groupId}/users")
    public ResponseEntity<List<UserGroup>> getGroupMembers(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userGroupRepository.findByGroupId(groupId));
    }

    @PostMapping("/groups/{groupId}/users/{userId}")
    public ResponseEntity<UserGroup> addUserToGroup(@PathVariable int groupId, @PathVariable int userId, @RequestBody(required = false) String role) {
        Group group = groupRepository.findById(groupId);
        User user = userRepository.findById(userId); // don't want the user id be null
        
        if (group == null || user == null) {
            return ResponseEntity.notFound().build();
        }

        if (userGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserGroup userGroup = role != null ? new UserGroup(user, group, role) : new UserGroup(user, group);
        return ResponseEntity.status(HttpStatus.CREATED).body(userGroupRepository.save(userGroup));
    }

    @DeleteMapping("/groups/{groupId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromGroup(@PathVariable int groupId, @PathVariable int userId) {
        Optional<UserGroup> userGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if (userGroup.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        userGroupRepository.delete(userGroup.get());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/groups/{groupId}/users/{userId}/role")
    public ResponseEntity<UserGroup> updateUserRole(@PathVariable int groupId, @PathVariable int userId, @RequestBody String newRole) {
        Optional<UserGroup> userGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if (userGroup.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserGroup membership = userGroup.get();
        membership.setRole(newRole);
        return ResponseEntity.ok(userGroupRepository.save(membership));
    }
}