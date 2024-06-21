package org.example.project.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IMetricProcess {
    void beforeAction(HttpServletRequest request);
    void afterAction(HttpServletRequest request, HttpServletResponse response);
}
