package manytoone.Groups;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import manytoone.Notifications.NotificationService;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @PostMapping("/groups/{group_id}/invitations")
    public ResponseEntity<GroupInvitation> createInvitation(
            @RequestBody GroupInvitation req, 
            @PathVariable("group_id") int groupId) {
        
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        User recipient = userRepository.findByUserName(req.getUserName());
        if (recipient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        // Check for duplicate invitation
        if (invitationRepository.existsByGroup_IdAndUserName(groupId, req.getUserName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        // TODO: Replace with actual logged-in user
        User inviter = userRepository.findById(1);
        
        req.setGroup(group);
        req.setInv_status(GroupInvitation.invitationStatus.Pending);
        GroupInvitation saved = invitationRepository.save(req);
        
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
        invitation.setUserName(req.getUserName());
        invitation.setDateCreated(req.getDateCreated());

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
                User admin = userRepository.findById(1);
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
