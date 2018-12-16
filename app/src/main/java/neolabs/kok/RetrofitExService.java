package neolabs.kok;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitExService {

    @GET("user/signup")
    Call<Data> signupUserInfo(@Query("email") String email, @Query("password") String password, @Query("gender") String gender, @Query("nickname") String nickname, @Query("introduce") String introduce);

    @GET("user/signin")
    Call<Data> signinUserInfo(@Query("email") String email, @Query("password") String password);

    @GET("addpick")
    Call<Data> addPick(@Query("latitude") String latitude, @Query("longitude") String longitude, @Query("userauthid") String userauthid, @Query("message") String message);

    @GET("getpicknearby")
    Call<Data> getPick(@Query("latitude") String latitude, @Query("longitude") String longitude);

    //출처: http://falinrush.tistory.com/5 [형필 개발일지]
}