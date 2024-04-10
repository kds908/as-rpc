package as.rpc.core.provider;

import as.rpc.core.annotation.ASProvider;
import as.rpc.core.api.RegistryCenter;
import as.rpc.core.meta.InstanceMeta;
import as.rpc.core.meta.ProviderMeta;
import as.rpc.core.meta.ServiceMeta;
import as.rpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * Provider 启动类
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/18 22:09
 */
@Slf4j
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    ApplicationContext applicationContext;
    RegistryCenter rc;
    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private InstanceMeta instance;
    @Value("${server.port}")
    private int port;
    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        // 获取 @ASProvider 注解的 Beans
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(ASProvider.class);
        providers.forEach((i, e) -> log.info(i));
        providers.values().forEach(this::generateInterface);
    }

    /**
     * 启动服务注册
     */
    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMeta.http(ip, port);
        rc = applicationContext.getBean(RegistryCenter.class);
        rc.start();
        // skeleton 中即为要注册的内容
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        log.info(" =====> unregister all services");
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    /**
     * 注册到注册中心
     * @param service 服务
     */
    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(service)
                .build();
        rc.register(serviceMeta, instance);
    }

    /**
     * 从注册中心取消注册
     * @param service 服务
     */
    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(service)
                .build();
        rc.unregister(serviceMeta, instance);
    }

    private void generateInterface(Object impl) {
        Arrays.stream(impl.getClass().getInterfaces()).forEach(
                service -> {
                    Method[] methods = service.getMethods();
                    for (Method method : methods) {
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(service, impl, method);
                    }
                }
        );
    }

    private void createProvider(Class<?> service, Object impl, Method method) {
        ProviderMeta providerMeta = ProviderMeta.builder()
                .method(method)
                .serviceImpl(impl)
                .methodSign(MethodUtils.methodSign(method))
                .build();
        log.info("create a provider : " + providerMeta);
        skeleton.add(service.getCanonicalName(), providerMeta);
    }



    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
