// ============================================================
//  CAÇADOR DE MOEDAS
//  Jogo simples em Java Swing: mova o quadrado azul com as setas
//  do teclado e pegue o máximo de moedas amarelas em 30 segundos.
//
//  Conceitos usados: JPanel (área de desenho), Timer (game loop),
//  KeyListener (teclado), Rectangle (colisão) e Graphics (desenho).
// ============================================================

import javax.swing.*;          // JFrame, JPanel, Timer, SwingUtilities
import java.awt.*;             // Graphics, Color, Font, Rectangle, Dimension
import java.awt.event.*;       // ActionListener, KeyListener, ActionEvent, KeyEvent
import java.util.Random;       // Sorteio da posição da moeda

/*
 * A classe herda de JPanel (ela É a área de desenho do jogo) e implementa:
 *  - ActionListener: para receber os "tiques" do Timer (o game loop);
 *  - KeyListener: para receber os eventos do teclado.
 */
public class CacadorDeMoedas extends JPanel implements ActionListener, KeyListener {

    // ---------- CONSTANTES ----------
    // "static final" = valores fixos, que não mudam durante o jogo.
    // Deixar tudo em constantes facilita ajustar a dificuldade depois.
    static final int LARGURA = 600;             // largura da área de jogo, em pixels
    static final int ALTURA = 400;              // altura da área de jogo, em pixels
    static final int TAMANHO_JOGADOR = 30;      // lado do quadrado do jogador
    static final int TAMANHO_MOEDA = 20;        // diâmetro da moeda
    static final int VELOCIDADE = 5;            // pixels que o jogador anda por quadro
    static final int QUADROS_POR_SEGUNDO = 60;  // taxa de atualização do jogo (FPS)
    static final int DURACAO_SEGUNDOS = 30;     // duração de cada partida

    // ---------- ESTADO DO JOGO ----------
    // Tudo que muda durante a partida fica aqui.
    int jogadorX, jogadorY;     // posição do canto superior esquerdo do jogador
    int moedaX, moedaY;         // posição do canto superior esquerdo da moeda
    int pontos;                 // moedas coletadas
    int quadrosRestantes;       // contagem regressiva em QUADROS (não em segundos)
    boolean fimDeJogo;          // true quando o tempo acabou

    // ---------- TECLADO ----------
    // Guardamos quais setas estão pressionadas AGORA. Assim o movimento
    // é suave e permite andar na diagonal (duas setas ao mesmo tempo).
    boolean esquerda, direita, cima, baixo;

    // Gerador de números aleatórios, usado para sortear a posição da moeda.
    Random sorteio = new Random();

    // ---------- CONSTRUTOR ----------
    public CacadorDeMoedas() {
        // Define o tamanho desejado do painel; o pack() da janela usa isso.
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(20, 20, 40));   // azul bem escuro (R, G, B)

        // Sem isso o painel não consegue receber o foco do teclado.
        setFocusable(true);
        addKeyListener(this);   // "this" = esta própria classe trata as teclas

        reiniciar();            // prepara o estado inicial da primeira partida

