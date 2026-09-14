public final class OilGasCompany extends Organization { // Похідний клас успадковує назву, адресу та Show.
    private final int wells; // Власна характеристика: кількість свердловин.
    public OilGasCompany(String name, String address, int wells) { // Конструктор отримує спільні та спеціальні дані.
        super(name, address); // Спочатку викликаємо конструктор батьківської організації.
        if (wells < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною."); // Перевіряємо спеціальне поле.
        this.wells = wells; // Зберігаємо перевірену кількість.
    }
    @Override public void Show() { System.out.println(this); } // Реалізуємо потрібний у методичці метод для цього підкласу.
    @Override public String toString() { return "OilGasCompany: " + super.toString() + "; свердловин: " + wells; } // Доповнюємо батьківський опис власною властивістю.
}
