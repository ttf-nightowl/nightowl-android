package its.progetto.nightowl;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class DataGetter
{
    private static String target;

    DataGetter(String target)
    {
        this.target= target;
    }

    void getData(ResultCallback callback)
    {
        new AsyncGetter( callback ).execute();
    }

    static class AsyncGetter extends AsyncTask<Void,Void,Integer>
    {
        String result ="";
        ResultCallback callback;

        AsyncGetter(ResultCallback callback)
        {
            this.callback=  callback;
        }

        @Override
        protected Integer doInBackground(Void... voids)
        {
            HttpURLConnection connection=null;
            try
            {
                URL urlTarget= new URL(target);
                connection= (HttpURLConnection) urlTarget.openConnection();

                // Not required
                connection.setRequestMethod("GET");

                if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
                {
                    connection.disconnect();
                    return 2;
                }
                InputStream in = connection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                if(scanner.hasNext())
                {
                    result = scanner.next();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return 1;
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
            return 0;
        }
        @Override
        protected void onPostExecute(Integer integer)
        {
            super.onPostExecute(integer);
            if(integer==0)
            {
                callback.onSuccess( result );
            }
            else {
                callback.onError( integer );
            }
        }
    }
    public interface ResultCallback
    {
        void onError(int errorCode);
        void onSuccess(String result);
    }
}
