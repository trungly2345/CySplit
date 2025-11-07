package manytoone.Groups;


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
    public Group getGroupById(@PathVariable int group_id) {
        return groupRepository.findById(group_id);
    }

    @PostMapping("groups/{group_id}")
    public ResponseEntity <Group> createGroup(@RequestBody Group req) {

     Group newGroup = groupRepository.save(req);
      return ResponseEntity.status(HttpStatus.CREATED).body(newGroup);
    }



   @PutMapping("groups/{group_id}")
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


   @DeleteMapping("groups/{group_id}")
    public ResponseEntity <Group> deleteGroupById(@PathVariable Long group_id){
     if (!groupRepository.existsById(group_id)){
         return null;
     }
     groupRepository.deleteById(group_id);
       return ResponseEntity.noContent().build();
   }
}
