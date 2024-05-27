package as.rpc.core.provider;

import as.rpc.core.api.ASRpcException;
import as.rpc.core.api.RpcRequest;
import as.rpc.core.api.RpcResponse;
import as.rpc.core.meta.ProviderMeta;
import as.rpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Description for this class
 *
 * <p>
 * {@code @author: } abners.
 * <p>
 * {@code @date: } 2024/3/29 15:04
 */
public class ProviderInvoker {
    private MultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBootstrap bootstrap) {
        this.skeleton = bootstrap.getSkeleton();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        String methodSign = request.getMethodSign();
        RpcResponse<Object> response = new RpcResponse<>();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, methodSign);
            Method method = meta.getMethod();
            // 对参数进行类型转换
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            response.setStatus(true);
            response.setData(result);
        } catch (InvocationTargetException e) {
            response.setEx(new ASRpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            response.setEx(new ASRpcException(e.getMessage()));
        }
        return response;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] actualArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actualArgs[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actualArgs;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream()
                .filter(pm -> methodSign.equals(pm.getMethodSign()))
                .findFirst()
                .orElse(null);
    }
}
