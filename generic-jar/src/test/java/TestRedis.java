import com.kamikazejam.kamicommon.redis.RedisAPI;
import com.kamikazejam.kamicommon.redis.RedisChannel;
import com.kamikazejam.kamicommon.redis.RedisConnector;
import com.kamikazejam.kamicommon.redis.util.RedisConf;
import com.kamikazejam.kamicommon.util.JacksonUtil;
import lombok.SneakyThrows;

public class TestRedis {
    @SneakyThrows
    public static void main(String[] args) {
        RedisConf conf = RedisConf.of(
                System.getenv("LUXIOUS_REDIS_HOST"),
                Integer.parseInt(System.getenv("LUXIOUS_REDIS_PORT")),
                System.getenv("LUXIOUS_REDIS_PASS")
        );
        RedisAPI api = RedisConnector.getAPI(conf);

        MyObject myObject = new MyObject("Test", 123);

        RedisChannel<MyObject> channel = api.registerChannel("test_channel", MyObject.class);
        channel.addCallback((my) -> {
            System.out.println("Received: " + JacksonUtil.serialize(my));
            System.out.println("\tName: "  + my.getName());
            System.out.println("\tCount: " + my.getCount());
        });

        System.out.println("Sleeping...");
        Thread.sleep(2_000L);

        System.out.println("Publishing...");
        channel.publishSync(myObject);

        System.out.println("Sleeping...");
        Thread.sleep(2_000L);

        System.out.println("Shutting Down...");
        api.shutdown();
    }
}
