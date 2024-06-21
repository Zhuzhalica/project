package org.example.project.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MetricRequestMetric implements IMetricProcess {
    private long startTime;
    private final MeterRegistry registry;

    public MetricRequestMetric(MeterRegistry registry){
        this.registry = registry;
    }
    @Override
    public void beforeAction(HttpServletRequest request) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterAction(HttpServletRequest request, HttpServletResponse response) {
        var endTime = System.currentTimeMillis();
        var requestTimer = Timer.builder("api.metric.timer")
                .tag("code", Integer.toString(response.getStatus()))
                .tag("method", request.getMethod())
                .register(registry);

        requestTimer.record(endTime - startTime, java.util.concurrent.TimeUnit.MILLISECONDS);

        var counter = Counter.builder("api.metric.count")
                .tag("code", Integer.toString(response.getStatus()))
                .tag("method", request.getMethod())
                .register(registry);

        counter.increment();
    }
}
