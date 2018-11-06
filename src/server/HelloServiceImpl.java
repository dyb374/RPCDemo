package server;

/**
 * HelloService实现类
 * @author Andrew Dong
 * @date 2018/11/6 14:43
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHi(String name) {
        return "Hi, " + name;
    }
}
