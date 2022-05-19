#include <sys/cdefs.h>
#include "config.h"

#define ADC_ATTENUATE   ADC_ATTEN_DB_11
#define ADC_CALIBRATE   ESP_ADC_CAL_VAL_EFUSE_VREF
#define MAX_CHANNELS    5
#define SAMPLES         3

#define NTC_CONSTANT    10000.0

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

static char status[128];
static int temp_out;
static int moist_out;
static int dist_out;
static int light_out;

static int tank_min;
static int tank_max;

static bool cali_enable;
static esp_adc_cal_characteristics_t adc1_chars;

static int adc_raw[MAX_CHANNELS];
static int voltage[MAX_CHANNELS];

char* get_status();
unsigned int get_temperature();
unsigned int get_moisture();
unsigned int get_distance();
unsigned int get_light();

void display_bad_wifi(void);
void adc_init(char* tank_levels);
void adc_result(void);
