// record автоматично створює конструктор, getters x()/y()/…, equals, hashCode і toString; поля final.
public record Position(int id, double x, double y, double targetX, double targetY, String owner, boolean arrived) { // Незмінний знімок стану; EDT читає його без доступу до змінних машин потоку.
}
