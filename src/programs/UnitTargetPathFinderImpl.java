package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27; // Ширина игрового поля
    private static final int HEIGHT = 21; // Высота игрового поля
    private static final int[][] DIRECTIONS = new int[][]{ // Возможные направления движения
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // Вверх, вниз, влево, вправо
            {-1, -1}, {1, 1}, {-1, 1}, {1, -1} // Диагональные направления
    };
    private static final int INFINITY = Integer.MAX_VALUE; // "Бесконечность" для инициализации расстояний

    @Override
    public List<Edge> getTargetPath(Unit sourceUnit, Unit targetUnit, List<Unit> allUnits) {
        // Матрица расстояний от исходной позиции до каждой клетки
        int[][] distanceMap = new int[WIDTH][HEIGHT];
        // Матрица посещенных клеток
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        // Матрица предыдущих клеток для восстановления пути
        Edge[][] previousEdges = new Edge[WIDTH][HEIGHT];

        // Инициализируем расстояния до "бесконечности"
        for (int[] row : distanceMap) {
            Arrays.fill(row, INFINITY);
        }

        // Очередь с приоритетом для обработки клеток по расстоянию.
        PriorityQueue<EdgeDistance> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance));
        int sourceX = sourceUnit.getxCoordinate(); // Начальная координата X
        int sourceY = sourceUnit.getyCoordinate(); // Начальная координата Y
        distanceMap[sourceX][sourceY] = 0; // Расстояние до начальной клетки равно 0
        priorityQueue.add(new EdgeDistance(sourceX, sourceY, 0)); // Добавляем стартовую клетку в очередь

        // Список занятых позиций, чтобы избегать их при поиске пути
        Set<String> occupiedPositions = new HashSet<>();
        for (Unit unit : allUnits) {
            // Учитываем только живых юнитов, кроме источника и цели
            if (unit != sourceUnit && unit != targetUnit && unit.isAlive()) {
                occupiedPositions.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        // Алгоритм поиска пути. Внезапно вспоминаем про алгритим Дейкстры =D
        while (!priorityQueue.isEmpty()) {
            EdgeDistance current = priorityQueue.poll(); // Извлекаем клетку с наименьшим расстоянием
            int currentX = current.getX();
            int currentY = current.getY();

            // Если клетка уже посещена, пропускаем её
            if (visited[currentX][currentY]) {
                continue;
            }

            visited[currentX][currentY] = true; // Отмечаем клетку как посещённую

            // Если достигли клетки цели, завершаем поиск
            if (currentX == targetUnit.getxCoordinate() && currentY == targetUnit.getyCoordinate()) {
                break;
            }

            // Проверяем соседние клетки
            for (int[] direction : DIRECTIONS) {
                int neighborX = currentX + direction[0];
                int neighborY = currentY + direction[1];

                // Проверяем, является ли соседняя клетка допустимой для перемещения
                if (isValid(neighborX, neighborY, occupiedPositions, targetUnit)) {
                    int newDistance = distanceMap[currentX][currentY] + 1; // Новое расстояние
                    // Если клетка не посещена и новое расстояние меньше текущего, обновляем данные
                    if (!visited[neighborX][neighborY] && newDistance < distanceMap[neighborX][neighborY]) {
                        distanceMap[neighborX][neighborY] = newDistance;
                        previousEdges[neighborX][neighborY] = new Edge(currentX, currentY); // Сохраняем предшественника
                        priorityQueue.add(new EdgeDistance(neighborX, neighborY, newDistance)); // Добавляем клетку в очередь
                    }
                }
            }
        }

        // Если путь до цели не найден, выводим сообщение и возвращаем пустой путь
        if (previousEdges[targetUnit.getxCoordinate()][targetUnit.getyCoordinate()] == null) {
            System.out.println("Юнит " + sourceUnit.getName() + " не смог найти путь, чтобы аатаковать юнита " + targetUnit.getName());
            return new ArrayList<>();
        }

        // Восстанавливаем путь от цели к источнику
        List<Edge> path = new ArrayList<>();
        int currentX = targetUnit.getxCoordinate();
        int currentY = targetUnit.getyCoordinate();

        while (currentX != sourceX || currentY != sourceY) {
            path.add(new Edge(currentX, currentY)); // Добавляем текущую клетку в путь
            Edge previousEdge = previousEdges[currentX][currentY]; // Переходим к предыдущей клетке
            currentX = previousEdge.getX();
            currentY = previousEdge.getY();
        }

        path.add(new Edge(sourceX, sourceY)); // Добавляем начальную клетку
        Collections.reverse(path); // Разворачиваем путь, чтобы он шёл от источника к цели
        return path; // Возвращаем найденный путь
    }

    // Проверка, является ли клетка допустимой для перемещения
    private boolean isValid(int x, int y, Set<String> occupiedPositions, Unit targetUnit) {
        // Клетка должна быть в пределах игрового поля
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return false;
        }

        // Клетка не должна быть занята другим юнитом
        return !occupiedPositions.contains(x + "," + y);
    }
}
