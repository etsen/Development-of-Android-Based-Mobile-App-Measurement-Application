package com.example.memo.camerameasure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import android.support.v7.app.ActionBarDrawerToggle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PixelFormat;
import android.hardware.Camera;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;

import org.w3c.dom.Text;

import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

public class CameraFocusActivity extends AppCompatActivity implements SurfaceHolder.Callback, SensorEventListener
{
    private Camera           camera;
    private boolean          isPreviewRunning = false;
    private SurfaceView      surfaceView;
    private SurfaceHolder    surfaceHolder;

    TextView txt1;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mMagnetometer;
    private static final String TAG = "MeasureApp";
    private String[] units = { "meters", "cms", "feet", "inches"};
    private double AngleA = 0.0;
    private double AngleB = 0.0;
    private double X1 = 0.0;
    private double X2 = 0.0;
    private float[] mGravity;
    private float[] mMagnetic;
    private double h;
    private double D;
    private double H;
    private double L;
    float[] value = new float[3];
    private int unit;//default unit in mts
    //mts-0,cms-1,ft-2,in-3
    private Spinner spinner;
    DecimalFormat ThreeDForm = new DecimalFormat("#.###");
    float pressure;
    float accel[] =  new float[3];
    float result[] = new float[3];
    CharSequence test = "Results:\nDistance = "+D+"\nHeight = "+H+"\nLength = "+L+"\nCamera height = "+h+"\n"+units[unit]+"\n\n";
    private PowerManager.WakeLock wl;
    private PowerManager pm;
    Toast toast;
    final String filename = "Measures.txt";
    String m = "";

    String filepath = "MeasureStorage";
    File myInternalFile;
    File directory;

    //String[] files = new String[15];
    //int count = -1;

    @Override
    public void onCreate(Bundle icicle)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(icicle);
        Log.e(getClass().getSimpleName(), "onCreate");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.frames); // frames





        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        final Button distance = (Button) findViewById(R.id.button1);
        final Button height = (Button) findViewById(R.id.button7);
        final Button rst_valider = (Button) findViewById(R.id.button2);

        final Button save = (Button) findViewById(R.id.button8);


        txt1 = (TextView) findViewById(R.id.textView1);
        final Button adjh = (Button) findViewById(R.id.button3);
        final EditText edit = (EditText) findViewById(R.id.editText1);
        final Button length = (Button) findViewById(R.id.button4);

        distance.setEnabled(false);
        height.setEnabled(false);
        length.setEnabled(false);
        edit.setText("0.0");
        unit = 0;
        txt1.setText(test);

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        final File directory = contextWrapper.getDir(filepath, Context.MODE_APPEND);
        myInternalFile = new File(directory , filename);

		/*try
		{
		BufferedWriter buf = new BufferedWriter(new FileWriter(myInternalFile, true));
	    buf.write("Measures:\n");
	    buf.newLine();
	    buf.close();
	    }
		catch(IOException e)
		{
			e.printStackTrace();
		}*/

        //Toast.makeText(this, "Adjust the height", Toast.LENGTH_SHORT);

        try
        {
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,TAG);
            wl.acquire();
        }
        catch (Exception ex)
        {
            Log.e("exception", "here 1");
        }

//OBJECT DISTANCE (LENGTH)
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        distance.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                //old method
                AngleA = getDirection();//taking x value
                AngleA = Math.toRadians(90)-AngleA;
                D = Double.valueOf(ThreeDForm.format(Math.abs(h*(Math.tan((AngleA))))));
                test = "Results:\nObj Length = "+D+"\nObj Width = "+H+"\nObj Area = "+L+"\nCam height = "+h+"\n"+units[unit]+"\n\n";
                toast = Toast.makeText(getApplicationContext(), "Object distance calculated!", Toast.LENGTH_SHORT);
                toast.show();
                txt1.setText(test);
            }
        });

