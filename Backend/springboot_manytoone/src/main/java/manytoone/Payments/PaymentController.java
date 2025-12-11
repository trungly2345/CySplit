package manytoone.Payments;

import manytoone.Bills.Bill;
import manytoone.Bills.BillRepository;

import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("bills/{bill_id}/payments")
@RestController
public class PaymentController {

    @Autowired
    BillRepository billRepository;

   @Autowired
   PaymentRepository paymentRepository;

   @Autowired
   GroupRepository groupRepository;

   @Autowired
    UserRepository userRepository;


    @GetMapping
    public ResponseEntity<List<Payment>> getPaymentsForBill(
            @PathVariable("bill_id") int bill_id
    ) {
        List<Payment> payments = paymentRepository.findAll()
                .stream()
                .filter(p -> p.getBill() != null && p.getBill().getBillId() == bill_id)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{payment_id}")
    public ResponseEntity<?> getPaymentById(
            @PathVariable("bill_id") int billId,
            @PathVariable("payment_id") int paymentId
    ) {   Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Payment not found\"}");
        }

        Payment payment = paymentOpt.get();


        if (payment.getBill()  == null || payment.getBill().getBillId() != billId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Payment does not belong to this bill\"}");
        }

        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{group_id}")
    public ResponseEntity<?> createPayment(
            @PathVariable("bill_id") int billId,
            @PathVariable("group_id") int groupId,
            @RequestBody Payment req
    ) {

        Optional<Bill> billOpt = billRepository.findById(billId);
        if (billOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Bill not found"));
        }
        Bill bill = billOpt.get();


        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Group not found"));
        }


        if (req.getPayer() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "payer is required"));
        }


        int payerId = req.getPayer().getId();
        System.out.println("DEBUG payerId from JSON = " + payerId);

        User payer = userRepository.findById(payerId);
        if (payer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Payer not found for id " + payerId));
        }

        // 5. Resolve payee if provided (optional)
        User payee = null;
        if (req.getPayee() != null) {
            int payeeId = req.getPayee().getId();
            System.out.println("DEBUG payeeId from JSON = " + payeeId);

            payee = userRepository.findById(payeeId);
            if (payee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Payee not found for id " + payeeId));
            }
        }

        // 6. Attach relations
        req.setBill(bill);
        req.setGroup(group);
        req.setPayer(payer);
        req.setPayee(payee);

        // 7. Default date
        if (req.getDate() == null) {
            req.setDate(LocalDateTime.now());
        }

        Payment saved = paymentRepository.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{payment_id}")
    public ResponseEntity<?> deletePayment(@PathVariable int bill_id,
                                          @PathVariable int payment_id) {

        Optional<Payment> paymentOpt = paymentRepository.findById(payment_id);
        if (paymentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Payment not found\"}");
        }

        Payment payment = paymentOpt.get();

        if (payment.getBill() == null || payment.getBill().getBillId() != bill_id) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Payment does not belong to this bill\"}");
        }

        paymentRepository.delete(payment);

        return ResponseEntity.noContent().build();
    }

}
