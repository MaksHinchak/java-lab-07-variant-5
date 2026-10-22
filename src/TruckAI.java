public final class TruckAI extends BaseAI {
    public TruckAI(Organization owner) { super("TruckAI", true, owner, 51); }
    @Override protected double speed() { return 90; }
}
