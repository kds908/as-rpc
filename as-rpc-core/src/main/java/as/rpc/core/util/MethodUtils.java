package as.rpc.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     * 获取类中所有带有 @ASConsumer 注解的 Field
     * @param aClass 类
     * @param annotationClass
     * @return field 数组
     */
    public static List<Field> findAnnotationField(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }
            // class 为代理扩展类，不能直接获取原类中注解，通过 getSuperclass() 获取原类
            aClass = aClass.getSuperclass();
        }

        return result;
    }
}
