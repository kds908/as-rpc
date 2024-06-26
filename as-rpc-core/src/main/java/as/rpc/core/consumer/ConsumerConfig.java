package as.rpc.core.consumer;

import as.rpc.core.api.Filter;
import as.rpc.core.api.LoadBalancer;
import as.rpc.core.api.RegistryCenter;
import as.rpc.core.api.Router;
import as.rpc.core.cluster.RoundRibbonLoadBalance;
import as.rpc.core.filter.CacheFilter;
import as.rpc.core.meta.InstanceMeta;
import as.rpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Description for this class
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/19 21:47
 */
@Slf4j
@Configuration
public class ConsumerConfig {
    @Value("${as-rpc.providers}")
    String servers;
    @Bean
    ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerConfigRunner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("consumerBootstrap starting...");
            consumerBootstrap.start();
            log.info("consumerBootstrap started...");
        };
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
//        return LoadBalancer.Default;
//        return new RandomLoadBalance();
        return new RoundRibbonLoadBalance<>();
    }

    @Bean
    public Router<?> router() {
        return Router.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumerRC() {
        return new ZkRegistryCenter();
    }

    @Bean
    public Filter filter() {
        return new CacheFilter();
    }
}
