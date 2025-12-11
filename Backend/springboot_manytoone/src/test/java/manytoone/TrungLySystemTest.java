package manytoone;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import manytoone.Bills.Bill;
import manytoone.Bills.BillRepository;
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
public class TrungLySystemTest {

    @LocalServerPort
    int port;

    @Autowired
    private BillRepository billRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    // POST /bill - create bill
    @Test
    void createBill_success_returns201AndPersists() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bill_name", "Test bill #1");
        payload.put("bill_amount", "2000");
        payload.put("dueTime", "2025-12-07T12:14:00");
        payload.put("dueCreated", "2025-12-04T01:00:00");
        payload.put("paid", false);

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/bill")
                .then()
                .statusCode(201)
                .body("bill_id", notNullValue())
                .body("bill_name", equalTo("Test bill #1"))
                .body("bill_amount", equalTo("2000"))
                .body("paid", equalTo(false));
    }

    // GET /bill/{id} - success
    @Test
    void getBillById_existingId_returns200() {
        Bill b = new Bill();
        b.setBill_name("Single bill");
        b.setBill_amount("5678");
        b.setDueTime(LocalDateTime.of(2025, 12, 7, 12, 0));
        b.setDueCreated(LocalDateTime.of(2025, 12, 4, 1, 0));
        b.setPaid(false);
        Bill saved = billRepository.save(b);

        given()
                .when()
                .get("/bill/" + saved.getBillId())
                .then()
                .statusCode(200)
                .body("bill_id", equalTo(saved.getBillId()))
                .body("bill_name", equalTo("Single bill"));
    }

    // GET /bill/{id} - not found
    @Test
    void getBillById_nonExistingId_returns404() {
        given()
                .when()
                .get("/bill/999999")
                .then()
                .statusCode(404);
    }


    @Test
    void updateBill_existingId_returns200WithUpdatedBody() {
        Bill b = new Bill();
        b.setBill_name("Old name");
        b.setBill_amount("1000");
        b.setDueTime(LocalDateTime.of(2025, 12, 7, 12, 0));
        b.setDueCreated(LocalDateTime.of(2025, 12, 4, 1, 0));
        b.setPaid(false);
        Bill saved = billRepository.save(b);

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("bill_name", "New name");
        updatePayload.put("bill_amount", "2000");
        updatePayload.put("dueTime", "2025-12-08T12:14:00");
        updatePayload.put("dueCreated", "2025-12-05T01:00:00");
        updatePayload.put("paid", true);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when()
                .put("/bill/" + saved.getBillId())
                .then()
                .statusCode(200)
                .body("bill_name", equalTo("New name"))
                .body("bill_amount", equalTo("2000"))
                .body("paid", equalTo(true));
    }


    @Test
    void updateBill_nonExistingId_returns404() {
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("bill_name", "Does not matter");
        updatePayload.put("bill_amount", "2000");
        updatePayload.put("dueTime", "2025-12-08T12:14:00");
        updatePayload.put("dueCreated", "2025-12-05T01:00:00");
        updatePayload.put("paid", true);

        given()
                .contentType(ContentType.JSON)
                .body(updatePayload)
                .when()
                .put("/bill/999999")
                .then()
                .statusCode(404);
    }

    // DELETE /bill/{id} - success
    @Test
    void deleteBill_existingId_returns204() {
        Bill b = new Bill();
        b.setBill_name("Delete me");
        b.setBill_amount("999");
        b.setDueTime(LocalDateTime.of(2025, 12, 7, 12, 0));
        b.setDueCreated(LocalDateTime.of(2025, 12, 4, 1, 0));
        b.setPaid(false);
        Bill saved = billRepository.save(b);

        given()
                .when()
                .delete("/bill/" + saved.getBillId())
                .then()
                .statusCode(204);
    }

    // DELETE /bill/{id} - not found
    @Test
    void deleteBill_nonExistingId_returns404() {
        given()
                .when()
                .delete("/bill/999999")
                .then()
                .statusCode(404);
    }
}