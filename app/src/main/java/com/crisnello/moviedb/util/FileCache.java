package com.crisnello.moviedb.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;


public class FileCache {
    
    private File cacheDir;
    private String filename="";
    private Context context;
   
    
    public FileCache(Context context){
        //Find the dir to save cached arquives
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"Cheklis");
//            cacheDir=context.getExternalCacheDir();
            Log.e("CacheDirIF", String.valueOf(cacheDir));

        } else {
            cacheDir=context.getCacheDir();
            Log.e("CacheDirELSE", String.valueOf(cacheDir));
        }

        if (!cacheDir.exists())
            cacheDir.mkdirs();

        this.context=context;
    }

    public File getFile(String url){
        //Create new File by url
        try
        {
            filename=url.substring( url.lastIndexOf('/')+1, url.length() );
            filename = URLEncoder.encode(filename, "UTF-8");


            File f = new File(cacheDir, filename);
            return f;
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        return null;

    }

    public File getFilebyName(String filename)
    {
        File f =new File(cacheDir,filename);
        return f;
    }

    public String getFileName()
    {
        return filename;
    }

    public String getPath(String filename)
    {
        return cacheDir+"/"+filename;
    }

    public Bitmap getBitmap(String filename)
    {
        String photoPath = cacheDir+"/"+filename;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        return bitmap;
    }

    public Bitmap getBitmapResized(String filename)
    {
        String photoPath = cacheDir+"/"+filename;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath,options);
        return bitmap;
    }

    public Bitmap getBitmapResizedDinamyc(String filename, int max_size){

        Bitmap b = null;
        String photoPath = cacheDir+"/"+filename;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        b = BitmapFactory.decodeFile(photoPath, o);

        int scale = 1;
        if (o.outHeight > max_size || o.outWidth > max_size) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(max_size /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        b = BitmapFactory.decodeFile(photoPath, o2);

        return b;
    }

    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

    public void delete(String filename)
    {
        File file = getFilebyName(filename);
        if(file!=null)
            file.delete();
    }


    public String saveFile(Uri sourceuri)
    {
        FileCache fileCache = new FileCache(context);
        String sourceFilename= sourceuri.getPath();
        fileCache.getFile(sourceuri.getPath());
        String destinationFilename = cacheDir+"/"+fileCache.getFileName();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {

        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {

            }
        }

        return fileCache.getFileName();
    }

//    public String downloadFileFromAWS(String path)
//    {
//        if(path.equals("") || path.length()<= Config.AMAZON_S3_PATH.length() )
//            return "";
//
//        //Get keyname only from path
//        String keyName="";
//        keyName=path.substring(Config.AMAZON_S3_PATH.length());
//
//        FileCache cache = new FileCache(context);
//
//        //Download from AWS
//        AmazonS3 s3Client = null;
//        try
//        {
//            AWSCredentials myCredentials = new BasicAWSCredentials(Config.AMAZON_S3_ACCESS_KEY,Config.AMAZON_S3_SECRET_KEY);
//
//            s3Client = new AmazonS3Client(myCredentials);
//
//            GetObjectRequest request = new GetObjectRequest(Config.AMAZON_S3_BUCKET,
//                    keyName);
//            S3Object object = s3Client.getObject(request);
//            S3ObjectInputStream input = object.getObjectContent();
//
//            File storagePath =cache.getFile(keyName);
//
//            OutputStream output = new FileOutputStream(storagePath);
//            try
//            {
//                byte[] buffer = new byte[1024];
//                int bytesRead = 0;
//                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
//                {
//                    output.write(buffer, 0, bytesRead);
//                }
//
//                //input.reset();
//            }
//            catch(Exception ex)
//            {
//                ex.printStackTrace();
//                return "";
//            }
//            finally {
//                output.close();
//            }
//
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//            return "";
//        }
//
//        return cache.getFileName();
//    }
//
//    public String downloadFileFromAWSOutput(String path)
//    {
//        if(path.equals("") || path.length()<=Config.AMAZON_S3_PATH_OUTPUT.length() )
//            return "";
//
//        //Get keyname only from path
//        String keyName="";
//        keyName=path.substring(Config.AMAZON_S3_PATH_OUTPUT.length());
//
//        FileCache cache = new FileCache(context);
//
//        //Download from AWS
//        AmazonS3 s3Client = null;
//        try
//        {
//            AWSCredentials myCredentials = new BasicAWSCredentials(Config.AMAZON_S3_ACCESS_KEY,Config.AMAZON_S3_SECRET_KEY);
//
//            s3Client = new AmazonS3Client(myCredentials);
//
//            GetObjectRequest request = new GetObjectRequest(Config.AMAZON_S3_BUCKET_OUTPUT,
//                    keyName);
//            S3Object object = s3Client.getObject(request);
//            S3ObjectInputStream input = object.getObjectContent();
//
//            File storagePath =cache.getFile(keyName);
//
//            OutputStream output = new FileOutputStream(storagePath);
//            try
//            {
//                byte[] buffer = new byte[1024];
//                int bytesRead = 0;
//                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
//                {
//                    output.write(buffer, 0, bytesRead);
//                }
//
//                //input.reset();
//            }
//            catch(Exception ex)
//            {
//                ex.printStackTrace();
//                return "";
//            }
//            finally {
//                output.close();
//            }
//
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//            return "";
//        }
//
//        return cache.getFileName();
//    }

    public String downloadFile(String urlString)
    {

        if(urlString==null || urlString.equals(""))
            return "";

        FileCache cache = new FileCache(context);

        URL url;
        try {

            url = new URL(urlString);

            InputStream input = url.openStream();
            try {

                File storagePath =cache.getFile(urlString);

                OutputStream output = new FileOutputStream(storagePath);
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
                finally {
                    output.close();
                }
            }

            finally {
                input.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }finally{

        }

        return cache.getFileName();

    }

}