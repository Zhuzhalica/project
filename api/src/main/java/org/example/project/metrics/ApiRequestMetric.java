package org.example.project.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;

public class ApiRequestMetric implements IMetricProcess {
    private final MeterRegistry registry;
    private long startTime;

    public ApiRequestMetric(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void beforeAction(HttpServletRequest request) {
        var dataSizeSummary = DistributionSummary.builder("api.requests.size")
                .baseUnit("bytes")
                .tag("method", request.getMethod())
                .tag("uri", request.getRequestURI())
                .register(registry);

        dataSizeSummary.record(request.getContentLength());

        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterAction(HttpServletRequest request, HttpServletResponse response) {
        var endTime = System.currentTimeMillis();
        var requestTimer = Timer.builder("api.requests.timer")
                .tag("code", Integer.toString(response.getStatus()))
                .tag("method", request.getMethod())
                .tag("uri", request.getRequestURI())
                .register(registry);
        requestTimer.record(endTime - startTime, java.util.concurrent.TimeUnit.MILLISECONDS);

        var counter = Counter.builder("api.requests.count")
                .tag("code", Integer.toString(response.getStatus()))
                .tag("method", request.getMethod())
                .tag("uri", request.getRequestURI())
                .register(registry);

        counter.increment();
    }
}
