package battleship;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Map<String, Integer> ships = new LinkedHashMap<>() {{
            put("Aircraft Carrier", 5);
            put("Battleship", 4);
            put("Submarine", 3);
            put("Cruiser", 3);
            put("Destroyer", 2);
        }};
        List<List<String>> unSankShips1 = new ArrayList<>();
        List<List<String>> unSankShips2 = new ArrayList<>();
        boolean player1Turn = true;
        int listIndex = 0;
        char[][] battleBoard1 = startEmptyBoard();
        char[][] battleBoard2 = startEmptyBoard();
        char[][] foggedGame1 = startEmptyBoard();
        char[][] foggedGame2 = startEmptyBoard();

        for(int turn = 0; turn < 2; turn++) {
            String playerName;
            List<List<String>> unSankShips;
            char[][] battleBoard;

            if (player1Turn) {
                playerName = "Player 1";
                unSankShips = unSankShips1;
                battleBoard = battleBoard1;
            } else {
                playerName = "Player 2";
                unSankShips = unSankShips2;
                battleBoard = battleBoard2;
            }

            System.out.println(playerName + ", place your ships on the game field\n");
            printBoard(battleBoard);

            // placing all ships
            for (String ship : ships.keySet()) {
                System.out.printf("%nEnter the coordinates of the %s (%d cells):%n%n", ship, ships.get(ship));
                String[] chosenCoordinates = scan.nextLine().split(" ");
                while (isInvalidCoordinates(chosenCoordinates, battleBoard, ship, ships.get(ship))) {
                    chosenCoordinates = scan.nextLine().split(" ");
                }
                System.out.println();

                unSankShips.add(new ArrayList<>());
                addShip(chosenCoordinates, battleBoard, unSankShips.get(listIndex));

                listIndex++;
                printBoard(battleBoard);
            }
            System.out.println("Press Enter and pass the move to another player");
            System.out.print("...");
            scan.nextLine();
            player1Turn = !player1Turn;
            listIndex = 0;
        }
        System.out.println("\n");

        boolean gameOver = false;

        // starting the game
        while (!gameOver) {
            String playerName;
            List<List<String>> unSankShips;
            char[][] battleBoard;
            char[][] foggedGame;

            if (player1Turn) {
                playerName = "Player 1";
                unSankShips = unSankShips2;
                battleBoard = battleBoard2;
                foggedGame = foggedGame2;
            } else {
                playerName = "Player 2";
                unSankShips = unSankShips1;
                battleBoard = battleBoard1;
                foggedGame = foggedGame1;
            }

            printBoard(foggedGame);
            System.out.println("---------------------");
            printBoard(player1Turn ? battleBoard1 : battleBoard2);
            System.out.printf("%n%s, it's your turn:%n%n", playerName);

            String shot = scan.nextLine();
            while (isInvalidShot(shot)) {
                System.out.println("\nError! You entered the wrong coordinates! Try again:\n");
                shot = scan.nextLine();
                System.out.println();
            }
            turnResult(shot, battleBoard, foggedGame, unSankShips);
            gameOver = unSankShips.isEmpty();
            if (!gameOver) {
                player1Turn = !player1Turn;
                System.out.println("Press Enter and pass the move to another player");
                System.out.print("...");
                scan.nextLine();
            }
        }
    }

    private static void turnResult(String shot,
                                   char[][] battleBoard,
                                   char[][] foggedGame,
                                   List<List<String>> unSankShips) {
        int row = getMatrixRow(shot);
        int column = getMatrixColumn(shot);

        if (battleBoard[row][column] == 'O' || battleBoard[row][column] == 'X') {
            battleBoard[row][column] = 'X';
            foggedGame[row][column] = 'X';
            int wasSank = removePartOfShip(unSankShips, row, column);
            if (wasSank == 1) {
                System.out.println("\nYou sank a ship!");
            } else if (wasSank == 0){
                System.out.println("\nYou hit a ship!");
            } else {
                System.out.println("\nYou sank the last ship. You won. Congratulations!");
            }
        } else {
            battleBoard[row][column] = 'M';
            foggedGame[row][column] = 'M';
            System.out.println("\nYou missed!");
        }
    }

    private static int removePartOfShip(List<List<String>> unSankShips, int row, int column) {
        String currentPositionString = row + "," + column;
        for (List<String> list: unSankShips) {
            if (list.remove(currentPositionString)) {
                if (list.isEmpty()) {
                    unSankShips.removeIf(List::isEmpty);
                    if (unSankShips.isEmpty()) {
                        return -1;
                    }
                    return 1;
                }
            }
        }
        return 0;
    }

    private static boolean isInvalidShot(String shot) {
        if ((int) shot.charAt(0) < 65 || (int) shot.charAt(0) > 74) {
            return true;
        }
        int column = getMatrixColumn(shot);
        return column < 0 || column > 9;
    }

    private static void addShip(String[] chosenCoordinates, char[][] battleBoard, List<String> unSankShips) {
        String firstCoordinate = chosenCoordinates[0];
        String secondCoordinate = chosenCoordinates[1];

        int firstRow = getMatrixRow(firstCoordinate);
        int secondRow = getMatrixRow(secondCoordinate);
        int firstColumn = getMatrixColumn(firstCoordinate);
        int secondColumn = getMatrixColumn(secondCoordinate);

        // check if coordinate 2 is greater than one and swap if so
        if (firstRow == secondRow && firstColumn > secondColumn) {
            int aux = firstColumn;
            firstColumn = secondColumn;
            secondColumn = aux;
        } else if (firstColumn == secondColumn && firstRow > secondRow) {
            int aux = firstRow;
            firstRow = secondRow;
            secondRow = aux;
        }

        if (firstRow == secondRow) {
            for (int i = firstColumn; i <= secondColumn; i++) {
                battleBoard[firstRow][i] = 'O';
                unSankShips.add(firstRow + "," + i);
            }
        } else {
            for (int j = firstRow; j <= secondRow; j++) {
                battleBoard[j][firstColumn] = 'O';
                unSankShips.add(j + "," + firstColumn);
            }
        }
    }

    private static boolean isInvalidCoordinates(String[] chosenCoordinates, char[][] battleBoard, String shipName, int shipCells) {
        if (chosenCoordinates == null || chosenCoordinates.length < 2) {
            return true;
        }
        String firstCoordinate = chosenCoordinates[0];
        String secondCoordinate = chosenCoordinates[1];

        if ((firstCoordinate.length() != 2 && firstCoordinate.length() != 3)
                || (secondCoordinate.length() != 2 & secondCoordinate.length() != 3)) {
            return true;
        }
        if ((int) firstCoordinate.charAt(0) < 65
                || (int) firstCoordinate.charAt(0) > 74
                || (int) secondCoordinate.charAt(0) < 65
                || (int) secondCoordinate.charAt(0) > 74) {
            return true;
        }
        int firstColumn = getMatrixColumn(firstCoordinate);
        int secondColumn = getMatrixColumn(secondCoordinate);
        if (firstColumn < 0 || firstColumn > 9 || secondColumn < 0 || secondColumn > 9) {
            return true;
        }
        if (firstCoordinate.charAt(0) != secondCoordinate.charAt(0) && firstColumn != secondColumn) {
            System.out.println("\nError! Wrong ship location! Try again:\n");
            return true;
        }
        int firstRow = getMatrixRow(firstCoordinate);
        int secondRow = getMatrixRow(secondCoordinate);

        // check if coordinate 2 is greater than one and swap if so
        if (firstRow == secondRow && firstColumn > secondColumn) {
            int aux = firstColumn;
            firstColumn = secondColumn;
            secondColumn = aux;
        } else if (firstColumn == secondColumn && firstRow > secondRow) {
            int aux = firstRow;
            firstRow = secondRow;
            secondRow = aux;
        }

        if (firstRow == secondRow) {
            if (Math.abs(firstColumn - secondColumn) + 1 != shipCells) {
                System.out.printf("%nError! Wrong length of the %s! Try again:%n%n", shipName);
                return true;
            }
            for (int i = firstColumn; i <= secondColumn; i++) {
                // above check
                if (firstRow > 0 && battleBoard[firstRow - 1][i] == 'O') {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return true;
                }
                // below check
                if (firstRow < battleBoard.length - 1 && battleBoard[firstRow + 1][i] == 'O') {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return true;
                }
                // left check
                if (i > 0 && battleBoard[firstRow][i - 1] == 'O') {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return true;
                }
                // right check
                if (i < battleBoard.length - 1 && battleBoard[firstRow][i + 1] == 'O') {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return true;
                }
            }
        } else {
            if (Math.abs(firstRow - secondRow) + 1 != shipCells) {
                System.out.printf("%nError! Wrong length of the %s! Try again:%n", shipName);
                return true;
            }
            for (int j = firstRow; j <= secondRow; j++) {
                // above check
                if (j > 0 && battleBoard[j - 1][firstColumn] == 'O') {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return true;
                }
                // below check
                if (j < battleBoard.length - 1 && battleBoard[j + 1][firstColumn] == 'O') {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return true;
                }
                // left check
                if (firstColumn > 0 && battleBoard[j][firstColumn - 1] == 'O') {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return true;
                }
                // right check
                if (firstColumn < battleBoard.length - 1 && battleBoard[j][firstColumn + 1] == 'O') {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return true;
                }
            }
        }
        return false;
    }

    private static int getMatrixColumn(String coordinate) {
        int numericPart = Integer.parseInt(coordinate.substring(1));
        return numericPart - 1;
    }

    private static int getMatrixRow(String coordinate) {
        int charNumericValue = coordinate.charAt(0);
        return charNumericValue - 65;
    }

    private static void printBoard(char[][] battleBoard) {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        char c = 65;
        for (char[] chars : battleBoard) {
            System.out.print(c + " ");
            for (int j = 0; j < battleBoard.length; j++) {
                System.out.print(chars[j] + " ");
            }
            System.out.println();
            c++;
        }
    }

    private static char[][] startEmptyBoard() {
        char[][] battleBoard = new char[10][10];
        for (char[] chars : battleBoard) {
            Arrays.fill(chars, '~');
        }
        return battleBoard;
    }
}
