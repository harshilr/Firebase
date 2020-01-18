package com.example.firebase;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;
import org.w3c.dom.Text;


public class Dashboard extends Fragment {

    TextView name;
    Button button_signout, deleteButton;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    public Dashboard(){

    }



    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.text_name);
        button_signout = view.findViewById(R.id.signout);
        deleteButton = view.findViewById(R.id.deleteButton);

        redFireStore();

        button_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                NavController navController = Navigation.findNavController(getActivity(),R.id.hostfragment);
                navController.navigate(R.id.loginFragment);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteUser(getView());


            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = getArguments().getParcelable("user");
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    public void redFireStore(){
        DocumentReference documentReference = firebaseFirestore.collection("user").document(firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        System.out.println(documentSnapshot.getData());

                        name.setText("Welcome "+documentSnapshot.get("name")+"!");
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void deleteUser(View view){

        final View popUpView = getActivity().getLayoutInflater().inflate(R.layout.popup_message,null);
        final PopupWindow popupWindow = new PopupWindow(popUpView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT,true);

        if(Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }

        final EditText email_editText = popUpView.findViewById(R.id.emailId_pop);
        final EditText password_editText = popUpView.findViewById(R.id.password_pop);
        Button login_button = popUpView.findViewById(R.id.login_pop);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),email_editText.getText(),Toast.LENGTH_LONG).show();

                if (password_editText.getText().toString().length()<6){
                    password_editText.setError("Invalid Password,Password is less than 6 character");
                    password_editText.requestFocus();
                }
                else {
                    if (TextUtils.isEmpty(email_editText.getText().toString())){
                        email_editText.setError("Email cannot be empty!");
                        email_editText.requestFocus();
                    }
                    else if (TextUtils.isEmpty(password_editText.getText().toString())){
                        password_editText.setError("Password cannot be empty!");
                        password_editText.requestFocus();
                    }
                    else {
                        AuthCredential authCredential = EmailAuthProvider.getCredential(email_editText.getText().toString(),password_editText.getText().toString());
                        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    firebaseFirestore.collection("user").document(firebaseUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            NavOptions navOptions = new NavOptions.Builder()
                                                                    .setPopUpTo(R.id.dashboard, true).build();

                                                            NavController navController = Navigation.findNavController(getActivity(), R.id.hostfragment);
                                                            navController.navigate(R.id.loginFragment, null, navOptions);
                                                            popupWindow.dismiss();
                                                        }
                                                    }
                                                });


                                            }
                                            else{

                                                System.out.println("Delete Task :"+task.getException().getMessage());
                                            }
                                        }
                                    });
                                }
                                else{
                                    System.out.println("ReAuth Task :"+task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(getView(), Gravity.CENTER,0,0);

    }

}
