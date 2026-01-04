# USAC15 - Synoptic Board Display Component

## Overview

The Board component is designed to display railway system information in a visually appealing synoptic board format. It receives data from the Manager component and presents it using colorful ASCII art, icons, symbols, boxes, and lines to grab the user's attention.

## Quick Start Guide (Passo a Passo)

### Passo 0: Encontrar o diretório compartilhado (UTM/VM)

Se estiver usando UTM ou outra máquina virtual, o diretório compartilhado pode estar em:

**Procurar onde está montado:**
```bash
# Verificar todos os pontos de montagem
mount | grep -i share

# Ou listar diretórios comuns
ls /media/
ls /mnt/
ls /media/share/
ls /mnt/share/
```

**Localizações comuns no UTM:**
- `/media/share/`
- `/mnt/share/`
- `/media/<seu-usuario>/share/`
- `/run/user/1000/share/`

**Depois de encontrar, navegue até o projeto:**
```bash
# Exemplo se estiver em /media/share
cd /media/share/IdeaProjects/sem3-pi-25-26-g113-repo/asm/sprint3/usac15

# Ou em /mnt/share
cd /mnt/share/IdeaProjects/sem3-pi-25-26-g113-repo/asm/sprint3/usac15
```

**Se não encontrar o shared directory:**
1. Verifique se o shared directory está configurado no UTM (Configurações da VM > Sharing)
2. Monte manualmente se necessário:
```bash
sudo mkdir -p /mnt/share
sudo mount -t 9p -o trans=virtio share /mnt/share
```

### Passo 1: Navegar até o diretório
```bash
# Ajuste o caminho de acordo com onde encontrou o shared directory
cd /media/share/IdeaProjects/sem3-pi-25-26-g113-repo/asm/sprint3/usac15
```

**OU use o comando find para localizar:**
```bash
find /media -name "usac15" 2>/dev/null
find /mnt -name "usac15" 2>/dev/null
```

### Passo 2: Compilar o programa

**IMPORTANTE: Verifique se está no diretório correto primeiro!**
```bash
# Verificar o diretório atual
pwd

# Deve mostrar algo como:
# /media/share/.../asm/sprint3/usac15
# ou /mnt/share/.../asm/sprint3/usac15

# Listar arquivos para confirmar
ls
# Deve ver: board.c board.h manager.c ui.c ui.h sensors_linux.c Makefile README.md
```

**Se não estiver no diretório correto, navegue primeiro (Passo 1)!**

Agora compile:
```bash
make clean    # Limpar arquivos anteriores (opcional)
make          # Compilar todos os arquivos
```

Se a compilação for bem-sucedida, verá:
```
gcc -Wall -Wextra -Werror -g -std=c11 -c manager.c
gcc -Wall -Wextra -Werror -g -std=c11 -c board.c
gcc -Wall -Wextra -Werror -g -std=c11 -c ui.c
gcc -Wall -Wextra -Werror -g -std=c11 -c sensors_linux.c
gcc -Wall -Wextra -Werror -g -std=c11 -o manager manager.o board.o ui.o sensors_linux.o -lm
```

### Passo 3: Executar o programa
```bash
./manager
```

Ou use o atalho:
```bash
make run
```

### Passo 4: Fazer login
Quando o programa iniciar, será solicitado um nome de usuário:
```
Enter username: operator1
```
Digite qualquer nome e pressione ENTER.

### Passo 5: Usar os comandos
Após o login, você verá o menu principal:
```
┌────────────────────────────────────────────────────────────────────┐
│  Press [S] Show Env | [L] Control Lights | [E] Exit             │
└────────────────────────────────────────────────────────────────────┘

Enter command:
```

**Comandos disponíveis:**
- **S** ou **SHOW** - Mostrar dados ambientais (temperatura e humidade)
- **L** ou **LIGHTS** - Controlar luzes de sinais ferroviários
- **E** ou **EXIT** - Sair do programa

#### Exemplo de uso do comando S (Mostrar Ambiente):
```
Enter command: S
```
Isso exibirá:
- Temperatura atual
- Humidade atual
- Gráficos de barras coloridos
- Estatísticas (médias, total de leituras)

#### Exemplo de uso do comando L (Controlar Luzes):
```
Enter command: L
  Enter track number (1-8): 3
  Enter light color (RE/YE/GE/RB): GE
```
Onde:
- **RE** = Red (Vermelho) - PARAR
- **YE** = Yellow (Amarelo) - CAUTELA
- **GE** = Green (Verde) - AVANÇAR
- **RB** = Blue (Azul) - MANUTENÇÃO

