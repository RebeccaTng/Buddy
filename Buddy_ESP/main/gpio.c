#include <sys/cdefs.h>
#include "gpio.h"

 void gpio_init() {
    gpio_config_t io_conf = {};
    io_conf.intr_type = GPIO_INTR_DISABLE;
    io_conf.mode = GPIO_MODE_OUTPUT;
    io_conf.pin_bit_mask = GPIO_OUTPUT_PIN_SEL;
    io_conf.pull_down_en = 0;
    io_conf.pull_up_en = 0;
    gpio_config(&io_conf);

    /*io_conf.intr_type = GPIO_INTR_POSEDGE;
    io_conf.pin_bit_mask = GPIO_INPUT_PIN_SEL;
    io_conf.mode = GPIO_MODE_INPUT;
    io_conf.pull_up_en = 1;
    gpio_config(&io_conf);*/
}

_Noreturn void* gpio_toggle(void* _) {
    while(1) {
        gpio_set_level(GPIO_OUTPUT_13, 1);
        gpio_set_level(GPIO_OUTPUT_27, 1);
        gpio_set_level(GPIO_OUTPUT_33, 1);
        gpio_set_level(GPIO_OUTPUT_15, 1);
        gpio_set_level(GPIO_OUTPUT_14, 1);
        vTaskDelay(pdMS_TO_TICKS(1));
        gpio_set_level(GPIO_OUTPUT_13, 0);
        gpio_set_level(GPIO_OUTPUT_27, 0);
        gpio_set_level(GPIO_OUTPUT_33, 0);
        gpio_set_level(GPIO_OUTPUT_15, 0);
        gpio_set_level(GPIO_OUTPUT_14, 0);
        vTaskDelay(pdMS_TO_TICKS(1));
    }
}