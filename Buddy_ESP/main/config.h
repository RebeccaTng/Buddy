//System Includes////////////////////////////////////
#include <sys/cdefs.h>
#include "sys/time.h"

//C-Lang Includes////////////////////////////////////
#include <math.h>
#include <pthread.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "time.h"

//freeRTOS Includes//////////////////////////////////
#include "freertos/FreeRTOS.h"
#include "freertos/queue.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"

//I/O Includes///////////////////////////////////////
#include "driver/adc.h"
#include "driver/dac.h"
#include "driver/gpio.h"
#include "driver/gptimer.h"

//ESP Includes///////////////////////////////////////
#include "esp_adc_cal.h"
#include "esp_bt.h"
#include "esp_bt_main.h"
#include "esp_bt_device.h"
#include "esp_event.h"
#include "esp_gap_bt_api.h"
#include "esp_log.h"
#include "esp_spp_api.h"
#include "esp_system.h"
#include "esp_wifi.h"

//NVS Includes///////////////////////////////////////
#include "nvs.h"
#include "nvs_flash.h"