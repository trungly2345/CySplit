package manytoone.Bills;

import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bill")   // base path: /bill/...
public class BillController {

    @Autowired
    private BillRepository billRepository;


    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = billRepository.findAll();
        return ResponseEntity.ok(bills);
    }

    // GET /bill/{bill_id}
    @GetMapping("/{bill_id}")
    public ResponseEntity<Bill> getBillById(@PathVariable int bill_id) {
        Optional<Bill> billOpt = billRepository.findById(bill_id);
        return billOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /bill
    // create a new bill
    @PostMapping
    public ResponseEntity<Bill> createBill(@RequestBody Bill req) {
        Bill newBill = billRepository.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBill);
    }

    // PUT /bill/{bill_id}
    // update existing bill
    @PutMapping("/{bill_id}")
    public ResponseEntity<Bill> updateBill(@PathVariable int bill_id, @RequestBody Bill request) {
        Optional<Bill> billOpt = billRepository.findById(bill_id);
        if (billOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Bill updateBill = billOpt.get();

        updateBill.setBill_name(request.getBill_name());
        updateBill.setBill_amount(request.getBill_amount());
        updateBill.setDueCreated(request.getDueCreated());
        updateBill.setDueTime(request.getDueTime());
        updateBill.setPaid(request.isPaid());


        billRepository.save(updateBill);
        return ResponseEntity.ok(updateBill);
    }

    // DELETE /bill/{bill_id}
    @DeleteMapping("/{bill_id}")
    public ResponseEntity<Void> deleteBillById(@PathVariable int bill_id) {
        if (!billRepository.existsById(bill_id)) {
            return ResponseEntity.notFound().build();
        }
        billRepository.deleteById(bill_id);
        return ResponseEntity.noContent().build();
    }
}