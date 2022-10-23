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
        rainbowCycle(cycles, phases, delay, 0, 255);
    }

    public void rainbowCycle(int cycles, int phases, int delay, int minimum, int maximum) throws InterruptedException {
        int[] color = {minimum,maximum,maximum};
        final float phasesMinusOne = phases - 1;
        for (int cyclesPassed = 0; cyclesPassed < cycles; cyclesPassed++) {
            for (int i = 0; i < 3; i++) {
                for (int ii = 0; ii < phases; ii++) {
                    color[i] = minimum + (int) (((maximum - minimum) / phasesMinusOne) * ii);
                    color[i == 2 ? 0 : i + 1] = maximum - (int) (((maximum - minimum) / phasesMinusOne) * ii);
                    send(color);
                    TimeUnit.MILLISECONDS.sleep(delay);
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
                    send(OFF);
                    TimeUnit.MILLISECONDS.sleep(ditTime);
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
        LEDClient ledClient = new LEDClient("tcp", "localhost", 5001);
        try {
            // display "SOS" in red
            ledClient.displayMorseCode("SOS", 500, new int[] {255, 0, 0});
            // show some pretty rainbow colors
            ledClient.rainbowCycle(10, 5, 50, 0, 255);
            ledClient.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}