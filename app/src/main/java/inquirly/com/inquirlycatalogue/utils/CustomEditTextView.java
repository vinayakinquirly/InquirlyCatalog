package inquirly.com.inquirlycatalogue.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.Hashtable;

public class CustomEditTextView extends EditText {

    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int BOLD_ITALIC = 3;

    public CustomEditTextView(Context context) {
        super(context);
        init();
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);init();
    }

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);init();
    }

    public void init() {

        try {
            Typeface tfRegular = getTypeFace(getContext(), "Montserrat-Regular");
            Typeface tfBold = getTypeFace(getContext(), "Montserrat-Bold");
            if(this.getTypeface()!=null){
                switch (this.getTypeface().getStyle()){
                    case NORMAL:
                        setTypeface(tfRegular , NORMAL);
                        break;
                    case BOLD:
                        setTypeface(tfBold , BOLD);
                        break;
                    case ITALIC:
                        setTypeface(tfRegular , ITALIC);
                        break;
                }
            }else{
                setTypeface(tfRegular , NORMAL);
            }

        } catch (Exception e) {
            // do nothing on exception
            e.printStackTrace();
        }
    }

    public static final String TYPEFACE_FOLDER = "fonts";
    public static final String TYPEFACE_EXTENSION = ".ttf";

    private static Hashtable<String, Typeface> sTypeFaces = new Hashtable<String, Typeface>(4);

    public static Typeface getTypeFace(Context context, String fileName) {
        Typeface tempTypeface = sTypeFaces.get(fileName);

        if (tempTypeface == null) {
            String fontPath = new StringBuilder(TYPEFACE_FOLDER).append('/').append(fileName).append(TYPEFACE_EXTENSION).toString();
            tempTypeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            sTypeFaces.put(fileName, tempTypeface);
        }

        return tempTypeface;
    }
}
