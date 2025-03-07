package com.alcea;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alcea.adapters.ServicesAdapter;
import com.alcea.interfaces.RecyclerItemClickListener;
import com.alcea.models.Service;
import com.alcea.utils.PasswordEncoder;
import com.alcea.utils.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AbstractActivity {
    private Bundle extras;

    private ServicesAdapter servicesAdapter;
    private List<Service> servicesList;
    private int extraFieldCount = 0;
    private static final int MAX_EXTRA_FIELDS = 5;
    private EditText servicePasswordEditText;

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


        RecyclerView servicesRecyclerView = findViewById(R.id.services_recycler_view);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        servicesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, servicesRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showInfoServiceDialog(view);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        servicesList = databaseManager.getServices();

        servicesAdapter = new ServicesAdapter(servicesList);
        servicesRecyclerView.setAdapter(servicesAdapter);

        Button addServiceButton = findViewById(R.id.add_service_button);
        addServiceButton.setOnClickListener(v -> showAddServiceDialog());
    }

    private void showAddServiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Добавить новый сервис");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_service_add, null);
        builder.setView(dialogView);

        EditText serviceNameEditText = dialogView.findViewById(R.id.service_name);
        servicePasswordEditText = dialogView.findViewById(R.id.service_password);
        LinearLayout extraFieldsContainer = dialogView.findViewById(R.id.extra_fields_container);
        Button addFieldButton = dialogView.findViewById(R.id.add_field_button);
        addFieldButton.setOnClickListener(v -> {
            extraFieldCount = Utils.countEditText(extraFieldsContainer);
            if(extraFieldCount < MAX_EXTRA_FIELDS){
                addExtraField(extraFieldsContainer);
            }
        });
        Button generatePasswordButton = dialogView.findViewById(R.id.generate_password);
        generatePasswordButton.setOnClickListener(v -> {
            showGeneratePasswordDialog();
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
            String extraData = Utils.getEditTextData(extraFieldsContainer);
            if(serviceSave(serviceName, servicePassword, extraData)){
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
        extraFieldEditText.setTextColor(Color.WHITE);
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

    private void setExtraFieldsData(LinearLayout container, String data){
        String[] dataArr = data.split(";");
        for(String dataVal : dataArr){
            if(!dataVal.isEmpty()) {
                EditText extraFieldEditText = new EditText(this);
                extraFieldEditText.setTextColor(Color.WHITE);
                extraFieldEditText.setText(dataVal);
                Button removeFieldButton = new Button(this);
                removeFieldButton.setText("Удалить");
                removeFieldButton.setOnClickListener(v -> {
                    container.removeView(extraFieldEditText);
                    container.removeView(removeFieldButton);
                });

                container.addView(extraFieldEditText);
                container.addView(removeFieldButton);
            }
        }
    }

    private boolean serviceSave(String serviceName, String servicePassword, String extraData){
        if (databaseManager.getService(serviceName) != null){
            return false;
        }
        Service service = new Service();
        service.setName(serviceName);
        service.setTimestamp(Utils.timestamp());
        service.setExtraData(extraData);
        try {
            String encrypted = PasswordEncoder.encrypt(servicePassword, extras.getString("master"));
            service.setPassword(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Service newService = databaseManager.createService(service);
        servicesList.add(newService);
        updateServiceListInsert(servicesList.indexOf(newService));
        return true;
    }
    private boolean serviceUpdate(Service service, String serviceName, String servicePassword, String extraData){
        if(!service.getName().equals(serviceName) && databaseManager.getService(serviceName) != null){
            return false;
        }
        if(service.getName().equals(serviceName) && service.getPassword().equals(servicePassword)
        && service.getExtraData().equals(extraData)){
            return true;
        }
        int pos = Utils.findServiceByName(servicesList, service.getName());
        service.setName(serviceName);
        service.setTimestamp(Utils.timestamp());
        service.setExtraData(extraData);
        try {
            String encrypted = PasswordEncoder.encrypt(servicePassword, extras.getString("master"));
            service.setPassword(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        databaseManager.updateService(service);
        servicesList.set(pos, service);
        updateServiceListChange(pos);
        return true;
    }

    private void updateServiceList(){
        servicesAdapter.notifyDataSetChanged();
    }
    private void updateServiceListInsert(int position){
        servicesAdapter.notifyItemInserted(position);
    }
    private void updateServiceListChange(int position){
        servicesAdapter.notifyItemChanged(position);
    }

    private void serviceItemsFilter(String filter){
        switch (filter){
            case "По названию":
                servicesList.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
                break;
            case "По дате добавления":
                servicesList.sort(Comparator.comparing(o -> Utils.dateParse(o.getTimestamp())));
                break;
            default:
                break;
        }
        Collections.reverse(servicesList);
        updateServiceList();
    }

    private void showInfoServiceDialog(View item){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        TextView serviceName = item.findViewById(R.id.service_name);
        builder.setTitle("Информация о " + serviceName.getText().toString());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_service_info, null);
        builder.setView(dialogView);

        Service service = databaseManager.getService(serviceName.getText().toString());

        EditText serviceNameEditText = dialogView.findViewById(R.id.service_name);
        serviceNameEditText.setText(service.getName());
        EditText servicePasswordEditText = dialogView.findViewById(R.id.service_password);
        try {
            servicePasswordEditText.setText(PasswordEncoder.decrypt(service.getPassword(), extras.getString("master")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LinearLayout extraFieldsContainer = dialogView.findViewById(R.id.extra_fields_container);
        setExtraFieldsData(extraFieldsContainer, service.getExtraData());
        Button addFieldButton = dialogView.findViewById(R.id.add_field_button);
        addFieldButton.setOnClickListener(v -> {
            extraFieldCount = Utils.countEditText(extraFieldsContainer);
            if(extraFieldCount < MAX_EXTRA_FIELDS){
                addExtraField(extraFieldsContainer);
            }
        });
        Button saveButton = dialogView.findViewById(R.id.save_button);
        AlertDialog dialog = builder.create();
        saveButton.setOnClickListener(v -> {
            String newServiceName = serviceNameEditText.getText().toString();
            String newServicePassword = servicePasswordEditText.getText().toString();
            String newExtraData = Utils.getEditTextData(extraFieldsContainer);
            if(serviceUpdate(service, newServiceName, newServicePassword, newExtraData)){
                dialog.dismiss();
            }
            else{
                TextView error = dialogView.findViewById(R.id.service_add_error);
                error.setText("Ошибка! Сервис с таким названием уже существует");
            }
        });
        dialog.show();
    }

    private void showGeneratePasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Сгенерировать пароль");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_generate_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button easyButton = dialogView.findViewById(R.id.easy_button);
        Button middleButton = dialogView.findViewById(R.id.middle_button);
        Button hardButton = dialogView.findViewById(R.id.hard_button);
        easyButton.setOnClickListener(v -> {
            servicePasswordEditText.setText(Utils.generatePassword(4));
            dialog.dismiss();
        });
        middleButton.setOnClickListener(v -> {
            servicePasswordEditText.setText(Utils.generatePassword(8));
            dialog.dismiss();
        });
        hardButton.setOnClickListener(v -> {
            servicePasswordEditText.setText(Utils.generatePassword(12));
            dialog.dismiss();
        });
        dialog.show();
    }
}
