import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
public final class SimulationPanel extends JPanel {
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
