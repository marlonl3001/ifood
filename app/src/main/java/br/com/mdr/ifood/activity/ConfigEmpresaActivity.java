package br.com.mdr.ifood.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import br.com.mdr.ifood.R;
import br.com.mdr.ifood.helper.ConfiguracaoFirebase;
import br.com.mdr.ifood.helper.UsuarioFirebase;
import br.com.mdr.ifood.model.Empresa;

public class ConfigEmpresaActivity extends AppCompatActivity {
    private ImageView imgEmpresa;
    private EditText edtNome, edtTipoComida, edtTempoEntrega, edtTaxaEntrega;
    private ProgressBar progress;
    private Uri selectedImage;
    private Bitmap bitmapImage;
    private String idUsuario;
    private DatabaseReference firebaseRef;
    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_empresa);

        //Deine a toolbar customizada como ActionBar do app
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        //Mostra o botão voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        instanciaComponentes();

    }

    private void instanciaComponentes() {
        imgEmpresa = findViewById(R.id.imgEmpresa);
        edtNome = findViewById(R.id.edtNomeEmpresa);
        edtTipoComida = findViewById(R.id.edtTipoComida);
        edtTempoEntrega = findViewById(R.id.edtTempoEntrega);
        edtTaxaEntrega = findViewById(R.id.edtTaxaEntrega);
        progress = findViewById(R.id.progress);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();
        recuperaDados();
    }

    private void recuperaDados() {
        DatabaseReference empRef = firebaseRef.child("empresas").child(idUsuario);
        empRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    empresa = dataSnapshot.getValue(Empresa.class);
                    edtNome.setText(empresa.getNome());
                    edtTipoComida.setText(empresa.getCategoria());
                    edtTaxaEntrega.setText(empresa.getTaxaEntrega().toString());
                    edtTempoEntrega.setText(empresa.getTempo());
                    Picasso.get().load(empresa.getUrlImagem()).into(imgEmpresa);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgEmpresa: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                else
                    abreAlbum();
                break;
            }
            case R.id.btnSalvar: {
                progress.setVisibility(View.VISIBLE);
                if (validaDados())
                    salvaConfiguracoes();
                else
                    progress.setVisibility(View.GONE);
                break;
            }
        }
    }

    private boolean validaDados() {
        String nome = edtNome.getText().toString();
        String categoria = edtTipoComida.getText().toString();
        String tempo = edtTempoEntrega.getText().toString();
        String taxa = edtTaxaEntrega.getText().toString();

        return !nome.isEmpty() && !categoria.isEmpty() && !tempo.isEmpty() && !taxa.isEmpty();
    }

    private void salvaConfiguracoes() {
        try {
            String imageName = idUsuario + "jpeg";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            byte[] dadosImagem = outputStream.toByteArray();

            final StorageReference reference =
                    ConfiguracaoFirebase.getFirebaseStorage()
                            .child("imagens")
                            .child("empresas")
                            .child(imageName);
            UploadTask uploadTask = reference.putBytes(dadosImagem);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        Empresa empresa = new Empresa();
                        empresa.setUrlImagem(downloadUrl.toString());
                        empresa.setNome(edtNome.getText().toString());
                        empresa.setCategoria(edtTipoComida.getText().toString());
                        empresa.setIdUsuario(idUsuario);
                        empresa.setTaxaEntrega(Double.parseDouble(edtTaxaEntrega.getText().toString()));
                        empresa.setTempo(edtTempoEntrega.getText().toString());
                        empresa.salvar();
                        progress.setVisibility(View.GONE);
                        mostraMensagem("Configurações salvas!");
                        finish();
                    } else {
                        mostraMensagem("Erro ao salvar configurações!");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mostraMensagem("Erro ao salvar configurações!");
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
            mostraMensagem("Erro ao salvar configurações!");
        }
    }

    private void mostraMensagem(String texto) {
        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
    }

    private void abreAlbum() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                abreAlbum();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.getData();

            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imgEmpresa.setImageBitmap(bitmapImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
