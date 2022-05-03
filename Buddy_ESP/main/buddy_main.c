#include <sys/cdefs.h>
#include "config.h"
#include "audio.h"
#include "gpio.h"
#include "https.h"
#include "i2c.h"

ESP_EVENT_DECLARE_BASE(TASK_EVENTS);

void start_tasks(void) {

    ESP_LOGW("MAIN", "Setting up tasks...");

    adc_init();
    gpio_init();
    esp_event_loop_args_t loop_args = {
            .queue_size = 5,
            .task_name = NULL
    };

    esp_event_loop_handle_t loop;
    ESP_ERROR_CHECK(esp_event_loop_create(&loop_args, &loop));

    ESP_LOGW("ADC", "Starting adc");
    xTaskCreate((TaskFunction_t) adc_result,
                "adc",
                2048,
                NULL,
                uxTaskPriorityGet(NULL),
                NULL
    );

    ESP_LOGW("PUMP", "Starting pump");
    xTaskCreate((TaskFunction_t) gpio_pump,
                "pump",
                2048,
                NULL,
                uxTaskPriorityGet(NULL),
                NULL
    );
}

_Noreturn void app_main(void)
{
    ESP_LOGI("MAIN", "Initialising 'Buddy'.");
    i2c_master_init(CONFIG_SDA_GPIO, CONFIG_SCL_GPIO, CONFIG_RESET_GPIO);

    ESP_LOGI("SCREEN", "Configured!");
    ssd1306_display_text(&dev, 0, "       *        ", 16, false);
    ssd1306_display_text(&dev, 1, "     *   *      ", 16, false);
    ssd1306_display_text(&dev, 2, "   *       *    ", 16, false);
    ssd1306_display_text(&dev, 3, " -   Buddy   -  ", 16, false);
    ssd1306_display_text(&dev, 4, "   *       *    ", 16, false);
    ssd1306_display_text(&dev, 5, "     *   *      ", 16, false);
    ssd1306_display_text(&dev, 6, "       *        ", 16, false);

    vTaskDelay(5000 / portTICK_PERIOD_MS);
    ESP_LOGI("BLUFI", "Starting Blufi.");
    ssd1306_display_text(&dev, 0, " How to Start?  ", 16, false);
    ssd1306_display_text(&dev, 1, "                ", 16, false);
    ssd1306_display_text(&dev, 2, "Start Bluetooth ", 16, false);
    ssd1306_display_text(&dev, 3, "on your Android ", 16, false);
    ssd1306_display_text(&dev, 4, "                ", 16, false);
    ssd1306_display_text(&dev, 5, "Press Bluetooth ", 16, false);
    ssd1306_display_text(&dev, 6, "button on Buddy ", 16, false);
    blufi_btn();

    ESP_LOGI("TASKS", "Starting tasks...");
    start_tasks();

    ESP_LOGI("HTTPS", "Sending HTTPS...");
    https_ready = false;
    https_init();

    while(1);
}