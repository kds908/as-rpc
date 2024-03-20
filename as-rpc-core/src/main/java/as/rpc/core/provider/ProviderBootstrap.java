package as.rpc.core.provider;

import as.rpc.core.annotation.ASProvider;
import as.rpc.core.api.RpcRequest;
import as.rpc.core.api.RpcResponse;
import as.rpc.core.meta.ProviderMeta;
import as.rpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Provider 启动类
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/18 22:09
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    // 多值Map
    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    @PostConstruct
    public void start() {
        // 获取 @ASProvider 注解的 Beans
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(ASProvider.class);
        providers.forEach((i, e) -> System.out.println(i));
        providers.values().forEach(this::generateInterface);
    }

    private void generateInterface(Object e) {
        Class<?> itf = e.getClass().getInterfaces()[0];
        Method[] methods = itf.getMethods();
        for (Method method : methods) {
            if (MethodUtils.checkLocalMethod(method)) {
                continue;
            }
            createProvider(itf, e, method);
        }
    }

    private void createProvider(Class<?> itf, Object e, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setServiceImpl(e);
        meta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println("create a provider : " + meta);
        skeleton.add(itf.getCanonicalName(), meta);
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        String methodSign = request.getMethodSign();
        RpcResponse<Object> response = new RpcResponse<>();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, methodSign);
            Method method = meta.getMethod();
            Object result = method.invoke(meta.getServiceImpl(), request.getArgs());
            response.setStatus(true);
            response.setData(result);
        } catch (InvocationTargetException e) {
            response.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            response.setEx(new RuntimeException(e.getMessage()));
        }
        return response;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream()
                .filter(pm -> methodSign.equals(pm.getMethodSign()))
                .findFirst()
                .orElse(null);
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
