package com.portfolio.library;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {
    /** Injected so tests can control "today" (due dates, fines). */
    @Bean
    Clock clock() { return Clock.systemDefaultZone(); }
}
