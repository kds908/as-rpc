package as.rpc.core.provider;

import as.rpc.core.api.RegistryCenter;
import as.rpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Provider 配置类
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/18 22:21
 */
@Slf4j
@Configuration
public class ProviderConfig {
    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap bootStrap) {
        return new ProviderInvoker(bootStrap);
    }

    @Bean // (initMethod = "start", destroyMethod = "stop")
    public RegistryCenter providerRC() {
        return new ZkRegistryCenter();
    }

    /**
     * 待服务完全启动后，再调用start将服务暴露
     * 避免注册到注册中心，但服务未完全启动不可用的状态
     * @param providerBootstrap
     * @return
     */
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerConfigRunner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            log.info("providerBootstrap starting...");
            providerBootstrap.start();
            log.info("providerBootstrap started...");
        };
    }
}
