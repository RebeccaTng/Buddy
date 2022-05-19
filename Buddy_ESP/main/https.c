#include "https.h"
#include "i2c.h"

static void display_bad_wifi(void) {
    ssd1306_display_text(&dev, 0, "Bad WiFi Creds!!", 16, false);
    ssd1306_display_text(&dev, 1, "                ", 16, false);
    ssd1306_display_text(&dev, 2, "Make sure WiFi's", 16, false);
    ssd1306_display_text(&dev, 3, "correctly workin", 16, false);
    ssd1306_display_text(&dev, 4, "                ", 16, false);
    ssd1306_display_text(&dev, 5, "If so, pass the ", 16, false);
    ssd1306_display_text(&dev, 6, "correct creds :)", 16, false);
}

static void https_error(void) {
    static char* https_err = "HTTPS ERR";
    ESP_LOGE(https_err, "Decimal Error Code: %d", ret);
    switch (ret) {
        case -69:
            ESP_LOGE(https_err, WIFI_TURNED_OFF_ERR);
            break;
        case -76:
            ESP_LOGE(https_err, SOCKET_READING_ERR);
            break;
        case -82:
            ESP_LOGE(https_err, BAD_WIFI_CREDS_ERR);
            display_bad_wifi();
            vTaskDelay(pdMS_TO_TICKS(10000));
            esp_restart();
        case -12288:
            ESP_LOGE(https_err, VERIFY_CALLBACK_ERR);
            break;
        case -28800:
            ESP_LOGE(https_err, FEATURE_NOT_AV_ERR);
            break;
        case -28928:
            ESP_LOGE(https_err, BAD_INP_PARAMS_ERR);
            break;
        default:
            ESP_LOGE(https_err, "Unknown Error");
            break;
    }
    if (err_cnt++ > 3)
        esp_restart();
}

static bool read_https() {
    bool br1 = false;
    bool br2 = false;
    bool start_reading = false;

    int len;
    char buf[512];

    ESP_LOGI(HTTPS_TAG, "Reading HTTP response...");
    do {
        len = sizeof(&buf) - 1;
        bzero(buf, sizeof(&buf));
        ret = mbedtls_ssl_read(&ssl, (unsigned char *) buf, len);

        if (ret == MBEDTLS_ERR_SSL_WANT_READ || ret == MBEDTLS_ERR_SSL_WANT_WRITE)
            continue;
        else if (ret == MBEDTLS_ERR_SSL_PEER_CLOSE_NOTIFY) {
            ret = 0;
            break;
        } else if (ret < 0) {
            ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_read returned -0x%x", -ret);
            https_error();
            return false;
        } else if (ret == 0) {
            ESP_LOGI(HTTPS_TAG, "Connection closed");
            return false;
        } else {
            for(int i = 0; i < ret; i++) {
                if(buf[i] == '\n') {
                    if(br2 == true) {
                        start_reading = true;
                    }
                    br1 = true;
                    br2 = true;
                } else {
                    if(br1 == false)
                        br2 = false;
                    br1 = false;
                }
                if(start_reading == true) {
                    char c = buf[i];
                    strncat(response, &c, 1);
                }
            }
        }
    } while (1);

    ESP_LOGI(HTTPS_TAG, "Response: %s", response);
    mbedtls_ssl_close_notify(&ssl);

    if (ret != 0) {
        mbedtls_strerror(ret, buf, 100);
        ESP_LOGE(HTTPS_TAG, "Error: -0x%x - %s", -ret, buf);
        https_error();
        return false;
    }

    return true;
}

static bool write_https(void) {
    ESP_LOGI(HTTPS_TAG, "Writing HTTP request...");
    size_t written_bytes = 0;
    do {
        ret = mbedtls_ssl_write(&ssl,
                                (const unsigned char *) request + written_bytes,
                                strlen(request) - written_bytes);
        if (ret >= 0) {
            ESP_LOGI(HTTPS_TAG, "%d bytes written", ret);
            written_bytes += ret;
        } else if (ret != MBEDTLS_ERR_SSL_WANT_WRITE && ret != MBEDTLS_ERR_SSL_WANT_READ) {
            ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_write returned -0x%x", -ret);
            https_error();
            return false;
        }
    } while (written_bytes < strlen(request));
    return true;
}

