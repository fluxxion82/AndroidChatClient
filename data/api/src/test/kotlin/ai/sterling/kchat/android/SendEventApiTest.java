package ai.sterling.kchat.android;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ai.sterling.kchat.android.api.SpireRestClient;
import io.spire.android.sharedvo.api.BleEventData;
import io.spire.android.sharedvo.api.SpireUser;
import io.spire.android.http.SpireApiInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendEventApiTest {

    private String email = "qq@ww.ee";
    private String password = "password";
    private String accessToken;
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
    @Ignore // TODO: @mk 17/02/2019 Investigate why this test is failing
    public void test_sendEventNormal() {
        Assert.assertNotNull(apiClient);

        try {
            generateCloseOOM();
            // mApiClient.sendEventsNormal(getEventList(3000));
        } catch (OutOfMemoryError error) {
            Assert.fail("Out of memory");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @Ignore // TODO: @mk 17/02/2019 Investigate why this test is failing
    public void test_sendEventStream() {
        Assert.assertNotNull(apiClient);

        try {
            generateCloseOOM();
            apiClient.sendEvents(getEventList(3000), accessToken);
        } catch (OutOfMemoryError error) {
            Assert.fail("Out of memory");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    private void generateCloseOOM() throws Exception {
        int iteratorValue = 20;
        for (int outerIterator = 1; outerIterator < 20; outerIterator++) {
            long freeMem = Runtime.getRuntime().freeMemory();
            System.out.println("Iteration " + outerIterator + " Free Mem: " + freeMem);
            int loop1 = 2;
            int[] memoryFillIntVar = new int[iteratorValue];
            // feel memoryFillIntVar array in loop..
            do {
                memoryFillIntVar[loop1] = 0;
                loop1--;
            } while (loop1 > 0);
            iteratorValue = iteratorValue * 5;
            System.out.println("\nRequired Memory for next loop: " + iteratorValue + "\n");
            if (iteratorValue > freeMem) {
                break;
            }
            Thread.sleep(100);
        }
    }

    private List<BleEventData> getEventList(int size) {
        List<BleEventData> eventList = new ArrayList<>();

        BleEventData data;
        for (int i = 0; i < size; i++) {
            double value = new Random().nextDouble();
            long timestamp = new Random().nextLong();
            if ((int) (value * 10) % 2 == 0) {
                data = new BleEventData("app_memory", value, timestamp);
            } else if ((int) (value * 10) % 3 == 0) {
                data = new BleEventData("device_pair", value, timestamp);
            } else {
                data = new BleEventData("device_connect", value, timestamp);
            }

            eventList.add(data);
        }

        return eventList;
    }

    @After
    public void afterTest() {
        accessToken = null;
    }
}
