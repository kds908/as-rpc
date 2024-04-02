package as.rpc.core.registry;

import as.rpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Description for this class
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/27 0:52
 */
@Data
@AllArgsConstructor
public class Event {
    List<InstanceMeta> data;
}
