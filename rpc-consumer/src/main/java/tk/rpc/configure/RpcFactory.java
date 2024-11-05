package tk.rpc.configure;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import tk.rpc.entity.Request;
import tk.rpc.entity.Response;
import tk.rpc.netty.client.NettyClient;
import tk.rpc.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author: Zhu Guangshun
 * @Date: 2024-11-05 11:06
 **/
@Component
public class RpcFactory<T> implements InvocationHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NettyClient client;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        request.setId(IdUtil.getId());

        Object result = client.send(request);
        Class<?> returnType = method.getReturnType();

        Response response = JSON.parseObject(result.toString(), Response.class);
        if (response.getCode() == 1) {
            throw new Exception(response.getError_msg());
        }
        if (returnType.isPrimitive() || String.class.isAssignableFrom(returnType)){
            return response.getData();
        }else if(Collection.class.isAssignableFrom(returnType)){
            return JSONArray.parseArray(response.getData().toString(), Object.class);
        }else if (Map.class.isAssignableFrom(returnType)) {
            return JSON.parseObject(response.getData().toString(), Map.class);
        }else {
            Object data = response.getData();
            return JSONObject.parseObject(data.toString(), returnType);
        }
    }
}
