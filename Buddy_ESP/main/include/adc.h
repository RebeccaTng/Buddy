#include <sys/cdefs.h>
#include "config.h"

#define ADC_ATTENUATE   ADC_ATTEN_DB_11
#define ADC_CALIBRATE   ESP_ADC_CAL_VAL_EFUSE_VREF
#define MAX_CHANNELS    5
#define SAMPLES         3

static const adc1_channel_t channels[MAX_CHANNELS] = {
        ADC1_CHANNEL_6, //Pin A2 //Temperature1
        ADC1_CHANNEL_3, //Pin A3 //Temperature2
        ADC1_CHANNEL_0, //Pin A4 //Moisture
        ADC1_CHANNEL_4, //Pin 32 //Distance
        ADC1_CHANNEL_5  //Pin 33 // LDR
};

static const char *tags[16] = {
        "Temp+",
        "Temp-",
        "Moist",
        "Dist.",
        "LDR-3"
};

static unsigned int temperature;
static unsigned int moisture;
static unsigned int distance;
static unsigned int light;

static bool cali_enable;
static esp_adc_cal_characteristics_t adc1_chars;

static int adc_raw[MAX_CHANNELS];
static uint32_t voltage[MAX_CHANNELS];

unsigned int getTemperature();
unsigned int getMoisture();
unsigned int getDistance();
unsigned int getLight();

void adc_init(void);
void adc_result(void);
