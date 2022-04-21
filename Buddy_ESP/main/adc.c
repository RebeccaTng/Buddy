#include <sys/cdefs.h>
#include "adc.h"
pthread_mutex_t lock;

bool adc_calibration_init(void)
{
    static const char *TAG = "ADC SINGLE";
    esp_err_t ret;
    cali_enable = false;

    ret = esp_adc_cal_check_efuse(ADC_CALIBRATE);
    if (ret == ESP_ERR_NOT_SUPPORTED)
        ESP_LOGW(TAG, "Calibration scheme not supported, skip software calibration");
    else if (ret == ESP_ERR_INVALID_VERSION)
        ESP_LOGW(TAG, "eFuse not burnt, skip software calibration");
    else if (ret == ESP_OK) {
        cali_enable = true;
        esp_adc_cal_characterize(ADC_UNIT_1, ADC_ATTENUATE, ADC_WIDTH_BIT_DEFAULT, 0, &adc1_chars);
    } else
        ESP_LOGE(TAG, "Invalid arg");

    return cali_enable;
}

_Noreturn void* adc_result(void* _) {
    int counter = 0;
    while(1) {
        for(int i = 0; i < MAX_CHANNELS; i++) {
            adc_raw[i] = adc1_get_raw(channels[i]);
            if (cali_enable) {
                voltage[i] = esp_adc_cal_raw_to_voltage(adc_raw[i], &adc1_chars);
                ESP_LOGI(tags[i], "ADC Result: %d mV", voltage[i]);
            }
        }
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        if(counter++ == 3) {
            pthread_mutex_lock(&lock);
            send_data(NULL);
            pthread_mutex_unlock(&lock);
            counter = 0;
        }
    }
}

void adc_init() {
    if (pthread_mutex_init(&lock, NULL) != 0)
    {
        printf("\n mutex init failed\n");
    }
    cali_enable = adc_calibration_init();
    ESP_ERROR_CHECK(adc1_config_width(ADC_WIDTH_BIT_DEFAULT));
    for (int i = 0; i < MAX_CHANNELS; i++)
        ESP_ERROR_CHECK(adc1_config_channel_atten(channels[i], ADC_ATTENUATE));
    https_init();
}