package com.studyflow.backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
@Profile("!test")
public class RateLimitingFilter implements Filter {

    // Enable X-Forwarded-For resolution when the app is deployed behind a trusted
    // reverse proxy (e.g. Railway, Heroku). Keep false in direct-access environments
    // to prevent clients from spoofing their IP address.
    @org.springframework.beans.factory.annotation.Value(
            "${security.rate-limit.trust-proxy-headers:false}")
    private boolean trustProxyHeaders;

    private static final int CAPACITY = 60;
    private static final int REFILL_TOKENS = 60;
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            chain.doFilter(request, response);
            return;
        }
        String path = httpRequest.getServletPath();

        if (shouldSkip(path)) {
            chain.doFilter(request, response);
            return;
        }

        String ip = resolveClientIp(httpRequest);
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else if (response instanceof HttpServletResponse httpResponse) {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"status\":429,\"message\":\"Too many requests. Try again in a minute.\"}");
        } else {
            throw new ServletException("Rate limit exceeded but response is not HttpServletResponse");
        }
    }

    private boolean shouldSkip(String path) {
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (trustProxyHeaders) {
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                return xff.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(CAPACITY)
                .refillGreedy(REFILL_TOKENS, REFILL_DURATION)
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
