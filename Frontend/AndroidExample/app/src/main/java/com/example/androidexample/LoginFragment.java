package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private EditText usernameInput, passwordInput;
    private Button loginBtn, signupBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the XML layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // Hook up UI elements
        usernameInput = rootView.findViewById(R.id.username_input);
        passwordInput = rootView.findViewById(R.id.password_input);
        loginBtn = rootView.findViewById(R.id.login_btn);
        signupBtn = rootView.findViewById(R.id.signup_btn);

        // Example click listener
        loginBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            Toast.makeText(getActivity(), "Logging in " + username, Toast.LENGTH_SHORT).show();
            // TODO: replace with real login logic
        });

        signupBtn.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Go to signup", Toast.LENGTH_SHORT).show();
            // TODO: replace with navigation to signup fragment or page
        });

        return rootView;
    }
}