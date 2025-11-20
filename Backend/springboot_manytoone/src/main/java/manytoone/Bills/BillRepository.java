package manytoone.Bills;

import manytoone.Groups.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


///  test for main 
public interface BillRepository extends JpaRepository<Bill, Integer> {

}
