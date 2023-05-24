package nl.overheid.koop.plooi.repository.service;

import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMapAdapter;

public final class Java11MockRequestBuilders {

    private Java11MockRequestBuilders() {
    }

    public static RequestBuilder buildMockRequest(HttpRequest.Builder requestBuilder) {
        var request = requestBuilder.build();
        var bodySubscriber = new BodyBufferSubscriber();
        request.bodyPublisher().orElseThrow().subscribe(bodySubscriber);
        return MockMvcRequestBuilders.request(request.method(), request.uri())
                .headers(HttpHeaders.readOnlyHttpHeaders(new MultiValueMapAdapter<>(request.headers().map())))
                .content(bodySubscriber.getBytes());
    }
}

class BodyBufferSubscriber implements Flow.Subscriber<ByteBuffer> {

    private List<Byte> body = new ArrayList<>();

    @Override
    public void onSubscribe(final Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(ByteBuffer byteBuf) {
        byte[] bytes = new byte[byteBuf.remaining()];
        byteBuf.get(bytes);
        for (var b : bytes) {
            this.body.add(Byte.valueOf(b));
        }
    }

    @Override
    public void onError(Throwable e) {
        LoggerFactory.getLogger(getClass()).error("Subscription error", e);
    }

    @Override
    public void onComplete() {
    }

    byte[] getBytes() {
        var bytes = new byte[this.body.size()];
        for (int i = 0; i < this.body.size(); i++) {
            bytes[i] = this.body.get(i);
        }
        return bytes;
    }
}
