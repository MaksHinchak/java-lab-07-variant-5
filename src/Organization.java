public class Organization {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    private final String name;
    private final String address;
    public Organization(String name, String address) {
        if (name.isBlank() || address.isBlank()) throw new IllegalArgumentException("Назва й адреса не можуть бути порожні.");
        // this — поточний об’єкт; this.поле відрізняє поле від однойменного параметра.
        this.name = name;
        this.address = address;
    }
    public String getName() { return name; }
    public void Show() { System.out.println(this); }
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override public String toString() { return name + "; адреса: " + address; }
}
