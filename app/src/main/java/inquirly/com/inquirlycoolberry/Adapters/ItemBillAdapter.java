package inquirly.com.inquirlycoolberry.Adapters;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import inquirly.com.inquirlycatalogue.R;
import inquirly.com.inquirlycatalogue.models.BillResponse;

/**
 * Created by Vinayak on 7/28/2016.
 */
public class ItemBillAdapter extends RecyclerView.Adapter<ItemBillAdapter.ViewHolder> {

    LinearLayout[] myTextViews;
    private Context context;
    private float total;
    private static final String TAG = "ItembillAdapter";
    private BillResponse.Taxes billTaxes = new BillResponse.Taxes();
    private BillResponse.BillItems billItems = new BillResponse.BillItems();
    private ArrayList<BillResponse.Taxes> billTaxesList = new ArrayList<>();
    private ArrayList<BillResponse.BillItems> billItemsList = new ArrayList<>();

    public ItemBillAdapter(Context context,float total,ArrayList<BillResponse.Taxes> billTaxesList,
                           ArrayList<BillResponse.BillItems> billItemsList){
        this.context = context;
        this.total = total;
        this.billTaxesList = billTaxesList;
        this.billItemsList = billItemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_bill, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        billItems = billItemsList.get(position);
        holder.item_num.setText(String.valueOf(billItems.getSerial_num()));
        holder.item_name.setText(billItems.getName());
        holder.item_price.setText(String.valueOf(billItems.getItem_price()));
        holder.item_qty.setText("X "+String.valueOf(billItems.getQuantity()));
        holder.item_total.setText(String.valueOf(billItems.getTotal()));

        if(billItems.getDiscounts().size()!=0){
            int discountTypes = billItems.getDiscounts().size();
            holder.linear_discounts.setVisibility(View.VISIBLE);

            LinearLayout[] myTextViews = new LinearLayout[discountTypes];

            for (int i = 0; i < discountTypes; i++) {
                final LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setWeightSum(10);
                linearLayout.setPadding(200,0,0,0);

                final TextView discountLabel = new TextView(context);
                final TextView discountamount = new TextView(context);

                discountLabel.setTextColor(context.getResources().getColor(R.color.accent_material_light));
                discountamount.setTextColor(context.getResources().getColor(R.color.accent_material_light));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.weight = 9.0f;
                params2.weight = 1.0f;

                discountLabel.setLayoutParams(params);
                discountamount.setLayoutParams(params2);

                discountLabel.setGravity(Gravity.LEFT);
                discountamount.setGravity(Gravity.CENTER);

                discountLabel.setTypeface(null, Typeface.BOLD);
                discountamount.setTypeface(null, Typeface.BOLD);

                discountamount.setPadding(30,0,20,0);

                discountLabel.setTextSize(14);
                discountamount.setTextSize(14);

                // set some properties of rowTextView or something
                discountLabel.setText(billItems.getDiscounts().get(i).getLabel());
                discountamount.setText(String.valueOf("-"+billItems.getDiscounts().get(i).getAmount()));
                // add the textview to the linearlayout

                linearLayout.addView(discountLabel);
                linearLayout.addView(discountamount);
                holder.linear_discounts.addView(linearLayout);

            //    myTextViews[i] = linearLayout;
                Log.i(TAG,"check linear count---" + myTextViews[i]);
            //    myTextViews[i] = discountamount;
            }
        }

        if(billItemsList.size()-1 == position){
            Log.i(TAG,"entered if---" + billItemsList.size());
            if(billTaxesList.size()!=0){
                holder.table_taxes.setVisibility(View.VISIBLE);
                myTextViews = new LinearLayout[billTaxesList.size()];

                for(int j=0;j<billTaxesList.size();j++) {
                    Log.i(TAG, "for ---j" + j);
                    billTaxes = billTaxesList.get(j);

                    final LinearLayout row= new LinearLayout(context);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setWeightSum(10);
                    row.setPadding(140,5,0,5);
                    row.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));

                    final TextView taxMainLabel = new TextView(context);
                    final TextView taxableAmount = new TextView(context);

                    LinearLayout.LayoutParams paramsTax = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout.LayoutParams paramsTax2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    paramsTax.weight = 9.0f;
                    paramsTax2.weight = 1.0f;

                    taxMainLabel.setLayoutParams(paramsTax);
                    taxableAmount.setLayoutParams(paramsTax2);

                    taxMainLabel.setTextSize(16);
                    taxableAmount.setTextSize(16);

                    taxMainLabel.setTextColor(context.getResources().getColor(R.color.mainColor));
                    taxableAmount.setTextColor(context.getResources().getColor(R.color.mainColor));

                    taxMainLabel.setTypeface(null, Typeface.BOLD_ITALIC);
                    taxableAmount.setTypeface(null, Typeface.BOLD);

                    taxMainLabel.setText(billTaxes.getName() + " on Items  " + billTaxes.getItems());
                    taxableAmount.setText("SubTotal: " + billTaxes.getTaxable_amount());

                    row.addView(taxMainLabel);
                    row.addView(taxableAmount);

                    Log.i(TAG, "before adding----" + billTaxes.getTaxable_amount() + "---" +
                            billTaxesList.size() + "--text set--" + taxMainLabel.getText().toString());

                    holder.table_taxes.addView(row);
                    myTextViews[j] = row;

                    Log.i(TAG,"---j--->" + j+"----" + billTaxesList.get(j).getTax_components().size());
                    if(billTaxesList.get(j).getTax_components().size()!=0){
                        LinearLayout[] tax_compo_array = new LinearLayout[billTaxes.getTax_components().size()];

                        for(int i=0;i<billTaxesList.get(j).getTax_components().size();i++) {

                            final LinearLayout tax_compo= new LinearLayout(context);
                            tax_compo.setOrientation(LinearLayout.HORIZONTAL);
                            tax_compo.setWeightSum(10);
                            tax_compo.setPadding(200,7,0,7);
                            tax_compo.setBackgroundColor(context.getResources().getColor(R.color.button_material_light));

                            final TextView compo_label = new TextView(context);
                            final TextView compo_amount = new TextView(context);

                            compo_label.setLayoutParams(paramsTax);
                            compo_amount.setLayoutParams(paramsTax2);

                            compo_label.setTextSize(14);
                            compo_amount.setTextSize(14);

                            compo_label.setGravity(Gravity.LEFT);
                            compo_amount.setGravity(Gravity.CENTER);

                            compo_amount.setPadding(30,0,20,0);
                            compo_label.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));
                            compo_amount.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));

