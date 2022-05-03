#include <sys/cdefs.h>
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

_Noreturn void adc_result(void) {
    int counter = 0;
    bool new = true;
    while(1) {
        for(int i = 0; i < MAX_CHANNELS; i++) {
            adc_raw[i] = adc1_get_raw(channels[i]);
            if (cali_enable) {
                voltage[i] = esp_adc_cal_raw_to_voltage(adc_raw[i], &adc1_chars);
                ESP_LOGI(tags[i], "ADC: %d mV", voltage[i]);
                if(new == true) {
                    char screen_text[16];
                    sprintf(screen_text, "%s: %dmV", tags[i], voltage[i]);
                    ssd1306_display_text(&dev, i + 2, screen_text, 16, false);
                }
            }
        }

        temperature = voltage[0] - voltage[1];
        moisture = voltage[2];
        distance = voltage[3];
        light = voltage[4];

        if(counter++ == 3 && https_ready == false) {
            counter = 0;
            https_ready = true;
            new = false;
            ssd1306_clear_screen(&dev, false);
            ssd1306_display_text(&dev, 0, " Plant's Status ", 16, false);
            ssd1306_display_text(&dev, 4, "       :D       ", 16, false);
        }

        vTaskDelay(2000 / portTICK_PERIOD_MS);
    }
}

void adc_init(void) {
    cali_enable = adc_calibration_init();
    ESP_ERROR_CHECK(adc1_config_width(ADC_WIDTH_BIT_DEFAULT));
    for (int i = 0; i < MAX_CHANNELS; i++)
        ESP_ERROR_CHECK(adc1_config_channel_atten(channels[i], ADC_ATTENUATE));
}