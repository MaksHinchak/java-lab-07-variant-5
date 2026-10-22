public final class InsuranceCompany extends Organization {
    private final int policies;
    public InsuranceCompany(String name, String address, int policies) {
        super(name, address);
        if (policies < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною.");
        this.policies = policies;
    }
    @Override public void Show() { System.out.println(this); }
    @Override public String toString() { return "InsuranceCompany: " + super.toString() + "; страхових полісів: " + policies; }
}
