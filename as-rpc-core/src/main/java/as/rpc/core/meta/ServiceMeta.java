package as.rpc.core.meta;

import lombok.Builder;
import lombok.Data;

/**
 * 描述服务元数据
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/4/7 21:34
 */
@Data
@Builder
public class ServiceMeta {
    private String app;
    private String namespace;
    private String env;
    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }
}
