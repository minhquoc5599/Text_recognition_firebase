package com.example.text_recognition;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.example.text_recognition.Adapter.ColorAdapter;
import com.example.text_recognition.Interface.BrushFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrushFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener {

    SeekBar seekBar_brush_size, seekBar_opacity_size;
    RecyclerView recycler_color;
    ToggleButton btn_brush_state;
    ColorAdapter colorAdapter;

    BrushFragmentListener listener;

    static BrushFragment instance;

    public static BrushFragment getInstance() {
        if(instance== null)
        {
            instance = new BrushFragment();
        }
        return instance;
    }

    public void setListener(BrushFragmentListener listener) {
        this.listener = listener;
    }

    public BrushFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_brush, container, false);

        seekBar_brush_size = itemView.findViewById(R.id.seakbar_brush_size);
        seekBar_opacity_size = itemView.findViewById(R.id.seakbar_brush_opacity);
        btn_brush_state  = itemView.findViewById(R.id.btn_brush_state);
        recycler_color = itemView.findViewById(R.id.recycler_color);
        recycler_color.setHasFixedSize(true);
        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        colorAdapter = new ColorAdapter(getContext(), genColorList(), this);
        recycler_color.setAdapter(colorAdapter);


        seekBar_opacity_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushOpacityChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushSizeChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btn_brush_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onBrushStateChangedListener(isChecked);
            }
        });

        return itemView;
    }

    private List<Integer> genColorList() {
        List <Integer> colorList = new ArrayList<>();
        colorList.add(Color.parseColor("#000000"));
        colorList.add(Color.parseColor("#5f9ea0"));
        colorList.add(Color.parseColor("#7ac5cd"));
        colorList.add(Color.parseColor("#5b6d5e"));
        colorList.add(Color.parseColor("#b7dbbc"));
        colorList.add(Color.parseColor("#fdaa60"));
        colorList.add(Color.parseColor("#cfd850"));
        colorList.add(Color.parseColor("#e84723"));
        colorList.add(Color.parseColor("#c7c3f5"));
        colorList.add(Color.parseColor("#2451c4"));
        colorList.add(Color.parseColor("#32c78f"));
        colorList.add(Color.parseColor("#6200e1"));
        colorList.add(Color.parseColor("#056664"));
        colorList.add(Color.parseColor("#ebe12a"));
        colorList.add(Color.parseColor("#a9fb0c"));


        return colorList;
    }

    @Override
    public void onColorSelected(int color) {
        listener.onBrushColorChangedListener(color);
    }
}
