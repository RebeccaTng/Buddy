#include <sys/cdefs.h>
#include "adc.h"
#include "config.h"
#include "audio.h"
#include "gpio.h"
#include "https.h"
#include "i2c.h"

static void display_buddy() {
    ssd1306_display_text(&dev, 0, "       *        ", 16, false);
    ssd1306_display_text(&dev, 1, "     *   *      ", 16, false);
    ssd1306_display_text(&dev, 2, "   *       *    ", 16, false);
    ssd1306_display_text(&dev, 3, " -   Buddy   -  ", 16, false);
    ssd1306_display_text(&dev, 4, "   *       *    ", 16, false);
    ssd1306_display_text(&dev, 5, "     *   *      ", 16, false);
    ssd1306_display_text(&dev, 6, "       *        ", 16, false);
}

static void display_tutorial() {
    ssd1306_display_text(&dev, 0, " How to Start?  ", 16, false);
    ssd1306_display_text(&dev, 1, "                ", 16, false);
    ssd1306_display_text(&dev, 2, "Start Bluetooth ", 16, false);
    ssd1306_display_text(&dev, 3, "on your Android ", 16, false);
    ssd1306_display_text(&dev, 4, "                ", 16, false);
    ssd1306_display_text(&dev, 5, "Press Bluetooth ", 16, false);
    ssd1306_display_text(&dev, 6, "button on Buddy ", 16, false);
}

_Noreturn void app_main(void)
{
    ESP_LOGI("MAIN", "Initialising 'Buddy'.");
    i2c_master_init(CONFIG_SDA_GPIO, CONFIG_SCL_GPIO, CONFIG_RESET_GPIO);

    ESP_LOGI("SCREEN", "Configured!");
    display_buddy();
    audio_init(0);

    ESP_LOGI("BLUFI", "Starting Blufi.");
    display_tutorial();
    blufi_btn();
    ssd1306_display_text(&dev, 0, " Connecting...  ", 16, false);
    ssd1306_display_text(&dev, 2, "     _____      ", 16, false);
    ssd1306_display_text(&dev, 3, "    /  |  \\    ", 16, false);
    ssd1306_display_text(&dev, 4, "   |   |   |    ", 16, false);
    ssd1306_display_text(&dev, 5, "   |    \\  |   ", 16, false);
    ssd1306_display_text(&dev, 6, "    \\     /    ", 16, false);
    ssd1306_display_text(&dev, 7, "     -----      ", 16, false);
    audio_init(1);

    ESP_LOGI("HTTPS", "Starting HTTPS...");
    ssd1306_display_text(&dev, 0, " Testing HTTPS ", 16, false);
    https_init();
    audio_init(2);

    ESP_LOGI("TASKS", "Starting ADC and GPIO...");
    adc_init(get_tank());
    gpio_init();

    ssd1306_display_text(&dev, 0, " Plant's Status ", 16, false);
    while(1) {
        adc_result();
        insert_sensor_data(get_moisture(), get_light(), get_temperature(), get_distance(), get_status());
        if(pump_started(get_moisture(), get_day())) {
            insert_water();
        }
    }
}