package manytomany.Invitations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 *
 * @author Vivek Bengre
 *
 */

@RestController
public class GroupController {

    @Autowired
    GroupRepository groupRepository;


    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping("groups/{group_id}")
    public GroupInvitation getGroupById(@PathVariable int group_id) {
        return groupRepository.findById(group_id);
    }

    @PostMapping("groups/{group_id}")
    public ResponseEntity <GroupInvitation> createGroup(@RequestBody GroupInvitation req) {

     GroupInvitation newGroup = groupRepository.save(req);
      return ResponseEntity.status(HttpStatus.CREATED).body(newGroup);
    }



   @PutMapping("groups/{group_id}")
    public ResponseEntity <GroupInvitation> updateGroup(@PathVariable int group_id, @RequestBody GroupInvitation request){
       GroupInvitation updateGroup = groupRepository.findById(group_id);
       if (updateGroup == null){
           return null;
       }
       updateGroup.setGroup_name(request.getGroup_name());
       updateGroup.setCapacity(request.getCapacity());
       updateGroup.setId(request.getId());
       groupRepository.save(updateGroup);
       return ResponseEntity.ok(updateGroup);
   }


   @DeleteMapping("groups/{group_id}")
    public ResponseEntity <GroupInvitation> deleteGroupById(@PathVariable Long group_id){
     if (!groupRepository.existsById(group_id)){
         return null;
     }
     groupRepository.deleteById(group_id);
       return ResponseEntity.noContent().build();
   }
}
