#include <sys/cdefs.h>
#include "config.h"
#include "https.h"

#define ADC_ATTENUATE   ADC_ATTEN_DB_11
#define ADC_CALIBRATE   ESP_ADC_CAL_VAL_EFUSE_VREF
#define MAX_CHANNELS    5

static const adc1_channel_t channels[MAX_CHANNELS] = {
        ADC1_CHANNEL_6, //Pin A2 //Temperature1
        ADC1_CHANNEL_3, //Pin A3 //Temperature2
        ADC1_CHANNEL_0, //Pin A4 //Moisture
        ADC1_CHANNEL_4, //Pin 32 //Distance
        ADC1_CHANNEL_5  //Pin 33 // LDR
};

static const char *tags[16] = {
        "Temp+ (ch. A2)",
        "Temp- (ch. A3)",
        "Moist (ch. A4)",
        "Dist. (ch. 32)",
        "LDR-3 (ch. 33)"
};

static bool cali_enable;
static esp_adc_cal_characteristics_t adc1_chars;

static int adc_raw[MAX_CHANNELS];
static uint32_t voltage[MAX_CHANNELS];

void adc_init();

_Noreturn void* adc_result(void* _);
