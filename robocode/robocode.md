# Robocode para Alunos de Java

> **Robocode** é um jogo de programação em que você escreve, em Java, o "cérebro" de um tanque de guerra virtual. Seu robô luta sozinho (sem teclado, sem mouse) contra robôs escritos por outras pessoas. Ganha quem sobrevive e causa mais dano.

É uma ótima ferramenta de ensino porque pratica, de forma divertida: **classes, herança, métodos, eventos, polimorfismo, laços, condicionais, `static`, classes internas, coleções, trigonometria e depuração**.

---

## Sumário

1. [Visão geral e versões](#1-visão-geral-e-versões)
2. [Instalação e ambiente](#2-instalação-e-ambiente)
3. [Interface e como rodar uma batalha](#3-interface-e-como-rodar-uma-batalha)
4. [Anatomia do tanque](#4-anatomia-do-tanque)
5. [Física e regras do jogo](#5-física-e-regras-do-jogo)
6. [Coordenadas e ângulos](#6-coordenadas-e-ângulos)
7. [Hierarquia de classes de robôs](#7-hierarquia-de-classes-de-robôs)
8. [Eventos](#8-eventos)
9. [Exemplos comentados](#9-exemplos-comentados)
10. [Técnicas de estratégia](#10-técnicas-de-estratégia)
11. [Depuração](#11-depuração)
12. [Restrições de segurança (sandbox)](#12-restrições-de-segurança-sandbox)
13. [Empacotamento, times e torneios](#13-empacotamento-times-e-torneios)
14. [Robocode Tank Royale (a nova geração)](#14-robocode-tank-royale-a-nova-geração)
15. [Problemas comuns](#15-problemas-comuns)
16. [Exercícios propostos](#16-exercícios-propostos)
17. [Cola rápida da API](#17-cola-rápida-da-api)
18. [Links úteis](#18-links-úteis)

---

## 1. Visão geral e versões

- **Criador:** Mathew Nelson (IBM, 2001). Foi liberado como código aberto em 2005. Hoje é mantido por Flemming N. Larsen e colaboradores.
- **Robocode "clássico"** (o que este guia usa nos exemplos): roda na JVM, robôs escritos em Java (também .NET via plugin, mas aqui focamos em Java).
- **Versão atual na data deste guia:** a série **1.10.x** (a 1.10.0 saiu em junho de 2025 e adotou *Semantic Versioning*; a **1.10.1** é a usada, por exemplo, na competição Robocode Ireland 2026). Antes disso, a linha era 1.9.x (por exemplo, 1.9.5.6).
- **Robocode Tank Royale:** projeto novo, independente, em que os robôs conversam com um servidor via WebSocket e podem ser escritos em várias linguagens (Java, C#, Python...). Veja a [seção 14](#14-robocode-tank-royale-a-nova-geração).

> ⚠️ **Segurança:** foram publicadas vulnerabilidades (CVEs de dezembro de 2025) que afetam a versão **1.9.3.6**. Peça aos alunos que baixem a versão atual (1.10.x) do site oficial e evitem instaladores antigos de repositórios de terceiros.

> 💡 Confira sempre o [site oficial](https://robocode.sourceforge.io) para ver qual é a versão mais recente e as notas de lançamento (inclusive qual versão do Java é suportada).

---

## 2. Instalação e ambiente

### Requisitos
- **JDK** instalado (use um JDK LTS recente, como 17 ou 21, a menos que as notas da sua versão do Robocode digam outra coisa). Teste no terminal:
  ```bash
  java -version
  javac -version
  ```

### Passo a passo
1. Baixe o instalador (`robocode-<versão>-setup.jar`) no site oficial.
2. Execute:
   ```bash
   java -jar robocode-1.10.1-setup.jar
   ```
3. Escolha uma pasta de instalação (ex.: `C:\robocode` ou `~/robocode`).
4. Inicie o jogo:
   - Windows: `robocode.bat`
   - Linux / macOS: `./robocode.sh` (talvez precise de `chmod +x robocode.sh`)

### Estrutura de pastas importante

```
robocode/
├── libs/robocode.jar   ← API do jogo (coloque no classpath da sua IDE)
├── robots/             ← aqui ficam seus robôs (fontes .java e .class)
├── battles/            ← batalhas salvas (.battle)
└── javadoc/            ← documentação da API
```

### Opções de desenvolvimento

Você pode editar robôs de duas formas:

**A) Editor embutido:** `Robot → Source Editor → File → New → Robot`. Ele compila com um clique (`Compile → Compile`). Simples, bom para a primeira aula.

**B) IDE (IntelliJ, Eclipse, VS Code, NetBeans):**
1. Crie um projeto Java.
2. Adicione `robocode/libs/robocode.jar` ao *classpath* (ou aos *Libraries*).
3. Escreva seus robôs em um **pacote** (ex.: `alunos`).
4. Compile para uma pasta de saída e informe esse caminho ao Robocode em **Options → Preferences → Development Options → Add** (para ele achar suas classes compiladas).
5. Reinicie o Robocode (ou use *Refresh*) e seu robô aparece na lista.

> A API também pode ser obtida via repositórios Maven/JitPack (o código vive em `github.com/robo-code/robocode`), mas usar o `robocode.jar` da instalação é o caminho mais simples e garante que a API corresponde à versão do jogo.

---

## 3. Interface e como rodar uma batalha

1. **Battle → New**: abre a tela de seleção.
2. À esquerda, escolha os robôs disponíveis e clique em **Add** para colocá-los na batalha. (Os robôs de exemplo ficam no pacote `sample`: `sample.SpinBot`, `sample.Walls`, `sample.Tracker`, `sample.Fire`, `sample.Crazy`, `sample.RamFire`, `sample.Corners`, `sample.TrackFire`, `sample.MyFirstRobot`, `sample.SittingDuck`, `sample.VelocityBot`, `sample.Target`, entre outros.)
3. Na aba **Rules** defina: número de *rounds*, tamanho da arena, taxa de resfriamento do canhão (`gun cooling rate`), tempo de inatividade (`inactivity time`) e **Sentry** (robô-sentinela, nas versões mais novas).
4. Clique em **Start Battle**.
5. Durante a batalha, clique no robô na lateral direita para ver **energia, pontuação** e botões de **Paint** (ativa o `onPaint` do seu robô) e **Pause/Debug**.
6. Ao final aparece o placar com a pontuação detalhada.

### Pontuação (resumo)

| Item | Pontos |
|---|---|
| Sobrevivência | 50 pontos cada vez que um adversário morre antes de você |
| Bônus de último sobrevivente | 10 pontos × número de adversários |
| Dano por tiro | 1 ponto por ponto de dano causado |
| Bônus de morte por tiro | +20% do dano causado ao inimigo que você matou com bala |
| Dano por colisão (ram) | 2 pontos por ponto de dano |
| Bônus de morte por colisão | +30% do dano causado ao inimigo que você matou por colisão |

---

## 4. Anatomia do tanque

O robô tem **três partes que giram de forma independente**:

```
          ▲  Radar  (detecta inimigos, gira até 45°/tick)
          │
       ┌──┴──┐
       │Canhão│  (atira, gira até 20°/tick)
       │ Corpo│  (anda e gira conforme a velocidade)
       └─────┘
```

- **Corpo (body):** movimenta-se (`ahead`, `back`) e gira (`turnLeft`, `turnRight`).
- **Canhão (gun):** dispara balas (`fire`) e gira (`turnGunLeft`, `turnGunRight`).
- **Radar:** só ele "vê". Gera o evento `onScannedRobot` quando o feixe do radar passa por cima de um inimigo.

> ⚠️ **Por padrão, o canhão acompanha o corpo e o radar acompanha o canhão.** Se você gira o corpo 90°, canhão e radar giram juntos. Para desacoplar, use:
> - `setAdjustGunForRobotTurn(true)` (canhão independente do corpo)
> - `setAdjustRadarForGunTurn(true)` (radar independente do canhão)
> - `setAdjustRadarForRobotTurn(true)` (radar independente do corpo)

---

## 5. Física e regras do jogo

O tempo no Robocode é dividido em **turnos (ticks)**. Em cada tick, o jogo processa o que cada robô mandou fazer e depois atualiza o mundo.

| Regra | Valor |
|---|---|
| Tamanho do robô | 36 × 36 px |
| Arena padrão | 800 × 600 px (configurável) |
| Energia inicial | 100 |
| Velocidade máxima do corpo | 8 px/tick |
| Aceleração | 1 px/tick² |
| Desaceleração (frear) | 2 px/tick² |
| Giro máximo do corpo | `10 − 0,75 × |velocidade|` graus/tick |
| Giro máximo do canhão | 20°/tick |
| Giro máximo do radar | 45°/tick |
| Alcance do radar | 1200 px |
| Potência da bala | 0,1 a 3,0 |
| Velocidade da bala | `20 − 3 × potência` px/tick |
| Dano da bala | `4 × potência` (+ `2 × (potência − 1)` se potência > 1) |
| Energia ganha ao acertar | `3 × potência` |
| Custo de atirar | `potência` (sai da sua energia) |
| Aquecimento do canhão | `1 + potência / 5` |
| Resfriamento do canhão | 0,1/tick (padrão), e o canhão começa **quente** (3,0) a cada round |
| Colisão entre robôs | 0,6 de dano para cada um |
| Bater na parede | `max(|velocidade| × 0,5 − 1, 0)` de dano |
| Inatividade | Se ninguém causar dano por ~450 turnos, os robôs perdem energia continuamente |

### O que isso significa na prática

- **Potência alta** = dano maior e bala lenta (fácil de desviar), mas **esquenta mais o canhão** e gasta mais energia.
- **Potência baixa** = bala rápida (difícil de desviar), tiros mais frequentes e menos dano.
- **Girar rápido exige estar devagar:** quanto mais veloz, menor a curva possível.
- **Energia é vida.** Atirar à toa te enfraquece.

### Ordem de execução em cada tick
Resumo simplificado: o jogo move as balas, atualiza giros e movimento dos robôs, detecta colisões, executa o radar (gerando eventos de scan) e só então os eventos são entregues aos robôs. Por isso os dados que você recebe em `onScannedRobot` já têm **um tick de atraso** em relação à realidade, e é por isso que mira preditiva funciona melhor que mirar na posição atual.

---

## 6. Coordenadas e ângulos

Este é o ponto que mais confunde os alunos.

```
          Norte = 0° (heading 0)
                ▲ y
                │
 Oeste 270° ────┼──── Leste 90° ───► x
                │
          Sul = 180°
```

- A origem `(0, 0)` está no **canto inferior esquerdo**. `x` cresce para a direita; `y` cresce **para cima**.
- Os ângulos de *heading* são medidos **em sentido horário a partir do Norte** (0° = norte, 90° = leste). Isso é diferente do círculo trigonométrico da matemática (anti-horário a partir do eixo x).
- **`bearing`** é um ângulo **relativo** ao heading do seu corpo: entre −180° e +180°. Positivo = à sua direita.
- Muitos métodos têm duas versões: **graus** (`getHeading()`) e **radianos** (`getHeadingRadians()`). Escolha uma e seja consistente.

### Fórmulas essenciais

Posição de um inimigo detectado (em radianos):

```java
// ângulo ABSOLUTO (da bússola) até o inimigo = meu heading + bearing relativo
double anguloAbsoluto = getHeadingRadians() + e.getBearingRadians();

// Como o ângulo é medido a partir do norte, o seno vai no X e o cosseno no Y
double inimigoX = getX() + e.getDistance() * Math.sin(anguloAbsoluto);
double inimigoY = getY() + e.getDistance() * Math.cos(anguloAbsoluto);
```

Ângulo da bússola entre dois pontos `(x1, y1)` → `(x2, y2)`:

```java
// atan2(dx, dy): note a ordem "invertida" em relação à matemática usual!
double angulo = Math.atan2(x2 - x1, y2 - y1);
```

Normalizar ângulos (para evitar que o robô dê voltas de 350° em vez de 10°):

```java
import robocode.util.Utils;

Utils.normalRelativeAngleDegrees(angulo);   // → [-180, 180)
Utils.normalAbsoluteAngleDegrees(angulo);   // → [0, 360)
Utils.normalRelativeAngle(radianos);        // → [-π, π)
Utils.normalAbsoluteAngle(radianos);        // → [0, 2π)
```

---

## 7. Hierarquia de classes de robôs

```
Object
 └── _Robot (interna)
      ├── JuniorRobot      → API mínima, ideal para o primeiro contato
      └── Robot            → API básica, chamadas BLOQUEANTES
           └── AdvancedRobot   → chamadas NÃO bloqueantes (setXxx + execute)
                ├── TeamRobot      → comunicação entre membros do time
                └── RateControlRobot → controla por "taxas" (graus/tick) em vez de distâncias
```

(Existe ainda a interface `Droid`, que marca um robô-escudeiro sem radar, para uso em times.)

| Classe | Quando usar |
|---|---|
| `JuniorRobot` | Primeira aula; sem eventos com parâmetros; campos públicos com os dados. |
| `Robot` | Aprender lógica sequencial. `ahead(100)` **espera terminar** antes da próxima linha. |
| `AdvancedRobot` | Robôs competitivos. `setAhead(100)` apenas **agenda**; tudo é executado junto em `execute()`. |
| `TeamRobot` | Times de 2 ou mais robôs que trocam mensagens e não se atacam. |

### Bloqueante × não bloqueante (a ideia mais importante do curso)

```java
// ----- Robot (bloqueante): as ações acontecem uma DEPOIS da outra -----
ahead(100);        // anda 100 px, só passa para a próxima linha quando terminar
turnRight(90);     // depois gira 90°
fire(1);           // depois atira

// ----- AdvancedRobot (não bloqueante): as ações acontecem JUNTAS -----
setAhead(100);     // apenas agenda andar 100 px
setTurnRight(90);  // agenda girar 90°
setFire(1);        // agenda o tiro
execute();         // executa TUDO isso no mesmo tick
```

> 🧠 Analogia para os alunos: `Robot` é uma pessoa que só faz uma coisa de cada vez. `AdvancedRobot` é um piloto que aperta vários botões e depois diz "agora vai!" (`execute()`).

---

## 8. Eventos

Seu robô **reage** a eventos sobrescrevendo métodos (herança + polimorfismo na prática). Os mais usados:

| Evento (método) | Quando acontece | Dados úteis |
|---|---|---|
| `run()` | Início de cada round. É o "main" do robô. | — |
| `onScannedRobot(ScannedRobotEvent e)` | O radar viu um inimigo. | `getBearing()`, `getDistance()`, `getEnergy()`, `getHeading()`, `getVelocity()`, `getName()` |
| `onHitByBullet(HitByBulletEvent e)` | Você foi atingido. | `getBearing()`, `getPower()`, `getName()`, `getHeading()` |
| `onBulletHit(BulletHitEvent e)` | Sua bala acertou alguém. | `getName()`, `getEnergy()` (energia restante do alvo), `getBullet()` |
| `onBulletMissed(BulletMissedEvent e)` | Sua bala saiu da arena sem acertar. | `getBullet()` |
| `onBulletHitBullet(BulletHitBulletEvent e)` | Sua bala colidiu com outra bala. | `getBullet()`, `getHitBullet()` |
| `onHitWall(HitWallEvent e)` | Bateu na parede. | `getBearing()` |
| `onHitRobot(HitRobotEvent e)` | Bateu em outro robô. | `getBearing()`, `isMyFault()`, `getEnergy()` |
| `onRobotDeath(RobotDeathEvent e)` | Algum robô morreu. | `getName()` |
| `onDeath(DeathEvent e)` | Você morreu. | — |
| `onWin(WinEvent e)` | Você venceu o round. | — |
| `onRoundEnded(RoundEndedEvent e)` | Fim de round. | — |
| `onBattleEnded(BattleEndedEvent e)` | Fim da batalha toda. | `getResults()` |
| `onStatus(StatusEvent e)` | A cada tick (útil para ler o estado). | `getStatus()` |
| `onSkippedTurn(SkippedTurnEvent e)` | Seu código demorou demais e perdeu um tick. | — |
| `onCustomEvent(CustomEvent e)` | Uma `Condition` sua virou verdadeira. | `getCondition()` |
| `onMessageReceived(MessageEvent e)` | (`TeamRobot`) chegou mensagem. | `getSender()`, `getMessage()` |
| `onPaint(Graphics2D g)` | Desenho de depuração (veja a seção 11). | — |

### Prioridade e interrupção de eventos
- Cada evento tem **prioridade**; se um evento de prioridade maior ocorrer enquanto outro está em tratamento, ele pode **interromper** o manipulador atual.
- Em `Robot`, uma chamada bloqueante como `ahead(1000)` dentro de `run()` pode ser interrompida por eventos (o manipulador roda no meio da ação).
- Boas práticas: **manipuladores de evento devem ser rápidos**. Em `AdvancedRobot`, prefira `setXxx` dentro dos eventos e deixe o `execute()` para o laço principal.

---

## 9. Exemplos comentados

> Todos os exemplos assumem um **pacote** chamado `alunos` (crie a pasta `alunos` dentro de `robots/` ou use sua IDE). Troque pelo nome que preferir.
> Cada robô é uma classe pública com o **mesmo nome do arquivo**.

---

### Exemplo 1 — Primeiro robô com `Robot` (bloqueante)

Conceitos: herança, método `run()`, laço infinito, eventos.

```java
package alunos;

import robocode.*;          // Robot, ScannedRobotEvent, HitByBulletEvent, HitWallEvent...
import java.awt.Color;      // para pintar o robô

/**
 * MeuPrimeiroRobo: anda de um lado para o outro girando o canhão
 * e atira em qualquer inimigo que o radar encontrar.
 */
public class MeuPrimeiroRobo extends Robot {

    /**
     * run() é chamado no início de cada round.
     * Quando run() termina, o robô para de agir, então usamos while(true).
     */
    @Override
    public void run() {
        // Cores: corpo, canhão, radar (opcional, mas ajuda a identificar o robô)
        setColors(Color.BLUE, Color.WHITE, Color.RED);

        while (true) {
            ahead(100);          // anda 100 px para frente (BLOQUEIA até terminar)
            turnGunRight(360);   // gira o canhão (e o radar junto) uma volta inteira
            back(100);           // anda 100 px para trás
            turnGunRight(360);   // outra volta de varredura
        }
    }

    /**
     * O radar passou por cima de um inimigo.
     * Como o canhão está girando junto com o radar, ele aponta para o inimigo agora.
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);                 // dispara com potência 1 (equilibrada)
    }

    /**
     * Fomos atingidos por uma bala.
     * e.getBearing() é o ângulo (relativo ao nosso corpo) de onde veio o tiro.
     * Virar-se de lado em relação ao tiro nos afasta da linha de fogo.
     */
    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

    /**
     * Bateu na parede: recua um pouco para sair do canto.
     */
    @Override
    public void onHitWall(HitWallEvent e) {
        back(50);
    }
}
```

**Para discutir em sala:** o que acontece quando `onScannedRobot` dispara no meio do `ahead(100)`? (O evento interrompe a ação em andamento, atira, e depois `run()` continua.)

---

### Exemplo 2 — Espiral com `AdvancedRobot` (não bloqueante)

Conceitos: `setXxx` × `execute`, movimento contínuo, `setMaxVelocity`.

```java
package alunos;

import robocode.*;
import java.awt.Color;

/**
 * Espiral: gira e anda ao mesmo tempo, tornando-se um alvo difícil de acertar.
 * Atira em quem passar na frente do radar.
 */
public class Espiral extends AdvancedRobot {

    @Override
    public void run() {
        setBodyColor(Color.ORANGE);
        setGunColor(Color.BLACK);
        setRadarColor(Color.YELLOW);

        while (true) {
            // Agenda um giro "praticamente infinito" para a direita.
            // Como é setXxx, NÃO bloqueia: apenas registra a intenção.
            setTurnRight(10000);

            // Limita a velocidade a 5 px/tick. Com velocidade menor, o giro do corpo
            // é mais fechado (lembre: giro máx. = 10 − 0,75·|v|).
            setMaxVelocity(5);

            // ahead() é BLOQUEANTE: ao chamá-lo, o robô executa TODOS os comandos
            // agendados (giro + movimento) juntos, tick a tick.
            ahead(10000);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        fire(3);   // potência máxima: seguro porque o inimigo geralmente está perto
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        // Se colidimos e o inimigo está à nossa frente, vamos trocar o sentido.
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            back(100);
        } else {
            ahead(100);
        }
    }
}
```

---

### Exemplo 3 — Torreta com travamento de radar e mira direta

Conceitos: radar *lock*, `setAdjust...`, ângulos relativos, radianos, `Utils`.

```java
package alunos;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

/**
 * Torreta: fica parada no lugar e rastreia o inimigo com o radar.
 * Ideal para 1 contra 1 (com vários inimigos o radar "pula" de alvo em alvo).
 */
public class Torreta extends AdvancedRobot {

    @Override
    public void run() {
        setColors(Color.DARK_GRAY, Color.RED, Color.WHITE);

        // Desacopla as três partes: cada uma gira de forma independente.
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        // Gira o radar infinitamente até achar alguém.
        // Depois de achar, onScannedRobot passa a controlar o radar a cada tick.
        while (true) {
            turnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // 1) Direção da bússola (absoluta) até o inimigo, em radianos.
        double anguloAbsoluto = getHeadingRadians() + e.getBearingRadians();

        // 2) TRAVA DE RADAR: quanto o radar precisa girar para ficar apontado
        //    ao inimigo? Multiplicamos por 2 para "ultrapassar" um pouco o alvo.
        //    Assim o radar oscila em cima dele e nunca o perde de vista.
        double giroRadar = Utils.normalRelativeAngle(anguloAbsoluto - getRadarHeadingRadians());
        setTurnRadarRightRadians(giroRadar * 2);

        // 3) MIRA DIRETA: gira o canhão exatamente para onde o inimigo está agora.
        double giroCanhao = Utils.normalRelativeAngle(anguloAbsoluto - getGunHeadingRadians());
        setTurnGunRightRadians(giroCanhao);

        // 4) Só atira se o canhão está frio E já quase alinhado (menos de 3 graus de erro).
        if (getGunHeat() == 0 && Math.abs(giroCanhao) < Math.toRadians(3)) {
            setFire(2);
        }
        // Sem execute() explícito aqui: ele acontece no laço de run() automaticamente
        // ao fim do tick quando há ações agendadas.
    }
}
```

**Observação didática:** mira direta erra contra alvos que se movem, porque a bala demora a chegar. O Exemplo 5 resolve isso.

---

### Exemplo 4 — Eventos personalizados com `Condition`

Conceitos: classe interna anônima, `addCustomEvent`, `onCustomEvent`.

```java
package alunos;

import robocode.*;
import java.awt.Color;

/**
 * Cauteloso: muda de comportamento quando a energia fica baixa.
 * Demonstra como criar um evento seu usando Condition.
 */
public class Cauteloso extends AdvancedRobot {

    // Atributo de instância: guarda o "modo" atual do robô.
    private boolean modoDefensivo = false;

    @Override
    public void run() {
        setColors(Color.GREEN, Color.GREEN, Color.GREEN);

        // Registra um evento personalizado. A cada tick o jogo chama test();
        // quando retornar true, onCustomEvent() é acionado.
        // Uma classe interna anônima enxerga os métodos do robô, como getEnergy().
        addCustomEvent(new Condition("energiaBaixa") {
            @Override
            public boolean test() {
                return getEnergy() < 30;
            }
        });

        while (true) {
            if (modoDefensivo) {
                // Defensivo: anda em zigue-zague e atira pouco.
                setTurnRight(45);
                setAhead(150);
            } else {
                // Agressivo: anda em linha reta.
                setAhead(200);
            }
            turnGunRight(360);   // varre o ambiente (bloqueante; executa o que foi agendado)
        }
    }

    @Override
    public void onCustomEvent(CustomEvent e) {
        // Podemos ter vários eventos personalizados; distinguimos pelo nome.
        if (e.getCondition().getName().equals("energiaBaixa")) {
            modoDefensivo = true;
            out.println("Energia baixa! Modo defensivo ativado.");  // aparece na janela do robô
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Potência proporcional ao modo: economiza energia quando está fraco.
        fire(modoDefensivo ? 0.5 : 2);
    }
}
```

---

### Exemplo 5 — Mira linear (prevê onde o inimigo estará)

Conceitos: trigonometria, iteração, `Rules`, `Point2D`, métodos auxiliares.

```java
package alunos;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * AtiradorLinear: assume que o inimigo continuará na mesma direção e
 * velocidade. Calcula o ponto onde a bala e o inimigo se encontram.
 */
public class AtiradorLinear extends AdvancedRobot {

    @Override
    public void run() {
        setColors(Color.MAGENTA, Color.BLACK, Color.WHITE);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            turnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double potencia = escolherPotencia(e.getDistance());

        // Velocidade da bala depende da potência: 20 - 3 * potência.
        double velocidadeBala = Rules.getBulletSpeed(potencia);

        // Posição ATUAL do inimigo
        double anguloAbsoluto = getHeadingRadians() + e.getBearingRadians();
        double inimigoX = getX() + e.getDistance() * Math.sin(anguloAbsoluto);
        double inimigoY = getY() + e.getDistance() * Math.cos(anguloAbsoluto);

        // Dados de movimento do inimigo
        double rumoInimigo = e.getHeadingRadians();
        double velInimigo  = e.getVelocity();

        // SIMULAÇÃO: avança o tempo um tick de cada vez, movendo o inimigo
        // virtual, até que a bala (que anda velocidadeBala px/tick) alcance o ponto.
        double previstoX = inimigoX;
        double previstoY = inimigoY;
        double tempo = 0;

        while ((++tempo) * velocidadeBala < Point2D.distance(getX(), getY(), previstoX, previstoY)) {
            previstoX += Math.sin(rumoInimigo) * velInimigo;
            previstoY += Math.cos(rumoInimigo) * velInimigo;

            // Se o ponto previsto saiu da arena, "prendemos" na borda
            // (o inimigo não pode ir além das paredes). 18 px = metade do robô.
            double margem = 18;
            previstoX = Math.max(margem, Math.min(getBattleFieldWidth()  - margem, previstoX));
            previstoY = Math.max(margem, Math.min(getBattleFieldHeight() - margem, previstoY));
        }

        // Ângulo (bússola) até o ponto previsto
        double anguloAteAlvo = Utils.normalAbsoluteAngle(
                Math.atan2(previstoX - getX(), previstoY - getY()));

        // Gira o canhão até lá
        setTurnGunRightRadians(Utils.normalRelativeAngle(anguloAteAlvo - getGunHeadingRadians()));

        // Trava de radar (igual ao Exemplo 3)
        double giroRadar = Utils.normalRelativeAngle(anguloAbsoluto - getRadarHeadingRadians());
        setTurnRadarRightRadians(giroRadar * 2);

        // Atira se o canhão está pronto e quase alinhado
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 5) {
            setFire(potencia);
        }
    }

    /**
     * Escolhe potência pela distância: perto = tiro forte (bala chega rápido de qualquer
     * forma), longe = tiro fraco e rápido (mais chance de acertar).
     */
    private double escolherPotencia(double distancia) {
        double p;
        if (distancia < 150)      p = 3.0;
        else if (distancia < 400) p = 2.0;
        else                      p = 1.0;

        // Economiza energia quando está fraco
        if (getEnergy() < 15) {
            p = Math.min(p, 0.5);
        }
        return p;
    }
}
```

---

### Exemplo 6 — Robô completo: órbita, desvio de tiros, suavização de paredes e mira linear

Conceitos: **organização em métodos pequenos**, constantes, estado entre eventos, detecção de tiro inimigo pela queda de energia.

```java
package alunos;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * RoboCompleto (1 contra 1):
 *  - Radar travado no inimigo.
 *  - Orbita em volta dele (movimento perpendicular) mantendo uma distância ideal.
 *  - Inverte o sentido da órbita quando percebe que o inimigo atirou.
 *  - Evita paredes antecipadamente (wall smoothing).
 *  - Mira com previsão linear.
 */
public class RoboCompleto extends AdvancedRobot {

    // ---- Constantes (static final): fáceis de ajustar e de ler ----
    private static final double DISTANCIA_IDEAL = 300;   // distância desejada do inimigo
    private static final double MARGEM_PAREDE   = 40;    // folga para as bordas
    private static final double TAMANHO_VARA    = 140;   // "antena" que olha à frente

    // ---- Estado do robô ----
    private int direcao = 1;                    // +1 = orbita horário, -1 = anti-horário
    private double energiaAnteriorInimigo = 100;

    // Variável ESTÁTICA: sobrevive entre rounds (variáveis de instância são zeradas)
    private static int tirosDisparados = 0;

    @Override
    public void run() {
        setColors(new Color(30, 30, 120), Color.CYAN, Color.WHITE);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        // Reinicia o estado a cada round (a instância é nova, mas deixamos explícito)
        energiaAnteriorInimigo = 100;
        direcao = 1;

        while (true) {
            turnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double anguloAbsoluto = getHeadingRadians() + e.getBearingRadians();

        travarRadar(anguloAbsoluto);
        mover(e, anguloAbsoluto);
        mirarEAtirar(e, anguloAbsoluto);
    }

    // =========================================================
    //  RADAR
    // =========================================================
    private void travarRadar(double anguloAbsoluto) {
        double giro = Utils.normalRelativeAngle(anguloAbsoluto - getRadarHeadingRadians());
        setTurnRadarRightRadians(giro * 2);
    }

    // =========================================================
    //  MOVIMENTO
    // =========================================================
    private void mover(ScannedRobotEvent e, double anguloAbsoluto) {
        // Se a energia do inimigo caiu entre 0,1 e 3,0, ele provavelmente ATIROU.
        // Trocamos de sentido para que a bala (que vem na direção em que estávamos) erre.
        double queda = energiaAnteriorInimigo - e.getEnergy();
        if (queda > 0.09 && queda <= 3.0) {
            direcao = -direcao;
        }
        energiaAnteriorInimigo = e.getEnergy();

        // Órbita: o ideal é andar a 90° da direção do inimigo.
        // Ajustamos um pouco para aproximar (se longe) ou afastar (se perto).
        double ajuste = (e.getDistance() > DISTANCIA_IDEAL) ? 0.25 : -0.25;  // em radianos
        double anguloDesejado = anguloAbsoluto + direcao * (Math.PI / 2 - ajuste);

        // Corrige o ângulo para não entrar em parede
        anguloDesejado = suavizarParede(anguloDesejado, direcao);

        // Aplica: vira para o ângulo desejado e anda
        setTurnRightRadians(Utils.normalRelativeAngle(anguloDesejado - getHeadingRadians()));
        setAhead(100);
    }

    /**
     * Wall smoothing: "olha" TAMANHO_VARA pixels à frente no ângulo desejado.
     * Se o ponto cair fora da área segura, gira o ângulo aos poucos até ficar dentro.
     */
    private double suavizarParede(double angulo, int sentido) {
        Rectangle2D.Double areaSegura = new Rectangle2D.Double(
                MARGEM_PAREDE, MARGEM_PAREDE,
                getBattleFieldWidth()  - 2 * MARGEM_PAREDE,
                getBattleFieldHeight() - 2 * MARGEM_PAREDE);

        int tentativas = 0;
        while (!areaSegura.contains(
                    getX() + Math.sin(angulo) * TAMANHO_VARA,
                    getY() + Math.cos(angulo) * TAMANHO_VARA)
                && tentativas++ < 100) {
            angulo += sentido * 0.05;   // gira ~3° e testa de novo
        }
        return angulo;
    }

    // =========================================================
    //  MIRA E TIRO
    // =========================================================
    private void mirarEAtirar(ScannedRobotEvent e, double anguloAbsoluto) {
        double potencia = escolherPotencia(e);
        double velocidadeBala = Rules.getBulletSpeed(potencia);

        // Posição atual do inimigo
        double inimigoX = getX() + e.getDistance() * Math.sin(anguloAbsoluto);
        double inimigoY = getY() + e.getDistance() * Math.cos(anguloAbsoluto);

        // Predição linear (mesma ideia do Exemplo 5)
        double previstoX = inimigoX, previstoY = inimigoY;
        double tempo = 0;
        while ((++tempo) * velocidadeBala < Point2D.distance(getX(), getY(), previstoX, previstoY)) {
            previstoX += Math.sin(e.getHeadingRadians()) * e.getVelocity();
            previstoY += Math.cos(e.getHeadingRadians()) * e.getVelocity();
            previstoX = Math.max(18, Math.min(getBattleFieldWidth()  - 18, previstoX));
            previstoY = Math.max(18, Math.min(getBattleFieldHeight() - 18, previstoY));
        }

        double anguloAteAlvo = Math.atan2(previstoX - getX(), previstoY - getY());
        setTurnGunRightRadians(Utils.normalRelativeAngle(anguloAteAlvo - getGunHeadingRadians()));

        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 5 && getEnergy() > 0.5) {
            setFire(potencia);
            tirosDisparados++;
        }
    }

    private double escolherPotencia(ScannedRobotEvent e) {
        double p = (e.getDistance() < 200) ? 3.0 : (e.getDistance() < 450 ? 2.0 : 1.0);
        if (getEnergy() < 20) p = Math.min(p, 1.0);
        // Não precisa gastar mais do que o necessário para matar o alvo
        // (dano ≈ 4 × potência para potência ≤ 1).
        p = Math.min(p, Math.max(0.1, e.getEnergy() / 4));
        return p;
    }

    // =========================================================
    //  OUTROS EVENTOS
    // =========================================================
    @Override
    public void onHitWall(HitWallEvent e) {
        direcao = -direcao;   // se encostou na parede, inverte a órbita
    }

    @Override
    public void onWin(WinEvent e) {
        // Dancinha da vitória
        for (int i = 0; i < 30; i++) {
            turnRight(30);
            turnLeft(30);
        }
    }

    @Override
    public void onRoundEnded(RoundEndedEvent e) {
        out.println("Tiros disparados até agora na batalha: " + tirosDisparados);
    }
}
```

---

### Exemplo 7 — `JuniorRobot` (para quem está começando)

Conceitos: a API mais simples possível; os dados vêm em **campos públicos**, não em objetos de evento.

```java
package alunos;

import robocode.*;
import java.awt.Color;

/**
 * Juniorzinho: usa JuniorRobot, que tem uma API reduzida.
 * Os eventos NÃO recebem parâmetros; os dados ficam em campos como
 * scannedDistance, scannedBearing, hitByBulletBearing etc.
 */
public class Juniorzinho extends JuniorRobot {

    @Override
    public void run() {
        // Cores: corpo, canhão, radar, bala, arco de scan
        setColors(Color.PINK, Color.BLACK, Color.BLUE, Color.RED, Color.WHITE);

        while (true) {
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

    // Sem parâmetros: consulte os campos herdados.
    @Override
    public void onScannedRobot() {
        // scannedAngle é o ângulo absoluto do inimigo; apontamos o canhão para ele.
        turnGunTo(scannedAngle);
        // scannedDistance: perto = tiro forte; longe = tiro fraco.
        if (scannedDistance < 200) {
            fire(3);
        } else {
            fire(1);
        }
    }

    @Override
    public void onHitByBullet() {
        // hitByBulletBearing: de onde veio o tiro, relativo ao corpo
        turnAheadLeft(100, 90 - hitByBulletBearing);
    }

    @Override
    public void onHitWall() {
        back(50);
    }
}
```

---

### Exemplo 8 — Time de robôs com comunicação (`TeamRobot`)

Conceitos: comunicação por mensagens, `Serializable`, evitar fogo amigo.

**Arquivo `Lider.java`**

```java
package alunos;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Lider: tem o radar e informa aos companheiros onde está o inimigo.
 */
public class Lider extends TeamRobot {

    @Override
    public void run() {
        setColors(Color.RED, Color.BLACK, Color.RED);
        setAdjustRadarForGunTurn(true);
        while (true) {
            turnRadarRight(360);   // varre sem parar
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Não atira nem reporta companheiros de time!
        if (isTeammate(e.getName())) {
            return;
        }

        // Calcula a posição do inimigo
        double anguloAbsoluto = Math.toRadians(getHeading() + e.getBearing());
        double x = getX() + e.getDistance() * Math.sin(anguloAbsoluto);
        double y = getY() + e.getDistance() * Math.cos(anguloAbsoluto);

        try {
            // broadcastMessage envia a TODOS os companheiros.
            // O objeto precisa ser Serializable (Point2D.Double é).
            broadcastMessage(new Point2D.Double(x, y));
        } catch (java.io.IOException ex) {
            out.println("Falha ao enviar: " + ex.getMessage());
        }

        // O próprio líder também atira
        double giro = Utils.normalRelativeAngleDegrees(getHeading() + e.getBearing() - getGunHeading());
        turnGunRight(giro);
        fire(2);
    }
}
```

**Arquivo `Soldado.java`**

```java
package alunos;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Soldado: recebe coordenadas do líder e dispara nelas.
 */
public class Soldado extends TeamRobot {

    @Override
    public void run() {
        setColors(Color.ORANGE, Color.BLACK, Color.ORANGE);
        while (true) {
            ahead(80);
            back(80);   // movimento simples enquanto espera mensagens
        }
    }

    /**
     * Chamado quando chega uma mensagem de um companheiro.
     */
    @Override
    public void onMessageReceived(MessageEvent e) {
        if (e.getMessage() instanceof Point2D.Double) {
            Point2D.Double alvo = (Point2D.Double) e.getMessage();

            // Ângulo da bússola até o alvo
            double angulo = Math.toDegrees(Math.atan2(alvo.x - getX(), alvo.y - getY()));
            turnGunRight(Utils.normalRelativeAngleDegrees(angulo - getGunHeading()));
            fire(1.5);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Fogo amigo desativado: só atira se NÃO for companheiro
        if (!isTeammate(e.getName())) {
            fire(1);
        }
    }
}
```

**Arquivo `MeuTime.team`** (fica na mesma pasta dos robôs, ex. `robots/alunos/`):

```properties
# Lista os robôs do time, separados por vírgula (nome completo com pacote)
robots=alunos.Lider,alunos.Soldado,alunos.Soldado
```

---

### Exemplo 9 — Desenhando informação de depuração com `onPaint`

Conceitos: `Graphics2D`, depuração visual.

```java
package alunos;

import robocode.*;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Pintor: mostra visualmente onde o robô "acha" que o inimigo está.
 * Para ver o desenho, clique no botão "Paint" do robô durante a batalha.
 */
public class Pintor extends AdvancedRobot {

    private double inimigoX, inimigoY;   // última posição conhecida
    private boolean viuInimigo = false;

    @Override
    public void run() {
        while (true) {
            turnRadarRight(360);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double ang = Math.toRadians(getHeading() + e.getBearing());
        inimigoX = getX() + e.getDistance() * Math.sin(ang);
        inimigoY = getY() + e.getDistance() * Math.cos(ang);
        viuInimigo = true;
    }

    /**
     * Chamado a cada quadro. Usa o sistema de coordenadas da arena
     * (origem no canto inferior esquerdo, y para cima).
     */
    @Override
    public void onPaint(Graphics2D g) {
        if (!viuInimigo) return;

        // Círculo vermelho transparente ao redor do inimigo
        g.setColor(new Color(255, 0, 0, 100));
        g.fillOval((int) inimigoX - 30, (int) inimigoY - 30, 60, 60);

        // Linha amarela do nosso robô até o inimigo
        g.setColor(Color.YELLOW);
        g.drawLine((int) getX(), (int) getY(), (int) inimigoX, (int) inimigoY);

        // Texto com a distância
        g.setColor(Color.WHITE);
        double dist = Math.hypot(inimigoX - getX(), inimigoY - getY());
        g.drawString(String.format("%.0f px", dist), (int) inimigoX + 35, (int) inimigoY);
    }
}
```

---

### Exemplo 10 — Memória entre rounds e arquivo de dados

Conceitos: `static` × instância, `getDataFile`, `RobocodeFileOutputStream`, tratamento de exceções.

```java
package alunos;

import robocode.*;
import java.io.*;

/**
 * Memorioso: demonstra como guardar informação.
 *  - Atributos de INSTÂNCIA são zerados a cada round.
 *  - Atributos ESTÁTICOS persistem durante toda a batalha.
 *  - Arquivos só podem ser gravados pela API getDataFile (pasta própria do robô,
 *    com cota de aproximadamente 200 KB).
 */
public class Memorioso extends AdvancedRobot {

    private int acertosNoRound = 0;          // volta a 0 a cada round
    private static int acertosTotais = 0;    // acumula na batalha inteira

    @Override
    public void run() {
        while (true) {
            ahead(100);
            turnGunRight(360);
            back(100);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
    }

    @Override
    public void onBulletHit(BulletHitEvent e) {
        acertosNoRound++;
        acertosTotais++;
    }

    @Override
    public void onBattleEnded(BattleEndedEvent e) {
        // Grava as estatísticas em um arquivo na pasta de dados do robô.
        // O try-with-resources fecha o arquivo automaticamente.
        try (PrintStream saida = new PrintStream(
                new RobocodeFileOutputStream(getDataFile("estatisticas.txt")))) {
            saida.println("Acertos totais: " + acertosTotais);
        } catch (IOException ex) {
            out.println("Não foi possível salvar: " + ex.getMessage());
        }
    }
}
```

---

## 10. Técnicas de estratégia

### 10.1 Radar
| Técnica | Ideia |
|---|---|
| **Giro infinito** | `turnRadarRight(Double.POSITIVE_INFINITY)`. Simples; ruim em batalhas com muitos robôs. |
| **Radar lock (1v1)** | A cada scan, aponta o radar para o inimigo com um *overshoot* (`ângulo × 2`). Nunca perde o alvo. |
| **Radar de melee** | Gira em um sentido até ter varrido todos os inimigos, inverte e repete. |

### 10.2 Movimento
| Técnica | Ideia |
|---|---|
| **Zigue-zague / aleatório** | Mudar direção e velocidade de forma imprevisível. Fácil de implementar. |
| **Órbita (strafing)** | Mover-se perpendicularmente ao inimigo, invertendo o sentido de vez em quando. |
| **Anti-gravidade** | Cada inimigo/parede "repele" o robô; soma de vetores define a direção. |
| **Wall smoothing** | Corrigir o ângulo antes de bater na parede (Exemplo 6). |
| **Detecção de tiro** | Queda de energia do inimigo entre 0,1 e 3,0 ⇒ ele provavelmente atirou. |
| **Wave surfing** | (Avançado) Rastrear "ondas" de balas inimigas e escolher a direção mais segura. |

### 10.3 Mira
| Técnica | Ideia | Contra quem funciona |
|---|---|---|
| **Direta (head-on)** | Mirar onde o inimigo está. | Alvos parados ou lentos. |
| **Linear** | Assumir velocidade e direção constantes. | Robôs que andam em linha reta. |
| **Circular** | Assumir velocidade angular constante. | Robôs que giram. |
| **Guess Factor** | Aprender estatisticamente para onde o inimigo foge. | Robôs sofisticados. |
| **Pattern matching** | Procurar sequências repetidas do histórico. | Movimento repetitivo. |

### 10.4 Gestão de energia
- **Fórmula intuitiva:** potência maior quando o inimigo está perto e você tem energia de sobra; menor quando está longe ou você está fraco.
- Atirar e **errar** é prejuízo duplo: você perde a energia gasta e deixa o inimigo vivo. Acertar devolve `3 × potência`.
- Contra um inimigo **parado** (energia 0, desativado), uma bala de potência 0,1 já basta para finalizar.

### 10.5 Estratégias clássicas
- **Ramming:** correr e bater no inimigo (funciona bem contra robôs parados ou fracos).
- **Cantos (Corners):** ir ao canto reduz o número de direções de ataque.
- **Sentar e atirar (SittingDuck):** o pior robô possível, mas serve de alvo de testes.
- **Melee:** batalhas com muitos robôs: priorize sobrevivência e escolha alvos fáceis (energia baixa, perto, parados).

---

## 11. Depuração

### 11.1 `out.println`
`out` é um `PrintStream` que escreve na **janela de console do robô** (clique no botão do robô durante a batalha). **Não use `System.out`**: ele é redirecionado e pode ser limitado.

```java
out.println("Energia: " + getEnergy() + " | Tick: " + getTime());
```

> ⚠️ Imprimir demais **deixa o jogo lento** e pode causar `SkippedTurnEvent`.

### 11.2 `onPaint`
Veja o Exemplo 9. Habilite pelo botão **Paint** do robô na batalha.

### 11.3 Pausar e depurar
- Botão **Pause/Debug** na janela de batalha: congela o jogo e permite avançar *tick a tick* com **Step**.
- Em uma IDE, é possível depurar com *breakpoints* configurando o Robocode como programa externo e a opção de depuração (**Options → Preferences → Development Options**, e desabilitar o *timeout* de turno durante a depuração).

### 11.4 Mensagens de erro comuns
- `SkippedTurnEvent`: seu código demora demais por tick. Cada robô tem um tempo limite de CPU por tick. Evite laços pesados.
- Robô desativado (*disabled*): estourou o limite de skipped turns.

---

## 12. Restrições de segurança (sandbox)

Por segurança, os robôs rodam em uma **área restrita**. Alunos costumam esbarrar nisso.

| Permitido | Proibido |
|---|---|
| Ler/escrever arquivos **somente** via `getDataFile(...)` (cota de ~200 KB) | Ler/escrever em qualquer outro lugar do disco |
| Usar `static` para guardar dados entre rounds | Abrir conexões de rede, executar programas externos |
| Criar objetos, usar coleções, matemática, `java.util`, `java.awt.geom` | Criar *threads* próprias (isso pode ser bloqueado) |
| Usar `out.println` | Chamar `System.exit`, mexer em propriedades do sistema |

Também **não chame métodos do robô (`ahead`, `getX`...) no construtor** ou em inicializadores de atributos: o jogo ainda não terminou de preparar o robô. Faça isso em `run()`.

---

## 13. Empacotamento, times e torneios

### Pacotes
- Use sempre um **pacote** (`package alunos;`) e a pasta correspondente. O Robocode usa o nome completo (`alunos.MeuRobo`) para identificar o robô.
- A classe precisa ser `public`, ter um construtor sem argumentos e estar numa pasta de `robots/` correspondente ao pacote.

### Metadados (`.properties`)
Opcionalmente, crie `MeuRobo.properties` ao lado da classe para dar descrição:

```properties
robot.description=Robô de órbita com mira linear
robot.author.name=Nome do Aluno
robot.classname=alunos.MeuRobo
robot.name=MeuRobo
robot.version=1.0
robocode.version=1.10.1
robot.java.source.included=true
```

### Empacotar para entrega
`Robot → Package Robot or Team` gera um `.jar` com o robô para ser entregue ao professor e importado por outros (`Robot → Import downloaded robot`).

### Torneios
- **Dentro da turma:** monte uma batalha com todos os robôs dos alunos; use **muitos rounds (35 ou mais)** para reduzir o efeito do acaso; some os pontos.
- **RoboRumble:** ranking mundial contínuo da comunidade (wiki: robowiki.net).
- **Robocode Ireland:** competição estudantil anual (a edição 2026 usa a versão 1.10.1 e um robô-sentinela, o *SentryBot*, que mata robôs que ficam parados perto das bordas).
- **Dica:** defina regras de torneio claras (versão do Robocode, tamanho da arena, número de rounds, proibido copiar robôs de exemplo sem alteração).

---

## 14. Robocode Tank Royale (a nova geração)

O **Tank Royale** é uma reescrita independente. Diferenças principais:

| | Robocode clássico | Tank Royale |
|---|---|---|
| Execução | Tudo na JVM, robôs dentro do jogo | Servidor + GUI + robôs como processos separados (WebSocket) |
| Linguagens | Java (e .NET) | Java, C#, Python, entre outras |
| Ângulos | Bússola (0° = norte, horário) | Matemática (0° = leste, anti-horário) |
| Configuração do robô | `.properties` | Arquivo `.json` |
| Pacote da API (Java) | `robocode.*` | `dev.robocode.tankroyale.botapi.*` |

### Exemplo mínimo em Java (Tank Royale)

> A API ainda evolui entre versões. Consulte a documentação oficial para ver os nomes exatos dos métodos na versão que usar.

```java
import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;

/**
 * MeuBot: o equivalente ao primeiro robô, mas para o Tank Royale.
 * Aqui a classe base é Bot (não Robot/AdvancedRobot).
 */
public class MeuBot extends Bot {

    // Ponto de entrada: o bot é um programa independente com seu próprio main()
    public static void main(String[] args) {
        new MeuBot().start();    // conecta ao servidor e entra na batalha
    }

    // O construtor lê nome, autor, versão etc. de um arquivo JSON
    MeuBot() {
        super(BotInfo.fromFile("MeuBot.json"));
    }

    @Override
    public void run() {
        // isRunning() vira false quando o round termina
        while (isRunning()) {
            forward(100);       // anda 100 unidades
            turnGunLeft(360);   // varre com o canhão
            back(100);
            turnGunLeft(360);
        }
    }

    // Equivalente ao onScannedRobot
    @Override
    public void onScannedBot(ScannedBotEvent e) {
        fire(1);
    }
}
```

**`MeuBot.json`**:
```json
{
  "name": "MeuBot",
  "version": "1.0",
  "authors": ["Nome do Aluno"],
  "description": "Meu primeiro bot no Tank Royale",
  "gameTypes": ["classic", "melee", "1v1"],
  "platform": "Java",
  "programmingLang": "Java"
}
```

> Dica de ensino: comece com o Robocode clássico (material e exemplos abundantes) e apresente o Tank Royale como "próximo passo" para alunos que queiram explorar outras linguagens ou redes.

---

## 15. Problemas comuns

| Sintoma | Causa provável / solução |
|---|---|
| Meu robô não aparece na lista | Erro de compilação; o pacote não bate com a pasta; faltou `Options → Preferences → Development Options` apontar para a pasta de saída; use *Refresh* ou reinicie. |
| `ClassNotFoundException` / robô não carrega | Nome da classe diferente do arquivo, ou classe não pública. |
| O robô gira em círculo sem parar | Não normalizou o ângulo (use `Utils.normalRelativeAngle...`). |
| Canhão erra para a direção errada | Misturou graus e radianos, ou esqueceu que o heading é medido a partir do **norte**. |
| O canhão "gira junto" com o corpo | Faltou `setAdjustGunForRobotTurn(true)`. |
| Radar perde o inimigo ao girar | Faltou `setAdjustRadarForRobotTurn(true)` / `setAdjustRadarForGunTurn(true)`. |
| `SkippedTurnEvent` frequente | Muito processamento por tick ou muito `out.println`. |
| `execute()` não é chamado e nada acontece | Em `AdvancedRobot`, `setXxx` só agenda; chame `execute()` ou uma ação bloqueante (`ahead`, `turnGunRight` etc.) em `run()`. |
| O robô fica "preso" em um evento | Evite laços longos e chamadas bloqueantes dentro dos manipuladores de evento. |
| Erro de versão do Java / segurança | Verifique nas notas da versão qual JDK é suportado. Versões antigas (1.9.x) tiveram problemas com o *Security Manager* em JDKs novos. |

---

## 16. Exercícios propostos

### Nível 1 — Iniciante (`Robot` ou `JuniorRobot`)
1. Altere o `MeuPrimeiroRobo` para andar em **quadrado** (4 vezes `ahead(200)` e `turnRight(90)`).
2. Faça o robô mudar de cor quando for atingido (`setBodyColor`).
3. Faça o robô atirar com potência **proporcional à distância** (use `e.getDistance()`).
4. Imprima no console a **energia do inimigo** a cada scan.

### Nível 2 — Intermediário (`AdvancedRobot`)
5. Crie um robô que se movimenta em **círculo** enquanto o canhão fica travado no inimigo.
6. Implemente a **trava de radar** e verifique no console quantos scans por round você recebe.
7. Use `Condition` para ativar um modo "fuga" quando a energia estiver abaixo de 20.
8. Implemente **zigue-zague aleatório** (use `Math.random()` para escolher distância e ângulo).
9. Faça o robô **evitar paredes** sem usar `onHitWall`.

### Nível 3 — Avançado
10. Implemente **mira circular** (assuma velocidade angular constante do inimigo).
11. Detecte o tiro do inimigo pela queda de energia e **calcule o tempo até a bala chegar**.
12. Crie um **robô de melee** que escolhe o alvo mais fraco ou mais próximo (use `HashMap<String, InimigoInfo>`).
13. Faça um **time** de 2 robôs: um com radar e um "droid" que ataca, compartilhando posições.
14. Grave estatísticas em arquivo (`getDataFile`) e use-as para **ajustar a potência** nos rounds seguintes.

### Projeto final (sugestão de critérios de avaliação)
| Critério | Peso |
|---|---|
| Código limpo, comentado, com métodos pequenos e nomes claros | 25% |
| Uso correto de POO (herança, encapsulamento, classes auxiliares) | 20% |
| Técnicas de movimento, mira e radar implementadas | 25% |
| Desempenho no torneio da turma | 20% |
| Relatório curto explicando a estratégia | 10% |

### Ideias de conceitos de Java a trabalhar com Robocode
- **Herança e polimorfismo:** sobrescrever `onScannedRobot`, `run`...
- **Encapsulamento:** classe `Inimigo` com posição, energia, velocidade.
- **Coleções:** `Map<String, Inimigo>` para batalhas de vários robôs.
- **Interfaces / padrão Strategy:** `EstrategiaMovimento`, `EstrategiaMira` intercambiáveis.
- **Classes internas anônimas:** `Condition`.
- **Enums:** `Modo { ATAQUE, DEFESA, FUGA }`.
- **Tratamento de exceções:** `IOException` ao salvar dados.
- **Records / classes imutáveis:** para guardar "fotografias" do inimigo em cada tick.

#### Exemplo de refatoração com Strategy (para a aula de padrões de projeto)

```java
package alunos;

import robocode.*;

/** Estratégia de potência de tiro: cada implementação é intercambiável. */
interface EstrategiaPotencia {
    double calcular(double distancia, double minhaEnergia);
}

/** Implementação: potência decresce com a distância. */
class PotenciaPorDistancia implements EstrategiaPotencia {
    @Override
    public double calcular(double distancia, double minhaEnergia) {
        double p = distancia < 200 ? 3 : distancia < 450 ? 2 : 1;
        return minhaEnergia < 15 ? Math.min(p, 0.5) : p;
    }
}

public class RoboStrategy extends AdvancedRobot {

    // Programamos para a INTERFACE, não para a implementação.
    private final EstrategiaPotencia estrategia = new PotenciaPorDistancia();

    @Override
    public void run() {
        while (true) {
            turnRadarRight(360);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double p = estrategia.calcular(e.getDistance(), getEnergy());
        fire(p);
    }
}
```

---

## 17. Cola rápida da API

### Movimento do corpo (`Robot`, bloqueantes)
| Método | Função |
|---|---|
| `ahead(d)` / `back(d)` | Anda `d` pixels |
| `turnLeft(g)` / `turnRight(g)` | Gira o corpo `g` graus |
| `stop()` / `resume()` | Pausa / retoma o movimento |

### Canhão e radar
| Método | Função |
|---|---|
| `turnGunLeft(g)` / `turnGunRight(g)` | Gira o canhão |
| `turnRadarLeft(g)` / `turnRadarRight(g)` | Gira o radar |
| `fire(p)` | Atira com potência `p` (bloqueante até o disparo) |
| `fireBullet(p)` | Atira e retorna o objeto `Bullet` |
| `scan()` | Força um novo scan |

### Versões não bloqueantes (`AdvancedRobot`)
| Método | Função |
|---|---|
| `setAhead(d)`, `setBack(d)` | Agenda movimento |
| `setTurnLeft(g)`, `setTurnRight(g)` | Agenda giro do corpo |
| `setTurnGunLeft(g)`, `setTurnGunRight(g)` | Agenda giro do canhão |
| `setTurnRadarLeft(g)`, `setTurnRadarRight(g)` | Agenda giro do radar |
| `setFire(p)`, `setFireBullet(p)` | Agenda tiro |
| `setMaxVelocity(v)`, `setMaxTurnRate(g)` | Limita velocidade / giro |
| `execute()` | Executa tudo o que foi agendado |
| `waitFor(Condition)` | Espera uma condição ficar verdadeira |
| `addCustomEvent(Condition)` | Registra um evento personalizado |
| `getDistanceRemaining()`, `getTurnRemaining()`, `getGunTurnRemaining()`, `getRadarTurnRemaining()` | Quanto falta das ações agendadas |

> Todas as versões acima têm variante em radianos (`...Radians`).

### Estado do robô
| Método | Retorna |
|---|---|
| `getX()`, `getY()` | Posição |
| `getHeading()`, `getGunHeading()`, `getRadarHeading()` | Direção do corpo / canhão / radar (graus, bússola) |
| `getVelocity()` | Velocidade (negativa = de ré) |
| `getEnergy()` | Energia |
| `getGunHeat()` | Calor do canhão (só atira com 0) |
| `getTime()` | Tick atual do round |
| `getRoundNum()`, `getNumRounds()` | Round atual / total |
| `getOthers()` | Quantos inimigos ainda vivos |
| `getBattleFieldWidth()`, `getBattleFieldHeight()` | Tamanho da arena |
| `getName()` | Nome do robô |

### Configuração
| Método | Função |
|---|---|
| `setColors(corpo, canhão, radar)` | Cores |
| `setBulletColor(c)`, `setScanColor(c)` | Cor da bala e do arco do radar |
| `setAdjustGunForRobotTurn(bool)` | Desacopla canhão e corpo |
| `setAdjustRadarForGunTurn(bool)` | Desacopla radar e canhão |
| `setAdjustRadarForRobotTurn(bool)` | Desacopla radar e corpo |

### `ScannedRobotEvent`
`getName()`, `getBearing()` (+ `Radians`), `getDistance()`, `getEnergy()`, `getHeading()` (+ `Radians`), `getVelocity()`.

### Classe `Rules` (constantes e fórmulas úteis)
`Rules.MAX_VELOCITY`, `Rules.MAX_BULLET_POWER`, `Rules.MIN_BULLET_POWER`, `Rules.GUN_TURN_RATE`, `Rules.RADAR_TURN_RATE`, `Rules.getBulletSpeed(p)`, `Rules.getBulletDamage(p)`, `Rules.getGunHeat(p)`, `Rules.getTurnRate(v)`.

### Classe `Utils` (`robocode.util`)
`normalRelativeAngle`, `normalAbsoluteAngle`, `normalRelativeAngleDegrees`, `normalAbsoluteAngleDegrees`, `isNear(a, b)`.

### Times (`TeamRobot`)
`isTeammate(nome)`, `getTeammates()`, `sendMessage(nome, obj)`, `broadcastMessage(obj)`, `onMessageReceived(MessageEvent)`.

---

## 18. Links úteis

- **Site oficial (download, documentação, Javadoc):** https://robocode.sourceforge.io
- **Código-fonte do Robocode clássico:** https://github.com/robo-code/robocode
- **Robocode Tank Royale (código e documentação):** https://github.com/robocode-dev/tank-royale
- **RoboWiki (a maior fonte de estratégias, tutoriais e artigos):** https://robowiki.net
- **Blog de notícias/versões:** https://robo-code.blogspot.com
- **Competição Robocode Ireland (estudantes):** https://tus.ie/gamesfleadh/robocode/

### Sugestão de roteiro de curso (4 aulas)
1. **Aula 1:** instalação, interface, Exemplo 1 e 7, exercícios do nível 1.
2. **Aula 2:** física, coordenadas, `AdvancedRobot` e `setXxx`/`execute` (Exemplos 2 e 3).
3. **Aula 3:** eventos, `Condition`, mira linear, depuração (Exemplos 4, 5 e 9).
4. **Aula 4:** organização em classes, Strategy, times e **torneio final** (Exemplos 6 e 8).

*Bons estudos e boas batalhas! 🤖💥*
