package as.rpc.core.api;

import java.util.List;

/**
 * 注册中心
 * <p>
 * {@code @author} abners.
 * <p>
 * {@code @date} 2024/3/26 14:59
 */
public interface RegistryCenter {
    void start();

    void stop();

    // provider 侧
    void register(String service, String instance);

    void unregister(String service, String instance);

    // consumer 侧
    List<String> fetchAll(String service);

    // void subscribe();

    // void heartbeat();

    /**
     * 静态注册中心
     */
    class StaticRegistryCenter implements RegistryCenter {
        List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }
    }

}
