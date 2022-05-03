#include "https.h"

_Noreturn void https_init(void) {
    char buf[512];
    int ret, flags, len;

    mbedtls_entropy_context entropy;
    mbedtls_ctr_drbg_context ctr_drbg;
    mbedtls_ssl_context ssl;
    mbedtls_x509_crt cacert;
    mbedtls_ssl_config conf;
    mbedtls_net_context server_fd;

    mbedtls_ssl_init(&ssl);
    mbedtls_x509_crt_init(&cacert);
    mbedtls_ctr_drbg_init(&ctr_drbg);
    ESP_LOGI(HTTPS_TAG, "Seeding the random number generator");

    mbedtls_ssl_config_init(&conf);

    mbedtls_entropy_init(&entropy);
    if ((ret = mbedtls_ctr_drbg_seed(&ctr_drbg, mbedtls_entropy_func, &entropy,
                                     NULL, 0)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_ctr_drbg_seed returned %d", ret);
    }
    ESP_LOGI(HTTPS_TAG, "Attaching the certificate bundle...");

    ret = esp_crt_bundle_attach(&conf);
    if (ret < 0) {
        ESP_LOGE(HTTPS_TAG, "esp_crt_bundle_attach returned -0x%x\n\n", -ret);
        
    }
    ESP_LOGI(HTTPS_TAG, "Setting hostname for TLS session...");

    if ((ret = mbedtls_ssl_set_hostname(&ssl, WEB_SERVER)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_set_hostname returned -0x%x", -ret);
    }
    ESP_LOGI(HTTPS_TAG, "Setting up the SSL/TLS structure...");

    if ((ret = mbedtls_ssl_config_defaults(&conf,
                                           MBEDTLS_SSL_IS_CLIENT,
                                           MBEDTLS_SSL_TRANSPORT_STREAM,
                                           MBEDTLS_SSL_PRESET_DEFAULT)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_config_defaults returned %d", ret);
    }

    mbedtls_ssl_conf_authmode(&conf, MBEDTLS_SSL_VERIFY_OPTIONAL);
    mbedtls_ssl_conf_ca_chain(&conf, &cacert, NULL);
    mbedtls_ssl_conf_rng(&conf, mbedtls_ctr_drbg_random, &ctr_drbg);

    if ((ret = mbedtls_ssl_setup(&ssl, &conf)) != 0) {
        ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_setup returned -0x%x\n\n", -ret);
        
    }

    while(1) {
        if(https_ready == true) {
            char request[200];
            sprintf(request, "GET " WEB_URL "%d,%d,%d,%d HTTP/1.0\r\n"
                            "Host: " WEB_SERVER "\r\n"
                            "User-Agent: esp-idf/1.0 esp32\r\n"
                            "\r\n", temperature, moisture, distance, light);

            ESP_LOGW("request", "%s", request);

            mbedtls_net_init(&server_fd);
            ESP_LOGI(HTTPS_TAG, "Connecting to %s:%s...", WEB_SERVER, WEB_PORT);

            if ((ret = mbedtls_net_connect(&server_fd, WEB_SERVER, WEB_PORT, MBEDTLS_NET_PROTO_TCP)) != 0) {
                ESP_LOGE(HTTPS_TAG, "mbedtls_net_connect returned -%x", -ret);

            }
            ESP_LOGI(HTTPS_TAG, "Connected.");
            mbedtls_ssl_set_bio(&ssl, &server_fd, mbedtls_net_send, mbedtls_net_recv, NULL);

            ESP_LOGI(HTTPS_TAG, "Performing the SSL/TLS handshake...");
            ret = mbedtls_ssl_handshake(&ssl);
            if (ret != 0 && ret != MBEDTLS_ERR_SSL_WANT_READ && ret != MBEDTLS_ERR_SSL_WANT_WRITE) {
                ESP_LOGE(HTTPS_TAG, "Mbed TLS Error!");
                ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_handshake returned -0x%x", -ret);
                https_ready = false;
                continue;
            }

            ESP_LOGI(HTTPS_TAG, "Verifying peer X.509 certificate...");

            if ((flags = mbedtls_ssl_get_verify_result(&ssl)) != 0) {
                ESP_LOGW(HTTPS_TAG, "Failed to verify peer certificate!");
                bzero(buf, sizeof(buf));
                mbedtls_x509_crt_verify_info(buf, sizeof(buf), "  ! ", flags);
                ESP_LOGW(HTTPS_TAG, "verification info: %s", buf);
            } else {
                ESP_LOGI(HTTPS_TAG, "Certificate verified.");
            }
            ESP_LOGI(HTTPS_TAG, "Cipher suite is %s", mbedtls_ssl_get_ciphersuite(&ssl));

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
                }
            } while (written_bytes < strlen(request));
            ESP_LOGI(HTTPS_TAG, "Reading HTTP response...");

            do {
                len = sizeof(buf) - 1;
                bzero(buf, sizeof(buf));
                ret = mbedtls_ssl_read(&ssl, (unsigned char *) buf, len);

                if (ret == MBEDTLS_ERR_SSL_WANT_READ || ret == MBEDTLS_ERR_SSL_WANT_WRITE)
                    continue;

                if (ret == MBEDTLS_ERR_SSL_PEER_CLOSE_NOTIFY) {
                    ret = 0;
                    break;
                }

                if (ret < 0) {
                    ESP_LOGE(HTTPS_TAG, "mbedtls_ssl_read returned -0x%x", -ret);
                    break;
                }

                if (ret == 0) {
                    ESP_LOGI(HTTPS_TAG, "connection closed");
                    break;
                }

                len = ret;
                ESP_LOGD(HTTPS_TAG, "%d bytes read", len);
                /* Print response directly to stdout as it is read */
                for (int i = 0; i < len; i++) {
                    putchar(buf[i]);
                }
            } while (1);

            mbedtls_ssl_close_notify(&ssl);

            if (ret != 0) {
                mbedtls_strerror(ret, buf, 100);
                ESP_LOGE(HTTPS_TAG, "Error: -0x%x - %s", -ret, buf);
            }
            mbedtls_ssl_session_reset(&ssl);
            mbedtls_net_free(&server_fd);

            printf("Available heap size: %dB\n", esp_get_minimum_free_heap_size());
            vTaskDelay(1000 / portTICK_PERIOD_MS);
            https_ready = false;
        }
    }
}
