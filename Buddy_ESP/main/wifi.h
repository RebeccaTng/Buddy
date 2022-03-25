#include "config.h"

#define EXAMPLE_ESP_WIFI_SSID      "123"
#define EXAMPLE_ESP_WIFI_PASS      "12345678"
#define EXAMPLE_ESP_MAXIMUM_RETRY  3
#define WIFI_CONNECTED_BIT BIT0
#define WIFI_FAIL_BIT      BIT1

static EventGroupHandle_t s_wifi_event_group;
static const char *wifi_tag = "wifi station";
static int s_retry_num = 0;

void wifi_init();