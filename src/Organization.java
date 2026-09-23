public class Organization {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    private final String name;
    private final String address;
    public Organization(String name, String address) { // Конструктор спільних даних.
        // throw передає помилку в catch; new створює об’єкт винятку з повідомленням.
        // && — «і», || — «або»; праву умову перевіряють лише за потреби.
        if (name.isBlank() || address.isBlank()) throw new IllegalArgumentException("Назва й адреса не можуть бути порожні."); // Зберігаємо інваріант змістовних полів.
        // this — поточний об’єкт; this.поле відрізняє поле від однойменного параметра.
        this.name = name;
        this.address = address;
    }
    public String getName() { return name; } // Підкласи та інші об'єкти можуть читати назву.
    public void Show() { System.out.println(this); } // Динамічний виклик toString відображає фактичний підклас.
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override public String toString() { return name + "; адреса: " + address; } // Формуємо спільну частину опису.
}
