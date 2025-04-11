package uk.gov.justice.laa.crime.meansassessment.tracing;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


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
        assertTrue(traceIdHandler.getTraceId().isBlank());
    }

    @Test
    void whenTraceContextIsNull_thenTraceIdIsBlank() {
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.context()).thenReturn(null);
        assertTrue(traceIdHandler.getTraceId().isBlank());
    }

    @Test
    void whenTraceContextIsNotNull_thenReturnValidTraceId() {
        String traceId = "mock-trace-id";
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn(traceId);
        assertEquals(traceId, traceIdHandler.getTraceId());
    }
}
