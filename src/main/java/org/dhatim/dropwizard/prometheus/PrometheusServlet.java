package org.dhatim.dropwizard.prometheus;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.servlets.MetricsServlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
class PrometheusServlet extends HttpServlet {

    public static final String METRICS_REGISTRY = MetricsServlet.class.getCanonicalName() + ".registry";
    public static final String METRIC_FILTER = MetricsServlet.class.getCanonicalName() + ".metricFilter";
    public static final String ALLOWED_ORIGIN = MetricsServlet.class.getCanonicalName() + ".allowedOrigin";
    private static final Logger LOG = LoggerFactory.getLogger(PrometheusServlet.class);

    private MetricRegistry registry;

    private String allowedOrigin;

    private MetricFilter filter;

    public PrometheusServlet(MetricRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        final ServletContext context = config.getServletContext();
        if (null == registry) {
            final Object registryAttr = context.getAttribute(METRICS_REGISTRY);
            if (registryAttr instanceof MetricRegistry) {
                this.registry = (MetricRegistry) registryAttr;
            } else {
                throw new ServletException("Couldn't find a MetricRegistry instance.");
            }
        }

        filter = (MetricFilter) context.getAttribute(METRIC_FILTER);
        if (filter == null) {
            filter = MetricFilter.ALL;
        }

        this.allowedOrigin = context.getInitParameter(ALLOWED_ORIGIN);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(TextFormat.CONTENT_TYPE);
        if (allowedOrigin != null) {
            resp.setHeader("Access-Control-Allow-Origin", allowedOrigin);
        }
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        resp.setStatus(HttpServletResponse.SC_OK);

        Set<String> filtered = parse(req);

        PrometheusTextWriter writer = new PrometheusTextWriter(resp.getWriter());
        try {
            DropwizardMetricsExporter exporter = new DropwizardMetricsExporter(writer);

            for (Map.Entry<String, Gauge> entry : registry.getGauges(filter).entrySet()) {
                String sanitizedName = DropwizardMetricsExporter.sanitizeMetricName(entry.getKey());
                if (filtered.isEmpty() || filtered.contains(sanitizedName)) {
                    exporter.writeGauge(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, Counter> entry : registry.getCounters(filter).entrySet()) {
                String sanitizedName = DropwizardMetricsExporter.sanitizeMetricName(entry.getKey());
                if (filtered.isEmpty() || filtered.contains(sanitizedName)) {
                    exporter.writeCounter(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, Histogram> entry : registry.getHistograms(filter).entrySet()) {
                String sanitizedName = DropwizardMetricsExporter.sanitizeMetricName(entry.getKey());
                if (filtered.isEmpty() || filtered.contains(sanitizedName)) {
                    exporter.writeHistogram(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, Meter> entry : registry.getMeters(filter).entrySet()) {
                String sanitizedName = DropwizardMetricsExporter.sanitizeMetricName(entry.getKey());
                if (filtered.isEmpty() || filtered.contains(sanitizedName)) {
                    exporter.writeMeter(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, Timer> entry : registry.getTimers(filter).entrySet()) {
                String sanitizedName = DropwizardMetricsExporter.sanitizeMetricName(entry.getKey());
                if (filtered.isEmpty() || filtered.contains(sanitizedName)) {
                    exporter.writeTimer(entry.getKey(), entry.getValue());
                }
            }

            writer.flush();
        } catch (RuntimeException ex) {
            LOG.error("Unhandled exception", ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            writer.close();
        }
    }

    private Set<String> parse(HttpServletRequest req) {
        String[] includedParam = req.getParameterValues("name[]");
        return includedParam == null ? Collections.<String>emptySet() : new HashSet<String>(Arrays.asList(includedParam));
    }

}
