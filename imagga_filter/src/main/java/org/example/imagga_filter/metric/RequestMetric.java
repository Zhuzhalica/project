package org.example.imagga_filter.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class RequestMetric {

  private long startTime;

  private final MeterRegistry registry;

  public RequestMetric(MeterRegistry registry) {
    this.registry = registry;
  }

  public void beforeAction() {
    startTime = System.currentTimeMillis();
  }

  public void afterAction() {
    var endTime = System.currentTimeMillis();
    var requestTimer = Timer.builder("filters.Immaga.requests.timer")
        .register(registry);
    requestTimer.record(endTime - startTime, java.util.concurrent.TimeUnit.MILLISECONDS);

    var counter = Counter.builder("filters.Immaga.requests.count")
        .tag("success", "true")
        .register(registry);

    counter.increment();
  }

  public void excpetion() {
    var endTime = System.currentTimeMillis();
    var requestTimer = Timer.builder("filters.Immaga.requests.timer")
        .register(registry);
    requestTimer.record(endTime - startTime, java.util.concurrent.TimeUnit.MILLISECONDS);

    var counter = Counter.builder("filters.Immaga.requests.count")
        .tag("success", "false")
        .register(registry);

    counter.increment();
  }
}