### Passo 6: Sair do programa
```
Enter command: E
```

### Modo Simulação vs Hardware

**Modo Simulação (Padrão):**
- O programa gera dados aleatórios de sensores
- Não precisa de hardware conectado
- Ideal para testes e desenvolvimento

**Modo Hardware:**
- Conecta a sensores reais via porta serial `/dev/ttyUSB0` ou `/dev/ttyACM0`
- Se o hardware não estiver disponível, volta automaticamente ao modo simulação

## Configuração de Hardware (Raspberry Pi Pico W)

### Passo 1: Encontrar o Pico W

**Antes de conectar o Pico, veja os dispositivos atuais:**
```bash
ls /dev/ttyUSB* 2>/dev/null
ls /dev/ttyACM* 2>/dev/null
```

**Se não aparecer nada, é normal - não há dispositivos USB seriais conectados ainda.**

**Conecte o Pico W via USB à sua máquina (Mac)**

**IMPORTANTE para UTM/VM:** 
- **PASSO CRÍTICO:** No UTM, clique no menu superior **Devices > USB**
- Procure por **Raspberry Pi Pico** ou **RP2040** ou **USB Serial Device**
- **Clique para conectar** à VM
- Se não aparecer nada no menu USB, o Pico pode não estar conectado ao Mac ou pode estar em modo bootloader

**Depois de conectar à VM via menu USB, liste novamente:**
```bash
ls /dev/ttyUSB* 2>/dev/null
ls /dev/ttyACM* 2>/dev/null
```

**Se AINDA não aparecer nada:**
```bash
# Ver TODOS os dispositivos tty
ls -la /dev/tty*

# Ver últimas 30 linhas do log do sistema
dmesg | tail -30

# Ver dispositivos USB no sistema
lsusb

# Verificar se existem dispositivos seriais
ls -la /dev/serial/by-id/ 2>/dev/null
```

**Procure por:**
- `/dev/ttyACM0` (comum para Pico)
- `/dev/ttyUSB0` (algumas configurações)
- Mensagens tipo: `ttyACM0: USB ACM device`

### Passo 2: Verificar permissões

```bash
# Ver permissões da porta (substitua ttyACM0 pela sua porta)
ls -l /dev/ttyACM0

# Dar permissões (temporário - até reiniciar)
sudo chmod 666 /dev/ttyACM0

# OU adicionar ao grupo dialout (permanente)
sudo usermod -a -G dialout $USER
# Depois faça logout/login
```

### Passo 3: Testar comunicação

```bash
# Instalar ferramentas se necessário
sudo apt-get install minicom screen

# Testar com screen (substitua ttyACM0 pela sua porta)
screen /dev/ttyACM0 9600

# Para sair do screen: Ctrl+A, depois K, depois Y
```

### Passo 4: Configurar o código (se necessário)

Se sua porta não for `/dev/ttyUSB0`, edite [manager.c](manager.c):

Linha ~167, mude de:
```c
serial_fd = open_serial_port("/dev/ttyUSB0");
```

Para:
```c
serial_fd = open_serial_port("/dev/ttyACM0");  // ou sua porta
```

Depois recompile:
```bash
make clean
make
```

### Passo 5: Executar com hardware

```bash
./manager
```
Nada aparece ao executar ls /dev/ttyUSB* ou ls /dev/ttyACM*

**Causa:** O Pico não foi passado para a VM pelo UTM

**Solução passo a passo:**

a) Verifique se o Pico está conectado ao Mac:
   - No Mac, abra "Informações do Sistema" (ícone Apple > Sobre este Mac > Mais Informações)
   - Vá em "USB"
   - Procure por "Raspberry Pi Pico" ou "RP2040"
   
b) No UTM, com a VM ligada:
   - Menu superior: **Devices > USB**
   - Deve aparecer o Pico na lista
   - **Clique no nome do Pico** para conectar à VM
   
c) Na VM, execute novamente:
```bash
ls /dev/ttyACM* 2>/dev/null
dmesg | tail -20
```

### 2. Pico não aparece no menu Devices > USB do UTM

**Possíveis causas:**
- Pico em modo BOOTSEL (mass storage mode)
- Cabo USB só carrega (não transmite dados)
- Pico não tem firmware instalado

**Soluções:**

a) **Verificar modo do Pico:**
   - Se o Pico aparecer como disco "RPI-RP2" no Mac → está em modo BOOTSEL
   - Precisa instalar firmware primeiro
   
