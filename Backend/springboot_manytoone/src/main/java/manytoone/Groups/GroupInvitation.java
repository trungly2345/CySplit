package manytoone.Groups;



import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Table(name = "group_Invitation",
uniqueConstraints = @UniqueConstraint(name = "uk_group_user", columnNames = {"group_id", "user_name"})
)
public class GroupInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key


   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "group_id", nullable = false)
   @JsonIgnore
   private Group group;


    @Column(name = "user_name", nullable = false)
    private String userName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();


    public GroupInvitation(){}

    public GroupInvitation(Group group, String userName){
            this.group = group;
            this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }






}
