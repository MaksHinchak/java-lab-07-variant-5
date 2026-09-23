import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
// abstract — клас не створюють напряму; його абстрактні методи реалізують підкласи.
public abstract class BaseAI extends Thread {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    // new Клас(...) створює об’єкт і викликає його конструктор.
    private final Object gate = new Object(); // Монітор для узгодження паузи, відновлення й кроку руху.
    // ArrayList<T> — змінний список елементів типу T; <> після new виводить тип із контексту.
    private final ArrayList<Vehicle> vehicles = new ArrayList<>(); // Лише цей потік змінює позиції своїх машин.
    // List.of створює незмінюваний список.
    // volatile робить нове значення видимим іншим потокам; це не замок для складених операцій.
    private volatile List<Position> positions = List.of(); // volatile безпечно публікує повністю готовий незмінний знімок для EDT.
    private volatile boolean running = true;
    private boolean paused = false;
    protected BaseAI(String name, boolean truck, Organization owner, long seed) { // Спільна підготовка обох видів інтелекту.
        // super(...) — виклик конструктора батьківського класу.
        super(name);
        Random random = new Random(seed);
        // for (початок; умова; крок); i++ збільшує лічильник після проходу.
        for (int i = 1; i <= 8; i++) vehicles.add(new Vehicle(i, owner, truck, random)); // Створюємо колекцію з восьми машин потрібного типу.
        publish();
    }
    // abstract-метод не має тіла; конкретний підклас мусить надати реалізацію.
    protected abstract double speed(); // Кожен конкретний інтелект визначає швидкість свого виду машин.
    private void publish() { // Передаємо дані в основний потік GUI через незмінні повідомлення.
        // stream().map(Vehicle::snapshot).toList() викликає snapshot для кожної машини й збирає незмінюваний список.
        positions = vehicles.stream().map(Vehicle::snapshot).toList();
    }
    public List<Position> snapshots() { return positions; } // EDT читає останній повний знімок без блокування малювання.
    public void setPaused(boolean value) { // GUI або консоль керує паузою через цей метод.
        // synchronized(gate) — взаємне виключення: лише один потік утримує цей замок.
        synchronized (gate) { // Той самий монітор не дозволяє одночасно виконати крок і змінити паузу.
            paused = value;
            // notifyAll будить очікування на gate; продовження можливе після звільнення замка.
            gate.notifyAll();
        }
    }
    public void shutdown() { // Кооперативне завершення замість небезпечного Thread.stop.
        running = false;
        synchronized (gate) { gate.notifyAll(); } // Будимо також потік, який зараз чекає на паузі.
        // interrupt перериває sleep/wait винятком; не знищує потік примусово.
        interrupt();
    }
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override public final void run() { // start створює потік, а JVM викликає цей метод у ньому.
        long previous = System.nanoTime();
        try { // InterruptedException використовується як штатний сигнал завершення.
            while (running) { // Цикл триває до shutdown.
                synchronized (gate) { // Узгоджуємо весь крок руху з керуванням паузою.
                    // && — «і», || — «або»; праву умову перевіряють лише за потреби.
                    while (paused && running) { // while потрібен через можливі спонтанні пробудження wait.
                        // wait віддає замок і чекає; після пробудження бере його знову. Умову перевіряємо в while.
                        gate.wait();
                        previous = System.nanoTime();
                    }
                    if (!running) break; // Після пробудження перевіряємо, чи не надійшло завершення.
                    long now = System.nanoTime();
                    double dt = (now - previous) / 1_000_000_000.0;
                    previous = now;
                    // for (Тип елемент : колекція) — перебір елементів без індексу.
                    for (Vehicle vehicle : vehicles) vehicle.move(speed(), dt); // Оновлюємо кожну машину тільки у власному потоці.
                    publish();
                }
                // Thread.sleep чекає в мілісекундах; може кинути InterruptedException, замків не звільняє.
                Thread.sleep(16);
            }
        // InterruptedException очищає прапорець переривання; тут відновлюємо його перед виходом.
        // catch (Тип e) перехоплює виняток із try; e.getMessage() повертає його повідомлення.
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); } // Відновлюємо прапорець переривання перед виходом із run.
    }
}
