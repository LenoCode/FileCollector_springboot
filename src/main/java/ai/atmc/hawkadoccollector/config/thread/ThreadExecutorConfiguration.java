package ai.atmc.hawkadoccollector.config.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Class that have method to creating TaskExecutor -> https://dzone.com/articles/spring-and-threads-taskexecutor
 *
 */
@Configuration
@EnableAsync
public class ThreadExecutorConfiguration {

    @Value("${thread.executor.config.corepoolsize}")
    private int corePoolSize;

    @Value("${thread.executor.config.maxpoolsize}")
    private int maxPoolSize;

    @Value("${thread.executor.config.queueCapacity}")
    private int queueCapacity;



    /**
     * Initializing main Thread executor object.
     * Something like a context for threads
     * @return
     */
    @Bean
    public TaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
