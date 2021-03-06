package com.coolweather.android;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasToString;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.test.SimpleIdlingResource;
import com.coolweather.android.util.Utility;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WeatherActivityTest {

    @Rule
    public ActivityTestRule<WeatherActivity> activityTestRule = new ActivityTestRule<>(WeatherActivity.class,true);

    SimpleIdlingResource mIdlingResource;

    @Before
    public void setup(){
        WeatherActivity activity = activityTestRule.getActivity();
        mIdlingResource = new SimpleIdlingResource(activity);//????????????????????????SimpleIdlingResource?????????????????????
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void mainUITest() throws InterruptedException {
        //????????????
        onView(withId(R.id.swipe_refresh)).perform(withCustomConstraints(swipeDown(),isDisplayingAtLeast(85)));//????????????
        Thread.sleep(1000);
        onView(withId(R.id.nav_button)).perform(click());
        onView(withId(R.id.list_view)).perform(swipeUp());
        Thread.sleep(1000);
        chooseAreaTest1();
        Thread.sleep(1000);
        chooseAreaTest2();
        onView(withId(R.id.swipe_refresh)).perform(withCustomConstraints(swipeDown(),isDisplayingAtLeast(85)));//????????????
        Thread.sleep(1000);
    }

    /**
     * ??????????????????
     */
    @Test
    public void refreshTest() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.swipe_refresh)).perform(withCustomConstraints(swipeDown(),isDisplayingAtLeast(85)));//????????????
        Thread.sleep(1000);
        titleTextShowTest();
        nowWeatherTextShowTest();
        forecastWeatherTextShowTest();
        suggestionTextTest();

        if (TestUtility.networkJudge(activityTestRule.getActivity().getBaseContext())){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity());
            Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
            BaseTestLogUtility.judgeResponse(weather.status);
        }else {
            failGetInfo();
        }

        Thread.sleep(1000);
    }

    /**
     * ????????????????????????
     */
    private void chooseAreaTest1() throws InterruptedException {
        onData(hasToString(startsWith("??????")))
                .inAdapterView(withId(R.id.list_view)).atPosition(0)
                .perform(click());
        Thread.sleep(1000);
        onData(hasToString(startsWith("??????")))
                .inAdapterView(withId(R.id.list_view)).atPosition(0)
                .perform(click());
        Thread.sleep(1000);
        onData(hasToString(startsWith("??????")))
                .inAdapterView(withId(R.id.list_view)).atPosition(0)
                .perform(click());
        if (!TestUtility.networkJudge(activityTestRule.getActivity().getBaseContext())){
            failGetInfo();
        }
    }

    /**
     * ???????????????????????????
     */
    private void chooseAreaTest2() throws InterruptedException {
        onView(withId(R.id.nav_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.back_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.back_button)).perform(click());
        Thread.sleep(1000);
        onData(hasToString(startsWith("??????")))
                .inAdapterView(withId(R.id.list_view)).atPosition(0)
                .perform(click());
        Thread.sleep(1000);
        onData(hasToString(startsWith("??????")))
                .inAdapterView(withId(R.id.list_view)).atPosition(0)
                .perform(click());
        Thread.sleep(1000);
        onData(hasToString(startsWith("??????")))
                .inAdapterView(withId(R.id.list_view)).atPosition(0)
                .perform(click());
        if (!TestUtility.networkJudge(activityTestRule.getActivity().getBaseContext())){
            failGetInfo();
        }
    }

    /**
     * sleep????????????????????????.?????????????????????
     * ??????????????????????????????89?????????espresso??????????????????????????????90???.
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     */
    public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return constraints;
            }

            @Override
            public String getDescription() {
                return action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                action.perform(uiController, view);
            }
        };
    }
    /**
     * ????????????listview??????Matcher
     * !!!?????????????????????
     */
    public static Matcher<Object> withItemContent(String expectedText) {
        checkNotNull(expectedText);
        return withItemContent(equalTo(expectedText));
    }
    /*
     * ?????????UI??????????????????
     */
    /**
     * ??????????????????????????????
     */
    @Test
    public void titleTextShowTest(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        onView(withId(R.id.title_city)).check(matches(withText(weather.basic.cityName)));
    }

    /**
     * ????????????????????????
     */
    @Test
    public void nowWeatherTextShowTest(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        onView(withId(R.id.degree_text)).check(matches(withText(weather.now.temperature+"???")));
        onView(withId(R.id.weather_info_text)).check(matches(withText(weather.now.more.info)));

        BaseTestLogUtility.judgeTem("????????????", Integer.parseInt(weather.now.temperature));
        BaseTestLogUtility.judgeInfo("????????????",weather.now.more.info);
    }

    /**
     * ??????????????????
     */
    @Test
    public void forecastWeatherTextShowTest(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        for (Forecast data : weather.forecastList){
            onView(withText(data.date)).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.max_text),hasSibling(withText(data.date)))).check(matches(withText(data.temperature.max)));
            onView(allOf(withId(R.id.min_text),hasSibling(withText(data.date)))).check(matches(withText(data.temperature.min)));
            onView(allOf(withId(R.id.info_text),hasSibling(withText(data.date)))).check(matches(withText(data.more.info)));

            BaseTestLogUtility.judgeTem("????????????????????????", Integer.parseInt(data.temperature.max));
            BaseTestLogUtility.judgeTem("????????????????????????", Integer.parseInt(data.temperature.min));
            BaseTestLogUtility.judgeInfo("????????????",weather.now.more.info);
        }
    }

    /**
     * ????????????????????????
     */
    @Test
    public void AQITextShowTest(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        onView(withId(R.id.aqi_text)).check(matches(withText(weather.aqi.city.aqi)));
        onView(withId(R.id.pm25_text)).check(matches(withText(weather.aqi.city.pm25)));
    }

    /**
     * ??????????????????
     */
    @Test
    public void suggestionTextTest(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        onView(withId(R.id.comfort_text)).check(matches(withText("????????????" + weather.suggestion.comfort.info)));
        onView(withId(R.id.car_wash_text)).check(matches(withText("???????????????" + weather.suggestion.carWash.info)));
        onView(withId(R.id.sport_text)).check(matches(withText("???????????????" + weather.suggestion.sport.info)));
    }

    /**
     * ??????????????????
     * @throws Exception
     */
    @Test
    public void loadImage() throws Exception {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity());
