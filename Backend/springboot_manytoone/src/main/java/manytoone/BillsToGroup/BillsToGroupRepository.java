package manytoone.BillsToGroup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import manytoone.Bills.Bill;
import manytoone.Groups.Group;
import manytoone.Users.User;

public interface BillsToGroupRepository extends JpaRepository<BillToGroup, Integer> {
    List<BillToGroup> findByGroupAndAssignedBy(Group group, User user);
    
    // Find all groups a bill is assigned to
    List<BillToGroup> findByBill(Bill bill);
    
    // Find all bills assigned to a group
    List<BillToGroup> findByGroup(Group group);
}