b) **Testar cabo USB:**
   - Tente outro cabo USB
   - Certifique-se que não é um cabo "só carga"

c) **Instalar firmware básico no Pico:**
   - Desconecte o Pico
   - Segure o botão BOOTSEL no Pico
   - Conecte ao Mac (ainda segurando BOOTSEL)
   - Aparecerá como disco "RPI-RP2"
   - Copie um firmware .uf2 para este disco
   - Pico reiniciará com o firmware instalado

### 3. Para este projeto funcionar com hardware real

O Pico precisa ter um firmware que:
- Responda ao comando `READ\n` pela serial
- Envie dados no formato `T:25.5,H:60.2\n`
- Use baudrate 9600

**Por enquanto, use o MODO SIMULAÇÃO** que já está funcionando!

O programa detecta automaticamente se não há hardware e usa simulação. ID 2e8a:0005 Raspberry Pi Pico`

### 4. Monitorar mensagens do sistema ao conectar
```bash
# Deixe isto a correr e conecte o Pico
dmesg -w
```

### 5. UTM - Configurar USB passthrough
1. Desligue a VM
2. Configurações da VM > **Devices**
3. Adicione **USB** device
4. Inicie a VM
5. Menu **Devices > USB** e selecione o Pico

## Troubleshooting (Resolução de Problemas)

### Erro de compilação
Se encontrar erros ao compilar:
```bash
make clean
make
```

### Permissões de porta serial (se usar hardware)
```bash
sudo chmod 666 /dev/ttyUSB0
```

### Terminal não mostra cores
Certifique-se de que está usando um terminal moderno que suporta cores ANSI (Terminal.app no macOS, GNOME Terminal no Linux, etc.)

## Features

### Visual Elements
- **Colorful Display**: Uses ANSI color codes for vibrant terminal output
- **Text Icons & Labels**: Clear text-based indicators for intuitive information representation
  - [TEMP] Temperature indicators
  - [HUM] Humidity indicators
  - [LIGHT] Traffic light signals
  - [TRAIN] Train symbols
  - [OK] Success indicators
  - [ERR] Error indicators
  - [WARN] Warning symbols

### Display Components

#### 1. **Environmental Status Display**
- Real-time temperature and humidity readings
- Visual progress bars with color coding:
  - **Blue**: Cold temperatures (< 15°C)
  - **Green**: Normal temperatures (15-25°C)
  - **Yellow**: Warm temperatures (25-30°C)
  - **Red**: Hot temperatures (> 30°C)
- Statistical information:
  - Total readings count
  - Average temperature
  - Average humidity

#### 2. **Track Light Signals Display**
- Current light signal status per track
- Color-coded light indicators:
  - **Red Light**: STOP
  - **Yellow Light**: CAUTION
  - **Green Light**: GO
  - **Blue Light**: MAINTENANCE
- Signal usage statistics

#### 3. **System Status Messages**
- Success/error message display
- Color-coded status boxes
- Informational notifications

## Architecture

### Files Structure

```
usac15/
├── board.h           # Board component header (data structures & function declarations)
├── board.c           # Board display implementation with fancy formatting
├── manager.c         # Main manager with integrated board display
├── ui.h              # User interface header
├── ui.c              # User interface implementation
├── sensors_linux.c   # Sensor simulation and data acquisition
├── Makefile          # Build configuration
└── README.md         # This file
```

### Component Interaction Flow

```
┌─────────────┐
│   Manager   │ ◄── Receives sensor data
└──────┬──────┘
       │
       ├──► ┌──────────┐
       │    │  Sensors │ (Environmental data)
       │    └──────────┘
       │
       ├──► ┌──────────┐
       │    │   Board  │ (Display component)
       │    └──────────┘
       │
       └──► ┌──────────┐
            │    UI    │ (User interaction)
            └──────────┘
```

## Building and Running

### Compilation

```bash
make
```

This will compile all source files and create the `manager` executable.

### Running the Program

```bash
make run
```

Or directly:

```bash
./manager
```

### Cleaning Build Files

```bash
make clean
```

## Usage

### Login
When the program starts, you'll be prompted to enter a username.

### Commands

The system supports the following commands:

- **S** or **SHOW**: Display environmental sensor data
- **L** or **LIGHTS**: Control track light signals
  - Prompts for track number (1-8)
  - Prompts for light color (RE/YE/GE/RB)
- **E** or **EXIT**: Exit the program

### Example Session

```
╔════════════════════════════════════════════════════════════════════╗
║              RAILWAY SYNOPTIC BOARD SYSTEM                    ║
╚════════════════════════════════════════════════════════════════════╝

