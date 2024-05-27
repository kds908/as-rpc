package as.rpc.core.registry.zk;

import as.rpc.core.api.ASRpcException;
import as.rpc.core.api.RegistryCenter;
import as.rpc.core.meta.InstanceMeta;
import as.rpc.core.meta.ServiceMeta;
import as.rpc.core.registry.ChangedListener;
import as.rpc.core.registry.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

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
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {
    @Value("${as-rpc.zk-server}")
    private String zkServer;
    @Value("${as-rpc.zk-root}")
    private String zkRoot;
    //
    private CuratorFramework client = null;

    @Override
    public void start() {
        // 定义 Retry 策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .namespace(zkRoot)
                .retryPolicy(retryPolicy)
                .build();
        log.info("  ======>  zk service starting, zkServer=[" + zkServer + ", zkRoot=" + zkRoot + "]");
        client.start();
    }

    @Override
    public void stop() {
        log.info("  =====> zk client stopped");
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            if (client.checkExists().forPath(servicePath) == null) {
                // 创建持久化节点模式服务
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info("  =====>  register to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
        } catch (Exception e) {
            throw new ASRpcException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info("   =====>  zk service unregister");
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new ASRpcException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service;
        try {
            // 获取所有子节点
            List<String> nodes = client.getChildren().forPath(servicePath);
            log.info(" =====> fetch all from zk: " + servicePath);
            nodes.forEach(System.out::println);
            return mapInstance(nodes);
        } catch (Exception e) {
            throw new ASRpcException(e);
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
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service)
                .setCacheData(true)
                .setMaxDepth(2)
                .build();
        cache.getListenable().addListener((curator, event) -> {
            // 有任何节点变动，这里就会执行
            log.info("zk subscribe event: " + event);
            List<InstanceMeta> nodes = fetchAll(service);
            listener.fire(new Event(nodes));
        });
        cache.start();
    }
}
