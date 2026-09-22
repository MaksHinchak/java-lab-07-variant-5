// record — короткий запис класу даних: Java сама створює конструктор, методи id(), x() тощо, equals, hashCode і
// toString. Це близько до @dataclass(frozen=True) у Python. Поля тут — числа, boolean та незмінний String, тому
// отриманий знімок не зміниться після публікації.
public record Position(int id, double x, double y, double targetX, double targetY, String owner, boolean arrived) { // Незмінний знімок стану; EDT читає його без доступу до змінних машин потоку.
}
