public final class InsuranceCompany extends Organization { // Похідний клас успадковує назву, адресу та Show.
    private final int policies; // Власна характеристика: кількість страхових полісів.
    public InsuranceCompany(String name, String address, int policies) { // Конструктор отримує спільні та спеціальні дані.
        super(name, address); // Спочатку викликаємо конструктор батьківської організації.
        if (policies < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною."); // Перевіряємо спеціальне поле.
        this.policies = policies; // Зберігаємо перевірену кількість.
    }
    @Override public void Show() { System.out.println(this); } // Реалізуємо потрібний у методичці метод для цього підкласу.
    @Override public String toString() { return "InsuranceCompany: " + super.toString() + "; страхових полісів: " + policies; } // Доповнюємо батьківський опис власною властивістю.
}
