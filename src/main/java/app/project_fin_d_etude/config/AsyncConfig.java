package app.project_fin_d_etude.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration de l'exécution asynchrone dans l'application. Cette classe
 * configure le pool de threads pour gérer les opérations asynchrones.
 */
@Configuration
@EnableAsync // Active le support asynchrone dans Spring
public class AsyncConfig {

    // Constantes pour la configuration du pool de threads
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final int QUEUE_CAPACITY = 100;
    private static final String THREAD_NAME_PREFIX = "AsyncThread-";

    /**
     * Configure et crée un pool de threads pour l'exécution des tâches
     * asynchrones.
     *
     * @return Un Executor configuré pour gérer les tâches asynchrones
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Nombre de threads qui seront toujours actifs
        executor.setCorePoolSize(CORE_POOL_SIZE);

        // Nombre maximum de threads que le pool peut créer
        executor.setMaxPoolSize(MAX_POOL_SIZE);

        // Nombre maximum de tâches en attente dans la file
        executor.setQueueCapacity(QUEUE_CAPACITY);

        // Préfixe pour identifier les threads dans les logs
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);

        // Politique de gestion si la file est pleine : ici, exécute la tâche dans le thread appelant
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Initialise le pool de threads
        executor.initialize();

        return executor;
    }
}
