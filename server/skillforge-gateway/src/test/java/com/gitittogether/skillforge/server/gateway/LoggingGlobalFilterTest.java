package com.gitittogether.skillforge.server.gateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoggingGlobalFilterTest {

    @Test
    void shouldProcessRequestAndContinueChain() {
        // Given
        LoggingGlobalFilter filter = new LoggingGlobalFilter();
        GatewayFilterChain filterChain = mock(GatewayFilterChain.class);
        when(filterChain.filter(any())).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/courses")
                .header("Content-Type", "application/json")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<Void> result = filter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        verify(filterChain).filter(exchange);
    }

    @Test
    void shouldHandlePostRequest() {
        // Given
        LoggingGlobalFilter filter = new LoggingGlobalFilter();
        GatewayFilterChain filterChain = mock(GatewayFilterChain.class);
        when(filterChain.filter(any())).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/v1/users/login")
                .header("Authorization", "Bearer token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<Void> result = filter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        verify(filterChain).filter(exchange);
    }

    @Test
    void shouldReturnCorrectOrder() {
        // Given
        LoggingGlobalFilter filter = new LoggingGlobalFilter();

        // When
        int order = filter.getOrder();

        // Then
        assertThat(order).isEqualTo(-1);
    }
} 