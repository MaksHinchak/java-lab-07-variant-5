import java.util.Random; // Генеруємо початкові та кінцеві координати в дозволених областях.
public final class Vehicle { // Змінний стан однієї машини належить лише одному потоку BaseAI.
    public static final int WIDTH = 760; // Ширина логічної області симуляції не залежить від розміру вікна.
    public static final int HEIGHT = 440; // Висота логічної області руху.
    private final int id; // Номер машини для ідентифікації на малюнку та у виводі.
    private final Organization owner; // Зберігаємо зв'язок з ієрархією організацій із лабораторної 3, завдання 1.
    private double x; // Поточна горизонтальна координата, яку змінює тільки робочий потік.
    private double y; // Поточна вертикальна координата.
    private final double targetX; // Кінцева точка вибирається один раз при народженні машини.
    private final double targetY; // Після прибуття нову ціль не призначаємо.
    public Vehicle(int id, Organization owner, boolean truck, Random random) { // Прапорець truck визначає цільову чверть.
        this.id = id; // Запам'ятовуємо номер.
        this.owner = owner; // Машина належить заданій організації.
        x = random.nextDouble() * WIDTH; // Випадкове народження по всій ширині області.
        y = random.nextDouble() * HEIGHT; // Випадкове народження по всій висоті.
        boolean inside = truck ? x <= WIDTH / 2.0 && y <= HEIGHT / 2.0 : x >= WIDTH / 2.0 && y >= HEIGHT / 2.0; // Перевіряємо, чи машина вже у своїй цільовій чверті.
        double offsetX = truck ? 0 : WIDTH / 2.0; // Вантажівки мають ліву, легкові - праву половину.
        double offsetY = truck ? 0 : HEIGHT / 2.0; // Вантажівки мають верхню, легкові - нижню половину.
        targetX = inside ? x : offsetX + random.nextDouble() * WIDTH / 2.0; // Машина, народжена в потрібній чверті, залишається на місці.
        targetY = inside ? y : offsetY + random.nextDouble() * HEIGHT / 2.0; // Інакше обираємо випадкову кінцеву точку потрібної чверті.
    }
    public void move(double speed, double seconds) { // Пересуваємо машину прямолінійно на відстань V*dt.
        if (!Double.isFinite(speed) || speed < 0 || !Double.isFinite(seconds) || seconds < 0) throw new IllegalArgumentException("Швидкість і час мають бути скінченними та невід'ємними."); // Забороняємо некоректний крок інтегрування.
        double dx = targetX - x; // Горизонтальна складова вектора до цілі.
        double dy = targetY - y; // Вертикальна складова вектора до цілі.
        double distance = Math.hypot(dx, dy); // Евклідова довжина цього вектора.
        if (distance == 0) return; // Прибуття означає остаточну зупинку.
        double step = speed * seconds; // Відстань, яку можна пройти за поточний проміжок часу.
        if (step >= distance) { // Не дозволяємо проскочити кінцеву точку.
            x = targetX; // Точно встановлюємо кінцеву координату x.
            y = targetY; // Точно встановлюємо кінцеву координату y.
        } else { // Залишок шляху більший за один крок.
            x += dx / distance * step; // Нормалізуємо напрям і додаємо горизонтальне зміщення.
            y += dy / distance * step; // Так само обчислюємо вертикальне зміщення.
        }
    }
    public Position snapshot() { return new Position(id, x, y, targetX, targetY, owner.getName(), x == targetX && y == targetY); } // Створюємо незалежне незмінне повідомлення для GUI.
}
