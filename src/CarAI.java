// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
public final class CarAI extends BaseAI {
    // super(...) — виклик конструктора батьківського класу.
    public CarAI(Organization owner) { super("CarAI", false, owner, 52); }
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override protected double speed() { return 110; }
}
