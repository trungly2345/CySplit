package manytoone.Groups;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;



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

   @GetMapping
    public Group getGroupById(@PathVariable int groupId){
       return groupRepository.findById(groupId);

   }
}
