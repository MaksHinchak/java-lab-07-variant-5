import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
// abstract — клас не створюють напряму; його абстрактні методи реалізують підкласи.
public abstract class BaseAI extends Thread {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    private final Object gate = new Object();
    // ArrayList<T> — змінний список елементів типу T; <> після new виводить тип із контексту.
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    // List.of створює незмінюваний список.
    // volatile робить нове значення видимим іншим потокам; це не замок для складених операцій.
    private volatile List<Position> positions = List.of();
    private volatile boolean running = true;
    private boolean paused = false;
    protected BaseAI(String name, boolean truck, Organization owner, long seed) {
        // super(...) — виклик конструктора батьківського класу.
        super(name);
        Random random = new Random(seed);
        // for (початок; умова; крок); i++ збільшує лічильник після проходу.
        for (int i = 1; i <= 8; i++) vehicles.add(new Vehicle(i, owner, truck, random));
        publish();
    }
    // abstract-метод не має тіла; конкретний підклас мусить надати реалізацію.
    protected abstract double speed();
    private void publish() {
        // stream().map(Vehicle::snapshot).toList() викликає snapshot для кожної машини й збирає незмінюваний список.
        positions = vehicles.stream().map(Vehicle::snapshot).toList();
    }
    public List<Position> snapshots() { return positions; }
    public void setPaused(boolean value) {
        // synchronized(gate) — взаємне виключення: лише один потік утримує цей замок.
        synchronized (gate) {
            paused = value;
            // notifyAll будить очікування на gate; продовження можливе після звільнення замка.
            gate.notifyAll();
        }
    }
    public void shutdown() {
        running = false;
        synchronized (gate) { gate.notifyAll(); }
        // interrupt перериває sleep/wait винятком; не знищує потік примусово.
        interrupt();
    }
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override public final void run() {
        long previous = System.nanoTime();
        try {
            while (running) {
                synchronized (gate) {
                    while (paused && running) {
                        // wait віддає замок і чекає; після пробудження бере його знову. Умову перевіряємо в while.
                        gate.wait();
                        previous = System.nanoTime();
                    }
                    if (!running) break;
                    long now = System.nanoTime();
                    double dt = (now - previous) / 1_000_000_000.0;
                    previous = now;
                    // for (Тип елемент : колекція) — перебір елементів без індексу.
                    for (Vehicle vehicle : vehicles) vehicle.move(speed(), dt);
                    publish();
                }
                // Thread.sleep чекає в мілісекундах; може кинути InterruptedException, замків не звільняє.
                Thread.sleep(16);
            }
        // InterruptedException очищає прапорець переривання; тут відновлюємо його перед виходом.
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
