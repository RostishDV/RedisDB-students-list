import java.util.Random;

public class Main {
    private static RedisStorage redis;

    private static final String phoneStartSymbols = "+7(999)999-99-";

    //интервал между показыванием
    private static final long SLEEP = 1000;

    private static final String defaultMessage = "- На главной странице показываем пользователя";

    public static void main(String[] args) {
        redis = new RedisStorage();
        redis.init();
        int nextId;
        for (nextId = 1; nextId <= 20; nextId++){
            String nextPhone;
            if (nextId < 10) {
                nextPhone = phoneStartSymbols + "0" + nextId;
            } else {
                nextPhone = phoneStartSymbols + nextId;
            }
            addNewUser(nextId, nextPhone);
        }
        Random random = new Random();
        for(;;){
            if ((random.nextInt(10) + 1) <= 1){
                String user = redis.getNextUser(random.nextInt(20) + 1);
                System.out.println(">Пользователь" + user + " оплатил платную услугу");
                System.out.println(defaultMessage + user);
            } else {
                System.out.println(defaultMessage + redis.getNextUser());
            }
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static void addNewUser(int id, String phone){
        redis.addUser(id, phone);
    }

}
