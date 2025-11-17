package manytoone.Groups;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import manytoone.Notifications.NotificationService;
import manytoone.Users.User;
import manytoone.Users.UserRepository;

@RestController
public class GroupInvitationController {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupInvitationRepository invitationRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    NotificationService notificationService;




    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping("/invitations")
    public List<GroupInvitation> getGroupInviationById() {
        return invitationRepository.findAll();
    }

    @GetMapping("/invitations/{invitation_id}")
    public ResponseEntity <GroupInvitation> getGroupInvitationById(@PathVariable Long invitation_id) {
        Optional<GroupInvitation> invitation = invitationRepository.findById(invitation_id);
        return invitation.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/invitations/user/{userName}")
    public ResponseEntity<List<GroupInvitation>> getInvitationsForUser(@PathVariable String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<GroupInvitation> invitations = invitationRepository.findAllByUser_Id(user.getId());
        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/groups/{group_id}/invitations")
    public ResponseEntity<List<GroupInvitation>> getInvitationsForGroup(@PathVariable int group_id) {
        Group group = groupRepository.findById(group_id);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<GroupInvitation> invitations = invitationRepository.findAllByGroup_Id(group_id);
        return ResponseEntity.ok(invitations);
    }

    // Invite a specific user to a specific group
    @PostMapping("/groups/{user_id}/{group_id}/invitations")
    public ResponseEntity<GroupInvitation> createInvitation(
            @PathVariable("group_id") int groupId,
            @PathVariable("user_id") int userId) {


        // find group
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


        // find user
        User recipient = userRepository.findById(userId);
        if (recipient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        if (invitationRepository.existsByGroup_IdAndUser_Id(groupId, userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        User inviter = userRepository.findAll().stream().findFirst().orElse(null);
        if (inviter == null) {
            System.out.println("WARNING: No users exist in database. Create a user first.");
        }

        GroupInvitation invitation = new GroupInvitation(group, recipient);
        invitation.setInv_status(GroupInvitation.invitationStatus.Pending);
        GroupInvitation saved = invitationRepository.save(invitation);

        
        if (inviter != null) {
            notificationService.notifyGroupInvitation(recipient, group, inviter);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }



    @PutMapping("invitations/{invitation_id}")
    public ResponseEntity <GroupInvitation> updateGroup(@PathVariable int invitation_id, @RequestBody GroupInvitation req){
        Optional<GroupInvitation>currentInv = invitationRepository.findById((long) invitation_id);
        if (currentInv.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        GroupInvitation invitation = currentInv.get();
        if (req.getInv_status() == null){
            invitation.setInv_status(req.getInv_status());
        }

        if (req.getDateCreated() == null){
            invitation.setDateCreated(req.getDateCreated());
        }

        GroupInvitation updated = invitationRepository.save(invitation);
        return ResponseEntity.ok(updated);


    }



    @PutMapping("invitations/{invitation_id}/invitationStatus")
    public ResponseEntity<GroupInvitation> updateInvitationStatus(
            @PathVariable int invitation_id, 
            @RequestBody GroupInvitation req) {
        
        Optional<GroupInvitation> currentInv = invitationRepository.findById((long) invitation_id);
        if (currentInv.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        GroupInvitation invitation = currentInv.get();
        GroupInvitation.invitationStatus oldStatus = invitation.getInv_status();
        
        // Update the status
        if (req.getInv_status() != null) {
            invitation.setInv_status(req.getInv_status());
        }
        
        GroupInvitation updated = invitationRepository.save(invitation);
        
        if (req.getInv_status() == GroupInvitation.invitationStatus.Accepted && 
            oldStatus != GroupInvitation.invitationStatus.Accepted) {
            
            User acceptedUser = userRepository.findByUserName(invitation.getUserName());
            Group group = invitation.getGroup();
            
            if (acceptedUser != null && group != null) {
                User admin = userRepository.findAll().stream().findFirst().orElse(null);
                if (admin != null) {
                    notificationService.notifyInvitationAccepted(admin, group, acceptedUser);
                }
            }
        }
        
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("invitations/{invitation_id}")
    public ResponseEntity <GroupInvitation> deleteGroupById(@PathVariable Long invitation_id){
        if (!invitationRepository.existsById(invitation_id)){
            return ResponseEntity.notFound().build();
        }
        invitationRepository.deleteById(invitation_id);
        return ResponseEntity.noContent().build();
    }






}
