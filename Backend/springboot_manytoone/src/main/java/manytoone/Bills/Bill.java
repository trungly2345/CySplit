package manytoone.Bills;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

//Bills -> bill (bill_id, bill_amount, dueDate, date_created,paid)
    @Entity
    @Table(name = "bills")
    public class Bill {
        // Testing for pipeline test, please delete later
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "bill_id" , nullable = false)
        private int bill_id; // Primary key
        @Column(name = "bill_name" , nullable = false)
        private String bill_name;
        @Column(name = "bill_amount" , nullable = false)
        private String bill_amount;
        @Column(name = "dueDate", nullable = false)
        private LocalDateTime dueTime;
        @Column(name = "due_created", nullable = false)
        private LocalDateTime dueCreated;
        @Column(name = "paid", nullable = false)
        private boolean paid;



        public Bill() {
            this.paid = false;
        }              


    public Bill(int bill_id, String bill_name, String bill_amount, LocalDateTime dueTime, LocalDateTime dueCreated, boolean paid){
                this.bill_id = bill_id;
                this.bill_name = bill_name;
                this.bill_amount = bill_amount;
                this.dueTime = dueTime;
                this.dueCreated = dueCreated;
                this.paid = paid;
        }

    public int getBillId() {
        return bill_id;
    }

    public void setBillId(int bill_id) {
        this.bill_id = bill_id;
    }

    public String getBill_name() {
        return bill_name;
    }

    public void setBill_name(String bill_name) {
        this.bill_name = bill_name;
    }

    public String getBill_amount() {
        return bill_amount;
    }

    public void setBill_amount(String bill_amount) {
        this.bill_amount = bill_amount;
    }

    public LocalDateTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalDateTime dueTime) {
        this.dueTime = dueTime;
    }

    public LocalDateTime getDueCreated() {
        return dueCreated;
    }

    public void setDueCreated(LocalDateTime dueCreated) {
        this.dueCreated = dueCreated;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

}
