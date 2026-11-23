# Java Games 🎮

Repositório público do **Professor George Mendes Marra** sobre como criar jogos em Java.

Aqui você encontra exemplos e exercícios práticos para aprender programação em Java de forma divertida, começando com um jogo completo feito só com o que já vem no Java (**Caçador de Moedas**) e seguindo para o **Robocode**.

🌐 Site do professor: [georgemendesmarra.eti.br](https://georgemendesmarra.eti.br/)

## 📁 Estrutura do repositório

```
java_games/
├── cacador_de_moedas/                  # Jogo completo em Java Swing
│   └── CacadorDeMoedas.java
├── robocode/                           # Material e robôs para o Robocode
│   ├── robocode.md
│   └── robocode_robos_exemplo.md
├── projeto_para_criar_jogos_em_java.md # Guia passo a passo para iniciantes
├── LICENSE                             # Licença BSD 3-Clause
└── README.md
```

## 📘 Guia para iniciantes

O arquivo [`projeto_para_criar_jogos_em_java.md`](./projeto_para_criar_jogos_em_java.md) é um material de apoio para quem já conhece o básico de Java (variáveis, métodos, classes, `if`, `for`), mas **nunca criou um jogo**. Ele explica, passo a passo:

- O que é um jogo para o computador (estado, regras e desenho)
- Como criar a janela e desenhar na tela
- O *game loop* (o laço que faz as coisas se moverem)
- Como ler o teclado
- Como detectar colisões
- O jogo completo **Caçador de Moedas**, além de boas práticas, desafios, erros comuns e glossário

## 🪙 Caçador de Moedas

Pasta: [`cacador_de_moedas`](./cacador_de_moedas)

Jogo simples feito com **Java Swing**: mova o quadrado ciano com as **setas do teclado** e pegue o máximo de moedas amarelas em **30 segundos**. Quando o tempo acaba, aparece a pontuação final e você pode jogar de novo apertando **ENTER**.

Conceitos praticados:

| Conceito | Onde aparece no código |
|----------|------------------------|
| `JPanel` e `Graphics` | Área de desenho e renderização do jogo |
| `Timer` | *Game loop* a 60 quadros por segundo |
| `KeyListener` | Leitura do teclado, com movimento suave e diagonal |
| `Rectangle.intersects()` | Colisão entre jogador e moeda |
| Constantes (`static final`) | Ajuste fácil de tamanho, velocidade e duração |
| `Random` | Posição aleatória das moedas |

O código é todo comentado em português, para ser lido como material de estudo.

## 🤖 Robocode

[Robocode](https://robocode.sourceforge.io/) é um jogo de programação em que você escreve, em Java, o código de um tanque de guerra virtual. Os robôs batalham entre si de forma totalmente autônoma, e vence quem tiver a melhor estratégia de movimento, mira e disparo.

É uma ótima forma de praticar:

- Classes, objetos e herança
- Estruturas condicionais e laços de repetição
- Lógica e tomada de decisão
- Eventos e métodos *callback*

A pasta [`robocode`](./robocode) contém:

- [`robocode.md`](./robocode/robocode.md): material de estudo sobre o Robocode
- [`robocode_robos_exemplo.md`](./robocode/robocode_robos_exemplo.md): robôs de exemplo comentados

## ✅ Pré-requisitos

- **JDK** (Java Development Kit) instalado, versão 8 ou superior para o Caçador de Moedas. Para o Robocode, consulte a versão exigida pela versão que você utiliza.
- **Robocode** instalado (somente para a parte de robôs): [baixe aqui](https://robocode.sourceforge.io/)
- **Git** (opcional, para clonar o repositório)

Para verificar se o Java está instalado:

```bash
java -version
```

## 🚀 Como usar

1. Clone o repositório:

```bash
git clone https://github.com/GeorgeMendesMarra/java_games.git
cd java_games
```

### Jogando o Caçador de Moedas

2. Entre na pasta, compile e execute:

```bash
cd cacador_de_moedas
javac CacadorDeMoedas.java
java CacadorDeMoedas
```

> No Java 11 ou superior, também é possível executar direto: `java CacadorDeMoedas.java`

3. Use as **setas** para mover o jogador e **ENTER** para jogar de novo ao fim da partida. Se preferir, abra o arquivo na sua IDE e execute o método `main`.

### Usando os robôs do Robocode

2. Siga o material em [`robocode/robocode.md`](./robocode/robocode.md) e copie os robôs de exemplo para o diretório de robôs da sua instalação do Robocode (ou importe o projeto na sua IDE e aponte a pasta de robôs do Robocode).

3. Abra o Robocode, clique em **Battle → New** e selecione os robôs para a batalha.

4. Inicie a batalha e observe seu robô em ação! 💥

## 🛠️ Tecnologias

- Java
- Java Swing / AWT (Caçador de Moedas)
- Robocode

## 🤝 Contribuindo

Sugestões, correções e novos exemplos são bem-vindos! Abra uma *issue* ou envie um *pull request*.

## 👤 Autor

**George Mendes Marra**
GitHub: [@GeorgeMendesMarra](https://github.com/GeorgeMendesMarra)
Site: [georgemendesmarra.eti.br](https://georgemendesmarra.eti.br/)

## 📄 Licença

Este projeto está licenciado sob a licença **BSD 3-Clause**. Veja o arquivo [LICENSE](./LICENSE) para mais detalhes.
