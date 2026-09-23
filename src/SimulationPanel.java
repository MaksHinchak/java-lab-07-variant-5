import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
public final class SimulationPanel extends JPanel {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    private final BaseAI trucks;
    private final BaseAI cars;
    public SimulationPanel(BaseAI trucks, BaseAI cars) { // Зберігаємо джерела даних для малювання.
        // this — поточний об’єкт; this.поле відрізняє поле від однойменного параметра.
        this.trucks = trucks;
        this.cars = cars;
    }
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override protected void paintComponent(Graphics graphics) { // Swing малює тільки на головному потоці інтерфейсу EDT.
        super.paintComponent(graphics);
        // (Graphics2D) уточнює тип копії графічного контексту; копію потім звільняє dispose().
        Graphics2D g = (Graphics2D) graphics.create();
        // (double) переводить операнд у дробовий тип до арифметики; int / int у Java дає ціле.
        g.scale(getWidth() / (double) Vehicle.WIDTH, getHeight() / (double) Vehicle.HEIGHT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // new Клас(...) створює об’єкт і викликає його конструктор.
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
    private void draw(Graphics2D g, BaseAI ai, Color color, boolean truck) { // Малювання одного виду машин з останнього узгодженого знімка.
        // for (Тип елемент : колекція) — перебір елементів без індексу.
        for (Position p : ai.snapshots()) { // Список та його елементи незмінні, тому блокування не потрібне.
            // (int) відкидає дробову частину до нуля; int обмежений 32 бітами.
            int x = (int) p.x();
            int y = (int) p.y();
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x, y, (int) p.targetX(), (int) p.targetY());
            g.setColor(color);
            if (truck) g.fillRect(x - 7, y - 4, 14, 8); // Вантажна машина у вигляді прямокутника.
            else g.fillOval(x - 6, y - 4, 12, 8); // Легкова машина у вигляді овалу.
            g.drawString(Integer.toString(p.id()), x + 8, y);
        }
    }
}
