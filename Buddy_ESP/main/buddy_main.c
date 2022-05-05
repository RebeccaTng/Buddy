#include <sys/cdefs.h>
#include "config.h"
#include "audio.h"
#include "gpio.h"
#include "https.h"
#include "i2c.h"

ESP_EVENT_DECLARE_BASE(TASK_EVENTS);

void start_pump(void) {

    ESP_LOGI("MAIN", "Setting up tasks...");

    adc_init();
    gpio_init();
    esp_event_loop_args_t loop_args = {
            .queue_size = 3,
            .task_name = NULL
    };

    esp_event_loop_handle_t loop;
    ESP_ERROR_CHECK(esp_event_loop_create(&loop_args, &loop));

    ESP_LOGI("PUMP", "Starting pump");
    xTaskCreate((TaskFunction_t) gpio_pump,
                "pump",
                2048,
                NULL,
                uxTaskPriorityGet(NULL),
                NULL
    );
}

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
    ssd1306_display_text(&dev, 0, "  Connected!   ", 16, false);
    audio_init(1);

    ESP_LOGI("TASKS", "Starting Pump...");
    start_pump();

    ESP_LOGI("HTTPS", "Starting HTTPS...");
    ssd1306_display_text(&dev, 0, " Testing HTTPS ", 16, false);
    https_init();
    audio_init(2);

    ssd1306_display_text(&dev, 0, " Plant's Status ", 16, false);
    while(1) {
        adc_result();
        send_data();
    }
}