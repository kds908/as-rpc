package as.rpc.core.meta;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 描述 Provider 映射关系
 *
 * @author abners.
 * @description Provider 元数据类
 * @date 2024/3/20 11:39
 */
@Data
@Builder
public class ProviderMeta {
    Method method;
    String methodSign;
    Object serviceImpl;
}
