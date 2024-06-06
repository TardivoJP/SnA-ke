[:us:](README.md) [:jp:](README_jp.md) [:cn:](README_cn.md) [:india:](README_india.md)

# SnA*ke

Bem-vindo ao SnA\*ke, uma implementação em Java Swing do clássico jogo da cobrinha com caminho inteligente usando o algoritmo A\*.

![Snake](https://i.imgur.com/TcbzVZL.png)

## Funcionalidades

- **Caminho Inteligente**: A cobrinha usa o algoritmo A* para navegar inteligentemente pelo tabuleiro do jogo, otimizando seu caminho para alcançar a maçã enquanto evita armadilhas e obstáculos.

- **Detecção de Armadilhas**: Há uma heurística adicional que tenta evitar situações de potencial aprisionamento, atribuindo um peso muito alto para movimentos que levam a uma parte do tabuleiro menor do que o tamanho atual da cobrinha.

- **Avaliação Inteligente de Objetivos**: A cobrinha avalia os espaços disponíveis ao redor da maçã, verificando se há espaço suficiente ao seu redor para uma possível fuga ou se tem 3 ou mais quadrados adjacentes bloqueados, também ajustando os pesos desses movimentos potenciais.

- **Estatísticas da Sessão**: Acompanhe o progresso do jogo atual e as médias de movimentos e tamanho da cobrinha em várias partidas.


## Instalação

Para rodar o SnA*ke localmente, siga estes passos:

1. Clone o repositório:

   ```bash
   git clone https://github.com/TardivoJP/SnA-ke.git
   ```

2. Navegue até o diretório do projeto:

   ```bash
   cd SnA-ke
   ```

3. Compile e execute o jogo:

   ```bash
   javac SnakeGUI.java
   java SnakeGUI
   ```


---

Obrigado por conferir o SnA*ke! Se você tiver alguma pergunta ou feedback, sinta-se à vontade para abrir uma issue ou entrar em contato. Aproveite o jogo! 🐍✨
