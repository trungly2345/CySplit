package manytoone.Bills;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import manytoone.BillsToGroup.BillToGroup;
import manytoone.BillsToGroup.BillsToGroupRepository;
import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;
import manytoone.Groups.UserGroup;
import manytoone.Groups.UserGroupRepository;
import manytoone.Notifications.NotificationService;
import manytoone.Users.User;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BillsToGroupRepository billsToGroupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private GroupRepository groupRepository;


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

        // ========== NOTIFICATION: Notify group members about bill update ==========
        // Find all groups this bill is assigned to
        List<BillToGroup> groupLinks = billsToGroupRepository.findByBill(updateBill);
        for (BillToGroup link : groupLinks) {
            Group group = link.getGroup();
            User updatedBy = link.getAssignedBy(); // Use the person who assigned the bill
            
            if (group != null) {
                // Notify all group members except the person who updated it
                List<UserGroup> members = userGroupRepository.findByGroupId(group.getId());
                for (UserGroup member : members) {
                    if (updatedBy == null || member.getUser().getId() != updatedBy.getId()) {
                        notificationService.notifyBillUpdated(
                            member.getUser(),
                            group,
                            updatedBy != null ? updatedBy : member.getUser(), // Fallback to member if updatedBy is null
                            updateBill.getBill_name()
                        );
                    }
                }
                
                // ========== AUTO-DELETE: Check if temporary group should be deleted ==========
                // TODO: Enable this when receipt interface is ready
                // if (updateBill.isPaid() && group.isTemporary()) {
                //     checkAndDeleteTemporaryGroup(group);
                // }
            }
        }

        return ResponseEntity.ok(updateBill);
    }

    // DELETE /bill/{bill_id}
    @DeleteMapping("/{bill_id}")
    public ResponseEntity<Void> deleteBillById(@PathVariable int bill_id) {
        if (!billRepository.existsById(bill_id)) {
            return ResponseEntity.notFound().build();
        }
        
        // ========== NOTIFICATION: Get bill info before deleting ==========
        Bill bill = billRepository.findById(bill_id).get();
        String billName = bill.getBill_name();
        
        // Find all groups this bill was assigned to
        List<BillToGroup> groupLinks = billsToGroupRepository.findByBill(bill);
        
        // Delete the bill
        billRepository.deleteById(bill_id);
        
        // Send notifications after successful deletion
        for (BillToGroup link : groupLinks) {
            Group group = link.getGroup();
            User deletedBy = link.getAssignedBy();
            
            if (group != null && deletedBy != null) {
                notificationService.notifyBillDeleted(group, deletedBy, billName);
            }
        }
        
        return ResponseEntity.noContent().build();
    }

    // ========== HELPER METHOD: Check and delete temporary group if all bills are paid ==========
    private void checkAndDeleteTemporaryGroup(Group group) {
        // Get all bills for this group
        List<BillToGroup> allGroupBills = billsToGroupRepository.findByGroup(group);
        
        // Check if all bills are paid
        boolean allPaid = true;
        for (BillToGroup link : allGroupBills) {
            if (!link.getBill().isPaid()) {
                allPaid = false;
                break;
            }
        }
        
        // If all bills are paid, delete the group
        if (allPaid) {
            System.out.println("All bills paid for temporary group '" + group.getGroup_name() 
                + "' (ID: " + group.getId() + "). Auto-deleting group...");
            groupRepository.deleteById(group.getId());
            System.out.println("Temporary group '" + group.getGroup_name() + "' deleted successfully.");
        }
    }
}