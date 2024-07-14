package faang.school.urlshortenerservice.config.async.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncSchedulerConfig {

    @Value("${async.corePoolSize}")
    private int corePoolSize;

    @Value("${async.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${async.queueCapacity}")
    private int queueCapacity;

    @Value("${async.keepAliveTime}")
    private int keepAliveTime;

    @Bean
    public ExecutorService hashGeneratorExecutorService() {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueCapacity));
    }
}
