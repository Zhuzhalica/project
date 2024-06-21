package org.example.filter_one.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class RequestMetric {
    private long startTime;

    private final MeterRegistry registry;

    public RequestMetric(MeterRegistry registry) {
        this.registry = registry;
    }

    public void beforeAction(ConsumerRecord<String, String> record) {
        startTime = System.currentTimeMillis();
    }

    public void afterAction(ConsumerRecord<String, String> record) {
        var endTime = System.currentTimeMillis();
        var requestTimer = Timer.builder("filters.ColorInverse.requests.timer")
                .register(registry);
        requestTimer.record(endTime - startTime, java.util.concurrent.TimeUnit.MILLISECONDS);

        var counter = Counter.builder("filters.ColorInverse.requests.count")
                .tag("success", "true")
                .register(registry);

        counter.increment();
    }

    public void excpetion(ConsumerRecord<String, String> record, Exception e) {
        var counter = Counter.builder("filters.ColorInverse.requests.count")
                .tag("success", "false")
                .register(registry);

        counter.increment();
    }
}
