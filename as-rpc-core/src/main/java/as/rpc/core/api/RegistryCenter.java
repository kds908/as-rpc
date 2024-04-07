package as.rpc.core.api;

import as.rpc.core.meta.InstanceMeta;
import as.rpc.core.meta.ServiceMeta;
import as.rpc.core.registry.ChangedListener;

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
    /**
     * 注册
     * @param service 服务
     * @param instance 实例名
     */
    void register(ServiceMeta service, InstanceMeta instance);

    /**
     * 取消注册
     * @param service 服务
     * @param instance 实例名
     */
    void unregister(ServiceMeta service, InstanceMeta instance);

    // consumer 侧

    /**
     * 获取服务节点
     * @param service
     * @return
     */
    List<InstanceMeta> fetchAll(ServiceMeta service);

    /**
     * 订阅
     *
     * 当服务宕机或关闭，外部需要有感知变化的能力
     * @param service 服务
     * @param listener 监听
     */
     void subscribe(ServiceMeta service, ChangedListener listener);

    // void heartbeat();

    /**
     * 静态注册中心
     */
    class StaticRegistryCenter implements RegistryCenter {
        List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }
    }

}
