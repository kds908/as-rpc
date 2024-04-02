package as.rpc.core.registry;

import as.rpc.core.api.RegistryCenter;
import as.rpc.core.meta.InstanceMeta;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public void register(String service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            if (client.checkExists().forPath(servicePath) == null) {
                // 创建持久化节点模式服务
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance.toPath();
            System.out.println("  =====>  register to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance.toPath();
            System.out.println("   =====>  zk service unregister");
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(String service) {
        String servicePath = "/" + service;
        try {
            // 获取所有子节点
            List<String> nodes = client.getChildren().forPath(servicePath);
            System.out.println(" =====> fetch all from zk: " + servicePath);
            nodes.forEach(System.out::println);
            return mapInstance(nodes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<InstanceMeta> mapInstance(List<String> nodes) {
        return nodes.stream().map(n -> {
            String[] str = n.split("_");
            return InstanceMeta.http(str[0], Integer.valueOf(str[1]));
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(String service, ChangedListener listener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service)
                .setCacheData(true)
                .setMaxDepth(2)
                .build();
        cache.getListenable().addListener((curator, event) -> {
            // 有任何节点变动，这里就会执行
            System.out.println("zk subscribe event: " + event);
            List<InstanceMeta> nodes = fetchAll(service);
            listener.fire(new Event(nodes));
        });
        cache.start();
    }
}
