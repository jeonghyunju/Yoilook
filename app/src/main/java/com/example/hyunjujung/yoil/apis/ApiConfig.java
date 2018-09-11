package com.example.hyunjujung.yoil.apis;

import com.example.hyunjujung.yoil.ServerResponse;
import com.example.hyunjujung.yoil.vo.CheckPassVO;
import com.example.hyunjujung.yoil.vo.CommentList;
import com.example.hyunjujung.yoil.vo.DailyCodiVO;
import com.example.hyunjujung.yoil.vo.FavoriteDetailVO;
import com.example.hyunjujung.yoil.vo.FavoriteList;
import com.example.hyunjujung.yoil.vo.FollowCountVO;
import com.example.hyunjujung.yoil.vo.FollowingList;
import com.example.hyunjujung.yoil.vo.MyFollowerVO;
import com.example.hyunjujung.yoil.vo.MyFollowingVO;
import com.example.hyunjujung.yoil.vo.SelectVO;
import com.example.hyunjujung.yoil.vo.TimelineList;
import com.example.hyunjujung.yoil.vo.TimelineVO;
import com.example.hyunjujung.yoil.vo.WeekCodiVO;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by hyunjujung on 2017. 10. 10..
 */

public interface ApiConfig {
    @Multipart
    @POST("insert.php")
    Call<ServerResponse> saveDataBase(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("selectmembers.php")
    Call<SelectVO> selectmembers(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("checkPassword.php")
    Call<CheckPassVO> checkpassword(@Field("loginid") String loginid,
                                    @Field("loginpw") String loginpw);

    @Multipart
    @POST("insertTimeline.php")
    Call<ServerResponse> saveTimeline(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("selectTimeline.php")
    Call<List<TimelineVO>> selectTimeline(@Field("page") int page);

    @FormUrlEncoded
    @POST("selectWrite.php")
    Call<TimelineList> selectWrite(@Field("userid") String userid);

    @Multipart
    @POST("updateUser.php")
    Call<ServerResponse> updatemembers(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("insertToken.php")
    Call<ServerResponse> insertToken(@Field("userid") String userid,
                                     @Field("userToken") String userToken);

    @FormUrlEncoded
    @POST("selectFavorite.php")
    Call<FavoriteList> selectFavorite(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("insertFavorite.php")
    Call<ServerResponse> insertFavorite(@Field("userid") String userid,
                                        @Field("favoriteIdx") int favoriteIdx);

    @FormUrlEncoded
    @POST("deleteFavorite.php")
    Call<ServerResponse> deleteFavorite(@Field("userid") String userid,
                                        @Field("favoriteIdx") int favoriteIdx);

    @FormUrlEncoded
    @POST("sendfcm.php")
    Call<ServerResponse> sendfcm(@Field("writeid") String writeid,
                                 @Field("loginid") String loginid,
                                 @Field("message") String message);

    @FormUrlEncoded
    @POST("selectMyFavorite.php")
    Call<TimelineList> selectMyfavorite(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("insertComment.php")
    Call<CommentList> insertComment(@Field("loginid") String loginid,
                                    @Field("loginImg") String loginImg,
                                    @Field("commentCon") String commnetCon,
                                    @Field("commentDate") String commentDate,
                                    @Field("cGrpidx") int cGrpidx);

    @FormUrlEncoded
    @POST("selectComment.php")
    Call<CommentList> selectComment(@Field("writeidx") int wrirteid);

    @FormUrlEncoded
    @POST("insertRecomment.php")
    Call<CommentList> insertRecomment(@Field("loginid") String loginid,
                                      @Field("loginImg") String loginImg,
                                      @Field("commentCon") String commentCon,
                                      @Field("commentDate") String commentDate,
                                      @Field("cGrpidx") int cGrpidx,
                                      @Field("cGroup") int cGroup);

    @FormUrlEncoded
    @POST("selectFavoriteDetail.php")
    Call<FavoriteDetailVO> selectFavoriteDetail(@Field("favoriteIdx") int idx);

    @FormUrlEncoded
    @POST("selectOneTimeline.php")
    Call<TimelineVO> selectOnetimeline(@Field("writeidx") int idx);

    @Multipart
    @POST("updateOneTimeline.php")
    Call<ServerResponse> updateOnetimeline(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("deleteOneTimeline.php")
    Call<ServerResponse> deleteOnetimeline(@Field("deleteIdx") int deleteidx);

    @Multipart
    @POST("insertDailyCodi.php")
    Call<ServerResponse> insertDailyCodi(@PartMap Map<String, RequestBody> params,
                                         @Part List<MultipartBody.Part> files);

    @FormUrlEncoded
    @POST("selectDailyCodi.php")
    Call<DailyCodiVO> selectDailycodi(@Field("ymd") String ymd);

    @Multipart
    @POST("updateDailyCodi.php")
    Call<ServerResponse> updateDailyCodi1(@PartMap Map<String, RequestBody> params,
                                          @Part List<MultipartBody.Part> files);

    @Multipart
    @POST("updateDailyCodi.php")
    Call<ServerResponse> updateDailyCodi2(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("selectWeekCodi.php")
    Call<List<WeekCodiVO>> selectWeekcodi(@Field("ymd") String ymd);

    @FormUrlEncoded
    @POST("selectFriendList.php")
    Call<List<SelectVO>> selectFriend(@Field("loginid") String loginid);

    @Multipart
    @POST("updateChatImage.php")
    Call<ServerResponse> updateChatImage(@Part MultipartBody.Part images);

    @FormUrlEncoded
    @POST("selectFollowing.php")
    Call<FollowingList> selectFollowlist(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("insertFollows.php")
    Call<ServerResponse> insertFollows(@Field("loginid") String loginid,
                                       @Field("loginprofile") String loginprofile,
                                       @Field("loginname") String loginname,
                                       @Field("writeid") String writeid,
                                       @Field("writeprofile") String writeprofile,
                                       @Field("writename") String writename);

    @FormUrlEncoded
    @POST("deleteFollows.php")
    Call<ServerResponse> deleteFollows(@Field("loginid") String loginid,
                                       @Field("writeid") String writeid);

    @FormUrlEncoded
    @POST("selectFollowerList.php")
    Call<List<MyFollowerVO>> selectFollower(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("selectCountFollow.php")
    Call<FollowCountVO> selectcountFollow(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("selectFollowingList.php")
    Call<List<MyFollowingVO>> selectFollowings(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("deleteFollowerList.php")
    Call<ServerResponse> deleteFollowerList(@Field("userid") String userid,
                                            @Field("followerid") String followerId);
}
