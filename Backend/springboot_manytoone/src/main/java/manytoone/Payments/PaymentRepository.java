package manytoone.Payments;

import manytoone.Bills.BillShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
