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
void blufi_btn(void);
bool pump_started(unsigned int moisture, char* day);