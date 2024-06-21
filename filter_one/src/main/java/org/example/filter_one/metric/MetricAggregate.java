package org.example.filter_one.metric;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

//@Component
//public class MetricAggregate {
//
//    private final MeterRegistry registry;
//    private final RequstFilterMetric filterMetric;
//
//    public MetricAggregate(MeterRegistry registry) {
//        this.registry = registry;
//        this.filterMetric = new RequstFilterMetric();
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        if (Objects.equals(request.getRequestURI(), "/actuator/prometheus")) {
//            logger.info("get metric");
//            var requestMetricCounter = new RequestMetricCounter();
//            requestMetricCounter.beforeAction(request, registry);
//            filterChain.doFilter(request, response);
//            requestMetricCounter.afterAction(request, response, registry);
//            return;
//        }
//
//        if (Objects.equals(request.getMethod(), "POST") && request.getRequestURI().endsWith("/filters/apply")) {
//            filterMetric.beforeAction(request, registry);
//        }
//
//        var requestFilter = new RequestCounterFilter(logger);
//        try {
//            requestFilter.beforeAction(request, registry);
//            filterChain.doFilter(request, response);
//        } finally {
//            requestFilter.afterAction(request, response, registry);
//        }
//    }
//
//    public void beforeRequest(ConsumerRecord<String, String> record) {
//        var requestMetric = new RequestMetric(registry);
//        requestMetric.beforeAction(record);
//    }
//
//    public void excpetion(ConsumerRecord<String, String> record, Exception e) {
//    }
//
//    public void afterRequest(ConsumerRecord<String, String> record) {
//        requestMetric.beforeAction(record);
//    }
//}