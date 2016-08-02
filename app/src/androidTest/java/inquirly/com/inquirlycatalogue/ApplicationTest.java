package inquirly.com.inquirlycatalogue;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        MultiDex.install(getApplication());
    }
}