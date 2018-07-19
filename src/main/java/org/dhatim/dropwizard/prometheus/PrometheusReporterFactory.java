package org.dhatim.dropwizard.prometheus;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.metrics.BaseReporterFactory;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@JsonTypeName("prometheus")
public class PrometheusReporterFactory extends BaseReporterFactory {

    @JsonProperty
    @NotNull
    public String url = null;

    @JsonProperty
    @NotNull
    public String prefix = "";

    @JsonProperty
    @NotNull
    public String job = "prometheus";

    @Override
    public ScheduledReporter build(MetricRegistry registry) {

        final Pushgateway pushgateway = new Pushgateway(url, job);

        final PrometheusReporter reporter = PrometheusReporter.forRegistry(registry)
                .prefixedWith(prefix)
                .filter(getFilter())
                .build(pushgateway);
        return reporter;
    }
}
