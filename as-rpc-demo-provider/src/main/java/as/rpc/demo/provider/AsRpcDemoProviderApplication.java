package as.rpc.demo.provider;

import as.rpc.core.api.RpcRequest;
import as.rpc.core.api.RpcResponse;
import as.rpc.core.provider.ProviderBootstrap;
import as.rpc.core.provider.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class AsRpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsRpcDemoProviderApplication.class, args);
    }

    @Autowired
    ProviderBootstrap providerBootstrap;

    // 使用 HTTP + JSON 实现序列化和通信
    @RequestMapping("/")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerBootstrap.invoke(request);
    }

    @Bean
    ApplicationRunner providerRun() {
        return e -> {
            RpcRequest request = new RpcRequest();
            request.setService("as.rpc.demo.api.UserService");
            request.setMethod("findById");
            request.setArgs(new Object[]{100});
            RpcResponse<Object> response = invoke(request);
            System.out.println("return : " + response.getData());
        };
    }
}
