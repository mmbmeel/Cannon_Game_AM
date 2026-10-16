package com.uemg.cannon_game_am;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class HighScoresFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_high_scores, container, false);
        view.setBackground(new PokeballPatternDrawable(getResources().getDisplayMetrics().density));

        TextView txtScores = view.findViewById(R.id.txtScoresList);
        Button btnBack = view.findViewById(R.id.btnBack);

        // Ler a maior pontuação guardada via SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("CannonGamePrefs", Context.MODE_PRIVATE);
        int savedHighScore = prefs.getInt("high_score", 0);
        int highScore = (savedHighScore > 0) ? savedHighScore : prefs.getInt("high_score_1", 0);

        txtScores.setText(String.format(Locale.getDefault(), getString(R.string.high_score_format), highScore));

        // Botão para voltar ao menu anterior
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
