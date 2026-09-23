// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
public final class OilGasCompany extends Organization {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    private final int wells;
    public OilGasCompany(String name, String address, int wells) { // Конструктор отримує спільні та спеціальні дані.
        // super(...) — виклик конструктора батьківського класу.
        super(name, address);
        // throw передає помилку в catch; new створює об’єкт винятку з повідомленням.
        if (wells < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною."); // Перевіряємо спеціальне поле.
        // this — поточний об’єкт; this.поле відрізняє поле від однойменного параметра.
        this.wells = wells;
    }
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override public void Show() { System.out.println(this); } // Реалізуємо потрібний у методичці метод для цього підкласу.
    @Override public String toString() { return "OilGasCompany: " + super.toString() + "; свердловин: " + wells; } // Доповнюємо батьківський опис власною властивістю.
}
