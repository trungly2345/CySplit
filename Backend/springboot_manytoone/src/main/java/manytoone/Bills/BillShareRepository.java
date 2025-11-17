package manytoone.Bills;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillShareRepository extends JpaRepository<BillShare, Integer> {
    List<BillShare> findByGroupBill_GroupBillId(Integer groupBillId);
    Optional<BillShare> findByGroupBill_GroupBillIdAndUser_Id(Integer groupBillId, Integer userId);
}
