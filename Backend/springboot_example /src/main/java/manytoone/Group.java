package unassigned;


import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import onetoone.Users.User;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;

@Entity
public class Group {

    @Id
    @NaturalId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int group_id; // Primary key
    private String group_name;
    private String group_role; // Primary Key
    private ArrayList<String> group_lists;
    private int capacity;

    // This can subject to change, so far this is just the initial implementation
    @ManyToMany
    @JsonIgnore
    private Group Group;

    public Group(String group_name, ArrayList <String> group_list, )

}
