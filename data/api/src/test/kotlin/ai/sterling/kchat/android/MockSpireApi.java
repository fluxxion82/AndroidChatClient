package ai.sterling.kchat.android;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import ai.sterling.kchat.android.src.BehaviorDelegate;
import io.spire.android.sharedvo.SpireDeviceEvent;
import io.spire.android.sharedvo.api.Boost;
import io.spire.android.sharedvo.api.DevicePack;
import io.spire.android.sharedvo.api.FileBucketUrl;
import io.spire.android.sharedvo.api.LocationData;
import io.spire.android.sharedvo.api.RollupData;
import io.spire.android.sharedvo.api.SpireUser;
import io.spire.android.sharedvo.api.StreakAddon;
import io.spire.android.sharedvo.api.StreakData;
import io.spire.android.sharedvo.api.UpgradePlatform;
import io.spire.android.sharedvo.api.User;
import io.spire.android.sharedvo.api.UserSetting;
import io.spire.android.sharedvo.notifications.NotificationConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MockSpireApi implements SpireApi {
    public static final String DEBUG_URL = "https://app-sandbox.spire.io/";
    public static final String DEBUG_GRAPHQL_URL = "https://data-sandbox.spire.io/v1/graphql/";

    public static final String DEBUG_APP_AT_SPIRE = "https://app-sandbox.spire.io/";
    public static final String DEBUG_API_AT_SPIRE = "https://api-sandbox.spire.io/";
    public static final String DEBUG_DATA_AT_SPIRE = "https://data-sandbox.spire.io/";

    private final BehaviorDelegate<SpireApi> delegate;
    private final List<Boost> boostList;

    public MockSpireApi(BehaviorDelegate<SpireApi> delegate) {
        this.delegate = delegate;

        boostList = new ArrayList<>();

        // Seed some mock data.
        addBoosts("BoostTemplatePack", "Revitalize", "A new boost each week, with teachers from MindBodyGreens Revitalize 2015.", "", "", false, 0);
        addBoosts("BoostTemplate", "Day 1 – The Slow Breath", "An overview of Spire, your breath-mind connection, and Deep vs. Slow breaths.", "", "https://s3-us-west-2.amazonaws.com/sandbox.spire.io/spirebin/boosts/89/Day%201.mp3", false, 545);
        addBoosts("BoostTemplate", "Day 2 – Streaks and Box Breathing", "Streaks on Spire, state of mind, then practice Box Breathing.", "", "https://s3-us-west-2.amazonaws.com/sandbox.spire.io/spirebin/boosts/90/Day%202.mp3", true, 457);
        addBoosts("BoostTemplate", "Day 3 – Notifications and the Calming Breath", "Day 3 – Notifications and the Calming Breath", "", "https://s3-us-west-2.amazonaws.com/sandbox.spire.io/spirebin/boosts/91/Day%203.mp3", true, 439);
        addBoosts("BoostTemplate", "Focus", "Clear distractions, identify your next task.", "https://s3-us-west-2.amazonaws.com/sandbox.spire.io/spirebin/boosts/12/focusBoost%403x.png", "https://s3-us-west-2.amazonaws.com/sandbox.spire.io/spirebin/boosts/12/focus.mp3", false, 154);
    }

    @Override
    public Call<SpireUser> createSpireUser(@NonNull SpireUser user) {
        return null;
    }

    @Override
    public Call<User> createUser(@NonNull @Body SpireUser user) {
        return null;
    }

    @Override
    public Call<User> loginUser(@NonNull SpireUser user) {
        return null;
    }

    @Override
    public Call<SpireUser> login(@NonNull @Body SpireUser user) {
        return null;
    }

    @Override
    public Call<ResponseBody> setUserSetting(@Header("Authorization") String accessToken,
                                             @NonNull @Body UserSetting userSetting) {
        return null;
    }

    @Override
    public Call<ResponseBody> updateEvents(@NonNull @Field("access_token") String accessToken, @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<FileBucketUrl> uploadDiagnosticLogs(@NonNull @Query("access_token") String accessToken, @NonNull @Query("key") String key) {
        return null;
    }

    @Override
    public Call<ResponseBody> sendDiagnosticSlack(@NonNull @Field("access_token") String accessToken, @NonNull @Query("message") String message, @NonNull @Query("key") String key) {
        return null;
    }

    @Override
    public Call<FileBucketUrl> getFileUploadUrl(@NonNull @Path("bucket") String bucket,
                                                @NonNull @Query("access_token") String accessToken, @NonNull @Query("key") String key) {
        return null;
    }

    @Override
    public Call<List<NotificationConfig>> getNotificationConfigs(
        @NonNull @Query("access_token") String accessToken) {
        return null;
    }

    @Override
    public Call<ResponseBody> postStreakAddon(@NonNull @Field("access_token") String accessToken,
                                              @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<ResponseBody> postStreak(@NonNull @Field("access_token") String accessToken,
                                         @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<ResponseBody> postNotificationConfigs(
        @NonNull @Field("access_token") String accessToken,
        @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<ResponseBody> sendPasswordResetLink(
        @NonNull @Field("user[email]") String email) {
        return null;
    }

    @Override
    public Call<ResponseBody> sendSensor(@NonNull @Field("access_token") String accessToken,
                                         @NonNull @Field("uuid") String uuid, @NonNull @Field("firmware") String firmware,
                                         @NonNull @Field("description") String description) {
        return null;
    }

    @Override
    public Call<ResponseBody> postUserSettings(
        @NonNull @Field("access_token") String accessToken,
        @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<UserSetting[]> getUserSettings(
        @NonNull @Query("access_token") String accessToken) {
        return null;
    }

    @Override
    public Call<ResponseBody> postRollup(@NonNull @Field("access_token") String accessToken,
                                         @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<StreakData[]> getStreaks(
        @NonNull @Query("access_token") String accessToken) {
        return null;
    }

    @Override
    public Call<LocationData[]> getLocationData(
        @NonNull @Query("access_token") String accessToken) {
        return null;
    }

    @Override
    public Call<ResponseBody> postLocationData(
        @NonNull @Field("access_token") String accessToken,
        @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<StreakAddon[]> getStreakAddonData(
        @NonNull @Query("access_token") String accessToken) {
        return null;
    }

    @Override
    public Call<ResponseBody> postStreakAddonData(
        @NonNull @Field("access_token") String accessToken,
        @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<RollupData[]> getRollups(
        @NonNull @Query("access_token") String accessToken) {
        return null;
    }

    @Override
    public Call<RollupData[]> getLegacyWornTime(@NonNull @Query("access_token") String accessToken) {
        return null;
    }

    private void addBoosts(String type, String title, String instruction, String iconUrl, String mp3Url, boolean locked, int duration) {
        Boost boost = new Boost();
        boost.setType(type);
        boost.setTitle(title);
        boost.setInstruction(instruction);
        boost.setIconUrl(iconUrl);
        boost.setAudioUrl(mp3Url);
        boost.setLocked(locked);
        boost.setDuration(duration);

        boostList.add(boost);
    }

    @Override
    public Call<List<Boost>> getBoosts(@NonNull @Query("access_token") String accessToken) {
        if (accessToken == null) {
            return null;
        }

        List<Boost> response = boostList;

        return delegate.returningResponse(response).getBoosts(accessToken);
    }

    @Override
    public Call<ResponseBody> updateBoosts(
        @NonNull @Field("access_token") String accessToken,
        @NonNull @Field("json") String jsonString) {
        return null;
    }

    @Override
    public Call<UpgradePlatform> fetchUpgradeVersion() {
        return null;
    }

    @Override
    public Call<FileBucketUrl> getS2RawFileUploadUrl(String accessToken, @NonNull String timestamp, @NonNull String deviceSerial) {
        return null;
    }

    @Override
    public Call<ResponseBody> postIntervals(String accessToken, @NonNull JsonArray intervalList) {
        return null;
    }

    @Override
    public Call<ResponseBody> postMetrics(String accessToken, @NonNull JsonArray metricList) {
        return null;
    }

    @Override
    public Call<ResponseBody> postEvents(String accessToken, @NonNull List<SpireDeviceEvent> eventList) {
        return null;
    }

    @Override
    public Call<DevicePack> getStarlingPack(String accessToken, @NonNull String deviceSerial) {
        return null;
    }

    @Override
    public Call<ResponseBody> postChunks(String accessToken, @NonNull JsonObject chunks) {
        return null;
    }
}
