public final class Factory extends Organization { // Похідний клас успадковує назву, адресу та Show.
    private final int workers; // Власна характеристика: кількість працівників.
    public Factory(String name, String address, int workers) { // Конструктор отримує спільні та спеціальні дані.
        super(name, address); // Спочатку викликаємо конструктор батьківської організації.
        if (workers < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною."); // Перевіряємо спеціальне поле.
        this.workers = workers; // Зберігаємо перевірену кількість.
    }
    @Override public void Show() { System.out.println(this); } // Реалізуємо потрібний у методичці метод для цього підкласу.
    @Override public String toString() { return "Factory: " + super.toString() + "; працівників: " + workers; } // Доповнюємо батьківський опис власною властивістю.
}
