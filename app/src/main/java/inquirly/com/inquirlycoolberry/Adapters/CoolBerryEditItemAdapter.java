package inquirly.com.inquirlycoolberry.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Vinayak on 7/15/2016.
 */
public class CoolBerryEditItemAdapter extends RecyclerView.Adapter<CoolBerryEditItemAdapter.ViewHolder>{

    private Context context;

    public CoolBerryEditItemAdapter(Context context){
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
