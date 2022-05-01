#include <sys/cdefs.h>
#include "wifi.h"
#include "gpio.h"

static QueueHandle_t gpio_evt_queue = NULL;

static void IRAM_ATTR gpio_isr_handler(void* arg)
{
    uint32_t gpio_num = (uint32_t) arg;
    xQueueSendFromISR(gpio_evt_queue, &gpio_num, NULL);
}

void blufi_btn(void)
{
    bool btn_pressed = false;
    wifi_connected = false;
    while(1) {
        if (gpio_get_level(BT_IN) == false && btn_pressed == false) {
            ESP_LOGW("BLUFI", "Button pressed");
            btn_pressed = true;
            blufi_init();
        }
        if (wifi_connected == true) {
            ESP_LOGW("WIFI", "Connected!");
            break;
        }
    }
}

_Noreturn void gpio_pump(void) {
    while(1) {
        ESP_LOGW("PUMP:", "Working...");
        gpio_set_level(PUMP_OUT, 1);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        gpio_set_level(PUMP_OUT, 0);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
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