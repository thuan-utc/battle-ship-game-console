import board_component.Cell;
import cell_enum.CellStage;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Bot extends Player {
    // attack only white or Black
    public static boolean attackWhite = true; // target parity

    public Bot(String playerName) {
        super(playerName);
    }

    public static void updateBoardProbability(Board board, AttackResult result) throws Exception {
        Cell lastAttack = board.findCellByName(result.getCellName());
        if (lastAttack == null) {
            return;
        }
        lastAttack.setStage(result.getResult());
        if (result.getSunkShipName() == null) {
            if (lastAttack.getStage() == CellStage.HIT_SUCCESS) {
                // tăng chỉ số khả năng chứa tàu của 4 ô kề cùng hàng, cột //hunting
                String cellName = lastAttack.getName();
                increaseProbability(board, cellName);
            }
        } else {
            // liệt kê ra các cell thuộc tàu đã chìm, các cell xung quoanh nếu lớn hown 0 và chưa bị bắn phải giảm đi 1
            result.getListCellShipSunk().forEach(currentCellName -> decreaseProbability(board, currentCellName));
        }
        // ccần implement đánh từ giữa ra, tìm cacs vùng có khả năng chứa tàu lớn nhất chưa chìm
    }

    private static void increaseProbability(Board board, String cellName) {
        char columnName = cellName.charAt(0);
        Character rowName = cellName.charAt(1);
        int row, columnn;
        row = Integer.parseInt(String.valueOf(rowName)) - 1;
        columnn = (int) columnName - (int) 'A';
        int positionInList100;
        Cell cell;
        //update up
        positionInList100 = (row - 1) * 10 + columnn;
        if (positionInList100 < 100 && positionInList100 >= 0) {
            cell = board.getCellList().get(positionInList100);
            if (cell.getStage() == CellStage.NOT_HIT) {
                cell.setProbabilityContainsShip(cell.getProbabilityContainsShip() + 1);
            }
        }
        //update down
        positionInList100 = (row + 1) * 10 + columnn;
        if (positionInList100 < 100 && positionInList100 >= 0) {
            cell = board.getCellList().get(positionInList100);
            if (cell.getStage() == CellStage.NOT_HIT) {
                cell.setProbabilityContainsShip(cell.getProbabilityContainsShip() + 1);
            }
        }
        //update left
        positionInList100 = row * 10 + columnn - 1;
        if (positionInList100 < 100 && positionInList100 >= 0) {
            cell = board.getCellList().get(positionInList100);
            if (cell.getStage() == CellStage.NOT_HIT) {
                cell.setProbabilityContainsShip(cell.getProbabilityContainsShip() + 1);
            }
        }
        //update right
        positionInList100 = row * 10 + columnn + 1;
        if (positionInList100 < 100 && positionInList100 >= 0) {
            cell = board.getCellList().get(positionInList100);
            if (cell.getStage() == CellStage.NOT_HIT) {
                cell.setProbabilityContainsShip(cell.getProbabilityContainsShip() + 1);
            }
        }
    }

    private static void decreaseProbability(Board board, String cellName) {
        char columnName = cellName.charAt(0);
        Character rowName = cellName.charAt(1);
        int row, column;
        row = Integer.parseInt(String.valueOf(rowName)) - 1;
        column = (int) columnName - (int) 'A';
        int positionInList100;
        Cell cell;
        //update up
        positionInList100 = (row - 1) * 10 + column;
        if (positionInList100 < 100 && positionInList100 >= 0) {
            cell = board.getCellList().get(positionInList100);
            if (cell.getStage() == CellStage.NOT_HIT && cell.getProbabilityContainsShip() > 0) {
                cell.setProbabilityContainsShip(cell.getProbabilityContainsShip() - 1);
            }
        }
        //update down
        positionInList100 = (row + 1) * 10 + column;
        if (positionInList100 < 100 && positionInList100 >= 0) {
            cell = board.getCellList().get(positionInList100);
            if (cell.getStage() == CellStage.NOT_HIT && cell.getProbabilityContainsShip() > 0) {
                cell.setProbabilityContainsShip(cell.getProbabilityContainsShip() - 1);
            }
        }
        //update left
        positionInList100 = row * 10 + column - 1;
        if (positionInList100 < 100 && positionInList100 >= 0) {
            cell = board.getCellList().get(positionInList100);
            if (cell.getStage() == CellStage.NOT_HIT && cell.getProbabilityContainsShip() > 0) {
                cell.setProbabilityContainsShip(cell.getProbabilityContainsShip() - 1);
            }
        }
        //update right
        positionInList100 = row * 10 + column + 1;
        if (positionInList100 < 100 && positionInList100 >= 0) {
            cell = board.getCellList().get(positionInList100);
            if (cell.getStage() == CellStage.NOT_HIT && cell.getProbabilityContainsShip() > 0) {
                cell.setProbabilityContainsShip(cell.getProbabilityContainsShip() - 1);
            }
        }
    }

    public static String findNextAttack(Board board) {
        boolean attackRandom = true;
        int maxProb = 0;
        List<Cell> cellList = board.getCellList();
        for (Cell cell : cellList) {
            if (cell.getProbabilityContainsShip() > maxProb) {
                maxProb = cell.getProbabilityContainsShip();
                attackRandom = false;
            }
        }
        List<Cell> selectedCells;
        Random random = new Random();
        if (attackRandom) {
            if (attackWhite) {
                selectedCells = cellList.stream()
                        .filter(c -> c.getColor().equalsIgnoreCase("white") && c.getStage() == CellStage.NOT_HIT)
                        .toList();
            } else {
                selectedCells = cellList.stream()
                        .filter(c -> c.getColor().equalsIgnoreCase("black") && c.getStage() == CellStage.NOT_HIT)
                        .toList();
            }
        } else {
            int finalMaxProb = maxProb;
            selectedCells = cellList.stream()
                    .filter(c -> c.getProbabilityContainsShip() == finalMaxProb)
                    .collect(Collectors.toList());
        }
        return selectedCells.get(random.nextInt(selectedCells.size())).getName();
    }
}