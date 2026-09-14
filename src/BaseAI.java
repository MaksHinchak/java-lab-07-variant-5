import java.util.ArrayList; // Робочий потік керує власною змінною колекцією машин.
import java.util.List; // Публікуємо знімки через інтерфейс List.
import java.util.Random; // Відтворюваний генератор потрібен для демонстрації й тестування.
public abstract class BaseAI extends Thread { // Абстрактний інтелект виконаний окремим потоком згідно з умовою.
    private final Object gate = new Object(); // Монітор для узгодження паузи, відновлення й кроку руху.
    private final ArrayList<Vehicle> vehicles = new ArrayList<>(); // Лише цей потік змінює позиції своїх машин.
    private volatile List<Position> positions = List.of(); // volatile безпечно публікує повністю готовий незмінний знімок для EDT.
    private volatile boolean running = true; // Прапорець завершення має бути видимим робочому потоку.
    private boolean paused = false; // Прапорець паузи читається й змінюється лише під gate.
    protected BaseAI(String name, boolean truck, Organization owner, long seed) { // Спільна підготовка обох видів інтелекту.
        super(name); // Задаємо зрозуміле ім'я Java-потоку.
        Random random = new Random(seed); // Однакове зерно відтворює початкові дані.
        for (int i = 1; i <= 8; i++) vehicles.add(new Vehicle(i, owner, truck, random)); // Створюємо колекцію з восьми машин потрібного типу.
        publish(); // Початковий знімок доступний ще до Thread.start.
    }
    protected abstract double speed(); // Кожен конкретний інтелект визначає швидкість свого виду машин.
    private void publish() { // Передаємо дані в основний потік GUI через незмінні повідомлення.
        positions = vehicles.stream().map(Vehicle::snapshot).toList(); // toList створює немодифікований список нових Position.
    }
    public List<Position> snapshots() { return positions; } // EDT читає останній повний знімок без блокування малювання.
    public void setPaused(boolean value) { // GUI або консоль керує паузою через цей метод.
        synchronized (gate) { // Той самий монітор не дозволяє одночасно виконати крок і змінити паузу.
            paused = value; // Зберігаємо запитаний стан.
            gate.notifyAll(); // Будимо потік, щоб він повторно перевірив умову очікування.
        }
    }
    public void shutdown() { // Кооперативне завершення замість небезпечного Thread.stop.
        running = false; // Просимо основний цикл завершитися.
        synchronized (gate) { gate.notifyAll(); } // Будимо також потік, який зараз чекає на паузі.
        interrupt(); // Перериваємо sleep або wait для швидкого закриття.
    }
    @Override public final void run() { // start створює потік, а JVM викликає цей метод у ньому.
        long previous = System.nanoTime(); // Початковий момент для розрахунку dt.
        try { // InterruptedException використовується як штатний сигнал завершення.
            while (running) { // Цикл триває до shutdown.
                synchronized (gate) { // Узгоджуємо весь крок руху з керуванням паузою.
                    while (paused && running) { // while потрібен через можливі спонтанні пробудження wait.
                        gate.wait(); // Потік засинає та звільняє монітор, не витрачаючи CPU на активне очікування.
                        previous = System.nanoTime(); // Час паузи не додається до переміщення після відновлення.
                    }
                    if (!running) break; // Після пробудження перевіряємо, чи не надійшло завершення.
                    long now = System.nanoTime(); // Поточний монотонний час.
                    double dt = (now - previous) / 1_000_000_000.0; // Фактичний проміжок часу у секундах.
                    previous = now; // Оновлюємо відлік наступного кроку.
                    for (Vehicle vehicle : vehicles) vehicle.move(speed(), dt); // Оновлюємо кожну машину тільки у власному потоці.
                    publish(); // Після завершення всього кроку атомарно публікуємо новий список.
                }
                Thread.sleep(16); // Обмежуємо частоту кроків, звільняючи процесор між ними.
            }
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); } // Відновлюємо прапорець переривання перед виходом із run.
    }
}
