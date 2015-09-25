package philoats.celerysplit.data;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import philoats.celerysplit.models.SplitSet;
import rx.Observable;

public class RunFileHelper {

    public static final String IMPORT_PATH = Environment.getExternalStorageDirectory().toString() + "/CelerySplit/Import";
    private Context context;

    public RunFileHelper(Context context)
    {
        this.context = context;
    }

    public void exportFile(SplitSet set)
    {
        try
        {
            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/CelerySplit/Export");
            if (!folder.exists()) {
                System.out.println(folder.mkdirs() || folder.isDirectory());
            }

            File file = new File(folder.getAbsolutePath(), set.getTitle());
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter outWriter = new OutputStreamWriter(fOut);

            outWriter.write("Title=" + set.getTitle() + "\r\n");
            outWriter.write("Attempts=" + "0" + "\r\n");
            outWriter.write("Offset=" + "0" + "\r\n");
            outWriter.write("Size=120,25" + "\r\n");
            for (int i = 0; i < set.getCount(); i++)
            {
                outWriter.write(set.getName(i) + "," + "0," + ((float) set.getPbTime(i)) / 1000 + "," + ((float) set.getBestTime(i)) / 1000 + "\r\n");
            }
            outWriter.close();
            fOut.close();

            Toast.makeText(context,
                    file.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public Observable<SplitSet> importFile(String path)
    {
        String[] names;
        long[] pb;
        long[] best;
        String title;
        String[] curLine;
        File file = new File(RunFileHelper.IMPORT_PATH + "/" + path);

        ArrayList<String> splits = new ArrayList<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            curLine = br.readLine().split("=");
            title = curLine[1];
            br.readLine(); br.readLine(); br.readLine();

            while(true)
            {
                String next = br.readLine();
                if (next.startsWith("Icons=")) break;
                else splits.add(next);
            }

            int count = splits.size();
            names = new String[count];
            pb = new long[count];
            best = new long[count];

            for (int i = 0; i < count; i++)
            {
                curLine = splits.get(i).split(",");
                names[i] = curLine[0];
                pb[i] = (long) (Float.parseFloat(curLine[2]) * 1000);
                best[i] = (long) (Float.parseFloat(curLine[3])* 1000);
            }

            br.close();
            Toast.makeText(context,
                    "Finished loading " + title + "!",
                    Toast.LENGTH_SHORT).show();

            return Observable.just(new SplitSet(-1, title, names, pb, best));
        }
        catch (Exception e)
        {
           System.out.println("Error: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<String> getImportFiles() {
        File dir = new File(IMPORT_PATH);
        File[] fileList = dir.listFiles();
        ArrayList<String> files = new ArrayList<>();
        for (File f : fileList){
            files.add(f.getName());
        }
        return files;
    }
}