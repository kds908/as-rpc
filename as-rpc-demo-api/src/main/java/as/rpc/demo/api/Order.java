package as.rpc.demo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/19 0:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    Long id;
    Double amount;
}
