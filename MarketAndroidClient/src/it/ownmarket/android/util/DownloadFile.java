package it.ownmarket.android.util;

import it.ownmarket.android.dto.Applicazione;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

public class DownloadFile extends AsyncTask<String, Integer, String> {
	private ProgressDialog progressD;
	private Applicazione app;
	private OnDownloadFinished event;

	public DownloadFile(ProgressDialog p,Applicazione app,OnDownloadFinished event) {
		this.progressD=p;
		this.app=app;
		this.event=event;
	}
	
    @Override
    protected String doInBackground(String... sUrl) {
    	String urlSavedApk=null;
        try {
            URL url = new URL(sUrl[0]);
            URLConnection connection = url.openConnection();
            urlSavedApk=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+app.getApkname();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(urlSavedApk);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return urlSavedApk;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressD.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        progressD.setProgress(progress[0]);
    }
    @Override
    protected void onPostExecute(String result) {    	
    	super.onPostExecute(result);
    	progressD.dismiss();
    	event.downloaded(result);
    }
    
    public interface OnDownloadFinished{
    	public void downloaded(String url);
    }
    
}