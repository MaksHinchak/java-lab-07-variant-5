import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
public class Main {
    // static — виклик без об’єкта; public — доступ ззовні, private — лише в класі; void — без результату.
    private static JPanel controls(String title, BaseAI ai) {
        JPanel panel = new JPanel();
        JButton pause = new JButton("Пауза");
        JButton resume = new JButton("Продовжити");
        JLabel state = new JLabel("Працює");
        // new тип[n] — масив незмінної довжини; числові елементи спочатку 0, посилання — null.
        JComboBox<Integer> priority = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10});
        priority.setSelectedItem(Thread.NORM_PRIORITY);
        // event -> ... — лямбда-обробник; виконається при події на потоці Swing EDT.
        pause.addActionListener(event -> { ai.setPaused(true); state.setText("Пауза"); });
        resume.addActionListener(event -> { ai.setPaused(false); state.setText("Працює"); });
        // (Integer) уточнює тип Object; перед викликом setPriority оболонка автоматично стає int.
        priority.addActionListener(event -> ai.setPriority((Integer) priority.getSelectedItem()));
        panel.add(new JLabel(title));
        panel.add(pause);
        panel.add(resume);
        panel.add(new JLabel("Пріоритет:"));
        panel.add(priority);
        panel.add(state);
        return panel;
    }
    // main — точка входу; String[] args містить аргументи запуску без назви програми.
    // throws оголошує перевірюваний виняток: викликач мусить перехопити його або теж оголосити.
    public static void main(String[] args) throws InterruptedException {
        Factory factory = new Factory("Завод «Промінь»", "Чернівці", 120);
        InsuranceCompany insurer = new InsuranceCompany("СК «Оберіг»", "Чернівці", 500);
        TruckAI trucks = new TruckAI(factory);
        CarAI cars = new CarAI(insurer);
        // .length — довжина масиву без дужок; для String — length(), для колекції — size().
        // equals порівнює вміст; == для об’єктів Java перевіряє тотожність посилань.
        if (args.length == 1 && args[0].equals("--console")) {
            factory.Show();
            insurer.Show();
            // Thread.start запускає run в окремому потоці; прямий run() нового потоку не створює.
            trucks.start();
            cars.start();
            try {
                // for (початок; умова; крок); i++ збільшує лічильник після проходу.
                for (int second = 0; second < 6; second++) {
                    System.out.println("Вантажні: " + trucks.snapshots().get(0));
                    System.out.println("Легкові: " + cars.snapshots().get(0));
                    // Thread.sleep чекає в мілісекундах; може кинути InterruptedException, замків не звільняє.
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
        // invokeLater ставить лямбду () -> {...} у чергу EDT — потоку роботи з інтерфейсом Swing.
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Лабораторна 7 • Варіант 5 • Автопарки організацій");
            SimulationPanel area = new SimulationPanel(trucks, cars);
            JPanel bar = new JPanel(new GridLayout(2, 1));
            bar.add(controls("Вантажні • " + factory.getName(), trucks));
            bar.add(controls("Легкові • " + insurer.getName(), cars));
            frame.add(bar, BorderLayout.NORTH);
            frame.add(area, BorderLayout.CENTER);
            // Swing Timer викликає обробник на EDT; затримка задається в мілісекундах.
            Timer timer = new Timer(16, event -> area.repaint());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // new WindowAdapter() {...} — анонімний підклас із перевизначеним обробником.
            frame.addWindowListener(new WindowAdapter() {
                // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
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
