package as.rpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author abners.
 * @description Provider 元数据类
 * @date 2024/3/20 11:39
 */
@Data
public class ProviderMeta {
    Method method;
    String methodSign;
    Object serviceImpl;
}
