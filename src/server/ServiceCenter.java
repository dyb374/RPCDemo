package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务中心实现类
 * @author Andrew Dong
 * @date 2018/11/6 14:40
 */
public class ServiceCenter implements Server {


    //new一个线程池
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //HashMap用于存放方法
    private static final HashMap<String, Class> serviceRegistry = new HashMap<>();

    private static boolean isRunning = false;

    private static int port;

    public ServiceCenter(int port) {
        this.port = port;
    }

    @Override
    public void stop() {
        isRunning = false;
        executor.shutdown();
    }

    @Override
    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(port));//绑定ServerSocket要监听的端口
        System.out.println("start server");
        try {
            while (true){
                //监听客户端的TCP连接，接到TCP连接后将其封装成task，由线程池执行，从而实现多线程
                executor.execute(new ServiceTask(server.accept()));
            }
        } finally {
            server.close();
        }

    }

    @Override
    public void register(Class serviceInterface, Class impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPort() {
        return port;
    }

    //自定义的socket处理方法，需要继承Runnable接口
    private static class ServiceTask implements Runnable {
        Socket client = null;

        public ServiceTask(Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try {
                //将客户端发送的码反序列化成对象，反射调用服务实现者，获取执行结果
                inputStream = new ObjectInputStream(client.getInputStream());
                String serviceName = inputStream.readUTF();
                String methodName = inputStream.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) inputStream.readObject();
                Object[] arguments = (Object[]) inputStream.readObject();
                Class serviceClass = serviceRegistry.get(serviceName);
                if (serviceClass == null){
                    throw new ClassNotFoundException(serviceName + " not found");
                }
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                Object result = method.invoke(serviceClass.newInstance(), arguments);


                //将执行结果反序列化，通过socket发送给客户端
                outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.writeObject(result);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (client != null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
