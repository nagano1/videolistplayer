#include <string>
#include <mutex>
#include <thread>

#include <cstdlib>
#include <cassert>
#include <cstdio>
#include <chrono>
#include <sstream>

#include <cstdint>
#include <ctime>
#include <time.h>
#include <android/log.h>


#include "Test.h"

long Test2::g = 59;


int func() {
    std::unique_ptr<int> a;
    return 532;
}


static std::mutex main_sleeper_mutex;
static std::atomic<bool> main_sleeper_ready{false};
//static bool main_sleeper_ready{ false };
static std::condition_variable main_sleeper_cond;
static std::atomic<bool> is_waiting{false};


static std::mutex wait_back_mutex;
static std::condition_variable wait_back_cond;
static bool wait_back_ready;


static std::atomic<uint64_t> wait_start_time;
static JsCallFromBackground *current_wait_item = nullptr;

static std::mutex entry_lock_mutex;

static std::atomic<bool> is_waitback_request{false};

static std::atomic<bool> is_serving{false};


static bool is_boost_waiting{false};
static uint64_t client_boost_index{0};
static std::atomic<uint64_t> boost_accept_index{1};
static uint64_t server_boost_index{1};
static std::atomic<bool> boost_request_accepted{false};
static bool boost_server_executed{false};
static bool boost_client_complete{false};
static std::atomic<bool> boost_client_accepted{false};

static std::atomic<int *> pointer_atomic{nullptr};

static JsCallFromBackground *boost_request_item;
static int *request_token_address = nullptr;
static int *accepted_token_address = nullptr;

struct Node {
    Node *next;
};

std::atomic<int *> head{nullptr};

#if defined(OS_MACOSX)
constexpr bool BOOST_ENABLED{ true };
constexpr int BOOST_WAIT_TIME{ 7777 };
#else
constexpr bool BOOST_ENABLED{true};
//constexpr int BOOST_WAIT_TIME{3880};
constexpr int BOOST_WAIT_TIME{388000};

#endif

#define NANOS_IN_SECOND 1000000000


static long currentTimeInMicros() {
    /*
    struct timeval tv;
    gettimeofday (&tv, NULL);
    return tv.tv_sec*1000000+ tv.tv_usec;
    */

    int aa = 523;
    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return (res.tv_sec * NANOS_IN_SECOND) + res.tv_nsec;
}

static int count = 0;





static int canDoUiCode(JsCallFromBackground *item) {
    int address_holder;
    //for (int a = 0; a < 4000000; a++) {
    while (true) {
        //__android_log_print(ANDROID_LOG_DEBUG,"Tagoaa","geeze");


        if (is_boost_waiting) {
            auto this_client_boost_index = client_boost_index;
            auto this_server_index = server_boost_index;
            if ((this_client_boost_index < this_server_index)) {

                //if (is_serving) {
                //if (uv_hrtime() - wait_start_time + 10 * 1000 > static_cast<uint64_t>(current_sleep_nanoseconds)) {
//                    return PyVars::JsExceptionHolder;
//                }
                //}
                /*
                if (client_boost_index.compare_exchange_strong(this_client_boost_index,
                                                               this_server_index)) {
                    // done
                } else {
                    // failed
                    continue;
                }
                */

                boost_request_item = item;
                int *this_address = &address_holder;
                request_token_address = this_address;
                client_boost_index = this_server_index;


                while (true) {

                    bool shouldBreak = false;

                    int *temp_accepted = accepted_token_address;
                    if (temp_accepted) {

                        if (temp_accepted == this_address) {
                            for (int mm = 0; mm < 2; mm++) {

                            }
                            // won!
                            while (true) {

                                if (boost_server_executed) {
                                    boost_client_complete = true;
                                    shouldBreak = true;
                                    break;
                                }


                                if (this_server_index != server_boost_index) {
//                                __android_log_print(ANDROID_LOG_DEBUG,"Tagoaa","this?");

                                    break;
                                }
                            }

                        } else {
                            break;
                        }
                    }

                    if (shouldBreak) {
                        break;
                    }

                    if (this_server_index != server_boost_index) {
                        //__android_log_print(ANDROID_LOG_DEBUG,"Tagoaa","this A?");

                        break;
                    }
                    /*
                    if (using_sleep_mode) {
                        std::this_thread::sleep_for(std::chrono::microseconds(1000));
                    } else {
                        if (wait_count++ % 10000 == 7777) {
                            if (currentTimeInMicros() - complete_start_time > 3 * 1000) {
                                // js process takes a few milli seconds,
                                // now it's worth using sleep with 1 milli seconds
                                using_sleep_mode = true;
                            }
                        }
                    }

                    if (this_time_accept_index != boost_accept_index) {
                        //if (server_boost_index.load() != client_boost_index.load()) {
                        //DCHECK(CustomModuleManager::consoleInfo("break"));
                        break;
                    }
                     */
                }
            }
        }
    }

    __android_log_print(ANDROID_LOG_DEBUG, "Tagoaa", "end");


    return 32;
}


