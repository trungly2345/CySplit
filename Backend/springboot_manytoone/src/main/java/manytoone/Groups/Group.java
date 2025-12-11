package manytoone.Groups;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


// TEst TEst TEst
@Entity
@Table(name = "groups")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id" , nullable = false)
    private int group_id; // Primary key
    @Column(name = "group_name" , nullable = false)
    private String group_name;


    @Column(name = "capacity")
    private int capacity;

    @Column(name = "join_code", unique = true, length = 5)
    private String joinCode;

    @Column(name = "is_temporary")
    @JsonProperty("isTemporary")
    private boolean isTemporary = false;


    public Group() {}                 // <-- required no-args ctor for JPA and Jackson


    public Group(String group_name, int capacity){
        this.group_name = group_name;
        this.capacity = capacity;
        this.isTemporary = false;
    }

    public Group(String group_name, int capacity, boolean isTemporary){
        this.group_name = group_name;
        this.capacity = capacity;
        this.isTemporary = isTemporary;
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

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

}
