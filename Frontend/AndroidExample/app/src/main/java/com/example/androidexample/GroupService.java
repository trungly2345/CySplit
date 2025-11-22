package com.example.androidexample;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit service interface for fetching group-related data from a remote server.
 * <p>
 * Defines API endpoints for retrieving a single group by ID or all groups associated with a user.
 * </p>
 */
public interface GroupService {

    /**
     * Retrieves a group by its unique ID.
     *
     * @param groupId the ID of the group to fetch
     * @return a {@link Call} object for asynchronous execution that returns a {@link Group}
     */
    @GET("groups/{group_id}")
    Call<Group> getGroupById(@Path("group_id") int groupId);

    /**
     * Retrieves all groups associated with a specific user.
     *
     * @param username the username of the user whose groups are being fetched
     * @return a {@link Call} object for asynchronous execution that returns a {@link Group}
     */
    @GET("{user}/groups")
    Call<Group> getUserGroups(@Path("user") String username);
}