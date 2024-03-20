package as.rpc.demo.provider;

import as.rpc.core.annotation.ASProvider;
import as.rpc.demo.api.User;
import as.rpc.demo.api.UserService;
import org.springframework.stereotype.Service;

@Service
@ASProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        return new User(id, "AS-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, name + "-" + System.currentTimeMillis());
    }

    @Override
    public String getName() {
        return "hahaha";
    }

    @Override
    public String getName(int id) {
        return "hahahaha-" + id;
    }
}
