# 💡 Configuração do Raspberry Pi Pico W com Arduino IDE e DHT11

Este guia explica como configurar o Raspberry Pi Pico W com o Arduino IDE, instalar as bibliotecas necessárias e ligar o sensor DHT11 para recolha e envio de dados de temperatura e humidade.

---

## 🧩 1. Instalar o Arduino IDE

### Pré-requisitos:

1. Ter o Arduino IDE instalado (Windows, macOS ou Linux)
2. Cabo USB para ligação ao Pico W

### Passos:

1. Abra o Arduino IDE.
2. Vá a File → Preferences.
3. No campo Additional Boards Manager URLs, adicione:

```
https://github.com/earlephilhower/arduino-pico/releases/download/global/package_rp2040_index.json
```

4. Clique OK e depois vá a Tools → Board → Boards Manager....
5. Procure por “pico” e instale Raspberry Pi Pico/RP2040.
6. Em Tools → Board, selecione Raspberry Pi Pico W.

---

## ⚙️ 2. Ligar o Pico W em modo BOOTLOADER

1. Mantenha premido o botão BOOTSEL no Pico W.
2. Ligue-o via USB ao computador enquanto segura o botão.

Após o primeiro upload, o modo BOOTSEL será reconhecido automaticamente.

---

## 💻 3. Testar o LED integrado

1. Crie um novo Sketch no Arduino IDE com o seguinte código:

```
void setup() {
pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
digitalWrite(LED_BUILTIN, HIGH);
delay(1000);
digitalWrite(LED_BUILTIN, LOW);
delay(1000);
}
```

2.Selecione a porta correta em Tools → Port e clique em Upload.
O LED deverá piscar a cada segundo ✅.

---

## 📡 4. Instalar bibliotecas necessárias

1. No Arduino IDE:

   * Vá a Sketch → Include Library → Manage Libraries...
   * Procure e instale:

     * DHT sensor library (by Adafruit)
     * arduino-timer (by Michael Contreras)

---

## 🔌 5. Montagem do circuito com DHT11

 ### Conexões:

* (Esquerda para a direita) 
  * GND	GND
  * DOUT	GPIO 1
  * VCC 3.3V

---

## 💾 6. Código completo do sensor
```
#include "DHT.h"
#include <arduino-timer.h>

#define DHTPIN 16
#define DHTTYPE DHT11
#define TOGGLE_LED_PERIOD 1000
#define SOIL_HUMIDITY_PERIOD 5000
#define ATMOSPHERIC_HUMIDITY_PERIOD 2000
#define ATMOSPHERIC_TEMPERATURE_PERIOD 2000

DHT dht(DHTPIN, DHTTYPE);
auto timer = timer_create_default();

bool toggle_led(void *) {
digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));
return true;
}

void send_data(int id, const char* type, float value, const char* unit) {
Serial.printf("sensor_id:%d#type:%s#value:%.2f#unit:%s#time:%lu\n", id, type, value, unit, millis());
}

bool read_dht(void *) {
float humidity = dht.readHumidity();
float temperature = dht.readTemperature();
if (!isnan(humidity)) send_data(1, "humidity", humidity, "percent");
if (!isnan(temperature)) send_data(2, "temperature", temperature, "celsius");
return true;
}

void setup() {
pinMode(LED_BUILTIN, OUTPUT);
Serial.begin(9600);
dht.begin();
timer.every(TOGGLE_LED_PERIOD, toggle_led);
timer.every(ATMOSPHERIC_HUMIDITY_PERIOD, read_dht);
}

void loop() {
timer.tick();
}
```

Após upload, abra o Serial Monitor a 9600 baud para ver os dados enviados.

---

## 🖥️ 7. Receber dados via terminal (AntiX ou WSL)

No terminal:

* sudo dmesg | grep tty
  * password é guest
  
* cat /dev/ttyXXX (ler o que aparece no grep acima)


Deverá ver mensagens como:
```
sensor_id:2#type:temperature#value:22.50#unit:celsius#time:10230
```

---

### ✅ 8. Verificação final
1. Verificação	
2. LED pisca
3. Serial Monitor exibe dados
4. Dados formatados corretamente
5. Sensor DHT11 responde com valores reais

---

### 📊 Done Concept
* Pico W configurado e funcional com Arduino IDE
* DHT11 operacional e enviando dados formatados
* Comunicação serial validada no monitor ou terminal

---

### Autor

Diogo Remelgado


