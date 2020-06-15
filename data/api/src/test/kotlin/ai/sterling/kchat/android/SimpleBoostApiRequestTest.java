package ai.sterling.kchat.android;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ai.sterling.kchat.android.api.SpireRestClient;
import ai.sterling.kchat.android.src.BehaviorDelegate;
import ai.sterling.kchat.android.src.MockRetrofit;
import ai.sterling.kchat.android.src.NetworkBehavior;
import io.spire.android.sharedvo.api.Boost;
import io.spire.android.sharedvo.api.SpireUser;
import io.spire.android.http.SpireApiInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <p/>
 * Generally it's not a good idea to test real server requests because of the following points:
 * -Another moving piece that can intermittently fail
 * -Requires some expertise outside of the Android domain to deploy the server and keep it updated
 * -Difficult to trigger error/edge cases
 * -Slow test execution (still making HTTP calls)
 * <p/>
 * Despite this, I did try testing real server calls, once to log in to get an access token, and the other to test getting the boost list.
 * However, I did create some classes afterwards to be able to mock out our api class that will return results that you set up. This way we can simulate api responses, network delays, etc.
 * The last test shows some of this using the MockSpireApi class I set up.
 */
public class SimpleBoostApiRequestTest {
    private String email = "qq@ww.ee";
    private String password = "password";
    private String accessToken;
    private List<Boost> boostList;
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
    public void test_getBoosts() {
        Assert.assertNotNull(accessToken);
        Assert.assertNotNull(apiClient);
        boostList = apiClient.getBoostList(accessToken);

        Assert.assertNotNull(boostList);
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

        Call<List<Boost>> boosts = spireApi.getBoosts(accessToken);

        Assert.assertNotNull(boosts);
    }

    @After
    public void afterTest() {
        accessToken = null;
    }
}
