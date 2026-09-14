import javax.swing.JPanel; // Область малювання симуляції.
import java.awt.Color; // Кольори машин і цільових областей.
import java.awt.Graphics; // Початковий графічний контекст.
import java.awt.Graphics2D; // Підтримує масштабування та згладжування.
import java.awt.RenderingHints; // Параметри якості графіки.
public final class SimulationPanel extends JPanel { // Панель читає лише незмінні знімки робочих потоків.
    private final BaseAI trucks; // Джерело знімків вантажівок.
    private final BaseAI cars; // Джерело знімків легкових машин.
    public SimulationPanel(BaseAI trucks, BaseAI cars) { // Зберігаємо джерела даних для малювання.
        this.trucks = trucks; // Посилання на перший потік.
        this.cars = cars; // Посилання на другий потік.
    }
    @Override protected void paintComponent(Graphics graphics) { // Swing малює тільки на головному потоці інтерфейсу EDT.
        super.paintComponent(graphics); // Стираємо попередній кадр.
        Graphics2D g = (Graphics2D) graphics.create(); // Налаштування застосовуються тільки до локальної копії.
        g.scale(getWidth() / (double) Vehicle.WIDTH, getHeight() / (double) Vehicle.HEIGHT); // Відображаємо сталі логічні координати в будь-який розмір панелі.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Згладжування ліній і фігур.
        g.setColor(new Color(228, 239, 255)); // Світло-синя цільова чверть вантажівок.
        g.fillRect(0, 0, Vehicle.WIDTH / 2, Vehicle.HEIGHT / 2); // Верхня ліва чверть.
        g.setColor(new Color(232, 247, 230)); // Світло-зелена цільова чверть легкових.
        g.fillRect(Vehicle.WIDTH / 2, Vehicle.HEIGHT / 2, Vehicle.WIDTH / 2, Vehicle.HEIGHT / 2); // Нижня права чверть.
        draw(g, trucks, new Color(25, 80, 180), true); // Малюємо вантажівки прямокутниками.
        draw(g, cars, new Color(30, 140, 60), false); // Малюємо легкові овальними фігурами.
        g.setColor(Color.DARK_GRAY); // Нейтральний колір підписів.
        g.drawString("Вантажні: верхня ліва чверть", 12, 20); // Підпис області вантажівок.
        g.drawString("Легкові: нижня права чверть", Vehicle.WIDTH / 2 + 12, Vehicle.HEIGHT - 14); // Підпис області легкових.
        g.dispose(); // Звільняємо локальний графічний контекст.
    }
    private void draw(Graphics2D g, BaseAI ai, Color color, boolean truck) { // Малювання одного виду машин з останнього узгодженого знімка.
        for (Position p : ai.snapshots()) { // Список та його елементи незмінні, тому блокування не потрібне.
            int x = (int) p.x(); // Перетворюємо координату для піксельного малювання.
            int y = (int) p.y(); // Перетворюємо вертикальну координату.
            g.setColor(Color.LIGHT_GRAY); // Лінія до цілі допомагає перевірити прямолінійність руху.
            g.drawLine(x, y, (int) p.targetX(), (int) p.targetY()); // Візуалізуємо залишок шляху.
            g.setColor(color); // Встановлюємо колір поточного виду машин.
            if (truck) g.fillRect(x - 7, y - 4, 14, 8); // Вантажна машина у вигляді прямокутника.
            else g.fillOval(x - 6, y - 4, 12, 8); // Легкова машина у вигляді овалу.
            g.drawString(Integer.toString(p.id()), x + 8, y); // Підписуємо номер для спостереження за конкретною машиною.
        }
    }
}
