package com.example.hong.boaaproject.mainActivity;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class MyAxisValueFormatter implements IAxisValueFormatter {

    ArrayList<String> XValue;

    public MyAxisValueFormatter(ArrayList<String> list) {
        this.XValue = list;

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return XValue.get((int) (value - 1));
    }
}
