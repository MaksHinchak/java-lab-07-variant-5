import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Лабораторна робота № 7. Варіант 5 — вантажні та легкові машини.
public class Main {
    // Завдання 2.4–2.5. Окремі елементи керування для кожного потоку.
    private static JPanel controls(String title, BaseAI ai) {
        JPanel panel = new JPanel();
        JButton pause = new JButton("Пауза");
        JButton resume = new JButton("Продовжити");
        JLabel state = new JLabel("Працює");
        JComboBox<Integer> priority = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        priority.setSelectedItem(Thread.NORM_PRIORITY);

        // Пункт 2.4. Пауза й відновлення вибраного автопарку.
        pause.addActionListener(event -> { ai.setPaused(true); state.setText("Пауза"); });
        resume.addActionListener(event -> { ai.setPaused(false); state.setText("Працює"); });

        // Пункт 2.5. Пріоритет потоку задається від 1 до 10.
        priority.addActionListener(event -> ai.setPriority((Integer) priority.getSelectedItem()));

        panel.add(new JLabel(title));
        panel.add(pause);
        panel.add(resume);
        panel.add(new JLabel("Пріоритет:"));
        panel.add(priority);
        panel.add(state);
        return panel;
    }

    public static void main(String[] args) throws InterruptedException {
        Factory factory = new Factory("Завод «Промінь»", "Чернівці", 120);
        InsuranceCompany insurer = new InsuranceCompany("СК «Оберіг»", "Чернівці", 500);
        TruckAI trucks = new TruckAI(factory);
        CarAI cars = new CarAI(insurer);

        // Завдання 1. Консольна демонстрація двох потоків.
        if (args.length == 1 && args[0].equals("--console")) {
            factory.Show();
            insurer.Show();
            trucks.start();
            cars.start();
            try {
                for (int second = 0; second < 6; second++) {
                    System.out.println("Вантажні: " + trucks.snapshots().get(0));
                    System.out.println("Легкові: " + cars.snapshots().get(0));
                    Thread.sleep(1000);
                }
            } finally {
                trucks.shutdown();
                cars.shutdown();
                trucks.join();
                cars.join();
            }
            return;
        }

        // Завдання 2. Графічна симуляція з незалежним керуванням автопарками.
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Лабораторна 7 • Варіант 5 • Автопарки організацій");
            SimulationPanel area = new SimulationPanel(trucks, cars);
            JPanel bar = new JPanel(new GridLayout(2, 1));
            bar.add(controls("Вантажні • " + factory.getName(), trucks));
            bar.add(controls("Легкові • " + insurer.getName(), cars));
            frame.add(bar, BorderLayout.NORTH);
            frame.add(area, BorderLayout.CENTER);

            // Пункт 2.3. Таймер просить Swing перемалювати знімки.
            Timer timer = new Timer(16, event -> area.repaint());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(WindowEvent event) {
                    timer.stop();
                    trucks.shutdown();
                    cars.shutdown();
                }
            });
            frame.setSize(1000, 650);
            frame.setMinimumSize(new java.awt.Dimension(900, 500));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            trucks.start();
            cars.start();
            timer.start();
        });
    }
}

// Завдання 2.1. Абстрактний потік BaseAI керує колекцією машин.
abstract class BaseAI extends Thread {
    private final Object gate = new Object();
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();

    // Пункт 2.3. volatile публікує готовий незмінний список для Swing.
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

    // Пункт 2.3. Робочий потік передає знімки стану машин.
    private void publish() {
        positions = vehicles.stream().map(Vehicle::snapshot).toList();
    }
    public List<Position> snapshots() { return positions; }

