#include "config.h"
#include "adc.h"

#define WEB_SERVER "a21iot03.studev.groept.be"
#define WEB_PORT "443"
#define WEB_URL "https://a21iot03.studev.groept.be/public/api/esp/insert/"

static const char *HTTPS_TAG = "HTTPS";

_Noreturn void https_init(void);
