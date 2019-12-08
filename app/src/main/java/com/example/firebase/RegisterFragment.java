package com.example.firebase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterFragment extends Fragment {

    EditText email_editText;
    EditText name_edittext;
    EditText password_editText;
    EditText confirmPassword;
    Button buttonRegister;

    private FirebaseAuth firebaseAuth;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email_editText = view.findViewById(R.id.emailId);
        name_edittext = view.findViewById(R.id.name);
        password_editText = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.forgotpassword);

        buttonRegister = view.findViewById(R.id.signin);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkEmptyField()){
                    if (password_editText.getText().length()<6){
                        password_editText.setError("Weak Password,Password should be atleast 6 digits");
                        password_editText.requestFocus();
                    }
                    else {
                        if (!password_editText.getText().toString().equals(confirmPassword.getText().toString())){
                            confirmPassword.setError("Password not match");
                            confirmPassword.requestFocus();
                        }
                        else{
                            String emaild = email_editText.getText().toString();
                            String password = password_editText.getText().toString();
                            String name = name_edittext.getText().toString();

                            createUser(emaild,password,name);
                        }
                    }
                }
            }
        });

    }

    public RegisterFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    public boolean checkEmptyField(){

        if (TextUtils.isEmpty(email_editText.getText().toString())){
            email_editText.setError("Email cannot found");
            email_editText.requestFocus();
            return true;

        }
        else if (TextUtils.isEmpty(password_editText.getText().toString())){
            password_editText.setError("Password cannot found");
            password_editText.requestFocus();
            return true;
        }
        else if (TextUtils.isEmpty(confirmPassword.getText().toString())){
            confirmPassword.setError("Confirm Password cannot found");
            confirmPassword.requestFocus();
            return true;
        }
        else if (TextUtils.isEmpty(name_edittext.getText().toString())){
            name_edittext.setError("Namme cannot found");
            name_edittext.requestFocus();
            return true;
        }


        return false;
    }

    public void createUser (String email,String password,String name){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(getActivity().getApplicationContext(),"Register Success!",Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    NavController navController = Navigation.findNavController(getActivity(),R.id.hostfragment);
                    navController.navigate(R.id.loginFragment);

                }
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