//OBJECT HEIGHT (change fxn here same as 'get distance/length')(changed fxn & to 'object width') H is width
        height.setOnClickListener(new View.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(View v)
                                      {

                                          AngleA = getDirection();//taking x value
                                          AngleA = Math.toRadians(90)-AngleA;
                                          H = Double.valueOf(ThreeDForm.format(Math.abs(h*(Math.tan((AngleA))))));
                                          test = "Results:\nLength = "+D+"\nWidth = "+H+"\nArea = "+L+"\nCamera height = "+h+"\n"+units[unit]+"\n\n";
                                          toast = Toast.makeText(getApplicationContext(), "Object width calculated!", Toast.LENGTH_SHORT);
                                          toast.show();
                                          txt1.setText(test);
                                      }

                                  }
        );

//RESET
        rst_valider.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                AngleA = 0.0;
                AngleB = 0.0;
                X1 = 0.0;
                X2 = 0.0;
                D=0.0;
                H=0.0;
                L=0.0;
                toast = Toast.makeText(getApplicationContext(), "Values reset!", Toast.LENGTH_SHORT);
                toast.show();
                txt1.setText("Results:\nLength = "+D+"\nWidth = "+H+"\nArea = "+L+"\nCamera height = "+h+"\n"+units[unit]+"\n\n");
            }
        });


//SAVE (from frame to transfer to save.xml)
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.save);
                final EditText save_name = (EditText) findViewById(R.id.saveText1);
                final Button saveButton = (Button) findViewById(R.id.savebutton1);

                saveButton.setOnClickListener(new View.OnClickListener()
                {

                    public void onClick(View view)
                    {
                        Log.d(TAG,"save button pressed");

                        //Log.d(TAG,"1");
                        //count = count+1;
                        //Log.d(TAG,"count"+count);
                        //files[count]=filename.toString();
                        //Log.d(TAG,"2");
                        try//on press of save button
                        {
                            //Log.d(TAG,"3");
                            Log.d(TAG,"1");
                            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                            final File directory = contextWrapper.getDir(filepath, Context.MODE_APPEND);
                            myInternalFile = new File(directory , filename);
                            Log.d(TAG,"2");
                            BufferedWriter buf = new BufferedWriter(new FileWriter(myInternalFile, true));
                            buf.append(save_name.getText().toString()+" "+test);
                            buf.newLine();
                            buf.close();
                            Log.d(TAG,"3");
                            //FileOutputStream fos = new FileOutputStream(myInternalFile);
                            //fos.write((save_name.getText().toString()+" "+test).getBytes());
                            //fos.close();
                            //m = m + save_name.getText().toString()+" "+test+"\n";
                            Log.d(TAG,"written to file");
                        }
                        catch (IOException e)
                        {
                            try
                            {
                                BufferedWriter buf = new BufferedWriter(new FileWriter(myInternalFile, true));
                                buf.write("\n\n"+save_name.getText().toString()+" "+test);
                            }
                            catch(IOException e2)
                            {
                                e.printStackTrace();
                            }
                        }
                        save_name.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(),"Successfully saved " +filename+" to Internal Storage",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });





            }
        });


