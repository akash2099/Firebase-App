package com.akash2099.fragment_tutorial;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_1 extends Fragment {

    // below constructor is required when directly declaring a fragment
    public Fragment_1() {
        // Required empty public constructor
    }
    // for this case its not required

    private static final String ARG_TEXT = "argTextKey";
    private static final String ARG_NUMBER = "argNumberKey";

    private String text_received="";
    private int number_received=0;

    public static Fragment_1 createFor(String text,int number) {
        Fragment_1 fragment = new Fragment_1();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_NUMBER, number);
        fragment.setArguments(args);
//        System.out.println("Hi1"+text);
//        System.out.println("Hi2"+number);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        Bundle args = getArguments();
        if(args!=null){
            text_received=args.getString(ARG_TEXT);
            number_received=args.getInt(ARG_NUMBER);
//            System.out.println("Hi1"+text_received);
//            System.out.println("Hi2"+number_received);
        }
//        final String text_received = args != null ? args.getString(ARG_TEXT) : "";
//        final String number_received = args != null ? args.getString(ARG_NUMBER) : "";

        final String text_final=text_received+number_received;

        TextView textView = view.findViewById(R.id.text_frag1);
        textView.setText(text_final);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(v.getContext(), text_final, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
