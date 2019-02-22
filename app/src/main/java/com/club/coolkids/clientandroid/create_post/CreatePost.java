package com.club.coolkids.clientandroid.create_post;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.display_posts.DisplayPosts;
import com.club.coolkids.clientandroid.models.dtos.CreatePictureDTO;
import com.club.coolkids.clientandroid.models.dtos.CreatePostDTO;
import com.club.coolkids.clientandroid.exceptions.ImageTooBigException;
import com.club.coolkids.clientandroid.exceptions.TooManyImagesToUploadException;
import com.club.coolkids.clientandroid.models.EnuProgress;
import com.club.coolkids.clientandroid.models.ImageUploadElement;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreatePost extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    public static final int MAX_IMAGES_PER_POST = 10;
    public static final int MAX_FILE_SIZE = 5000000; //5Mb in bytes

    public int succesfulImageUploads;
    public ArrayList<CreatePictureDTO> convertedImages;
    public List<ImageUploadElement> images;
    public CreatePostGridAdapter customAdapter;
    public EditText textDesc;
    public Button btnGallery;
    public Button btnSendPost;
    public int currentActivityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);
        images = new ArrayList<>();
        setTitle(R.string.createPost);
        this.convertedImages = new ArrayList<>();
        currentActivityId = this.getIntent().getIntExtra("ActivityId", 0);

        textDesc = findViewById(R.id.txtPostDesc);
        btnGallery = findViewById(R.id.btnGallery);
        btnSendPost = findViewById(R.id.btnSend);


        textDesc.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if(s.toString().trim().length() > 0) {
                   enableButton(btnSendPost);
               }
               else{
                   if(customAdapter.dataSet.size() <= 0)
                       disableButton(btnSendPost);
               }
            }
        });

        disableButton(btnSendPost);


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager staggeredGridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);


        customAdapter = new CreatePostGridAdapter(getApplicationContext(), images);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotToGallery();
            }
        });
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer();
            }
        });
    }

    private void gotToGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void sendPicture(CreatePictureDTO cDTO, final ImageUploadElement elt){

        Log.i("understandingUIbug", "Loading an image spinner   sendPicture " + elt.uri);
        //elt.progressState = EnuProgress.ongoing;

        DataService.getInstance().service.createPicture("Bearer " + Token.token.getToken(), cDTO)
            .enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if(response.isSuccessful()) {
                        Log.i("understandingUIbug", "Setting an Image to complete");
                        elt.progressState = EnuProgress.complete;
                        customAdapter.notifyDataSetChanged();
                        uploadFinishedUpdater(false);
                        Log.i("testAsync", "Call Reussi"+response.code());
                    }

                    else{
                        elt.progressState = EnuProgress.complete;
                        customAdapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(),R.string.postUploaderImageLostError, Toast.LENGTH_SHORT).show();
                        Log.i("testAsync", "CallFailed"+response.code());
                        Intent i = new Intent(getApplicationContext(),CreatePost.class);
                        startActivity(i);
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),R.string.postUploaderConnectionLostError, Toast.LENGTH_LONG).show();
                    Log.i("failure", t.getMessage());
                    t.printStackTrace();
                }
            });
    }

    private void sendToServer() {

        setupLoading();

        //délimitations du progres pour la progess bar pendant le loading
        CreatePostDTO cDTO = new CreatePostDTO(
                this.images.size(),
                textDesc.getText().toString().trim(),
                currentActivityId);

        //appel au serveur pour créer un post
        DataService.getInstance().service.createPost("Bearer " + Token.token.getToken(), cDTO)
                .enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if(response.isSuccessful()) {
                    //commencement de l'upload des photos individuelles

                    uploadFinishedUpdater(true);
                    Integer postId = response.body();
                    Toast.makeText(getApplicationContext(),R.string.postUploaderAddPictures, Toast.LENGTH_SHORT).show();
                    Log.i("understandingUIbug", "Post created");
                    for (ImageUploadElement element: images) {
                        element.progressState = EnuProgress.ongoing;
                    }
                    customAdapter.notifyDataSetChanged();
                    for (ImageUploadElement element: images) {
                        E e = new E();
                        e.postId = postId;
                        e.element = element;
                        new B64AsyncTask().execute(e);
                    }
                }

                else{
                    Toast.makeText(getApplicationContext(),R.string.postUploaderMessageError, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.postUploaderMessageError, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class E {
        ImageUploadElement element;
        Integer postId;
    }

    public class B64AsyncTask extends AsyncTask<E, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(E... elts) {
            try {
                E theOne = elts[0];
                long a = System.currentTimeMillis();
                CreatePictureDTO dto = new CreatePictureDTO(theOne.postId, convertImageTo64(theOne.element));
                long b = System.currentTimeMillis();
                Log.i("understandingUIbug", "Create DTO time " + (b - a) + "  " + theOne.element.uri);
                sendPicture(dto, theOne.element);
            } catch (Throwable t) {
                return null;
            }
            return null;
        }
    }

    //convertis l'URI en tableau d'octets, le buffer sert à plus facilement se promenet dans
    //l'image. Le tableau d'octets inputData se fait ensuite convertir en base64 qui est retournee
    private String convertImageTo64(ImageUploadElement element) throws IOException {
        Log.i("understandingUIbug", "start b64 " + element.uri);
        InputStream inputStream = getContentResolver().openInputStream(element.uri);

        byte[] inputData;
        //se mets devant le string en abse 64 de la photo comme header pour l'API
        String baseHeader = "data:image/jpeg;base64,";

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        inputData = byteBuffer.toByteArray();
        Log.i("UPLOAD Glide", "B64 " + inputData.length);

        //l'api ne reconnait pas le format standard
        Log.i("understandingUIbug", "end  b64 " + element.uri);
        byteBuffer.close();
        return baseHeader+Base64.encodeToString(inputData,Base64.NO_WRAP);
    }

    //verifies si une image, en bytes, est plus grosse que 10Mb
    private boolean imageIsSmallerThanMaxSize(Uri uri) {

        File file = new File(convertURItoFilepath(uri));
        long fileSizeInBytes = file.length();

        return fileSizeInBytes <= MAX_FILE_SIZE;
    }

    //remodifies le chemin pour calculer la bonne grosseur
    private String convertURItoFilepath(Uri uri){

        Log.i("understandingUIbug", "Converting an image");
        String path = uri.getPath();
        //verifies si le chemin contient ':'. si oui, prendre le chemin après le split
        //tres probablement est dans la VM
        if(path.contains(":")) {
            path = path.split(":")[1];
        }
        return path;
    }

    //se produit lorsque le selecteur de photos android se refereme
    //c'est a cet endroit que les photos sont vérifiées
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {

            boolean anImageIsTooBig = false;
            //Obtient le data si une seule image a ete selectionne
            if(data.getData()!=null){
                Uri uri = data.getData();

                try {
                    addValidImageToUpload(uri);
                }
                catch (TooManyImagesToUploadException e) {
                    //on arrête de parcourir car la limite est atteinte
                    Toast.makeText(this.getApplicationContext(), R.string.tooManyImages, Toast.LENGTH_LONG).show();
                }
                catch (ImageTooBigException e) {
                    //l'image ne sera pas ajoutée mais on continue à parcourir
                    anImageIsTooBig = true;
                }
            }

            //Obtient le data si plusieures dataSet ont ete sélectionnees
            else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();

                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();

                        try {
                            addValidImageToUpload(uri);
                        }
                        catch (TooManyImagesToUploadException e) {
                            //on arrête de parcourir car la limite est atteinte
                            Toast.makeText(this.getApplicationContext(), R.string.tooManyImages, Toast.LENGTH_LONG).show();
                            break;
                        }
                        catch (ImageTooBigException e) {
                            //l'image ne sera pas ajoutée mais on continue à parcourir
                            anImageIsTooBig = true;
                        }
                    }
                }
            }

            if(anImageIsTooBig)
                Toast.makeText(this.getApplicationContext(), R.string.imagesTooBig, Toast.LENGTH_LONG).show();

            customAdapter.notifyDataSetChanged();
        }

        else {
            Toast.makeText(this, R.string.noImagesChosen, Toast.LENGTH_LONG).show();
        }
    }

    //tentes de créer une image valide
    private void addValidImageToUpload(Uri uri) throws TooManyImagesToUploadException, ImageTooBigException {

        //verifies si l'image est trop grosse
        if(!imageIsSmallerThanMaxSize(uri))
            throw new ImageTooBigException();

        //verifies si il y a assez de place pour une autre image
        //le -1 sert a savoir si il va s'agir de la dernière image à ajouter,
        //permettant de disable le bouton directement après l'envoi
        //si c'est le cas on ne veut pas lancer d'exception puisque la liste n'ajoutera pas l'image
        //mais on veut quand même empêcher l'usage du bouton
        if(customAdapter.dataSet.size() >= MAX_IMAGES_PER_POST -1) {
            disableButton(btnGallery);
        }

        //cette fois-ci, on veut envoyer une exception car la limite a été dépassée
        //et on veut prévenir l'utilisateur qu'il n'aura pas toutes ses photos dans la liste
        if(customAdapter.dataSet.size() >= MAX_IMAGES_PER_POST) {
            throw new TooManyImagesToUploadException();
        }

        //vérifies si des dataSet avaient deja ete ajoutees.
        //si non, debloque le bouton pour permettre d'envoyer un post
        if(!btnSendPost.isEnabled()){
            enableButton(btnSendPost);
        }

        customAdapter.dataSet.add(new ImageUploadElement(uri, this));
    }

    //update le compteur d'images bien envoyées. Si le compteur atteint le size
    //de la liste d'images à envoyer, ça veut dire que chaque image s'est envoyée avec succès
    private void uploadFinishedUpdater (boolean firstCall){
        if(images.size()> 0 ) {
            //vérifies si il y a des images
            if(!firstCall) {
                succesfulImageUploads++;
                if (succesfulImageUploads == images.size()) {
                    Toast.makeText(getApplicationContext(),
                            R.string.postUploaderMessageSuccesful,
                            Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(), DisplayPosts.class);
                    i.putExtra("ActivityId", currentActivityId);
                    startActivity(i);
                    finish();
                }
            }
        }else if (textDesc.getText().length() > 0){
            //il n'y a pas d'images mais il y a du texte
            Intent i = new Intent(getApplicationContext(), DisplayPosts.class);
            i.putExtra("ActivityId", currentActivityId);
            startActivity(i);
            finish();
        }
    }

    ///
    ///UI RELATED METHODS
    ///

    //raccourci pour enable les boutons selon un style commun
    private void enableButton(Button btn){
        btn.setEnabled(true);
        btn.setTextColor(getResources().getColor(R.color.colorTextDisabled));
        btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    //raccourci pour disable les boutons selon un style commun
    private void disableButton(Button btn){
        btn.setEnabled(false);
        btn.setTextColor(getResources().getColor(R.color.colorTextDisabled));
        btn.setBackgroundColor(getResources().getColor(R.color.lightGray));
    }

    //commence l'affichage des photos en gris
    //et disable les boutons pour ne pas qu'on spam l'envoi au serveur
    private void setupLoading() {
        succesfulImageUploads = 0;

        Log.i("understandingUIbug", "Setting up a loading screen");

        Toast.makeText(getApplicationContext(),
                R.string.postUploaderMessageStart,
                Toast.LENGTH_SHORT).show();

        disableButton(btnSendPost);
        disableButton(btnGallery);
        textDesc.setEnabled(false);

        for (ImageUploadElement elt : customAdapter.dataSet) {
            elt.progressState = EnuProgress.waiting;
        }
        customAdapter.notifyDataSetChanged();
    }

}
