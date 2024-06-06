//Import das bibliotecas Java para a lógica e Swing para interface

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
    //Variáveis globais para controlar o estado da partida
    //e mostrar o que está acontecendo na interface gráfica

    //Tamanho de cada quadrado e cores que serão utilizadas
    private static final int SQUARE_SIZE = 40;
    private static final Color COLOR_EMPTY = Color.WHITE;
    private static final Color COLOR_SNAKE = Color.GREEN;
    private static final Color COLOR_FRUIT = Color.RED;

    //Array de char para representar o campo
    private Character[][] board;
    
    //Timer para o game loop, e variáveis que guardam
    //as estatísticas de cada partida
    public static Timer timer;
    public static int totalMoves = 0;
    public static int totalSnakeSize = 0;

    public static int totalGames = 0;
    public static int bestGame = 0;

    //Coordenadas iniciais da Snake e da fruta
    public static Coord head = new Coord(4, 5);
    public static Coord tail = new Coord(4, 4);
    public static Coord nextFruit;

    //Tamanho inicial da Snake
    public static int snakeSize = 2;
    public static int availableSquares = 0;

    //Fila que representa a Snake para simular o seu movimento
    public static ArrayDeque<Coord> snakeQueue = new ArrayDeque<>();
    //Lista que armazena os movimentos futuros da Snake
    public static ArrayList<Coord> nextMovesGlobal;

    //Booleanas de controle para iniciar um novo jogo e ver o
    //caminho atual da Snake
    public static boolean gameRunning = true;
    public static boolean viewPathing = true;

    //Instanciando a classe com o campo de array de char
    public SnakeGUI(Character[][] board){
        this.board = board;
    }

    //Função que atualiza o campo em cada quadro
    public void setBoard(Character[][] board){
        this.board = board;
        repaint();
    }

    //Função para mostrar os componentes da interface
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        //Grid com o tamnho estabelecido na variável global
        int boardWidth = board[0].length * SQUARE_SIZE;
        int boardHeight = board.length * SQUARE_SIZE;

        int xOffset = (getWidth() - boardWidth) / 2;
        int yOffset = (getHeight() - boardHeight) / 2;

        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length; j++){
                int x = j * SQUARE_SIZE + xOffset;
                int y = i * SQUARE_SIZE + yOffset;

                //Pintamos cada quadrado de sua cor correspondente
                //Snake = verde
                //Fruta = vermelho
                //Vazio = bracno
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

                //Pintamos o caminho atual da Snake
                //com circulos azuis
                //por isso que temos a lista dos próximos
                //movimentos da Snake de forma global
                //
                //Também verificamos se a variável viewPathing é True
                //basicamente um toggle se o caminho é visível ou não
                //que é controlado por um botão na interface
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

    //Função main que é o ponto de entrada do código
    public static void main(String[] args){
        //Instanciamos um campo vazio
        Character[][] board = {{}};

        //Instanciamos um novo objeto SnakeGUI
        //e estabelecemos a interface gráfica
        //Basicamente título da janela
        //depois as linhas com as estatísticas das partidas
        //depois o campo do jogo em si
        //depois os botões de recomeçar e mudar a visibilidade do caminho da Snake
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

        //Funcionalidade do botão de restart
        restartButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Basicamente resetamos as variáveis de controle para o seu
                //estado inicial e chamamos o run game novamente para iniciar
                //uma nova partida
                resetGameVariables();
                runGame(snakeGUI, curStatsLabel, prevStatsLabel, averageStatsLabel);
            }
        });

        //Funcionalidade do botão de visibilidade do caminho
        toggleViewPathingButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Como a lógica de renderização em cada quadro verifica
                //se esta variável é true ou não na hora de mostrar o 
                //caminho atual da Snake, este botão é bem simples
                //apenas invertendo essa booleana
                viewPathing = !viewPathing;
            }
        });

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(topPanel, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);

        //Finalmente chamamos a função inicial para iniciar uma partida
        runGame(snakeGUI, curStatsLabel, prevStatsLabel, averageStatsLabel);
    }

    //Função de game loop
    public static void runGame(SnakeGUI snakeGUI, JLabel curStatsLabel, JLabel prevStatsLabel, JLabel averageStatsLabel){
        //Instanciamos o campo em seu estado inicial
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

        //Calculamos quantos quadrados estão disponíveis para fins de controle
        availableSquares = (board.length * board[0].length) - snakeSize;
        //Iniciamos a fila da Snake com a sua cabeça e rabo
        snakeQueue.addFirst(head);
        snakeQueue.addLast(tail);
        //Chamamos a função auxiliar para criar a próxima fruta no campo
        spawnFruit(board);

        //Instanciamos um novo timer para o game loop
        //Ele pode ser ajustado para rodar o jogo mais rápido ou devagar
        timer = new Timer(150, new ActionListener(){
            //Instanciamos uma lista que recebe os próximos movimentos
            //calculados pelo algoritmo A*
            private ArrayList<Coord> nextMoves = aStar(board);
            //Também iniciamos um contador de movimentos da Snake para
            //fins de armazenar estatísticas das partidas
            private int movesCount = 0;

            @Override
            public void actionPerformed(ActionEvent e){
                //Em cada movimento incrementamos o contador
                movesCount++;
                //Atualizamos o campo para o seu estado atual
                //para ser desenhado na tela
                snakeGUI.setBoard(board);
                
                //Calculamos os próximos movimentos com o A*
                nextMoves = aStar(board);
                nextMovesGlobal = nextMoves;

                //Atualizamos as estatísticas da partida atual
                curStatsLabel.setText("Current Game's Moves: " + movesCount + " | Current Game's Snake Size: " + snakeSize);
                
                //Se não existem movimentos válidos, entramos neste
                //branch para terminar o jogo
                if(nextMoves.size() == 0 || gameRunning == false){
                    //O timer é parado
                    ((Timer) e.getSource()).stop();
                    //A quantidade de partidas é incrementada
                    totalGames++;

                    //O total de movimentos é somado e calculamos as médias
                    totalMoves += movesCount;
                    totalSnakeSize += snakeSize;
                    int averageSnakeSize = totalSnakeSize / totalGames;
                    int averageMoves = totalMoves / totalGames;

                    if(snakeSize > bestGame){
                        bestGame = snakeSize;
                    }

                    //Finalmente atualizamos as infromações estatísticas nas tela
                    //conforme calculado anteriormente
                    averageStatsLabel.setText("Average Moves: " + averageMoves + " | Average Snake Size: " + averageSnakeSize + " | Best Game: " + bestGame + " | Total Games: "+totalGames);
                    prevStatsLabel.setText("Previous Game's Moves: " + movesCount + " | Previous Game's Snake Size: " + snakeSize);

                    //Chamamos a função auxiliar para resetar as variáveis de controle
                    //para o seu estado inicial
                    resetGameVariables();
                    //Chamamos esta mesma função para inciar a próxima partida
                    runGame(snakeGUI, curStatsLabel, prevStatsLabel, averageStatsLabel);
                }else{
                    //Caso contrário, ou seja, se existem movimentos válidos
                    //a partida continua normalmente
                    //
                    //Pegamos as coordenadas do próximo movimento e executamos ele
                    Coord next = nextMoves.get(nextMoves.size() - 1);
                    nextMoves.remove(nextMoves.size() - 1);
                    performStep(board, next.x, next.y);
                }
            }
        });
        timer.start();
    }

    //Função auxiliar que retorna todas as variáveis ao seu estado inicial
    public static void resetGameVariables(){
        if(timer != null && timer.isRunning()){
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

    //Classe auxiliar que define uma coordenada
    public static class Coord{
        //Tem os valores x e y no campo
        int x;
        int y;
        //Temos também o peso para o A*
        int weight;
        //Finalmente temos a coordenada pai para desenhar o caminho
        Coord parent;

        //Dois construtures, um mais básico apenas com coordenadas
        //Já o segundo é instanciado com peso e um pai
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

        //Override na função geral do java de comparação (equals)
        //para podermos determinar se duas coordenadas são de fato iguais
        @Override
        public boolean equals(Object o){
            if(this == o){
                return true;
            }
            if(o == null || getClass() != o.getClass()){
                return false;
            }
            Coord coord = (Coord) o;
            return x == coord.x && y == coord.y;
        }

        @Override
        public int hashCode(){
            return Objects.hash(x, y);
        }
    }

    //Arrays auxliares para controlar os movimentos possíveis no campo
    //cima = x - 1 e y + 0
    //baixo = x + 1 e y + 0
    //esquerda = x + 0 e y - 1
    //direita = x + 0 e y + 1
    public static int[] possibleX = {-1, 1, 0, 0};
    public static int[] possibleY = {0, 0, -1, 1};

    //Implementação do algoritmo A*
    public static ArrayList<Coord> aStar(Character[][] board){
        //Instanciamos uma PriorityQueue, de forma minHeap
        //para nos servir como a lista aberta do algoritmo
        Coord cur = null;
        Coord goal = nextFruit;
        //O comparador customizado da minHeap é o peso das coordenadas
        //ou seja, o menor peso sempre é o primeiro elemento
        PriorityQueue<Coord> openList = new PriorityQueue<>(Comparator.comparingInt((Coord c) -> c.weight));
        //Instanciamos uma array de booleanas como nossa lista fechada
        //ela começa com todos os campos como false, e na medida
        //que vamos vizitando as coordenadas, marcamos como true
        boolean[][] visited = new boolean[board.length][board[0].length];

        //Iniciamos a lista aberta com a coordenada atual da cabeça da Snake
        openList.add(createWeightedCoord(board, head.x, head.y, goal, null));
        
        //While que roda enquanto tem coordenadas na lista aberta
        while(openList.size() > 0){
            //Pegamos o elemento atual da priority queue, ou seja,
            //a coordenada com o menor peso
            cur = openList.poll();
            //marcamos ela como visitada, ou seja, colocamos na lista
            //fechada
            visited[cur.x][cur.y] = true;

            //Critério de parada que sai do loop while se um caminho
            //até o objetivo (próxima fruta) foi encontrado
            if(cur.x == goal.x && cur.y == goal.y){
                break;
            }

            //verficamos os movimentos possíveis, cima, baixo, esquerda, direita
            //se são coordenadas válidas no nosso campo, e também se já foram visitadas
            //caso possível, adicionamos na lista aberta
            for(int i=0; i<4; i++){
                if(isValid(board, cur.x + possibleX[i], cur.y + possibleY[i]) && visited[cur.x + possibleX[i]][cur.y + possibleY[i]] == false){
                    openList.add(createWeightedCoord(board, cur.x + possibleX[i], cur.y + possibleY[i], goal, cur));
                }
            }
        }

        //Criamos uma lista para armazenar o caminho até a fruta
        //isto é feito com a questão dos pais mencionada anteriomente
        //a função retorna essa lista
        ArrayList<Coord> moves = new ArrayList<>();

        while(cur.parent != null){
            moves.add(cur);
            cur = cur.parent;
        }

        return moves;
    }

    //Função que determina o peso de uma coordenada
    public static Coord createWeightedCoord(Character[][] board, int x, int y, Coord goal, Coord parent){
        //estimativa simples de distância até o objetivo
        int distanceToGoal = Math.abs(x - goal.x) + Math.abs(y - goal.y);

        //heurísticas adicionais para tentar evitar que a Snake fique presa
        //serão mais especificadas em suas próprias funções
        if(isTrapped(board, x, y) || hasEnoughSpaceAroundGoal(board, goal) == false){
            return new Coord(x, y, Integer.MAX_VALUE - 5000 + distanceToGoal, parent);
        }else{
            return new Coord(x, y, distanceToGoal, parent);
        }
    }

    //Função auxiliar do game loop que executa um movimento da Snake
    public static void performStep(Character[][] board, int futureX, int futureY){
        //Por precaução verificamos se é um movimento válido ou não
        if(isValid(board, futureX, futureY)){
            //O movimento da Snake funciona basicamente com a estrutura de fila
            //a head sempre muda de posição para este próximo movimento
            //enquanto a tail é "deletada" e o novo tail é o penúltimo
            //isso simula o movimento da Snake
            //
            //Caso o próximo movimento seja em uma fruta, não fazemos essa parte
            //do tail, o que simula o crescimento da Snake
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

    //Função auxiliar que tenta verficar se a Snake vai ficar presa ou não
    public static boolean isTrapped(Character[][] board, int x, int y){
        //instanciamos uma nova array boolena que controla os epaços vistados
        boolean[][] visited = new boolean[board.length][board[0].length];
        //realizamos uma DFS par aver quantos espaços temos disponível
        int availableMoves = dfsTrapped(board, x, y, visited);

        //Se os espaços disponíveis são menores que o tamanho atual da Snake
        //podemos dizer que ela vai ficar presa, então na hora de determinar
        //os pesos, estabelecemos um muito alto para este movimento na tentativa
        //de evitar ele
        return availableMoves < snakeSize;
    }
    
    //DFS auxiliar a função anterior para contar os espaços disponíveis
    public static int dfsTrapped(Character[][] board, int x, int y, boolean[][] visited){
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

    //função similar as anteriores para tentar deixar a Snake mais "esperta"
    //aqui tentamos contar os espaços disponíveis envolta da fruta atual
    public static boolean hasEnoughSpaceAroundGoal(Character[][] board, Coord goal) {
        //Estratégia similar a anterior, executamos um DFS para contar os espaços
        //disponíveis, e estabelecemos um threshold, ou seja uma quantidade de espaços
        //que determina se o movimento seria possível ou não

        //função auxiliar que determina se os quadrados adjacentes a fruta estão bloqueados
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

    //função auxiliar que verfiica a disponibilidade de espaços envolta da fruta atual
    public static boolean isBlockedAdjacent(Character[][] board, int x, int y){
        int count = 0;
        for(int i=0; i<4; i++){
            int nextX = x + possibleX[i];
            int nextY = y + possibleY[i];
            if(isValid(board, nextX, nextY) == false || board[nextX][nextY] != '.'){
                count++;
            }
        }

        //determinamos que não tem saída se 3 ou mais espaços estão bloqueados
        if(count >= 3){
            return true;
        }

        return false;
    }
    
    //função auxiliar para determinar se uma coordenada é válida ou não
    public static boolean isValid(Character[][] board, int x, int y){
        //verifica se esta nos bounds da array
        if(x < 0 || y < 0 || x >= board.length || y >= board[0].length){
            return false;
        }

        //verifica se a Snake não vai se colidir com si própria
        if(board[x][y] == 'S'){
            //unica exceção é o tail, pois como ele geralmente "anda" um para frente
            //em cada movimento, então ele está disponível também
            if(x == tail.x && y == tail.y){
                return true;
            }
            return false;
        }

        return true;
    }

    //função auxiliar para criar a próxima fruta
    public static void spawnFruit(Character[][] board){
        //enquanto tivermos espaços disponíveis podemos criar a próxima fruta
        while(availableSquares > 0){
            //criamos uma coordenada aleatória e verfiicamos se é válida
            //caso positivo, criamos a fruta
            int randX = randomNumber(0, 9);
            int randY = randomNumber(0, 9);

            if(board[randX][randY] != 'S' && board[randX][randY] != 'A'){
                board[randX][randY] = 'A';
                nextFruit = new Coord(randX, randY);
                break;
            }    
        }
    }

    //função auxiliar para imprimir o campo atual no console
    //remanescente da implementação no terminal
    public static void printBoard(Character[][] board){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board.length; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    //função auxiliar para criar um número dentro de uma
    //range específica
    public static int randomNumber(int max, int min){
        return (int)(Math.random() * (max - min + 1)) + min;
    }
}
