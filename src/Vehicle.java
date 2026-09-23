import java.util.Random;
// final class — від цього класу не можна успадковуватися.
public final class Vehicle {
    // static final — спільна константа класу, доступна без створення об’єкта.
    public static final int WIDTH = 760;
    public static final int HEIGHT = 440;
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    private final int id;
    private final Organization owner;
    private double x;
    private double y;
    private final double targetX;
    private final double targetY;
    public Vehicle(int id, Organization owner, boolean truck, Random random) { // Прапорець truck визначає цільову чверть.
        // this — поточний об’єкт; this.поле відрізняє поле від однойменного параметра.
        this.id = id;
        this.owner = owner;
        x = random.nextDouble() * WIDTH;
        y = random.nextDouble() * HEIGHT;
        // умова ? a : b — вибір значення: a, якщо true, інакше b.
        // && — «і», || — «або»; праву умову перевіряють лише за потреби.
        boolean inside = truck ? x <= WIDTH / 2.0 && y <= HEIGHT / 2.0 : x >= WIDTH / 2.0 && y >= HEIGHT / 2.0;
        double offsetX = truck ? 0 : WIDTH / 2.0;
        double offsetY = truck ? 0 : HEIGHT / 2.0;
        targetX = inside ? x : offsetX + random.nextDouble() * WIDTH / 2.0;
        targetY = inside ? y : offsetY + random.nextDouble() * HEIGHT / 2.0;
    }
    public void move(double speed, double seconds) { // Пересуваємо машину прямолінійно на відстань V*dt.
        // Double.isFinite відкидає NaN та ±Infinity; ! заперечує перевірку.
        // throw передає помилку в catch; new створює об’єкт винятку з повідомленням.
        if (!Double.isFinite(speed) || speed < 0 || !Double.isFinite(seconds) || seconds < 0) throw new IllegalArgumentException("Швидкість і час мають бути скінченними та невід'ємними."); // Забороняємо некоректний крок інтегрування.
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.hypot(dx, dy);
        if (distance == 0) return; // Прибуття означає остаточну зупинку.
        double step = speed * seconds;
        if (step >= distance) { // Не дозволяємо проскочити кінцеву точку.
            x = targetX;
            y = targetY;
        } else { // Залишок шляху більший за один крок.
            x += dx / distance * step;
            y += dy / distance * step;
        }
    }
    // new Клас(...) створює об’єкт і викликає його конструктор.
    public Position snapshot() { return new Position(id, x, y, targetX, targetY, owner.getName(), x == targetX && y == targetY); } // Створюємо незалежне незмінне повідомлення для GUI.
}
