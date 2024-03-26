package as.rpc.core.consumer;

import as.rpc.core.annotation.ASConsumer;
import as.rpc.core.api.LoadBalancer;
import as.rpc.core.api.Router;
import as.rpc.core.api.RpcContext;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consumer 启动类
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/19 21:43
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {
    ApplicationContext applicationContext;
    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RpcContext context = new RpcContext();
        context.setLoadBalancer(loadBalancer);
        context.setRouter(router);

        String urls = environment.getProperty("as-rpc.providers");
        if (Strings.isEmpty(urls)) {
            System.out.println("as-rpc.providers is empty.");
        }
        String[] providers = urls.split(",");

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotationField(bean.getClass());
            fields.forEach(f -> {
                System.out.println("======>>>" + f.getName());
                try {
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createConsumer(service, context, List.of(providers));
                    }
                    f.setAccessible(true);
                    f.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * 创建 Consumer
     *
     * @param service
     * @param context
     * @param providers
     * @return
     */
    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}, new ASInvocationHandler(service, context, providers));
    }

    /**
     * 获取类中所有带有 @ASConsumer 注解的 Field
     * @param aClass 类
     * @return field 数组
     */
    private List<Field> findAnnotationField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ASConsumer.class)) {
                    result.add(field);
                }
            }
            // class 为代理扩展类，不能直接获取原类中注解，通过 getSuperclass() 获取原类
            aClass = aClass.getSuperclass();
        }

        return result;
    }
}
