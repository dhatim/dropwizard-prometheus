# Dropwizard Prometheus module
Dropwizard bundle and reporter for [Prometheus](https://prometheus.io)

## Reporting to Prometheus Pushgateway

This module provides `PrometheusReporter`, which allows your application to constantly stream metric values to a [Prometheus Pushway](https://prometheus.io/docs/instrumenting/pushing/) server:


    final Pushgateway pushgateway = new Pushgateway("localhost", 9091));
    final PrometheusReporter reporter = PrometheusReporter.forRegistry(registry)
                                                  .prefixedWith("web1.example.com")
                                                  .filter(MetricFilter.ALL)
                                                  .build(pushgateway);
    reporter.start(1, TimeUnit.MINUTES);

## Prometheus servlet

You can also use `PrometheusBundle`, which starts a new admin servlet exposing expose metric values to a compatible [Prometheus](https://prometheus.io) format.

    @Override
    public void initialize(Bootstrap<DsnConfiguration> bootstrap) {
        bootstrap.addBundle(new PrometheusBundle());
    }

After the Dropwizard application server start, a new endpoint `/prometheus-metrics` will be accessible with `admin` endpoint.

