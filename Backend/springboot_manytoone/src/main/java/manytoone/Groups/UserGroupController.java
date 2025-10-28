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

    @Autowired
    private GroupInvitationRepository invitationRepository;

    @GetMapping("/users/{userId}/groups")
    public ResponseEntity<List<UserGroup>> getUserGroups(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userGroupRepository.findByUserId(userId));
    }

    @GetMapping("/users/{userId}/groups/active")
    public ResponseEntity<List<UserGroup>> getUserActiveGroups(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userGroupRepository.findActiveGroupsByUserId(userId));
    }

    @GetMapping("/groups/{groupId}/members")
    public ResponseEntity<List<UserGroup>> getGroupMembers(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userGroupRepository.findByGroupId(groupId));
    }

    @GetMapping("/groups/{groupId}/members/active")
    public ResponseEntity<List<UserGroup>> getActiveGroupMembers(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userGroupRepository.findActiveMembersByGroupId(groupId));
    }

    @GetMapping("/groups/{groupId}/admins")
    public ResponseEntity<List<UserGroup>> getGroupAdmins(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userGroupRepository.findAdminsByGroupId(groupId));
    }

    @GetMapping("/groups/{groupId}/members/count")
    public ResponseEntity<Long> getGroupMemberCount(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userGroupRepository.countActiveMembersByGroupId(groupId));
    }

    @PostMapping("/groups/{groupId}/members")
    public ResponseEntity<UserGroup> addMemberToGroup(
            @PathVariable int groupId, 
            @RequestParam int userId,
            @RequestParam(required = false, defaultValue = "MEMBER") String role) {
        
        Group group = groupRepository.findById(groupId);
        User user = userRepository.findById(userId);
        
        if (group == null || user == null) {
            return ResponseEntity.notFound().build();
        }

        if (userGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserGroup.Role userRole;
        try {
            userRole = UserGroup.Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        UserGroup userGroup = new UserGroup(user, group, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(userGroupRepository.save(userGroup));
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<UserGroup> acceptInvitation(
            @PathVariable int invitationId,
            @RequestParam int userId) {
        
        Optional<GroupInvitation> invitationOpt = invitationRepository.findById((long) invitationId);
        if (invitationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        GroupInvitation invitation = invitationOpt.get();
        
        if (invitation.getInv_status() != GroupInvitation.invitationStatus.Pending) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getUserName().equals(invitation.getUserName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (userGroupRepository.existsByUserIdAndGroupId(userId, invitation.getGroup().getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        invitation.setInv_status(GroupInvitation.invitationStatus.Accepted);
        invitationRepository.save(invitation);

        UserGroup userGroup = new UserGroup(user, invitation.getGroup(), UserGroup.Role.MEMBER);
        return ResponseEntity.status(HttpStatus.CREATED).body(userGroupRepository.save(userGroup));
    }

    @PutMapping("/groups/{groupId}/members/{userId}/role")
    public ResponseEntity<UserGroup> updateMemberRole(
            @PathVariable int groupId,
            @PathVariable int userId,
            @RequestParam int requesterId,
            @RequestParam String newRole) {
        
        Optional<UserGroup> requesterMembership = userGroupRepository.findByUserIdAndGroupId(requesterId, groupId);
        if (requesterMembership.isEmpty() || !requesterMembership.get().isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<UserGroup> targetMembership = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if (targetMembership.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserGroup.Role role;
        try {
            role = UserGroup.Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        UserGroup membership = targetMembership.get();
        membership.setRole(role);
        return ResponseEntity.ok(userGroupRepository.save(membership));
    }

    @PutMapping("/groups/{groupId}/members/{userId}/contribution")
    public ResponseEntity<UserGroup> updateMemberContribution(
            @PathVariable int groupId,
            @PathVariable int userId,
            @RequestParam Double amount) {
        
        Optional<UserGroup> membershipOpt = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if (membershipOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserGroup membership = membershipOpt.get();
        membership.setTotalContribution(membership.getTotalContribution() + amount);
        return ResponseEntity.ok(userGroupRepository.save(membership));
    }

    @DeleteMapping("/groups/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromGroup(
            @PathVariable int groupId,
            @PathVariable int userId,
            @RequestParam int requesterId) {
        
        Optional<UserGroup> requesterMembership = userGroupRepository.findByUserIdAndGroupId(requesterId, groupId);
        if (requesterMembership.isEmpty() || !requesterMembership.get().isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<UserGroup> targetMembership = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if (targetMembership.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userGroupRepository.delete(targetMembership.get());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/groups/{groupId}/members/{userId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable int groupId,
            @PathVariable int userId) {
        
        Optional<UserGroup> membershipOpt = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if (membershipOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserGroup membership = membershipOpt.get();
        
        List<UserGroup> admins = userGroupRepository.findAdminsByGroupId(groupId);
        if (membership.isAdmin() && admins.size() == 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        membership.setIsActive(false);
        userGroupRepository.save(membership);
        return ResponseEntity.noContent().build();
    }
}