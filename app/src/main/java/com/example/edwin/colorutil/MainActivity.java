package com.example.edwin.colorutil;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IColorPickerListener, SeekBar.OnSeekBarChangeListener {

    private TextView mColorText;
    private ColorPickView mColorPickView;
    private SeekBar mSeekbar;
    private ColorView mColorView;

    private SeekBar mTranSeekBar;

    private int     mCurrentColor = 0xFFFFFFFF;
    private float   mCurrentAlpha = 1.0f;
    private float[] tmpHsv = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mColorText = findViewById(R.id.tv_color);
        mColorPickView = findViewById(R.id.colorview);
        mColorPickView.setOnColorPickerChanger(this);
        mSeekbar = findViewById(R.id.seekbar_v);
        mSeekbar.setOnSeekBarChangeListener(this);

        mColorView = findViewById(R.id.colorshow);

        mTranSeekBar = findViewById(R.id.seekbar_t);
        mTranSeekBar.setOnSeekBarChangeListener(this);
        mTranSeekBar.setProgress(100);

    }

    @Override
    public void onColorPickerChanger(int currentColor, int red, int green, int blue) {
        mColorText.setText(Utils.toColorText(mCurrentColor, mCurrentAlpha));
        mCurrentColor = currentColor;
        mColorView.setColor(currentColor, mCurrentAlpha);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.seekbar_v) {
            float percent = 1 - (progress / 100f);
            Color.colorToHSV(mCurrentColor, tmpHsv);
            tmpHsv[2] = percent;
            mCurrentColor = Color.HSVToColor(tmpHsv);
            mColorPickView.setColor(mCurrentColor, percent);

            mColorText.setText(Utils.toColorText(mCurrentColor, mCurrentAlpha));
            mColorView.setColor(mCurrentColor, mCurrentAlpha);
        } else if (seekBar.getId() == R.id.seekbar_t) {
            mCurrentAlpha = progress / 100f;
            mColorText.setText(Utils.toColorText(mCurrentColor, mCurrentAlpha));
            mColorView.setColor(mCurrentColor, mCurrentAlpha);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
