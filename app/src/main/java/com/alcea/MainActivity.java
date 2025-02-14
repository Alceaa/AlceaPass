package com.alcea;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alcea.adapters.ServicesAdapter;
import com.alcea.models.Service;
import com.alcea.utils.PasswordEncoder;
import com.alcea.utils.Utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AbstractActivity {
    private Bundle extras;

    private RecyclerView servicesRecyclerView;
    private ServicesAdapter servicesAdapter;
    private List<Service> servicesList;
    private Button addServiceButton;
    private int extraFieldCount = 0;
    private static final int MAX_EXTRA_FIELDS = 5;

    @SuppressLint("ResourceType")
    @Override
    protected void initialize() {
        setContentView(R.layout.activity_main);
        extras = getIntent().getExtras();

        String[] filters = getResources().getStringArray(R.array.filter);
        ArrayAdapter filterAdapter = new ArrayAdapter(this, R.layout.filter_item, filters);
        AutoCompleteTextView autocompleteFilter = findViewById(R.id.autoCompleteFilter);
        autocompleteFilter.setAdapter(filterAdapter);
        autocompleteFilter.setOnItemClickListener((parent, view, position, id) -> {
            serviceItemsFilter(parent.getItemAtPosition(position).toString());
        });


        servicesRecyclerView = findViewById(R.id.services_recycler_view);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        servicesList = databaseManager.getServices();

        servicesAdapter = new ServicesAdapter(servicesList);
        servicesRecyclerView.setAdapter(servicesAdapter);

        addServiceButton = findViewById(R.id.add_service_button);
        addServiceButton.setOnClickListener(v -> showAddServiceDialog());
    }

    private void showAddServiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Добавить новый сервис");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_service, null);
        builder.setView(dialogView);

        EditText serviceNameEditText = dialogView.findViewById(R.id.service_name);
        EditText servicePasswordEditText = dialogView.findViewById(R.id.service_password);
        LinearLayout extraFieldsContainer = dialogView.findViewById(R.id.extra_fields_container);
        Button addFieldButton = dialogView.findViewById(R.id.add_field_button);
        addFieldButton.setOnClickListener(v -> {
            if(extraFieldCount < MAX_EXTRA_FIELDS){
                addExtraField(extraFieldsContainer);
            }
        });
        builder.setPositiveButton("Сохранить", null);
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setTextColor(Color.WHITE);
                negativeButton.setTextColor(Color.WHITE);
            });

        dialog.setOnShowListener(v1 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v2 -> {
            String serviceName = serviceNameEditText.getText().toString();
            String servicePassword = servicePasswordEditText.getText().toString();
            if(serviceSave(serviceName, servicePassword)){
                dialog.dismiss();
            }
            else{
                TextView error = dialogView.findViewById(R.id.service_add_error);
                error.setText("Ошибка! Сервис с таким названием уже существует");
            }
        }));
        dialog.show();
    }

    private void addExtraField(LinearLayout container){
        EditText extraFieldEditText = new EditText(this);
        Button removeFieldButton = new Button(this);
        removeFieldButton.setText("Удалить");
        removeFieldButton.setOnClickListener(v -> {
            container.removeView(extraFieldEditText);
            container.removeView(removeFieldButton);
            extraFieldCount--;
        });

        container.addView(extraFieldEditText);
        container.addView(removeFieldButton);
        extraFieldCount++;
    }
    private boolean serviceSave(String serviceName, String servicePassword){
        if (databaseManager.getService(serviceName) != null){
            return false;
        }
        Service service = new Service();
        service.setName(serviceName);
        service.setTimestamp(Utils.timestamp());
        try {
            String encrypted = PasswordEncoder.encrypt(servicePassword, extras.getString("master"));
            service.setPassword(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Service newService = databaseManager.createService(service);
        servicesList.add(newService);
        updateServiceList(servicesList.indexOf(newService));
        return true;
    }

    private void updateServiceList(){
        servicesAdapter.notifyDataSetChanged();
    }
    private void updateServiceList(int position){
        servicesAdapter.notifyItemInserted(position);
    }

    private void serviceItemsFilter(String filter){
        switch (filter){
            case "По названию":
                servicesList.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
                Collections.reverse(servicesList);
                break;
            case "По дате добавления":
                servicesList.sort(Comparator.comparing(o -> Utils.dateParse(o.getTimestamp())));
                break;
            default:
                break;
        }
        updateServiceList();
    }
}
