#include <DHT.h>

#define DHTPIN 1
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

// LEDs
const int ledVermelho = 16;
const int ledAmarelo   = 17;
const int ledVerde     = 18;

// LOGIN
bool logged_in = false;
String username = "";

// SENSOR DATA STRUCT
typedef struct {
  float temperature;
  float humidity;
  bool error;
} SensorData;

#define BUFFER_SIZE 5
SensorData buffer[BUFFER_SIZE];
int indexBuffer = 0;

// ---------- ASSEMBLY DUMMY (struct como parâmetro) ----------
extern "C" SensorData asmStruct(SensorData* data) {
  // Função assembly mínima, apenas retorna o primeiro elemento
  asm volatile(
    "" // nenhuma instrução de ponto flutuante
  );
  return *data;
}

// ---------- FUNÇÃO DE MEDIANA EM C ----------
SensorData movingMedian(SensorData* buf, int size) {
  SensorData result;
  result.error = false;
  result.temperature = 0.0;
  result.humidity = 0.0;

  float temps[BUFFER_SIZE];
  int count = 0;

  // Copia valores e verifica erro
  for (int i = 0; i < size; i++) {
    temps[i] = buf[i].temperature;
    result.humidity += buf[i].humidity;
    if (buf[i].error) result.error = true;
    count++;
  }
  if (count == 0) return result;

  // Bubble sort
  for (int i = 0; i < count - 1; i++) {
    for (int j = 0; j < count - i - 1; j++) {
      if (temps[j] > temps[j + 1]) {
        float tmp = temps[j];
        temps[j] = temps[j + 1];
        temps[j + 1] = tmp;
      }
    }
  }

  result.temperature = temps[count / 2]; // mediana
  result.humidity /= count; // média
  return result;
}

// ---------- SETUP ----------
void setup() {
  Serial.begin(115200);
  delay(500);

  dht.begin();

  pinMode(ledVermelho, OUTPUT);
  pinMode(ledAmarelo, OUTPUT);
  pinMode(ledVerde, OUTPUT);

  Serial.println("=== Sistema de Estacao ===");
  Serial.println("Digite seu username para logar:");
}

// ---------- MENU ----------
void printMenu() {
  Serial.println("\n=== MENU PRINCIPAL ===");
  Serial.println("1 - Ler sensores");
  Serial.println("0 - Logout");
  Serial.print("Opção: ");
}

// ---------- LOOP ----------
void loop() {
  if (Serial.available()) {
    String input = Serial.readStringUntil('\n');
    input.trim();

    // LOGIN
    if (!logged_in) {
      if (input.length() > 0) {
        username = input;
        logged_in = true;
        Serial.print("Bem-vindo, ");
        Serial.println(username);
        printMenu();
      } else {
        Serial.println("Username invalido. Digite novamente:");
      }
      return;
    }

    // MENU
    if (input == "1") {
      Serial.println("=== LEITURA DE SENSORES ===");
      Serial.println("Digite '0' a qualquer momento para voltar ao menu.");

      while (true) {
        // Sair se usuário digitar 0
        if (Serial.available()) {
          String cmd = Serial.readStringUntil('\n');
          cmd.trim();
          if (cmd == "0") {
            Serial.println("Saindo da leitura de sensores...");
            printMenu();
            break;
          }
        }

        // Leitura do DHT11
        SensorData data;
        data.temperature = dht.readTemperature();
        data.humidity = dht.readHumidity();
        data.error = isnan(data.temperature) || isnan(data.humidity);

        // Atualiza buffer circular
        buffer[indexBuffer] = data;
        indexBuffer = (indexBuffer + 1) % BUFFER_SIZE;

        // Mediana usando C
        SensorData median = movingMedian(buffer, BUFFER_SIZE);

        // Também demonstra uso da função assembly
        SensorData dummy = asmStruct(&median);

        if (median.error) {
          Serial.println("ERROR&SENSOR");
        } else {
          Serial.print("Temperatura mediana: ");
          Serial.print(median.temperature);
          Serial.print("°C | Humidade média: ");
          Serial.print(median.humidity);
          Serial.println("%");
        }

        // Sequência de LEDs
        digitalWrite(ledVerde, LOW);
        digitalWrite(ledAmarelo, LOW);
        digitalWrite(ledVermelho, HIGH);
        delay(1000);

        digitalWrite(ledVermelho, LOW);
        digitalWrite(ledAmarelo, HIGH);
        delay(1000);

        digitalWrite(ledAmarelo, LOW);
        digitalWrite(ledVerde, HIGH);
        delay(2000);
      }
    }
    else if (input == "0") {
      Serial.println("Logout efetuado.");
      logged_in = false;
      Serial.println("Digite seu username:");
      return;
    }
    else {
      Serial.println("Opção invalida. Digite:");
      printMenu();
    }
  }
}
