public final class CarAI extends BaseAI {
    public CarAI(Organization owner) { super("CarAI", false, owner, 52); }
    @Override protected double speed() { return 110; }
}
