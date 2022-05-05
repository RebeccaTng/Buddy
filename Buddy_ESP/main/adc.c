#include "adc.h"
#include "i2c.h"

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

unsigned int getTemperature() {
    return temperature;
}

unsigned int getMoisture() {
    return moisture;
}

unsigned int getDistance() {
    return distance;
}

unsigned int getLight() {
    return light;
}

static void collect_data() {
    temperature = 0;
    moisture = 0;
    distance = 0;
    light = 0;

    for(int j = 0; j < SAMPLES; j++) {
        vTaskDelay(2000 / portTICK_PERIOD_MS);
        for(int i = 0; i < MAX_CHANNELS; i++) {
            adc_raw[i] = adc1_get_raw(channels[i]);
            if (cali_enable) {
                voltage[i] = esp_adc_cal_raw_to_voltage(adc_raw[i], &adc1_chars);
                ESP_LOGI(tags[i], "ADC: %d mV", voltage[i]);
                char screen_text[16];
                sprintf(screen_text, "%s: %dmV", tags[i], voltage[i]);
                ssd1306_display_text(&dev, i + 2, screen_text, 16, false);
            }
        }
        temperature += voltage[0] - voltage[1];
        moisture += voltage[2];
        distance += voltage[3];
        light += voltage[4];
    }
    temperature /= SAMPLES;
    moisture /= SAMPLES;
    distance /= SAMPLES;
    light /= SAMPLES;
}

void adc_result(void) {
    collect_data();
    ssd1306_clear_screen(&dev, false);
    ssd1306_display_text(&dev, 0, " Plant's Status ", 16, false);
    if      (
            temperature > 2000 && temperature < 2500
            && moisture > 2000 && moisture < 2500
            && distance > 2000 && distance < 2500
            && light > 2000 && light < 2500
            )
        ssd1306_display_text(&dev, 4, "       :D       ", 16, false);
    else if (
            temperature > 1500 && temperature < 3000
            && moisture > 1500 && moisture < 3000
            && distance > 1500 && distance < 3000
            && light > 1500 && light < 3000
            )
        ssd1306_display_text(&dev, 4, "       :)       ", 16, false);
    else
        ssd1306_display_text(&dev, 4, "       :(       ", 16, false);
}

void adc_init(void) {
    cali_enable = adc_calibration_init();
    ESP_ERROR_CHECK(adc1_config_width(ADC_WIDTH_BIT_DEFAULT));
    for (int i = 0; i < MAX_CHANNELS; i++)
        ESP_ERROR_CHECK(adc1_config_channel_atten(channels[i], ADC_ATTENUATE));
}