package org.example.project.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FilterRequstMetric implements IMetricProcess {

    private final MeterRegistry registry;

    public FilterRequstMetric(MeterRegistry registry){
        this.registry = registry;
    }

    @Override
    public void beforeAction(HttpServletRequest request) {
        var filters = request.getParameterValues("filters");
        for (var filter : filters) {
            var requestCounter = Counter.builder("api.filters.count")
                    .tag("filter", filter)
                    .register(registry);

            requestCounter.increment();
        }
    }

    @Override
    public void afterAction(HttpServletRequest request, HttpServletResponse response) {
        return;
    }
}
