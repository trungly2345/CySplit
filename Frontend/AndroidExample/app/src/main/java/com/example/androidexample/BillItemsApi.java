package com.example.androidexample;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface BillItemsApi {

    @GET("billitems/group/{groupId}")
    Call<List<BillItemDto>> getAll(@Path("groupId") int groupId);

    @POST("billitems/group/{groupId}")
    Call<BillItem> createItem(@Path("groupId") int groupId, @Body BillItem item);

    @PUT("billitems/{itemId}/pay")
    Call<BillItem> markPaid(@Path("itemId") int id);


    @DELETE("billitems/{itemId}")
    Call<Void> deleteItem(@Path("itemId") int id);
}
