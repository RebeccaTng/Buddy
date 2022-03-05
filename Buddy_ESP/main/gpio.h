#include <sys/cdefs.h>
#include "config.h"

#define GPIO_OUTPUT_13      13  //Sound
#define GPIO_OUTPUT_12      12  //Display
#define GPIO_OUTPUT_27      27  //LED1
#define GPIO_OUTPUT_33      33
#define GPIO_OUTPUT_15      15  //Pump
#define GPIO_OUTPUT_14      14

#define GPIO_OUTPUT_PIN_SEL ( \
                                (1ULL<<GPIO_OUTPUT_13) | \
                                (1ULL<<GPIO_OUTPUT_12) | \
                                (1ULL<<GPIO_OUTPUT_27) | \
                                (1ULL<<GPIO_OUTPUT_33) | \
                                (1ULL<<GPIO_OUTPUT_15) | \
                                (1ULL<<GPIO_OUTPUT_14)   \
                            )
//#define GPIO_INPUT_IO_0     CONFIG_GPIO_INPUT_0
//#define GPIO_INPUT_IO_1     CONFIG_GPIO_INPUT_1
//#define GPIO_INPUT_PIN_SEL  ((1ULL<<GPIO_INPUT_IO_0) | (1ULL<<GPIO_INPUT_IO_1))

void gpio_init();

_Noreturn void* gpio_toggle(void* _);