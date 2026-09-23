// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
public final class TruckAI extends BaseAI {
    // super(...) — виклик конструктора батьківського класу.
    public TruckAI(Organization owner) { super("TruckAI", true, owner, 51); }
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override protected double speed() { return 90; }
}
