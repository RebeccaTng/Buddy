#include <sys/cdefs.h>
#include "audio.h"

static int play_doremi() {
    int index = 0;
    static const double notes[DOREMI_LENGTH+2] = {
            PAUSE_NOTE,
            C5_NOTE,
            D5_NOTE,
            E5_NOTE,
            F5_NOTE,
            G5_NOTE,
            A5_NOTE,
            B5_NOTE,
            C6_NOTE,
            PAUSE_NOTE
        };

    static const int durations[DOREMI_LENGTH+2] = {
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR,
            EIGHT_DUR
        };

    int totalDur = 0;
    while(totalDur < ms_counter) {
        totalDur += durations[index];
        if(index >= DOREMI_LENGTH) {
            ms_counter = 1;
            us_counter = 1;
        }
        index++;
    }
    return (int) notes[index];
}

static bool IRAM_ATTR on_timer_alarm_cb(gptimer_handle_t timer, const gptimer_alarm_event_data_t *edata, void *user_data)
{
    us_counter++;
    if(us_counter > 100) {
        ms_counter++;
        us_counter = 1;
    }

    int result = play_doremi();
    if(result != 1) {
        int points = (int) (1000000 / (TIMER_INTR_US * result) + 0.5);

        int *head = (int *) user_data;
        if (g_index >= points)
            g_index = 0;

        dac_output_voltage(DAC_CHAN, *(head + g_index));
        g_index++;
    }
    return false;
}

_Noreturn TaskFunction_t audio_init(void)
{
    while(1) {
        g_index = 0;
        gptimer_handle_t gptimer = NULL;
        gptimer_config_t timer_config = {
                .clk_src = GPTIMER_CLK_SRC_APB,
                .direction = GPTIMER_COUNT_UP,
                .resolution_hz = 1000000, // 1MHz, 1 tick = 1us
        };
        (gptimer_new_timer(&timer_config, &gptimer));
        (dac_output_enable(DAC_CHAN));

        gptimer_alarm_config_t alarm_config = {
                .reload_count = 0,
                .alarm_count = TIMER_INTR_US,
                .flags.auto_reload_on_alarm = true,
        };
        gptimer_event_callbacks_t cbs = {
                .on_alarm = on_timer_alarm_cb,
        };
        (gptimer_register_event_callbacks(gptimer, &cbs, raw_val));
        (gptimer_set_alarm_action(gptimer, &alarm_config));
        (gptimer_start(gptimer));
    }
}
