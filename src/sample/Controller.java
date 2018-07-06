package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/*
import javax.speech.*;
import java.util.*;
import javax.speech.synthesis.*;
*/

import com.kkingsbe.yahooweatherapi.yahooWeather;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class Controller {
    public Label timeLabel;
    public Label conditionLabel;
    public Label temperatureLabel;
    public Rectangle nightTheme;
    public Rectangle morningTheme;
    public Rectangle eveningTheme;
    public Rectangle dayTheme;
    public Label headlinesLabel;
    public Label smallClock;
    public Label headlinesActual;
    public int numHeadlines = 5;
    public Button wakeUpBtn;
    public Boolean alarmRun = false;
    public Label twitterTrending;
    public Label trendingOnTwitterTitle;
    String bip = "Resources/Sounds/Cool-alarm-tone-notification-sound.mp3";
    Media hit = new Media(new File(bip).toURI().toString());
    public MediaPlayer mediaPlayer = new MediaPlayer(hit);

    public void initialize() {
        startTimeService();
        startWeatherService();
        startNewsService();
        startTweetService();
    }

    private void startTimeService(){
        String alarmTime = "21:40"; //Set to be the time you want the alarm to go off at
        Timeline getTime = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            String abbrTime = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            timeLabel.setText(time);
            smallClock.setText(abbrTime);
            String theme = getTimeOfDay();
            setTheme(theme);
            int seconds = Integer.parseInt(new SimpleDateFormat("ss").format(Calendar.getInstance().getTime()));
            if (seconds == 1){
                showHeadlines();
            }
            if (seconds == 20){
                showTweets();
            }
            if (seconds == 31){
                showTime();
            }
            if (alarmTime.equals(abbrTime) && !alarmRun && seconds < 3){
                alarmStart();
            }
        }),
                new KeyFrame(Duration.seconds(1))
        );
        getTime.setCycleCount(Animation.INDEFINITE);
        getTime.play();
    }

    private String getTimeOfDay() {
        String timeOfDay;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 19 || hour <= 5){
            timeOfDay = "Night";
        } else if (hour <= 9){
            timeOfDay = "Sunrise";
        } else if (hour >= 17 && hour >= 18){
            timeOfDay = "Sunset";
        } else {
            timeOfDay = "Midday";
        }
        return timeOfDay;
    }

    private void setTheme(String theme){
        switch (theme){
            case "Night":
                nightTheme.setVisible(true);
                morningTheme.setVisible(false);
                eveningTheme.setVisible(false);
                dayTheme.setVisible(false);
                break;
            case "Sunrise":
                nightTheme.setVisible(false);
                morningTheme.setVisible(true);
                eveningTheme.setVisible(false);
                dayTheme.setVisible(false);
                break;
            case "Sunset":
                nightTheme.setVisible(false);
                morningTheme.setVisible(false);
                eveningTheme.setVisible(true);
                dayTheme.setVisible(false);
                break;
            case "Midday":
                nightTheme.setVisible(false);
                morningTheme.setVisible(false);
                eveningTheme.setVisible(false);
                dayTheme.setVisible(true);
                break;
        }
    }

    private void startWeatherService(){
        Timeline getWeather = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            String condition = yahooWeather.condition(0,"Kensington, md");
            int temp = yahooWeather.currentTemp("Kensington, md");
            temperatureLabel.setText(temp + "°");
            conditionLabel.setText(condition);
        }),
                new KeyFrame(Duration.seconds(1))
        );
        getWeather.setCycleCount(Animation.INDEFINITE);
        getWeather.play();
    }

    private void showTime(){
        if (!alarmRun) {
            temperatureLabel.setVisible(true);
            timeLabel.setVisible(true);
            conditionLabel.setVisible(true);
            headlinesLabel.setVisible(false);
            smallClock.setVisible(false);
            headlinesActual.setVisible(false);
            headlinesLabel.setVisible(false);
            wakeUpBtn.setVisible(false);
            twitterTrending.setVisible(false);
            trendingOnTwitterTitle.setVisible(false);
        }
    }

    private void showHeadlines(){
        if (!alarmRun) {
            headlinesLabel.setVisible(true);
            temperatureLabel.setVisible(false);
            timeLabel.setVisible(false);
            conditionLabel.setVisible(false);
            smallClock.setVisible(true);
            headlinesLabel.setVisible(true);
            headlinesActual.setVisible(true);
            wakeUpBtn.setVisible(false);
            twitterTrending.setVisible(false);
            trendingOnTwitterTitle.setVisible(false);
        }
    }

    private void showTweets(){
        if (!alarmRun) {
            headlinesLabel.setVisible(false);
            temperatureLabel.setVisible(false);
            timeLabel.setVisible(false);
            conditionLabel.setVisible(false);
            smallClock.setVisible(true);
            headlinesLabel.setVisible(false);
            headlinesActual.setVisible(false);
            wakeUpBtn.setVisible(false);
            twitterTrending.setVisible(true);
            trendingOnTwitterTitle.setVisible(true);
        }
    }

    private void startNewsService(){
        Timeline getNews = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            RSSFeedParser parser = new RSSFeedParser(
                    "https://news.google.com/news/rss/?ned=us&gl=US&hl=en");
            Feed feed = parser.readFeed();
            String headlines = "";
            System.out.println(feed);
            int line = 0;
            for (FeedMessage message : feed.getMessages()) {
                System.out.println(message.title);
                if (!message.title.startsWith("More Top Stories - Google News")){
                    headlines += message.title + "\n" + "\n";
                    line ++;
                }
                if (line > numHeadlines){
                    break;
                }
            }
            headlinesActual.setText(headlines);
        }),
                new KeyFrame(Duration.seconds(60))
        );
        getNews.setCycleCount(Animation.INDEFINITE);
        getNews.play();
    }

    public void alarmStart(){
        mediaPlayer.play();
        headlinesLabel.setVisible(false);
        temperatureLabel.setVisible(false);
        timeLabel.setVisible(false);
        conditionLabel.setVisible(false);
        smallClock.setVisible(true);
        headlinesLabel.setVisible(false);
        headlinesActual.setVisible(false);
        wakeUpBtn.setVisible(true);
        alarmRun = true;
    }

    public void alarmStop(){
        mediaPlayer.stop();
        alarmRun = false;
        showHeadlines();
    }

    public void startTweetService(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("Qt4SPFsGLZVQJDLLTVless1Kg")
                .setOAuthConsumerSecret("ioEv0Shad2rPq4IAUmFkpxe65vgBKEBf7zyldHdFQgzGl0TehU")
                .setOAuthAccessToken("1015242795219595265-hNiMtQFbWD3RztOX08Co9UHzcrZdDS")
                .setOAuthAccessTokenSecret("bKok65AmkHsUknOW6bPQkFXgIOvV6L2B8oZhHz0a2fc7K");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Timeline getTweets = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            String trendingHashTags = "";
            ResponseList<Location> locations = null;
            try {
                locations = twitter.getAvailableTrends();
            } catch (TwitterException e1) {
                e1.printStackTrace();
            }
            System.out.println("Showing available trends");
            try {
                Trends trends = twitter.getPlaceTrends(23424977);
                for (int i = 0; i < trends.getTrends().length; i++) {
                    trendingHashTags += trends.getTrends()[i].getName() + "\n" + "\n";
                }
            } catch (twitter4j.TwitterException e2){
                e2.printStackTrace();
            }
            twitterTrending.setText(trendingHashTags);
        }),
                new KeyFrame(Duration.seconds(60))
        );
        getTweets.setCycleCount(Animation.INDEFINITE);
        getTweets.play();
    }
}