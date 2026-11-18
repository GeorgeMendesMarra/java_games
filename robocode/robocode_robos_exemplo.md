# Robôs de Exemplo para o Robocode (Java)

Este material reúne **9 robôs comentados**, organizados em três níveis: **iniciante**, **intermediário** e **avançado**. Cada robô introduz conceitos novos, então a ideia é percorrer na ordem, rodar cada um contra os robôs de exemplo (`sample.*`) e depois modificá-lo.

> Os códigos usam o **Robocode clássico** (Java). Funcionam em Windows, Linux e macOS.

---

## Sumário

1. [Preparando o ambiente](#1-preparando-o-ambiente)
2. [Conceitos básicos do Robocode](#2-conceitos-básicos-do-robocode)
3. [Nível iniciante](#3-nível-iniciante)
   - [PrimeiroRobo](#31-primeirorobo)
   - [Sentinela](#32-sentinela)
   - [ParedeSimples](#33-paredesimples)
4. [Nível intermediário](#4-nível-intermediário)
   - [Perseguidor](#41-perseguidor)
   - [Dancarino](#42-dancarino)
   - [Cacador](#43-cacador)
5. [Nível avançado](#5-nível-avançado)
   - [Preditor](#51-preditor)
   - [AntiGravidade](#52-antigravidade)
   - [Mestre](#53-mestre)
6. [Resumo dos níveis](#6-resumo-dos-níveis)
7. [Exercícios propostos](#7-exercícios-propostos)
8. [Referência rápida da API](#8-referência-rápida-da-api)

---

## 1. Preparando o ambiente

1. Instale o **JDK** (Java 8 ou superior) e o **Robocode** (site oficial: <https://robocode.sourceforge.io>).
2. Abra o Robocode e use o menu **Robot → Source Editor → File → New → Robot**. Dê o nome da classe e o nome do pacote (usaremos o pacote `ensino`).
3. Cole o código do robô, salve (`Ctrl+S`) e compile (`Ctrl+B`).
4. Inicie uma batalha: **Battle → New**, selecione seu robô e adversários (por exemplo `sample.Walls`, `sample.SpinBot`, `sample.Tracker`) e clique em **Start Battle**.

Se preferir usar uma IDE (Eclipse, IntelliJ, VS Code, NetBeans), adicione o `robocode.jar` ao classpath e configure o diretório de robôs em **Options → Preferences → Development Options**.

---

## 2. Conceitos básicos do Robocode

Todo robô é um tanque com **três partes que giram de forma independente**:

| Parte | Função |
|-------|--------|
| **Corpo** | Move o tanque (`ahead`, `back`, `turnRight`, `turnLeft`) |
| **Canhão** | Atira (`fire`) e gira (`turnGunRight`, `turnGunLeft`) |
| **Radar** | Detecta inimigos (`turnRadarRight`, `scan`) e dispara o evento `onScannedRobot` |

### Duas classes base

- **`Robot`**: comandos **bloqueantes**. `ahead(100)` só retorna depois que o tanque andou. É mais simples, mas faz só uma coisa por vez.
- **`AdvancedRobot`**: comandos **não bloqueantes** (`setAhead`, `setTurnRight`, `setFire`...). Você agenda várias ações e chama `execute()` para que todas aconteçam **no mesmo turno**. É o que permite mover, girar o canhão e o radar ao mesmo tempo.

### Eventos mais usados

| Evento | Quando ocorre |
|--------|---------------|
| `onScannedRobot` | O radar viu um inimigo |
| `onHitByBullet` | Seu robô levou um tiro |
| `onHitWall` | Bateu na parede |
| `onHitRobot` | Colidiu com outro robô |
| `onRobotDeath` | Outro robô foi destruído |

### Regras do tiro

- A **potência** vai de `0.1` a `3.0`.
- Quanto maior a potência, **maior o dano** e **menor a velocidade** da bala: `velocidade = 20 - 3 × potência`.
- Atirar **gasta energia** do seu robô; acertar **devolve** energia.
- O canhão esquenta após cada tiro (`getGunHeat()`); só dá para atirar quando chega a `0`.

---

## 3. Nível iniciante

Usam a classe `Robot` (comandos bloqueantes). Foco: **loop principal**, **eventos** e **tiro simples**.

### 3.1 PrimeiroRobo

**Conceitos:** método `run()`, loop infinito, `ahead`/`back`, girar o canhão, evento `onScannedRobot`.

**Estratégia:** anda para frente e para trás girando o canhão 360° para varrer o campo. Quando vê alguém, atira.

```java
package ensino;

import robocode.*;
import java.awt.Color;

public class PrimeiroRobo extends Robot {

    // run() é o "main" do robô: o Robocode chama este método ao iniciar a batalha.
    @Override
    public void run() {
        // Cores: corpo, canhão, radar
        setColors(Color.BLUE, Color.WHITE, Color.RED);

        // Loop infinito: o robô age até morrer ou a batalha acabar
        while (true) {
            ahead(100);          // anda 100 pixels para frente
            turnGunRight(360);   // gira o canhão uma volta completa (varre o campo)
            back(100);           // anda 100 pixels para trás
            turnGunRight(360);   // varre novamente
        }
    }

    // Chamado automaticamente quando o radar detecta um inimigo
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);                 // atira com potência 1 (tiro fraco, mas barato)
    }

    // Chamado quando levamos um tiro
    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // e.getBearing() é o ângulo em que veio o tiro, relativo ao nosso corpo.
        // Viramos de lado (perpendicular) para dificultar o próximo tiro.
        turnLeft(90 - e.getBearing());
    }
}
```

---

### 3.2 Sentinela

**Conceitos:** ficar parado, varrer com o canhão, **escolher a potência do tiro pela distância**.

**Estratégia:** não se move. Gira o canhão (e o radar, que acompanha o canhão) e atira com mais força quanto mais perto o inimigo estiver, pois de perto é mais fácil acertar.

```java
package ensino;

import robocode.*;
import java.awt.Color;

public class Sentinela extends Robot {

    @Override
    public void run() {
        setColors(Color.DARK_GRAY, Color.ORANGE, Color.YELLOW);

        while (true) {
            // O radar está preso ao canhão: girar o canhão também gira o radar.
            // Giros curtos de 10° fazem uma varredura contínua e constante.
            turnGunRight(10);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double distancia = e.getDistance();

        // Quanto mais perto, mais forte o tiro (mais dano, mais fácil de acertar).
        if (distancia < 150) {
            fire(3);
        } else if (distancia < 400) {
            fire(2);
        } else {
            fire(1);             // de longe, economizamos energia
        }
    }

    // Se um robô encostar em nós, descarregamos tiro forte nele
    @Override
    public void onHitRobot(HitRobotEvent e) {
        fire(3);
    }
}
```

> **Limitação:** como fica parado, o Sentinela é um alvo fácil para robôs que preveem a posição. Isso será resolvido nos níveis seguintes.

---

### 3.3 ParedeSimples

**Conceitos:** variáveis de estado, descobrir o tamanho do campo, andar pelas bordas, `onHitRobot`.

**Estratégia:** vai até uma parede e passa a **contornar o campo** andando rente às bordas, atirando para o centro. Quem fica na borda só pode ser atacado de um lado.

```java
package ensino;

import robocode.*;
import java.awt.Color;

public class ParedeSimples extends Robot {

    private double tamanhoLado;   // maior dimensão do campo (garante chegar até a borda)
    private boolean andando;      // true enquanto o robô está em movimento

    @Override
    public void run() {
        setColors(Color.BLACK, Color.RED, Color.WHITE);

        tamanhoLado = Math.max(getBattleFieldWidth(), getBattleFieldHeight());

        // Alinha o corpo com um múltiplo de 90° (norte, leste, sul ou oeste)
        turnLeft(getHeading() % 90);

        // Vai até a parede mais próxima
        andando = true;
        ahead(tamanhoLado);
        andando = false;

        // Aponta o canhão para dentro do campo e vira o corpo ao longo da parede
        turnGunRight(90);
        turnRight(90);

        while (true) {
            andando = true;
            ahead(tamanhoLado);   // percorre a parede inteira até o canto
            andando = false;
            turnRight(90);        // dobra o canto e segue pela próxima parede
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        fire(2);

        // Se estávamos parados no meio de um 'ahead', pedimos nova varredura:
        // assim o radar pode ver outro inimigo logo em seguida.
        if (!andando) {
            scan();
        }
    }

    // Se colidirmos com alguém, desviamos
    @Override
    public void onHitRobot(HitRobotEvent e) {
        // Bearing entre -90 e 90 significa que o robô está à nossa frente
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            back(100);
        } else {
            ahead(100);
        }
    }
}
```

---

## 4. Nível intermediário

Usam `AdvancedRobot` (comandos `set...` + `execute()`). Foco: **fazer várias coisas ao mesmo tempo**, **travar o radar** e **mirar de verdade**.

### 4.1 Perseguidor

**Conceitos:** `AdvancedRobot`, `setAdjust...` (partes independentes), **radar lock**, mirar com `absBearing`, `Utils.normalRelativeAngleDegrees`.

**Estratégia:** localiza o inimigo, mantém o radar **travado** nele, aponta o canhão com precisão e se aproxima até uma distância segura.

```java
package ensino;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class Perseguidor extends AdvancedRobot {

    @Override
    public void run() {
        setColors(Color.RED, Color.BLACK, Color.RED);

        // Permite que canhão e radar girem independentemente do corpo.
        // Sem isso, girar o corpo também giraria o canhão e o radar.
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            // Se o radar parou (não há alvo travado), volta a varrer o campo
            if (getRadarTurnRemaining() == 0.0) {
                setTurnRadarRight(360);
            }
            execute();   // executa TODAS as ações agendadas neste turno
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Ângulo absoluto (0° = norte) do inimigo = nosso heading + bearing relativo
        double absBearing = getHeading() + e.getBearing();

        // ---- RADAR LOCK ----
        // Giramos o radar até o inimigo; o fator 2 faz o radar "passar" um pouco
        // do alvo, o que mantém o inimigo sempre dentro do campo de visão.
        double giroRadar = Utils.normalRelativeAngleDegrees(absBearing - getRadarHeading());
        setTurnRadarRight(giroRadar * 2);

        // ---- MIRA ----
        double giroCanhao = Utils.normalRelativeAngleDegrees(absBearing - getGunHeading());
        setTurnGunRight(giroCanhao);

        // ---- MOVIMENTO ----
        setTurnRight(e.getBearing());                // vira o corpo para o inimigo
        setAhead(e.getDistance() - 150);             // mantém ~150 px de distância
        // (se a distância for menor que 150, o valor é negativo e o robô recua)

        // ---- TIRO ----
        // Só atira se o canhão já estiver quase apontado e frio
        if (Math.abs(giroCanhao) < 10 && getGunHeat() == 0) {
            setFire(e.getDistance() < 200 ? 3 : 2);
        }
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        setBack(100);   // bateu na parede: recua
    }
}
```

---

### 4.2 Dancarino

**Conceitos:** **strafing** (movimento lateral), **detectar tiro inimigo pela queda de energia**, mudar de direção para desviar.

**Estratégia:** move-se **perpendicularmente** ao inimigo. Quando a energia dele cai entre 0,1 e 3,0, é sinal de que ele **acabou de atirar**. Nesse instante invertemos a direção, e a bala (que foi mirada onde estávamos) erra.

```java
package ensino;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class Dancarino extends AdvancedRobot {

    private double energiaAnterior = 100;   // energia do inimigo no scan anterior
    private int direcao = 1;                // 1 = frente, -1 = trás

    @Override
    public void run() {
        setColors(Color.MAGENTA, Color.PINK, Color.WHITE);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            if (getRadarTurnRemaining() == 0.0) {
                setTurnRadarRight(360);
            }
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Ângulo absoluto (em radianos) até o inimigo
        double absBearing = getHeadingRadians() + e.getBearingRadians();

        // Radar travado no inimigo
        setTurnRadarRightRadians(
            2 * Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians()));

        // ---- DETECÇÃO DE TIRO ----
        // Se a energia caiu entre 0.1 e 3.0 sem termos acertado nada,
        // o inimigo provavelmente disparou uma bala agora.
        double queda = energiaAnterior - e.getEnergy();
        if (queda > 0 && queda <= 3.0) {
            direcao = -direcao;           // inverte a direção para desviar da bala
        }
        energiaAnterior = e.getEnergy();

        // ---- MOVIMENTO LATERAL ----
        // Corpo perpendicular (90°) ao inimigo; andar ahead/back faz "dançar" de um lado a outro
        setTurnRightRadians(Utils.normalRelativeAngle(e.getBearingRadians() + Math.PI / 2));
        setAhead(150 * direcao);

        // ---- MIRA E TIRO ----
        setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing - getGunHeadingRadians()));
        if (getGunHeat() == 0 && getEnergy() > 5) {
            setFire(2);
        }
    }

    // Bateu na parede: inverte o sentido do movimento
    @Override
    public void onHitWall(HitWallEvent e) {
        direcao = -direcao;
    }
}
```

---

### 4.3 Cacador

**Conceitos:** **máquina de estados simples** (procurar → atacar), memória do último alvo, gerenciamento de energia.

**Estratégia:** alterna entre dois modos. Em **BUSCA** gira procurando alguém. Em **ATAQUE** persegue e atira. Se perde o alvo por alguns turnos, volta para a busca. Atira com potência proporcional à própria energia, para não se suicidar atirando.

```java
package ensino;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class Cacador extends AdvancedRobot {

    // Estados possíveis do robô
    private enum Estado { BUSCA, ATAQUE }

    private Estado estado = Estado.BUSCA;
    private int turnosSemVer = 0;    // quantos turnos passaram sem ver ninguém

    @Override
    public void run() {
        setColors(Color.GREEN, Color.BLACK, Color.GREEN);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            turnosSemVer++;

            // Se faz mais de 5 turnos que não vemos ninguém, voltamos a procurar
            if (turnosSemVer > 5) {
                estado = Estado.BUSCA;
            }

            if (estado == Estado.BUSCA) {
                // Gira tudo para encontrar um alvo
                setTurnRadarRight(360);
                setTurnRight(30);
                setAhead(60);
            }
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        estado = Estado.ATAQUE;
        turnosSemVer = 0;

        double absBearing = getHeadingRadians() + e.getBearingRadians();

        // Radar travado
        setTurnRadarRightRadians(
            2 * Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians()));

        // Persegue mantendo ~200 px
        setTurnRightRadians(e.getBearingRadians());
        setAhead(e.getDistance() - 200);

        // Mira
        double giro = Utils.normalRelativeAngle(absBearing - getGunHeadingRadians());
        setTurnGunRightRadians(giro);

        // ---- POTÊNCIA CONFORME A ENERGIA ----
        // Com pouca energia, atiramos fraco para não morrer por atirar demais.
        double potencia = Math.min(3.0, getEnergy() / 10.0);
        potencia = Math.max(0.1, potencia);

        if (getGunHeat() == 0 && Math.abs(giro) < 0.15) {
            setFire(potencia);
        }
    }

    // Se colidirmos, aproveitamos que estamos perto e atiramos forte
    @Override
    public void onHitRobot(HitRobotEvent e) {
        setFire(3);
    }
}
```

---

## 5. Nível avançado

Foco: **predição de movimento**, **suavização de paredes**, **física das balas** e **batalhas com muitos robôs**.

### 5.1 Preditor

**Conceitos:** **mira linear (linear targeting)**, **radar lock perfeito**, **órbita com wall smoothing**, trigonometria.

**Estratégia:**

1. Calcula a **posição exata** do inimigo (x, y) a partir da distância e do ângulo.
2. Assume que ele continuará com a mesma velocidade e direção e **simula turno a turno** onde ele estará quando a bala chegar.
3. Mira nesse ponto futuro, não no ponto atual.
4. Orbita o inimigo e inverte o sentido quando detecta tiro, sem bater nas paredes.

```java
package ensino;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Preditor extends AdvancedRobot {

    private double energiaInimigo = 100;
    private int direcao = 1;                 // sentido da órbita: 1 ou -1
    private Rectangle2D.Double campo;        // área segura do campo (longe das paredes)

    @Override
    public void run() {
        setColors(new Color(30, 30, 120), Color.CYAN, Color.WHITE);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        // Retângulo que representa onde o robô pode andar sem encostar nas paredes.
        // O tanque tem 36x36 px, então ficamos a 18 px da borda.
        campo = new Rectangle2D.Double(
            18, 18, getBattleFieldWidth() - 36, getBattleFieldHeight() - 36);

        // Gira o radar "infinitamente" até achar o primeiro alvo
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

        while (true) {
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double distancia = e.getDistance();
        double absBearing = getHeadingRadians() + e.getBearingRadians();

        // ===== 1. RADAR LOCK =====
        setTurnRadarRightRadians(
            2 * Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians()));

        // ===== 2. MOVIMENTO: ÓRBITA + DETECÇÃO DE TIRO =====
        double queda = energiaInimigo - e.getEnergy();
        if (queda > 0 && queda <= 3.0) {
            direcao = -direcao;              // inimigo atirou: troca o sentido
        }
        energiaInimigo = e.getEnergy();

        // Ângulo desejado: perpendicular ao inimigo (órbita)
        double destino = absBearing + direcao * Math.PI / 2;
        destino = suavizarParede(destino);
        irParaAngulo(destino);

        // ===== 3. POTÊNCIA DO TIRO =====
        double potencia = distancia < 200 ? 3.0 : (distancia < 500 ? 2.0 : 1.0);
        potencia = Math.max(0.1, Math.min(potencia, getEnergy() / 4));
        double velocidadeBala = 20 - 3 * potencia;

        // ===== 4. MIRA LINEAR =====
        // Posição atual do inimigo no plano cartesiano
        // (no Robocode, 0 rad = norte, então x usa seno e y usa cosseno)
        double inimigoX = getX() + distancia * Math.sin(absBearing);
        double inimigoY = getY() + distancia * Math.cos(absBearing);

        double direcaoInimigo = e.getHeadingRadians();
        double velocidadeInimigo = e.getVelocity();

        // Simula a posição futura do inimigo turno a turno até a bala "alcançá-lo"
        double previstoX = inimigoX;
        double previstoY = inimigoY;
        double turnos = 0;

        while ((++turnos) * velocidadeBala
                < Point2D.distance(getX(), getY(), previstoX, previstoY)) {

            previstoX += Math.sin(direcaoInimigo) * velocidadeInimigo;
            previstoY += Math.cos(direcaoInimigo) * velocidadeInimigo;

            // O inimigo não pode sair do campo: limitamos a previsão às bordas
            if (!campo.contains(previstoX, previstoY)) {
                previstoX = Math.max(campo.getMinX(), Math.min(campo.getMaxX(), previstoX));
                previstoY = Math.max(campo.getMinY(), Math.min(campo.getMaxY(), previstoY));
                break;
            }
        }

        // Ângulo do nosso robô até o ponto previsto
        double anguloMira = Math.atan2(previstoX - getX(), previstoY - getY());
        setTurnGunRightRadians(Utils.normalRelativeAngle(anguloMira - getGunHeadingRadians()));

        // ===== 5. TIRO =====
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemainingRadians()) < 0.1) {
            setFire(potencia);
        }
    }

    /**
     * Wall smoothing: se andar nesse ângulo nos levaria contra a parede,
     * vamos girando o ângulo em direção ao centro até o caminho ficar livre.
     */
    private double suavizarParede(double angulo) {
        double paraCentro = Math.atan2(
            campo.getCenterX() - getX(), campo.getCenterY() - getY());

        int tentativas = 0;   // trava de segurança contra loop infinito
        while (!campo.contains(getX() + Math.sin(angulo) * 160,
                               getY() + Math.cos(angulo) * 160) && tentativas++ < 40) {
            double diferenca = Utils.normalRelativeAngle(paraCentro - angulo);
            angulo += Math.signum(diferenca) * 0.05;
        }
        return angulo;
    }

    /**
     * Vai na direção do ângulo escolhido pelo caminho mais curto:
     * se o ângulo está "atrás" do robô, anda de ré em vez de dar meia-volta.
     */
    private void irParaAngulo(double angulo) {
        double giro = Utils.normalRelativeAngle(angulo - getHeadingRadians());
        int sentido = 1;

        if (Math.abs(giro) > Math.PI / 2) {
            giro = Utils.normalRelativeAngle(giro + Math.PI);
            sentido = -1;
        }
        setTurnRightRadians(giro);
        setAhead(100 * sentido);
    }
}
```

---

### 5.2 AntiGravidade

**Conceitos:** **movimento por campo de forças** (anti-gravity), estruturas de dados para rastrear vários inimigos, `onRobotDeath`, batalhas **melee** (todos contra todos).

**Estratégia:** cada inimigo e cada parede **repele** o robô com uma força proporcional a `1 / distância²`. Somando todas as forças, obtemos o melhor ponto de fuga. O resultado é um movimento fluido que se afasta de multidões e de paredes.

```java
package ensino;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class AntiGravidade extends AdvancedRobot {

    // Guarda o que sabemos de cada inimigo
    private static class Inimigo {
        double x, y;
    }

    // Chave = nome do inimigo
    private final Map<String, Inimigo> inimigos = new HashMap<>();

    // Constante que regula a "intensidade" da repulsão
    private static final double FORCA = 100000;

    @Override
    public void run() {
        setColors(Color.YELLOW, Color.BLACK, Color.YELLOW);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            setTurnRadarRight(Double.POSITIVE_INFINITY);   // radar sempre varrendo
            mover();
            execute();
        }
    }

    /** Soma as forças de todos os inimigos e paredes e anda na direção resultante. */
    private void mover() {
        double forcaX = 0;
        double forcaY = 0;

        // ---- Repulsão dos inimigos ----
        for (Inimigo in : inimigos.values()) {
            double dx = getX() - in.x;               // vetor "do inimigo até mim"
            double dy = getY() - in.y;
            double dist = Math.max(1, Math.hypot(dx, dy));

            double forca = FORCA / (dist * dist);    // lei do inverso do quadrado
            forcaX += forca * dx / dist;             // componentes da força
            forcaY += forca * dy / dist;
        }

        // ---- Repulsão das paredes (esquerda, direita, baixo, cima) ----
        double x = Math.max(1, getX());
        double y = Math.max(1, getY());
        double sobraX = Math.max(1, getBattleFieldWidth() - getX());
        double sobraY = Math.max(1, getBattleFieldHeight() - getY());

        forcaX += FORCA / (x * x) - FORCA / (sobraX * sobraX);
        forcaY += FORCA / (y * y) - FORCA / (sobraY * sobraY);

        // Ângulo da força resultante (0 = norte no Robocode)
        double angulo = Math.atan2(forcaX, forcaY);

        // Escolhe frente ou ré, o que for mais rápido
        double giro = Utils.normalRelativeAngle(angulo - getHeadingRadians());
        int sentido = 1;
        if (Math.abs(giro) > Math.PI / 2) {
            giro = Utils.normalRelativeAngle(giro + Math.PI);
            sentido = -1;
        }
        setTurnRightRadians(giro);
        setAhead(100 * sentido);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing = getHeadingRadians() + e.getBearingRadians();

        // Atualiza (ou cria) o registro do inimigo com sua posição em x, y
        Inimigo in = inimigos.computeIfAbsent(e.getName(), n -> new Inimigo());
        in.x = getX() + e.getDistance() * Math.sin(absBearing);
        in.y = getY() + e.getDistance() * Math.cos(absBearing);

        // Mira direta no último inimigo visto (versão simples)
        setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing - getGunHeadingRadians()));
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemainingRadians()) < 0.15) {
            setFire(e.getDistance() < 250 ? 3 : 1.5);
        }
    }

    // Inimigo destruído: remove do mapa para não sermos repelidos por um fantasma
    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        inimigos.remove(e.getName());
    }
}
```

---

### 5.3 Mestre

**Conceitos:** **escolha inteligente de alvo**, **combinação de técnicas** (anti-gravidade + mira linear), **seleção do inimigo mais fraco ou mais próximo**.

**Estratégia:** parte do `AntiGravidade`, mas agora guarda velocidade, direção e energia de cada inimigo. A cada turno escolhe o **alvo mais próximo** e usa mira linear nele.

```java
package ensino;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class Mestre extends AdvancedRobot {

    private static class Inimigo {
        String nome;
        double x, y;
        double direcao, velocidade, energia, distancia;
    }

    private final Map<String, Inimigo> inimigos = new HashMap<>();
    private static final double FORCA = 100000;

    @Override
    public void run() {
        setColors(Color.BLACK, Color.RED, Color.ORANGE);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            mover();
            atirar();
            execute();
        }
    }

    /** Escolhe o inimigo mais próximo (ou null se não há ninguém conhecido). */
    private Inimigo escolherAlvo() {
        Inimigo melhor = null;
        for (Inimigo in : inimigos.values()) {
            // Recalcula a distância atual, pois nos movemos desde o último scan
            in.distancia = Math.hypot(in.x - getX(), in.y - getY());
            if (melhor == null || in.distancia < melhor.distancia) {
                melhor = in;
            }
        }
        return melhor;
    }

    private void atirar() {
        Inimigo alvo = escolherAlvo();
        if (alvo == null) return;

        double potencia = alvo.distancia < 200 ? 3.0 : (alvo.distancia < 500 ? 2.0 : 1.0);
        potencia = Math.max(0.1, Math.min(potencia, getEnergy() / 4));
        double velocidadeBala = 20 - 3 * potencia;

        // Mira linear (mesma ideia do Preditor)
        double previstoX = alvo.x;
        double previstoY = alvo.y;
        double turnos = 0;
        double limiteX = getBattleFieldWidth() - 18;
        double limiteY = getBattleFieldHeight() - 18;

        while ((++turnos) * velocidadeBala
                < Math.hypot(previstoX - getX(), previstoY - getY())) {
            previstoX += Math.sin(alvo.direcao) * alvo.velocidade;
            previstoY += Math.cos(alvo.direcao) * alvo.velocidade;
            previstoX = Math.max(18, Math.min(limiteX, previstoX));
            previstoY = Math.max(18, Math.min(limiteY, previstoY));
        }

        double mira = Math.atan2(previstoX - getX(), previstoY - getY());
        setTurnGunRightRadians(Utils.normalRelativeAngle(mira - getGunHeadingRadians()));

        if (getGunHeat() == 0 && Math.abs(getGunTurnRemainingRadians()) < 0.1) {
            setFire(potencia);
        }
    }

    /** Anti-gravidade, igual ao robô anterior. */
    private void mover() {
        double forcaX = 0, forcaY = 0;

        for (Inimigo in : inimigos.values()) {
            double dx = getX() - in.x;
            double dy = getY() - in.y;
            double dist = Math.max(1, Math.hypot(dx, dy));
            double forca = FORCA / (dist * dist);
            forcaX += forca * dx / dist;
            forcaY += forca * dy / dist;
        }

        double x = Math.max(1, getX());
        double y = Math.max(1, getY());
        double sobraX = Math.max(1, getBattleFieldWidth() - getX());
        double sobraY = Math.max(1, getBattleFieldHeight() - getY());
        forcaX += FORCA / (x * x) - FORCA / (sobraX * sobraX);
        forcaY += FORCA / (y * y) - FORCA / (sobraY * sobraY);

        double angulo = Math.atan2(forcaX, forcaY);
        double giro = Utils.normalRelativeAngle(angulo - getHeadingRadians());
        int sentido = 1;
        if (Math.abs(giro) > Math.PI / 2) {
            giro = Utils.normalRelativeAngle(giro + Math.PI);
            sentido = -1;
        }
        setTurnRightRadians(giro);
        setAhead(100 * sentido);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing = getHeadingRadians() + e.getBearingRadians();

        Inimigo in = inimigos.computeIfAbsent(e.getName(), n -> new Inimigo());
        in.nome = e.getName();
        in.x = getX() + e.getDistance() * Math.sin(absBearing);
        in.y = getY() + e.getDistance() * Math.cos(absBearing);
        in.direcao = e.getHeadingRadians();
        in.velocidade = e.getVelocity();
        in.energia = e.getEnergy();
        in.distancia = e.getDistance();
    }

    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        inimigos.remove(e.getName());
    }
}
```

---

## 6. Resumo dos níveis

| Robô | Nível | Classe base | Movimento | Mira | Radar |
|------|-------|-------------|-----------|------|-------|
| PrimeiroRobo | Iniciante | `Robot` | Frente/trás | Varredura 360° | Preso ao canhão |
| Sentinela | Iniciante | `Robot` | Parado | Varredura contínua | Preso ao canhão |
| ParedeSimples | Iniciante | `Robot` | Contorna paredes | Fixa para o centro | Preso ao canhão |
| Perseguidor | Intermediário | `AdvancedRobot` | Aproxima/mantém distância | Direta | Radar lock |
| Dancarino | Intermediário | `AdvancedRobot` | Lateral + inversão por tiro | Direta | Radar lock |
| Cacador | Intermediário | `AdvancedRobot` | Máquina de estados | Direta, potência por energia | Radar lock / busca |
| Preditor | Avançado | `AdvancedRobot` | Órbita + wall smoothing | **Linear (preditiva)** | Radar lock |
| AntiGravidade | Avançado | `AdvancedRobot` | **Campo de forças** | Direta | Varredura contínua |
| Mestre | Avançado | `AdvancedRobot` | Campo de forças | **Linear + escolha de alvo** | Varredura contínua |

---

## 7. Exercícios propostos

**Iniciante**
1. No `PrimeiroRobo`, troque `ahead(100)` por um valor aleatório com `Math.random()`.
2. No `Sentinela`, faça o robô mudar a cor quando a energia ficar abaixo de 30 (`setBodyColor`).
3. No `ParedeSimples`, use `onHitByBullet` para inverter o sentido de contorno.

**Intermediário**
4. No `Perseguidor`, altere a distância ideal (150 px) e veja qual valor rende mais vitórias contra `sample.Tracker`.
5. No `Dancarino`, adicione aleatoriedade: inverta a direção também com 5% de chance a cada turno.
6. No `Cacador`, acrescente um terceiro estado, **FUGA**, para quando a energia estiver abaixo de 20.

**Avançado**
7. No `Preditor`, substitua a mira linear por **mira circular** (considere também a velocidade angular do inimigo).
8. No `AntiGravidade`, adicione uma força de atração ao centro do campo para evitar ficar nas bordas.
9. No `Mestre`, escolha o alvo pelo **menor energia** em vez do mais próximo. Compare os resultados.

---

## 8. Referência rápida da API

**Movimento**

| Bloqueante (`Robot`) | Não bloqueante (`AdvancedRobot`) |
|----------------------|----------------------------------|
| `ahead(px)` / `back(px)` | `setAhead(px)` / `setBack(px)` |
| `turnRight(graus)` / `turnLeft(graus)` | `setTurnRight(graus)` / `setTurnLeft(graus)` |
| `turnGunRight(graus)` | `setTurnGunRight(graus)` |
| `turnRadarRight(graus)` | `setTurnRadarRight(graus)` |
| `fire(potencia)` | `setFire(potencia)` |

**Informações do próprio robô**

`getX()`, `getY()`, `getHeading()`, `getGunHeading()`, `getRadarHeading()`, `getEnergy()`, `getGunHeat()`, `getVelocity()`, `getBattleFieldWidth()`, `getBattleFieldHeight()`.

Versões em radianos: `getHeadingRadians()`, `getGunHeadingRadians()`, `getRadarHeadingRadians()`.

**Informações do inimigo (`ScannedRobotEvent e`)**

`e.getName()`, `e.getBearing()`, `e.getDistance()`, `e.getEnergy()`, `e.getHeading()`, `e.getVelocity()` (e as versões `...Radians()`).

**Utilitários (`robocode.util.Utils`)**

- `normalRelativeAngle(rad)`: normaliza para o intervalo −π a π.
- `normalRelativeAngleDegrees(graus)`: normaliza para −180° a 180°.
- `normalAbsoluteAngle(rad)`: normaliza para 0 a 2π.

**Dica final:** use sempre `Utils.normalRelativeAngle...` antes de girar corpo, canhão ou radar. Sem isso, o robô pode escolher o caminho mais longo (por exemplo, girar 350° em vez de −10°).
