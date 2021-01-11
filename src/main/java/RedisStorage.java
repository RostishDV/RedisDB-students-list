import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;

import java.util.Date;

import static java.lang.System.out;

public class RedisStorage {
    // Объект для работы с Redis
    private RedissonClient redisson;

    // Объект для работы с ключами
    private RKeys rKeys;

    // Объект для хранения номеров
    private RMap<Integer, String> usersPhones;

    // Объект для работы с Sorted Set'ом
    private RScoredSortedSet<String> usersQueue;

    private final static String KEY_QUEUE = "USERS_QUEUE";
    private final static String KEY_PHONES = "USERS_PHONES";

    private double getTs(){
        return new Date().getTime() / 1000;
    }
    public void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            out.println("Не удалось подключиться к Redis");
            out.println(Exc.getMessage());
        }
        rKeys = redisson.getKeys();
        usersQueue = redisson.getScoredSortedSet(KEY_QUEUE);
        usersPhones = redisson.getMap(KEY_PHONES);
        rKeys.delete(KEY_QUEUE);
        rKeys.delete(KEY_PHONES);
    }

    public void shutdown() {
        redisson.shutdown();
    }

    public void addUser(int userId, String userPhone){
        usersQueue.add(getTs(), String.valueOf(userId));
        usersPhones.put(userId, userPhone);
    }

    public String getNextUser(int userId){
        if (usersPhones.containsKey(userId)){
            usersQueue.add(getTs(), String.valueOf(userId));
            return usersPhones.get(userId);
        } else {
            return "phone not found";
        }
    }

    public String getNextUser(){
        int userId = Integer.parseInt(usersQueue.first());
        usersQueue.add(getTs(), String.valueOf(userId));
        if (usersPhones.containsKey(userId)){
            return usersPhones.get(userId);
        } else {
            return "number of " + userId + " user not found";
        }
    }
}
