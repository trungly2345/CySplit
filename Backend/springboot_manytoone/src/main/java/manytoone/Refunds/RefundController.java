package manytoone.Refunds;

import manytoone.Bills.Bill;
import manytoone.Bills.BillRepository;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bills/{bill_id}/refunds")
public class RefundController {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public ResponseEntity<?> getRefundsForBill(@PathVariable int bill_id) {

        Optional<Bill> billOpt = billRepository.findById(bill_id);
        if (billOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Bill not found\"}");
        }

        Bill bill = billOpt.get();
        List<Refund> refunds = refundRepository.findByBill(bill);

        return ResponseEntity.ok(refunds);
    }


    @GetMapping("/{refund_id}")
    public ResponseEntity<?> getRefundById(@PathVariable int bill_id,
                                           @PathVariable int refund_id) {

        Optional<Refund> refundOpt = refundRepository.findById(refund_id);
        if (refundOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Refund not found\"}");
        }

        Refund refund = refundOpt.get();


        if (refund.getBill() == null || refund.getBill().getBillId() != bill_id) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Refund does not belong to this bill\"}");
        }

        return ResponseEntity.ok(refund);
    }


    @PostMapping
    public ResponseEntity<?> createRefund(@PathVariable int bill_id,
                                          @RequestBody Refund req) {

        Optional<Bill> billOpt = billRepository.findById(bill_id);
        if (billOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Bill not found\"}");
        }
        Bill bill = billOpt.get();


        if (req.getRefundedTo() == null || req.getRefundedTo().getId() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"refundedTo user is required\"}");
        }

        int userId = req.getRefundedTo().getId();
        User refundedTo = userRepository.findById(userId);
        if (refundedTo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"User to refund not found for id " + userId + "\"}");
        }

        req.setBill(bill);
        req.setRefundedTo(refundedTo);

        Refund saved = refundRepository.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{refund_id}")
    public ResponseEntity<?> editRefund(@PathVariable int bill_id,
                                        @PathVariable int refund_id,
                                        @RequestBody Refund req) {

        Optional<Refund> refundOpt = refundRepository.findById(refund_id);
        if (refundOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Refund not found\"}");
        }

        Refund refund = refundOpt.get();

        if (refund.getBill() == null || refund.getBill().getBillId() != bill_id) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Refund does not belong to this bill\"}");
        }

        // Update fields
        refund.setRefund_name(req.getRefund_name());
        refund.setRefund_amount(req.getRefund_amount());

        refundRepository.save(refund);

        return ResponseEntity.ok(refund);
    }

    @DeleteMapping("/{refund_id}")
    public ResponseEntity<?> deleteRefund(@PathVariable int bill_id,
                                          @PathVariable int refund_id) {

        Optional<Refund> refundOpt = refundRepository.findById(refund_id);
        if (refundOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Refund not found\"}");
        }

        Refund refund = refundOpt.get();

        if (refund.getBill() == null || refund.getBill().getBillId() != bill_id) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Refund does not belong to this bill\"}");
        }

        refundRepository.delete(refund);

        return ResponseEntity.noContent().build();
    }

}