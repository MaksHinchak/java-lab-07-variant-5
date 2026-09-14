public class Organization { // Базовий клас для всіх організацій; також використовується в лабораторній 7.
    private final String name; // Назва є спільною властивістю всіх організацій.
    private final String address; // Адресу зберігаємо в базовому класі, не дублюючи в підкласах.
    public Organization(String name, String address) { // Конструктор спільних даних.
        if (name.isBlank() || address.isBlank()) throw new IllegalArgumentException("Назва й адреса не можуть бути порожні."); // Зберігаємо інваріант змістовних полів.
        this.name = name; // Ініціалізуємо назву поточного об'єкта.
        this.address = address; // Ініціалізуємо адресу.
    }
    public String getName() { return name; } // Підкласи та інші об'єкти можуть читати назву.
    public void Show() { System.out.println(this); } // Динамічний виклик toString відображає фактичний підклас.
    @Override public String toString() { return name + "; адреса: " + address; } // Формуємо спільну частину опису.
}
