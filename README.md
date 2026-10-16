# 🎯 Cannon Game AM

> Aplicação mobile desenvolvida para a disciplina de Desenvolvimento para Dispositivos Móveis na **Universidade do Estado de Minas Gerais (UEMG)**, baseada no Capítulo 6 do livro *"Android 6 for Programmers: An App-Driven Approach"* (Deitel & Deitel) e aprimorada com uma temática inspirada em **Batalhas Pokémon**.

---

## 👥 Integrantes (Autores)

* **Mel Borges Martins**
* **Arthur Miguel Gonzaga da Fonseca**

**Instituição:** Universidade do Estado de Minas Gerais (UEMG)  
**Disciplina:** Desenvolvimento para Dispositivos Móveis  

---

## 🎯 Objetivo do Projeto

O objetivo principal deste projeto é consolidar e aplicar os conceitos fundamentais de desenvolvimento Android apresentados no **Capítulo 6 (Cannon Game App)** do livro *"Android 6 for Programmers: An App-Driven Approach"*.

A aplicação demonstra a construção de um jogo 2D interativo utilizando renderização gráfica em tempo real com `SurfaceView`, manipulação de `Canvas` e `Paint`, controle de thread dedicada para o loop do jogo (*Game Loop Thread*), execução de efeitos sonoros com `SoundPool`, detecção de colisões físicas, persistência de dados com `SharedPreferences`, suporte dinâmico a orientações (Paisagem e Retrato) e arquitetura modular de interface com Fragments.

---

## ✨ Funcionalidades e Destaques Técnicos

### 🎮 Mecânicas de Jogo e Temática Pokémon
* **Visual Inspirado em Batalhas Pokémon:**
  * **Lançador de Pokébolas (Canhão):** Estilizado como uma *Great Ball* metálica.
  * **Projétil (Bola de Canhão):** Renderizado como uma *Pokébola* clássica (vermelha e branca com botão central).
  * **Alvos (Targets):** Representados por Pokémon Selvagens (Pikachu, Zubat, Gastly, Oddish) desenhados via renderização vetorial no Canvas.
  * **Escudo Bloqueador:** Estilizado como um *Escudo de Energia da Equipe Rocket* com borda neon e o ícone "R".
  * **Cenário de Batalha:** Arena retro pixel-art com estampa e padrão Pokébola.

### ⚙️ Arquitetura Técnica e APIs Android
* **Controle de Loop do Jogo (*Game Loop Thread*):** Thread dedicada (`CannonThread`) em `CannonView` que executa atualização de posições, verificação de colisões e renderização a 60 FPS.
* **Renderização Gráfica com `SurfaceView` e `SurfaceHolder`:** Renderização de alto desempenho para jogos 2D sem bloquear a thread principal de interface.
* **Gráficos 2D Personalizados com `Canvas` e `Paint`:** Desenho vetorial de geometrias, arcos, trajetórias, rotações matriciais e HUD do jogo.
* **Áudio com `SoundPool` e `AudioAttributes`:** Gerenciamento de efeitos sonoros com suporte a múltiplos canais simultâneos (disparo, acerto no alvo e ricochete no escudo) e opção de ativar/desativar o som.
* **Persistência de Dados com `SharedPreferences`:** Cálculo de pontuação final e armazenamento persistente do recorde de pontuação (*High Score*) na chave `"CannonGamePrefs"`.
* **Suporte Dinâmico a Orientações (Paisagem e Retrato):**
  * **Modo Paisagem (Landscape):** Lançador posicionado na esquerda disparando horizontalmente (esquerda para direita), com alvos e escudo movendo-se verticalmente.
  * **Modo Retrato (Portrait):** Lançador posicionado no topo central disparando verticalmente (topo para baixo), com alvos e escudo movendo-se horizontalmente e cronômetro rotacionado $-90^\circ$ na margem esquerda.
