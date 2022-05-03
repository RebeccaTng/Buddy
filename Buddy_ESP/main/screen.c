#include "screen.h"
#include "font.h"

void ssd1306_init(SSD1306_t * dev, int width, int height)
{
	i2c_init(dev, width, height);
}

void ssd1306_display_text(SSD1306_t * dev, int page, char * text, int text_len, bool invert)
{
	if (page >= dev->_pages) return;
	int _text_len = text_len;
	if (_text_len > 16) _text_len = 16;

	uint8_t seg = 0;
	uint8_t image[8];
	for (uint8_t i = 0; i < _text_len; i++) {
		memcpy(image, font8x8_basic_tr[(uint8_t)text[i]], 8);
		if (invert) ssd1306_invert(image, 8);
		if (dev->_flip) ssd1306_flip(image, 8);
		i2c_display_image(dev, page, seg, image, 8);
		seg = seg + 8;
	}
}

void ssd1306_clear_screen(SSD1306_t * dev, bool invert)
{
    char space[16];
    memset(space, 0x20, sizeof(space));
    for (int page = 0; page < dev->_pages; page++) {
        ssd1306_display_text(dev, page, space, sizeof(space), invert);
    }
}

void ssd1306_clear_line(SSD1306_t * dev, int page, bool invert)
{
    char space[16];
    memset(space, 0x20, sizeof(space));
    ssd1306_display_text(dev, page, space, sizeof(space), invert);
}

void ssd1306_contrast(SSD1306_t * dev, int contrast)
{
    i2c_contrast(dev, contrast);
}

void ssd1306_invert(uint8_t *buf, size_t blen)
{
	uint8_t wk;
	for(int i=0; i<blen; i++){
		wk = buf[i];
		buf[i] = ~wk;
	}
}

void ssd1306_flip(uint8_t *buf, size_t blen)
{
	for(int i=0; i<blen; i++){
		buf[i] = ssd1306_rotate(buf[i]);
	}
}

uint8_t ssd1306_rotate(uint8_t ch1) {
	uint8_t ch2 = 0;
	for (int j=0;j<8;j++) {
		ch2 = (ch2 << 1) + (ch1 & 0x01);
		ch1 = ch1 >> 1;
	}
	return ch2;
}