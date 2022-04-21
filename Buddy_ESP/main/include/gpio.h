#include <sys/cdefs.h>
#include "config.h"

//Define Outputs
#define DISP_OUT    12  //Display   Output Pin 12
#define PUMP_OUT    15  //Pump      Output Pin 15
#define GPIO_OUTPUT_PIN_SEL ((1ULL<<DISP_OUT) | (1ULL<<PUMP_OUT))

//Define Inputs
#define BT_IN       13  //Bluetooth Input Pin 13
#define GPIO_INPUT_PIN_SEL  (1ULL<<BT_IN)

void gpio_init();
_Noreturn void* gpio_toggle(void* _);