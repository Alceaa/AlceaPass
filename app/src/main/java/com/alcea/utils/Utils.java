package com.alcea.utils;

import static com.alcea.utils.Contsants.DIGITS;
import static com.alcea.utils.Contsants.LOWERCASE;
import static com.alcea.utils.Contsants.SPECIAL_CHARACTERS;
import static com.alcea.utils.Contsants.UPPERCASE;

import android.widget.EditText;
import android.widget.LinearLayout;

import com.alcea.models.Service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    public static String timestamp(){
        return dateFormat.format(new Date());
    }

    public static Date dateParse(String date){
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static int countEditText(LinearLayout container){
        int c = 0;
        for(int i = 0; i < container.getChildCount(); i++){
            if(container.getChildAt(i) instanceof EditText){
                c++;
            }
        }
        return c;
    }

    public static String getEditTextData(LinearLayout container){
        int size = container.getChildCount();
        String[] data = new String[size / 2];
        int dataIndex = 0;
        for(int i = 0; i < size; i++){
            if(container.getChildAt(i) instanceof EditText){
                data[dataIndex] = ((EditText) container.getChildAt(i)).getText().toString();
                dataIndex++;
            }
        }
        return String.join(";", data);
    }

    public static int findServiceByName(List<Service> list, String name){
        for(Service service : list){
            if(service.getName().equals(name)){
                return list.indexOf(service);
            }
        }
        return -1;
    }

    public static String generatePassword(int len){
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        String allCharacters = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
        String lettersAndDigits = LOWERCASE + UPPERCASE + DIGITS;
        char[] lettersAndDigitsArray = lettersAndDigits.toCharArray();
        char[] charactersArray = allCharacters.toCharArray();

        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(charactersArray.length);
            if (i == 0){
                randomIndex = random.nextInt(lettersAndDigitsArray.length);
                password.append(lettersAndDigitsArray[randomIndex]);
            }else{
                password.append(charactersArray[randomIndex]);
            }
        }

        return password.toString();
    }
}
