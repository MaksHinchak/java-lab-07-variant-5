public class Organization {
    private final String name;
    private final String address;
    public Organization(String name, String address) {
        if (name.isBlank() || address.isBlank()) throw new IllegalArgumentException("Назва й адреса не можуть бути порожні.");
        this.name = name;
        this.address = address;
    }
    public String getName() { return name; }
    public void Show() { System.out.println(this); }
    @Override public String toString() { return name + "; адреса: " + address; }
}
