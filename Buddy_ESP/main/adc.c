#include "adc.h"
#include "i2c.h"

unsigned int getTemperature(void) {
    double r_ntc = (-6.8 + sqrt(6.8*6.8 + 4.0*(double)(temp_out)))*0.5;
    double temperature = 298.15*NTC_CONSTANT/(298.15*log(r_ntc/10.0)+NTC_CONSTANT)-273.15;
    ESP_LOGI("TEMP", "%f C (NTC = %f kOhm)", temperature, r_ntc);
    return (int) temperature;
}

unsigned int getMoisture(void) {
    unsigned int moisture = moist_out >> 5;
    ESP_LOGI("MOIST", "%d %%", moisture);
    return moisture ;
}

unsigned int getDistance(void) {
    ESP_LOGI("DIST", "%d mV", dist_out);
    return dist_out;
}

unsigned int getLight(void) {
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

    sprintf(screen_text, "Temp: %d C    ", getTemperature());
    ssd1306_display_text(&dev, 2, screen_text, 16, false);

    sprintf(screen_text, "Moist: %d %%  ", getMoisture());
    ssd1306_display_text(&dev, 3, screen_text, 16, false);

    sprintf(screen_text, "Dist: %d mV   ", getDistance());
    ssd1306_display_text(&dev, 4, screen_text, 16, false);

    sprintf(screen_text, "Light: %d %%  ", getLight());
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
    collect_data();
    ssd1306_clear_screen(&dev, false);
    ssd1306_display_text(&dev, 0, " Plant's Status ", 16, false);
    if ( getTemperature() > 24 && getTemperature() < 26 && getLight() > 50 && getLight() < 60 )
        ssd1306_display_text(&dev, 4, "       :D       ", 16, false);
    else if ( getTemperature() > 20 && getTemperature() < 30 && getLight() > 40 && getLight() < 70 )
        ssd1306_display_text(&dev, 4, "       :)       ", 16, false);
    else
        ssd1306_display_text(&dev, 4, "       :(       ", 16, false);
}

void adc_init(void) {
    temp_out = 0;
    moist_out = 0;
    dist_out = 0;
    light_out = 0;
    cali_enable = adc_calibration_init();
    ESP_ERROR_CHECK(adc1_config_width(ADC_WIDTH_BIT_DEFAULT));
    for (int i = 0; i < MAX_CHANNELS; i++)
        ESP_ERROR_CHECK(adc1_config_channel_atten(channels[i], ADC_ATTENUATE));
}