                            compo_label.setText(billTaxes.getTax_components().get(i).getBill_label());
                            compo_amount.setText(String.valueOf(billTaxes.getTax_components().get(i).getTax_amount()));

                            tax_compo.addView(compo_label);
                            tax_compo.addView(compo_amount);

                            Log.i(TAG, "before adding----" +billTaxes.getName() + "---" +billTaxes.getTaxable_amount()
                                    +"---"+compo_label.getText().toString() + "---" + billTaxesList.size() + "--text set--" );
                            try{
                                Log.i(TAG,"---i--->" + i);
                                holder.table_taxes.addView(tax_compo);
                            }catch (Exception e){
                                Log.i(TAG,"error---=-" + e.getMessage());
                            }
                        //    tax_compo_array[j] = tax_compo;
                        }
                    }
                }
            }if(billTaxesList.size()==myTextViews.length){
                holder.linear_total.setVisibility(View.VISIBLE);
                final TextView billAmount = new TextView(context);
                billAmount.setTextSize(16);
                billAmount.setPadding(0,5,10,5);
                billAmount.setGravity(Gravity.RIGHT);
                billAmount.setTypeface(null,Typeface.BOLD);
                billAmount.setTextColor(context.getResources().getColor(android.R.color.white));
                billAmount.setText("Grand Total: "+String.valueOf(total));
                holder.linear_total.addView(billAmount);
            }
        }
    }

    @Override
    public int getItemCount() {
        return billItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout table_taxes;
        LinearLayout linear_extras,linear_discounts,linear_total;
        TextView item_num,item_name,item_price,item_qty,item_total;
        TextView item_extra,extra_amount;

        public ViewHolder(View itemView) {
            super(itemView);

            item_num = (TextView)itemView.findViewById(R.id.bill_item_num);
            item_name = (TextView)itemView.findViewById(R.id.bill_item_name);
            item_price = (TextView)itemView.findViewById(R.id.bill_item_price);
            item_qty = (TextView)itemView.findViewById(R.id.bill_item_qty);
            item_total = (TextView)itemView.findViewById(R.id.bill_item_total);
            item_extra = (TextView)itemView.findViewById(R.id.bill_item_extra);
            extra_amount = (TextView)itemView.findViewById(R.id.bill_amount_extra);
            linear_extras = (LinearLayout)itemView.findViewById(R.id.bill_linear_extras);
            linear_discounts = (LinearLayout)itemView.findViewById(R.id.bill_linear_discount);
            table_taxes = (LinearLayout) itemView.findViewById(R.id.bill_table_taxes);
            linear_total = (LinearLayout) itemView.findViewById(R.id.bill_linear_total);
        }
    }
}