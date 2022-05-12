#include "config.h"
#include "adc.h"

#define CHIP_ID     "60"
#define WEB_SERVER  "a21iot03.studev.groept.be"
#define WEB_PORT    "443"
#define WEB_URL     "https://a21iot03.studev.groept.be/public/api/esp/insertdata/"

static const char *HTTPS_TAG = "HTTPS";
static char request[200];

static int ret;
static int err_cnt;

static mbedtls_ssl_context ssl;
static mbedtls_entropy_context entropy;
static mbedtls_ctr_drbg_context ctr_drbg;
static mbedtls_x509_crt cacert;
static mbedtls_ssl_config conf;

void https_init(void);
void send_data(void);
