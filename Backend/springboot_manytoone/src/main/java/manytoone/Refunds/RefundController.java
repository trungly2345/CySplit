package manytoone.Refunds;


import manytoone.Bills.Bill;
import manytoone.Bills.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("bills/refunds")
public class RefundController {
    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private BillRepository billRepository;


    // List all refunds
    @GetMapping
    public ResponseEntity<List<Refund>> getAllRefunds() {
        List<Refund> refunds = refundRepository.findAll();
        return ResponseEntity.ok(refunds);
    }


    // Get a refund by refund id
    @GetMapping("/{refund_id}")
    public ResponseEntity<Refund> getRefundById(@PathVariable int refund_id) {
        Optional<Refund> refundOpt = refundRepository.findById(refund_id);
        return refundOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    /***
     * Creates a refund from an existing bill
     *
     * @param bill_id, a specific bill id from an existing bill
     * @param req, a request body from refund to be saved to the repository
     *
     * @return a response body from the post request
     */
    @PostMapping("/{bill_id}")
    public ResponseEntity<Refund> createRefund(@PathVariable int bill_id,
                                               @RequestBody Refund req) {

        Bill bill = billRepository.findById(bill_id).orElseThrow(() -> new RuntimeException("Bill not found"));
        req.setBill(bill);
        Refund newRefund = refundRepository.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRefund);
    }


}
