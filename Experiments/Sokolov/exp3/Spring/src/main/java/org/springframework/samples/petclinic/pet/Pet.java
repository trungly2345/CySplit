package org.springframework.samples.petclinic.pet;

import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer petId;

    private String name;
    private Integer age;
    private String type;
    private Integer ownerId;

    public Pet() {}

    public Pet(String name, Integer age, String type, Integer ownerId) {
        this.name = name;
        this.age = age;
        this.type = type;
        this.ownerId = ownerId;
    }

    public Integer getPetId() { return petId; }
    public void setPetId(Integer petId) { this.petId = petId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
}