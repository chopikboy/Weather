package ru.azat.mbaweather.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import ru.azat.mbaweather.R;

public class CityAddingDialog extends Dialog  {

    public CityAddingDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_adding);
        Button okBtn = (Button) findViewById(R.id.btn_yes);
        Button cancelBtn = (Button) findViewById(R.id.btn_no);
        final EditText cityET = (EditText) findViewById(R.id.cityET);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(onCityChoosedListener!=null)
                {
                    onCityChoosedListener.onCityChoose(cityET.getText().toString());
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });

    }

    OnCityChosedListener onCityChoosedListener;
    public void setOnCityChosedListener(OnCityChosedListener l)
    {
        onCityChoosedListener = l;
    }
    public interface OnCityChosedListener
    {
        public void onCityChoose(String city);
     }


}
