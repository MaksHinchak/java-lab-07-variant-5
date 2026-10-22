public final class Factory extends Organization {
    private final int workers;
    public Factory(String name, String address, int workers) {
        super(name, address);
        if (workers < 0) throw new IllegalArgumentException("Кількість не може бути від'ємною.");
        this.workers = workers;
    }
    @Override public void Show() { System.out.println(this); }
    @Override public String toString() { return "Factory: " + super.toString() + "; працівників: " + workers; }
}
