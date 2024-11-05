package tk.rpc.configure;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * @author: Zhu Guangshun
 * @Date: 2024-11-05 12:51
 **/
public class RpcFactoryBean<T> implements FactoryBean<T> {
    private Class<T> rpcInterface;

    @Autowired
    RpcFactory<T> factory;

    public RpcFactoryBean() {
    }

    public RpcFactoryBean(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    @Override
    public T getObject() throws Exception {
        return getRpc();
    }

    private T getRpc() {
        return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class[]{rpcInterface}, factory);
    }

    @Override
    public Class<?> getObjectType() {
        return this.rpcInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
