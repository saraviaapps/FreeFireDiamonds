package saravia.apps.freefirediamonds;

import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    int tap_count;


    public PageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tap_count=behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Global_Fragment();
            case 1:
                return new ChatFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tap_count;
    }
}
