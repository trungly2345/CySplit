package manytoone.Groups;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Group getGroupById(@PathVariable int group_id){
       return groupRepository.findById(group_id);
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
    public Group deleteGroupById(@PathVariable int group_id, @RequestBody Group request){
       Group group = groupRepository.deleteById(group_id);
       if (group == null){
           return null;
       }
       groupRepository.save(request);
       return groupRepository.deleteById(group_id);
   }
}
