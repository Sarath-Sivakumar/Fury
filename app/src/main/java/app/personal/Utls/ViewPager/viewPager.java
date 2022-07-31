package app.personal.Utls.ViewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class viewPager extends ViewPager {

    Boolean Bool;

    public viewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.Bool = true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.Bool) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.Bool) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.Bool = enabled;
    }
}