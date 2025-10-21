package manytoone.Groups;

import jakarta.persistence.*;
import manytoone.Users.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_groups")
public class UserGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "role")
    private String role;

    @Column(name = "joined_date", nullable = false)
    private LocalDateTime joinedDate;

    @Column(name = "total_contribution")
    private Double totalContribution;

    protected UserGroup() {}

    public UserGroup(User user, Group group) {
        this.user = user;
        this.group = group;
        this.role = "MEMBER";
        this.joinedDate = LocalDateTime.now();
        this.totalContribution = 0.0;
    }

    public UserGroup(User user, Group group, String role) {
        this(user, group);
        this.role = role;
    }

    public int getId() {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public Double getTotalContribution() {
        return totalContribution;
    }

    public void setTotalContribution(Double totalContribution) {
        this.totalContribution = totalContribution;
    }
}