        // O Timer dispara actionPerformed() a cada 1000/60 ≈ 16 ms.
        // Esse é o coração do jogo: o "game loop".
        Timer timer = new Timer(1000 / QUADROS_POR_SEGUNDO, this);
        timer.start();
    }

    // Coloca o jogo no estado inicial (usado ao começar e ao jogar de novo)
    void reiniciar() {
        // Centraliza o jogador: metade da tela menos metade do tamanho dele
        jogadorX = LARGURA / 2 - TAMANHO_JOGADOR / 2;
        jogadorY = ALTURA / 2 - TAMANHO_JOGADOR / 2;
        pontos = 0;
        // 30 segundos x 60 quadros = 1800 quadros de jogo
        quadrosRestantes = DURACAO_SEGUNDOS * QUADROS_POR_SEGUNDO;
        fimDeJogo = false;
        sortearMoeda();         // coloca a primeira moeda
    }

    // Escolhe uma posição aleatória para a moeda
    void sortearMoeda() {
        // nextInt(n) devolve de 0 até n-1. Subtraímos o tamanho da moeda
        // para ela nunca ficar parcialmente fora da tela.
        moedaX = sorteio.nextInt(LARGURA - TAMANHO_MOEDA);
        moedaY = sorteio.nextInt(ALTURA - TAMANHO_MOEDA);
    }

    // ---------- GAME LOOP ----------
    // Chamado pelo Timer ~60 vezes por segundo.
    // Cada chamada é um "quadro": primeiro atualiza a lógica, depois redesenha.
    @Override
    public void actionPerformed(ActionEvent e) {
        atualizar();    // calcula o novo estado do jogo
        repaint();      // pede ao Swing para chamar paintComponent()
    }

    // ---------- LÓGICA ----------
    // Executada uma vez por quadro. Só calcula; não desenha nada.
    void atualizar() {
        // Terminou o tempo? Congela o jogo (nada se move até o ENTER).
        if (fimDeJogo) return;

        // Movimento: em Java o eixo Y cresce para BAIXO,
        // por isso "cima" diminui Y e "baixo" aumenta Y.
        if (esquerda) jogadorX -= VELOCIDADE;
        if (direita)  jogadorX += VELOCIDADE;
        if (cima)     jogadorY -= VELOCIDADE;
        if (baixo)    jogadorY += VELOCIDADE;

        // Mantém o jogador dentro da tela.
        // Math.min limita o máximo; Math.max limita o mínimo (0).
        jogadorX = Math.max(0, Math.min(jogadorX, LARGURA - TAMANHO_JOGADOR));
        jogadorY = Math.max(0, Math.min(jogadorY, ALTURA - TAMANHO_JOGADOR));

        // Colisão: criamos um retângulo para cada objeto e verificamos
        // se eles se sobrepõem (intersects). Simples e eficiente.
        Rectangle jogador = new Rectangle(jogadorX, jogadorY, TAMANHO_JOGADOR, TAMANHO_JOGADOR);
        Rectangle moeda = new Rectangle(moedaX, moedaY, TAMANHO_MOEDA, TAMANHO_MOEDA);

        if (jogador.intersects(moeda)) {
            pontos++;           // ganhou um ponto
            sortearMoeda();     // a moeda reaparece em outro lugar
        }

        // Tempo: cada quadro consome 1 da contagem regressiva.
        quadrosRestantes--;
        if (quadrosRestantes <= 0) {
            fimDeJogo = true;
        }
    }

    // ---------- DESENHO ----------
    // Chamado pelo Swing sempre que o painel precisa ser pintado
    // (após cada repaint()). Aqui só DESENHAMOS; a lógica fica em atualizar().
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);    // limpa a tela com a cor de fundo

        // Jogador: retângulo ciano preenchido
        g.setColor(Color.CYAN);
        g.fillRect(jogadorX, jogadorY, TAMANHO_JOGADOR, TAMANHO_JOGADOR);

        // Moeda: círculo amarelo (oval com largura = altura)
        g.setColor(Color.YELLOW);
        g.fillOval(moedaX, moedaY, TAMANHO_MOEDA, TAMANHO_MOEDA);

        // Placar: pontos à esquerda, tempo à direita
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        // Converte quadros restantes em segundos (divisão inteira)
        int segundos = quadrosRestantes / QUADROS_POR_SEGUNDO;
        g.drawString("Pontos: " + pontos, 15, 25);              // (x, y da linha de base)
        g.drawString("Tempo: " + segundos + "s", LARGURA - 110, 25);

        // Tela de fim de jogo, desenhada por cima do restante
        if (fimDeJogo) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("FIM DE JOGO!", 160, 180);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Você fez " + pontos + " pontos", 205, 220);
            g.drawString("Pressione ENTER para jogar de novo", 140, 260);
        }
    }

    // ---------- TECLADO ----------
    // Tecla APERTADA: liga a "chave" da direção correspondente.
    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();     // código numérico da tecla
        if (tecla == KeyEvent.VK_LEFT)  esquerda = true;
        if (tecla == KeyEvent.VK_RIGHT) direita = true;
        if (tecla == KeyEvent.VK_UP)    cima = true;
        if (tecla == KeyEvent.VK_DOWN)  baixo = true;

        // ENTER só funciona depois do fim de jogo; durante a partida é ignorado.
        if (tecla == KeyEvent.VK_ENTER && fimDeJogo) {
            reiniciar();
        }
    }

    // Tecla SOLTA: desliga a "chave". Enquanto estiver ligada, o jogador
    // continua andando a cada quadro (veja atualizar()).
    @Override
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_LEFT)  esquerda = false;
        if (tecla == KeyEvent.VK_RIGHT) direita = false;
        if (tecla == KeyEvent.VK_UP)    cima = false;
        if (tecla == KeyEvent.VK_DOWN)  baixo = false;
    }

    // Obrigatório pela interface KeyListener, mas não usamos aqui
    // (keyTyped serve para caracteres digitados, não para setas).
    @Override
    public void keyTyped(KeyEvent e) { }

    // ---------- MAIN ----------
    // Ponto de entrada do programa.
    public static void main(String[] args) {
        // Todo código de interface Swing deve rodar na "thread de eventos" (EDT).
        // invokeLater agenda a criação da janela nela, de forma segura.
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("Caçador de Moedas");    // janela com título
            CacadorDeMoedas jogo = new CacadorDeMoedas();       // nosso painel/jogo
            janela.add(jogo);                                   // coloca o jogo na janela
            janela.pack();                                      // ajusta a janela ao tamanho preferido do painel
            janela.setResizable(false);                         // tamanho fixo (a lógica depende dele)
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fechar a janela encerra o programa
            janela.setLocationRelativeTo(null);                 // centraliza na tela
            janela.setVisible(true);                            // mostra a janela
            jogo.requestFocusInWindow(); // garante que o teclado funcione
        });
    }
}
