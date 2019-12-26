package com.example.text_recognition.Interface;

public interface BrushFragmentListener {
    void onBrushSizeChangedListener(float size);
    void onBrushOpacityChangedListener(int opacity);
    void onBrushColorChangedListener(int color);
    void onBrushStateChangedListener(boolean isEraser);
}
