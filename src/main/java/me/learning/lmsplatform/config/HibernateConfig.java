package me.learning.lmsplatform.config;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HibernateConfig {

    private final SessionFactory sessionFactory;

    @PostConstruct
    public void configureHibernate() {
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(false);
        log.info("Hibernate statistics disabled to prevent startup queries");
    }
}
