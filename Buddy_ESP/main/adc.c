#include "adc.h"
#include "i2c.h"

char* get_status(void) {
    ESP_LOGI("STATUS", "%s", status);
    return status;
}

unsigned int get_temperature(void) {
    double r_ntc = (-6.8 + sqrt(6.8*6.8 + 4.0*(double)(temp_out)))*0.5;
    double temperature = 298.15*NTC_CONSTANT/(298.15*log(r_ntc/10.0)+NTC_CONSTANT)-273.15;
    ESP_LOGI("TEMP", "%f C (NTC = %f kOhm)", temperature, r_ntc);
    return (int) temperature;
}

unsigned int get_moisture(void) {
    unsigned int moisture = 100 - (moist_out >> 5);
    ESP_LOGI("MOIST", "%d %%", moisture);
    return moisture ;
}

unsigned int get_distance(void) {
    double distance = -2.46 * dist_out * dist_out / 1000 / 1000 + 3.96 * dist_out / 1000 + 8.12;
    ESP_LOGI("DIST", "%f cm", distance);
    return (int) distance;
}

unsigned int get_light(void) {
    unsigned int light = 100-(light_out >> 4);
    ESP_LOGI("LIGHT", "%d %%", light);
    return light;
}

static void display_first(void) {
    ssd1306_clear_screen(&dev, false);
    ssd1306_display_text(&dev, 0, "Collecting Data ", 16, false);
    ssd1306_display_text(&dev, 2, "     _____      ", 16, false);
    ssd1306_display_text(&dev, 3, "    /  |  \\    ", 16, false);
    ssd1306_display_text(&dev, 4, "   |   |   |    ", 16, false);
    ssd1306_display_text(&dev, 5, "   |    \\  |   ", 16, false);
    ssd1306_display_text(&dev, 6, "    \\     /    ", 16, false);
    ssd1306_display_text(&dev, 7, "     -----      ", 16, false);
}

static void display_values(void) {
    char screen_text[20];
    ssd1306_clear_screen(&dev, false);
    ssd1306_display_text(&dev, 0, "Plant's Ambient", 16, false);

    sprintf(screen_text, "Temp: %d C     ", get_temperature());
    ssd1306_display_text(&dev, 2, screen_text, 16, false);

    sprintf(screen_text, "Moist: %d %%   ", get_moisture());
    ssd1306_display_text(&dev, 3, screen_text, 16, false);

    sprintf(screen_text, "Dist: %d cm    ", get_distance());
    ssd1306_display_text(&dev, 4, screen_text, 16, false);

    sprintf(screen_text, "Light: %d %%   ", get_light());
    ssd1306_display_text(&dev, 5, screen_text, 16, false);
}

bool adc_calibration_init(void)
{
    esp_err_t ret;
    cali_enable = false;

    ret = esp_adc_cal_check_efuse(ADC_CALIBRATE);
    if (ret == ESP_ERR_NOT_SUPPORTED)
        ESP_LOGW("ADC", "Calibration not supported");
    else if (ret == ESP_ERR_INVALID_VERSION)
        ESP_LOGW("ADC", "Invalid calibration");
    else if (ret == ESP_OK) {
        cali_enable = true;
        esp_adc_cal_characterize(ADC_UNIT_1, ADC_ATTENUATE, ADC_WIDTH_BIT_DEFAULT, 0, &adc1_chars);
    } else
        ESP_LOGE("ADC", "Invalid argument");

    return cali_enable;
}

static void collect_data() {
    if(temp_out == 0 && moist_out == 0 && light_out == 0 && dist_out == 0)
        display_first();
    else
        display_values();

    temp_out = 0;
    moist_out = 0;
    dist_out = 0;
    light_out = 0;

    for(int j = 0; j < SAMPLES; j++) {
        vTaskDelay(2000 / portTICK_PERIOD_MS);
        for(int i = 0; i < MAX_CHANNELS; i++) {
            adc_raw[i] = adc1_get_raw(channels[i]);
            if (cali_enable)
                voltage[i] = (int) esp_adc_cal_raw_to_voltage(adc_raw[i], &adc1_chars);
        }
        temp_out += abs(voltage[0] - voltage[1]);
        moist_out += voltage[2];
        dist_out += voltage[3];
        light_out += voltage[4];
    }
    temp_out /= SAMPLES;
    moist_out /= SAMPLES;
    dist_out /= SAMPLES;
    light_out /= SAMPLES;
}

void adc_result(void) {
    status[0] = "\0";
    collect_data();
    ssd1306_clear_screen(&dev, false);
    ssd1306_display_text(&dev, 0, " Plant's Status ", 16, false);
    if (get_temperature() >= 24 && get_temperature() <= 26 && get_light() >= 50 && get_light() <= 60 ) {
        ssd1306_display_text(&dev, 4, "       :D       ", 16, false);
        sprintf(status, "I%%20am%%20Happy!");
    } else if (get_temperature() > 20 && get_temperature() < 24 ) {
        ssd1306_display_text(&dev, 4, "       :)       ", 16, false);
        sprintf(status, "I%%20am%%20OK,%%20but%%20the%%20temperature%%20could%%20be%%20higher.");
    } else if (get_temperature() > 26 && get_temperature() < 30 ) {
        ssd1306_display_text(&dev, 4, "       :)       ", 16, false);
        sprintf(status, "I%%20am%%20OK,%%20but%%20the%%20temperature%%20could%%20be%%20lower.");
    } else if (get_light() > 40 && get_light() < 70 ) {
        ssd1306_display_text(&dev, 4, "       :)       ", 16, false);
        sprintf(status, "I%%20am%%20OK,%%20but%%20the%%20intensity%%20of%%20light%%20could%%20be%%20better.");
    } else {
        ssd1306_display_text(&dev, 4, "       :(       ", 16, false);
        sprintf(status, "I%%20am%%20sad,%%20I%%20do%%20not%%20have%%20the%%20appropiate%%20amount%%20of%%20light%%20and%%20temperature.");
    }
}

void adc_init(char* tank_levels) {
    temp_out = 0;
    moist_out = 0;
    dist_out = 0;
    light_out = 0;

    tank_levels[0] = ' ';
    tank_min = atoi(strtok(tank_levels, " "));
    tank_max = atoi(strtok(NULL, " "));

    cali_enable = adc_calibration_init();
    ESP_ERROR_CHECK(adc1_config_width(ADC_WIDTH_BIT_DEFAULT));
    for (int i = 0; i < MAX_CHANNELS; i++)
        ESP_ERROR_CHECK(adc1_config_channel_atten(channels[i], ADC_ATTENUATE));
}