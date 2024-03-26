package as.rpc.demo.api;

import java.util.List;
import java.util.Map;

public interface UserService {
    User findById(int id);

    User findById(int id, String name);

    long getId(int id);

    long getId(User user);

    String getName();

    String getName(int id);

    long[] getIds();

    int[] getIds(int[] ids);

    // TODO
    List<User> getList(List<User> userList);

    Map<String, User> getMap(Map<String, User> userMap);

    // TODO
    Boolean getFlag(boolean flag);
}
