package tk.rpc.netty.server;

import com.alibaba.fastjson.JSON;
import tk.rpc.entity.Request;
import tk.rpc.entity.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author: Zhu Guangshun
 * @Date: 2024-11-04 21:33
 **/
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final Map<String, Object> serviceMap;

    public NettyServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        logger.info("客户端连接成功!" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        logger.info("客户端断开连接!{}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = JSON.parseObject(msg.toString(), Request.class);

        if ("heartBeat".equals(request.getMethodName())){
            logger.info("客户端心跳信息..." + ctx.channel().remoteAddress());
        }else {
            logger.info("RPC客户端请求接口:" + request.getClassName() + " 方法名:" + request.getMethodName());
            Response response = new Response();
            response.setRequestId(request.getId());
            try {
                Object result = this.handler(request);
                response.setData(result);
            } catch (Throwable e) {
                e.printStackTrace();
                response.setCode(1);
                response.setError_msg(e.toString());
                logger.error("RPC Server handle request error", e);
            }
            ctx.writeAndFlush(response);
        }
    }

    private Object handler(Request request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = serviceMap.get(className);

        if (serviceBean != null){
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, getParameters(parameterTypes, parameters));
        }else {
            throw new Exception("未找到服务接口,请检查配置!:" + className + "#" + request.getMethodName());
        }
    }

    private Object[] getParameters(Class<?>[] parameterTypes, Object[] parameters){
        if (parameters == null || parameters.length == 0){
            return parameters;
        }else {
            Object[] new_parameters = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                new_parameters[i] = JSON.parseObject(parameters[i].toString(), parameterTypes[i]);
            }
            return new_parameters;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE){
                logger.info("客户端已超过60秒未读写数据,关闭连接.{}", ctx.channel().remoteAddress());
                ctx.channel().close();
            }else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info(cause.getMessage());
        ctx.close();
    }
}
