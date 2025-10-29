package manytoone.Groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public ResponseEntity<List<UserGroup>> getUserGroups(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        List<UserGroup> groups = userGroupRepository.findByUserId(userId);
        // Force initialization of lazy-loaded entities
        groups.forEach(ug -> {
            ug.getUser().getUserName();
            ug.getGroup().getId();
        });
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/users/{userId}/groups/active")
    @Transactional
    public ResponseEntity<List<UserGroup>> getUserActiveGroups(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        List<UserGroup> groups = userGroupRepository.findActiveGroupsByUserId(userId);
        // Force initialization of lazy-loaded entities
        groups.forEach(ug -> {
            ug.getUser().getUserName();
            ug.getGroup().getId();
        });
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/groups/{groupId}/members")
    @Transactional
    public ResponseEntity<List<UserGroup>> getGroupMembers(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        List<UserGroup> members = userGroupRepository.findByGroupId(groupId);
        // Force initialization of lazy-loaded entities
        members.forEach(ug -> {
            ug.getUser().getUserName();
            ug.getGroup().getId();
        });
        return ResponseEntity.ok(members);
    }

    @GetMapping("/groups/{groupId}/members/active")
    @Transactional
    public ResponseEntity<List<UserGroup>> getActiveGroupMembers(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        List<UserGroup> members = userGroupRepository.findActiveMembersByGroupId(groupId);
        // Force initialization of lazy-loaded entities
        members.forEach(ug -> {
            ug.getUser().getUserName();
            ug.getGroup().getId();
        });
        return ResponseEntity.ok(members);
    }

    @GetMapping("/groups/{groupId}/admins")
    @Transactional
    public ResponseEntity<List<UserGroup>> getGroupAdmins(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        List<UserGroup> admins = userGroupRepository.findAdminsByGroupId(groupId);
        // Force initialization of lazy-loaded entities
        admins.forEach(ug -> {
            ug.getUser().getUserName();
            ug.getGroup().getId();
        });
        return ResponseEntity.ok(admins);
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
    @Transactional
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
        UserGroup savedUserGroup = userGroupRepository.save(userGroup);
        
        // Force initialization
        savedUserGroup.getUser().getUserName();
        savedUserGroup.getGroup().getId();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUserGroup);
    }

    @PostMapping("/invitations/{invitationId}/accept")
    @Transactional
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

        // Access the group within the transaction to avoid LazyInitializationException
        Group group = invitation.getGroup();
        
        if (userGroupRepository.existsByUserIdAndGroupId(userId, group.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        invitation.setInv_status(GroupInvitation.invitationStatus.Accepted);
        invitationRepository.save(invitation);

        UserGroup userGroup = new UserGroup(user, group, UserGroup.Role.MEMBER);
        UserGroup savedUserGroup = userGroupRepository.save(userGroup);
        
        // Force initialization of lazy-loaded entities before transaction ends
        savedUserGroup.getUser().getUserName(); // Touch user to load it
        savedUserGroup.getGroup().getId(); // Touch group to load it
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUserGroup);
    }

    @PutMapping("/groups/{groupId}/members/{userId}/role")
    @Transactional
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
        UserGroup savedMembership = userGroupRepository.save(membership);
        
        // Force initialization
        savedMembership.getUser().getUserName();
        savedMembership.getGroup().getId();
        
        return ResponseEntity.ok(savedMembership);
    }

    @PutMapping("/groups/{groupId}/members/{userId}/contribution")
    @Transactional
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
        UserGroup savedMembership = userGroupRepository.save(membership);
        
        // Force initialization
        savedMembership.getUser().getUserName();
        savedMembership.getGroup().getId();
        
        return ResponseEntity.ok(savedMembership);
    }

    @DeleteMapping("/groups/{groupId}/members/{userId}")
    @Transactional
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