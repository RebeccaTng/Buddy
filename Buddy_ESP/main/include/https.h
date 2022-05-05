#include "config.h"
#include "adc.h"

#define WEB_SERVER "a21iot03.studev.groept.be"
#define WEB_PORT "443"
#define WEB_URL "https://a21iot03.studev.groept.be/public/api/esp/insert/"

static const char *HTTPS_TAG = "HTTPS";
static int ret;

static mbedtls_ssl_context ssl;
static mbedtls_entropy_context entropy;
static mbedtls_ctr_drbg_context ctr_drbg;
static mbedtls_x509_crt cacert;
static mbedtls_ssl_config conf;

void https_init(void);
void send_data(void);
