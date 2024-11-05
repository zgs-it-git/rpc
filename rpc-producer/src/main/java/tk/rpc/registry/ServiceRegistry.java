package tk.rpc.registry;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: Zhu Guangshun
 * @Date: 2024-11-05 09:20
 **/
@Component
public class ServiceRegistry {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${registry.address}")
    private String registryAddress;

    private static final String ZK_REGISTRY_PATH = "/rpc";

    public void register(String data){
        if (data != null){
            ZkClient client = connectServer();
            if (client != null){
                addRootNode(client);
                createNode(client, data);
            }
        }
    }

    private void createNode(ZkClient client, String data) {
        String path = client.create(ZK_REGISTRY_PATH + "/provider", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        logger.info("创建zookeeper数据节点 ({} => {})", path, data);
    }

    private ZkClient connectServer(){
        return new ZkClient(registryAddress, 20000, 20000);
    }

    public void addRootNode(ZkClient client){
        boolean exists = client.exists(ZK_REGISTRY_PATH);
        if (!exists){
            client.createPersistent(ZK_REGISTRY_PATH);
            logger.info("创建zookeeper主节点 {}", ZK_REGISTRY_PATH);
        }
    }
}
