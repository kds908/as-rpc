package as.rpc.demo.provider;

import as.rpc.core.annotation.ASProvider;
import as.rpc.demo.api.User;
import as.rpc.demo.api.UserService;
import org.springframework.stereotype.Service;

@Service
@ASProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(Integer id) {
        return new User(id, "AS-" + System.currentTimeMillis());
    }
}
