package manytoone.BillsToGroup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import manytoone.Bills.Bill;
import manytoone.Groups.Group;
import manytoone.Users.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_bills")
public class BillToGroup {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_bill_id")
    private Integer groupBillId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "group_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_groupbill_group")
    )
    @JsonIgnore
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "bill_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_groupbill_bill")
    )
    @JsonIgnore
    private Bill bill;

    // When this bill was linked to the group
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "assigned_by",
            foreignKey = @ForeignKey(name = "fk_groupbill_user")
    )
    @JsonIgnore
    private User assignedBy;

    @Column(name = "resolved", nullable = false)
    private boolean resolved = false;


    @Column(name = "notes")
    private String notes;

    protected BillToGroup() {}

    public BillToGroup(Group group, Bill bill, User assignedBy,String notes) {
        this.group = group;
        this.bill = bill;
        this.assignedBy = assignedBy;
        this.notes = notes;
    }


    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}