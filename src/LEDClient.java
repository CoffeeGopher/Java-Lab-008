import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.util.concurrent.TimeUnit;

public class LEDClient {
    private ZContext zctx;
    private ZMQ.Socket zsocket;
    private Gson gson;
    private String connStr;
    private final String topic = "GPIO";

    private static final int[] OFF = {0, 0, 0};

    public LEDClient(String protocol, String host, int port) {
        zctx = new ZContext();
        zsocket = zctx.createSocket(SocketType.PUB);
        this.connStr = String.format("%s://%s:%d", protocol, "*", port);
        zsocket.bind(connStr);
        this.gson = new Gson();
    }

    public void send(int[] color) throws InterruptedException {
        JsonArray ja = gson.toJsonTree(color).getAsJsonArray();
        String message = topic + " " + ja.toString();
        System.out.println(message);
        zsocket.send(message);
    }

    public void blinkN(int[] color, int times, int miliseconds) throws  InterruptedException{
        for(int i=0; i<times; i++) {
            send(color);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
            send(LEDClient.OFF);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
        }
    }

    public void rainbowCycle(int cycles, int phases, int delay) throws InterruptedException {
        int[] color = {0,255,255};
        final float phasesMinusOne = phases - 1;
        for (int cyclesPassed = 0; cyclesPassed < cycles; cyclesPassed++) {
            for (int i = 0; i < 3; i++) {
                for (int ii = 0; ii < phases; ii++) {
                    color[i] = (int) ((255 / phasesMinusOne) * ii);
                    color[i == 2 ? 0 : i + 1] = 255 - (int) ((255 / phasesMinusOne) * ii);
                    send(color);
                    TimeUnit.MILLISECONDS.sleep(delay);
                }
            }
        }
        send(OFF);
    }

    public void close() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2); // Allow the socket a chance to flush.
        this.zsocket.close();
        this.zctx.close();
    }

    public static void main(String[] args) {
        LEDClient ledClient = new LEDClient("tcp", "localhost", 5001);
        try {
//            int[] color = {0, 0, 255};
//            ledClient.blinkN(color, 5, 1000);
            ledClient.rainbowCycle(10, 7, 70);
            ledClient.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}