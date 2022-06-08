package com.example.foodswapp.receta.visualizar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

/**
 * Adaptador para el ViewPage que actúa de intermediario entre los fragments y la IngredientesPasosActivity
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment){
        fragments.add(fragment);
    }
}
