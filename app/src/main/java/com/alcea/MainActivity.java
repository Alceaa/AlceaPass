package com.alcea;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alcea.adapters.ServicesAdapter;
import com.alcea.models.Service;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbstractActivity {

    private RecyclerView servicesRecyclerView;
    private ServicesAdapter servicesAdapter;
    private List<Service> servicesList;
    private Button addServiceButton;
    private int extraFieldCount = 0;
    private static final int MAX_EXTRA_FIELDS = 5;

    @Override
    protected void initialize() {
        setContentView(R.layout.activity_main);
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
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String serviceName = serviceNameEditText.getText().toString();
            String servicePassword = servicePasswordEditText.getText().toString();
            if(serviceSave(serviceName, servicePassword)){
                dialog.dismiss();
            }
            else{
                TextView error = dialogView.findViewById(R.id.service_add_error);
                error.setText("Ошибка! Сервис с таким названием уже существует");
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setTextColor(Color.WHITE);
                negativeButton.setTextColor(Color.WHITE);
            });
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
        service.setPassword(servicePassword);
        databaseManager.createService(service);
        return true;
    }
}
