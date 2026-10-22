import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public abstract class BaseAI extends Thread {
    private final Object gate = new Object();
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private volatile List<Position> positions = List.of();
    private volatile boolean running = true;
    private boolean paused = false;
    protected BaseAI(String name, boolean truck, Organization owner, long seed) {
        super(name);
        Random random = new Random(seed);
        for (int i = 1; i <= 8; i++) vehicles.add(new Vehicle(i, owner, truck, random));
        publish();
    }
    protected abstract double speed();
    private void publish() {
        positions = vehicles.stream().map(Vehicle::snapshot).toList();
    }
    public List<Position> snapshots() { return positions; }
    public void setPaused(boolean value) {
        synchronized (gate) {
            paused = value;
            gate.notifyAll();
        }
    }
    public void shutdown() {
        running = false;
        synchronized (gate) { gate.notifyAll(); }
        interrupt();
    }
    @Override public final void run() {
        long previous = System.nanoTime();
        try {
            while (running) {
                synchronized (gate) {
                    while (paused && running) {
                        gate.wait();
                        previous = System.nanoTime();
                    }
                    if (!running) break;
                    long now = System.nanoTime();
                    double dt = (now - previous) / 1_000_000_000.0;
                    previous = now;
                    for (Vehicle vehicle : vehicles) vehicle.move(speed(), dt);
                    publish();
                }
                Thread.sleep(16);
            }
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
