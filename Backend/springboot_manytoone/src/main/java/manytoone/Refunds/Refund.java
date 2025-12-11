package manytoone.Refunds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import manytoone.Bills.Bill;
import manytoone.Users.User;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "refunds")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id", nullable = false)
    private int refund_id;
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonIgnore
    private Bill bill;
    @Column(name = "refund_name", nullable = false)
    private String refund_name;
    @Column(name = "refund_amount", nullable = false)
    private String refund_amount;

    @ManyToOne(optional = true)
    @JoinColumn(name = "refunded_to", nullable = false)
    private User refundedTo;

    @Column(name = "refund_date", nullable = false)
    private LocalDateTime refundDate;

    public Refund() {this.refundDate = LocalDateTime.now();}


    public int getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(int refund_id) {
        this.refund_id = refund_id;
    }

    public Bill getBill(){
        return bill;
    }

    public void setBill(Bill bill){
        this.bill = bill;
    }

    public int getBillId(){
        return (bill != null) ? bill.getBillId() : 0;
    }

    public String getRefund_name() {
        return refund_name;
    }

    public void setRefund_name(String refund_name) {
        this.refund_name = refund_name;
    }

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }

    public User getRefundedTo() {
        return refundedTo;
    }

    public void setRefundedTo(User refundedTo) {
        this.refundedTo = refundedTo;
    }
}
