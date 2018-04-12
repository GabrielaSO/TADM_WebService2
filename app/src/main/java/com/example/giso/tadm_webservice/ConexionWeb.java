package com.example.giso.tadm_webservice;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ConexionWeb extends AsyncTask<URL,String,String> {

    List<String[]> variables;
    AsyncResponse delegado;

    public ConexionWeb(AsyncResponse p){
        delegado = p;
        variables = new ArrayList<>();
    }

    public void agregarVariables(String nombreVariable, String contenidoVariable){
        String[] temporal ={nombreVariable,contenidoVariable};
        variables.add(temporal);
    }
    private String generarCadenaPost(){
        String post="";
        try {
            for (int i = 0; i < variables.size(); i++) {
                String[] temporal = variables.get(i);
                post += temporal[0] + "=" + URLEncoder.encode(temporal[1], "UTF-8") + " ";
            }
        }catch (Exception e){
            Log.i("Error generarCadenaPost", e.getMessage());
        }
        post=post.trim();
        post = post.replaceAll(" ","&");
        return post;
    }
    @Override
    protected String doInBackground(URL...params)
    {
        String POST = "?"+generarCadenaPost();
        URL url = null; //Url de donde queremos obtener la informacion
        String devuelve = "";
        //Consultar a todos los alumnos

        try {
            url = new URL(params[0].toString()+POST);
            HttpURLConnection connexion = (HttpURLConnection)url.openConnection();
            connexion.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            int respuesta = connexion.getResponseCode();
            StringBuilder result = new StringBuilder();
            if (respuesta == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(connexion.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            devuelve = result.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("Error doInBackground", e.getMessage());
        } catch (IOException e) {
            Log.i("Error doInBackground", e.getMessage());
        }
        return devuelve;
    }
    public void onPostExecute(String respuesta){

        delegado.procesarRespuesta(respuesta);
    }
}