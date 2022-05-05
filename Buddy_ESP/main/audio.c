#include <sys/cdefs.h>
#include "audio.h"

static int get_active_note(const double notes[AUDIO_LENGTH+2], const int durations[AUDIO_LENGTH+2]) {
    int index = 0;
    int totalDur = 0;
    while(totalDur < ms_counter) {
        totalDur += durations[index];
        if(index >= AUDIO_LENGTH) {
            ms_counter = 0;
            us_counter = 0;
            gptimer_stop(gptimer);
            gptimer_del_timer(gptimer);
            dac_output_disable(DAC_CHAN);
            sound_finished = true;
        }
        index++;
    }
    return (int) notes[index];
}

static int play_doremi() {
    static const double notes[AUDIO_LENGTH+2] = {
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

    static const int durations[AUDIO_LENGTH+2] = {
            QUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
    };

    return get_active_note(notes, durations);
}

static int play_start() {
    static const double notes[AUDIO_LENGTH+2] = {
            PAUSE_NOTE,
            Ab4_NOTE,
            Bb4_NOTE,
            Eb5_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE
    };

    static const int durations[AUDIO_LENGTH+2] = {
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            QUAVER_DUR,
            QUAVER_DUR,
            QUARTER_DUR,
            QUARTER_DUR,
            SKIP_DUR,
            SKIP_DUR,
    };

    return get_active_note(notes, durations);
}

static int play_bluetooth() {
    static const double notes[AUDIO_LENGTH+2] = {
            PAUSE_NOTE,
            C6_NOTE,
            PAUSE_NOTE,
            C6_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE
    };

    static const int durations[AUDIO_LENGTH+2] = {
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            QUARTER_DUR,
            QUARTER_DUR,
            QUARTER_DUR,
            QUARTER_DUR,
            SKIP_DUR,
    };
    return get_active_note(notes, durations);
}

static int play_https() {
    static const double notes[AUDIO_LENGTH+2] = {
            PAUSE_NOTE,
            C5_NOTE,
            E5_NOTE,
            F5_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE,
            PAUSE_NOTE
    };

    static const int durations[AUDIO_LENGTH+2] = {
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            SEMIQUAVER_DUR,
            QUARTER_DUR,
            QUARTER_DUR,
            QUARTER_DUR,
            QUARTER_DUR,
            SKIP_DUR,
    };
    return get_active_note(notes, durations);
}

static bool IRAM_ATTR on_timer_alarm_cb(void *data)
{
    int result;
    us_counter++;
    if(us_counter > 100) {
        ms_counter++;
        us_counter = 1;
    }

    switch (song_id) {
        case 0:
            result = play_start();
            break;
        case 1:
            result = play_bluetooth();
            break;
        case 2:
            result = play_https();
            break;
        default:
            result = play_doremi();
            break;
    }

    if(result != 1) {
        int points = (int) (1000000.0 / (TIMER_INTR_US * result) + 0.5);

        int *head = (int *) &data[0];
        if (g_index >= points)
            g_index = 0;

        dac_output_voltage(DAC_CHAN, *(head + g_index));
        g_index++;
    }
    return false;
}

void audio_init(int song_id_)
{
    song_id = song_id_;
    g_index = 0;
    sound_finished = false;

    gptimer = NULL;
    gptimer_config_t timer_config = {
            .clk_src = GPTIMER_CLK_SRC_APB,
            .direction = GPTIMER_COUNT_UP,
            .resolution_hz = 1000000, // 1MHz, 1 tick = 1us
    };

    gptimer_new_timer(&timer_config, &gptimer);
    dac_output_enable(DAC_CHAN);

    gptimer_alarm_config_t alarm_config = {
            .reload_count = 0,
            .alarm_count = TIMER_INTR_US,
            .flags.auto_reload_on_alarm = true,
    };

    gptimer_event_callbacks_t cbs = {
            .on_alarm = (gptimer_alarm_cb_t) on_timer_alarm_cb,
    };

    gptimer_register_event_callbacks(gptimer, &cbs, raw_val);
    gptimer_set_alarm_action(gptimer, &alarm_config);
    gptimer_start(gptimer);
    while(!sound_finished);
}
