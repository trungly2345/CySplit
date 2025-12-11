package manytoone.Bills;

import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/billitems")
public class BillItemController {

    @Autowired
    private BillItemRepository billItemRepository;

    @Autowired
    private GroupRepository groupRepository;


    // ========== GET ALL ITEMS FOR A GROUP ==========
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<BillItem>> getItemsForGroup(@PathVariable int groupId) {
        List<BillItem> items = billItemRepository.findByGroupId(groupId);
        return ResponseEntity.ok(items);
    }


    // ========== GET UNPAID ITEMS FOR A GROUP ==========
    @GetMapping("/group/{groupId}/unpaid")
    public ResponseEntity<List<BillItem>> getUnpaidItemsForGroup(@PathVariable int groupId) {
        List<BillItem> items = billItemRepository.findUnpaidItemsByGroupId(groupId);
        return ResponseEntity.ok(items);
    }


    // ========== GET PAID ITEMS FOR A GROUP ==========
    @GetMapping("/group/{groupId}/paid")
    public ResponseEntity<List<BillItem>> getPaidItemsForGroup(@PathVariable int groupId) {
        List<BillItem> items = billItemRepository.findPaidItemsByGroupId(groupId);
        return ResponseEntity.ok(items);
    }


    // ========== GET SINGLE ITEM BY ID ==========
    @GetMapping("/{itemId}")
    public ResponseEntity<BillItem> getItemById(@PathVariable int itemId) {
        Optional<BillItem> item = billItemRepository.findById(itemId);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // ========== CREATE NEW ITEM ==========
    @PostMapping("/group/{groupId}")
    public ResponseEntity<?> createItem(
            @PathVariable int groupId,
            @RequestBody Map<String, Object> body) {
        
        // Validate required fields
        if (!body.containsKey("itemName") || !body.containsKey("itemPrice")) {
            return ResponseEntity.badRequest()
                    .body("{\"message\":\"itemName and itemPrice are required\"}");
        }

        // Find group
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\":\"Group not found\"}");
        }

        // Extract fields
        String itemName = body.get("itemName").toString();
        String itemPrice = body.get("itemPrice").toString();
        int quantity = body.containsKey("quantity") 
                ? Integer.parseInt(body.get("quantity").toString()) 
                : 1;
        String notes = body.containsKey("notes") ? body.get("notes").toString() : null;

        // Create item
        BillItem item = new BillItem(group, itemName, itemPrice, quantity);
        item.setNotes(notes);

        BillItem savedItem = billItemRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }


    // ========== UPDATE ITEM ==========
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable int itemId,
            @RequestBody Map<String, Object> body) {
        
        Optional<BillItem> itemOpt = billItemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BillItem item = itemOpt.get();

        // Update fields if provided
        if (body.containsKey("itemName")) {
            item.setItemName(body.get("itemName").toString());
        }
        if (body.containsKey("itemPrice")) {
            item.setItemPrice(body.get("itemPrice").toString());
        }
        if (body.containsKey("quantity")) {
            item.setQuantity(Integer.parseInt(body.get("quantity").toString()));
        }
        if (body.containsKey("isPaid")) {
            item.setPaid(Boolean.parseBoolean(body.get("isPaid").toString()));
        }
        if (body.containsKey("notes")) {
            item.setNotes(body.get("notes").toString());
        }

        BillItem updatedItem = billItemRepository.save(item);
        return ResponseEntity.ok(updatedItem);
    }


    // ========== MARK ITEM AS PAID ==========
    @PutMapping("/{itemId}/pay")
    public ResponseEntity<BillItem> markItemAsPaid(@PathVariable int itemId) {
        Optional<BillItem> itemOpt = billItemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BillItem item = itemOpt.get();
        item.setPaid(true);
        BillItem updatedItem = billItemRepository.save(item);
        
        return ResponseEntity.ok(updatedItem);
    }


    // ========== DELETE ITEM ==========
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable int itemId) {
        if (!billItemRepository.existsById(itemId)) {
            return ResponseEntity.notFound().build();
        }

        billItemRepository.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }


    // ========== GET TOTAL AMOUNT FOR GROUP (UNPAID ITEMS) ==========
    @GetMapping("/group/{groupId}/total")
    public ResponseEntity<Map<String, Object>> getTotalForGroup(@PathVariable int groupId) {
        List<BillItem> unpaidItems = billItemRepository.findUnpaidItemsByGroupId(groupId);
        
        double total = 0.0;
        for (BillItem item : unpaidItems) {
            try {
                double price = Double.parseDouble(item.getItemPrice());
                total += price * item.getQuantity();
            } catch (NumberFormatException e) {
                // Skip invalid prices
            }
        }

        return ResponseEntity.ok(Map.of(
            "groupId", groupId,
            "unpaidItemsCount", unpaidItems.size(),
            "totalAmount", total
        ));
    }
}
