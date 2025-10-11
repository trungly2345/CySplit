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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 👈 use Integer (nullable for JPA)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "group_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_invitation_group") // 👈 stable FK name
    )
    @JsonIgnore
    private Group group;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    public GroupInvitation() {}

    public GroupInvitation(Group group, String userName) {
        this.group = group;
        this.userName = userName;
    }

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