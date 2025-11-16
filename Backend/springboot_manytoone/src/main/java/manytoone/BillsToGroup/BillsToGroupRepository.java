package manytoone.BillsToGroup;

import manytoone.Groups.Group;
import manytoone.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillsToGroupRepository extends JpaRepository<BillToGroup, Integer> {
    List<BillToGroup> findByGroupAndAssignedBy(Group group, User user);


}
