package manytoone.Groups;


import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "group_id" , nullable = false)
    private int group_id; // Primary key
    @Column(name = "group_name" , nullable = false)
    private String group_name;



    @Column(name = "capacity")
    private int capacity;


    @JsonIgnore


    protected Group() {}                 // <-- required no-args ctor


    public Group(String group_name, int capacity ){
        this.group_name = group_name;
        this.capacity = capacity;
    }

    public int getId() {
        return group_id;
    }

    public void setId(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }



    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }




}
