package com.uemg.cannon_game_am;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HowToPlayFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_how_to_play, container, false);
        view.setBackground(new PokeballPatternDrawable(getResources().getDisplayMetrics().density));

        Button btnBack = view.findViewById(R.id.btnBack);

        // Botão para voltar ao menu principal
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new MenuFragment())
                        .commit();
            }
        });

        return view;
    }
}
