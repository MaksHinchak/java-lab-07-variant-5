public class LabChecks { // Автономні перевірки без зовнішніх бібліотек тестування.
    private static int count = 0; // Лічильник успішно перевірених умов.
    private static void check(boolean value) { // Допоміжний метод перетворює хибну умову на провал тесту.
        if (!value) throw new AssertionError("Перевірка " + (count + 1) + " не пройшла."); // Зупиняємо тест із ненульовим кодом процесу.
        count++; // Рахуємо лише успішні перевірки.
    }
    private static void near(double actual, double expected) { // Порівнюємо double з допуском на округлення.
        check(Math.abs(actual - expected) <= 1e-9 * Math.max(1, Math.abs(expected))); // Допуск враховує масштаб очікуваного числа.
    }
    private interface Action { void run() throws Exception; } // Лямбда тесту може породжувати і перевірювані винятки.
    private static void expect(Class<? extends Throwable> type, Action action) { // Перевіряємо, що помилкові дані дають саме потрібний вид помилки.
        try { action.run(); } // Виконуємо потенційно помилкову операцію.
        catch (Throwable error) { check(type.isInstance(error)); return; } // Неправильний тип винятку теж провалює тест.
        throw new AssertionError("Очікували " + type.getSimpleName()); // Відсутність потрібного винятку є помилкою реалізації.
    }
    public static void main(String[] args) throws Exception { // Метод запускає усі перевірки цієї лабораторної.
        Organization owner = new Factory("Test","Test",1); // Тестовий власник з ієрархії лабораторної 3.
        for (boolean truck : new boolean[]{true,false}) { // Перевіряємо обидва види машин.
            for (int seed = 0; seed < 100; seed++) { // Різні початкові позиції охоплюють рух і народження всередині цільової області.
                Vehicle v = new Vehicle(seed,owner,truck,new java.util.Random(seed)); // Створюємо відтворюваний початковий стан.
                Position before = v.snapshot(); // Фіксуємо початок і ціль.
                boolean inside = truck ? before.x()<=380 && before.y()<=220 : before.x()>=380 && before.y()>=220; // Незалежно перевіряємо чверть народження.
                if (inside) check(before.arrived()); // Народжена в цільовій чверті машина повинна одразу стояти.
                check(truck ? before.targetX()<=380 && before.targetY()<=220 : before.targetX()>=380 && before.targetY()>=220); // Ціль завжди у потрібній чверті.
                v.move(100,100); // Великий крок повинен завершитись точно в цілі без проскоку.
                Position after = v.snapshot(); // Отримуємо результат руху.
                check(after.arrived()); near(after.x(),after.targetX()); near(after.y(),after.targetY()); // Машина зупинилася в кінцевій точці.
                v.move(100,1); check(v.snapshot().equals(after)); // Після прибуття рух не відновлюється.
            }
        }
        TruckAI ai = new TruckAI(owner); // Перевіряємо протокол реального потоку.
        ai.setPaused(true); ai.start(); // Запускаємо потік у стані очікування.
        try { // Завершення гарантоване навіть при помилці перевірки.
            java.util.List<Position> first = ai.snapshots(); // Знімок до відновлення.
            Thread.sleep(60); check(ai.snapshots().equals(first)); // На паузі координати не змінюються.
            ai.setPriority(3); check(ai.getPriority()==3); // Параметр пріоритету має зберігатися.
            ai.setPaused(false); // Пробуджуємо потік через notifyAll.
            long deadline = System.nanoTime()+2_000_000_000L; // Обмежуємо час очікування тесту двома секундами.
            while (ai.snapshots().equals(first) && System.nanoTime()<deadline) Thread.sleep(10); // Чекаємо фактичного кроку, а не конкретного розкладу ОС.
            check(!ai.snapshots().equals(first)); // Після відновлення машини мають рухатися.
            ai.setPaused(true); java.util.List<Position> frozen = ai.snapshots(); // setPaused повертає лише після узгодження з поточним кроком.
            Thread.sleep(60); check(ai.snapshots().equals(frozen)); // Повторна пауза також стабільна.
        } finally { ai.shutdown(); ai.join(2000); } // Завершуємо навіть потік, який перебуває у wait.
        check(!ai.isAlive()); // Після закриття не повинно лишитися фонового потоку.
        System.out.println("OK: " + count + " перевірок"); // Видимий підсумок після успішного виконання.
    }
}
