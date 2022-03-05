#include "config.h"
#include "gpio.h"
#include "adc.h"

_Noreturn void app_main(void)
{
    adc_init();
    gpio_init();
    pthread_t adc_thread, gpio_thread;

    pthread_create(&adc_thread, NULL, adc_result, "ADC Started");
    pthread_create(&gpio_thread, NULL, gpio_toggle, "GPIO Started");
    pthread_join( adc_thread, NULL);
    pthread_join( gpio_thread, NULL);

}
