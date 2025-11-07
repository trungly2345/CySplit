package manytoone.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "user", schema = "DB309")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "user_rating")
    private Double userRating;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "if_active", nullable = false)
    private boolean ifActive;

    @Column(name = "name")
    private String name;

    // Keep it for now
    @Column(name = "laptop_id", unique = true)
    private Integer laptopId;

    @JsonIgnore
    protected User() {}  // Required no-args constructor

    public User(String userName, String userPassword, String phoneNumber, String paymentMethod) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.phoneNumber = phoneNumber;
        this.paymentMethod = paymentMethod;
        this.userRating = 0.0;  // Default rating for new users
        this.ifActive = true;   // New users are active by default
        this.emailId = null;    // Can be set later
        this.name = userName;   // Default name to username
        // laptopId is automatically null becasue we do not need it, I may change it to an array of GroupIds later
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getUserRating() {
        return userRating;
    }

    public void setUserRating(Double userRating) {
        this.userRating = userRating;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public boolean isIfActive() {
        return ifActive;
    }

    public void setIfActive(boolean ifActive) {
        this.ifActive = ifActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLaptopId() {
        return laptopId;
    }

    public void setLaptopId(Integer laptopId) {
        this.laptopId = laptopId;
    }
}