package com.example.pras.enursing;

/**
 * Created by pras on 12/10/17.
 */

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "https://api-e-nursing.herokuapp.com/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
