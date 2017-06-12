package org.dhatim.dropwizard.prometheus;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class PrometheusTextWriterTest {

    @Test
    public void testType() throws IOException {
        StringWriter buffer = new StringWriter();
        try (PrometheusTextWriter writer = new PrometheusTextWriter(buffer)) {
            writer.writeType("lorem.ipsum", MetricType.GAUGE);
        }
        assertThat(buffer.toString()).isEqualTo("# TYPE lorem.ipsum gauge\n");
    }

    @Test
    public void testHelp() throws IOException {
        StringWriter buffer = new StringWriter();
        try (PrometheusTextWriter writer = new PrometheusTextWriter(buffer)) {
            writer.writeHelp("lorem.ipsum", "A\nB\nC");
        }
        assertThat(buffer.toString()).isEqualTo("# HELP lorem.ipsum A\\nB\\nC\n");
    }

    @Test
    public void testSample() throws IOException {
        StringWriter buffer = new StringWriter();
        try (PrometheusTextWriter writer = new PrometheusTextWriter(buffer)) {
            writer.writeSample("lorem.ipsum", Collections.<String, String>emptyMap(), 1.0D);
        }
        assertThat(buffer.toString()).isEqualTo("lorem.ipsum 1.0\n");
    }

    @Test
    public void testLabelizedSample() throws IOException {
        StringWriter buffer = new StringWriter();
        try (PrometheusTextWriter writer = new PrometheusTextWriter(buffer)) {
            writer.writeSample("lorem.ipsum", simpleMap(), 2.0D);
        }
        assertThat(buffer.toString()).isEqualTo("lorem.ipsum{quantile=\"1.0\",} 2.0\n");
    }

    @Test
    public void testLabelizedSample2() throws IOException {
        StringWriter buffer = new StringWriter();
        try (PrometheusTextWriter writer = new PrometheusTextWriter(buffer)) {
            writer.writeSample("lorem.ipsum", doubleMap(), 2.0D);
        }
        assertThat(buffer.toString()).isEqualTo("lorem.ipsum{quantile=\"1.0\",centile=\"3.0\",} 2.0\n");
    }

    private static Map<String, String> simpleMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("quantile", "1.0");
        return map;
    }

    private static Map<String, String> doubleMap() {
        Map<String, String> map = simpleMap();
        map.put("centile", "3.0");
        return map;
    }

}
