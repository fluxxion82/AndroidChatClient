package ai.sterling.kchat.android.src;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public final class BehaviorDelegate<T> {
    private final Retrofit retrofit;
    private final NetworkBehavior behavior;
    private final ExecutorService executor;
    private final Class<T> service;

    BehaviorDelegate(Retrofit retrofit, NetworkBehavior behavior, ExecutorService executor,
                     Class<T> service) {
        this.retrofit = retrofit;
        this.behavior = behavior;
        this.executor = executor;
        this.service = service;
    }

    public T returningResponse(Object response) {
        return returning(Calls.response(response));
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <R> T returning(Call<R> call) {
        final Call<R> behaviorCall = new BehaviorCall<>(behavior, executor, call);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
            (proxy, method, args) -> {
                Type returnType = method.getGenericReturnType();
                Annotation[] methodAnnotations = method.getAnnotations();
                CallAdapter<R, T> callAdapter = (CallAdapter<R, T>) retrofit.callAdapter(returnType, methodAnnotations);
                return callAdapter.adapt(behaviorCall);
            });
    }
}
