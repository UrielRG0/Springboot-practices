package tacos.web.api;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import tacos.web.filter.CorrelationIdFilter;

public class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    public void testNoHeader_GeneratesNewUUID() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        WebFilterChain chain = ex -> Mono.empty();

        filter.filter(exchange, chain).block();

        String responseHeader = exchange.getResponse().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertThat(responseHeader).isNotNull();
        assertThat(responseHeader).matches("^[a-zA-Z0-9\\-]{16,36}$");
    }

    @Test
    public void testValidHeader_PreservesValue() {
        String validId = "my-valid-corr-id-12345";
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/").header(CorrelationIdFilter.CORRELATION_ID_HEADER, validId)
        );
        WebFilterChain chain = ex -> Mono.empty();

        filter.filter(exchange, chain).block();

        String responseHeader = exchange.getResponse().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertThat(responseHeader).isEqualTo(validId);
    }

    @Test
    public void testInvalidHeader_RejectsAndGeneratesNew() {
        String invalidId = "bad_id_with_invalid_chars!@#\n<script>"; 
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/").header(CorrelationIdFilter.CORRELATION_ID_HEADER, invalidId)
        );
        WebFilterChain chain = ex -> Mono.empty();

        filter.filter(exchange, chain).block();

        String responseHeader = exchange.getResponse().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertThat(responseHeader).isNotNull();
        assertThat(responseHeader).isNotEqualTo(invalidId);
        assertThat(responseHeader).matches("^[a-zA-Z0-9\\-]{16,36}$");
    }

    @Test
    public void testMdcIsClearedAfterRequest() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        WebFilterChain chain = ex -> Mono.empty();

        filter.filter(exchange, chain).block();

        assertThat(MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY)).isNull();
    }
}