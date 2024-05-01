package as.rpc.core.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * Description for this class
 *
 * <p>
 *
 * @author: Abner Song
 * <p>
 * @date: 2024/5/1 19:46
 */
public class MockUtils {
    public static Object mock(Class<?> type) {
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return 1;
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return 10000L;
        }
        if (Number.class.isAssignableFrom(type)) {
            return 1;
        }
        if (String.class.equals(type)) {
            return "this_is_a_mock_string";
        }
        return mockPojo(type);
    }

    @SneakyThrows
    private static Object mockPojo(Class<?> type) {
        Object result = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            Object fieldValue = mock(fieldType);
            field.set(result, fieldValue);
        }
        return result;
    }
}
