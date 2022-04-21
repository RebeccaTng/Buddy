#include "config.h"

#define WEB_SERVER "a21iot03.studev.groept.be"
#define WEB_PORT "443"
#define WEB_URL "https://a21iot03.studev.groept.be/public/api/esp/insert/working"

static const char *HTTPS_TAG = "HTTPS";

static const char *REQUEST = "GET " WEB_URL " HTTP/1.0\r\n"
                             "Host: "WEB_SERVER"\r\n"
                             "User-Agent: esp-idf/1.0 esp32\r\n"
                             "\r\n";

void send_data(void *pvParameters);
void https_init(void);
