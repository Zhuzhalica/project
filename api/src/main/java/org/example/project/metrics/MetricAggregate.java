package org.example.project.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class MetricAggregate extends OncePerRequestFilter {

    private final MeterRegistry registry;
    private final FilterRequstMetric filterMetric;

    public MetricAggregate(MeterRegistry registry) {
        this.registry = registry;
        this.filterMetric = new FilterRequstMetric(registry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (Objects.equals(request.getRequestURI(), "/actuator/prometheus")) {
            logger.info("get metric");
            var requestMetricCounter = new MetricRequestMetric(registry);
            requestMetricCounter.beforeAction(request);
            filterChain.doFilter(request, response);
            requestMetricCounter.afterAction(request, response);
            return;
        }

        if (Objects.equals(request.getMethod(), "POST") && request.getRequestURI().endsWith("/filters/apply")) {
            filterMetric.beforeAction(request);
        }

        var requestFilter =new ApiRequestMetric(registry);
        try {
            requestFilter.beforeAction(request);
            filterChain.doFilter(request, response);
        } finally {
            requestFilter.afterAction(request, response);
        }
    }
}