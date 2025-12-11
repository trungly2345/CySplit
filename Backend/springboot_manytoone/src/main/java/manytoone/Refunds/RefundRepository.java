package manytoone.Refunds;



import manytoone.Bills.Bill;
import manytoone.Refunds.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Integer> {
    List<Refund> findByBill(Bill bill);
}
