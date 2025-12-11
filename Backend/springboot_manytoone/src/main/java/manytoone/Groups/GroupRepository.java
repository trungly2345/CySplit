package manytoone.Groups;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    Group findById(int id);


    // Find group by join code
    Group findByJoinCode(String joinCode);

    // Check if join code exists
    boolean existsByJoinCode(String joinCode);

}