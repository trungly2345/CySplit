package manytoone.Payments;

import jakarta.persistence.*;
import manytoone.Bills.Bill;
import manytoone.Groups.Group;
import manytoone.Users.User;

import java.time.LocalDateTime;

/**
 * #### Payments (Main Feature)
 * - [ ] Create `Payment` entity (fields: amount, date, note, bill, payer)
 * - [ ] Create `PaymentRepository`
 * - [ ] Implement `PaymentController`:
 *   - [ ] `POST /bills/{billId}/payments`
 *   - [ ] `GET /bills/{billId}/payments`
 * - [ ] Add system tests for Payment endpoints
 * - [ ] Add Payment requests to Postman collection
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @Column(nullable = false)
    private String amount;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    private String note;



    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @ManyToOne
    @JoinColumn(name = "payee_id")
    private User payee; // bill creator

    @ManyToOne
    @JoinColumn(name = "payer_id")
    private User payer; // bill payer

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;




    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return paidAt;
    }

    public void setDate(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public User getPayee() {
        return payee;
    }

    public void setPayee(User payee) {
        this.payee = payee;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }
}
