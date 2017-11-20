package Fragments;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.raul.photonotes.ListPhotos;
import com.example.raul.photonotes.Metadatos;
import com.example.raul.photonotes.R;
import com.facebook.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import Adapters.RecycleViewCustomAdapter;
import Adapters.RecycleViewCustomAdapterCourses;
import DB.Modelo.Cursando;
import DB.Modelo.Materia;
import DB.Modelo.Photo;
import DB.Modelo.PhotosCRUD;
import DB.Modelo.Usuario;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubjectFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvCourses;
    private static final String TAG = "RecyclerViewFragment";
    private FloatingActionButton fabAddCourse;
    protected RecyclerView.LayoutManager mLayoutManager;

    private EditText ed_nombre;
    private OnFragmentInteractionListener mListener;
    private PhotosCRUD crud;
    Profile profile;
    int id;
    int idCursando;
    Usuario user;
    
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    Metadatos metadatos;
    String v_nombre, v_fecha;
    String horainit,horafin;
    ArrayList<Photo> photosPaths = new ArrayList<>();

    
    public SubjectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubjectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubjectFragment newInstance(String param1, String param2) {
        SubjectFragment fragment = new SubjectFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        metadatos = new Metadatos();
        View rootView = inflater.inflate(R.layout.fragment_subject, container, false);
        rootView.setTag(TAG);
        profile = Profile.getCurrentProfile();
        crud = new PhotosCRUD(getActivity());

        rvCourses = (RecyclerView) rootView.findViewById(R.id.rvCourses);
        rvCourses.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvCourses.setLayoutManager(mLayoutManager);

        fabAddCourse= (FloatingActionButton) rootView.findViewById(R.id.fabAddCourse);
        fabAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Course", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                AlertDialog.Builder mBuilder = new  AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater(getArguments()).inflate(R.layout.dialog_spinner,null);
                mBuilder.setTitle("Añadir Materia");
                final Spinner mSpinner =(Spinner)mView.findViewById(R.id.spinner);
                //final TextView prueba =(TextView)mView.findViewById(R.id.prueba);

                ed_nombre= (EditText)mView.findViewById(R.id.edit_nombre);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.horario));

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);

                Button bFrom = (Button) mView.findViewById(R.id.button_from);
                Button bTo = (Button) mView.findViewById(R.id.button_to);
                final TextView txt_from=(TextView) mView.findViewById(R.id.txt_from);
                final TextView txt_to=(TextView) mView.findViewById(R.id.txt_to);

                //Time Picker
                bFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Auto-generated method stub
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                txt_from.setText( selectedHour + ":" + selectedMinute);
                                horainit =  selectedHour + ":" + selectedMinute;
                            }
                        }, hour, minute, DateFormat.is24HourFormat(getActivity()));//Yes 24 hour time

                        mTimePicker.show();
                    }
                });

                bTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Auto-generated method stub
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                txt_to.setText( selectedHour + ":" + selectedMinute);
                                horafin =  selectedHour + ":" + selectedMinute;
                                Log.d("Hora Fin: ",horafin);

                            }
                        }, hour, minute, DateFormat.is24HourFormat(getActivity()));//Yes 24 hour time

                        mTimePicker.show();
                    }
                });



                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        v_nombre="";
                        if(!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose a day")){
                            v_nombre=ed_nombre.getText().toString();
                            v_fecha = mSpinner.getSelectedItem().toString();

                            if(v_nombre.isEmpty())
                            {
                                Toast.makeText(getActivity(),"Error: Algun campo esta vacio"
                                        , Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            /*
                            Si todo Sale bien
                            * */
                            id= crud.newMateria(new Materia(0,v_nombre));
                            user = crud.selectUsuario(profile.getName());
                            //Materia materia = crud.selectMateria()
                            //TODO PEDIR PERMISO DE STORAGE...DESPUES MANDA LLAMAR A METADATOS
                            idCursando = crud.newCursando(new Cursando(0,user.getId_user(),id,"","",v_fecha,txt_from.getText().toString(),txt_to.getText().toString()));
                           // Log.d("datos: ", v_fecha + " " + horainit + " " + horafin);
                            requestPermission();

                           // ArrayList<Photo> pictures = metadatos.getAllShownImagesPath(getActivity(),v_fecha,txt_from.getText().toString(),txt_to.getText().toString());
                            /*
                            for(int i = 0; i<pictures.size();i++){
                                crud.newPhoto(new Photo(0,idCursando,user.getId_user(),id,pictures.get(i).getPath(),pictures.get(i).getFecha()));
                            }
                            */

                            RecycleViewCustomAdapterCourses adapter = new RecycleViewCustomAdapterCourses(getActivity(),crud.getMaterias(), new Adapters.RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    Toast.makeText(getContext(),"Position: " +position, Toast.LENGTH_SHORT);
                                }
                            });
                            rvCourses.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            Log.d("ID:",id+"");
                            Toast.makeText(getActivity(),mSpinner.getSelectedItem().toString()+" "
                                    + v_nombre, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();


                        }
                        else{
                            Toast.makeText(getActivity(),"Error: Debes de seleccionar un dia"
                                    , Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog =mBuilder.create();
                dialog.show();

            }
        });

        //ArrayList<Materia> courses = crud.getMaterias();

        RecycleViewCustomAdapterCourses adapter = new RecycleViewCustomAdapterCourses(getActivity(),crud.getMaterias(), new Adapters.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(),"Position: " +position, Toast.LENGTH_SHORT);
                Intent inte = new Intent(getActivity(), ListPhotos.class);
                inte.putExtra("idMateria",id);
                startActivity(inte);
            }
        });
        rvCourses.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

     private void requestPermission() {

         if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                 != PackageManager.PERMISSION_GRANTED) {
             //debemos mostrar un mensaje
             if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                     Manifest.permission.READ_EXTERNAL_STORAGE)) {
                 //mostramos una explicacind eque no acepto dar permiso para acceder a la libreria

             } else  {
                 //pedimos permiso
                 ActivityCompat.requestPermissions(getActivity(),
                         new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                         REQUEST_EXTERNAL_STORAGE);

             }
         } else {
            //TODO: AQUI LLAMA A LOS METADATOS Y REGRESA OBJETO FOTO CON PATHS Y FECHAS PARA LA MATERIA CREADA
            //Log.d("permiso", "no se pide");
            photosPaths= metadatos.getAllShownImagesPath(getActivity(),v_fecha,horainit,horafin);
            /*for(int i = 0; i<photosPaths.size();i++){
                Log.d("Path:" , photosPaths.get(i).getPath() +" " + photosPaths.get(i).getFecha());
                //crud.newPhoto(new Photo(0,idCursando,user.getId_user(),id,photosPaths.get(i).getPath(),photosPaths.get(i).getFecha()));
            }*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                   // Log.e("value", "Permission Granted, Now you can use local drive .");
                    //TODO: AQUI LLAMA A LOS METADATOS Y REGRESA OBJETO FOTO CON PATHS Y FECHAS PARA LA MATERIA CREADA
                    //Log.d("permiso", "no se pide");
                    photosPaths=metadatos.getAllShownImagesPath(getActivity(),v_fecha,horainit,horafin);
                    /*for(int i = 0; i<photosPaths.size();i++){
                        Log.d("Path:" , photosPaths.get(i).getPath() +" " + photosPaths.get(i).getFecha());
                        //crud.newPhoto(new Photo(0,idCursando,user.getId_user(),id,photosPaths.get(i).getPath(),photosPaths.get(i).getFecha()));
                    }*/
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

}

