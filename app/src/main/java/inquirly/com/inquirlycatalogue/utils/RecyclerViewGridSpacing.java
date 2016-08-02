package inquirly.com.inquirlycatalogue.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kaushal on 04-12-2015.
 */
public class RecyclerViewGridSpacing extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public RecyclerViewGridSpacing(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public RecyclerViewGridSpacing(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);


        }
    }
