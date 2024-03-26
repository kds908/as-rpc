package as.rpc.core.registry;

import as.rpc.core.api.RegistryCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * zookeeper registry center
 * 1. 集成 zk client
 *
 * <p>
 * {@code @author} abners.
 * <p>
 * {@code @date} 2024/3/26 15:22
 */
public class ZkRegistryCenter implements RegistryCenter {

    //
    private CuratorFramework client = null;


    @Override
    public void start() {
        // 定义 Retry 策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("101.35.215.95:21819")
                .namespace("as-rpc")
                .retryPolicy(retryPolicy)
                .build();
        System.out.println("  ======>  zk service start");
        client.start();
    }

    @Override
    public void stop() {
        System.out.println("  =====> zk client stopped");
        client.close();
    }

    /**
     * 注册
     * @param service 服务
     * @param instance 实例名
     */
    @Override
    public void register(String service, String instance) {
        String servicePath = "/" + service;
        try {
            if (client.checkExists().forPath(servicePath) == null) {
                // 创建持久化节点模式服务
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance;
            System.out.println("  =====>  register to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 取消注册
     * @param service 服务
     * @param instance 实例名
     */
    @Override
    public void unregister(String service, String instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance;
            System.out.println("   =====>  zk service unregister");
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> fetchAll(String service) {
        return null;
    }
}
