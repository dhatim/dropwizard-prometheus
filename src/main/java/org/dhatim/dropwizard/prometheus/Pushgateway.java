package org.dhatim.dropwizard.prometheus;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pushgateway implements PrometheusSender {

    private static final int SECONDS_PER_MILLISECOND = 1000;

    private static final Logger LOG = LoggerFactory.getLogger(Pushgateway.class);

    private final String hostname;
    private final int port;
    private final String job;

    private HttpURLConnection connection;
    private PrometheusTextWriter writer;
    private DropwizardMetricsExporter exporter;

    public Pushgateway(String hostname, int port) {
        this(hostname, port, "prometheus");
    }

    public Pushgateway(String hostname, int port, String job) {
        this.hostname = hostname;
        this.port = port;
        this.job = job;
    }

    @Override
    public void close() throws IOException {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            LOG.error("Error closing writer", e);
        } finally {
            this.writer = null;
            this.exporter = null;
        }

        int response = connection.getResponseCode();
        if (response != HttpURLConnection.HTTP_ACCEPTED) {
            throw new IOException("Response code from " + hostname + " was " + response);
        }
        connection.disconnect();
        this.connection = null;
    }

    @Override
    public void connect() throws IllegalStateException, IOException {
        if (isConnected()) {
            throw new IllegalStateException("Already connected");
        }

        String url = "http://" + hostname + ":" + port + "/metrics/job/" + URLEncoder.encode(job, "UTF-8");
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("Content-Type", TextFormat.REQUEST_CONTENT_TYPE);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        conn.setConnectTimeout(10 * SECONDS_PER_MILLISECOND);
        conn.setReadTimeout(10 * SECONDS_PER_MILLISECOND);
        conn.connect();

        this.connection = conn;
        this.writer = new PrometheusTextWriter(new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)));
        this.exporter = new DropwizardMetricsExporter(writer);
    }

    @Override
    public void sendGauge(String name, Gauge<?> gauge) throws IOException {
        exporter.writeGauge(name, gauge);
    }

    @Override
    public void sendCounter(String name, Counter counter) throws IOException {
        exporter.writeCounter(name, counter);
    }

    @Override
    public void sendHistogram(String name, Histogram histogram) throws IOException {
        exporter.writeHistogram(name, histogram);
    }

    @Override
    public void sendMeter(String name, Meter meter) throws IOException {
        exporter.writeMeter(name, meter);
    }

    @Override
    public void sendTimer(String name, Timer timer) throws IOException {
        exporter.writeTimer(name, timer);
    }

    @Override
    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

}
