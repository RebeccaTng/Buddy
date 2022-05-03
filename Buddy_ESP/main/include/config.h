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

//freeRTOS Includes//////////////////////////////////
#include "freertos/FreeRTOS.h"
#include "freertos/queue.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"

//Driver Includes////////////////////////////////////
#include "driver/adc.h"
#include "driver/dac.h"
#include "driver/gpio.h"
#include "driver/gptimer.h"
#include "driver/i2c.h"

//ESP Includes///////////////////////////////////////
#include "esp_adc_cal.h"
#include "esp_blufi.h"
#include "esp_blufi_api.h"
#include "esp_bt.h"
#include "esp_bt_main.h"
#include "esp_bt_device.h"
#include "esp_crc.h"
#include "esp_crt_bundle.h"
#include "esp_event.h"
#include "esp_event_base.h"
#include "esp_gap_bt_api.h"
#include "esp_log.h"
#include "esp_netif.h"
#include "esp_spp_api.h"
#include "esp_system.h"
#include "esp_timer.h"
#include "esp_wifi.h"

//Lwip Includes////////////////////////////////////////
#include "lwip/err.h"
#include "lwip/sockets.h"

//Mbeds Includes///////////////////////////////////////
#include "mbedtls/aes.h"
#include "mbedtls/dhm.h"
#include "mbedtls/md5.h"
#include "mbedtls/net_sockets.h"
#include "mbedtls/ssl.h"
#include "mbedtls/entropy.h"
#include "mbedtls/ctr_drbg.h"
#include "mbedtls/error.h"

//NVS Includes///////////////////////////////////////
#include "nvs.h"
#include "nvs_flash.h"

//Other Includes/////////////////////////////////////
#include "protocol_examples_common.h"
#include "time.h"