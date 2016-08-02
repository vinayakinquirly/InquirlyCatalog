package inquirly.com.inquirlycatalogue.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import inquirly.com.inquirlycatalogue.ApplicationController;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.models.CartItem;
import inquirly.com.inquirlycatalogue.models.Customer;
import inquirly.com.inquirlycatalogue.models.OrderItem;
import inquirly.com.inquirlycatalogue.rest.ApiRequest;
import inquirly.com.inquirlycatalogue.rest.IRequestCallback;
import inquirly.com.inquirlycatalogue.utils.ApiConstants;
import inquirly.com.inquirlycatalogue.utils.CatalogSharedPrefs;
import inquirly.com.inquirlycatalogue.utils.InternetConnectionStatus;
import inquirly.com.inquirlycatalogue.utils.TimePickerDialogue;

public class CustomerDetailActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_placeorder;
    private String mCampaignId;
    AlertDialog.Builder builder;
    SharedPreferences mSharedPrefs;
    TextView lbl_heading, desc1, desc2;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    public int year, month, day, hour, minute;
    ArrayList<String> orders = new ArrayList<String>();
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText name, phone, email, address, date, time;

    private static final String TAG = CustomerDetailActivity.class.getSimpleName();

    public CustomerDetailActivity() {

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        mSharedPrefs = this.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);

        final boolean isFromDeepLinking = mSharedPrefs.getBoolean(CatalogSharedPrefs.IS_FROM_DEEPLINKING, false);
        String tableId = mSharedPrefs.getString(CatalogSharedPrefs.TABLE_NAME, null);


        Toast.makeText(getApplicationContext(), "Order for table : " + tableId, Toast.LENGTH_SHORT).show();

        mCampaignId = getIntent().getStringExtra("campaign_id");

        lbl_heading = (TextView) findViewById(R.id.lbl_heading);
        desc1 = (TextView) findViewById(R.id.desc1);
        desc2 = (TextView) findViewById(R.id.desc2);
        name = (EditText) findViewById(R.id.cust_fullname);
        phone = (EditText) findViewById(R.id.cust_phonenumber);
        address = (EditText) findViewById(R.id.cust_address);
        btn_placeorder = (Button) findViewById(R.id.btn_placeorder);
        date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        email = (EditText) findViewById(R.id.cust_emailid);
        TextView lbl_date = (TextView)findViewById(R.id.label_date);
        TextView lbl_time = (TextView)findViewById(R.id.label_time);

        if(isFromDeepLinking)
        {
            address.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
            lbl_date.setVisibility(View.GONE);
            lbl_time.setVisibility(View.GONE);

            int maxLength = 14;
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
            phone.setFilters(FilterArray);
        }

        pDialog = new ProgressDialog(CustomerDetailActivity.this);
        builder = new AlertDialog.Builder(CustomerDetailActivity.this, R.style.AppCompatAlertDialogStyle);

        final Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        lbl_heading.setTypeface(font);
        desc1.setTypeface(font);
        desc2.setTypeface(font);
        btn_placeorder.setTypeface(font);

        int count = ApplicationController.getInstance().getCartItemCount();
        float amount = ApplicationController.getInstance().getTotalCartAmount();
        String itemMessage = "You have " + count + " items in your cart. The total amount payable is Rs." + amount;
        desc1.setText(itemMessage);
        btn_placeorder.setTypeface(font);
        name.setTypeface(font);
        phone.setTypeface(font);
        address.setTypeface(font);
        email.setTypeface(font);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                time.setText("");

                showDialog(DATE_DIALOG_ID);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeValidation();
            }
        });

        btn_placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(isFromDeepLinking)
                    {
                        deeplinking();
                    }
                    else
                    {
                        nondeeplinking();
                    }
            }

        });
    }

    private void showPipelinePushDialog(String message, boolean isSuccess) {
        final Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDetailActivity.this, R.style.AppCompatAlertDialogStyle);
        View view = LayoutInflater.from(CustomerDetailActivity.this).inflate(R.layout.checkout_success_layout, null);
        TextView tx = (TextView) view.findViewById(R.id.txt_checkout_message);
        tx.setText(message);
        ImageView img = (ImageView) view.findViewById(R.id.img_checkout);
        if (isSuccess) {
            img.setImageResource(R.drawable.thumbs_up_icon);
            builder.setTitle("Order Successful");

        } else {
            img.setImageResource(R.drawable.ic_sad);
            builder.setTitle("Order failed");
        }
        tx.setTypeface(font);
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApplicationController.getInstance().removeAllCartItems();
                ApplicationController.getInstance().setTotalCartAmount();

                Intent intent = new Intent(CustomerDetailActivity.this, MainActivity.class);
                intent.putExtra(ApiConstants.CAMPAIGN_TYPE, ApiConstants.CAMPAIGN_TYPE_CATALOG);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                CustomerDetailActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                // the callback received when the user "sets" the Date in the DatePickerDialog
                public void onDateSet(DatePicker view, int yearSelected,
                                      int monthOfYear, int dayOfMonth) {
                    year = yearSelected;
                    month = monthOfYear;
                    day = dayOfMonth;

                    Calendar cal = Calendar.getInstance();

                    cal.add(Calendar.DATE, -1);
                    String selectedDay = date.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = new Date();
                    Date today = cal.getTime();
                    try {
                        sdf.format(today);
                        // Log.d("today's date:", today.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    try {
                        d = sdf.parse(selectedDay);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //  Log.d("selected",d.toString());
                    if (d.before(today)) {
                        Toast.makeText(getApplicationContext(), "Selected Date is Invalid...Please select Valid Date", Toast.LENGTH_SHORT).show();
                        date.setText("");
                    } else {
                        String Dates = year + "-" + (++month < 10 ? ("0" + month++) : (month)) + "-" + (day < 10 ? ("0" + day) : (day));
                        date.setText(Dates);
                    }
                }
            };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                // the callback received when the user "sets" the TimePickerDialog in the dialog
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    hour = hourOfDay;
                    minute = min;

                    hour = hourOfDay;
                    minute = min;
                    String AM_PM;
                    if (hourOfDay < 12) {
                        AM_PM = "AM";

                    } else {
                        AM_PM = "PM";
                        hour = hour - 12;
                    }
                    String output = String.format("%02d:%02d", hourOfDay, min);
                    time.setText(output + " " + AM_PM);
                }
            };


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DATE_DIALOG_ID:

                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
        }
        return null;
    }

    public void pushOrderfromDeeplink(Customer customer) {

        final ArrayList<CartItem> items = ApplicationController.getInstance().getCartItems();
        SharedPreferences sPrefs = this.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);
        String tableId = sPrefs.getString(CatalogSharedPrefs.TABLE_NAME, null);

        Gson gson = new Gson();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            final OrderItem order = new OrderItem();

            order.campaignDetails.id = item.getCampaignId();
            order.campaignDetails.type = "SALES-PIPELINE";
            order.campaignDetails.channel = "CATALOGUE";

            order.form.item = item.getItemCode();
            HashMap<String, String[]> specs = item.getItemSpecs();
            specs.put("totalCost", new String[]{item.getItemPrice() + ""});
            order.form.itemDetails = specs;
            order.form.customer.Name = customer.Name;
            order.form.customer.Mobile = customer.Phone;
            order.form.customer.Email = customer.Email;
            order.form.customer.tableId = tableId;

            Log.i(TAG, "pushing to pipeline json=" + gson.toJson(order).toString());
            final String pipelineUrl = sPrefs.getString(CustomerDetailActivity.this.mCampaignId + "_" + CatalogSharedPrefs.KEY_PIPELINE_URL, null);
            // final String pipelineUrl = "https://stagingsignin.inquirly.com/pipeline/opinify/new";
            ApiRequest.postOrder(order, pipelineUrl, new IRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    pDialog.dismiss();
                    Log.i(TAG, "pipeline push successful " + response.toString());
                    try {
                        ApplicationController.getInstance().removeAllCartItems();
                        ApplicationController.getInstance().setTotalCartAmount();

                        if (pipelineUrl.contains("justbake")) {
                            Log.i(TAG, "from justbake");
                            String orderNumber = response.getString("order_id");
                            orders.add(orderNumber);
                        } else {
                            Log.i(TAG, "non-justbake");
                            String orderNumber = response.getJSONObject("status").getString("orderId");
                            orders.add(orderNumber);
                        }
                        if (items.size() == orders.size()) {
                            postSuccess();
                            Log.d("item size", String.valueOf(items.size()));
                            Log.d("order id's", String.valueOf(orders.size()));
                        } else {

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        ex.getMessage();
                        showPipelinePushDialog(ex.getMessage(), true);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    pDialog.dismiss();
                    Log.e(TAG, "error pipeline push " + error.toString());
                    postFailure();

                    ApplicationController.getInstance().removeAllCartItems();
                    ApplicationController.getInstance().setTotalCartAmount();
                }
            });
        }
    }

    public void pushOrderfromnonDeeplink(Customer customer) {

        final ArrayList<CartItem> items = ApplicationController.getInstance().getCartItems();
        SharedPreferences sPrefs = this.getSharedPreferences(CatalogSharedPrefs.KEY_NAME, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            final OrderItem order = new OrderItem();

            order.campaignDetails.id = item.getCampaignId();
            order.campaignDetails.type = "SALES-PIPELINE";
            order.campaignDetails.channel = "CATALOGUE";

            order.form.item = item.getItemCode();
            HashMap<String, String[]> specs = item.getItemSpecs();
            specs.put("totalCost", new String[]{item.getItemPrice() + ""});
            order.form.itemDetails = specs;
            order.form.customer.Name = customer.Name;
            order.form.customer.Mobile = customer.Phone;
            order.form.customer.Email = customer.Email;
            order.form.customer.Address = customer.Address;
            order.form.customer.deliveryDate = customer.DeliveryDate;

            Log.i(TAG, "pushing to pipeline json=" + gson.toJson(order).toString());
            final String pipelineUrl = sPrefs.getString(CustomerDetailActivity.this.mCampaignId + "_" + CatalogSharedPrefs.KEY_PIPELINE_URL, null);
            // final String pipelineUrl = "https://stagingsignin.inquirly.com/pipeline/opinify/new";
            ApiRequest.postOrder(order, pipelineUrl, new IRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    pDialog.dismiss();
                    Log.i(TAG, "pipeline push successful " + response.toString());
                    try {
                        ApplicationController.getInstance().removeAllCartItems();
                        ApplicationController.getInstance().setTotalCartAmount();

                        if (pipelineUrl.contains("justbake")) {
                            Log.i(TAG, "from justbake");
                            String orderNumber = response.getString("order_id");
                            orders.add(orderNumber);
                        } else {
                            Log.i(TAG, "non-justbake");
                            String orderNumber = response.getJSONObject("status").getString("orderId");
                            orders.add(orderNumber);
                        }
                        if (items.size() == orders.size()) {
                            postSuccess();
                            Log.d("item size", String.valueOf(items.size()));
                            Log.d("order id's", String.valueOf(orders.size()));
                        } else {

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        ex.getMessage();
                        showPipelinePushDialog(ex.getMessage(), true);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    pDialog.dismiss();
                    Log.e(TAG, "error pipeline push " + error.toString());
                    postFailure();

                    ApplicationController.getInstance().removeAllCartItems();
                    ApplicationController.getInstance().setTotalCartAmount();
                }
            });
        }
    }


    public void postSuccess() {
        if (orders.size() > 1) {
            String str = Arrays.toString(orders.toArray()).replace("[", "").replace(" ", "").replace("]", "");
            String message = "Congratulations! Your order was placed successfully. Your order numbers are \n" + str;
            showPipelinePushDialog(message, true);
        } else if (orders.size() == 1) {
            String str = Arrays.toString(orders.toArray()).replace("[", "").replace(" ", "").replace("]", "");
            String message = "Congratulations! Your order was placed successfully. Your order number is " + str;
            showPipelinePushDialog(message, true);
        }
    }

    public void postFailure() {
        String message = "Sorry! unable to place the order";
        showPipelinePushDialog(message, false);
    }

    public void TimeValidation() {

        final Calendar mcurrentTime = Calendar.getInstance();
        final int mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int mMinute = mcurrentTime.get(Calendar.MINUTE);
        final TimePickerDialogue mTimePicker;

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, 0);
        String selectedDay = date.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        Date today = cal.getTime();
        try {
            sdf.format(today);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            d = sdf.parse(selectedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d.before(today)) {

            mTimePicker = new TimePickerDialogue(CustomerDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    hour = selectedHour;
                    minute = selectedMinute;
                    String AM_PM;
                    if (selectedHour < 12) {
                        AM_PM = "AM";

                    } else {
                        AM_PM = "PM";
                        hour = hour - 12;
                    }
                    String output = String.format("%02d:%02d", selectedHour, selectedMinute);
                    time.setText(output + " " + AM_PM);


                }
            }, mHour, mMinute, true);//true = 24 hour time
            mTimePicker.setTitle("Select Delivery Time");
            mTimePicker.setMin(mHour, mMinute);
            mTimePicker.show();

        } else {
            showDialog(TIME_DIALOG_ID);
        }

    }

    public void DateValidation() {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, -1);
        String selectedDay = date.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        Date today = cal.getTime();
        try {
            sdf.format(today);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            d = sdf.parse(selectedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d.before(today)) {
            Toast.makeText(getApplicationContext(), "Selected Date is Invalid...Please select Valid Date", Toast.LENGTH_SHORT).show();
            date.setText("");
        }
    }

    public void deeplinking() {
        if (InternetConnectionStatus.checkConnection(getApplicationContext())) {

            if (name.getText().toString().length() == 0 | phone.getText().toString().length() == 0 | email.getText().toString().length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDetailActivity.this, R.style.AppCompatAlertDialogStyle);
                View view = LayoutInflater.from(CustomerDetailActivity.this).inflate(R.layout.checkout_success_layout, null);

                final Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
                TextView tx = (TextView) view.findViewById(R.id.txt_checkout_message);
                tx.setTypeface(font);
                tx.setText("We need your details to process the order!");
                ImageView img = (ImageView) view.findViewById(R.id.img_checkout);
                img.setImageResource(R.drawable.ic_sad);
                builder.setView(view);
                builder.setTitle("Details missing");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (!email.getText().toString().matches("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")) {
                Toast.makeText(getApplicationContext(), "Email is invalid",
                        Toast.LENGTH_LONG).show();

            } else if (!(phone.getText().toString().length() >= 10)) {
                Toast.makeText(getApplicationContext(), "Please enter valid phone number", Toast.LENGTH_SHORT).show();
            }
            Customer customer = new Customer();
            customer.Name = name.getText().toString();
            customer.Phone = phone.getText().toString();
            customer.Email = email.getText().toString();
            pushOrderfromDeeplink(customer);

            pDialog.setTitle("Ordering....Please wait");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        else {
            builder.setTitle("No Internet!!");
            builder.setMessage("Please connect to the internet and try again");
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    }

    public void nondeeplinking() {
        DateValidation();
        if (InternetConnectionStatus.checkConnection(getApplicationContext())) {
            if (name.getText().toString().length() == 0 | phone.getText().toString().length() == 0 | address.getText().toString().length() == 0 | email.getText().toString().length() == 0 | date.getText().toString().length() == 0 | time.getText().toString().length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDetailActivity.this, R.style.AppCompatAlertDialogStyle);
                View view = LayoutInflater.from(CustomerDetailActivity.this).inflate(R.layout.checkout_success_layout, null);

                final Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Montserrat-Regular.ttf");
                TextView tx = (TextView) view.findViewById(R.id.txt_checkout_message);
                tx.setTypeface(font);
                tx.setText("We need your details to process the order!");
                ImageView img = (ImageView) view.findViewById(R.id.img_checkout);
                img.setImageResource(R.drawable.ic_sad);
                builder.setView(view);
                builder.setTitle("Details missing");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (!email.getText().toString().matches("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")) {
                Toast.makeText(getApplicationContext(), "Email is invalid",
                        Toast.LENGTH_LONG).show();

            } else if (!(phone.getText().toString().length() == 10)) {
                Toast.makeText(getApplicationContext(), "Please enter valid phone number", Toast.LENGTH_SHORT).show();
            } else {
                String datetime = date.getText().toString() + " " + time.getText().toString();
                //   Log.d("date", datetime);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date dt = null;
                try {
                    dt = formatter.parse(datetime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                converter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String utctime = converter.format(dt);
                String newdate = utctime.replace(" ", "T");
                String finaldatetime = newdate + ":00.000Z";

                Customer customer = new Customer();
                customer.Name = name.getText().toString();
                customer.Phone = phone.getText().toString();
                customer.Address = address.getText().toString();
                customer.DeliveryDate = finaldatetime;
                customer.Email = email.getText().toString();
                pushOrderfromnonDeeplink(customer);

                pDialog.setTitle("Ordering....Please wait");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        } else {
            builder.setTitle("No Internet!!");
            builder.setMessage("Please connect to the internet and try again");
            builder.setPositiveButton("OK", null);
            builder.show();
        }

    }
}
