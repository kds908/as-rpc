package as.rpc.demo.api;

public interface UserService {
    User findById(int id);

    User findById(int id, String name);

    long getId(int id);

    long getId(User user);

    String getName();

    String getName(int id);

    long[] getIds();

    int[] getIds(int[] ids);
}
