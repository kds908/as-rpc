package as.rpc.core.provider;

import as.rpc.core.api.RegistryCenter;
import as.rpc.core.registry.ZkRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provider 配置类
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/18 22:21
 */
@Configuration
public class ProviderConfig {
    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter providerRC() {
        return new ZkRegistryCenter();
    }
}