static int64_t wait_remain;

static void request_next_skip() {
    is_serving = true;

    int64_t start_wait_remain = wait_remain;
    {
        int gcount = 0;
        bool might_have_js_access = false;
        bool first = true;
        int ag = 0;

        while (ag == 3 * 0) {
//            __android_log_print(ANDROID_LOG_DEBUG,"Tagoaa","geeze");

            // bool is_accepted = false;
            server_boost_index++;
            request_token_address = nullptr;
            accepted_token_address = nullptr;
            is_boost_waiting = true;

            for (int i = 0; i < BOOST_WAIT_TIME; i++) {// 6666 3880
                if (client_boost_index == server_boost_index) {
                    boost_server_executed = false;

                    is_boost_waiting = false;

                    int *this_req = request_token_address;
                    if (this_req) {
                        accepted_token_address = this_req;


                        count++;
                        if (count % 1000000 == 5) {
                            __android_log_print(ANDROID_LOG_DEBUG, "Tagoaa", "p=%d", count);
                        }
                        //  boost_request_accepted = true;

                        //is_accepted = true;
                        //std::this_thread::sleep_for(std::chrono::microseconds(1000));

                        /*
                        if (item->ui_get_done == false) {
                            //item->ui_get_done = true;
                            //item->ui_getjs();
                        }
                         */

                        boost_server_executed = true;
                        //for (int k = 0; k < 10; k++) {

                        break;
                    }
                }
            }

            //server_boost_index++;
/*
            std::unique_lock<std::mutex> lock{main_sleeper_mutex};

            {
                if (main_sleeper_ready == true) {
                    main_sleeper_ready = false;
                    late = 0;
                    break;
                }

                is_waiting = true;
                auto notified = main_sleeper_cond.wait_for(lock,
                                                           std::chrono::nanoseconds(wait_remain),
                                                           [] { return main_sleeper_ready == true; }
                );
                is_waiting = false;
                main_sleeper_ready = false;

                if (notified) {
                    if (is_waitback_request == true) {
                        is_waitback_request = false;

                        {
                            std::lock_guard<std::mutex> lock4{wait_back_mutex};

                            if (!current_wait_item->ui_get_done) {


                                current_wait_item->ui_get_done = true;
                                //current_wait_item->ui_getjs();
                            }

                            wait_back_ready = true;
                            wait_back_cond.notify_all();
                        }

                        wait_remain = start_wait_remain - (uv_hrtime() - wait_start_time);

                        if (wait_remain > 500 * 1000) {
                            count++;
                            continue;
                        }
                    }

                    //late = 0;
                } else {
                    //late = current_skip_ms;
                }
            }

            break;*/
        }

        //printf("c=%d,", count);
        //printf("g=%d,", gcount);
        //printf("late=%d,", late);
        //printf("wait_remain=%lld\n", wait_remain);
        //fflush(stdout);

    }


    is_serving = false;

}

long funca() {
    //auto elapsed = std::chrono::high_resolution_clock::now() - start;

    //long long microseconds = std::chrono::duration_cast<std::chrono::microseconds>(elapsed).count();
    std::thread thread1([]() {
        request_next_skip();
    });
    std::thread thread2([]() {
        JsCallFromBackground *item = new JsCallFromBackground();
        canDoUiCode(item);
    });


    thread1.join();
    thread2.join();
    //long long ti_after = funca();

    //long long diff = ti_after - ti;

    //__android_log_print(ANDROID_LOG_DEBUG,"Tagoaa","%llu", diff);


    //return currentTimeInMicros();
}