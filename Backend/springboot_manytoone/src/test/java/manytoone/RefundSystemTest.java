package manytoone;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import manytoone.Bills.Bill;
import manytoone.Bills.BillRepository;
import manytoone.Refunds.RefundRepository;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }


    @Test
    void createRefund_andListRefundsForBill_success() {

        Bill bill = new Bill();
        bill.setBill_name("Test Bill For Refund");
        bill.setBill_amount("42.50");
        bill.setDueTime(LocalDateTime.now().plusDays(7));
        bill.setDueCreated(LocalDateTime.now());

        bill = billRepository.save(bill);
        int billId = bill.getBillId();

        User refundedUser = new User(
                "refundUser1",
                "password123",
                "515-000-0000",
                "venmo"
        );
        refundedUser = userRepository.save(refundedUser);


        Map<String, Object> payload = new HashMap<>();
        payload.put("refund_name", "Overcharged correction");
        payload.put("refund_amount", "10.00");

        Map<String, Object> refundedTo = new HashMap<>();
        refundedTo.put("id", refundedUser.getId());
        payload.put("refundedTo", refundedTo);


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
                        .body("refundedTo.id", equalTo(refundedUser.getId()))
                        .extract()
                        .path("refund_id");


        given()
                .when()
                .get("/bills/{billId}/refunds", billId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].refund_id", equalTo(refundId))
                .body("[0].refund_name", equalTo("Overcharged correction"))
                .body("[0].refund_amount", equalTo("10.00"))
                .body("[0].refundedTo.id", equalTo(refundedUser.getId()));
    }

    @Test
    void updateAndDeleteRefund_success() {

        Bill bill = new Bill();
        bill.setBill_name("Bill For Update/Delete");
        bill.setBill_amount("30.00");
        bill.setDueTime(LocalDateTime.now().plusDays(3));
        bill.setDueCreated(LocalDateTime.now());
        bill = billRepository.save(bill);
        int billId = bill.getBillId();


        User refundedUser = new User(
                "refundUser2",
                "password123",
                "515-111-1111",
                "cash"
        );
        refundedUser = userRepository.save(refundedUser);


        Map<String, Object> payload = new HashMap<>();
        payload.put("refund_name", "Initial Refund");
        payload.put("refund_amount", "5.00");

        Map<String, Object> refundedTo = new HashMap<>();
        refundedTo.put("id", refundedUser.getId());
        payload.put("refundedTo", refundedTo);

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