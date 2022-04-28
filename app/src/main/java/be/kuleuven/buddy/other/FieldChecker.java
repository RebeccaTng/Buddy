package be.kuleuven.buddy.other;

import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import be.kuleuven.buddy.R;

public class FieldChecker {

    EditText moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place, name, species;
    TextView errorMessage;
    int fields;

    public FieldChecker(EditText moistMin, EditText moistMax, EditText lightMin, EditText lightMax, EditText tempMin, EditText tempMax, EditText waterlvl, EditText ageYears, EditText ageMonths, EditText place, EditText name, EditText species, TextView errorMessage) {
        this.moistMin = moistMin;
        this.moistMax = moistMax;
        this.lightMin = lightMin;
        this.lightMax = lightMax;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.waterlvl = waterlvl;
        this.ageYears = ageYears;
        this.ageMonths = ageMonths;
        this.place = place;
        this.name = name;
        this.species = species;
        this.errorMessage = errorMessage;
        fields = 2;
    }

    public void setFilters(){
        moistMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        moistMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        waterlvl.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        ageMonths.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "12")});
    }

    public boolean addPlant() {
        clearAll();

        // Check all fields are filled in
        if(checkEmpty()) {
            errorMessage.setText(R.string.notAllFilled);
            errorMessage.setVisibility(View.VISIBLE);

        } else if(checkMinMaxError(moistMin, moistMax) & checkMinMaxError(lightMin,lightMax) & checkMinMaxError(tempMin, tempMax)){
            errorMessage.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    public Boolean checkMinMaxError(EditText min, EditText max) {
        if(Integer.parseInt(min.getText().toString()) >= Integer.parseInt(max.getText().toString())) {
            errorMessage.setText(R.string.minMaxError);
            errorMessage.setVisibility(View.VISIBLE);
            min.setBackgroundResource(R.drawable.bg_fill_red);
            max.setBackgroundResource(R.drawable.bg_fill_red);
            return false; // min is not smaller than max
        }
        return true; // min max okay
    }

    public void clearAll() {
        moistMin.setBackgroundResource(R.drawable.bg_fill);
        moistMax.setBackgroundResource(R.drawable.bg_fill);
        lightMin.setBackgroundResource(R.drawable.bg_fill);
        lightMax.setBackgroundResource(R.drawable.bg_fill);
        tempMin.setBackgroundResource(R.drawable.bg_fill);
        tempMax.setBackgroundResource(R.drawable.bg_fill);
    }

    public boolean checkEmpty() {
        boolean empty = moistMin.getText().toString().isEmpty() || moistMax.getText().toString().isEmpty() ||
                lightMin.getText().toString().isEmpty() || lightMax.getText().toString().isEmpty() ||
                tempMin.getText().toString().isEmpty() || tempMin.getText().toString().isEmpty() ||
                waterlvl.getText().toString().isEmpty() || ageYears.getText().toString().isEmpty() ||
                ageMonths.getText().toString().isEmpty() || place.getText().toString().isEmpty();

        if(fields == 1) {
            empty = empty || name.getText().toString().isEmpty();

        } else if(fields == 2 ) {
            empty = empty || name.getText().toString().isEmpty() || species.getText().toString().isEmpty();
        }
        return empty;
    }
}
