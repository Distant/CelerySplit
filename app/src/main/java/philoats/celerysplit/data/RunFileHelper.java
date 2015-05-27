package philoats.celerysplit.data;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import philoats.celerysplit.models.SplitSet;

public class RunFileHelper {

    Context context;

    public RunFileHelper(Context context)
    {
        this.context = context;
    }

    public void saveToFile(SplitSet set)
    {
        // TODO check for existing file
        try
        {
            File myFile = new File(Environment.getExternalStorageDirectory() + "/celerysplit", set.getTitle() + ".txt");
            //myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(set.getTitle() + "," + set.getCount() + "\r\n");
            for (int i = 0; i < set.getCount(); i++)
            {
                myOutWriter.write(set.names[i] + "," + set.pbTimes[i] + "," + set.bestSegments[i] + "\r\n");
            }
            myOutWriter.close();
            fOut.close();
            Toast.makeText(context,
                    "Done writing to SD " + set.getTitle() + ".txt'",
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public SplitSet readFromFile(String path)
    {
        String[] names;
        long[] pb;
        long[] best;
        String title;
        String[] curLine;
        File file = new File(Environment.getExternalStorageDirectory(), path);

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            curLine = br.readLine().split(",");
            title = curLine[0];
            int count = Integer.parseInt(curLine[1]);
            names = new String[count];
            pb = new long[count];
            best = new long[count];
            for (int i = 0; i < count; i++)
            {
                curLine = br.readLine().split(",");
                names[i] = curLine[0];
                pb[i] = Long.parseLong(curLine[1]);
                best[i] = Long.parseLong(curLine[2]);
            }
            br.close();
            Toast.makeText(context,
                    "Finished loading " + title + "!",
                    Toast.LENGTH_SHORT).show();

            return new SplitSet(-1, title, names, pb, best);
        }
        catch (Exception e)
        {
           System.out.println("oh shit " + e.getMessage());
        }

        return null;
    }
}