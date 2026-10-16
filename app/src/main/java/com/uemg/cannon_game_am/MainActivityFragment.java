package com.uemg.cannon_game_am;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainActivityFragment extends Fragment {

    private CannonView cannonView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        cannonView = view.findViewById(R.id.cannonView);
        Button btnBackToMenu = view.findViewById(R.id.btnBackToMenu);

        if (btnBackToMenu != null) {
            btnBackToMenu.bringToFront();
            btnBackToMenu.setOnClickListener(v -> {
                if (cannonView != null) {
                    cannonView.stopGame();
                    cannonView.releaseResources();
                }
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, new MenuFragment())
                            .commit();
                }
            });
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cannonView != null) {
            cannonView.stopGame();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cannonView != null) {
            cannonView.stopGame();
            cannonView.releaseResources();
        }
    }
}
