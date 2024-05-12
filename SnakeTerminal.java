import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

class Snake {
    public static class Coord{
        int x;
        int y;
        int weight;
        Coord parent;

        public Coord(int x, int y){
            this.x = x;
            this.y = y;
        }

        public Coord(int x, int y, int weight, Coord parent){
            this.x = x;
            this.y = y;
            this.weight = weight;
            this.parent = parent;
        }
    }

    public static Character[][] board = {{'.','.','.','.','.','.','.','.','.','.'},
                                         {'.','.','.','.','.','.','.','.','.','.'},
                                         {'.','.','.','.','.','.','.','.','.','.'},
                                         {'.','.','.','.','.','.','.','.','.','.'},
                                         {'.','.','.','.','S','S','.','.','.','.'},
                                         {'.','.','.','.','.','.','.','.','.','.'},
                                         {'.','.','.','.','.','.','.','.','.','.'},
                                         {'.','.','.','.','.','.','.','.','.','.'},
                                         {'.','.','.','.','.','.','.','.','.','.'},
                                         {'.','.','.','.','.','.','.','.','.','.'}};

    public static Coord head = new Coord(4, 5);
    public static Coord tail = new Coord(4, 4);
    public static Coord nextFruit;

    public static int snakeSize = 2;
    public static int availableSquares = (board.length * board[0].length) - snakeSize;

    public static ArrayDeque<Coord> snakeQueue = new ArrayDeque<>();

    public static boolean gameRunning = true;

    public static void main(String[] args){
        snakeQueue.addFirst(head);
        snakeQueue.addLast(head);
        spawnFruit();
        ArrayList<Coord> nextMoves = aStar();

        while(gameRunning){
            printBoard(board);

            if(nextMoves.size() == 0){
                nextMoves = aStar();
            }

            if(nextMoves.size() == 0){
                break;
            }

            Coord next = nextMoves.get(nextMoves.size()-1);
            nextMoves.remove(nextMoves.size()-1);
            performStep(next.x, next.y);
            
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("GAME OVER!");
    }

    public static int[] possibleX = {-1, 1, 0, 0};
    public static int[] possibleY = {0, 0, -1, 1};

    public static ArrayList<Coord> aStar(){
        Coord cur = null;
        Coord goal = nextFruit;
        PriorityQueue<Coord> openList = new PriorityQueue<>(Comparator.comparingInt((Coord c) -> c.weight));
        boolean[][] visited = new boolean[board.length][board[0].length];

        openList.add(createWeightedCoord(head.x, head.y, goal, null));
        
        while(openList.size() > 0){
            cur = openList.poll();
            visited[cur.x][cur.y] = true;

            if(cur.x == goal.x && cur.y == goal.y){
                break;
            }

            for(int i=0; i<4; i++){
                if(isValid(cur.x + possibleX[i], cur.y + possibleY[i]) && visited[cur.x + possibleX[i]][cur.y + possibleY[i]] == false){
                    openList.add(createWeightedCoord(cur.x + possibleX[i], cur.y + possibleY[i], goal, cur));
                }
            }
        }

        ArrayList<Coord> moves = new ArrayList<>();

        while(cur.parent != null){
            moves.add(cur);
            cur = cur.parent;
        }

        return moves;
    }

    public static Coord createWeightedCoord(int x, int y, Coord goal, Coord parent){
        int distanceToGoal = Math.abs(x - goal.x) + Math.abs(y - goal.y);
        if(isTrapped(x, y) || hasEnoughSpaceAroundGoal(goal) == false){
            return new Coord(x, y, Integer.MAX_VALUE - 5000 + distanceToGoal, parent);
        }else{
            return new Coord(x, y, distanceToGoal, parent);
        }
    }

    public static void performStep(int futureX, int futureY){
        if(isValid(futureX, futureY)){
            snakeQueue.addFirst(new Coord(futureX, futureY));
            head = snakeQueue.peekFirst();

            if(board[futureX][futureY] == 'A'){
                snakeSize++;
                availableSquares = (board.length * board[0].length) - snakeSize;
                spawnFruit();
            }else{
                board[tail.x][tail.y] = '.';
                snakeQueue.removeLast();
                tail = snakeQueue.peekLast();
            }

            board[head.x][head.y] = 'S';
        }else{
            gameRunning = false;
        }
    }

    public static boolean isValid(int x, int y){
        if(x < 0 || y < 0 || x >= board.length || y >= board.length){
            return false;
        }

        if(board[x][y] == 'S'){
            if(x == tail.x && y == tail.y){
                return true;
            }
            return false;
        }

        return true;
    }

    public static boolean isTrapped(int x, int y) {
        boolean[][] visited = new boolean[board.length][board[0].length];
        int availableMoves = dfsTrapped(x, y, visited);

        return availableMoves < snakeSize;
    }
    
    public static int dfsTrapped(int x, int y, boolean[][] visited) {
        if(isValid(x, y) == false || visited[x][y]){
            return 0;
        }
    
        visited[x][y] = true;
    
        int availableMoves = 0;
        for(int i=0; i<4; i++) {
            int nextX = x + possibleX[i];
            int nextY = y + possibleY[i];
            if(isValid(nextX, nextY) && visited[nextX][nextY] == false){
                availableMoves += 1 + dfsTrapped(nextX, nextY, visited);
            }
        }
    
        return availableMoves;
    }

    public static boolean hasEnoughSpaceAroundGoal(Coord goal) {
        if(isBlockedAdjacent(goal.x, goal.y)){
            return false;
        }
        
        boolean[][] visited = new boolean[board.length][board[0].length];
        int availableSpaces = dfsSpaces(goal.x, goal.y, visited);
        int threshold = snakeSize + 2;

        return availableSpaces >= threshold;
    }
    
    public static int dfsSpaces(int x, int y, boolean[][] visited){
        if(isValid(x, y) == false || visited[x][y]){
            return 0;
        }
    
        visited[x][y] = true;
    
        int availableSpaces = 0;
        for(int i=0; i<4; i++){
            int nextX = x + possibleX[i];
            int nextY = y + possibleY[i];
            if(isValid(nextX, nextY) && visited[nextX][nextY] == false && board[nextX][nextY] == '.'){
                availableSpaces += 1 + dfsSpaces(nextX, nextY, visited);
            }
        }
    
        return availableSpaces;
    }

    public static boolean isBlockedAdjacent(int x, int y){
        int count = 0;
        for(int i=0; i<4; i++){
            int nextX = x + possibleX[i];
            int nextY = y + possibleY[i];
            if(isValid(nextX, nextY) == false || board[nextX][nextY] != '.'){
                count++;
            }
        }

        if(count >= 3){
            return true;
        }

        return false;
    }

    public static void spawnFruit(){
        while(availableSquares > 0){
            int randX = randomNumber(0, 9);
            int randY = randomNumber(0, 9);

            if(board[randX][randY] != 'S' && board[randX][randY] != 'A'){
                board[randX][randY] = 'A';
                nextFruit = new Coord(randX, randY);
                break;
            }    
        }
    }

    public static void printBoard(Character[][] board){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board.length; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static int randomNumber(int max, int min){
        return (int)(Math.random() * (max - min + 1)) + min;
    }
}
