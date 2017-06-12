package org.dhatim.dropwizard.prometheus;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class PrometheusBundle implements Bundle {

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        environment.admin().addServlet("prometheus-metrics", new PrometheusServlet(environment.metrics())).addMapping("/prometheus-metrics");
    }

}
