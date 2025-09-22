package org.springframework.samples.petclinic.pet;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.samples.petclinic.owner.Owners;
import org.springframework.samples.petclinic.owner.OwnerRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pet")
public class PetController {

    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    public PetController(PetRepository petRepository, OwnerRepository ownerRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
    }

    @GetMapping("/all")
    public List<Pet> all() {
        return petRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> byId(@PathVariable Integer id) {
        return petRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public List<Pet> byOwner(@PathVariable Integer ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    @PostMapping
    public Pet create(@RequestBody Pet pet) {
        Pet saved = petRepository.save(pet);
        if (saved.getOwnerId() != null) {
            ownerRepository.findById(saved.getOwnerId()).ifPresent(o -> {
                o.setPetId(saved.getPetId());
                ownerRepository.save(o);
            });
        }
        return saved;
    }



    @GetMapping("/create")
    public String createDummy() {
        if (petRepository.count() > 0) return "Pets already exist";

        // trying to pair with owners if no will be null for now
        List<Owners> owners = ownerRepository.findAll();

        Pet p1 = new Pet("Rex", 3, "DOG", owners.size() > 0 ? owners.get(0).getId() : null);
        Pet p2 = new Pet("Misty", 2, "CAT", owners.size() > 1 ? owners.get(1).getId() : null);
        Pet p3 = new Pet("Bubbles", 1, "FISH", owners.size() > 2 ? owners.get(2).getId() : null);
        Pet p4 = new Pet("Flash", 5, "TURTLE", owners.size() > 3 ? owners.get(3).getId() : null);

        petRepository.saveAll(List.of(p1, p2, p3, p4));

        // update owners to hold petId if ownerId was set
        for (Pet p : petRepository.findAll()) {
            if (p.getOwnerId() != null) {
                Optional<Owners> o = ownerRepository.findById(p.getOwnerId());
                o.ifPresent(owner -> {
                    owner.setPetId(p.getPetId());
                    ownerRepository.save(owner);
                });
            }
        }
        return "Dummy pets created";
    }
}