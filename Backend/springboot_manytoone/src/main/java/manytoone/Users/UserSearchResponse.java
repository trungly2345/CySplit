package manytoone.Users;

// This class is for getting data about the user for login and search (if we need to display friends) results
public class UserSearchResponse {
    private int userId;
    private String userName;
    private Double userRating;

    public UserSearchResponse(int userId, String userName, Double userRating) {
        this.userId = userId;
        this.userName = userName;
        this.userRating = userRating;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Double getUserRating() {
        return userRating;
    }
}