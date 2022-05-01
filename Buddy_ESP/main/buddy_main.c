#include <sys/cdefs.h>
#include "config.h"
#include "audio.h"
#include "gpio.h"
#include "https.h"

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
    ESP_LOGW("MAIN", "Initialising 'Buddy'.");
    ESP_LOGW("BLUFI", "Press the button to connect to Blufi.");

    blufi_btn();
    start_tasks();

    ESP_LOGW("HTTPS", "Starting...");
    https_ready = false;
    https_init();

    while(1);
}