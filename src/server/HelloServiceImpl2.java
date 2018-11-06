package server;

/**
 * @author Andrew Dong
 * @date 2018/11/6 17:45
 */
public class HelloServiceImpl2 implements HelloService {
    @Override
    public String sayHi(String name) {
        return "Hello, " + name;
    }
}
