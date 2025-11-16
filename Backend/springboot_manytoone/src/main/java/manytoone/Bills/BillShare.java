package manytoone.Bills;

import jakarta.persistence.*;
import manytoone.BillsToGroup.BillToGroup;
import manytoone.Users.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bill_shares")
public class BillShare {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;


        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "group_bill_id")
        private BillToGroup groupBill;


        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id")
        private User user;

        @Column(name = "amount_owed", nullable = false)
        private String amountOwed; // or BigDecimal, better for money

        @Column(name = "paid", nullable = false)
        private boolean paid = false;

        @Column(name = "paid_at")
        private LocalDateTime paidAt;

        protected BillShare() {}

        public BillShare(BillToGroup groupBill, User user, String amountOwed) {
            this.groupBill = groupBill;
            this.user = user;
            this.amountOwed = amountOwed;
        }


    public BillToGroup getGroupBill() {
        return groupBill;
    }

    public void setGroupBill(BillToGroup groupBill) {
        this.groupBill = groupBill;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(String amountOwed) {
        this.amountOwed = amountOwed;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
