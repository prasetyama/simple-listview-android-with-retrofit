package com.example.pras.enursing;

import com.example.pras.enursing.Message;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
/**
 * Created by pras on 12/10/17.
 */

public interface APIService {

    @POST("/posts")
    @FormUrlEncoded
    Call<Message> savePost(@Field("title") String title,
                        @Field("message") String body,
                        @Field("image") String image);
}
