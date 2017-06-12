Dropwizard Prometheus module
=======
[![Build Status](https://travis-ci.org/dhatim/dropwizard-prometheus.png?branch=master)](https://travis-ci.org/dhatim/dropwizard-prometheus)
[![Coverage Status](https://coveralls.io/repos/github/dhatim/dropwizard-prometheus/badge.svg?branch=master)](https://coveralls.io/github/dhatim/dropwizard-prometheus?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/787d545edbad4c5fb47c22a813fa8535)](https://www.codacy.com/app/mathieu-ligocki/dropwizard-prometheus?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dhatim/dropwizard-prometheus&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.dhatim/dropwizard-prometheus/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.dhatim/dropwizard-prometheus)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.dhatim/dropwizard-prometheus/badge.svg)](http://www.javadoc.io/doc/org.dhatim/dropwizard-prometheus)

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

You can also use `PrometheusBundle`, which starts a new admin servlet exposing metric values to a [Prometheus](https://prometheus.io) server.

    @Override
    public void initialize(Bootstrap<DsnConfiguration> bootstrap) {
        bootstrap.addBundle(new PrometheusBundle());
    }

After the Dropwizard application server start, a new endpoint `/prometheus-metrics` will be accessible with `admin` endpoint.

