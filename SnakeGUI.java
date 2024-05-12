import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

public class SnakeGUI extends JPanel {
    private static final int SQUARE_SIZE = 40;
    private static final Color COLOR_EMPTY = Color.WHITE;
    private static final Color COLOR_SNAKE = Color.GREEN;
    private static final Color COLOR_FRUIT = Color.RED;

    private Character[][] board;

    public static Timer timer;
    public static int totalMoves = 0;
    public static int totalSnakeSize = 0;

    public static int totalGames = 0;
    public static int bestGame = 0;

    public static Coord head = new Coord(4, 5);
    public static Coord tail = new Coord(4, 4);
    public static Coord nextFruit;

    public static int snakeSize = 2;
    public static int availableSquares = 0;

    public static ArrayDeque<Coord> snakeQueue = new ArrayDeque<>();
    public static ArrayList<Coord> nextMovesGlobal;

    public static boolean gameRunning = true;
    public static boolean viewPathing = true;

    public SnakeGUI(Character[][] board){
        this.board = board;
    }

    public void setBoard(Character[][] board){
        this.board = board;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        int boardWidth = board[0].length * SQUARE_SIZE;
        int boardHeight = board.length * SQUARE_SIZE;

        int xOffset = (getWidth() - boardWidth) / 2;
        int yOffset = (getHeight() - boardHeight) / 2;

        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length; j++){
                int x = j * SQUARE_SIZE + xOffset;
                int y = i * SQUARE_SIZE + yOffset;

                if(board[i][j] == '.'){
                    g.setColor(COLOR_EMPTY);
                }else if(board[i][j] == 'S'){
                    g.setColor(COLOR_SNAKE);
                }else if(board[i][j] == 'A'){
                    g.setColor(COLOR_FRUIT);
                }

                g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

                if(viewPathing && nextMovesGlobal != null && !nextMovesGlobal.isEmpty() && nextMovesGlobal.contains(new Coord(i, j))){
                    g.setColor(Color.BLUE);
                    int circleSize = SQUARE_SIZE / 2;
                    int circleX = x + (SQUARE_SIZE - circleSize) / 2;
                    int circleY = y + (SQUARE_SIZE - circleSize) / 2;
                    g.fillOval(circleX, circleY, circleSize, circleSize);
                }
            }
        }
    }

    public static void main(String[] args){
        Character[][] board = {{}};

        JFrame frame = new JFrame("SnA*ke");
        SnakeGUI snakeGUI = new SnakeGUI(board);

        frame.add(snakeGUI, BorderLayout.CENTER);

        JLabel curStatsLabel = new JLabel("Current Game's Moves: 0 | Current Game's Snake Size: 0");
        JPanel curStatsPanel = new JPanel();
        curStatsPanel.add(curStatsLabel);

        JLabel prevStatsLabel = new JLabel("Previous Game's Moves: 0 | Previous Game's Snake Size: 0");
        JPanel prevStatsPanel = new JPanel();
        prevStatsPanel.add(prevStatsLabel);

        JLabel averageStatsLabel = new JLabel("Average Moves: 0 | Average Snake Size: 0 | Best Game: 0 | Total Games: 0");
        JPanel averageStatsPanel = new JPanel();
        averageStatsPanel.add(averageStatsLabel);

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(averageStatsPanel);
        topPanel.add(prevStatsPanel);
        topPanel.add(curStatsPanel);

        JButton restartButton = new JButton("Restart");
        JPanel restartButtonPanel = new JPanel();
        restartButtonPanel.add(restartButton);

        JButton toggleViewPathingButton = new JButton("View Pathing");
        JPanel toggleViewPathingButtonPanel = new JPanel();
        toggleViewPathingButtonPanel.add(toggleViewPathingButton);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.add(restartButtonPanel);
        bottomPanel.add(toggleViewPathingButtonPanel);


        restartButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGameVariables();
                runGame(snakeGUI, curStatsLabel, prevStatsLabel, averageStatsLabel);
            }
        });

        toggleViewPathingButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPathing = !viewPathing;
            }
        });

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(topPanel, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);

        runGame(snakeGUI, curStatsLabel, prevStatsLabel, averageStatsLabel);
    }

    public static void runGame(SnakeGUI snakeGUI, JLabel curStatsLabel, JLabel prevStatsLabel, JLabel averageStatsLabel){
        Character[][] board = {{'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', 'S', 'S', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
                               {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'}};

        availableSquares = (board.length * board[0].length) - snakeSize;
        snakeQueue.addFirst(head);
        snakeQueue.addLast(tail);
        spawnFruit(board);

        timer = new Timer(150, new ActionListener(){
            private ArrayList<Coord> nextMoves = aStar(board);
            private int movesCount = 0;

            @Override
            public void actionPerformed(ActionEvent e){
                movesCount++;
                snakeGUI.setBoard(board);
                
                nextMoves = aStar(board);
                nextMovesGlobal = nextMoves;

                curStatsLabel.setText("Current Game's Moves: " + movesCount + " | Current Game's Snake Size: " + snakeSize);
    
                if(nextMoves.size() == 0 || gameRunning == false){
                    ((Timer) e.getSource()).stop();
                    totalGames++;

                    totalMoves += movesCount;
                    totalSnakeSize += snakeSize;
                    int averageSnakeSize = totalSnakeSize / totalGames;
                    int averageMoves = totalMoves / totalGames;

                    if (snakeSize > bestGame){
                        bestGame = snakeSize;
                    }

                    averageStatsLabel.setText("Average Moves: " + averageMoves + " | Average Snake Size: " + averageSnakeSize + " | Best Game: " + bestGame + " | Total Games: "+totalGames);
                    prevStatsLabel.setText("Previous Game's Moves: " + movesCount + " | Previous Game's Snake Size: " + snakeSize);

                    resetGameVariables();
                    runGame(snakeGUI, curStatsLabel, prevStatsLabel, averageStatsLabel);
                }else{
                    Coord next = nextMoves.get(nextMoves.size() - 1);
                    nextMoves.remove(nextMoves.size() - 1);
                    performStep(board, next.x, next.y);
                }
            }
        });
        timer.start();
    }

    public static void resetGameVariables(){
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        head = new Coord(4, 5);
        tail = new Coord(4, 4);
        nextFruit = null;
    
        snakeSize = 2;
        availableSquares = 0;
    
        snakeQueue.clear();
    
        gameRunning = true;
    }

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return x == coord.x && y == coord.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public static int[] possibleX = {-1, 1, 0, 0};
    public static int[] possibleY = {0, 0, -1, 1};

    public static ArrayList<Coord> aStar(Character[][] board){
        Coord cur = null;
        Coord goal = nextFruit;
        PriorityQueue<Coord> openList = new PriorityQueue<>(Comparator.comparingInt((Coord c) -> c.weight));
        boolean[][] visited = new boolean[board.length][board[0].length];

        openList.add(createWeightedCoord(board, head.x, head.y, goal, null));
        
        while(openList.size() > 0){
            cur = openList.poll();
            visited[cur.x][cur.y] = true;

            if(cur.x == goal.x && cur.y == goal.y){
                break;
            }

            for(int i=0; i<4; i++){
                if(isValid(board, cur.x + possibleX[i], cur.y + possibleY[i]) && visited[cur.x + possibleX[i]][cur.y + possibleY[i]] == false){
                    openList.add(createWeightedCoord(board, cur.x + possibleX[i], cur.y + possibleY[i], goal, cur));
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

    public static Coord createWeightedCoord(Character[][] board, int x, int y, Coord goal, Coord parent){
        int distanceToGoal = Math.abs(x - goal.x) + Math.abs(y - goal.y);
        if(isTrapped(board, x, y) || hasEnoughSpaceAroundGoal(board, goal) == false){
            return new Coord(x, y, Integer.MAX_VALUE - 5000 + distanceToGoal, parent);
        }else{
            return new Coord(x, y, distanceToGoal, parent);
        }
    }

    public static void performStep(Character[][] board, int futureX, int futureY){
        if(isValid(board, futureX, futureY)){
            snakeQueue.addFirst(new Coord(futureX, futureY));
            head = snakeQueue.peekFirst();

            if(board[futureX][futureY] == 'A'){
                snakeSize++;
                availableSquares = (board.length * board[0].length) - snakeSize;
                spawnFruit(board);
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

    public static boolean isTrapped(Character[][] board, int x, int y) {
        boolean[][] visited = new boolean[board.length][board[0].length];
        int availableMoves = dfsTrapped(board, x, y, visited);

        return availableMoves < snakeSize;
    }
    
    public static int dfsTrapped(Character[][] board, int x, int y, boolean[][] visited) {
        if(isValid(board, x, y) == false || visited[x][y]){
            return 0;
        }
    
        visited[x][y] = true;
    
        int availableMoves = 0;
        for(int i=0; i<4; i++) {
            int nextX = x + possibleX[i];
            int nextY = y + possibleY[i];
            if(isValid(board, nextX, nextY) && visited[nextX][nextY] == false){
                availableMoves += 1 + dfsTrapped(board, nextX, nextY, visited);
            }
        }
    
        return availableMoves;
    }

    public static boolean hasEnoughSpaceAroundGoal(Character[][] board, Coord goal) {
        if(isBlockedAdjacent(board, goal.x, goal.y)){
            return false;
        }
        
        boolean[][] visited = new boolean[board.length][board[0].length];
        int availableSpaces = dfsSpaces(board, goal.x, goal.y, visited);
        int threshold = snakeSize + 2;

        return availableSpaces >= threshold;
    }
    
    public static int dfsSpaces(Character[][] board, int x, int y, boolean[][] visited){
        if(isValid(board, x, y) == false || visited[x][y]){
            return 0;
        }
    
        visited[x][y] = true;
    
        int availableSpaces = 0;
        for(int i=0; i<4; i++){
            int nextX = x + possibleX[i];
            int nextY = y + possibleY[i];
            if(isValid(board, nextX, nextY) && visited[nextX][nextY] == false && board[nextX][nextY] == '.'){
                availableSpaces += 1 + dfsSpaces(board, nextX, nextY, visited);
            }
        }
    
        return availableSpaces;
    }

    public static boolean isBlockedAdjacent(Character[][] board, int x, int y){
        int count = 0;
        for(int i=0; i<4; i++){
            int nextX = x + possibleX[i];
            int nextY = y + possibleY[i];
            if(isValid(board, nextX, nextY) == false || board[nextX][nextY] != '.'){
                count++;
            }
        }

        if(count >= 3){
            return true;
        }

        return false;
    }
    
    public static boolean isValid(Character[][] board, int x, int y){
        if(x < 0 || y < 0 || x >= board.length || y >= board[0].length){
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

    public static void spawnFruit(Character[][] board){
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
