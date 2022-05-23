package be.kuleuven.buddy.other;

import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import be.kuleuven.buddy.R;

public class FieldChecker {

    private final EditText moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place, name, species;
    private final TextView errorMessage;
    private final Boolean fields;

    public FieldChecker(EditText moistMin, EditText moistMax, EditText lightMin, EditText lightMax, EditText tempMin, EditText tempMax, EditText waterlvl, EditText ageYears, EditText ageMonths, EditText place, EditText name, EditText species, TextView errorMessage, Boolean fields) {
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
        this.fields = fields;
    }

    public void setFilters(){
        moistMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        moistMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMin.setFilters(new InputFilter[]{ new InputFilterMinMax("-10", "40")});
        tempMax.setFilters(new InputFilter[]{ new InputFilterMinMax("-10", "40")});
        waterlvl.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        ageMonths.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "12")});
    }

    public boolean checkFields() {
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
                ageMonths.getText().toString().isEmpty() || place.getText().toString().isEmpty() ||
                name.getText().toString().isEmpty();
        if(fields) empty = empty || name.getText().toString().isEmpty() || species.getText().toString().isEmpty();
        return empty;
    }

    public String calculatePlantDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat plantDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        calendar.add(Calendar.MONTH, -Integer.parseInt(ageMonths.getText().toString()));
        calendar.add(Calendar.YEAR, -Integer.parseInt((ageYears.getText().toString())));
        return plantDate.format(calendar.getTime());
    }
}
