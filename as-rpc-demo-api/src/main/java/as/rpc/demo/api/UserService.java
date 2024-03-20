package as.rpc.demo.api;

public interface UserService {
    User findById(int id);

    User findById(int id, String name);

    String getName();

    String getName(int id);
}
