package manytoone.Bills;

import manytoone.Groups.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillItemRepository extends JpaRepository<BillItem, Integer> {
    
    // Find all items for a group
    List<BillItem> findByGroup(Group group);
    
    // Find all items for a group by group ID
    @Query("SELECT bi FROM BillItem bi WHERE bi.group.id = :groupId")
    List<BillItem> findByGroupId(@Param("groupId") int groupId);
    
    // Find unpaid items for a group
    @Query("SELECT bi FROM BillItem bi WHERE bi.group.id = :groupId AND bi.isPaid = false")
    List<BillItem> findUnpaidItemsByGroupId(@Param("groupId") int groupId);
    
    // Find paid items for a group
    @Query("SELECT bi FROM BillItem bi WHERE bi.group.id = :groupId AND bi.isPaid = true")
    List<BillItem> findPaidItemsByGroupId(@Param("groupId") int groupId);
}
