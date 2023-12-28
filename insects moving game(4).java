import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;

import static java.util.Arrays.copyOfRange;

public final class Main {
    /**
     * Constants for boundaries and indexes
     */
    private static final int MINBOARD = 4;
    private static final int MAXBOARD = 1000;
    private static final int MINAMOUNT = 1;
    private static final int MAXAMOUNT = 16;
    private static final int MINFOOD = 1;
    private static final int MAXFOOD = 200;
    private static final int Z = 0;
    private static final int O = 1;
    private static final int T = 2;
    private static final int TH = 3;

    /**
     * Board for the current game
     */
    private static Board gameBoard;

    public static void main(String[] args) throws IOException {
        FileReader input = new FileReader("input.txt");
        BufferedReader reader = new BufferedReader(input);
        FileWriter file = new FileWriter("output.txt");
        BufferedWriter output = new BufferedWriter(file);
        /**
         * Inputs checking, throwing exceptions if some mistakes are caught
         */
        try {
            int boardSize = Integer.parseInt(reader.readLine());
            if (boardSize < MINBOARD || boardSize > MAXBOARD) {
                throw new InvalidBoardSizeException();
            }
            gameBoard = new Board(boardSize);
            int insectsAmount = Integer.parseInt(reader.readLine());
            if (insectsAmount < MINAMOUNT || insectsAmount > MAXAMOUNT) {
                throw new InvalidNumberOfInsectsException();
            }
            int foodPoints = Integer.parseInt(reader.readLine());
            if (foodPoints < MINFOOD || foodPoints > MAXFOOD) {
                throw new InvalidNumberOfFoodPointsException();
            }

            for (int n = 0; n < insectsAmount; ++n) {
                String[] insectString = reader.readLine().split(" ");
                if (!colorChecker(insectString[Z].toLowerCase())) {
                    throw new InvalidInsectColorException();
                }
                if (!typeChecker(insectString[O].toLowerCase())) {
                    throw new InvalidInsectTypeException();
                }
                if (positionChecker(Integer.parseInt(insectString[T]), Integer.parseInt(insectString[TH]), boardSize)) {
                    throw new InvalidEntityPositionException();
                }
                if (twoSameInsects(InsectColor.toColor(insectString[Z]), insectString[O])) {
                    throw new DuplicateInsectException();
                }

                Insect insectOnBoard;
                switch (insectString[O].toLowerCase()) {
                    case "grasshopper":
                        insectOnBoard = new Grasshopper(
                                new EntityPosition(Integer.parseInt(insectString[T]),
                                        Integer.parseInt(insectString[TH])), InsectColor.toColor(insectString[Z]));
                        break;

                    case "ant":
                        insectOnBoard = new Ant(
                                new EntityPosition(Integer.parseInt(insectString[T]),
                                        Integer.parseInt(insectString[TH])), InsectColor.toColor(insectString[Z]));
                        break;
                    case "butterfly":
                        insectOnBoard = new Butterfly(
                                new EntityPosition(Integer.parseInt(insectString[T]),
                                        Integer.parseInt(insectString[TH])), InsectColor.toColor(insectString[Z]));
                        break;
                    default:
                        insectOnBoard = new Spider(
                                new EntityPosition(Integer.parseInt(insectString[T]),
                                        Integer.parseInt(insectString[TH])), InsectColor.toColor(insectString[Z]));
                        break;
                }

                if (samePosition(insectString[T] + "|" + insectString[TH])) {
                    throw new TwoEntitiesOnSamePositionException();
                }

                gameBoard.addEntity(insectOnBoard);
            }
            for (int m = 0; m < foodPoints; ++m) {
                String[] foodString = reader.readLine().split(" ");
                if (positionChecker(Integer.parseInt(foodString[T]), Integer.parseInt(foodString[O]), boardSize)) {
                    throw new InvalidEntityPositionException();
                }

                FoodPoint foodOnBoard = new FoodPoint(
                        new EntityPosition(Integer.parseInt(foodString[O]),
                                Integer.parseInt(foodString[T])), Integer.parseInt(foodString[Z]));

                if (samePosition(foodString[O] + "|" + foodString[T])) {
                    throw new TwoEntitiesOnSamePositionException();
                }

                gameBoard.addEntity(foodOnBoard);
            }

            insectsLoop(output);
            output.close();

        } catch (Exception e) {
            output.write(e.getMessage());
            output.newLine();
            output.close();
        }
    }

