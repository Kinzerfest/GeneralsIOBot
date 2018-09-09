import pl.joegreen.sergeants.simulator.SimulatorConfiguration;
import pl.joegreen.sergeants.simulator.SimulatorFactory;

import java.io.File;

public class Simulate {
    public static void main(String[] args) {

        SimulatorFactory.of(SimulatorFactory.create2PlayerMap(),
                SimulatorConfiguration.configuration()
                        .withMaxTurns(2000)
                        .withReplayFile(new File("C:\\Users\\Robotics\\Desktop\\Replays.myReplay.json")),
                Bots.ZestyBot::new, Bots.DegenerateBot::new).start();
    }
}
