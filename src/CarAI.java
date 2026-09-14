public final class CarAI extends BaseAI { // Конкретний потік керує окремим видом машин.
    public CarAI(Organization owner) { super("CarAI", false, owner, 52); } // Тип задає цільову чверть, зерно - відтворювані початкові позиції.
    @Override protected double speed() { return 110; } // Стала швидкість у логічних пікселях за секунду.
}
