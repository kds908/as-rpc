package as.rpc.core.api;

import as.rpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @author abners.
 * @description
 * @date 2024/3/26 11:10
 */
public interface Router<T> {
    List<T> route(List<T> providers);

    Router<?> Default = p -> p;
}
