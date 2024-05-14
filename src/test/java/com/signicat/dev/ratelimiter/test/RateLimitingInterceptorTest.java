package com.signicat.dev.ratelimiter.test;

import com.signicat.dev.interceptor.RateLimitingInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class RateLimitingInterceptorTest {

    @Mock
    private RateLimitingInterceptor rateLimitingInterceptor;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testPreHandle() throws Exception {
        when(rateLimitingInterceptor.preHandle(any(HttpServletRequest.class),
                any(HttpServletResponse.class), any())).thenReturn(true);

        boolean result = rateLimitingInterceptor.preHandle(request, response, null);

        assertTrue(result);
        verify(rateLimitingInterceptor,
                times(1)).
                preHandle(any(HttpServletRequest.class),
                        any(HttpServletResponse.class), any());
    }

    @Test
    public void testPreHandle_Failure() throws Exception {
        when(rateLimitingInterceptor.preHandle(any(HttpServletRequest.class),
                any(HttpServletResponse.class), any())).thenReturn(false);

        boolean result = rateLimitingInterceptor.preHandle(request, response, null);

        assertFalse(result);

        verify(rateLimitingInterceptor, times(1))
                .preHandle(any(HttpServletRequest.class),
                        any(HttpServletResponse.class), any());
    }
}
