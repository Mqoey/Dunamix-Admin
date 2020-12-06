package afm.drc.dunamixadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class UploadPicturesActivity extends AppCompatActivity {

    private Uri filePath;
    EditText nameEditText,descriptionEditText;
    private ImageView teacherImageView;
    private ProgressBar uploadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pictures);

        nameEditText=findViewById(R.id.nameEditText);
        descriptionEditText=findViewById(R.id.descriptionEditText);
        TextView showChooserBtn = findViewById(R.id.chooseBtn);
        Button sendToMySQLBtn = findViewById(R.id.sendBtn);
        teacherImageView=findViewById(R.id.imageView);
        uploadProgressBar=findViewById(R.id.myProgressBar);

        showChooserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        sendToMySQLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    //GET VALUES
                    String teacher_name = nameEditText.getText().toString();
                    String teacher_description = descriptionEditText.getText().toString();

                    SpiritualTeacher s = new SpiritualTeacher(teacher_name, teacher_description);

                    //upload data to mysql
                    //new MyUploader(MainActivity.this).upload(s, nameEditText, descriptionEditText, teacherImageView);
                    new MyUploader(UploadPicturesActivity.this).upload(s, nameEditText, descriptionEditText);

                } else {
                    Toast.makeText(UploadPicturesActivity.this, "PLEASE ENTER ALL FIELDS CORRECTLY ", Toast.LENGTH_LONG).show();
                }
            }
        });

        teacherImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    /*
    Our data object. THE POJO CLASS
     */
    static class SpiritualTeacher{
        private String name,description;
        public SpiritualTeacher(String name, String description) {
            this.name = name;
            this.description = description;
        }
        public String getName() {return name;}
        public String getDescription() {return description;}
    }
    /*
    CLASS TO UPLOAD BOTH IMAGES AND TEXT
     */
    public class MyUploader {
       // private static final String DATA_UPLOAD_URL="http://dunamixapp.000webhostapp.com/index.php";
       private static final String DATA_UPLOAD_URL="http://localhost/Dunamix/images.php";
        //INSTANCE FIELDS
        private final Context c;
        public MyUploader(Context c) {this.c = c;}
        /*
        SAVE/INSERT
         */
        public void upload(SpiritualTeacher s, final View...inputViews)
        {
            if(s == null){Toast.makeText(c, "No Data To Save", Toast.LENGTH_SHORT).show();}
            else {
                File imageFile;
                try {
                    imageFile = new File(getImagePath(filePath));

                }catch (Exception e){
                    Toast.makeText(c, "Please pick an Image From Right Place, maybe Gallery or File Explorer so that we can get its path."+e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                uploadProgressBar.setVisibility(View.VISIBLE);

                AndroidNetworking.upload(DATA_UPLOAD_URL)
                        .addMultipartFile("image",imageFile)
                        .addMultipartParameter("name",s.getName())
                        .addMultipartParameter("description",s.getDescription())
                        //.addMultipartParameter("image","upload")
                        .setTag("MYSQL_UPLOAD")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(response != null) {
                                    try{
                                        //SHOW RESPONSE FROM SERVER
                                        String responseString = response.get("message").toString();

                                        if (responseString.equalsIgnoreCase("Success")) {
                                            //RESET VIEWS
                                            Toast.makeText(c, "PHP SERVER RESPONSE : " + responseString, Toast.LENGTH_LONG).show();
                                            EditText nameEditText = (EditText) inputViews[0];
                                            EditText descriptionEditText = (EditText) inputViews[1];
                                            ImageView teacherImageView = (ImageView) inputViews[2];

                                            nameEditText.setText("");
                                            descriptionEditText.setText("");
                                            teacherImageView.setImageResource(R.drawable.placeholder);

                                        } else {
                                            Toast.makeText(c, "PHP WASN'T SUCCESSFUL. ", Toast.LENGTH_LONG).show();
                                        }
                                    }catch(Exception e)
                                    {
                                        e.printStackTrace();
                                        Toast.makeText(c, "JSONException "+e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(c, "NULL RESPONSE. ", Toast.LENGTH_LONG).show();
                                }
                                uploadProgressBar.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError(ANError error) {
                                error.printStackTrace();
                                uploadProgressBar.setVisibility(View.GONE);
                                Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : \n"+error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }
    /*
    Show File Chooser Dialog
     */
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image To Upload"), 1);
    }
    /*
    Receive Image data from FileChooser and set it to ImageView as Bitmap
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                teacherImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*
    Get Image Path provided its  android.net.Uri
     */
    public String getImagePath(Uri uri)
    {
        String[] projection={MediaStore.Images.Media.DATA};
        Cursor cursor=getContentResolver().query(uri,projection,null,null,null);
        if(cursor == null){
            return null;
        }
        int columnIndex= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(columnIndex);
        cursor.close();
        return s;
    }
    /*
    Perform basic data validation
     */
    private boolean validateData()
    {
        String name=nameEditText.getText().toString();
        String description=descriptionEditText.getText().toString();
        if( name == null || description == null){  return false;  }
        if(name == "" || description == ""){  return false;  }
        if(filePath == null){return false;}

        return true;
    }
}