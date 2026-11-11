package com.example.englishlearningsoftware;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class FlashcardAdapter extends ArrayAdapter<Flashcard> {

    private Activity context;
    private List<Flashcard> flashcards;

    public FlashcardAdapter(Activity context, int layoutId, List<Flashcard> flashcards) {
        super(context, layoutId, flashcards);
        this.context = context;
        this.flashcards = flashcards;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_flashcard, parent, false);
        }

        Flashcard card = getItem(position);

        TextView tvWord = convertView.findViewById(R.id.item_tv_word);
        TextView tvType = convertView.findViewById(R.id.item_tv_type);
        TextView tvMeaning = convertView.findViewById(R.id.item_tv_meaning);
        LinearLayout llParent = convertView.findViewById(R.id.item_flashcard_parent);

        if (card != null) {
            tvWord.setText(card.getWord());

            if (card.getType() != null && !card.getType().trim().isEmpty()) {
                tvType.setVisibility(View.VISIBLE);
                tvType.setText("(" + card.getType() + ")");
            } else {
                tvType.setVisibility(View.GONE);
            }

            tvMeaning.setText(card.getMeaning());
        }

        if (position % 2 == 0) {
            llParent.setBackgroundColor(Color.WHITE);
        } else {
            llParent.setBackgroundColor(Color.parseColor("#F9FAFB"));
        }

        return convertView;
    }
}
