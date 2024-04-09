package as.rpc.demo.consumer;

import as.rpc.demo.provider.AsRpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class AsRpcDemoConsumerApplicationTests {

    static ConfigurableApplicationContext context;
    @BeforeAll
    static void init() {
        context = SpringApplication.run(AsRpcDemoProviderApplication.class,
                "--server.port=8085",
                "--logging.level.as.rpc=debug");
    }
    @Test
    void contextLoads() {
        System.out.println("===> aaaa .....");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context);
    }

}
