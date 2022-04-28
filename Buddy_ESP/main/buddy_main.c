#define TASK_TAG "xTASK"

#include <sys/cdefs.h>
#include "config.h"
#include "adc.h"
#include "gpio.h"
#include "audio.h"

ESP_EVENT_DECLARE_BASE(TASK_EVENTS);

void start_tasks(void) {

    ESP_LOGI("MAIN", "Setting up tasks...");

    adc_init();
    gpio_init();
    esp_event_loop_args_t loop_args = {
            .queue_size = 5,
            .task_name = NULL
    };

    esp_event_loop_handle_t loop;
    ESP_ERROR_CHECK(esp_event_loop_create(&loop_args, &loop));
    ESP_ERROR_CHECK(esp_event_loop_create(&loop_args, &loop));
    ESP_ERROR_CHECK(esp_event_loop_create(&loop_args, &loop));
    ESP_ERROR_CHECK(esp_event_loop_create(&loop_args, &loop));

    ESP_LOGI(TASK_TAG, "Starting adc");
    xTaskCreate((TaskFunction_t) adc_result,
                "adc",
                4096,
                NULL,
                uxTaskPriorityGet(NULL),
                NULL
    );

    /*ESP_LOGI(TASK_TAG, "Starting audio");
    xTaskCreate((TaskFunction_t) gpio_motor(),
                "audio",
                4096,
                NULL,
                uxTaskPriorityGet(NULL),
                NULL
    );*/

    ESP_LOGI(TASK_TAG, "Starting pump");
    xTaskCreate((TaskFunction_t) gpio_pump,
                "pump",
                4096,
                NULL,
                uxTaskPriorityGet(NULL),
                NULL
    );
}

_Noreturn void app_main(void)
{
    ESP_LOGI("MAIN", "Initialising 'Buddy'");
    start_tasks();

    pthread_t blufi_thread;
    pthread_create(&blufi_thread, NULL, blufi_btn, NULL);
    pthread_join(blufi_thread, NULL);

    while(1);
}