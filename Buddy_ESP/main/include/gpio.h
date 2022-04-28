#include <sys/cdefs.h>
#include "bluetooth.h"
#include "config.h"

//Define Outputs
#define PUMP_OUT    15  //Pump Output Pin 15
#define GPIO_OUTPUT_PIN_SEL (1ULL<<PUMP_OUT)

//Define Inputs
#define BT_IN       13  //Bluetooth Input Pin 13
#define GPIO_INPUT_PIN_SEL  (1ULL<<BT_IN)

void gpio_init(void);
_Noreturn void* blufi_btn(void* _);
_Noreturn TaskFunction_t gpio_pump(void);