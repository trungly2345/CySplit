package manytoone.BillsToGroup;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import manytoone.Bills.Bill;
import manytoone.Bills.BillRepository;
import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;
import manytoone.Notifications.NotificationService;
import manytoone.Users.User;
import manytoone.Users.UserRepository;

@RequestMapping("/groupbill")
@RestController
public class BillsToGroupController {
    // Local Test branch pipeline test
    @Autowired
    BillsToGroupRepository billsToGroupRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BillRepository billRepository;

    @Autowired
    NotificationService notificationService;




    @PostMapping("/{groupId}/bills")
    public ResponseEntity<?> assignBillToGroup(
            @PathVariable Integer groupId,
            @RequestBody Map<String, Object> body) {

        // Extract billId
        Integer billId = (Integer) body.get("billId");
        if (billId == null) {
            return ResponseEntity.badRequest().body("{\"message\":\"billId is required\"}");
        }


        Integer assignedByUserId = body.get("assignedByUserId") != null
                ? (Integer) body.get("assignedByUserId")
                : null;

        String notes = body.get("notes") != null
                ? body.get("notes").toString()
                : null;


        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\":\"Group not found\"}");
        }

        // 2. Load Bill
        Bill bill = billRepository.findById(billId).orElse(null);
        if (bill == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\":\"Bill not found\"}");
        }


        User assignedBy = null;
        if (assignedByUserId != null) {
            assignedBy = userRepository.findById(assignedByUserId).orElse(null);
            if (assignedBy == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"message\":\"Assigned-by user not found\"}");
            }
        }

        BillToGroup link = new BillToGroup(group, bill, assignedBy, notes);

        BillToGroup saved = billsToGroupRepository.save(link);

        // ========== NOTIFICATION: Notify group members about new bill ==========
        if (assignedBy != null) {
            try {
                // Parse bill amount as double for notification
                double billAmount = Double.parseDouble(bill.getBill_amount());
                notificationService.notifyBillCreated(group, assignedBy, bill.getBill_name(), billAmount);
            } catch (NumberFormatException e) {
                // Log error but don't fail the bill creation
                System.err.println("Failed to parse bill amount for notification: " + e.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * GET /groupbill/{groupId}/bills
     * List all bills assigned to a group.
     */
    @GetMapping("/{groupId}/bills")
    public ResponseEntity<?> getBillsForGroup(@PathVariable Integer groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Group not found"));
        }
        Group group = groupOpt.get();

        List<BillToGroup> links = billsToGroupRepository.findByGroup(group);
        return ResponseEntity.ok(links);
    }

    /**
     * GET /groupbill/{groupId}/bills/{billId}
     * Get the BillToGroup link for a specific bill in a group.
     */
    @GetMapping("/{groupId}/bills/{billId}")
    public ResponseEntity<?> getGroupBill(
            @PathVariable Integer groupId,
            @PathVariable Integer billId
    ) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Group not found"));
        }
        Group group = groupOpt.get();

        Optional<Bill> billOpt = billRepository.findById(billId);
        if (billOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Bill not found"));
        }
        Bill bill = billOpt.get();

        Optional<BillToGroup> linkOpt = billsToGroupRepository.findByGroupAndBill(group, bill);
        if (linkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Bill is not assigned to this group"));
        }

        return ResponseEntity.ok(linkOpt.get());
    }

    /**
     * PUT /groupbill/{groupId}/bills/{billId}
     * Update notes or resolved flag for a group-bill link.
     *
     * Example body:
     * {
     *   "notes": "Paid back by everyone",
     *   "resolved": true
     * }
     */
    @PutMapping("/{groupId}/bills/{billId}")
    public ResponseEntity<?> updateGroupBill(
            @PathVariable Integer groupId,
            @PathVariable Integer billId,
            @RequestBody Map<String, Object> body
    ) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Group not found"));
        }
        Group group = groupOpt.get();

        Optional<Bill> billOpt = billRepository.findById(billId);
        if (billOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Bill not found"));
        }
        Bill bill = billOpt.get();

        Optional<BillToGroup> linkOpt = billsToGroupRepository.findByGroupAndBill(group, bill);
        if (linkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Bill is not assigned to this group"));
        }

        BillToGroup link = linkOpt.get();

        if (body.containsKey("notes")) {
            String notes = body.get("notes") != null ? body.get("notes").toString() : null;
            link.setNotes(notes);
        }

        if (body.containsKey("resolved")) {
            Object resolvedObj = body.get("resolved");
            if (resolvedObj instanceof Boolean boolVal) {
                link.setResolved(boolVal);
            }
        }

        BillToGroup updated = billsToGroupRepository.save(link);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /groupbill/{groupId}/bills/{billId}
     * Remove the bill assignment from the group.
     */
    @DeleteMapping("/{groupId}/bills/{billId}")
    public ResponseEntity<?> removeBillFromGroup(
            @PathVariable Integer groupId,
            @PathVariable Integer billId
    ) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Group not found"));
        }
        Group group = groupOpt.get();

        Optional<Bill> billOpt = billRepository.findById(billId);
        if (billOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Bill not found"));
        }
        Bill bill = billOpt.get();

        Optional<BillToGroup> linkOpt = billsToGroupRepository.findByGroupAndBill(group, bill);
        if (linkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Bill is not assigned to this group"));
        }

        billsToGroupRepository.delete(linkOpt.get());
        return ResponseEntity.noContent().build();
    }
}
