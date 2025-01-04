package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;
import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Перебираем все ряды и юнитов в рядах
        for (int rowIndex = 0; rowIndex < unitsByRow.size(); rowIndex++) {
            List<Unit> row = unitsByRow.get(rowIndex);

            for (Unit unit : row) {
                // Если атакующая армия (армия компьютера), то проверяем армию игрока
                if (isLeftArmyTarget) {
                    // Если юнит армии игрока и он не закрыт другим юнитом того же игрока
                    if (!unit.isAlive()) continue;
                    boolean isBlocked = false;
                    // Проверяем, нет ли юнитов армии игрока справа
                    for (int i = rowIndex + 1; i < unitsByRow.size(); i++) {
                        List<Unit> nextRow = unitsByRow.get(i);
                        for (Unit nextUnit : nextRow) {
                            // Если на той же координате Y живет юнит армии игрока, то атака невозможна
                            if (nextUnit.getyCoordinate() == unit.getyCoordinate() && nextUnit.isAlive()) {
                                isBlocked = true;
                                break;
                            }
                        }
                        if (isBlocked) break;
                    }
                    if (!isBlocked) {
                        suitableUnits.add(unit);
                    }
                }
                // Если атакующая армия (армия игрока), проверяем армию компьютера
                else {
                    // Если юнит армии компьютера и он не закрыт другим юнитом того же компьютера
                    if (!unit.isAlive()) continue;
                    boolean isBlocked = false;
                    // Проверяем, нет ли юнитов армии компьютера слева
                    for (int i = rowIndex - 1; i >= 0; i--) {
                        List<Unit> prevRow = unitsByRow.get(i);
                        for (Unit prevUnit : prevRow) {
                            // Если на той же координате Y живет юнит армии компьютера, то атака невозможна
                            if (prevUnit.getyCoordinate() == unit.getyCoordinate() && prevUnit.isAlive()) {
                                isBlocked = true;
                                break;
                            }
                        }
                        if (isBlocked) break;
                    }
                    if (!isBlocked) {
                        suitableUnits.add(unit);
                    }
                }
            }
        }

        if (suitableUnits.isEmpty()) {
            System.out.println("Подходящих юнитов для атаки не найдено!");
        }

        return suitableUnits;
    }
}
