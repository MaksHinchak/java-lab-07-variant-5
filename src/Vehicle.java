import java.util.Random;
public final class Vehicle {
    public static final int WIDTH = 760;
    public static final int HEIGHT = 440;
    private final int id;
    private final Organization owner;
    private double x;
    private double y;
    private final double targetX;
    private final double targetY;
    public Vehicle(int id, Organization owner, boolean truck, Random random) {
        this.id = id;
        this.owner = owner;
        x = random.nextDouble() * WIDTH;
        y = random.nextDouble() * HEIGHT;
        boolean inside = truck ? x <= WIDTH / 2.0 && y <= HEIGHT / 2.0 : x >= WIDTH / 2.0 && y >= HEIGHT / 2.0;
        double offsetX = truck ? 0 : WIDTH / 2.0;
        double offsetY = truck ? 0 : HEIGHT / 2.0;
        targetX = inside ? x : offsetX + random.nextDouble() * WIDTH / 2.0;
        targetY = inside ? y : offsetY + random.nextDouble() * HEIGHT / 2.0;
    }
    public void move(double speed, double seconds) {
        if (!Double.isFinite(speed) || speed < 0 || !Double.isFinite(seconds) || seconds < 0) throw new IllegalArgumentException("Швидкість і час мають бути скінченними та невід'ємними.");
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.hypot(dx, dy);
        if (distance == 0) return;
        double step = speed * seconds;
        if (step >= distance) {
            x = targetX;
            y = targetY;
        } else {
            x += dx / distance * step;
            y += dy / distance * step;
        }
    }
    public Position snapshot() { return new Position(id, x, y, targetX, targetY, owner.getName(), x == targetX && y == targetY); }
}
