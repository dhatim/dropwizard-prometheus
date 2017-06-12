package org.dhatim.dropwizard.prometheus;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import java.io.Closeable;
import java.io.IOException;

public interface PrometheusSender extends Closeable {

    /**
     * Connects to the server.
     *
     * @throws IllegalStateException if the client is already connected
     * @throws IOException if there is an error connecting
     */
    public void connect() throws IllegalStateException, IOException;

    public void sendGauge(String name, Gauge<?> gauge) throws IOException;
    public void sendCounter(String name, Counter counter) throws IOException;
    public void sendHistogram(String name, Histogram histogram) throws IOException;
    public void sendMeter(String name, Meter meter) throws IOException;
    public void sendTimer(String name, Timer timer) throws IOException;

    /**
     * Flushes buffer, if applicable
     *
     * @throws IOException
     */
    void flush() throws IOException;

    /**
     * Returns true if ready to send data
     */
    boolean isConnected();

}
