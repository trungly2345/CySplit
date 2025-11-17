package manytoone.Bills;

import manytoone.BillsToGroup.BillToGroup;
import manytoone.BillsToGroup.BillsToGroupRepository;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/billshare")
@RestController
public class BillShareController {

    @Autowired
    BillsToGroupRepository billsToGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BillShareRepository billShareRepository;



    @PutMapping("/{groupBillId}/")
    public ResponseEntity<?> updateCustomShares(
            @PathVariable Integer groupBillId,
            @RequestBody Map<String, Object> body) {

        List<Map<String, Object>> shares = (List<Map<String, Object>>) body.get("shares");
        if (shares == null || shares.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("{\"message\":\"Shares list is required\"}");
        }

        BillToGroup groupBill = billsToGroupRepository.findById(groupBillId).orElse(null);
        if (groupBill == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\":\"GroupBill not found\"}");
        }

        Bill bill = groupBill.getBill();
        BigDecimal billTotal = new BigDecimal(bill.getBill_amount());
        BigDecimal runningSum = BigDecimal.ZERO;

        List<BillShare> updated = new ArrayList<>();

        for (Map<String, Object> s : shares) {
            Integer userId = (Integer) s.get("userId");
            String amountStr = s.get("amount").toString();

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"message\":\"User " + userId + " not found\"}");
            }

            BigDecimal amount = new BigDecimal(amountStr);
            runningSum = runningSum.add(amount);

            Optional<BillShare> shareOpt =
                    billShareRepository.findByGroupBill_GroupBillIdAndUser_Id(groupBillId, userId);


            BillShare share;
            if (shareOpt.isEmpty()){
                share = new BillShare(groupBill, user, amountStr);
            } else {
                share = shareOpt.get();
                share.setAmountOwed(amountStr);
            }

            updated.add(share);

        }

        // Validate the sum matches the bill amount
        if (runningSum.compareTo(billTotal) != 0) {
            return ResponseEntity.badRequest()
                    .body("{\"message\":\"Shares do not add up to bill total\"}");
        }

        billShareRepository.saveAll(updated);

        return ResponseEntity.ok(updated);
    }
}
