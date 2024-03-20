package as.rpc.demo.consumer;

import as.rpc.core.annotation.ASConsumer;
import as.rpc.core.consumer.ConsumerConfig;
import as.rpc.demo.api.Order;
import as.rpc.demo.api.OrderService;
import as.rpc.demo.api.User;
import as.rpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class AsRpcDemoConsumerApplication {
    @ASConsumer
    UserService userService;
    @ASConsumer
    OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(AsRpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
            User user = userService.findById(1);
            System.out.println("RPC RESULT userService.findById(1) = " + user);

            User user1 = userService.findById(1, "abner");
            System.out.println("RPC RESULT userService.findById(1, 'abner') = " + user1);

            String name = userService.getName();
            System.out.println("RPC RESULT userService.getName() = " + name);

            String name2 = userService.getName(1234);
            System.out.println("RPC RESULT userService.getName(1234) = " + name2);


//            Order order = orderService.findById(2);
//            System.out.println("RPC RESULT orderService.findById(2) = " + order);

//            Order order404 = orderService.findById(404);
//            System.out.println("RPC RESULT orderService.findById(404) = " + order404);
        };
    }

}
