package app.project_fin_d_etude.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

/**
 * Configuration de l'exécution asynchrone dans l'application. Cette classe
 * configure le pool de threads pour gérer les opérations asynchrones.
 */
@Configuration
@EnableAsync // Active le support asynchrone dans Spring
public class AsyncConfig {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Value("${async.corePoolSize:2}")
    private int corePoolSize;
    @Value("${async.maxPoolSize:4}")
    private int maxPoolSize;
    @Value("${async.queueCapacity:100}")
    private int queueCapacity;
    @Value("${async.threadNamePrefix:AsyncThread-}")
    private String threadNamePrefix;

    /**
     * Configure et crée un pool de threads pour l'exécution des tâches
     * asynchrones.
     *
     * @return Un Executor configuré pour gérer les tâches asynchrones
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        logger.info("Initialisation du ThreadPoolTaskExecutor : core={}, max={}, queue={}, prefix={}", corePoolSize, maxPoolSize, queueCapacity, threadNamePrefix);
        return new DelegatingSecurityContextExecutorService(executor.getThreadPoolExecutor());
    }
}
