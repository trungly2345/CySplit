package com.example.androidexample;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GroupService {
    @GET("groups/{group_id}")
    Call<Group> getGroupById(@Path("group_id") int groupId);
}