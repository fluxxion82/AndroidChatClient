package ai.sterling.kchat.android;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ai.sterling.kchat.android.api.SpireRestClient;
import ai.sterling.kchat.android.src.BehaviorDelegate;
import ai.sterling.kchat.android.src.MockRetrofit;
import ai.sterling.kchat.android.src.NetworkBehavior;
import io.spire.android.sharedvo.api.SpireUser;
import io.spire.android.sharedvo.api.StreakData;
import io.spire.android.http.SpireApiInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetStreaksApiTest {
    private String email = "qq@ww.ee";
    private String password = "password";
    private String accessToken;
    private StreakData[] streakList;
    private SpireRestClient apiClient;

    @Before
    public void beforeTest() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(MockSpireApi.DEBUG_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        apiClient = new SpireRestClient(retrofit.create(SpireApi.class),
                new SpireApiInterceptor(),
                MockSpireApi.DEBUG_APP_AT_SPIRE,
                MockSpireApi.DEBUG_API_AT_SPIRE,
                MockSpireApi.DEBUG_DATA_AT_SPIRE);

        SpireUser user = apiClient.login(email, password).body();
        Assert.assertNotNull(user);
        accessToken = user.getAccessToken();
        Assert.assertNotNull(accessToken);
    }

    @Test
    public void test_getStreaks() {
        Assert.assertNotNull(accessToken);
        Assert.assertNotNull(apiClient);

        streakList = apiClient.getStreaks(accessToken); //"0705696b7fffa1614dbc723fad8801ad4dba951c24747f9165c44abde815521c"
        Assert.assertNotNull(streakList);

        Calendar calendar = Calendar.getInstance();

        Map<String, Long> dayList = new HashMap<>();

        for (StreakData streak : streakList) {
            long day = Instant.now().getEpochSecond();
            calendar.setTimeInMillis(day * 1000);

            if (!dayList.containsKey(calendar.getTime().toString())) {
                dayList.put(calendar.getTime().toString(), day);

                System.out.println("day=" + calendar.getTime().toString());
            }
        }

        System.out.println("number of days=" + dayList.size());
    }

    @Test
    public void test_mockApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MockSpireApi.DEBUG_URL)
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();

        BehaviorDelegate<SpireApi> delegate = mockRetrofit.create(SpireApi.class);
        MockSpireApi spireApi = new MockSpireApi(delegate);

        behavior.setDelay(500, TimeUnit.MILLISECONDS);
    }

    @After
    public void afterTest() {
        accessToken = null;
    }
}