* **Modo Imersivo (Fullscreen):** Ocultação automática das barras do sistema para máxima área de jogo.
* **Arquitetura de Navegação com Fragments:** Telas modulares gerenciadas via `FragmentManager` (`MenuFragment`, `MainActivityFragment`, `HowToPlayFragment`, `HighScoresFragment`).
* **Diálogo de Fim de Jogo Customizado:** Diálogo estilizado de caixa de texto Pokémon com estatísticas da partida (tiros disparados, tempo de batalha, pontuação atual, melhor pontuação e alerta de novo recorde) e botões para "Jogar Novamente" ou "Voltar ao Menu".

---

## 🕹️ Regras e Pontuação do Jogo

| Elemento | Mecânica / Regra |
| :--- | :--- |
| 🎯 **Objetivo** | Destruir todos os **9 alvos** antes que o tempo esgote. |
| ⏱️ **Cronômetro Inicial** | Inicia em **10 segundos**. |
| 🎯 **Acerto no Alvo** | Concede **+500 pontos** e adiciona **+3 segundos** ao cronômetro. |
| 🛡️ **Colisão no Escudo** | Ricocheteia a Pokébola, reduz **-2 segundos** e toca o som do escudo. |
| 🚀 **Tiros Disparados** | Cada lançamento deduz **-10 pontos** do placar final. |
| 🏆 **Fórmula da Pontuação** | $\text{Pontuação} = (\text{alvosAtingidos} \times 500) + (\text{tempoRestante} \times 100) - (\text{tirosDisparados} \times 10)$ |

---

## 📸 Demonstração e Capturas de Tela

> *Abaixo estão os espaços reservados para adição das capturas de tela e GIFs da aplicação.*

| Menu Principal | Tela de Jogo (Paisagem) | Tela de Jogo (Retrato) |
| :---: | :---: | :---: |
| ![Menu Placeholder](docs/screenshots/menu.png) | ![Game Landscape Placeholder](docs/screenshots/game_landscape.png) | ![Game Portrait Placeholder](docs/screenshots/game_portrait.png) |

| Diálogo de Fim de Jogo | Regras da Batalha | Melhor Pontuação |
| :---: | :---: | :---: |
| ![Game Over Placeholder](docs/screenshots/game_over.png) | ![How To Play Placeholder](docs/screenshots/how_to_play.png) | ![High Score Placeholder](docs/screenshots/high_score.png) |

---

## 🚀 Como Executar a Aplicação

### Pré-requisitos
* **Android Studio** (versão Ladybug 2024.2.1 ou superior).
* **JDK 17** configurado no Android Studio.
* Dispositivo físico Android rodando **Android 5.0 (API 21)** ou superior OU um Emulador Android (AVD).

### Passo a Passo

1. **Clonar o Repositório:**
   ```bash
   git clone https://github.com/mmbmeel/Cannon_Game_AM.git
   cd Cannon_Game_AM
   ```

2. **Abrir no Android Studio:**
   * Abra o **Android Studio**.
   * Selecione **Open an Existing Project**.
   * Escolha a pasta do projeto `Cannon_Game_AM`.

3. **Sincronizar e Compilar:**
   * Aguarde a sincronização automática do Gradle (*Gradle Sync*).
   * Verifique se o módulo selecionado na barra superior é `:app`.

4. **Executar:**
   * Conecte seu dispositivo Android via USB (com a Depuração USB ativada) ou inicie um Emulador (AVD).
   * Clique no botão **Run** ▶ (`Shift + F10`).

---

## 📜 Licença e Referências

* **Livro Texto:** DEITEL, Paul; DEITEL, Harvey. *Android 6 for Programmers: An App-Driven Approach*. 3. ed. Pearson Education, 2015. Capítulo 6: Cannon Game App.
* **Instituição:** Universidade do Estado de Minas Gerais (UEMG).
* **Finalidade:** Trabalho prático avaliativo da disciplina de Desenvolvimento para Dispositivos Móveis.
