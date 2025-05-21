package com.example.lostandfoundappbygeo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {
    private RadioGroup rgPostType;
    private RadioButton rbLost, rbFound;
    private EditText etName, etPhone, etDescription, etDate;
    private Button btnSave, btnGetCurrentLocation;
    private ImageButton btnBack;
    private TextView tvTitle;
    private Calendar calendar;
    private DBHelper dbHelper;


    private AutocompleteSupportFragment autocompleteFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng selectedLatLng;
    private String selectedAddress;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission is required to use this feature",
                            Toast.LENGTH_LONG).show();
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
        }

        dbHelper = new DBHelper(this);
        calendar = Calendar.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tvTitle = findViewById(R.id.tvTitle);
        rgPostType = findViewById(R.id.rgPostType);
        rbLost = findViewById(R.id.rbLost);
        rbFound = findViewById(R.id.rbFound);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);

        rbLost.setChecked(true);

        setupDatePicker();

        setupPlacesAutocomplete();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermissionAndGetLocation();
            }
        });
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateField();
            }
        };

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateAdvertActivity.this,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etDate.setFocusable(false);
        etDate.setClickable(true);
    }

    private void setupPlacesAutocomplete() {
        autocompleteFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

            autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

            autocompleteFragment.setHint("Enter location");

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    selectedAddress = place.getAddress();
                    selectedLatLng = place.getLatLng();
                    if (selectedAddress != null) {
                        Toast.makeText(CreateAdvertActivity.this,
                                "Location selected: " + selectedAddress,
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Toast.makeText(CreateAdvertActivity.this,
                            "An error occurred: " + status.getStatusMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkLocationPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            getCurrentLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            selectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            getAddressFromLocation(location);
                        } else {
                            Toast.makeText(CreateAdvertActivity.this,
                                    "Unable to get current location. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        sb.append(", ");
                    }
                }

                selectedAddress = sb.toString();

                if (autocompleteFragment != null) {
                    autocompleteFragment.setText(selectedAddress);
                }

                Toast.makeText(this, "Location found: " + selectedAddress, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No address found for this location", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error getting address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDateField() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveItem() {
        if (validateInputs()) {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String location = selectedAddress;
            String type = rbLost.isChecked() ? "Lost" : "Found";

            Item item;
            if (selectedLatLng != null) {
                item = new Item(name, phone, description, date, location, type,
                        selectedLatLng.latitude, selectedLatLng.longitude);
            } else {
                item = new Item(name, phone, description, date, location, type);
            }

            long id = dbHelper.insertItem(item);

            if (id > 0) {
                Toast.makeText(this, "Item saved successfully!", Toast.LENGTH_SHORT).show();
                clearInputs();
                finish();
            } else {
                Toast.makeText(this, "Failed to save item!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name is required");
            isValid = false;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone number is required");
            isValid = false;
        }

        if (etDescription.getText().toString().trim().isEmpty()) {
            etDescription.setError("Description is required");
            isValid = false;
        }

        if (etDate.getText().toString().trim().isEmpty()) {
            etDate.setError("Date is required");
            isValid = false;
        }

        if (selectedAddress == null || selectedAddress.isEmpty()) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void clearInputs() {
        etName.setText("");
        etPhone.setText("");
        etDescription.setText("");
        etDate.setText("");
        selectedAddress = null;
        selectedLatLng = null;
        if (autocompleteFragment != null) {
            autocompleteFragment.setText("");
        }
        rbLost.setChecked(true);
    }
}