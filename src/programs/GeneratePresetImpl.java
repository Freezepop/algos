package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {

        Army army = new Army(); // Создаем объект армии, который будет содержать выбранные юниты
        List<Unit> selectedUnits = new ArrayList<>(); // Список юнитов, которые войдут в армию
        Map<String, Integer> unitTypeCounts = new HashMap<>(); // Счетчик количества юнитов каждого типа
        Random random = new Random(); // Генератор случайных чисел для координат

        System.out.println("=== Начало формирования пресета армии компьютера ===");
        System.out.println("Список юнитов перед сортировкой: ");
        // Выводим список доступных юнитов с их характеристиками
        unitList.forEach(unit ->
                System.out.println(unit.getName() + " (Атака: " + unit.getBaseAttack() +
                        ", Здоровье: " + unit.getHealth() +
                        ", Стоимость: " + unit.getCost() + ")")
        );

        // Сортируем юнитов по убыванию "эффективности", вычисляемой как сумма атаки и здоровья, деленных на стоимость
        unitList.sort((u1, u2) -> {
            double efficiency1 = (double) u1.getBaseAttack() / u1.getCost() + (double) u1.getHealth() / u1.getCost();
            double efficiency2 = (double) u2.getBaseAttack() / u2.getCost() + (double) u2.getHealth() / u2.getCost();
            return Double.compare(efficiency2, efficiency1);
        });

        System.out.println("Список юнитов после сортировки по эффективности: ");
        // Выводим список юнитов после сортировки с их рассчитанной эффективностью
        unitList.forEach(unit ->
                System.out.println(unit.getName() + " (Эффективность: " +
                        ((double) unit.getBaseAttack() / unit.getCost() + (double) unit.getHealth() / unit.getCost()) + ")")
        );

        int currentPoints = 0; // Счетчик текущего количества очков, потраченных на армию

        // Проходим по отсортированному списку юнитов
        for (Unit unit : unitList) {
            String unitType = unit.getUnitType(); // Тип
            int unitCost = unit.getCost(); // Стоимость
            int unitCount = unitTypeCounts.getOrDefault(unitType, 0); // Сколько юнитов такого типа уже выбрано

            // Добавляем юнитов данного типа, пока их количество меньше 11 и текущие очки позволяют выбрать юнита
            while (unitCount < 11 && currentPoints + unitCost <= maxPoints) {
                // Ищем доступные координаты для нового юнита
                int[] coordinates = findAvailableCoordinates(selectedUnits, random);
                if (coordinates == null) { // Если координаты не найдены, прекращаем добавление
                    System.out.println("Не удалось найти доступные координаты для юнита " + unit.getName());
                    break;
                }

                // Создаем нового юнита с уникальным именем и координатами
                Unit newUnit = new Unit(
                        unit.getName() + " " + (unitCount + 1), // Уникальное имя юнита
                        unit.getUnitType(), // Тип
                        unit.getHealth(), // Здоровье
                        unit.getBaseAttack(), // Атака
                        unit.getCost(), // Стоимость
                        unit.getAttackType(), // Тип атаки
                        unit.getAttackBonuses(), // Бонусы атаки
                        unit.getDefenceBonuses(), // Бонусы защиты
                        coordinates[0], // Координата X
                        coordinates[1]  // Координата Y
                );

                selectedUnits.add(newUnit); // Добавляем нового юнита в список выбранных
                army.getUnits().add(newUnit); // Добавляем юнита в армию
                currentPoints += unitCost; // Увеличиваем текущие очки на стоимость юнита
                unitCount++; // Увеличиваем счетчик юнитов данного типа
                unitTypeCounts.put(unitType, unitCount); // Обновляем счетчик юнитов данного типа

                // Выводим информацию о добавленном юните
                System.out.println("Добавлен юнит: " + newUnit.getName() +
                        " (Тип: " + newUnit.getUnitType() +
                        ", Координаты: [" + coordinates[0] + ", " + coordinates[1] + "], Стоимость: " + unitCost + ")");
            }
        }

        // Выводим итоговую информацию
        System.out.println("=== Формирование пресета завершено ===");
        System.out.println("Общее количество юнитов: " + selectedUnits.size());
        System.out.println("Использовано очков: " + currentPoints + " из " + maxPoints);
        return army; // Возвращаем сгенерированную армию
    }

    // Метод для поиска доступных координат для нового юнита.
    private int[] findAvailableCoordinates(List<Unit> units, Random random) {
        int maxAttempts = 100; // Максимальное количество попыток для поиска координат

        // Пробуем до 100 раз найти свободные координаты
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Перебираем возможные координаты (сетка 3x21). Насколько понял армия может генерироваться только в пределах этой области.
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 21; y++) {
                    int finalX = x;
                    int finalY = y;
                    // Проверяем, заняты ли координаты другими юнитами
                    boolean isOccupied = units.stream().anyMatch(unit ->
                            unit.getxCoordinate() == finalX && unit.getyCoordinate() == finalY
                    );

                    if (!isOccupied) { // Если координаты свободны, возвращаем их
                        return new int[]{x, y};
                    }
                }
            }
        }

        // Если не удалось найти координаты за 100 попыток, выводим сообщение об ошибке
        System.out.println("Не удалось найти доступные координаты после " + maxAttempts + " попыток");
        return null; // Возвращаем null, если координаты не найдены
    }
}
