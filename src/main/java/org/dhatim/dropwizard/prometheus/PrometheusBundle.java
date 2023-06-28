package org.dhatim.dropwizard.prometheus;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class PrometheusBundle implements ConfiguredBundle<Configuration> {

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.admin().addServlet("prometheus-metrics", new PrometheusServlet(environment.metrics())).addMapping("/prometheus-metrics");
    }

}
