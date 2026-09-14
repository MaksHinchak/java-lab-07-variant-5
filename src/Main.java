import javax.swing.*; // Кнопки, списки, панелі, вікно та Swing Timer.
import java.awt.BorderLayout; // Керування зверху, симуляція в центрі.
import java.awt.GridLayout; // Окремий рядок керування для кожного потоку.
import java.awt.event.WindowAdapter; // Обробка закриття вікна.
import java.awt.event.WindowEvent; // Тип події закриття.
public class Main { // --console демонструє завдання 1, звичайний запуск відкриває GUI завдання 2.
    private static JPanel controls(String title, BaseAI ai) { // Будуємо незалежний набір керування одним потоком.
        JPanel panel = new JPanel(); // Поточний рядок панелі керування.
        JButton pause = new JButton("Пауза"); // Кнопка присипляє інтелект тільки вибраного виду.
        JButton resume = new JButton("Продовжити"); // Кнопка пробуджує той самий потік.
        JLabel state = new JLabel("Працює"); // Відображаємо запитаний користувачем стан.
        JComboBox<Integer> priority = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10}); // Діапазон стандартних пріоритетів платформних Java-потоків.
        priority.setSelectedItem(Thread.NORM_PRIORITY); // Початкове значення 5 відповідає типовому пріоритету.
        pause.addActionListener(event -> { ai.setPaused(true); state.setText("Пауза"); }); // setPaused узгоджується з поточним кроком через монітор.
        resume.addActionListener(event -> { ai.setPaused(false); state.setText("Працює"); }); // notifyAll усередині методу дозволяє вийти з wait.
        priority.addActionListener(event -> ai.setPriority((Integer) priority.getSelectedItem())); // Пріоритет є підказкою ОС, а не гарантією частоти виконання.
        panel.add(new JLabel(title)); // Назва виду машин і власника.
        panel.add(pause); // Додаємо кнопку паузи.
        panel.add(resume); // Додаємо кнопку відновлення.
        panel.add(new JLabel("Пріоритет:")); // Пояснення спадного списку.
        panel.add(priority); // Вибір пріоритету конкретного потоку.
        panel.add(state); // Текст стану поруч із керуванням.
        return panel; // Повертаємо готовий рядок.
    }
    public static void main(String[] args) throws InterruptedException { // Консольний сценарій може бути перерваний під час sleep або join.
        Factory factory = new Factory("Завод «Промінь»", "Чернівці", 120); // Реальний підклас із лабораторної 3 тепер володіє вантажним автопарком.
        InsuranceCompany insurer = new InsuranceCompany("СК «Оберіг»", "Чернівці", 500); // Другий похідний клас володіє легковими машинами.
        TruckAI trucks = new TruckAI(factory); // Створюємо перший, ще не запущений потік.
        CarAI cars = new CarAI(insurer); // Створюємо другий незалежний потік.
        if (args.length == 1 && args[0].equals("--console")) { // Окремий запуск двох потоків для завдання 1.
            factory.Show(); // Демонструємо збережений метод ієрархії організацій.
            insurer.Show(); // Друкуємо другого власника.
            trucks.start(); // Створюємо потік виконання вантажівок.
            cars.start(); // Створюємо потік виконання легкових машин.
            try { // finally гарантує зупинку обох потоків навіть при перериванні main.
                for (int second = 0; second < 6; second++) { // Демонстрація триває шість секунд.
                    System.out.println("Вантажні: " + trucks.snapshots().get(0)); // Читаємо знімок першої вантажівки.
                    System.out.println("Легкові: " + cars.snapshots().get(0)); // Читаємо знімок першої легкової машини.
                    Thread.sleep(1000); // Основний потік чекає секунду, робочі продовжують рух.
                }
            } finally { // Коректно завершуємо обидва потоки.
                trucks.shutdown(); // Просимо завершити перший інтелект.
                cars.shutdown(); // Просимо завершити другий інтелект.
                trucks.join(); // Чекаємо фактичного завершення першого run.
                cars.join(); // Чекаємо завершення другого run.
            }
            return; // GUI у консольному режимі не створюється.
        }
        SwingUtilities.invokeLater(() -> { // Основний потік інтерфейсу Swing - EDT; усі графічні дії виконує він.
            JFrame frame = new JFrame("Лабораторна 7 • Варіант 5 • Автопарки організацій"); // Створюємо головне вікно.
            SimulationPanel area = new SimulationPanel(trucks, cars); // Малювання отримує лише джерела незмінних знімків.
            JPanel bar = new JPanel(new GridLayout(2, 1)); // Один рядок керування на кожний вид машин.
            bar.add(controls("Вантажні • " + factory.getName(), trucks)); // Перший рядок керує TruckAI.
            bar.add(controls("Легкові • " + insurer.getName(), cars)); // Другий рядок керує CarAI.
            frame.add(bar, BorderLayout.NORTH); // Кнопки розміщуємо над симуляцією.
            frame.add(area, BorderLayout.CENTER); // Область руху займає решту вікна.
            Timer timer = new Timer(16, event -> area.repaint()); // Таймер EDT лише просить перемалювати, не рахує рух.
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Закриття запускає власне очищення ресурсів.
            frame.addWindowListener(new WindowAdapter() { // Слухач коректно завершує фонову роботу.
                @Override public void windowClosed(WindowEvent event) { // Реагуємо після закриття вікна.
                    timer.stop(); // Забираємо періодичні події малювання.
                    trucks.shutdown(); // Пробуджуємо й завершуємо потік вантажівок.
                    cars.shutdown(); // Пробуджуємо й завершуємо потік легкових.
                }
            });
            frame.setSize(1000, 650); // Початковий розмір вміщує керування та симуляцію.
            frame.setMinimumSize(new java.awt.Dimension(900, 500)); // Захищаємо кнопки від надмірного звуження вікна.
            frame.setLocationRelativeTo(null); // Центруємо вікно.
            frame.setVisible(true); // Показуємо готовий інтерфейс.
            trucks.start(); // Запускаємо обчислення після підготовки GUI.
            cars.start(); // Другий вид має окремий потік.
            timer.start(); // Починаємо регулярне відображення знімків.
        });
    }
}