void https_init() {
    err_cnt = 0;
    mbedtls_ssl_init(&ssl);
    mbedtls_x509_crt_init(&cacert);
    mbedtls_ctr_drbg_init(&ctr_drbg);

    ESP_LOGI(HTTPS_TAG, "Setting up HTTPS...");
    mbedtls_ssl_config_init(&conf);
    mbedtls_entropy_init(&entropy);

    if ((ret = mbedtls_ctr_drbg_seed(&ctr_drbg, mbedtls_entropy_func, &entropy,NULL, 0)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_ctr_drbg_seed returned %d", ret);
        https_error();
    }

    ret = esp_crt_bundle_attach(&conf);
    if (ret < 0) {
        ESP_LOGE(HTTPS_TAG, "esp_crt_bundle_attach returned -0x%x\n\n", -ret);
        https_error();
    }

    if ((ret = mbedtls_ssl_set_hostname(&ssl, WEB_SERVER)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_set_hostname returned -0x%x", -ret);
        https_error();
    }

    if ((ret = mbedtls_ssl_config_defaults(&conf,MBEDTLS_SSL_IS_CLIENT,MBEDTLS_SSL_TRANSPORT_STREAM,MBEDTLS_SSL_PRESET_DEFAULT)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_config_defaults returned %d", ret);
        https_error();
    }

    mbedtls_ssl_conf_authmode(&conf, MBEDTLS_SSL_VERIFY_OPTIONAL);
    mbedtls_ssl_conf_ca_chain(&conf, &cacert, NULL);
    mbedtls_ssl_conf_rng(&conf, mbedtls_ctr_drbg_random, &ctr_drbg);

    if ((ret = mbedtls_ssl_setup(&ssl, &conf)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_setup returned -0x%x\n\n", -ret);
        https_error();
    }
}

static void verify_cert(mbedtls_net_context server_fd) {
    char buf[512];
    int flags;

    mbedtls_ssl_set_bio(&ssl, &server_fd, mbedtls_net_send, mbedtls_net_recv, NULL);
    ret = mbedtls_ssl_handshake(&ssl);
    if (ret != 0 && ret != MBEDTLS_ERR_SSL_WANT_READ && ret != MBEDTLS_ERR_SSL_WANT_WRITE) {
        ESP_LOGE(HTTPS_TAG, "Mbed TLS Error!");
        ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_handshake returned -0x%x", -ret);
        https_error();
    } else {
        ESP_LOGI(HTTPS_TAG, "Connected, verifying certificate...");

        if ((flags = mbedtls_ssl_get_verify_result(&ssl)) != 0) {
            ESP_LOGE(HTTPS_TAG, "Failed to verify peer certificate!");
            bzero(buf, sizeof(buf));
            mbedtls_x509_crt_verify_info(buf, sizeof(buf), "  ! ", flags);
        } else {
            ESP_LOGI(HTTPS_TAG, "Certificate verified.");
        }

        if(write_https() && read_https())
            err_cnt = 0;

        mbedtls_ssl_session_reset(&ssl);
        mbedtls_net_free(&server_fd);
        printf("Available heap size: %dB\n", esp_get_minimum_free_heap_size());
    }
}

static void send_request(void) {
    mbedtls_net_context server_fd;
    ESP_LOGI(HTTPS_TAG, "%s", request);

    mbedtls_net_init(&server_fd);
    if ((ret = mbedtls_net_connect(&server_fd, WEB_SERVER, WEB_PORT, MBEDTLS_NET_PROTO_TCP)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_net_connect returned -%x", -ret);
        https_error();
    } else {
        verify_cert(server_fd);
    }
}

void insert_sensor_data(unsigned int moisture, unsigned int light, unsigned int temperature, unsigned int distance, char* status) {
    sprintf(request, "GET " WEB_URL IN_DATA CHIP_ID "/%d/%d/%d/%d/%s HTTP/1.0\r\n"
                     "Host: " WEB_SERVER "\r\n"
                     "User-Agent: esp-idf/1.0 esp32\r\n"
                     "\r\n", moisture, light, temperature, distance, status);
    send_request();
}

void insert_water() {
    sprintf(request, "GET " WEB_URL IN_WATER CHIP_ID " HTTP/1.0\r\n"
                     "Host: " WEB_SERVER "\r\n"
                     "User-Agent: esp-idf/1.0 esp32\r\n"
                     "\r\n");
    send_request();
}

char* get_day(void) {
    sprintf(request, "GET " WEB_URL GET_TIME " HTTP/1.0\r\n"
                     "Host: " WEB_SERVER "\r\n"
                     "User-Agent: esp-idf/1.0 esp32\r\n"
                     "\r\n");
    send_request();

    strtok(response, "-");
    strtok(NULL, "-");
    return  strtok(NULL, " ");
}

char* get_tank(void) {
    sprintf(request, "GET " WEB_URL GET_TANK CHIP_ID " HTTP/1.0\r\n"
                     "Host: " WEB_SERVER "\r\n"
                     "User-Agent: esp-idf/1.0 esp32\r\n"
                     "\r\n");
    send_request();
    return response;
}