    /**
     * Cycle for all entities: going through the all insects and outputting the directions + value of eaten food
     */
    public static void insectsLoop(BufferedWriter output) {
        for (Map.Entry entry : gameBoard.getEntries()) {
            if (entry.getValue() instanceof Insect) {

                Direction direction = gameBoard.getDirection((Insect) entry.getValue());
                int totalEaten = gameBoard.getDirectionsSum((Insect) entry.getValue());
                printAnswer(((Insect) entry.getValue()).color, (Insect) entry.getValue(),
                        direction, totalEaten, output);
            }
        }
    }

    /**
     * Function for printing correct answers
     */
    public static void printAnswer(InsectColor color, Insect insect, Direction direction,
                                   int totalEaten, BufferedWriter output) {
        try {
            output.write(color.getInsectColor() + " " + Insect.toStringInsect(insect)
                    + " " + direction.getTextRepresentation() + " " + totalEaten + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checker for existence of two same insects
     */
    public static boolean twoSameInsects(InsectColor color, String name) {
        for (Map.Entry entry : gameBoard.getEntries()) {
            if (entry.getValue() instanceof Insect) {
                InsectColor entityColor = ((Insect) entry.getValue()).getColor();
                String insectName = Insect.toStringInsect((Insect) entry.getValue());
                if (entityColor == color && name.equals(insectName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checker for existence of two entities in same insects
     */
    public static boolean samePosition(String s) {
        for (Map.Entry entry : gameBoard.getEntries()) {
            if (entry.getKey().equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checker for correspondence of the color
     */
    public static boolean colorChecker(String s) {
        return (s.equals("red") || s.equals("blue") || s.equals("green") || s.equals("yellow"));
    }

    /**
     * Checker for correspondence of the insect type
     */
    public static boolean typeChecker(String s) {
        return (s.equals("ant") || s.equals("spider") || s.equals("butterfly") || s.equals("grasshopper"));
    }

    /**
     * Checker for the correct position on board
     */
    public static boolean positionChecker(int a, int b, int size) {
        return (a > size || b > size || a < 1 || b < 1);
    }
}


/**
 * Class of the Board
 */
class Board {
    private final Map<String, BoardEntity> boardData;

    private final int size;

    public Board(int boardSize) {
        this.size = boardSize;
        boardData = new LinkedHashMap<>();
    }

    /**
     * Adding entity to the board's Map
     */
    public void addEntity(BoardEntity entity) {
        boardData.put(entity.getEntityPosition().toString(), entity);
    }

    /**
     * Returning entity from the board's Map
     */
    public BoardEntity getEntity(EntityPosition position) {
        return boardData.get(position.toString());
    }

    /**
     *  Returning direction to move on
     */
    public Direction getDirection(Insect insect) {
        return insect.getBestDirection(boardData, size);
    }

    /**
     *  Returning size of eaten food and removing insect from the board
     */
    public int getDirectionsSum(Insect insect) {
        int ans = insect.travelDirection(getDirection(insect), boardData, size);
        boardData.remove(insect.getEntityPosition().toString());
        return ans;
    }

    /**
     *  Returning array of 'tuples' in format: [(key, value), (key1, value1), ...]
     */
    public Map.Entry[] getEntries() {
        return boardData.entrySet().toArray(new Map.Entry[0]);
    }
}


/**
 *  Class of the position of entity
 */
class EntityPosition {
    private final int column;
    private final int row;

    public EntityPosition(int row, int column) {
        this.column = column;
        this.row = row;
    }

    /**
     *  Returning string representation of the position (row|column)
     */
    @Override
    public String toString() {
        return row + "|" + column;
    }

    /**
     *  Getter of column value
     */
    public int getColumn() {
        return column;
    }

    /**
     *  Getter of row value
     */
    public int getRow() {
        return row;
    }
}


/**
 *  Abstract class for entities on board
 */
abstract class BoardEntity {
    protected EntityPosition entityPosition;

    public EntityPosition getEntityPosition() {
        return entityPosition;
    }
}


/**
 *  Class for food, subclass of BoardEntity
 */
class FoodPoint extends BoardEntity {
    protected int value;
    public FoodPoint(EntityPosition position, int value) {
        this.entityPosition = position;
        this.value = value;
    }

    /**
     *  Getter of the food value
     */
    public int getValue() {
        return value;
    }
}


/**
 *  Class for insects, subclass of BoardEntity
 */
abstract class Insect extends BoardEntity {
    protected InsectColor color;

    /**
     * All values in the correct order from the Direction (enum)
     */
    protected Direction[] directionList = Direction.values();

    public Insect(EntityPosition position, InsectColor color) {
        this.entityPosition = position;
        this.color = color;
    }

    /**
     *  Declaration of function, which returns the amount of
     *  food in each given direction (without deleting entities)
     *  Function is used for all Insect entities
     */
    protected abstract int availableSafeMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize);

    /**
     *  Declaration of function, which returns the amount of
     *  food in each given direction (with deleting entities).
     *  Function is used for all Insect entities
     */
    protected abstract int availableMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize);

    /**
     *  Returning the most optimal direction to move on
     */
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int max = -1;
        Direction bestDirection = directionList[0];

        for (Direction dir : directionList) {
            int visibleValue = availableSafeMove(dir, boardData, boardSize);
            if (visibleValue > max) {
                max = visibleValue;
                bestDirection = dir;
            }
        }
        return bestDirection;
    }

    /**
     *  Returning the amount of food + removing entities
     */
    public int travelDirection(Direction direction, Map<String, BoardEntity> boardData, int size) {
        return availableMove(direction, boardData, size);
    }

    /**
     *  'Getter' of the string representation of Insect subclass
     */
    public static String toStringInsect(Insect entity) {
        if (entity instanceof Butterfly) {
            return "Butterfly";
        } else if (entity instanceof Ant) {
            return "Ant";
        } else if (entity instanceof Spider) {
            return "Spider";
        } else {
            return "Grasshopper";
        }
    }

    /**
     *  Getter of the InsectColor
     */
    public InsectColor getColor() {
        return color;
    }
}


/**
 *  Class for Butterflies, subclass of Insect
 */
class Butterfly extends Insect implements OrthogonalMoving {
    public Butterfly(EntityPosition position, InsectColor color) {
        super(position, color);
        super.directionList = copyOfRange(super.directionList, Direction.N.ordinal(), Direction.NE.ordinal());
    }

    /**
     *  Returning the number, which will correspond to the orthogonal shift in the coordinate system.
     *  This insect moves skipping some fields, => shift will be 1)
     */
    @Override
    public int getMultiplier() {
        return 1;
    }

    /**
     *  Butterfly can only move in orthogonal directions
     */
    @Override
    public int availableSafeMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    @Override
    public int availableMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
    }
}


/**
 *  Class for Ants, subclass of Insect
 */
class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {
    public Ant(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    /**
     *  Returning the number, which will correspond to the orthogonal shift in the coordinate system.
     *  This insect moves without skipping some fields, => shift will be 1)
     */
    @Override
    public int getMultiplier() {
        return 1;
    }

    /**
     *  Ant can move in different directions
     */
    @Override
    public int availableSafeMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        switch (dir) {
            case N:
                return getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
            case S:
                return getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
            case W:
                return getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
            case E:
                return getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
            default:
                return getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
        }
    }

    @Override
    public int availableMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        switch (dir) {
            case N:
                return travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
            case S:
                return travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
            case W:
                return travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
            case E:
                return travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
            default:
                return travelDiagonally(dir, entityPosition, boardData, boardSize);
        }
    }
}


/**
 *  Class for Spiders, subclass of Insect
 */
class Spider extends Insect implements DiagonalMoving {
    public Spider(EntityPosition position, InsectColor color) {
        super(position, color);

        /**
         *  copyOfRange copies the specified range of the array into a new array, in this case
         *  it copies range to the main array in the superClass
         */
        super.directionList = copyOfRange(super.directionList, Direction.NE.ordinal(), Direction.NW.ordinal() + 1);
    }

    /**
     *  Spiders can only move in diagonal directions
     */
    @Override
    public int availableSafeMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    @Override
    public int availableMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return travelDiagonally(dir, entityPosition, boardData, boardSize);
    }
}


/**
 *  Class for Grasshoppers, subclass of Insect
 */
class Grasshopper extends Insect implements OrthogonalMoving {

    public Grasshopper(EntityPosition position, InsectColor color) {

        super(position, color);
        super.directionList = copyOfRange(super.directionList, Direction.N.ordinal(), Direction.NE.ordinal());
    }

    /**
     *  Returning the number, which will correspond to the orthogonal shift in the coordinate system.
     *  This insect moves skipping some fields, => shift will be 2)
     */
    @Override
    public int getMultiplier() {
        return 2;
    }

    @Override
    public int availableSafeMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    @Override
    public int availableMove(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
    }


}


/**
 *  Interface for orthogonally moving insects
 */
interface OrthogonalMoving {
    public int getMultiplier();

    /**
     *  Returning the amount of eaten food without deleting entities (just checking)
     */
    public default int getOrthogonalDirectionVisibleValue(
            Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        int eatenFood = 0;
        int x = entityPosition.getRow();
        int y = entityPosition.getColumn();
        int multiplier = getMultiplier();

        int divX = 0;
        int divY = 0;
        switch (dir) {
            case N:
                divX = -1;
                break;
            case E:
                divY = 1;
                break;
            case S:
                divX = 1;
                break;
            default:
                divY = -1;
                break;
        }
        divY *= multiplier;
        divX *= multiplier;

        for (x += divX, y += divY; x >= 1 && x <= boardSize && y >= 1 && y <= boardSize; x += divX, y += divY) {
            if (boardData.containsKey(new EntityPosition(x, y).toString())) {
                BoardEntity entity = boardData.get(new EntityPosition(x, y).toString());
                if (entity instanceof FoodPoint) {
                    eatenFood += ((FoodPoint) entity).getValue();
                }
            }
        }
        return eatenFood;
    }


    /**
     *  Returning the amount of eaten food with deleting entities
     */
    public default int travelOrthogonally(
            Direction dir, EntityPosition entityPosition, InsectColor color,
            Map<String, BoardEntity> boardData, int boardSize) {

        int eatenFood = 0;

        int x = entityPosition.getRow();
        int y = entityPosition.getColumn();

        int multiplier = getMultiplier();

        int divX = 0;
        int divY = 0;
        switch (dir) {
            case N:
                divX = -1;
                break;
            case E:
                divY = 1;
                break;
            case S:
                divX = 1;
                break;
            default:
                divY = -1;
                break;
        }
        divY *= multiplier;
        divX *= multiplier;
        for (x += divX, y += divY; x >= 1 && x <= boardSize && y >= 1 && y <= boardSize; x += divX, y += divY) {
            if (boardData.containsKey(new EntityPosition(x, y).toString())) {
                BoardEntity entity = boardData.get(new EntityPosition(x, y).toString());
                if (entity instanceof FoodPoint) {
                    boardData.remove(entity.entityPosition.toString());
                    eatenFood += ((FoodPoint) entity).getValue();
                } else if (entity instanceof Insect) {
                    if (((Insect) entity).color != ((Insect) boardData.get(entityPosition.toString())).color) {
                        return eatenFood;
                    }
                }
            }
        }
        return eatenFood;

    }
}


/**
 *  Interface for orthogonally moving insects
 */
interface DiagonalMoving {

    /**
     *  Returning the amount of eaten food without deleting entities (just checking)
     */
    public default int getDiagonalDirectionVisibleValue(
            Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        int eatenFood = 0;
        int x = entityPosition.getRow();
        int y = entityPosition.getColumn();

        int divX = 0;
        int divY = 0;
        switch (dir) {
            case NE:
                divX = -1;
                divY = 1;
                break;
            case SE:
                divX = 1;
                divY = 1;
                break;
            case SW:
                divX = 1;
                divY = -1;
                break;
            default:
                divX = -1;
                divY = -1;
                break;
        }
        for (x += divX, y += divY; x >= 1 && x <= boardSize && y >= 1 && y <= boardSize; x += divX, y += divY) {
            if (boardData.containsKey(new EntityPosition(x, y).toString())) {
                BoardEntity entity = boardData.get(new EntityPosition(x, y).toString());
                if (entity instanceof FoodPoint) {
                    eatenFood += ((FoodPoint) entity).getValue();
                }
            }
        }
        return eatenFood;
    }


    /**
     *  Returning the amount of eaten food with deleting entities
     */
    public default int travelDiagonally(
            Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        int eatenFood = 0;
        int x = entityPosition.getRow();
        int y = entityPosition.getColumn();
        int i;
        int j;

        int divX = 0;
        int divY = 0;
        switch (dir) {
            case NE:
                divX = -1;
                divY = 1;
                break;
            case SE:
                divX = 1;
                divY = 1;
                break;
            case SW:
                divX = 1;
                divY = -1;
                break;
            default:
                divX = -1;
                divY = -1;
                break;
        }
        for (x += divX, y += divY; x >= 1 && x <= boardSize && y >= 1 && y <= boardSize; x += divX, y += divY) {
            if (boardData.containsKey(new EntityPosition(x, y).toString())) {
                BoardEntity entity = boardData.get(new EntityPosition(x, y).toString());
                if (entity instanceof FoodPoint) {
                    eatenFood += ((FoodPoint) entity).getValue();
                    boardData.remove(entity.entityPosition.toString());
                } else if (entity instanceof Insect) {
                    if (((Insect) entity).color != ((Insect) boardData.get(entityPosition.toString())).color) {
                        return eatenFood;
                    }
                }
            }
        }
        return eatenFood;
    }

}


/**
 *  Enum for the colors of insects
 */
enum InsectColor {
    RED,
    GREEN,
    BLUE,
    YELLOW;

    public static InsectColor toColor(String s) {
        return InsectColor.valueOf(s.toUpperCase());
    }

    /**
     *  Getter of string representation of the current color
     */
    public String getInsectColor() {
        switch (this) {
            case RED:
                return "Red";
            case GREEN:
                return "Green";
            case BLUE:
                return "Blue";
            default:
                return "Yellow";
        }
    }
}


/**
 *  Enum for available directions
 */
enum Direction {
    N("North"),
    E("East"),
    S("South"),
    W("West"),
    NE("North-East"),
    SE("South-East"),
    SW("South-West"),
    NW("North-West");

    private final String textRepresentation;

    private Direction(String s) {
        this.textRepresentation = s;
    }

    /**
     *  Getter of string representation of the current direction
     */
    public String getTextRepresentation() {
        return textRepresentation;
    }
}


/**
 * Special cases of Exceptions for the wrong inputs
 */
class InvalidBoardSizeException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid board size";
    }
}

class InvalidNumberOfInsectsException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid number of insects";
    }
}

class InvalidNumberOfFoodPointsException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid number of food points";
    }
}

class InvalidInsectColorException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid insect color";
    }
}

class InvalidInsectTypeException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid insect type";
    }
}

class InvalidEntityPositionException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid entity position";
    }
}

class DuplicateInsectException extends Exception {
    @Override
    public String getMessage() {
        return "Duplicate insects";
    }
}

class TwoEntitiesOnSamePositionException extends Exception {
    @Override
    public String getMessage() {
        return "Two entities in the same position";
    }
}
