package com.caseybrooks.scripturememory.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.defaults.DefaultMetaData;
import com.caseybrooks.androidbibletools.enumeration.Version;
import com.caseybrooks.scripturememory.R;
import com.caseybrooks.scripturememory.data.VerseDB;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ImportVersesFragment extends Fragment {
    Context context;
    ListView lv;
    FileAdapter adapter;

    public static Fragment newInstance() {
        Fragment fragment = new ImportVersesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the main layout for this fragment
        lv = new ListView(getActivity());

        return lv;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar ab = ((ActionBarActivity) context).getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(context.getResources().getColor(R.color.memorized));
        ab.setBackgroundDrawable(colorDrawable);
        ab.setTitle("Import");
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        new PopulateList().execute();
    }

    private class PopulateList extends AsyncTask<File, Void, Void> {
        AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            View view = LayoutInflater.from(context).inflate(R.layout.popup_progress, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);

            dialog = builder.create();

            view.findViewById(R.id.cancel_button).setVisibility(View.GONE);

            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dialog.dismiss();
            lv.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(File... params) {

            String path = Environment.getExternalStorageDirectory().toString() + "/scripturememory";
            File f = new File(path);
            File files[] = f.listFiles();
            ArrayList<File> goodFiles = new ArrayList<File>();

            for (File file : files) {
                try {
                    Document doc = Jsoup.parse(file, null);

                    if (doc.select("verses").size() > 0) {

                        goodFiles.add(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            adapter = new FileAdapter(goodFiles);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewHolder vh = (ViewHolder) view.getTag();

                    if (vh != null) {
                        new SaveVerses().showPopup(vh.file);
                    }
                }
            });
            return null;
        }
    }

    private class FindFiles extends AsyncTask<Void, Void, Void> {

        View view;
        AlertDialog dialog;

        public void showPopup() {
            view = LayoutInflater.from(context).inflate(R.layout.popup_find_files, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);

            dialog = builder.create();

            view.findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    execute();
                }
            });
            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            view.findViewById(R.id.description).setVisibility(View.GONE);
            view.findViewById(R.id.continue_button).setVisibility(View.GONE);
            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel(true);
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dialog.dismiss();
            new PopulateList().execute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //pathA = Scripture Now's external directory
            //pathB = RememberMe's external directory

            String pathA = Environment.getExternalStorageDirectory().getPath() + "/scripturememory";
            String pathB = Environment.getExternalStorageDirectory().getPath() + "/RememberMe";

            File folderA = new File(pathA);
            File folderB = new File(pathB);

            if(folderB.exists() && folderB.isDirectory() && folderB.listFiles().length > 0) {
                //the RememberMe folder exists and has files in it, which must be to imported

                for(File file : folderB.listFiles()) {
                    if(isCancelled()) break;

                    ArrayList<Passage> verses = new ArrayList<Passage>();
                    if(file.getName().equals("books_of_bible.txt")) continue;
                    if(!file.getName().contains(".txt")) continue;

                    Log.e("PARSING FILE:", file.getName());
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(file));

                        String line;
                        Passage passage = null;
                        String text = "";

                        while (true) {
                            if(isCancelled()) break;

                            line = in.readLine();
                            if (line == null) break;
                            if (line.length() == 0) continue;

                            String lineType = line.substring(0, 2).toLowerCase().trim();

                            if (lineType.equals("r:")) {
                                if (passage == null) {
                                    passage = new Passage(line.substring(2).trim());
                                }
                                else {
                                    if(text != null && text.length() > 0) {
                                        passage.setText(text);
                                        verses.add(passage);
                                    }
                                    passage = new Passage(line.substring(2).trim());
                                }
                            } else if (lineType.equals("q:") && passage != null) {
                                passage.setVersion(Version.parseVersion(line.substring(2).trim()));
                            } else if (lineType.equals("t:") && passage != null) {
                                passage.setTags(line.substring(2).trim().split(","));
                            } else if (lineType.equals("p:")) {
                                text = line.substring(2).trim();
                            } else {
                                text += " " + line.trim();
                            }
                        }
                        if(isCancelled()) break;

                        //print verses to an XML file
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();

                        org.w3c.dom.Document doc = builder.newDocument();
                        org.w3c.dom.Element root = doc.createElement("verses");
                        root.setAttribute("name", file.getName().replaceAll("_", " ").replaceAll(".txt", ""));
                        doc.appendChild(root);

                        for(Passage item : verses) {
                            if(isCancelled()) break;
                            org.w3c.dom.Element passageElement = doc.createElement("passage");
                            root.appendChild(passageElement);

                            org.w3c.dom.Element r = doc.createElement("R");
                            r.appendChild(doc.createTextNode(item.getReference().toString()));
                            passageElement.appendChild(r);

                            org.w3c.dom.Element q = doc.createElement("Q");
                            q.appendChild(doc.createTextNode(item.getVersion().getName()));
                            passageElement.appendChild(q);

                            //TODO: write all tags to file
                            org.w3c.dom.Element t = doc.createElement("T");
                            passageElement.appendChild(t);
                            for(String string : item.getTags()) {
                                org.w3c.dom.Element tagItem = doc.createElement("item");
                                tagItem.appendChild(doc.createTextNode(string));
                                t.appendChild(tagItem);
                            }

                            org.w3c.dom.Element p = doc.createElement("P");
                            p.appendChild(doc.createTextNode(item.getText()));
                            passageElement.appendChild(p);
                        }
                        if(isCancelled()) break;

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(new File(folderA, file.getName().replaceAll(".txt", ".xml")));
                        transformer.transform(source, result);
                    }
                    catch (ParseException pe) {
                        pe.printStackTrace();
                    }
                    catch(FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    }
                    catch(IOException ioe) {
                        ioe.printStackTrace();
                    }
                    catch (ParserConfigurationException pce) {
                        pce.printStackTrace();
                    }
                    catch (TransformerConfigurationException tce) {
                        tce.printStackTrace();
                    }
                    catch (TransformerException te) {
                        te.printStackTrace();
                    }
                }
            }

            return null;
        }
    }

    private class SaveVerses extends AsyncTask<Void, Void, Void> {

        View view;
        AlertDialog dialog;
        ArrayList<Passage> verses;
        File file;

        private boolean running = true;

        public void showPopup(final File file) {
            this.file = file;

            verses = new ArrayList<Passage>();

            view = LayoutInflater.from(context).inflate(R.layout.popup_import_verses, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);

            view.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/xml");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.getAbsolutePath()));
                    startActivity(Intent.createChooser(intent, "Send file to..."));
                }
            });

            view.findViewById(R.id.import_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    execute();
                }
            });

            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog = builder.create();
            dialog.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            view.findViewById(R.id.description).setVisibility(View.GONE);
            view.findViewById(R.id.import_button).setVisibility(View.GONE);
            view.findViewById(R.id.share_button).setVisibility(View.GONE);
            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel(false);
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(!isCancelled()) {

                VerseDB db = new VerseDB(context).open();
                int beforeCount = db.getStateCount(VerseDB.ALL_VERSES);
                for (Passage passage : verses) {
                    db.insertVerse(passage);
                }
                int afterCount = db.getStateCount(VerseDB.ALL_VERSES);
                db.close();
                Toast.makeText(context, (afterCount - beforeCount) + " new verses added", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Adding verses cancelled", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.parse(file, null);

                if(doc.select("verses").size() > 0) {
                    for(Element element : doc.select("passage")) {
                        if(!running) return null;

                        Passage passage = new Passage(element.select("R").text());
                        passage.setVersion(Version.parseVersion(element.select("Q").text()));

                        for(org.jsoup.nodes.Element tagElement : element.select("T").select("item")) {
                            passage.addTag(tagElement.text());
                        }

                        passage.setText(element.select("P").text());
                        passage.getMetadata().putInt(DefaultMetaData.STATE, VerseDB.CURRENT_NONE);

                        verses.add(passage);
                    }
                }
                return null;
            }
            catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "There was an error importing this verse pack", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
    }

    private class ViewHolder {
        TextView title;
        TextView count;
        TextView source;

        File file;
        Context context;

        ViewHolder(Context context, View inflater) {
            this.context = context;

            title = (TextView) inflater.findViewById(R.id.fileName);
            count = (TextView) inflater.findViewById(R.id.fileVerseCount);
            source = (TextView) inflater.findViewById(R.id.fileSource);
        }

        private void initialize(File file) {
            this.file = file;

            try {
                Document doc = Jsoup.parse(file, null);

                if(doc.select("verses").size() > 0) {

                    if(doc.select("verses").hasAttr("name")) {
                        title.setText(doc.select("verses").attr("name"));
                    }
                    else {
                        title.setText(file.getName());
                    }

                    count.setText(doc.select("passage").size() + " verses");

                    ArrayList<String> includedFileNames = new ArrayList<String>();
                    Field[] fields = R.raw.class.getFields();
                    for(Field f : fields) {
                        includedFileNames.add(f.getName() + ".xml");
                    }

                    if(includedFileNames.contains(file.getName().toLowerCase())) {
                        source.setText("Included with app");
                    }
                    else {
                        source.setText("From external source");
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class FileAdapter extends BaseAdapter {
        ArrayList<View> views;


        public FileAdapter(ArrayList<File> files) {
            this.views = new ArrayList<View>();

            for(File file : files) {
                ViewHolder vh;
                View view = LayoutInflater.from(context).inflate(R.layout.list_import_verses, null, false);
                vh = new ViewHolder(context, view);
                view.setTag(vh);

                vh.initialize(file);
                view.setTag(vh);
                views.add(view);
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public File getItem(int position) {
            return ((ViewHolder)views.get(position).getTag()).file;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return views.get(position);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = ((ActionBarActivity) context).getMenuInflater();
        inflater.inflate(R.menu.menu_import, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_import_show_path:
                String path = Environment.getExternalStorageDirectory().getPath() + "/scripturememory";
                Toast.makeText(context, "Files located at: " + path, Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_import_find_files:
                new FindFiles().showPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
