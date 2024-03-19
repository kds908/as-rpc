package as.rpc.demo.provider;

import as.rpc.core.annotation.ASProvider;
import as.rpc.demo.api.Order;
import as.rpc.demo.api.OrderService;
import org.springframework.stereotype.Service;

/**
 * Description for this class
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/19 0:06
 */
@Service
@ASProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {
        if (id == 404) {
            throw new RuntimeException("404 Exception");
        }
        return new Order(id.longValue(), 15.88D);
    }
}
