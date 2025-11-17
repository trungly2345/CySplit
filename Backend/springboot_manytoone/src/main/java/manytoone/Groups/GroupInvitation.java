package manytoone.Groups;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import manytoone.Users.User;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "group_invitation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_group_user",
                columnNames = {"group_id", "user_id"}
        )
)
public class GroupInvitation {
    public enum invitationStatus  {
        Pending,
        Accepted,
        Declined
    }





@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "group_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_invitation_group")

    )
    @JsonIgnore
    private Group group;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_invitation_user")

    )
    @JsonIgnore
    private User user;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status", nullable = false)
    private invitationStatus inv_status = invitationStatus.Pending;

    public GroupInvitation() {}

    public GroupInvitation(Group group, User user) {
        this.group = group;
        this.user = user;
    }

    public invitationStatus getInv_status() { return inv_status; }

    public void setInv_status(invitationStatus inv_status) { this.inv_status = inv_status;}
    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    @Transient
    public String getUserName() { return user != null ? user.getUserName() : null;}

    public User getUser (){ return user; }
    public void setUserName(User user) { this.user = user; }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
}