//        String url = "http://cn.bing.com/th?id=OHR.OmijimaIsland_ROW2080465862_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
        String url = prefs.getString("bing_pic",null);
        onView(withId(R.id.bing_pic_img)).check(matches(withContentDescription(url)));
        Thread.sleep(1000);
        onView(withId(R.id.nav_button)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
    }


    //////////////////////////////////////////////////////////////////////////////
    //********************???????????????????????????????????????????????????**************************//
    //////////////////////////////////////////////////////////////////////////////

    public void setup(ActivityTestRule rule,SimpleIdlingResource SIR){
        WeatherActivity activity = (WeatherActivity) rule.getActivity();
        SIR = new SimpleIdlingResource(activity);//????????????????????????SimpleIdlingResource?????????????????????
        IdlingRegistry.getInstance().register(SIR);
    }
    /**
     * ??????????????????????????????
     */
    public void titleTextShowTest(ActivityTestRule rule){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(rule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        onView(withId(R.id.title_city)).check(matches(withText(weather.basic.cityName)));
    }

    /**
     * ????????????????????????
     */
    public void nowWeatherTextShowTest(ActivityTestRule rule){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(rule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        onView(withId(R.id.degree_text)).check(matches(withText(weather.now.temperature+"???")));
        onView(withId(R.id.weather_info_text)).check(matches(withText(weather.now.more.info)));

        BaseTestLogUtility.judgeTem("????????????", Integer.parseInt(weather.now.temperature));
        BaseTestLogUtility.judgeInfo("????????????",weather.now.more.info);
    }

    /**
     * ??????????????????
     */
    public void forecastWeatherTextShowTest(ActivityTestRule rule){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(rule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        for (Forecast data : weather.forecastList){
            onView(withText(data.date)).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.max_text),hasSibling(withText(data.date)))).check(matches(withText(data.temperature.max)));
            onView(allOf(withId(R.id.min_text),hasSibling(withText(data.date)))).check(matches(withText(data.temperature.min)));
            onView(allOf(withId(R.id.info_text),hasSibling(withText(data.date)))).check(matches(withText(data.more.info)));

            BaseTestLogUtility.judgeTem("????????????????????????", Integer.parseInt(data.temperature.max));
            BaseTestLogUtility.judgeTem("????????????????????????", Integer.parseInt(data.temperature.min));
            BaseTestLogUtility.judgeInfo("????????????",weather.now.more.info);
        }
    }

    /**
     * ????????????????????????
     */
    public void AQITextShowTest(ActivityTestRule rule){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(rule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        onView(withId(R.id.aqi_text)).check(matches(withText(weather.aqi.city.aqi)));
        onView(withId(R.id.pm25_text)).check(matches(withText(weather.aqi.city.pm25)));
    }

    /**
     * ??????????????????
     */
    public void suggestionTextTest(ActivityTestRule rule){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(rule.getActivity());
        Weather weather = Utility.handleWeatherResponse(sp.getString("weather",null));
        onView(withId(R.id.comfort_text)).check(matches(withText("????????????" + weather.suggestion.comfort.info)));
        onView(withId(R.id.car_wash_text)).check(matches(withText("???????????????" + weather.suggestion.carWash.info)));
        onView(withId(R.id.sport_text)).check(matches(withText("???????????????" + weather.suggestion.sport.info)));
    }

    /**
     * ??????????????????
     * @throws Exception
     */
    public void loadImage(ActivityTestRule rule) throws Exception {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(rule.getActivity());
//        String url = "http://cn.bing.com/th?id=OHR.OmijimaIsland_ROW2080465862_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
        String url = prefs.getString("bing_pic",null);
        onView(withId(R.id.bing_pic_img)).check(matches(withContentDescription(url)));
        Thread.sleep(1000);
        onView(withId(R.id.nav_button)).perform(click());
    }

    /**
     * ??????
     * @param SIR
     */
    public void tearDown(SimpleIdlingResource SIR) {
        IdlingRegistry.getInstance().unregister(SIR);
    }

    /**
     * ??????????????????
     */
    public void openSet(){
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setClassName("com.android.phone","com.android.phone.MobileNetworkSettings");
        activityTestRule.getActivity().startActivity(intent);
    }

    public void failGetInfo(){
        onView(withText("????????????????????????"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        BaseTestLogUtility.showWeatherActivityTestSituation("????????????????????????");
        BaseTestLogUtility.judgeResponse("");
    }

}