package inquirly.com.inquirlycatalogue.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.WindowManager;

import inquirly.com.inquirlycatalogue.R;

/**
 * Created by kaushal on 27-04-2016.
 */
public class ThemeChanger {

        private static int sTheme;
        public final static int THEME_BLUE = 1;
        public final static int THEME_RED = 2;

        /**
         * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
         */
        public static void changeToTheme(Activity activity, int theme)
        {
            sTheme = theme;
            activity.finish();
            activity.startActivity(new Intent(activity, activity.getClass()));
        }
        /** Set the theme of the activity, according to the configuration. */
        public static void onActivityCreateSetTheme(Activity activity)
        {
            switch (sTheme)
            {
                case THEME_BLUE:
                    activity.setTheme(R.style.AppThemeBlue);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        activity.getWindow().setStatusBarColor(Color.BLUE);
                    }
                    break;

                case THEME_RED:
                    activity.setTheme(R.style.AppThemeRed);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        activity.getWindow().setStatusBarColor(Color.RED);
                    }
                    break;

            }
        }
    }