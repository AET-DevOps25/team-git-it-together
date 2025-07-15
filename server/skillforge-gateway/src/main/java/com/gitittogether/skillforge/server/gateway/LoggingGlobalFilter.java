package com.gitittogether.skillforge.server.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Profile("dev") // Only active in development profile
@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getPath().toString();
        String headers = exchange.getRequest().getHeaders().toString();
        log.info("Incoming request: {} {} | Headers: {}", method, path, headers);

        return chain.filter(exchange)
                .doOnSuccess((done) -> {
                    int statusCode = exchange.getResponse().getStatusCode() != null ?
                            exchange.getResponse().getStatusCode().value() : 0;
                    log.info("Response for {} {} -> HTTP {}", method, path, statusCode);
                });
    }

    @Override
    public int getOrder() {
        return -1; // High precedence
    }
}
