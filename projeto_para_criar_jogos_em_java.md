# Criando Jogos em Java: Guia para Iniciantes

> Material de apoio para alunos que já conhecem Java básico (variáveis, métodos, classes, `if`, `for`), mas **nunca criaram um jogo**.

Ao final deste guia você terá feito, passo a passo, um jogo completo e jogável, usando apenas o que já vem com o Java. Não é preciso instalar nenhuma biblioteca extra.

---

## Sumário

1. [O que é um jogo, para o computador?](#1-o-que-é-um-jogo-para-o-computador)
2. [O que vamos usar](#2-o-que-vamos-usar)
3. [Passo 1: Criando a janela](#3-passo-1-criando-a-janela)
4. [Passo 2: Desenhando na tela](#4-passo-2-desenhando-na-tela)
5. [Passo 3: Fazendo as coisas se moverem (o Game Loop)](#5-passo-3-fazendo-as-coisas-se-moverem-o-game-loop)
6. [Passo 4: Lendo o teclado](#6-passo-4-lendo-o-teclado)
7. [Passo 5: Colisões](#7-passo-5-colisões)
8. [Jogo completo: Caçador de Moedas](#8-jogo-completo-caçador-de-moedas)
9. [Boas práticas](#9-boas-práticas)
10. [Desafios para praticar](#10-desafios-para-praticar)
11. [Erros comuns](#11-erros-comuns)
12. [Próximos passos](#12-próximos-passos)
13. [Glossário](#13-glossário)

---

## 1. O que é um jogo, para o computador?

Um jogo parece mágico, mas por dentro é um programa que repete **três tarefas** muitas vezes por segundo:

```
┌──────────────────────────────────────────┐
│                                          │
│   1. LER A ENTRADA   (teclado, mouse)    │
│            ↓                             │
│   2. ATUALIZAR       (mover, colidir,    │
│                       somar pontos)      │
│            ↓                             │
│   3. DESENHAR        (mostrar na tela)   │
│            ↓                             │
│   (volta ao passo 1, ~60 vezes/segundo)  │
│                                          │
└──────────────────────────────────────────┘
```

Esse ciclo se chama **Game Loop** (laço do jogo). Cada repetição é um **quadro** (*frame*). Se o jogo roda a 60 FPS (*frames per second*), o ciclo acontece 60 vezes por segundo, e nossos olhos enxergam isso como movimento contínuo, como em um desenho animado.

### Ideia-chave: estado do jogo

O **estado** é o conjunto de variáveis que descrevem o jogo *agora*: posição do jogador, pontos, vidas, posição dos inimigos etc.

> **Um jogo é: estado + regras para modificar o estado + desenho do estado na tela.**

Se você sabe criar variáveis e alterá-las com `if` e `for`, você já tem a base para criar jogos.

---

## 2. O que vamos usar

Usaremos duas partes da biblioteca padrão do Java:

| Biblioteca | Para quê |
|---|---|
| **Swing** (`javax.swing`) | Criar a janela e o painel onde o jogo aparece |
| **AWT** (`java.awt`) | Desenhar formas, cores e textos; ler o teclado |

**Requisito:** JDK 11 ou superior instalado (qualquer IDE serve: IntelliJ, Eclipse, VS Code, NetBeans). Também é possível compilar pelo terminal:

```bash
javac MeuJogo.java
java MeuJogo
```

---

## 3. Passo 1: Criando a janela

Todo jogo precisa de uma janela. Em Swing, ela é um `JFrame`.

```java
import javax.swing.JFrame;

public class Janela {
    public static void main(String[] args) {
        JFrame janela = new JFrame("Meu Primeiro Jogo");
        janela.setSize(600, 400);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null); // centraliza na tela
        janela.setVisible(true);
    }
}
```

**O que cada linha faz:**

- `new JFrame("...")` cria a janela com um título.
- `setSize(600, 400)` define largura × altura em **pixels**.
- `setDefaultCloseOperation(EXIT_ON_CLOSE)` encerra o programa ao fechar a janela. Sem isso, o programa continua rodando "escondido".
- `setLocationRelativeTo(null)` centraliza a janela.
- `setVisible(true)` mostra a janela. Sem isso, nada aparece.

✅ **Teste:** execute e veja a janela vazia aparecer.

### Como funciona o sistema de coordenadas?

Atenção: na tela do computador, o **eixo Y cresce para baixo**.

```
(0,0) ────────────► X (aumenta para a direita)
  │
  │
  │
  ▼
  Y (aumenta para baixo)
```

- O canto **superior esquerdo** é `(0, 0)`.
- Para mover algo **para cima**, você **diminui** `y`.
- Para mover algo **para baixo**, você **aumenta** `y`.

Isso confunde no começo, mas é padrão em praticamente todos os jogos 2D.

---

## 4. Passo 2: Desenhando na tela

Para desenhar, criamos um `JPanel` próprio e sobrescrevemos o método `paintComponent`. O Java chama esse método automaticamente sempre que a tela precisa ser desenhada, entregando um objeto `Graphics`, que funciona como o "pincel".

```java
import javax.swing.*;
import java.awt.*;

public class Desenho extends JPanel {

    public Desenho() {
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // limpa a tela (pinta o fundo)

        g.setColor(Color.RED);
        g.fillRect(50, 50, 100, 60);      // retângulo: x, y, largura, altura

        g.setColor(Color.YELLOW);
        g.fillOval(250, 100, 80, 80);     // círculo/elipse dentro de um "quadro" invisível

        g.setColor(Color.WHITE);
        g.drawString("Olá, jogo!", 20, 30); // texto: o que, x, y
    }

    public static void main(String[] args) {
        JFrame janela = new JFrame("Desenhando");
        janela.add(new Desenho());
        janela.pack(); // ajusta a janela ao tamanho preferido do painel
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}
```

**Métodos de desenho mais usados:**

| Método | Desenha |
|---|---|
| `g.setColor(Color.X)` | Escolhe a cor do "pincel" |
| `g.fillRect(x, y, w, h)` | Retângulo preenchido |
| `g.drawRect(x, y, w, h)` | Apenas o contorno do retângulo |
| `g.fillOval(x, y, w, h)` | Círculo/elipse preenchido |
| `g.drawLine(x1, y1, x2, y2)` | Linha |
| `g.drawString("texto", x, y)` | Texto |

> **Importante:** nunca chame `paintComponent` diretamente. Peça para o Java redesenhar com `repaint()`.

✅ **Teste:** mude cores, posições e tamanhos. Desenhe uma casinha usando retângulos e um sol com um círculo.

---

## 5. Passo 3: Fazendo as coisas se moverem (o Game Loop)

Para criar movimento, precisamos de um laço que, a cada quadro:

1. altera o estado (ex.: aumenta `x`);
2. pede para redesenhar (`repaint()`).

Em Swing, a forma mais simples é o `javax.swing.Timer`, que chama um método a cada N milissegundos.

> Para ~60 FPS, usamos um intervalo de ~16 ms (1000 ms ÷ 60 ≈ 16).

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Movimento extends JPanel implements ActionListener {

    int x = 0;           // estado: posição horizontal da bola
    int velocidade = 3;  // pixels por quadro

    public Movimento() {
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.BLACK);

        Timer timer = new Timer(16, this); // a cada 16 ms chama actionPerformed
        timer.start();
    }

    // Chamado a cada "tick" do Timer: é o nosso game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        // 1) ATUALIZAR
        x += velocidade;
        if (x > 600) {   // saiu pela direita? volta para a esquerda
            x = -40;
        }

        // 2) DESENHAR (pede ao Java para chamar paintComponent)
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.ORANGE);
        g.fillOval(x, 180, 40, 40);
    }

    public static void main(String[] args) {
        JFrame janela = new JFrame("Movimento");
        janela.add(new Movimento());
        janela.pack();
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}
```

**Ponto de atenção:** separe mentalmente

- `actionPerformed` → **lógica** (mexe nas variáveis);
- `paintComponent` → **desenho** (só lê as variáveis e desenha).

Essa separação deixa o código muito mais organizado.

✅ **Teste:** faça a bola quicar nas bordas (inverta a `velocidade` quando `x` chegar em 0 ou 560). Depois adicione movimento vertical.

---

## 6. Passo 4: Lendo o teclado

Para o jogador controlar algo, implementamos `KeyListener`, que oferece três métodos:

| Método | Quando é chamado |
|---|---|
| `keyPressed` | Uma tecla foi **pressionada** |
| `keyReleased` | Uma tecla foi **solta** |
| `keyTyped` | Um caractere foi digitado (raramente usado em jogos) |

### Técnica importante: variáveis booleanas de estado

Em vez de mover o personagem **dentro** de `keyPressed`, guardamos quais teclas estão pressionadas e movemos **no game loop**. Isso gera um movimento suave e permite andar na diagonal.

```java
boolean esquerda, direita, cima, baixo;

@Override
public void keyPressed(KeyEvent e) {
    int tecla = e.getKeyCode();
    if (tecla == KeyEvent.VK_LEFT)  esquerda = true;
    if (tecla == KeyEvent.VK_RIGHT) direita  = true;
    if (tecla == KeyEvent.VK_UP)    cima     = true;
    if (tecla == KeyEvent.VK_DOWN)  baixo    = true;
}

@Override
public void keyReleased(KeyEvent e) {
    int tecla = e.getKeyCode();
    if (tecla == KeyEvent.VK_LEFT)  esquerda = false;
    if (tecla == KeyEvent.VK_RIGHT) direita  = false;
    if (tecla == KeyEvent.VK_UP)    cima     = false;
    if (tecla == KeyEvent.VK_DOWN)  baixo    = false;
}
```

E no game loop:

```java
if (esquerda) x -= velocidade;
if (direita)  x += velocidade;
if (cima)     y -= velocidade;   // lembre: Y cresce para baixo!
if (baixo)    y += velocidade;
```

> **Dica:** para o painel receber o teclado, é obrigatório chamar `setFocusable(true)` e `addKeyListener(this)` no construtor.

### Não deixe o jogador sair da tela

Use `Math.max` e `Math.min` para "prender" o valor entre um mínimo e um máximo:

```java
x = Math.max(0, Math.min(x, LARGURA - TAMANHO));
y = Math.max(0, Math.min(y, ALTURA - TAMANHO));
```

---

## 7. Passo 5: Colisões

Como saber se o jogador **encostou** em uma moeda, um inimigo ou uma parede? Pelo modo mais simples: tratamos cada objeto como um **retângulo** (a "caixa de colisão" ou *hitbox*) e usamos a classe `java.awt.Rectangle`.

```java
Rectangle jogador = new Rectangle(jogadorX, jogadorY, 30, 30);
Rectangle moeda   = new Rectangle(moedaX, moedaY, 20, 20);

if (jogador.intersects(moeda)) {
    // colidiram!
}
```

```
   ┌────────┐
   │ jogador│
   │     ┌──┼───┐
   └─────┼──┘   │   ← os retângulos se sobrepõem
         │ moeda│     intersects() retorna true
         └──────┘
```

Mesmo que o desenho seja um círculo, usar o retângulo ao redor dele funciona muito bem para jogos simples.

---

## 8. Jogo completo: Caçador de Moedas

Juntando tudo: o jogador controla um quadrado com as **setas**, coleta moedas e tenta fazer o máximo de pontos em **30 segundos**. Ao final, aperta **ENTER** para jogar de novo.

Crie um arquivo chamado `CacadorDeMoedas.java`:

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class CacadorDeMoedas extends JPanel implements ActionListener, KeyListener {

    // ---------- CONSTANTES ----------
    static final int LARGURA = 600;
    static final int ALTURA = 400;
    static final int TAMANHO_JOGADOR = 30;
    static final int TAMANHO_MOEDA = 20;
    static final int VELOCIDADE = 5;
    static final int QUADROS_POR_SEGUNDO = 60;
    static final int DURACAO_SEGUNDOS = 30;

    // ---------- ESTADO DO JOGO ----------
    int jogadorX, jogadorY;
    int moedaX, moedaY;
    int pontos;
    int quadrosRestantes;
    boolean fimDeJogo;

    // ---------- TECLADO ----------
    boolean esquerda, direita, cima, baixo;

    Random sorteio = new Random();

    // ---------- CONSTRUTOR ----------
    public CacadorDeMoedas() {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(20, 20, 40));
        setFocusable(true);
        addKeyListener(this);

        reiniciar();

        Timer timer = new Timer(1000 / QUADROS_POR_SEGUNDO, this);
        timer.start();
    }

    // Coloca o jogo no estado inicial
    void reiniciar() {
        jogadorX = LARGURA / 2 - TAMANHO_JOGADOR / 2;
        jogadorY = ALTURA / 2 - TAMANHO_JOGADOR / 2;
        pontos = 0;
        quadrosRestantes = DURACAO_SEGUNDOS * QUADROS_POR_SEGUNDO;
        fimDeJogo = false;
        sortearMoeda();
    }

    // Escolhe uma posição aleatória para a moeda
    void sortearMoeda() {
        moedaX = sorteio.nextInt(LARGURA - TAMANHO_MOEDA);
        moedaY = sorteio.nextInt(ALTURA - TAMANHO_MOEDA);
    }

    // ---------- GAME LOOP ----------
    @Override
    public void actionPerformed(ActionEvent e) {
        atualizar();
        repaint();
    }

    // ---------- LÓGICA ----------
    void atualizar() {
        if (fimDeJogo) return;

        // Movimento
        if (esquerda) jogadorX -= VELOCIDADE;
        if (direita)  jogadorX += VELOCIDADE;
        if (cima)     jogadorY -= VELOCIDADE;
        if (baixo)    jogadorY += VELOCIDADE;

        // Mantém o jogador dentro da tela
        jogadorX = Math.max(0, Math.min(jogadorX, LARGURA - TAMANHO_JOGADOR));
        jogadorY = Math.max(0, Math.min(jogadorY, ALTURA - TAMANHO_JOGADOR));

        // Colisão com a moeda
        Rectangle jogador = new Rectangle(jogadorX, jogadorY, TAMANHO_JOGADOR, TAMANHO_JOGADOR);
        Rectangle moeda = new Rectangle(moedaX, moedaY, TAMANHO_MOEDA, TAMANHO_MOEDA);

        if (jogador.intersects(moeda)) {
            pontos++;
            sortearMoeda();
        }

        // Tempo
        quadrosRestantes--;
        if (quadrosRestantes <= 0) {
            fimDeJogo = true;
        }
    }

    // ---------- DESENHO ----------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Jogador
        g.setColor(Color.CYAN);
        g.fillRect(jogadorX, jogadorY, TAMANHO_JOGADOR, TAMANHO_JOGADOR);

        // Moeda
        g.setColor(Color.YELLOW);
        g.fillOval(moedaX, moedaY, TAMANHO_MOEDA, TAMANHO_MOEDA);

        // Placar
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        int segundos = quadrosRestantes / QUADROS_POR_SEGUNDO;
        g.drawString("Pontos: " + pontos, 15, 25);
        g.drawString("Tempo: " + segundos + "s", LARGURA - 110, 25);

        // Tela de fim de jogo
        if (fimDeJogo) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("FIM DE JOGO!", 160, 180);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Você fez " + pontos + " pontos", 205, 220);
            g.drawString("Pressione ENTER para jogar de novo", 140, 260);
        }
    }

    // ---------- TECLADO ----------
    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_LEFT)  esquerda = true;
        if (tecla == KeyEvent.VK_RIGHT) direita = true;
        if (tecla == KeyEvent.VK_UP)    cima = true;
        if (tecla == KeyEvent.VK_DOWN)  baixo = true;

        if (tecla == KeyEvent.VK_ENTER && fimDeJogo) {
            reiniciar();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_LEFT)  esquerda = false;
        if (tecla == KeyEvent.VK_RIGHT) direita = false;
        if (tecla == KeyEvent.VK_UP)    cima = false;
        if (tecla == KeyEvent.VK_DOWN)  baixo = false;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("Caçador de Moedas");
            CacadorDeMoedas jogo = new CacadorDeMoedas();
            janela.add(jogo);
            janela.pack();
            janela.setResizable(false);
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
            jogo.requestFocusInWindow(); // garante que o teclado funcione
        });
    }
}
```

### Entendendo a estrutura do código

| Parte | Responsabilidade |
|---|---|
| **Constantes** | Valores fixos (tamanho da tela, velocidade). Fáceis de ajustar em um só lugar |
| **Estado do jogo** | Variáveis que mudam durante a partida |
| `reiniciar()` | Volta tudo ao início, o que permite jogar de novo |
| `actionPerformed()` | O game loop: chama `atualizar()` e `repaint()` |
| `atualizar()` | Toda a lógica: movimento, colisão, pontos, tempo |
| `paintComponent()` | Apenas desenha o estado atual |
| `keyPressed/Released` | Registra quais teclas estão pressionadas |

> **Discussão em aula:** por que o tempo é contado em *quadros* e não com um relógio? (Resposta: é simples e previsível, mas depende de o Timer ser estável. Jogos profissionais usam o tempo real decorrido entre quadros, o chamado *delta time*.)

---

## 9. Boas práticas

1. **Separe lógica de desenho.** `atualizar()` nunca desenha; `paintComponent()` nunca muda o estado.
2. **Use constantes** (`static final`) em vez de "números mágicos" espalhados pelo código.
3. **Crie um método `reiniciar()`**, pois facilita muito reiniciar a partida.
4. **Comece pequeno.** Faça funcionar um quadrado que se move antes de pensar em inimigos, fases e sons.
5. **Teste a cada passo.** Adicione uma coisa por vez e rode o programa.
6. **Evolua para classes.** Quando houver vários objetos (inimigos, tiros), crie classes como `Jogador`, `Inimigo` e `Moeda`, cada uma com seus atributos e métodos. É a orientação a objetos aplicada de verdade.

---

## 10. Desafios para praticar

### 🟢 Nível fácil
1. Mude as cores do jogador, da moeda e do fundo.
2. Aumente a velocidade do jogador e veja o efeito.
3. Altere o tempo da partida para 60 segundos.
4. Troque o jogador por um círculo (`fillOval`).

### 🟡 Nível médio
5. Permita controlar o jogador também com **W, A, S, D**.
6. Faça a moeda valer **mais pontos** quanto mais rápido for coletada.
7. Adicione **uma segunda moeda** (azul) que vale 5 pontos e aparece raramente.
8. Mostre o **recorde** (maior pontuação) enquanto o programa estiver aberto.

### 🔴 Nível desafiador
9. Adicione um **inimigo** (quadrado vermelho) que se move sozinho e quicando nas bordas. Se tocar nele, o jogo acaba.
10. Crie uma classe `Moeda` e uma lista `ArrayList<Moeda>` para ter várias moedas na tela ao mesmo tempo.
11. Faça o inimigo **perseguir** o jogador (dica: compare as posições `x` e `y` dos dois e ande na direção do jogador).
12. Adicione **níveis de dificuldade** que aumentam a velocidade do inimigo conforme os pontos.

---

## 11. Erros comuns

| Sintoma | Provável causa | Solução |
|---|---|---|
| Janela abre, mas fica vazia | Faltou `add(painel)` ou `setVisible(true)` | Conferir o `main` |
| O teclado não responde | Faltou `setFocusable(true)`, `addKeyListener(this)` ou `requestFocusInWindow()` | Conferir o construtor e o `main` |
| Rastro de desenhos antigos na tela | Esqueceu `super.paintComponent(g)` | Colocar como primeira linha de `paintComponent` |
| Nada se move | Esqueceu `repaint()` ou `timer.start()` | Conferir o game loop |
| O personagem anda para o lado errado | Confusão com o eixo Y (cresce para baixo) | Rever a seção de coordenadas |
| A janela fecha mas o programa continua rodando | Faltou `EXIT_ON_CLOSE` | Adicionar `setDefaultCloseOperation` |
| Movimento "travado" ao segurar tecla | Movendo o objeto direto em `keyPressed` | Usar variáveis booleanas (seção 6) |

---

## 12. Próximos passos

Depois de dominar este guia, explore:

- **Imagens (sprites):** carregar PNGs com `ImageIcon` ou `ImageIO` e desenhar com `g.drawImage(...)`.
- **Sons:** `javax.sound.sampled.Clip` para efeitos sonoros e música.
- **Mouse:** `MouseListener` e `MouseMotionListener`.
- **Orientação a objetos:** separar o jogo em classes (`Jogador`, `Inimigo`, `Tiro`...).
- **Coleções:** `ArrayList` para gerenciar vários inimigos e projéteis.
- **Delta time:** movimento independente da velocidade do computador.
- **Outras tecnologias:** [JavaFX](https://openjfx.io/) (alternativa moderna ao Swing) e [LibGDX](https://libgdx.com/) (framework profissional de jogos em Java, inclusive para Android).

### Ideias de jogos clássicos para os alunos recriarem

1. **Pong**, com duas raquetes e uma bola
2. **Snake** (jogo da cobrinha)
3. **Space Invaders** simplificado
4. **Flappy Bird**
5. **Jogo da Memória**
6. **Breakout** (quebra-tijolos)

---

## 13. Glossário

| Termo | Significado |
|---|---|
| **Game Loop** | Ciclo que repete: entrada → atualização → desenho |
| **Frame / Quadro** | Uma "foto" do jogo; uma repetição do ciclo |
| **FPS** | *Frames per second*: quadros por segundo |
| **Estado** | Conjunto de variáveis que descrevem o jogo em determinado momento |
| **Sprite** | Imagem de um personagem ou objeto do jogo |
| **Hitbox** | Retângulo invisível usado para detectar colisões |
| **Colisão** | Quando dois objetos se tocam ou se sobrepõem |
| **Pixel** | Menor ponto da tela; unidade das coordenadas |
| **Renderizar** | Desenhar o estado atual do jogo na tela |
| **Delta time** | Tempo real entre dois quadros, usado para movimento consistente |

---

## Resumo em 5 linhas

1. Um jogo é um **laço** que **lê entrada**, **atualiza o estado** e **desenha**, várias vezes por segundo.
2. Em Swing, a janela é um `JFrame` e a área de desenho é um `JPanel` com `paintComponent`.
3. O `Timer` funciona como o **game loop**, chamando `actionPerformed` a cada ~16 ms.
4. O teclado é lido com `KeyListener`, guardando as teclas pressionadas em variáveis `boolean`.
5. Colisões simples usam `Rectangle.intersects()`.

**Agora é com você: abra a IDE, digite o código (não copie e cole!) e comece a modificar. Aprender a criar jogos é, principalmente, experimentar. 🎮**
