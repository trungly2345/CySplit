package manytoone.Groups;

import jakarta.persistence.*;
import manytoone.Users.User;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_groups",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_group",
        columnNames = {"user_id", "group_id"}
    )
)
public class UserGroup {
    
    public enum Role {
        ADMIN,
        MEMBER
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "joined_date", nullable = false)
    private LocalDateTime joinedDate;

    @Column(name = "total_contribution")
    private Double totalContribution;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    protected UserGroup() {}

    public UserGroup(User user, Group group, Role role) {
        this.user = user;
        this.group = group;
        this.role = role;
        this.joinedDate = LocalDateTime.now();
        this.totalContribution = 0.0;
        this.isActive = true;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isMember() {
        return this.role == Role.MEMBER;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDateTime joinedDate) {
        this.joinedDate = joinedDate;
    }

    public Double getTotalContribution() {
        return totalContribution;
    }

    public void setTotalContribution(Double totalContribution) {
        this.totalContribution = totalContribution;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}