//CAM HEIGHT
        adjh.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                h = Double.parseDouble(edit.getText().toString());
                if(h==0)
                {
                    toast = Toast.makeText(getApplicationContext(), "Height must be more than 0!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    txt1.setText("Camera height = "+h+"\n"+units[unit]+"\n\n");
                    distance.setEnabled(true);
                    height.setEnabled(true);
                    length.setEnabled(true);
                    toast = Toast.makeText(getApplicationContext(), "Phone height adjusted!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

//OBJECT LENGTH (changed to OBJECT AREA)
        length.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                //if(X1==0.0)
                //{
                //X1 = value[0];//taking z

                //}
                //else
                //{
                //X2 = (value[0]);//taking z

                //float theta = (float) Math.abs(Math.abs(X1)-Math.abs(X2));
                //arc of a circle logic;
                L = D*H;
                test = "Results:\nLength = "+D+"\nWidth = "+H+"\nArea = "+L+"\nCamera height = "+h+"\n"+units[unit]+"\n\n";
                toast = Toast.makeText(getApplicationContext(), "Object area calculated!", Toast.LENGTH_SHORT);
                toast.show();
                txt1.setText(test);

            }
        });
    }




    public void addListenerOnSpinnerItemSelection()
    {
        spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                Toast.makeText(arg0.getContext(), "Unit selected : " + arg0.getItemAtPosition(arg2).toString(),Toast.LENGTH_SHORT).show();

                if(arg2==0)
                {
                    //convert to mts
                    if(unit==1)
                    {//cm-m
                        h = Double.valueOf(ThreeDForm.format(h*0.01));
                        H = Double.valueOf(ThreeDForm.format(H*0.01));
                        D = Double.valueOf(ThreeDForm.format(D*0.01));
                        L = Double.valueOf(ThreeDForm.format(L*0.01));
                    }
                    else if(unit==2)
                    {//ft-m
                        h = Double.valueOf(ThreeDForm.format(h*.3048));
                        H = Double.valueOf(ThreeDForm.format(H*.3048));
                        D = Double.valueOf(ThreeDForm.format(D*.3048));
                        L = Double.valueOf(ThreeDForm.format(L*.3048));
                    }
                    else if(unit==3)
                    {//in-m
                        h = Double.valueOf(ThreeDForm.format(h*.0254));
                        H = Double.valueOf(ThreeDForm.format(H*.0254));
                        D = Double.valueOf(ThreeDForm.format(D*.0254));
                        L = Double.valueOf(ThreeDForm.format(L*.0254));
                    }
                    unit=0;
                    test = "Results:\nLength = "+D+"\nWidth = "+H+"\nArea = "+L+"\nCamera height = "+h+"\n"+units[unit]+"\n\n";
                    txt1.setText(test);
                }
                else if(arg2==1)
                {//convert to cms
                    if(unit==0)
                    {//m-cm
                        h = Double.valueOf(ThreeDForm.format(h*100));
                        H = Double.valueOf(ThreeDForm.format(H*100));
                        D = Double.valueOf(ThreeDForm.format(D*100));
                        L = Double.valueOf(ThreeDForm.format(L*100));
                    }
                    else if(unit==2)
                    {//ft-cm
                        h = Double.valueOf(ThreeDForm.format(h*30.48));
                        H = Double.valueOf(ThreeDForm.format(H*30.48));
                        D = Double.valueOf(ThreeDForm.format(D*30.48));
                        L = Double.valueOf(ThreeDForm.format(L*30.48));
                    }
                    else if(unit==3)
                    {//in-cm
                        h = Double.valueOf(ThreeDForm.format(h*2.54));
                        H = Double.valueOf(ThreeDForm.format(H*2.54));
                        D = Double.valueOf(ThreeDForm.format(D*2.54));
                        L = Double.valueOf(ThreeDForm.format(L*2.54));
                    }
                    unit=1;
                    test = "Results:\nLength = "+D+"\nWidth = "+H+"\nArea = "+L+"\nCamera height = "+h+"\n"+units[unit]+"\n\n";
                    txt1.setText(test);
                }
                else if(arg2==2)
                {//convert to feet
                    if(unit==0)
                    {//m-ft
                        h = Double.valueOf(ThreeDForm.format(h*3.28084));
                        H = Double.valueOf(ThreeDForm.format(H*3.28084));
                        D = Double.valueOf(ThreeDForm.format(D*3.28084));
                        L = Double.valueOf(ThreeDForm.format(L*3.28084));
                    }
                    else if(unit==1)
                    {//cm-ft
                        h = Double.valueOf(ThreeDForm.format(h*0.0328084));
                        H = Double.valueOf(ThreeDForm.format(H*0.0328084));
                        D = Double.valueOf(ThreeDForm.format(D*0.0328084));
                        L = Double.valueOf(ThreeDForm.format(L*0.0328084));
                    }
                    else if(unit==3)
                    {//in-ft
                        h = Double.valueOf(ThreeDForm.format(h*0.0833333));
                        H = Double.valueOf(ThreeDForm.format(H*0.0833333));
                        D = Double.valueOf(ThreeDForm.format(D*0.0833333));
                        L = Double.valueOf(ThreeDForm.format(L*0.0833333));
                    }
                    unit=2;
                    test = "Results:\nLength = "+D+"\nWidth = "+H+"\nArea = "+L+"\nCamera height = "+h+"\n"+units[unit]+"\n\n";
                    txt1.setText(test);
                }
                else
                {//convert to in
                    if(unit==0)
                    {//m-in
                        h = Double.valueOf(ThreeDForm.format(h*39.3701));
                        H = Double.valueOf(ThreeDForm.format(H*39.3701));
                        D = Double.valueOf(ThreeDForm.format(D*39.3701));
                        L = Double.valueOf(ThreeDForm.format(L*39.3701));
                    }
                    else if(unit==1)
                    {//cm-in
                        h = Double.valueOf(ThreeDForm.format(h*0.393701));
                        H = Double.valueOf(ThreeDForm.format(H*0.393701));
                        D = Double.valueOf(ThreeDForm.format(D*0.393701));
                        L = Double.valueOf(ThreeDForm.format(L*0.393701));
                    }
                    else if(unit==2)
                    {//ft-in
                        h = Double.valueOf(ThreeDForm.format(h*12));
                        H = Double.valueOf(ThreeDForm.format(H*12));
                        D = Double.valueOf(ThreeDForm.format(D*12));
                        L = Double.valueOf(ThreeDForm.format(L*12));
                    }
                    unit=3;
                    test = "Results:\nLength = "+D+"\nWidth = "+H+"\nArea = "+L+"\nCamera height = "+h+"\n"+units[unit]+"\n\n";
                    txt1.setText(test);
                }
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });
    }





    public void addListenerOnButton()
    {
        spinner = (Spinner) findViewById(R.id.spinner1);
    }

    @Override
    public void onBackPressed()
    {
        //onCreate(null);
        System.exit(0);
        startActivity(new Intent(this, main_menu.class));
    }





    //SHOW OPTION MENU ON RIGHT
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu)
    {
        Log.d(TAG,"Menu button pressed");
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }


    //option menu tuto,obj,inte,view,dlt data
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

           // case R.id.menu_tutorial:
             //   Log.d(TAG, "tutorial");
             //   setContentView(R.layout.tutorial);
              //  return true;


          //  case R.id.subinterior:
           //     Log.d(TAG, "interior measure");
              //  //setContentView(R.layout.frames);
            //    Intent myIntent = new Intent(this, CameraFocusActivity.class);
             //   startActivity(myIntent);
             //   return true;

          //  case R.id.subobject:
           //     Log.d(TAG, "object measure");
             //   //setContentView(R.layout.activity1);
               // //make another activity for this and intent here
               // //Intent myIntent2 = new Intent(this, ImageAccess.class);
               // //startActivity(myIntent2);
             //   startActivity(new Intent(CameraFocusActivity.this, ImageAccess.class));
               // return true;


            //view data
            case R.id.menu_search:
                Log.d(TAG,"view button");
                setContentView(R.layout.view);
                TextView viewText = (TextView) findViewById(R.id.viewtextView1);
                String myData = "";
                //Toast.makeText(MainActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                try
                {        	Log.d(TAG,"11");
                    ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                    final File directory = contextWrapper.getDir(filepath, Context.MODE_APPEND);
                    myInternalFile = new File(directory , filename);
                    Log.d(TAG,"12");
                    FileInputStream fis = new FileInputStream(myInternalFile);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br =  new BufferedReader(new InputStreamReader(in));
                    Log.d(TAG,"13");
                    String strLine;
                    while ((strLine=br.readLine()) != null)
                    {
                        myData = myData + "\n" + strLine;
                    }
                    in.close();

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                viewText.setText(myData);
                Toast toast1 = Toast.makeText(this,"Data retrieved from Internal Storage",Toast.LENGTH_SHORT);
                toast1.show();
                return true;



            case R.id.menu_delete:
                setContentView(R.layout.view);
                viewText = (TextView) findViewById(R.id.viewtextView1);
                try
                {
                    PrintWriter writer = new PrintWriter(myInternalFile);
                    writer.print("Measurements:\n\n");
                    writer.close();
                    Toast toast2 = Toast.makeText(this,"Data deleted.",Toast.LENGTH_SHORT);
                    toast2.show();
                    viewText.setText("");
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        try
        {
            Log.v("On resume called","------ wl aquire next!");
            wl.acquire();
        }
        catch(Exception ex)
        {
        }
        Log.e(getClass().getSimpleName(), "onResume");
        super.onResume();
        //
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
        //
    }

    @Override
protected void onSaveInstanceState(Bundle outState)
{
    super.onSaveInstanceState(outState);
}

    @Override
    protected void onPause()
    {
        try
        {
            Log.v("on pause called", "on pause called");
            wl.release();
        }
        catch(Exception ex)
        {
            Log.e("Exception in on menu", "exception on menu");
        }
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /*@Override
    protected void onStop()
    {
        Log.e(getClass().getSimpleName(), "onStop");
        super.onStop();
        //
        mSensorManager.unregisterListener(this);

        //
    }*/

    @Override
    protected void onUserLeaveHint()
    {
        try
        {
            Log.v("on user leave hint pressed", "on user leave hint pressesd");
            wl.release();
        }
        catch(Exception ex)
        {
            Log.e("Exception in on menu", "exception on menu");
        }
        super.onUserLeaveHint();
    }

    //FXN HERE ONWARDS R CAMERA FOCUSING
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.e(getClass().getSimpleName(), "surfaceCreated");
        camera = Camera.open();
        //camera.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
        //Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO = camera.setFocusMode();
        Camera.Parameters parameters = camera.getParameters();

        float[] distances = new float[3];
        parameters.getFocusDistances(distances);
    }





    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Log.e(getClass().getSimpleName(), "surfaceChanged");
        if (isPreviewRunning)
        {
            camera.stopPreview();
        }
        Camera.Parameters p = camera.getParameters();
        // camera.getParameters().getFocusMode();
        //String focusMode = null;
        //focusMode = findSettableValue(p.getSupportedFocusModes(),Camera.Parameters.FOCUS_MODE_MACRO,"edof");
        p.getSupportedPictureSizes();
        camera.setParameters(p);
        try
        {
            camera.setPreviewDisplay(holder);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        camera.startPreview();
        isPreviewRunning = true;
    }

    /*private static String findSettableValue(Collection<String> supportedValues,String... desiredValues)
    {
    	Log.i(TAG, "Supported values: " + supportedValues);
    	String result = null;
    	if (supportedValues != null)
    	{
    		for (String desiredValue : desiredValues)
    		{
    			if (supportedValues.contains(desiredValue))
    			{
    				result = desiredValue;
    				break;
    			}
    		}
    	}
    	Log.i(TAG, "Settable value: " + result);
    	return result;
    }*/

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.e(getClass().getSimpleName(), "surfaceDestroyed");
        camera.stopPreview();
        isPreviewRunning = false;
        camera.release();
    }

    //
    public void onSensorChanged(SensorEvent event)
    {

        switch(event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values.clone();
                //onAccelerometerChanged(values[0],values[1],values[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetic = event.values.clone();
                break;
            case Sensor.TYPE_PRESSURE:
                pressure = event.values[0];
                pressure = pressure*100;
            default:
                return;
        }
        if(mGravity != null && mMagnetic != null)
        {
            getDirection();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO Auto-generated method stub

    }

    private float getDirection()
    {
        float[] temp = new float[9];
        float[] R = new float[9];

        //Load rotation matrix into R
        SensorManager.getRotationMatrix(temp, null, mGravity, mMagnetic);

        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_X, SensorManager.AXIS_Z, R);

        //Return the orientation values

        SensorManager.getOrientation(R, value);

        //value[0] - Z, value[1]-X, value[2]-Y in radians

        return value[1];       //return x
    }
}