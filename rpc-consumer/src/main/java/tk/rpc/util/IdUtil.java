package tk.rpc.util;

public class IdUtil {

    private final static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static String getId(){
        return String.valueOf(idWorker.nextId());
    }
}
