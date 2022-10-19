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

    public void rainbowCycle(int cycles) throws InterruptedException {
        int[] color = {0,255,255};
        for (int cyclesPassed = 0; cyclesPassed < cycles; cyclesPassed++) {
            for (int i = 0; i < 3; i++) {
                for (int ii = 0; ii < 255; ii++) {
                    color[i]++;
                    color[i == 2 ? 0 : i + 1]--;
                    send(color);
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            }
        }
        send(OFF);
    }

    public void displayMorseCode(String message, int ditTime) throws InterruptedException {
        displayMorseCode(message, ditTime, new int[] {255,255,255});
    }

    public void displayMorseCode(String message, int ditTime, int[] color) throws InterruptedException {

        for (char c : message.toCharArray()) {
            if (c == ' ') {
                // word space
                TimeUnit.MILLISECONDS.sleep(ditTime * 7);
                continue;
            }

            MorseCode morse = MorseCode.fromChar(c);
            if (morse == null) {
                // skip if char is not a valid morse code
                continue;
            }

            // process char
            for(int morseValue : morse.getMorseCodeArray()) {
                if (morseValue == 0) {
                    // dit
                    send(color);
                    TimeUnit.MILLISECONDS.sleep(ditTime);
                    send(OFF);
                    TimeUnit.MILLISECONDS.sleep(ditTime);
                } else {
                    send(color);
                    TimeUnit.MILLISECONDS.sleep(ditTime * 3);
                }
            }
            TimeUnit.MILLISECONDS.sleep(ditTime * 3);

        }

        send(OFF);

    }

    public void close() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2); // Allow the socket a chance to flush.
        this.zsocket.close();
        this.zctx.close();
    }

    public static void main(String[] args) {
        LEDClient ledClient = new LEDClient("tcp", "192.168.1.117", 5001);
        try {
//            int[] color = {0, 0, 255};
//            ledClient.blinkN(color, 5, 1000);
            ledClient.rainbowCycle(3);
            TimeUnit.SECONDS.sleep(3);
            ledClient.displayMorseCode("SOS", 250);
            TimeUnit.SECONDS.sleep(3);
            ledClient.displayMorseCode("hello world", 250);
            ledClient.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}