#include <sys/cdefs.h>
#include "gpio.h"
#include "wifi.h"
#include "i2c.h"

void blufi_btn(void)
{
    bool btn_pressed = false;
    wifi_connected = false;
    while(1) {
        if (gpio_get_level(BT_IN) == false && btn_pressed == false) {
            ESP_LOGW("BLUFI", "Button pressed");
            ssd1306_display_text(&dev, 0, "Button Pressed! ", 16, false);
            ssd1306_display_text(&dev, 1, "                ", 16, false);
            ssd1306_display_text(&dev, 2, "Now go to App   ", 16, false);
            ssd1306_display_text(&dev, 3, "Open Esp BluFi  ", 16, false);
            ssd1306_display_text(&dev, 4, "Tap:BLUFI_DEVICE", 16, false);
            ssd1306_display_text(&dev, 5, "Press Connect   ", 16, false);
            ssd1306_display_text(&dev, 6, "Configure WiFi  ", 16, false);

            btn_pressed = true;
            blufi_init();
        }
        if (wifi_connected == true) {
            ESP_LOGW("WIFI", "Connected!");
            ssd1306_clear_screen(&dev, false);
            ssd1306_display_text(&dev, 0, "   Connected!   ", 16, false);
            break;
        }
    }
}

bool pump_started(unsigned int moisture) {
    ESP_LOGW("PUMP:", "Checking moisture...");
    if(moisture <= 75) {
        ESP_LOGW("PUMP:", "Watering...");
        gpio_set_level(PUMP_OUT, 1);
        vTaskDelay(3000 / portTICK_PERIOD_MS);
        gpio_set_level(PUMP_OUT, 0);
        return true;
    }
    return false;
}

void gpio_init(void) {
    gpio_config_t io_conf = {};
    io_conf.intr_type = GPIO_INTR_DISABLE;
    io_conf.mode = GPIO_MODE_OUTPUT;
    io_conf.pin_bit_mask = GPIO_OUTPUT_PIN_SEL;
    io_conf.pull_down_en = 0;
    io_conf.pull_up_en = 0;
    gpio_config(&io_conf);

    io_conf.intr_type = GPIO_INTR_POSEDGE;
    io_conf.pin_bit_mask = GPIO_INPUT_PIN_SEL;
    io_conf.mode = GPIO_MODE_INPUT;
    io_conf.pull_up_en = 1;
    gpio_config(&io_conf);
}