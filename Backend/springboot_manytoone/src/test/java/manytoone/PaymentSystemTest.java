package manytoone;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import manytoone.Bills.Bill;
import manytoone.Bills.BillRepository;
import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;
import manytoone.Payments.Payment;
import manytoone.Payments.PaymentRepository;
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
public class PaymentSystemTest {

    @LocalServerPort
    int port;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }


    private Bill createTestBill(String name, String amount) {
        Bill b = new Bill();
        b.setBill_name(name);
        b.setBill_amount(amount);
        b.setDueTime(LocalDateTime.now().plusDays(3));
        b.setDueCreated(LocalDateTime.now());
        b.setPaid(false);
        return billRepository.save(b);
    }


    private Group createTestGroup(String name, int capacity) {
        Group g = new Group();
        g.setGroup_name(name);
        g.setCapacity(capacity);

        return groupRepository.save(g);
    }


    private User createTestUser(String username) {
        User u = new User(
                username,
                "password123",
                "515-000-0000",
                "venmo"
        );
        return userRepository.save(u);
    }


    @Test
    void createPayment_andListPaymentsForBill_success() {

        Bill bill = createTestBill("Payment Bill #1", "56.75");
        int billId = bill.getBillId();

        Group group = createTestGroup("Alpha Group", 5);
        int groupId = group.getId();

        User payer = createTestUser("payerUser1");
        User payee = createTestUser("payeeUser1");


        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", "30.00");
        payload.put("note", "Demo payment from payerUser1 to payeeUser1");

        Map<String, Object> payerJson = new HashMap<>();
        payerJson.put("id", payer.getId());
        payload.put("payer", payerJson);

        Map<String, Object> payeeJson = new HashMap<>();
        payeeJson.put("id", payee.getId());
        payload.put("payee", payeeJson);

        int paymentId =
                given()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post("/bills/{billId}/payments/{groupId}", billId, groupId)
                        .then()
                        .statusCode(201)
                        .body("amount", equalTo("30.00"))
                        .body("note", containsString("Demo payment"))
                        .body("bill.billId", equalTo(billId))
                        .body("group.id", equalTo(groupId))
                        .body("payer.id", equalTo(payer.getId()))
                        .body("payee.id", equalTo(payee.getId()))
                        .extract()
                        .path("paymentId");


        given()
                .when()
                .get("/bills/{billId}/payments", billId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].paymentId", equalTo(paymentId))
                .body("[0].amount", equalTo("30.00"))
                .body("[0].payer.id", equalTo(payer.getId()))
                .body("[0].payee.id", equalTo(payee.getId()));
    }


    @Test
    void getPaymentById_existingId_returns200() {

        Bill bill = createTestBill("Single Payment Bill", "100.00");
        int billId = bill.getBillId();

        Group group = createTestGroup("Beta Group", 4);
        int groupId = group.getId();

        User payer = createTestUser("payerUser2");
        User payee = createTestUser("payeeUser2");

        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", "40.00");
        payload.put("note", "Payment for entrée");

        Map<String, Object> payerJson = new HashMap<>();
        payerJson.put("id", payer.getId());
        payload.put("payer", payerJson);

        Map<String, Object> payeeJson = new HashMap<>();
        payeeJson.put("id", payee.getId());
        payload.put("payee", payeeJson);

        int paymentId =
                given()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post("/bills/{billId}/payments/{groupId}", billId, groupId)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("paymentId");


        given()
                .when()
                .get("/bills/{billId}/payments/{paymentId}", billId, paymentId)
                .then()
                .statusCode(200)
                .body("paymentId", equalTo(paymentId))
                .body("amount", equalTo("40.00"))
                .body("bill.billId", equalTo(billId));
    }


    @Test
    void getPaymentById_nonExistingId_returns404() {
        Bill bill = createTestBill("Bill With No Payments", "50.00");
        int billId = bill.getBillId();

        given()
                .when()
                .get("/bills/{billId}/payments/{paymentId}", billId, 999999)
                .then()
                .statusCode(404);
    }


    @Test
    void deletePayment_existingId_returns204() {
        Bill bill = createTestBill("Bill For Delete Payment", "75.00");
        int billId = bill.getBillId();

        Group group = createTestGroup("Gamma Group", 3);
        int groupId = group.getId();

        User payer = createTestUser("payerUser3");

        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", "20.00");
        payload.put("note", "Delete-me payment");

        Map<String, Object> payerJson = new HashMap<>();
        payerJson.put("id", payer.getId());
        payload.put("payer", payerJson);

        int paymentId =
                given()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post("/bills/{billId}/payments/{groupId}", billId, groupId)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("paymentId");

        given()
                .when()
                .delete("/bills/{billId}/payments/{paymentId}", billId, paymentId)
                .then()
                .statusCode(204);

        // Verify it is truly gone
        given()
                .when()
                .get("/bills/{billId}/payments/{paymentId}", billId, paymentId)
                .then()
                .statusCode(404);
    }


    @Test
    void deletePayment_nonExistingId_returns404() {
        Bill bill = createTestBill("Bill For 404 Delete", "60.00");
        int billId = bill.getBillId();

        given()
                .when()
                .delete("/bills/{billId}/payments/{paymentId}", billId, 999999)
                .then()
                .statusCode(404);
    }
}