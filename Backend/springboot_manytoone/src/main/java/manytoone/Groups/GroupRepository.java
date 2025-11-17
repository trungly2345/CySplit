package manytoone.Groups;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    Group findById(int id);

    @Transactional
    Group deleteById(int id);




}