    // Пункт 2.4. Зміна паузи узгоджена з кроком руху через спільний монітор.
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
                    // Пункт 2.4. wait() відпускає монітор, а після пробудження умова перевіряється знову.
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

// Завдання 1.1 і 2.2. Вантажівки рухаються до цілі у верхній лівій чверті.
final class TruckAI extends BaseAI {
    public TruckAI(Organization owner) { super("TruckAI", true, owner, 51); }
    @Override protected double speed() { return 90; }
}

// Завдання 1.2 і 2.2. Легкові машини рухаються до цілі в нижній правій чверті.
final class CarAI extends BaseAI {
    public CarAI(Organization owner) { super("CarAI", false, owner, 52); }
    @Override protected double speed() { return 110; }
}

// Завдання 1 і 2.2. Спільна логіка вибору цілі та руху машин.
final class Vehicle {
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

// Завдання 2.3. Незмінний знімок стану однієї машини.
record Position(int id, double x, double y, double targetX, double targetY, String owner, boolean arrived) {
}

// Завдання 2.3. Swing малює готові знімки на потоці подій (EDT).
final class SimulationPanel extends JPanel {
    private final BaseAI trucks;
    private final BaseAI cars;

    public SimulationPanel(BaseAI trucks, BaseAI cars) {
        this.trucks = trucks;
        this.cars = cars;
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.scale(getWidth() / (double) Vehicle.WIDTH, getHeight() / (double) Vehicle.HEIGHT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(228, 239, 255));
        g.fillRect(0, 0, Vehicle.WIDTH / 2, Vehicle.HEIGHT / 2);
        g.setColor(new Color(232, 247, 230));
        g.fillRect(Vehicle.WIDTH / 2, Vehicle.HEIGHT / 2, Vehicle.WIDTH / 2, Vehicle.HEIGHT / 2);
        draw(g, trucks, new Color(25, 80, 180), true);
        draw(g, cars, new Color(30, 140, 60), false);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Вантажні: верхня ліва чверть", 12, 20);
        g.drawString("Легкові: нижня права чверть", Vehicle.WIDTH / 2 + 12, Vehicle.HEIGHT - 14);
        g.dispose();
    }

    private void draw(Graphics2D g, BaseAI ai, Color color, boolean truck) {
        for (Position p : ai.snapshots()) {
            int x = (int) p.x();
            int y = (int) p.y();
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x, y, (int) p.targetX(), (int) p.targetY());
            g.setColor(color);
            if (truck) g.fillRect(x - 7, y - 4, 14, 8);
            else g.fillOval(x - 6, y - 4, 12, 8);
            g.drawString(Integer.toString(p.id()), x + 8, y);
        }
    }
}

// Завдання 2. Ієрархія організацій із лабораторної № 3.
class Organization {
    private final String name;
    private final String address;
    public Organization(String name, String address) {
        if (name.isBlank() || address.isBlank()) throw new IllegalArgumentException("Назва й адреса не можуть бути порожні.");
        this.name = name;
        this.address = address;
    }
    public String getName() { return name; }
    public void Show() { System.out.println(this); }
    @Override public String toString() { return name + "; адреса: " + address; }
}

final class Factory extends Organization {
    private final int workers;
    public Factory(String name, String address, int workers) {
        super(name, address);
        if (workers < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною.");
        this.workers = workers;
    }
    @Override public void Show() { System.out.println(this); }
    @Override public String toString() { return "Factory: " + super.toString() + "; працівників: " + workers; }
}

final class InsuranceCompany extends Organization {
    private final int policies;
    public InsuranceCompany(String name, String address, int policies) {
        super(name, address);
        if (policies < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною.");
        this.policies = policies;
    }
    @Override public void Show() { System.out.println(this); }
    @Override public String toString() { return "InsuranceCompany: " + super.toString() + "; страхових полісів: " + policies; }
}

final class OilGasCompany extends Organization {
    private final int wells;
    public OilGasCompany(String name, String address, int wells) {
        super(name, address);
        if (wells < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною.");
        this.wells = wells;
    }
    @Override public void Show() { System.out.println(this); }
    @Override public String toString() { return "OilGasCompany: " + super.toString() + "; свердловин: " + wells; }
}
