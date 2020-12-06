package afm.drc.dunamixadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetAllPicturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_all_pictures);

        ListView myGridView=findViewById(R.id.myListView);
        ProgressBar myDataLoaderProgressBar=findViewById(R.id.myDataLoaderProgressBar);

        new DataRetriever(GetAllPicturesActivity.this).retrieve(myGridView,myDataLoaderProgressBar);
    }

    static class Picture {
        private String name,description,imageURL;

        public Picture(String name, String description, String imageURL) {
            this.name = name;
            this.description = description;
            this.imageURL = imageURL;
        }
        public String getName() {return name;}
        public String getDescription() { return description; }
        public String getImageURL() { return imageURL; }
    }
    /*
   Our custom adapter class
    */
    public static class ListViewAdapter extends BaseAdapter {
        Context c;
        ArrayList<Picture> pictures;

        public ListViewAdapter(Context c, ArrayList<Picture> pictures) {
            this.c = c;
            this.pictures = pictures;
        }
        @Override
        public int getCount() {return pictures.size();}
        @Override
        public Object getItem(int i) {return pictures.get(i);}
        @Override
        public long getItemId(int i) {return i;}
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null)
            {
                view= LayoutInflater.from(c).inflate(R.layout.row_model,viewGroup,false);
            }

            TextView txtName = view.findViewById(R.id.nameTextView);
            TextView txtDescription = view.findViewById(R.id.descriptionTextView);
            ImageView ImageView = view.findViewById(R.id.teacherImageView);

            final Picture picture = (Picture) this.getItem(i);

            txtName.setText(picture.getName());
            txtDescription.setText(picture.getDescription());

            if(picture.getImageURL() != null && picture.getImageURL().length() > 0)
            {
                Picasso.get().load(picture.getImageURL()).placeholder(R.drawable.placeholder).into(ImageView);
            }else {
                Toast.makeText(c, "Empty Image URL", Toast.LENGTH_LONG).show();
                Picasso.get().load(R.drawable.placeholder).into(ImageView);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(c, picture.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }
    /*
    Our HTTP Client
     */
    public static class DataRetriever {

        private static final String PHP_MYSQL_SITE_URL="http://dunamixapp.000webhostapp.com/index.php";
        //INSTANCE FIELDS
        private final Context c;
        private ListViewAdapter adapter ;

        public DataRetriever(Context c) { this.c = c; }
        /*
        RETRIEVE/SELECT/REFRESH
         */
        public void retrieve(final ListView gv, final ProgressBar myProgressBar)
        {
            final ArrayList<Picture> pictures = new ArrayList<>();

            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);

            AndroidNetworking.get(PHP_MYSQL_SITE_URL)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            Picture picture;
                            try
                            {
                                for(int i=0;i<response.length();i++)
                                {
                                    jo=response.getJSONObject(i);

                                    int id=jo.getInt("id");
                                    String name=jo.getString("name");
                                    String description=jo.getString("description");
                                    String imageURL=jo.getString("image_url");

                                    picture= new Picture(name, description, PHP_MYSQL_SITE_URL + "images/" + imageURL);
                                    pictures.add(picture);
                                }
                                //SET TO SPINNER
                                adapter = new ListViewAdapter(c, pictures);
                                gv.setAdapter(adapter);
                                myProgressBar.setVisibility(View.GONE);

                            }catch (JSONException e)
                            {
                                myProgressBar.setVisibility(View.GONE);
                                Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIVED. "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            myProgressBar.setVisibility(View.GONE);
                            Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : "+anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}