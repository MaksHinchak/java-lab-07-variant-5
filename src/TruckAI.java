public final class TruckAI extends BaseAI { // Конкретний потік керує окремим видом машин.
    public TruckAI(Organization owner) { super("TruckAI", true, owner, 51); } // Тип задає цільову чверть, зерно - відтворювані початкові позиції.
    @Override protected double speed() { return 90; } // Стала швидкість у логічних пікселях за секунду.
}
