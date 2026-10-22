public final class OilGasCompany extends Organization {
    private final int wells;
    public OilGasCompany(String name, String address, int wells) {
        super(name, address);
        if (wells < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною.");
        this.wells = wells;
    }
    @Override public void Show() { System.out.println(this); }
    @Override public String toString() { return "OilGasCompany: " + super.toString() + "; свердловин: " + wells; }
}
