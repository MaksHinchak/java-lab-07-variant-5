import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
public class Main {
    private static JPanel controls(String title, BaseAI ai) {
        JPanel panel = new JPanel();
        JButton pause = new JButton("Пауза");
        JButton resume = new JButton("Продовжити");
        JLabel state = new JLabel("Працює");
        JComboBox<Integer> priority = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10});
        priority.setSelectedItem(Thread.NORM_PRIORITY);
        pause.addActionListener(event -> { ai.setPaused(true); state.setText("Пауза"); });
        resume.addActionListener(event -> { ai.setPaused(false); state.setText("Працює"); });
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
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Лабораторна 7 • Варіант 5 • Автопарки організацій");
            SimulationPanel area = new SimulationPanel(trucks, cars);
            JPanel bar = new JPanel(new GridLayout(2, 1));
            bar.add(controls("Вантажні • " + factory.getName(), trucks));
            bar.add(controls("Легкові • " + insurer.getName(), cars));
            frame.add(bar, BorderLayout.NORTH);
            frame.add(area, BorderLayout.CENTER);
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
