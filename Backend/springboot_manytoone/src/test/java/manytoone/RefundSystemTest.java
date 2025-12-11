package manytoone;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import manytoone.Bills.Bill;
import manytoone.Bills.BillRepository;
import manytoone.Refunds.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        classes = Main.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class RefundSystemTest {

    @LocalServerPort
    int port;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private RefundRepository refundRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    /**
     * System test:
     * 1. Create a Bill in the DB
     * 2. POST /bills/{billId}/refunds to create a refund
     * 3. GET /bills/{billId}/refunds and verify the refund is returned
     */
    @Test
    void createRefund_andListRefundsForBill_success() {
        // ---------- Arrange: create a Bill ----------
        Bill bill = new Bill();
        // TODO: adjust these setters to match your actual Bill entity

        bill.setBill_name("Test Bill For Refund");
        bill.setBill_amount("42.50");

        // If your Bill has dueTime / dueCreated non-nullable, set them:
        bill.setDueTime(LocalDateTime.now().plusDays(7));
        bill.setDueCreated(LocalDateTime.now());

        bill = billRepository.save(bill);
        int billId = bill.getBill_id();  // adjust getter name if different

        // ---------- Arrange: payload for Refund ----------
        Map<String, Object> payload = new HashMap<>();
        payload.put("refund_name", "Overcharged correction");
        payload.put("refund_amount", "10.00");

        // ---------- Act + Assert: POST /bills/{billId}/refunds ----------
        int refundId =
                given()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post("/bills/{billId}/refunds", billId)
                        .then()
                        .statusCode(201)
                        .body("refund_name", equalTo("Overcharged correction"))
                        .body("refund_amount", equalTo("10.00"))
                        .extract()
                        .path("refund_id"); // adjust if your field name is different

        // ---------- Assert: GET /bills/{billId}/refunds returns the refund ----------
        given()
                .when()
                .get("/bills/{billId}/refunds", billId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].refund_id", equalTo(refundId))
                .body("[0].refund_name", equalTo("Overcharged correction"))
                .body("[0].refund_amount", equalTo("10.00"));
    }

    @Test
    // testing testing testing testing 
    void updateAndDeleteRefund_success() {
        // ----- Arrange: Create a Bill -----
        Bill bill = new Bill();
        bill.setBill_name("Bill For Update/Delete");
        bill.setBill_amount("30.00");
        bill.setDueTime(LocalDateTime.now().plusDays(3));
        bill.setDueCreated(LocalDateTime.now());
        bill = billRepository.save(bill);
        int billId = bill.getBill_id();


        Map<String, Object> payload = new HashMap<>();
        payload.put("refund_name", "Initial Refund");
        payload.put("refund_amount", "5.00");

        int refundId =
                given()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post("/bills/{billId}/refunds", billId)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("refund_id");


        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("refund_name", "Updated Refund");
        updatePayload.put("refund_amount", "7.00");

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when()
                .put("/bills/{billId}/refunds/{refundId}", billId, refundId)
                .then()
                .statusCode(200)
                .body("refund_name", equalTo("Updated Refund"))
                .body("refund_amount", equalTo("7.00"));


        given()
                .when()
                .get("/bills/{billId}/refunds/{refundId}", billId, refundId)
                .then()
                .statusCode(200)
                .body("refund_name", equalTo("Updated Refund"))
                .body("refund_amount", equalTo("7.00"));

        given()
                .when()
                .delete("/bills/{billId}/refunds/{refundId}", billId, refundId)
                .then()
                .statusCode(204);


        given()
                .when()
                .get("/bills/{billId}/refunds/{refundId}", billId, refundId)
                .then()
                .statusCode(404);
    }
}