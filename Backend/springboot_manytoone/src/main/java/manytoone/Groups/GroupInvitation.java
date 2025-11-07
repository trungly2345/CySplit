package manytoone.Groups;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "group_invitation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_group_user",
                columnNames = {"group_id", "user_name"}
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

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status", nullable = false)
    private invitationStatus inv_status = invitationStatus.Pending;

    public GroupInvitation() {}

    public GroupInvitation(Group group, String userName) {
        this.group = group;
        this.userName = userName;
    }

    public invitationStatus getInv_status() { return inv_status; }

    public void setInv_status(invitationStatus inv_status) { this.inv_status = inv_status;}
    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
}
