package org.springframework.samples.petclinic.pet;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    List<Pet> findByOwnerId(Integer ownerId);
    List<Pet> findByNameIgnoreCase(String name);
}