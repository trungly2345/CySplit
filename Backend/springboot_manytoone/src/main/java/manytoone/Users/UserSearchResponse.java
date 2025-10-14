package manytoone.Users;

// This class represents a limited view of user data for search results
// to avoid exposing sensitive information
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