╔════════════════════════════════════════════════════════════════════╗
║                           USER LOGIN                              ║
╚════════════════════════════════════════════════════════════════════╝

Enter username: operator1

Welcome, operator1!

┌────────────────────────────────────────────────────────────────────┐
│ Time: 14:30:25                      Railway Control Center   │
└────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────┐
│  Press [S] Show Env | [L] Control Lights | [E] Exit             │
└────────────────────────────────────────────────────────────────────┘

Enter command: S

╔════════════════════════════════════════════════════════════════════╗
║                     ENVIRONMENTAL STATUS                          ║
╠════════════════════════════════════════════════════════════════════╣
║  Temperature:  23.5°C  [████████████░░░░░░░░]  ║
║  Humidity:     55.2%   [███████████░░░░░░░░░]  ║
║                                                                    ║
║  Statistics:                                                      ║
║     • Total Readings: 42                                         ║
║     • Average Temp:   23.1°C                                      ║
║     • Average Hum:    54.8%                                       ║
╚════════════════════════════════════════════════════════════════════╝
```

## Operation Modes

### 1. Simulation Mode (Default)
- Runs without hardware connection
- Generates realistic random sensor data
- Simulates light signal commands
- Perfect for development and testing

### 2. Hardware Mode
- Connects to real sensors via serial port (`/dev/ttyUSB0`)
- Reads actual temperature and humidity data
- Sends real light control commands
- Automatically falls back to simulation if hardware unavailable

## Data Processing

### Moving Median Filter
The system implements a moving median filter with a buffer size of 5 readings to:
- Reduce noise in sensor data
- Eliminate outliers
- Provide more stable readings

### Statistics Tracking
The board maintains running statistics:
- **Environmental Data**: Running averages for temperature and humidity
- **Light Signals**: Count of each signal type (red, yellow, green, blue)
- **System Activity**: Total commands and operations

## Logging

All user actions are logged to `manager.log` with the following format:
```
user=<username> action=<action_type> [additional_params]
```

Example:
```
user=operator1 action=SHOW_ENV
user=operator1 action=CONTROL_LIGHTS track=3 color=GE
user=operator1 action=EXIT
```

## Display Features

### Color Scheme
- **Cyan**: Headers and borders
- **Green**: Success messages, normal environmental readings
- **Yellow**: Warnings, warm temperatures, yellow lights
- **Red**: Errors, stop signals, hot temperatures
- **Blue**: Information, cold temperatures, maintenance lights
- **Magenta**: Light signal displays
- **White**: Normal text

### Visual Elements
- **Box Drawing Characters**: ╔═╗║╚╝┌─┐│└┘
- **Progress Bars**: █░ characters for visual data representation
- **Text Labels**: [TEMP], [HUM], [OK], [ERR], [WARN], etc.
- **Emojis**: UTF-8 emojis for intuitive iconography

## Terminal Requirements

For best display results, ensure your terminal supports:
- ANSI color codes
- UTF-8 encoding (for box-drawing characters)
- At least 80 columns width
- Unicode box-drawing characters

### Recommended Terminals
- **Linux**: GNOME Terminal, Konsole, xterm (with UTF-8)
- **macOS**: Terminal.app, iTerm2
- **Windows**: Windows Terminal, WSL terminal

## Troubleshooting

### Colors Not Displaying
- Ensure your terminal supports ANSI color codes
- Check terminal settings for color support

### Box-Drawing Characters Not Displaying
- Verify your terminal supports UTF-8 encoding
- Install fonts that include Unicode box-drawing characters

### Serial Port Access
If running in hardware mode and getting permission errors:
```bash
sudo chmod 666 /dev/ttyUSB0
```

Or add your user to the `dialout` group:
```bash
sudo usermod -a -G dialout $USER
```
(Requires logout/login to take effect)

## Future Enhancements

Potential improvements for future versions:
- Network-based data transmission
- Web-based board display
- Multi-track simultaneous display
- Historical data graphs
- Alert notifications
- Remote monitoring capabilities

## Technical Specifications

- **Language**: C11
- **Compiler**: GCC
- **Platform**: Linux (tested), macOS compatible
- **Dependencies**: Standard C library, POSIX APIs
- **Communication**: Serial port (optional)
- **Display**: Terminal-based with ANSI/UTF-8
