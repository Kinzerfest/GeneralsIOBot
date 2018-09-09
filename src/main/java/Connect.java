import Bots.*;
import pl.joegreen.sergeants.framework.Games;
import pl.joegreen.sergeants.framework.queue.QueueConfiguration;
import pl.joegreen.sergeants.framework.user.UserConfiguration;

public class Connect {

    public static void main(String[] args) {

        Games.play(1, ZestyBot::new,
                QueueConfiguration.freeForAll(false), UserConfiguration.random())
                .forEach(System.out::println);

    }

}
