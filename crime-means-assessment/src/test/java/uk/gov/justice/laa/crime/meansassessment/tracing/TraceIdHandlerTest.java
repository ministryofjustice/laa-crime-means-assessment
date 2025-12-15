package uk.gov.justice.laa.crime.meansassessment.tracing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceIdHandlerTest {

    @InjectMocks
    private TraceIdHandler traceIdHandler;

    @Mock
    private Tracer tracer;

    @Mock
    private CurrentTraceContext currentTraceContext;

    @Mock
    private TraceContext traceContext;

    @Test
    void whenCurrentTraceContextIsNull_thenTraceIdIsBlank() {
        when(tracer.currentTraceContext()).thenReturn(null);
        assertThat(traceIdHandler.getTraceId().isBlank());
    }

    @Test
    void whenTraceContextIsNull_thenTraceIdIsBlank() {
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.context()).thenReturn(null);
        assertThat(traceIdHandler.getTraceId().isBlank());
    }

    @Test
    void whenTraceContextIsNotNull_thenReturnValidTraceId() {
        String traceId = "mock-trace-id";
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn(traceId);
        assertThat(traceIdHandler.getTraceId()).isEqualTo(traceId);
    }
}
