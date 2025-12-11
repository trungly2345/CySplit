package manytoone.Groups;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import manytoone.Users.User;
import manytoone.Users.UserRepository;


/**
 *
 * @author Vivek Bengre
 *
 */

@RestController
public class GroupController {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserGroupRepository userGroupRepository;
    // re testing before deploy jar file 
    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping("/groups/{group_id}")
    public Group getGroupById(@PathVariable int group_id) {
        return groupRepository.findById(group_id);
    }

    @PostMapping("/groups")
    public ResponseEntity <Group> createGroup(@RequestBody Group req) {
        // If this is a temporary group and no join code provided, generate one
        if (req.isTemporary() && (req.getJoinCode() == null || req.getJoinCode().isEmpty())) {
            String uniqueCode = JoinCodeGenerator.generateUniqueCode(groupRepository);
            req.setJoinCode(uniqueCode);
        }
        Group newGroup = groupRepository.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(newGroup);
    }



   @PutMapping("/groups/{group_id}")
    public ResponseEntity <Group> updateGroup(@PathVariable int group_id, @RequestBody Group request){
       Group updateGroup = groupRepository.findById(group_id);
       if (updateGroup == null){
           return null;
       }
       updateGroup.setGroup_name(request.getGroup_name());
       updateGroup.setCapacity(request.getCapacity());
       updateGroup.setId(request.getId());
       groupRepository.save(updateGroup);
       return ResponseEntity.ok(updateGroup);
   }


   @DeleteMapping("/groups/{group_id}")
    public ResponseEntity <Group> deleteGroupById(@PathVariable int group_id){
     Group group = groupRepository.findById(group_id);
     if (group == null){
         return ResponseEntity.notFound().build();
     }
     groupRepository.deleteById(group_id);
       return ResponseEntity.noContent().build();
   }

   // ========== JOIN BY CODE ENDPOINT ==========
   @PostMapping("/groups/join/{code}")
   public ResponseEntity<?> joinGroupByCode(
           @PathVariable String code,
           @RequestParam int userId) {
       
       // Find group by join code
       Group group = groupRepository.findByJoinCode(code.toUpperCase());
       if (group == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("{\"message\":\"Invalid join code\"}");
       }
       
       // Find user
       User user = userRepository.findById(userId);
       if (user == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("{\"message\":\"User not found\"}");
       }
       
       // Check if user is already in the group
       if (userGroupRepository.existsByUserIdAndGroupId(userId, group.getId())) {
           return ResponseEntity.status(HttpStatus.CONFLICT)
                   .body("{\"message\":\"User already in group\"}");
       }
       
       // Add user to group as MEMBER
       UserGroup userGroup = new UserGroup(user, group, UserGroup.Role.MEMBER);
       UserGroup savedUserGroup = userGroupRepository.save(userGroup);
       
       // Force initialization
       savedUserGroup.getUser().getUserName();
       savedUserGroup.getGroup().getId();
       
       return ResponseEntity.status(HttpStatus.CREATED).body(savedUserGroup);
   }

   // ========== GET GROUP BY CODE ENDPOINT ==========
   @GetMapping("/groups/code/{code}")
   public ResponseEntity<?> getGroupByCode(@PathVariable String code) {
       Group group = groupRepository.findByJoinCode(code.toUpperCase());
       if (group == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("{\"message\":\"Invalid join code\"}");
       }
       return ResponseEntity.ok(group);
   }
}
