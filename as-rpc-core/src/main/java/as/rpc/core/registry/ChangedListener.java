package as.rpc.core.registry;

/**
 * Description for this class
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/27 0:44
 */
public interface ChangedListener {
    void fire(Event event);
}
