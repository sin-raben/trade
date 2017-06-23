package pro.gofman.trade.Docs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import pro.gofman.trade.Countragents.DeliveryPointAutoCompleteAdapter;
import pro.gofman.trade.Countragents.DeliveryPointObject;
import pro.gofman.trade.Items.ItemObject;
import pro.gofman.trade.Items.ItemsAutoCompleteAdapter;
import pro.gofman.trade.R;
import pro.gofman.trade.Trade;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DocShapkaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DocShapkaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DocShapkaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatePickerDialog mDateDialog;

    private OnFragmentInteractionListener mListener;

    public DocShapkaFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DocShapkaFragment newInstance(String param1, String param2) {
        DocShapkaFragment fragment = new DocShapkaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_docs, container, false);

        final AutoCompleteTextView countragent = (AutoCompleteTextView) view.findViewById(R.id.countragent_view);
        final ItemsAutoCompleteAdapter i_adapter = new ItemsAutoCompleteAdapter( view.getContext(), Trade.getWritableDatabase() );
        countragent.setAdapter( i_adapter );
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ItemObject io = (ItemObject) adapterView.getItemAtPosition(i);
                countragent.setText( io.getName(), true );

            }
        };
        countragent.setOnItemClickListener( clickListener );

        final Drawable x = ContextCompat.getDrawable( getContext(), android.support.v7.appcompat.R.drawable.abc_ic_clear_material );
        x.setBounds(0,0,x.getIntrinsicWidth(),x.getIntrinsicHeight());
        countragent.setCompoundDrawablesWithIntrinsicBounds(null, null, countragent.getText().toString().equals("") ? null : x, null);

        countragent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (countragent.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (motionEvent.getX() > countragent.getWidth() - countragent.getPaddingRight() - x.getIntrinsicWidth()) {
                    countragent.setText("");
                    countragent.setCompoundDrawables(null, null, null, null);
                }
                return false;
            }
        });

        countragent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                countragent.setCompoundDrawablesWithIntrinsicBounds(null,null,x,null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        countragent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i_adapter.setAllItems();
                countragent.setAdapter( i_adapter );
                countragent.showDropDown();
            }
        });




        final AutoCompleteTextView delivery_point = (AutoCompleteTextView) view.findViewById(R.id.delivery_point_view);
        DeliveryPointAutoCompleteAdapter dp_adapter = new DeliveryPointAutoCompleteAdapter( view.getContext(), Trade.getWritableDatabase() );
        delivery_point.setAdapter( dp_adapter );
        delivery_point.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeliveryPointObject io = (DeliveryPointObject) adapterView.getItemAtPosition(i);
                delivery_point.setText( io.getName(), true );
            }
        });

        final EditText delivery_date = (EditText) view.findViewById(R.id.delivery_date);
        delivery_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            delivery_date.setText( String.valueOf(i2) + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i) );
                        }
                    };

                    mDateDialog = new DatePickerDialog( view.getContext(), dateSetListener, mYear, mMonth, mDay );
                    mDateDialog.show();
                //}
            }
        });



        // Inflate the layout for this fragment
        return view;


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
