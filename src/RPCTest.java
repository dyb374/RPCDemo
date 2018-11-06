import client.RPCClient;
import server.*;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Andrew Dong
 * @date 2018/11/6 17:31
 */
public class RPCTest {

    public static void main(String[] args) throws IOException {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Server serviceServer = new ServiceCenter(8088);//new服务中心，监听8088端口
                    serviceServer.register(HelloService.class, HelloServiceImpl2.class);//在服务中心注册服务
                    serviceServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //调用远程服务
        HelloService service = RPCClient.getRemoteProxyObj(HelloService.class, new InetSocketAddress("localhost", 8088));
        System.out.println(service.sayHi("test"));
    }
}
