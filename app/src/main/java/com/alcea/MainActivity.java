package com.alcea;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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

import org.w3c.dom.Text;

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

        addServiceButton = findViewById(R.id.add_service_button);
        addServiceButton.setOnClickListener(v -> showAddServiceDialog());
    }

    private void showAddServiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Добавить новый сервис");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_service, null);
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
            String extraData = getExtraFieldsData(extraFieldsContainer);
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

    private String getExtraFieldsData(LinearLayout container){
        int size = container.getChildCount();
        String[] data = new String[size / 2];
        for(int i = 0; i < size; i++){
            if(container.getChildAt(i) instanceof EditText){
                data[i] = ((EditText) container.getChildAt(i)).getText().toString();
            }
        }
        return String.join(";", data);
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
        View dialogView = inflater.inflate(R.layout.dialog_service, null);
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
        builder.show();
    }
}
