package com.signicat.dev.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingInterceptor.class);
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private long rateLimit;
    private long rateLimitDurationInMinutes;

    public RateLimitingInterceptor(long rateLimit, long rateLimitDurationInMinutes) {
        this.rateLimit = rateLimit;
        this.rateLimitDurationInMinutes = rateLimitDurationInMinutes;
    }


    private Bucket createNewBucket(long rateLimit, long rateLimitDurationInMinutes) {
        log.info("Creating new bucket for rate limiting with capacity: {} and per {} minutes", rateLimit, rateLimitDurationInMinutes);
        Refill refill = Refill.greedy(5, Duration.ofMinutes(rateLimitDurationInMinutes));
        Bandwidth limit = Bandwidth.classic(rateLimit, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public boolean preHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        Bucket bucket = this.buckets.computeIfAbsent(ip, k -> createNewBucket(rateLimit, rateLimitDurationInMinutes));

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            String message = "You have exhausted your API request quota. Try again in " + waitForRefill + " seconds.";
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(message);
            response.setContentType("application/json");
            return false;
        }
    }
}

