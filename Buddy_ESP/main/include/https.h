#include "config.h"

#define CHIP_ID     "61"
#define IN_DATA     "insertdata/"
#define IN_WATER    "insertLastWater/"

#define WEB_SERVER  "a21iot03.studev.groept.be"
#define WEB_PORT    "443"
#define WEB_URL     "https://a21iot03.studev.groept.be/public/api/esp/"

#define VERIFY_CALLBACK_ERR "Verifying Certificate Callback Failed (Probably ESP32 out of memory)"
#define FEATURE_NOT_AV_ERR  "Invalid Request."
#define BAD_INP_PARAMS_ERR  "Bad Request Parameters."
#define SOCKET_READING_ERR  "Failed to read information from the socket"
#define BAD_WIFI_CREDS_ERR  "Invalid WiFi Credentials"
#define WIFI_TURNED_OFF_ERR "Invalid WiFi Credentials"

static const char *HTTPS_TAG = "HTTPS";
static char request[256];

static int ret;
static int err_cnt;

static mbedtls_ssl_context ssl;
static mbedtls_entropy_context entropy;
static mbedtls_ctr_drbg_context ctr_drbg;
static mbedtls_x509_crt cacert;
static mbedtls_ssl_config conf;

void https_init(void);
void insert_sensor_data(unsigned int moisture, unsigned int light, unsigned int temperature, unsigned int distance, char* status);
void insert_water(char* date);
