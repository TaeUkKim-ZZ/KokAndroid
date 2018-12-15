package neolabs.kok;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RetrofitExService {

    @GET("user/signup")
    Call<Data> signupUserInfo(@Query("email") String email, @Query("password") String password, @Query("gender") String gender, @Query("nickname") String nickname, @Query("introduce") String introduce);

    @GET("user/signin")
    Call<Data> signinUserInfo(@Query("email") String email, @Query("password") String password);

    //출처: http://falinrush.tistory.com/5 [형필 개발일지]
}