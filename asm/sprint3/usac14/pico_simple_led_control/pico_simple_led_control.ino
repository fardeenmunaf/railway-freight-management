// Código Arduino/Pico W - SIMPLIFICADO para USAC14
// Este código ESCUTA comandos pela serial e controla LEDs

#include "DHT.h"

// Configuração DHT11
#define DHTPIN 1 // GPIO 1 (GP1)
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

const int ledVermelho = 18;
const int ledAmarelo = 17;
const int ledVerde = 16;

String inputBuffer = "";

void setup()
{
    Serial.begin(115200); // Mantém 115200 para compatibilidade com AntiX
    delay(1000);

    pinMode(ledVermelho, OUTPUT);
    pinMode(ledAmarelo, OUTPUT);
    pinMode(ledVerde, OUTPUT);

    // Desligar todos
    digitalWrite(ledVermelho, LOW);
    digitalWrite(ledAmarelo, LOW);
    digitalWrite(ledVerde, LOW);

    // Inicializar DHT11
    dht.begin();

    Serial.println("[PICO] Pronto - Aguardando comandos");
    Serial.println("[DHT11] Sensor inicializado no GPIO 1");
}

void loop()
{
    // Ler comandos da serial
    while (Serial.available() > 0)
    {
        char c = Serial.read();

        if (c == '\n')
        {
            processCommand(inputBuffer);
            inputBuffer = "";
        }
        else
        {
            inputBuffer += c;
        }
    }
}

void processCommand(String cmd)
{
    cmd.trim();

    Serial.print("[RECV] ");
    Serial.println(cmd);

    // Comando GTH - Enviar dados de sensores
    if (cmd == "GTH")
    {
        // Ler sensor DHT11
        float temp = dht.readTemperature();
        float hum = dht.readHumidity();

        // Verificar se a leitura falhou
        if (isnan(temp) || isnan(hum))
        {
            Serial.println("ERROR:DHT11");
            return;
        }

        Serial.print("T:");
        Serial.print(temp, 1);
        Serial.print(",H:");
        Serial.println(hum, 1);
        return;
    }

    // Comandos de LEDs
    if (cmd == "RE")
    {
        digitalWrite(ledVermelho, HIGH);
        Serial.println("[OK] LED Vermelho ON");
    }
    else if (cmd == "YE")
    {
        digitalWrite(ledAmarelo, HIGH);
        Serial.println("[OK] LED Amarelo ON");
    }
    else if (cmd == "GE")
    {
        digitalWrite(ledVerde, HIGH);
        Serial.println("[OK] LED Verde ON");
    }
    else if (cmd == "RB")
    {
        digitalWrite(ledVermelho, LOW);
        Serial.println("[OK] LED Vermelho OFF");
    }
    else if (cmd == "YB")
    {
        digitalWrite(ledAmarelo, LOW);
        Serial.println("[OK] LED Amarelo OFF");
    }
    else if (cmd == "GB")
    {
        digitalWrite(ledVerde, LOW);
        Serial.println("[OK] LED Verde OFF");
    }
    else
    {
        Serial.print("[WARN] Comando desconhecido: ");
        Serial.println(cmd);
    }
}