package com.uemg.cannon_game_am;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class MenuFragment extends Fragment {

    private TextView txtMenuHighScore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        view.setBackground(new PokeballPatternDrawable(getResources().getDisplayMetrics().density));

        txtMenuHighScore = view.findViewById(R.id.txtMenuHighScore);
        Button btnPlay = view.findViewById(R.id.btnPlay);
        Button btnHowToPlay = view.findViewById(R.id.btnHowToPlay);
        Button btnSound = view.findViewById(R.id.btnSound);
        Button btnExit = view.findViewById(R.id.btnExit);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CannonGamePrefs", Context.MODE_PRIVATE);
        boolean soundEnabled = prefs.getBoolean("sound_enabled", true);
        btnSound.setText(soundEnabled ? getString(R.string.sound_on) : getString(R.string.sound_off));

        updateHighScore();

        // Ação do Botão Play (Abre o jogo)
        btnPlay.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new MainActivityFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Ação do Botão Como Jogar
        btnHowToPlay.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new HowToPlayFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Ação do Botão Som
        btnSound.setOnClickListener(v -> {
            boolean currentSound = prefs.getBoolean("sound_enabled", true);
            boolean newSound = !currentSound;

            prefs.edit().putBoolean("sound_enabled", newSound).apply();
            btnSound.setText(newSound ? getString(R.string.sound_on) : getString(R.string.sound_off));

            if (newSound) {
                Toast.makeText(getContext(), R.string.sound_enabled_toast, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.sound_disabled_toast, Toast.LENGTH_SHORT).show();
            }
        });

        // Ação do Botão Sair
        btnExit.setOnClickListener(v -> requireActivity().finish());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHighScore();
    }

    // Atualiza a exibição da melhor pontuação em tempo real
    private void updateHighScore() {
        if (txtMenuHighScore != null && isAdded()) {
            SharedPreferences prefs = requireActivity().getSharedPreferences("CannonGamePrefs", Context.MODE_PRIVATE);
            int savedHighScore = prefs.getInt("high_score", 0);
            int highScore = (savedHighScore > 0) ? savedHighScore : prefs.getInt("high_score_1", 0);
            txtMenuHighScore.setText(String.format(Locale.getDefault(), getString(R.string.menu_high_score_format), highScore));
        }
    }
}
