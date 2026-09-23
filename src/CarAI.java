// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
public final class CarAI extends BaseAI {
    // super(...) — виклик конструктора батьківського класу.
    public CarAI(Organization owner) { super("CarAI", false, owner, 52); } // Тип задає цільову чверть, зерно - відтворювані початкові позиції.
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override protected double speed() { return 110; } // Стала швидкість у логічних пікселях за секунду.
}
