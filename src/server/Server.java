package server;

import java.io.IOException;

/**
 * 服务中心
 * @author Andrew Dong
 * @date 2018/11/6 14:39
 */
public interface Server {

    void stop();

    void start() throws IOException;


    /**
     * 注册服务，参数一为服务，参数二为该服务实现类
     * @param serviceInterface
     * @param impl
     */
    void register(Class serviceInterface, Class impl);

    boolean isRunning();

    int getPort();
}
