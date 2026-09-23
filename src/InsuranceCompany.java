// extends — успадкування класу; final, якщо вказано, забороняє подальше успадкування.
public final class InsuranceCompany extends Organization {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    private final int policies;
    public InsuranceCompany(String name, String address, int policies) {
        // super(...) — виклик конструктора батьківського класу.
        super(name, address);
        if (policies < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною.");
        // this — поточний об’єкт; this.поле відрізняє поле від однойменного параметра.
        this.policies = policies;
    }
    // @Override — компілятор перевіряє, що метод перевизначає успадкований або реалізує інтерфейс.
    @Override public void Show() { System.out.println(this); }
    @Override public String toString() { return "InsuranceCompany: " + super.toString() + "; страхових полісів: " + policies; }
}
