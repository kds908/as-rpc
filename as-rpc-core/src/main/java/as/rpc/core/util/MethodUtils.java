package as.rpc.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author abners.
 * @description
 * @date 2024/3/20 11:27
 */
public class MethodUtils {
    /**
     * 方法签名处理
     *
     * @param method 方法
     * @return 方法签名
     */
    public static String methodSign(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        sb.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(e -> {
            sb.append("_").append(e.getCanonicalName());
        });
        return sb.toString();
    }

    /**
     * 方法签名处理
     *
     * @param method 方法
     * @param clazz 类
     * @return 方法签名
     */
    public static String methodSign(Method method, Class<?> clazz) {
        return null;
    }

    /**
     * 判断是否为本地方法，本地方法不代理
     *
     * @param method 方法
     * @return true/false
     */
    public static boolean checkLocalMethod(final Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    /**
     * 本地方法不代理
     * @param methodName 方法名
     * @return true/false
     */
    public static boolean checkLocalMethod(final String methodName) {
        return "toString".equals(methodName)
                || "hashCode".equals(methodName)
                || "notifyAll".equals(methodName)
                || "equals".equals(methodName)
                || "wait".equals(methodName)
                || "getClass".equals(methodName)
                || "notify".equals(methodName);
    }
}
